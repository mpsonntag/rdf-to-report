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

import java.util.Set;
import org.g_node.micro.rdf.RdfFileServiceJena;
import org.g_node.reporter.LKTLogbook.LktReporterJena;

/**
 * Class used as a switch between different RDF APIs.
 *
 * @author Michael Sonntag (sonntag@bio.lmu.de)
 */
public final class RdfServiceSwitch {
    /**
     * Switch to the query result file formats that are supported by the currently used RDF API.
     */
    public static final Set<String> QUERY_RESULT_FILE_FORMATS = RdfFileServiceJena.QUERY_RESULT_FILE_FORMATS.keySet();

    /**
     * Switch to the method that checks, if the provided file is a valid RDF file. This check is
     * dependent on the used RDF API.
     * @param uri Uri of the file that is to be checked if its a valid RDF file.
     * @return True if the file is a valid RDF file, false otherwise.
     */
    public static boolean isValidRdfFile(final String uri) {
        return RdfFileServiceJena.isValidRdfFile(uri);
    }

    /**
     * Switch to the method querying an RDF input file and writing
     * the results to an output file of a specified format.
     * @param inFile RDF file that is to be queried.
     * @param queryString SPARQL query string used to create a report from the inFile.
     * @param outputFile File where the results of the query are supposed to be saved to.
     * @param outputFormat Format of the output file.
     */
    public static void runReport(final String inFile, final String queryString,
                                   final String outputFile, final String outputFormat) {
        LktReporterJena.runReport(inFile, queryString, outputFile, outputFormat);
    }

}
