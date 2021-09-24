package com.prezi.spaghetti.typescript.gradle.internal;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.NameFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.*;
import com.google.common.base.Charsets;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.io.Files;
import com.prezi.spaghetti.definition.DefinitionFile;
import com.prezi.spaghetti.gradle.NeedsTypeScriptCompilerSpaghettiTask;
import com.prezi.spaghetti.gradle.internal.DefinitionAwareSpaghettiTask;
import com.prezi.spaghetti.tsast.TypeScriptAstParserException;
import com.prezi.spaghetti.tsast.TypeScriptAstParserService;

public class MergeDtsTask extends SourceTask implements DefinitionAwareSpaghettiTask, NeedsTypeScriptCompilerSpaghettiTask {

	private DefinitionFile definition = null;
    private DirectoryProperty outputDir = getProject().getObjects().directoryProperty();
    private RegularFileProperty sourceDir = getProject().getObjects().fileProperty();
    private RegularFileProperty tsCompilerPath = getProject().getObjects().fileProperty();

    @OutputDirectory
    public File getOutputDir() {
        return outputDir.getAsFile().get();
    }

    public void setOutputDir(File dir) {
        outputDir.set(dir);
    }

    @InputFile
    public File getDefinitionFile() {
        return getDefinition().getFile();
    }

    @Input
    public DefinitionFile getDefinition() {
        return definition;
    }

    public void setDefinition(DefinitionFile def) {
        this.definition = def;
    }

    @InputDirectory
    public File getSourceDir() {
        return sourceDir.getAsFile().get();
    }

    public void setSourceDir(File dir) {
        sourceDir.set(dir);
    }

    @OutputFile
    public File getOutputFile() {
        return outputDir.map(dir -> new File(dir.getAsFile(), getDefinitionDtsFilename())).get();
    }

    private String getDefinitionDtsFilename() {
        String definitionFilename = FilenameUtils.removeExtension(getDefinition().getFile().getName());
        return definitionFilename + ".d.ts";
    }

    @InputDirectory
    public File getCompilerPath() {
        return tsCompilerPath.getAsFile().get();
    }

    public void setCompilerPath(File compilerDir) {
        this.tsCompilerPath.set(compilerDir);
    }


    @TaskAction
    public void merge() throws IOException, InterruptedException {
        File workDir = getTemporaryDir();

        String filename = getDefinitionDtsFilename();
        Collection<File> files = FileUtils.listFiles(getSourceDir(), new NameFileFilter(filename), TrueFileFilter.TRUE);
        if (files.isEmpty()) {
            throw new RuntimeException(filename + " is not found");
        }

        File generatedDts = Iterables.getOnlyElement(files);
        List<String> lines = Lists.newArrayList();

        File mergedDts = new File(workDir, "_merged.d.ts");
        try {
            TypeScriptAstParserService.mergeDefinitionFileImports(
                workDir,
                getCompilerPath(),
                generatedDts,
                mergedDts,
                getLogger()
            );
        } catch (TypeScriptAstParserException e) {
            for (String line: e.getOutput()) {
                System.out.println(line);
            }
            throw new RuntimeException("Merge of TypeScript module definition failed, see console for errors.", e);
        }

        lines.addAll(Files.asCharSource(mergedDts, Charsets.UTF_8).readLines());
        lines.add(String.format("export as namespace %s;", getDefinition().getNamespaceOverride()));
        String content = ReferenceDirectiveStripper.stripAndJoin(lines);
        Files.write(content, getOutputFile(), Charsets.UTF_8);
    }
}
