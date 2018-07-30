package com.prezi.spaghetti.closure;

import java.util.ArrayList;
import java.util.List;
import java.io.File;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;


class Args {
    public static Args parse(String[] args) {
        Args parsedArgs = new Args();
        CmdLineParser parser = new CmdLineParser(parsedArgs);

        try {
            parser.parseArgument(args);
        } catch (CmdLineException e) {
            System.err.println(e.getMessage());
            parser.printUsage(System.err);
            System.exit(1);
        }

        return parsedArgs;
    }

    @Option(name="--js_output_file")
    public File outputFile;

    @Option(name="--create_source_map")
    public File sourceMap = null;

    @Option(name="--entry_point")
    public List<String> entryPoints = new ArrayList<String>();

    @Option(name="--js")
    public List<String> inputPatterns = new ArrayList<String>();

    @Option(name="--externs")
    public List<String> externsPatterns = new ArrayList<String>();

    @Option(name="--compilation_level")
    public String compilationLevel = "SIMPLE";

    @Option(name="--concat")
    public boolean concat = false;

    @Option(name="--es5")
    public boolean es5 = false;
}
