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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.g_node.App;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for the LktCliController class. Output and Error streams are redirected
 * from the console to a different PrintStream and reset after tests are finished
 * to avoid mixing tool error messages with actual test error messages.
 *
 * @author Michael Sonntag (sonntag@bio.lmu.de)
 */
public class LktCliControllerTest {

    private ByteArrayOutputStream outStream;
    private PrintStream stdout;

    private final String tmpRoot = System.getProperty("java.io.tmpdir");
    private final String testFolderName = "LktCliControllerTest";
    private final Path testFileFolder = Paths.get(tmpRoot, testFolderName);

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

        if (Files.exists(this.testFileFolder)) {
            FileUtils.deleteDirectory(this.testFileFolder.toFile());
        }
    }

    @Test
    public void testRunNonExistingInputFile() throws Exception {
        final String useCase = "lkt";
        final String testFileName = "iDoNotExist";

        final String[] helpArgs = new String[5];
        helpArgs[0] = useCase;
        helpArgs[1] = "-r";
        helpArgs[2] = "val";
        helpArgs[3] = "-i";
        helpArgs[4] = testFileName;

        App.main(helpArgs);
        assertThat(this.outStream.toString()).contains(
                String.join("", "File ", testFileName, " does not exist.")
        );
    }

    @Test
    public void testRunFalseInputFile() throws Exception {

        final String testFileName = "test.txt";
        final String testInvalidRDFFileName = "test.ttl";

        final File currTextFile = this.testFileFolder.resolve(testFileName).toFile();
        FileUtils.write(currTextFile, "This is a normal text file");

        final File currInvalidRdfFile = this.testFileFolder.resolve(testInvalidRDFFileName).toFile();
        FileUtils.write(currInvalidRdfFile, "This is an invalid rdf file");

        final String useCase = "lkt";

        final String[] falseInputFileArgs = new String[5];
        falseInputFileArgs[0] = useCase;
        falseInputFileArgs[1] = "-r";
        falseInputFileArgs[2] = "val";
        falseInputFileArgs[3] = "-i";
        falseInputFileArgs[4] = this.testFileFolder
                .resolve(testFileName)
                .toAbsolutePath()
                .normalize().toString();

        App.main(falseInputFileArgs);
        assertThat(this.outStream.toString()).contains("Failed to load file");
        assertThat(this.outStream.toString()).contains("Failed to determine the content type");

        falseInputFileArgs[4] = this.testFileFolder
                .resolve(testInvalidRDFFileName)
                .toAbsolutePath()
                .normalize().toString();

        App.main(falseInputFileArgs);
        assertThat(this.outStream.toString()).contains("Failed to load file");
        assertThat(this.outStream.toString()).contains("Out of place: [KEYWORD:This]");
    }

}
