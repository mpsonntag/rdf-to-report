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

import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.g_node.micro.commons.CliToolController;
import org.g_node.srv.CliOptionService;
import org.g_node.srv.CtrlCheckService;

/**
 * Class handling how to fetch reports from an RDF file specific to the LKT Logbook use case of Kay Thurley.
   *
 * @author Michael Sonntag (sonntag@bio.lmu.de)
 */
public class LktCliController implements CliToolController {
    /**
     * Reports available to the reporter tool specific for the LKT Logbook usecase. Entries should always be upper case.
     */
    private final List<String> reports = Collections.singletonList("DEFAULT");
    /**
     * Output formats available to the reporter tool. Entries should always be upper case.
     */
    //TODO this should be moved to a class where it can be accessed from other reporter tools as well.
    private final Set<String> outputFormats = Collections.singleton("CSV");
    /**
     * Method returning the commandline options of the LKT reporter tool.
     *
     * @return Available {@link CommandLine} {@link Options}.
     */
    public final Options options() {

        final Options options = new Options();

        final Option opHelp = CliOptionService.getHelpOption("");
        final Option opInRdfFile = CliOptionService.getInFileOption("");
        final Option opReport = CliOptionService.getReportOption("", this.reports);
        final Option opOutFile = CliOptionService.getOutFileOption("");
        final Option opOutFormat = CliOptionService.getOutFormatOption("", this.outputFormats);

        options.addOption(opHelp);
        options.addOption(opInRdfFile);
        options.addOption(opReport);
        options.addOption(opOutFile);
        options.addOption(opOutFormat);

        return options;
    }

    /**
     * Method to check input file, available report, output file format and to facilitate the
     * delegation of creating the report and saving it an output file.
     *
     * @param cmd User provided {@link CommandLine} input.
     */
    public final void run(final CommandLine cmd) {
        final String inFile = cmd.getOptionValue("i");
        if (!CtrlCheckService.isExistingFile(inFile)) {
            return;
        }

        if (!CtrlCheckService.isValidRdfFile(inFile)) {
            return;
        }

        if (!CtrlCheckService.isSupportedCliArgValue(cmd.getOptionValue("r"), this.reports, "-r/-report")) {
            return;
        }

        final String outputFormat = cmd.getOptionValue("f", "CSV");
        if (!CtrlCheckService.isSupportedOutputFormat(outputFormat, this.outputFormats)) {
            return;
        }

        System.out.println("\n\tImplement reading of RDF file next");
    }

}
