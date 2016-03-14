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
import static org.assertj.core.api.Assertions.catchThrowable;
import com.hp.hpl.jena.query.QueryParseException;
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
 * Unit tests for the {@link LktCliController} class. Output and Error streams are redirected
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
    private final String testRdfFileName = "test.ttl";
    private final File testRdfFile = this.testFileFolder.resolve(this.testRdfFileName).toFile();

    /**
     * Redirect Error and Out stream. Set up temporary folder and minimal RDF file.
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {
        final String miniTTL = "@prefix foaf: <http://xmlns.com/foaf/0.1/> . _:a foaf:name \"MainName\"";
        FileUtils.write(this.testRdfFile, miniTTL);

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
     * Reset Out stream to the console after the tests are done. Remove all testfiles and temporary folder.
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

        final String[] cliArgs = new String[5];
        cliArgs[0] = useCase;
        cliArgs[1] = "-r";
        cliArgs[2] = "val";
        cliArgs[3] = "-i";
        cliArgs[4] = testFileName;

        App.main(cliArgs);
        assertThat(this.outStream.toString()).contains(
                String.join("", "File ", testFileName, " does not exist.")
        );
    }

    @Test
    public void testRunFalseInputFile() throws Exception {
        final String testNotRdfFileName = "test.txt";
        final File testNotRdfFile = this.testFileFolder.resolve(testNotRdfFileName).toFile();
        FileUtils.write(testNotRdfFile, "I am not an RDF file!");

        final String testInvalidRDFFileName = "testFalseInFile.ttl";
        final File testInvalidRdfFile = this.testFileFolder.resolve(testInvalidRDFFileName).toFile();
        FileUtils.write(testInvalidRdfFile, "This is an invalid rdf file");

        final String useCase = "lkt";

        final String[] cliArgs = new String[5];
        cliArgs[0] = useCase;
        cliArgs[1] = "-r";
        cliArgs[2] = "val";
        cliArgs[3] = "-i";
        cliArgs[4] = testNotRdfFile.getAbsolutePath();

        App.main(cliArgs);
        assertThat(this.outStream.toString()).contains("Failed to load file");
        assertThat(this.outStream.toString()).contains("Failed to determine the content type");

        cliArgs[4] = testInvalidRdfFile.getAbsolutePath();

        App.main(cliArgs);
        assertThat(this.outStream.toString()).contains("Failed to load file");
        assertThat(this.outStream.toString()).contains("Out of place: [KEYWORD:This]");
    }

    @Test
    public void testInvalidReport() throws Exception {
        final String useCase = "lkt";
        final String invalidReportValue = "argumentValue";
        final String errorMessage = String.join("",
                "'", invalidReportValue,"' is not a supported value of command line option ");

        final String[] cliArgs = new String[5];
        cliArgs[0] = useCase;
        cliArgs[1] = "-i";
        cliArgs[2] = this.testRdfFile.getAbsolutePath();
        cliArgs[3] = "-r";
        cliArgs[4] = invalidReportValue;

        App.main(cliArgs);
        assertThat(this.outStream.toString()).contains(errorMessage);
    }

    @Test
    public void testInvalidOutputFormat() throws Exception {
        final String useCase = "lkt";
        final String invalidOutFormat = "invalidFormat";
        final String errorMessage = String.join("", "Unsupported output format: '", invalidOutFormat, "'");
        final String reportUsed = "experiments";

        final String[] cliArgs = new String[7];
        cliArgs[0] = useCase;
        cliArgs[1] = "-i";
        cliArgs[2] = this.testRdfFile.getAbsolutePath();
        cliArgs[3] = "-r";
        cliArgs[4] = reportUsed;
        cliArgs[5] = "-f";
        cliArgs[6] = invalidOutFormat;

        App.main(cliArgs);
        assertThat(this.outStream.toString()).contains(errorMessage);
    }

    @Test
    public void testCustomCliMissing() throws Exception {
        final String useCase = "lkt";
        final String errorMessage = "Missing required option: c";

        final String[] cliArgs = new String[5];
        cliArgs[0] = useCase;
        cliArgs[1] = "-i";
        cliArgs[2] = this.testRdfFile.getAbsolutePath();
        cliArgs[3] = "-r";
        cliArgs[4] = "custom";

        App.main(cliArgs);
        assertThat(this.outStream.toString()).contains(errorMessage);
    }

    @Test
    public void testCustomCliInvalidFile() throws Exception {
        final String useCase = "lkt";
        final String missingFile = "iDoNotExist";
        final String errorMessage = String.join("", "File ", missingFile, " does not exist.");

        final String[] cliArgs = new String[7];
        cliArgs[0] = useCase;
        cliArgs[1] = "-i";
        cliArgs[2] = this.testRdfFile.getAbsolutePath();
        cliArgs[3] = "-r";
        cliArgs[4] = "custom";
        cliArgs[5] = "-c";
        cliArgs[6] = missingFile;

        App.main(cliArgs);
        assertThat(this.outStream.toString()).contains(errorMessage);
    }

    @Test
    public void testCustomCliInvalidQuery() throws Exception {
        final String useCase = "lkt";

        final String notQueryFileName = "testQuery.txt";
        final File notQueryFile = this.testFileFolder.resolve(notQueryFileName).toFile();
        FileUtils.write(notQueryFile, "I am not a query file!");

        final String errorMessage = "Lexical error at line 1, column 2.  Encountered: \" \" (32), after : \"I\"";

        final String[] cliArgs = new String[7];
        cliArgs[0] = useCase;
        cliArgs[1] = "-i";
        cliArgs[2] = this.testRdfFile.getAbsolutePath();
        cliArgs[3] = "-r";
        cliArgs[4] = "custom";
        cliArgs[5] = "-c";
        cliArgs[6] = notQueryFile.getAbsolutePath();

        final Throwable thrown = catchThrowable(
                () -> App.main(cliArgs));
        assertThat(thrown).isInstanceOf(QueryParseException.class).hasMessageContaining(errorMessage);
    }

}
