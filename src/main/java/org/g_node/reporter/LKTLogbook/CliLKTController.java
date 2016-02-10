/**
 * Copyright (c) 2016, German Neuroinformatics Node (G-Node)
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted under the terms of the BSD License. See
 * LICENSE file in the root of the Project.
 */

package org.g_node.reporter.LKTLogbook;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.g_node.micro.commons.CliToolController;

/**
 * Class handling how to fetch reports from an RDF file specific to the LKT Logbook use case of Kay Thurley.
   *
 * @author Michael Sonntag (sonntag@bio.lmu.de)
 */
public class CliLKTController implements CliToolController {
    /**
     * Method returning the commandline options of the LKT reporter tool.
     *
     * @return Available {@link CommandLine} {@link Options}.
     */
    public final Options options() {

        final Options options = new Options();

        final Option opHelp = new Option("h", "help", false, "Print this message.");

        final String inDesc = String.join("",
                "RDF file containing the main database. ",
                "Data for the required report will be fetched from this file.");

        final Option opInRdfFile = Option.builder("i")
                .longOpt("input-rdf")
                .desc(inDesc)
                .required()
                .hasArg()
                .valueSeparator()
                .build();

        // TODO implement available reports
        final String reportDesc = String.join("",
                "Reports available to the selected tool.");

        final Option opReport = Option.builder("r")
                .longOpt("report")
                .desc(reportDesc)
                .required()
                .hasArg()
                .valueSeparator()
                .build();

        final String outDesc = String.join("",
                "Optional: Path and name of the output file. ",
                "Files with the same name will be overwritten. ");

        final Option opOut = Option.builder("o")
                .longOpt("out-file")
                .desc(outDesc)
                .hasArg()
                .valueSeparator()
                .build();

        final String formatDesc = String.join("",
                "Optional: format of the report file that will be written.\n",
                "\nDefault setting is the CSV format.");

        final Option opFormat = Option.builder("f")
                .longOpt("out-format")
                .desc(formatDesc)
                .hasArg()
                .valueSeparator()
                .build();

        options.addOption(opHelp);
        options.addOption(opInRdfFile);
        options.addOption(opReport);
        options.addOption(opOut);
        options.addOption(opFormat);

        return options;
    }

    /**
     * Method to check input file, available report, output file format and to facilitate the
     * delegation of creating the report and saving it an output file.
     *
     * @param cmd User provided {@link CommandLine} input.
     */
    public final void run(final CommandLine cmd) {
        System.out.println("Implement checks and actual call of reports.");
    }

}
