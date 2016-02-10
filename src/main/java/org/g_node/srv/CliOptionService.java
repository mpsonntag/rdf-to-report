/**
 * Copyright (c) 2016, German Neuroinformatics Node (G-Node)
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted under the terms of the BSD License. See
 * LICENSE file in the root of the Project.
 */

package org.g_node.srv;

import java.util.List;
import org.apache.commons.cli.Option;

/**
 * Class provides CLI {@link Option}s that are common
 * to all tools of this service.
 *
 * @author Michael Sonntag (sonntag@bio.lmu.de)
 */
public final class CliOptionService {

    /**
     * Returns help option.
     * @param altDesc Optional description.
     * @return Help option.
     */
    public static Option getHelpOption(final String altDesc) {

        final String defaultDesc = "Print this message.";
        final String desc = !altDesc.isEmpty() ? altDesc : defaultDesc;

        return new Option("h", "help", false, desc);
    }

    /**
     * Returns option necessary to parse an input file from the command line. This will always be a
     * required option. The command line option short hand will always be "-i" and "-input-rdf".
     * @param altDesc Alternative description replacing the default description.
     * @return CLI option handling the parsing of the RDF input file.
     */
    public static Option getInFileOption(final String altDesc) {
        final String defaultDesc = String.join("",
                "RDF file containing the main database. ",
                "Data for the required report will be fetched from this file.");
        final String desc = !altDesc.isEmpty() ? altDesc : defaultDesc;

        return Option.builder("i")
                .longOpt("input-rdf")
                .desc(desc)
                .required()
                .hasArg()
                .valueSeparator()
                .build();
    }

    /**
     * Returns option necessary to parse the requested report from the command line. This will
     * always be a required option. The command line option short hand will always be "-r" and "-report".
     * @param altDesc Alternative description replacing the default description.
     * @param reports List of reports available to the current tool.
     * @return CLI option handling the parsing of the requested report.
     */
    public static Option getReportOption(final String altDesc, final List<String> reports) {
        final String defaultDesc = String.join("",
                "Reports available to the selected tool: ",
                reports.toString());
        final String desc = !altDesc.isEmpty() ? altDesc : defaultDesc;

        return Option.builder("r")
                .longOpt("report")
                .desc(desc)
                .required()
                .hasArg()
                .valueSeparator()
                .build();
    }

    /**
     * Returns option required to parse a given output file name from the command line.
     * Command line option shorthand will always be "-o" and "-out-file". This option is optional.
     * Default output file name will be "[Timestamp]_out".
     * @param altDesc Alternative description replacing the default description.
     * @return CLI option handling the parsing of the output file.
     */
    public static Option getOutFileOption(final String altDesc) {

        final String defaultDesc = String.join("",
                "Optional: Path and name of the output file. ",
                "Files with the same name will be overwritten.",
                "\nDefault output file name will be '[Timestamp]_out'");
        final String desc = !altDesc.isEmpty() ? altDesc : defaultDesc;

        return  Option.builder("o")
                .longOpt("out-file")
                .desc(desc)
                .hasArg()
                .valueSeparator()
                .build();
    }

    /**
     * Returns option required to parse a given output format from the command line.
     * Commandline option shorthand will always be "-f" and "-out-format". This option is optional.
     * Default output format will be "CSV".
     * @param altDesc Alternative description replacing the default description.
     * @param formats List of available output formats.
     * @return CLI option handling the parsing of the output format.
     */
    public static Option getOutFormatOption(final String altDesc, final List<String> formats) {

        final String defaultDesc = String.join("",
                "Optional: Format of the report file. Default setting is the CSV format.",
                "\nAvailable output formats: ", formats.toString());
        final String desc = !altDesc.isEmpty() ? altDesc : defaultDesc;

        return Option.builder("f")
                .longOpt("out-format")
                .desc(desc)
                .hasArg()
                .valueSeparator()
                .build();
    }

}
