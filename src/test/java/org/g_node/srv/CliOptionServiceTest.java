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

import java.util.Collections;
import java.util.Set;
import org.apache.commons.cli.Option;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.Test;

/**
 * Unit tests for the {@link CliOptionService} class.
 *
 * @author Michael Sonntag (sonntag@bio.lmu.de)
 */
public class CliOptionServiceTest {

    /**
     * Test option letter, long option text, description, use of alternative description as well as
     * isRequired, hasArgument and hasArguments state of the help CLI option.
     * @throws Exception
     */
    @Test
    public void testHelpOption() throws Exception {
        final String shortOpt = "h";
        final String longOpt = "help";
        final String desc = "Print this message.";
        final String altDesc = "Different message.";
        final Boolean isRequired = false;
        final Boolean hasArgument = false;
        final Boolean hasArguments = false;

        final Option defaultOption = CliOptionService.getHelpOption("");
        this.assertOption(defaultOption, shortOpt, longOpt, desc, isRequired, hasArgument, hasArguments);

        final Option altDescOption = CliOptionService.getHelpOption(altDesc);
        this.assertOption(altDescOption, shortOpt, longOpt, altDesc, isRequired, hasArgument, hasArguments);
    }

    /**
     * Test option letter, long option text, description, use of alternative description as well as
     * isRequired, hasArgument and hasArguments state of the input file CLI option.
     * @throws Exception
     */
    @Test
    public void testInFileOpt() throws Exception {
        final String shortOpt = "i";
        final String longOpt = "input-rdf";
        final String desc = "RDF file containing the main database. ";
        final String altDesc = "Different message.";
        final Boolean isRequired = true;
        final Boolean hasArgument = true;
        final Boolean hasArguments = false;

        final Option defaultOption = CliOptionService.getInFileOption("");
        this.assertOption(defaultOption, shortOpt, longOpt, desc, isRequired, hasArgument, hasArguments);

        final Option altDescOption = CliOptionService.getInFileOption(altDesc);
        this.assertOption(altDescOption, shortOpt, longOpt, altDesc, isRequired, hasArgument, hasArguments);
    }

    /**
     * Test option letter, long option text, description, use of alternative description as well as
     * isRequired, hasArgument and hasArguments state of the report CLI option.
     * @throws Exception
     */
    @Test
    public void testReportOpt() throws Exception {
        final String shortOpt = "r";
        final String longOpt = "report";
        final Set reports = Collections.singleton("theOnlyReport");
        final String desc = String.join("", "Reports available to the selected tool: ", reports.toString());
        final String altDesc = "Different message.";
        final Boolean isRequired = true;
        final Boolean hasArgument = true;
        final Boolean hasArguments = false;

        final Option defaultOption = CliOptionService.getReportOption("", reports);
        this.assertOption(defaultOption, shortOpt, longOpt, desc, isRequired, hasArgument, hasArguments);

        final Option altDescOption = CliOptionService.getReportOption(altDesc, reports);
        this.assertOption(altDescOption, shortOpt, longOpt, altDesc, isRequired, hasArgument, hasArguments);
    }

    /**
     * Test option letter, long option text, description, use of alternative description as well as
     * isRequired, hasArgument and hasArguments state of the output file CLI option.
     * @throws Exception
     */
    @Test
    public void testOutFileOpt() throws Exception {
        final String shortOpt = "o";
        final String longOpt = "out-file";
        final String desc = "Optional: Path and name of the output file. ";
        final String altDesc = "Different message.";
        final Boolean isRequired = false;
        final Boolean hasArgument = true;
        final Boolean hasArguments = false;

        final Option defaultOption = CliOptionService.getOutFileOption("");
        this.assertOption(defaultOption, shortOpt, longOpt, desc, isRequired, hasArgument, hasArguments);

        final Option altDescOption = CliOptionService.getOutFileOption(altDesc);
        this.assertOption(altDescOption, shortOpt, longOpt, altDesc, isRequired, hasArgument, hasArguments);
    }

    /**
     * Test option letter, long option text, description, use of alternative description as well as
     * isRequired, hasArgument and hasArguments state of the output format CLI option.
     * @throws Exception
     */
    @Test
    public void testOutFormatOpt() throws Exception {
        final String shortOpt = "f";
        final String longOpt = "out-format";
        final Set formats = Collections.singleton("theOnlyFormat");
        final String desc = String.join("",
                "Optional: Format of the report file. Default setting is the CSV format.",
                "\nAvailable output formats: ", formats.toString());
        final String altDesc = "Different message.";
        final Boolean isRequired = false;
        final Boolean hasArgument = true;
        final Boolean hasArguments = false;

        final Option defaultOption = CliOptionService.getOutFormatOption("", formats);
        this.assertOption(defaultOption, shortOpt, longOpt, desc, isRequired, hasArgument, hasArguments);

        final Option altDescOption = CliOptionService.getOutFormatOption(altDesc, formats);
        this.assertOption(altDescOption, shortOpt, longOpt, altDesc, isRequired, hasArgument, hasArguments);
    }

    /**
     * Main assertions of all option arguments.
     * @param opt The actual {@link Option}.
     * @param shortOpt Short commandline argument of the current option.
     * @param longOpt Long commandline argument of the current option.
     * @param desc Description text of the current option.
     * @param isRequired Whether the current option is a required commandline argument.
     * @param hasArguments Whether the current option requires an additional argument.
     */
    private void assertOption(final Option opt, final String shortOpt, final String longOpt, final String desc,
                              final Boolean isRequired, final Boolean hasArgument, final Boolean hasArguments) {
        assertThat(opt.getOpt()).isEqualToIgnoringCase(shortOpt);
        assertThat(opt.getLongOpt()).isEqualToIgnoringCase(longOpt);
        assertThat(opt.getDescription()).contains(desc);
        assertThat(opt.isRequired()).isEqualTo(isRequired);
        assertThat(opt.hasArg()).isEqualTo(hasArgument);
        assertThat(opt.hasArgs()).isEqualTo(hasArguments);
    }

}
