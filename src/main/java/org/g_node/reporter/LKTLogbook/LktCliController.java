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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.log4j.Logger;
import org.g_node.micro.commons.AppUtils;
import org.g_node.micro.commons.CliToolController;
import org.g_node.micro.rdf.RdfFileServiceJena;
import org.g_node.srv.CliOptionService;
import org.g_node.srv.CtrlCheckService;

/**
 * Class handling how to fetch reports from an RDF file specific to the LKT Logbook use case of Kay Thurley.
   *
 * @author Michael Sonntag (sonntag@bio.lmu.de)
 */
public class LktCliController implements CliToolController {
    /**
     * Access to the main LOGGER.
     */
    private static final Logger LOGGER = Logger.getLogger(LktCliController.class.getName());
    /**
     * Reports available to the reporter tool specific for the LKT Logbook use case.
     */
    private Map<String, String> reports;
    /**
     * Constructor populates the Map of queries available to this reporter.
     * Entries have to be be upper case.
     */
    public LktCliController() {

        this.reports = new HashMap<String, String>() { {
                put("EXPERIMENTS", LktQueries.EXPERIMENTS_QUERY);
                put("SUBJECTS", LktQueries.SUBJECTS_QUERY);
                put("CUSTOM", "");
            } };
    }

    /**
     * Method returning the commandline options of the LKT reporter tool.
     *
     * @return Available {@link CommandLine} {@link Options}.
     */
    public final Options options() {

        final Options options = new Options();

        final Option opHelp = CliOptionService.getHelpOption("");
        final Option opInRdfFile = CliOptionService.getInFileOption("");
        final Option opReport = CliOptionService.getReportOption("", this.reports.keySet());
        final Option opOutFile = CliOptionService.getOutFileOption("");
        final Option opOutFormat =
                CliOptionService.getOutFormatOption("", RdfFileServiceJena.QUERY_RESULT_FILE_FORMATS.keySet());

        final Option opQueryFile = Option.builder("c")
                    .longOpt("custom-query-file")
                    .desc(String.join("", "Optional: SPARQL query file. ",
                            "-r CUSTOM requires option -c with a file containing a valid SPARQL query."))
                    .hasArg()
                    .valueSeparator()
                    .build();

        options.addOption(opHelp);
        options.addOption(opInRdfFile);
        options.addOption(opReport);
        options.addOption(opOutFile);
        options.addOption(opOutFormat);
        options.addOption(opQueryFile);

        return options;
    }

    /**
     * Method to check input file, available report, output file format and to facilitate the
     * delegation of creating the report and saving it an output file.
     *
     * @param cmd User provided {@link CommandLine} input.
     */
    public final void run(final CommandLine cmd) {

        final Set<String> resultFileFormats = RdfFileServiceJena.QUERY_RESULT_FILE_FORMATS.keySet();

        final String inFile = cmd.getOptionValue("i");
        if (!CtrlCheckService.isExistingFile(inFile)) {
            return;
        }

        if (!RdfFileServiceJena.isValidRdfFile(inFile)) {
            return;
        }

        if (!CtrlCheckService.isSupportedCliArgValue(cmd.getOptionValue("r"), this.reports.keySet(), "-r/-report")) {
            return;
        }

        final String outputFormat = cmd.getOptionValue("f", "CSV");
        if (!CtrlCheckService.isSupportedOutputFormat(outputFormat, resultFileFormats)) {
            return;
        }

        String queryString = this.reports.get(cmd.getOptionValue("r").toUpperCase(Locale.ENGLISH));

        if ("CUSTOM".equals(cmd.getOptionValue("r").toUpperCase(Locale.ENGLISH))) {
            final String customQueryFile = cmd.getOptionValue("c", "");
            LktCliController.LOGGER.info(
                    String.join("", "Using custom query option -c...\t(", customQueryFile , ")")
            );
            if ("".equals(cmd.getOptionValue("c", ""))) {
                LktCliController.LOGGER.error("Missing required option: c");
                return;
            } else if (!CtrlCheckService.isExistingFile(cmd.getOptionValue("c", ""))) {
                return;
            } else {
                try {
                    queryString = new String(Files.readAllBytes(Paths.get(cmd.getOptionValue("c", ""))));
                } catch (IOException exc) {
                    LktCliController.LOGGER.error(exc.getMessage());
                    exc.printStackTrace();
                    return;
                }
            }
        }
        final String defaultOutputFile = String.join("", AppUtils.getTimeStamp("yyyyMMddHHmm"), "_out");

        LktJenaReporter.runReport(inFile, queryString, cmd.getOptionValue("o", defaultOutputFile), outputFormat);
    }

}
