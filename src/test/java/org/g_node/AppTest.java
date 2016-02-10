/**
 * Copyright (c) 2016, German Neuroinformatics Node (G-Node)
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted under the terms of the BSD License. See
 * LICENSE file in the root of the Project.
 */

package org.g_node;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.assertj.core.api.Assertions.assertThat;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for the main App. Output and Error streams are redirected
 * from the console to a different PrintStream and reset after tests are finished
 * to avoid mixing tool error messages with actual test error messages.
 *
 * @author Michael Sonntag (sonntag@bio.lmu.de)
 */
public class AppTest {

    private ByteArrayOutputStream outStream;
    private PrintStream stdout;

    /**
     * Redirect Error and Out stream.
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {

        this.stdout = System.out;
        this.outStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(this.outStream));

        Logger rootLogger = Logger.getRootLogger();
        rootLogger.setLevel(Level.INFO);
        rootLogger.addAppender(
                new ConsoleAppender(
                        new PatternLayout("[%-5p] %m%n")
                )
        );
    }

    /**
     * Reset Out stream to the console after the tests are done.
     * @throws Exception
     */
    @After
    public void tearDown() throws Exception {
        System.setOut(this.stdout);
    }

    @Test
    public void testMainEmptyArgs() throws Exception {
        final String[] emptyArgs = new String[0];
        App.main(emptyArgs);
        assertThat(this.outStream.toString()).contains("[ERROR] No existing report tool selected!");
    }

    @Test
    public void testMainInvalidArgs() throws Exception {
        final String[] invalidArgs = new String[1];
        invalidArgs[0] = "iDoNotExist";
        App.main(invalidArgs);
        assertThat(this.outStream.toString()).contains("[ERROR] No existing report tool selected!");
    }

    @Test
    public void testMainLKTMissingRequiredOptions() throws Exception {
        final String useCase = "lkt";
        final String missingOptMessage = "Missing required option:";
        final String missingArgMessage = "Missing argument for option: ";
        final String argVal = "argumentValue";

        String[] missingOptionArgs = new String[1];
        missingOptionArgs[0] = useCase;
        App.main(missingOptionArgs);
        assertThat(this.outStream.toString()).contains("Missing required options:");

        missingOptionArgs = new String[2];
        missingOptionArgs[0] = useCase;
        missingOptionArgs[1] = "-i";
        App.main(missingOptionArgs);
        assertThat(this.outStream.toString()).contains(String.join("", missingArgMessage, "i"));

        missingOptionArgs = new String[2];
        missingOptionArgs[0] = useCase;
        missingOptionArgs[1] = "-r";
        App.main(missingOptionArgs);
        assertThat(this.outStream.toString()).contains(String.join("", missingArgMessage, "r"));

        missingOptionArgs = new String[3];
        missingOptionArgs[0] = useCase;
        missingOptionArgs[1] = "-i";
        missingOptionArgs[2] = argVal;
        App.main(missingOptionArgs);
        assertThat(this.outStream.toString()).contains(missingOptMessage);

        missingOptionArgs = new String[3];
        missingOptionArgs[0] = useCase;
        missingOptionArgs[1] = "-r";
        missingOptionArgs[2] = argVal;
        App.main(missingOptionArgs);
        assertThat(this.outStream.toString()).contains(missingOptMessage);
    }

    @Test
    public void testMainHelp() throws Exception {
        final String useCase = "lkt";
        final String argVal = "val";

        final String[] helpArgs = new String[6];
        helpArgs[0] = useCase;
        helpArgs[1] = "-h";
        helpArgs[2] = "-i";
        helpArgs[3] = argVal;
        helpArgs[4] = "-r";
        helpArgs[5] = argVal;
        App.main(helpArgs);
        assertThat(this.outStream.toString()).contains("usage: Help");
    }

}
