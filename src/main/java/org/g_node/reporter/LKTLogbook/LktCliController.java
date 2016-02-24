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
import java.util.Collections;
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
import org.g_node.micro.commons.RDFService;
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
     * Output formats available to the reporter tool. Entries should always be upper case.
     */
    //TODO this should be moved to a class where it can be accessed from other reporter tools as well.
    private final Set<String> outputFormats = Collections.singleton("CSV");

    /**
     * Constructor populates the Map of queries available to this reporter.
     * Entries have to be be upper case.
     */
    public LktCliController() {

        // TODO add custom cli option, load SPARQL from file and use instead of prepared report.

        final String queryPrefixes = String.join("",
                "prefix lkt:   <https://orcid.org/0000-0003-4857-1083#>",
                "prefix rdf:   <http://www.w3.org/1999/02/22-rdf-syntax-ns#>",
                "prefix gn:    <https://github.com/G-Node/neuro-ontology/>",
                "prefix rdfs:  <http://www.w3.org/2000/01/rdf-schema#>",
                "prefix xs:    <http://www.w3.org/2001/XMLSchema#>",
                "prefix foaf:  <http://xmlns.com/foaf/0.1/>",
                "prefix dc:    <http://purl.org/dc/terms/>"
                );

        final String experimentsQuery = String.join("",
                queryPrefixes,
                "SELECT ?Project ?Experiment ?ExperimentDate ?Paradigm ?ParadigmSpecifics ",
                "?Experimenter ?ExperimentComment ?SubjectId ?BirthDate ?Sex ?WithdrawalDate ",
                "?PermitNumber ?ExperimentId ",
                " WHERE {",
                "{",
                "?node a gn:Project ;",
                "rdfs:label ?Project ;",
                "gn:hasExperiment ?ExperimentId ;",
                "gn:hasProvenance ?ProvenanceId .",
                "}",
                " OPTIONAL {",
                "?ExperimentId rdfs:comment ?ExperimentComment .",
                "?ExperimentId gn:hasParadigm ?Paradigm .",
                "?ExperimentId gn:hasParadigmSpecifics ?ParadigmSpecifics .",
                "}",
                "?ExperimentId rdfs:label ?Experiment .",
                "?ExperimentId gn:startedAt ?ExperimentDate .",
                "?ExperimentId gn:hasSubject ?Subject .",
                "?ExperimentId gn:hasExperimenter ?ExperimenterId .",
                "?ExperimenterId foaf:name ?Experimenter .",
                "?Subject gn:hasSubjectID ?SubjectId .",
                "?Subject gn:hasPermit ?PermitId .",
                "?Subject gn:hasBirthDate ?BirthDate .",
                "?Subject gn:hasSex ?Sex .",
                "?Subject gn:hasWithdrawalDate ?WithdrawalDate .",
                "?PermitId gn:hasNumber ?PermitNumber .",
                "}",
                " ORDER BY ?Project ?SubjectId ?ExperimentDate"
        );

        //final String subjectsQuery = String.join("", queryPrefixes, "SELECT ?SubjectID ?Sex WHERE { ?node a gn:Subject ; gn:hasSubjectID ?SubjectID ; gn:hasSex ?Sex . FILTER regex(?Sex, \"m\")}");

        final String subjectsQuery = String.join("",
                queryPrefixes,
                "SELECT ?SubjectID ?PermitNumber ?SpeciesName ?ScientificName ?Sex ?BirthDate ?WithdrawalDate ",
                " ?FirstLogEntry ?FirstExperimenter ?LastLogEntry ?LastExperimenter ?ExitComment ?ExitLogEntry ?ExCom ",
                " WHERE ",
                "{",
                "{",
                "?node a gn:Subject ;",
                "gn:hasSubjectID ?SubjectID ;",
                "gn:hasSpeciesName ?SpeciesName ;",
                "gn:hasScientificName ?ScientificName ;",
                "gn:hasSex ?Sex ;",
                "gn:hasBirthDate ?BirthDate ;",
                "gn:hasWithdrawalDate ?WithdrawalDate ;",
                "gn:hasPermit ?PermitID .",
                "}",
                "?PermitID gn:hasNumber ?PermitNumber .",
                "{",
                    "SELECT ?node (MIN(?date) as ?FirstLogEntry)",
                    " WHERE { ",
                        "?node a gn:Subject ; gn:hasSubjectLogEntry ?sl . ?sl gn:startedAt ?date . ",
                    "} GROUP BY ?node ?FirstLogEntry",
                "}",
                "{",
                    "SELECT ?node (MAX(?date) as ?LastLogEntry)",
                    " WHERE { ",
                        "?node a gn:Subject ; gn:hasSubjectLogEntry ?sl . ?sl gn:startedAt ?date . ",
                    "} GROUP BY ?node ?LastLogEntry",
                "}",
                "{",
                    "SELECT ?node (MIN(?date) as ?ExitLogEntry)",
                    " WHERE { ",
                        "?node a gn:Subject ; gn:hasSubjectLogEntry ?sl . ?sl gn:startedAt ?date ; rdfs:comment ?c . ",
                        " FILTER regex(?c, \".*(Euthanasie|Ausgeschleust).*\") ",
                    "} GROUP BY ?node ?ExitLogEntry",
                "}",
                "OPTIONAL { ?node gn:hasSubjectLogEntry ?l2 . ?l2 gn:startedAt ?FirstLogEntry ; gn:hasExperimenter ?expUUID . ?expUUID foaf:name ?FirstExperimenter . }",
                "OPTIONAL { ?node gn:hasSubjectLogEntry ?l3 . ?l3 gn:startedAt ?LastLogEntry ; gn:hasExperimenter ?expUUID2 ; rdfs:comment ?ExitComment . ?expUUID2 foaf:name ?LastExperimenter . }",
                "OPTIONAL { ?node gn:hasSubjectLogEntry ?l4 . ?l4 gn:startedAt ?ExitLogEntry ; rdfs:comment ?ExCom . }",
                "}",
                " ORDER BY ?SubjectID ?EntryDate"
        );

        this.reports = new HashMap<String, String>() { {
                put("EXPERIMENTS", experimentsQuery);
                put("SUBJECTS", subjectsQuery);
            } };
    }

    final String blub = ".*(Euthanasie|Ausgeschleust).*";

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

        if (!CtrlCheckService.isSupportedCliArgValue(cmd.getOptionValue("r"), this.reports.keySet(), "-r/-report")) {
            return;
        }

        final String outputFormat = cmd.getOptionValue("f", "CSV");
        if (!CtrlCheckService.isSupportedOutputFormat(outputFormat, this.outputFormats)) {
            return;
        }

        final Model queryModel = RDFService.openModelFromFile(inFile);

        final String queryString = this.reports.get(cmd.getOptionValue("r").toUpperCase(Locale.ENGLISH));

        final Query query = QueryFactory.create(queryString);

        LktCliController.LOGGER.info("Start query...");

        try (QueryExecution qexec = QueryExecutionFactory.create(query, queryModel)) {
            final ResultSet result = qexec.execSelect();

            final String defaultOutputFile = String.join("", AppUtils.getTimeStamp("yyyyMMddHHmm"), "_out.csv");

            RDFService.saveResultsToCsv(result, cmd.getOptionValue("o", defaultOutputFile));
        }
    }

}
