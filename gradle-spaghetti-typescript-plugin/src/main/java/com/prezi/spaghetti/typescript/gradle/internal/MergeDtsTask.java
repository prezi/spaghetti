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
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputDirectory;
import org.gradle.api.tasks.InputFile;
import org.gradle.api.tasks.OutputFile;
import org.gradle.api.tasks.SourceTask;
import org.gradle.api.tasks.TaskAction;
import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.io.Files;
import com.prezi.spaghetti.definition.DefinitionFile;
import com.prezi.spaghetti.gradle.internal.DefinitionAwareSpaghettiTask;
import com.prezi.typescript.gradle.TypeScriptCompileDts;

public class MergeDtsTask extends SourceTask implements DefinitionAwareSpaghettiTask {

    private DefinitionFile definition = null;
    private File workDir;
	private File sourceDir;

	@Input
	public File getWorkDir() {
		return workDir;
	}

	public void setWorkDir(File dir) {
		workDir = dir;
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
		return sourceDir;
	}

	public void setSourceDir(File dir) {
		sourceDir = dir;
	}

	@OutputFile
	public File getOutputFile() {
		return new File(getWorkDir(), getDefinitionDtsFilename());
    }

    private String getDefinitionDtsFilename() {
        String definitionFilename = FilenameUtils.removeExtension(getDefinition().getFile().getName());
        return definitionFilename + ".d.ts";
    }


    @TaskAction
    public void merge() throws IOException, InterruptedException {
        FileUtils.deleteQuietly(getWorkDir());
        FileUtils.forceMkdir(getWorkDir());

        String filename = getDefinitionDtsFilename();
        Collection<File> files = FileUtils.listFiles(getSourceDir(), new NameFileFilter(filename), TrueFileFilter.TRUE);
        if (files.isEmpty()) {
            throw new RuntimeException(filename + " is not found");
        }

        File generatedDts = Iterables.getOnlyElement(files);
        List<String> lines = Lists.newArrayList();

        lines.addAll(Files.asCharSource(generatedDts, Charsets.UTF_8).readLines());
        lines.add(String.format("export as namespace %s;", getDefinition().getNamespaceOverride()));
        String content = Joiner.on("\n").join(lines);

        Files.write(content, getOutputFile(), Charsets.UTF_8);
    }
}