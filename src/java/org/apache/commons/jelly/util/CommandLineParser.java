/*
 * Copyright 2002,2004 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.commons.jelly.util;

import java.io.File;
import java.io.FileWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Properties;
import java.util.StringTokenizer;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.Parser;
import org.apache.commons.cli.ParseException;

import org.apache.commons.jelly.Jelly;
import org.apache.commons.jelly.JellyContext;
import org.apache.commons.jelly.JellyException;
import org.apache.commons.jelly.Script;
import org.apache.commons.jelly.XMLOutput;

/**
 * Utility class to parse command line options using CLI.
 * Using a separate class allows us to run Jelly without
 * CLI in the classpath when the command line interface
 * is not in use.
 *
 * @author <a href="mailto:jstrachan@apache.org">James Strachan</a>
 * @author Morgan Delagrange
 * @version $Revision: 1.6 $
 */
public class CommandLineParser {

    protected static CommandLineParser _instance = new CommandLineParser();

    public static CommandLineParser getInstance() {
        return _instance;
    }

    /**
     * Parse out the command line options and configure
     * the give Jelly instance.
     *
     * @param args   options from the command line
     * @exception JellyException
     *                   if the command line could not be parsed
     */
    public void invokeCommandLineJelly(String[] args) throws JellyException {
        CommandLine cmdLine = null;
        try {
            cmdLine = parseCommandLineOptions(args);
        } catch (ParseException e) {
            throw new JellyException(e);
        }

        // get the -script option. If there isn't one then use args[0]
        String scriptFile = null;
        if (cmdLine.hasOption("script")) {
            scriptFile = cmdLine.getOptionValue("script");
        } else {
            scriptFile = args[0];
        }

        //
        // Use classloader to find file
        //
        URL url = this.getClass().getClassLoader().getResource(scriptFile);
        // check if the script file exists
        if (url == null && !(new File(scriptFile)).exists()) {
            throw new JellyException("Script file " + scriptFile + " not found");
        }

        try {
            // extract the -o option for the output file to use
            final XMLOutput output = cmdLine.hasOption("o") ?
                    XMLOutput.createXMLOutput(new FileWriter(cmdLine.getOptionValue("o"))) :
                    XMLOutput.createXMLOutput(System.out);

            Jelly jelly = new Jelly();
            jelly.setScript(scriptFile);

            Script script = jelly.compileScript();

            // add the system properties and the command line arguments
            JellyContext context = jelly.getJellyContext();
            context.setVariable("args", args);
            context.setVariable("commandLine", cmdLine);
            script.run(context, output);

            // now lets wait for all threads to close
            Runtime.getRuntime().addShutdownHook(new Thread() {
                    public void run() {
                        try {
                            output.close();
                        }
                        catch (Exception e) {
                            // ignore errors
                        }
                    }
                }
            );

        } catch (Exception e) {
            throw new JellyException(e);
        }

    }

    /**
     * Parse the command line using CLI. -o and -script are reserved for Jelly.
     * -Dsysprop=sysval is support on the command line as well.
     */
    public CommandLine parseCommandLineOptions(String[] args) throws ParseException {
        // create the expected options
        Options cmdLineOptions = new Options();
        cmdLineOptions.addOption("o", true, "Output file");
        cmdLineOptions.addOption("script", true, "Jelly script to run");

        // -D options will be added to the system properties
        Properties sysProps = System.getProperties();

        // filter the system property setting from the arg list
        // before passing it to the CLI parser
        ArrayList filteredArgList = new ArrayList();

        for (int i=0;i<args.length;i++) {
            String arg = args[i];

            // if this is a -D property parse it and add it to the system properties.
            // -D args will not be copied into the filteredArgList.
            if (arg.startsWith("-D") && (arg.length() > 2)) {
                arg = arg.substring(2);
                StringTokenizer toks = new StringTokenizer(arg, "=");

                if (toks.countTokens() == 2) {
                    // add the tokens to the system properties
                    sysProps.setProperty(toks.nextToken(), toks.nextToken());
                } else {
                    System.err.println("Invalid system property: " + arg);
                }

            } else {
                // add this to the filtered list of arguments
                filteredArgList.add(arg);

                // add additional "-?" options to the options object. if this is not done
                // the only options allowed would be "-o" and "-script".
                if (arg.startsWith("-") && arg.length() > 1) {
                    if (!(arg.equals("-o") && arg.equals("-script"))) {
                        cmdLineOptions.addOption(arg.substring(1, arg.length()), true, "dynamic option");
                    }
                }
            }
        }

        // make the filteredArgList into an array
        String[] filterArgs = new String[filteredArgList.size()];
        filteredArgList.toArray(filterArgs);

        // parse the command line
        Parser parser = new GnuParser();
        return parser.parse(cmdLineOptions, filterArgs);
    }

}
