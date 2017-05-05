package com.prezi.spaghetti.typescript.gradle.internal;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.NameFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.gradle.api.Task;
import org.gradle.api.specs.Spec;
import org.gradle.api.tasks.InputFile;
import org.gradle.api.tasks.TaskAction;
import com.google.common.base.Charsets;
import com.google.common.collect.Iterables;
import com.google.common.io.Files;
import com.prezi.spaghetti.gradle.internal.DefinitionAwareSpaghettiTask;
import com.prezi.typescript.gradle.TypeScriptCompileDts;

public class DefinitionAwareTypeScriptCompileDtsTask extends TypeScriptCompileDts implements DefinitionAwareSpaghettiTask {

    private File definition = null;

    public DefinitionAwareTypeScriptCompileDtsTask() {
        this.onlyIf(new Spec<Task>() {
            public boolean isSatisfiedBy(Task task) {
                String path = ((DefinitionAwareTypeScriptCompileDtsTask)task).getDefinition().getPath();
                return path.endsWith(".ts") && !path.endsWith(".d.ts");
            }
        });
    }

    @InputFile
    public File getDefinition() {
        return definition;
    }

    public void setDefinition(Object def) {
        this.definition = getProject().file(def);
    }

    @TaskAction
    @Override
    public void run() throws IOException, InterruptedException {
        File tempDir = getTemporaryDir();
        FileUtils.deleteQuietly(tempDir);
        FileUtils.forceMkdir(tempDir);

        List<String> command = compileCommand(tempDir, true);
        executeCommand(command);

        String definitionFilename = FilenameUtils.removeExtension(getDefinition().getName());
        Collection<File> files = FileUtils.listFiles(tempDir, new NameFileFilter(definitionFilename + ".d.ts"), TrueFileFilter.TRUE);
        if (files.isEmpty()) {
            throw new RuntimeException(definitionFilename + ".d.ts is not found");
        }

        File generatedDts = Iterables.getOnlyElement(files);
        File finalOutputFile = new File(this.getOutputDir(), generatedDts.getName());
        List<String> lines = Files.asCharSource(generatedDts, Charsets.UTF_8).readLines();
        String content = ReferenceDirectiveStripper.stripAndJoin(lines);

        Files.write(content, finalOutputFile, Charsets.UTF_8);
    }
}