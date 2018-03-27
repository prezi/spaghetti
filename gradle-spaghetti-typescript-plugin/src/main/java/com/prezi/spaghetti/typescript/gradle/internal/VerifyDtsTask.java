package com.prezi.spaghetti.typescript.gradle.internal;

import java.io.File;
import java.io.IOException;

import org.gradle.api.Task;
import org.gradle.api.internal.ConventionTask;
import org.gradle.api.specs.Spec;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputDirectory;
import org.gradle.api.tasks.InputFile;
import org.gradle.api.tasks.TaskAction;

import com.prezi.spaghetti.definition.DefinitionFile;
import com.prezi.spaghetti.gradle.NeedsTypeScriptCompilerSpaghettiTask;
import com.prezi.spaghetti.gradle.internal.DefinitionAwareSpaghettiTask;
import com.prezi.spaghetti.tsast.TypeScriptAstParserException;
import com.prezi.spaghetti.tsast.TypeScriptAstParserService;

public class VerifyDtsTask extends ConventionTask implements DefinitionAwareSpaghettiTask, NeedsTypeScriptCompilerSpaghettiTask {
    private DefinitionFile definition = null;
    private File tsCompilerPath = null;

    public VerifyDtsTask() {
        super();

        this.onlyIf(new Spec<Task>() {
            public boolean isSatisfiedBy(Task task) {
                DefinitionFile def = ((VerifyDtsTask)task).getDefinition();
                if (def == null) {
                    return false;
                } else {
                    String path = def.getFile().getPath();
                    return path.endsWith(".ts");
                }
            }
        });

        // Since this task has no outputs, we have to tell gradle that outputs
        // will always be up-to-date, otherwise the task will always run
        // regardless of whether the inputs changed.
        getOutputs().upToDateWhen(new Spec<Task>() {
            public boolean isSatisfiedBy(Task task) {
                return true;
            }
        });
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
	public File getCompilerPath() {
		return tsCompilerPath;
	}

    public void setCompilerPath(File compilerDir) {
        this.tsCompilerPath = compilerDir;
    }

    @TaskAction
    public void verify() throws IOException, InterruptedException {
        File workDir = getTemporaryDir();
        try {
            TypeScriptAstParserService.verifyModuleDefinition(
                workDir,
                getCompilerPath(),
                getDefinition().getFile(),
                getLogger());
        } catch (TypeScriptAstParserException e) {
            for (String line: e.getOutput()) {
                System.out.println(line);
            }
            throw new RuntimeException("Verify TypeScript module definition failed, see console for errors.", e);
        }
    }
}