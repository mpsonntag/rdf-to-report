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

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import org.apache.log4j.Logger;
import org.g_node.micro.commons.RDFService;

/**
 * Class handles query execution and saving the results.
 *
 * @author Michael Sonntag (sonntag@bio.lmu.de)
 */
public class LktReporter {
    /**
     * Access to the main LOGGER.
     */
    private static final Logger LOGGER = Logger.getLogger(LktCliController.class.getName());

    /**
     * Method to run a SPARQL query on an RDF file and save the results to an output file.
     * @param inFile Path and filename of an RDF file that is to be queried.
     * @param queryString SPARQL query.
     * @param outFile Path and filename where the results of the query are saved to.
     * @param outputFormat Format of the output file.
     */
    public static void runReport(final String inFile, final String queryString,
                                 final String outFile, final String outputFormat) {

        LktReporter.LOGGER.info("Start query...");
        final Model queryModel = RDFService.openModelFromFile(inFile);
        final Query query = QueryFactory.create(queryString);

        try (QueryExecution qexec = QueryExecutionFactory.create(query, queryModel)) {
            final ResultSet result = qexec.execSelect();

            LktReporter.LOGGER.info("Save results...");
            RDFService.saveResultsToSupportedFile(result, outputFormat, outFile);
        }
    }

}
