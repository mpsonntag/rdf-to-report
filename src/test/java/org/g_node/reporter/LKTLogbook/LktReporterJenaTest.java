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

import com.hp.hpl.jena.query.QueryParseException;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for the {@link LktReporterJena} class. The output stream is redirected
 * from the console to a different PrintStream and reset after tests are finished
 * to avoid mixing tool error messages with actual test error messages.
 *
 * @author Michael Sonntag (sonntag@bio.lmu.de)
 */
public class LktReporterJenaTest {

    private ByteArrayOutputStream outStream;
    private PrintStream stdout;

    private final String tmpRoot = System.getProperty("java.io.tmpdir");
    private final String testFolderName = this.getClass().getSimpleName();
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
    public void testRunReporterInvalidQuery() throws Exception {
        final String invalidQuery = "I am not a SPARQL query";
        final String outFile = "out";
        final String errorMessage = "Invalid query: ";

        final Throwable thrown = catchThrowable(
                () -> LktReporterJena.runReport(this.testRdfFile.getAbsolutePath(), invalidQuery, outFile, ""));
        assertThat(this.outStream.toString()).contains(errorMessage);
        assertThat(thrown).isInstanceOf(QueryParseException.class);
    }

    @Test
    public void testRunReporterValidQuery() throws Exception {
        final String query = "PREFIX foaf: <http://xmlns.com/foaf/0.1/> SELECT ?name WHERE {?node foaf:name ?name . }";
        final String outFile = this.testFileFolder.resolve("out.csv").toString();
        final String outFormat = "CSV";

        LktReporterJena.runReport(this.testRdfFile.getAbsolutePath(), query, outFile, outFormat);
        assertThat(Files.exists(Paths.get(outFile))).isTrue();
    }

}
