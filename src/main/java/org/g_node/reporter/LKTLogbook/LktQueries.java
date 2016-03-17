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

/**
 * Class providing all SPARQL queries available to the LktLogbook use case.
 *
 * @author Michael Sonntag (sonntag@bio.lmu.de)
 */
public final class LktQueries {

    /**
     * Collection of RDF prefixes used by the SPARQL queries provided by this class.
     */
    public static final String QUERY_PREFIXES = String.join("",
            "prefix lkt:   <https://orcid.org/0000-0003-4857-1083#>",
            "prefix rdf:   <http://www.w3.org/1999/02/22-rdf-syntax-ns#>",
            "prefix gn:    <https://github.com/G-Node/neuro-ontology/>",
            "prefix rdfs:  <http://www.w3.org/2000/01/rdf-schema#>",
            "prefix xs:    <http://www.w3.org/2001/XMLSchema#>",
            "prefix foaf:  <http://xmlns.com/foaf/0.1/>",
            "prefix dc:    <http://purl.org/dc/terms/>"
    );

    /**
     * SPARQL Query returning information centered on experiments.
     */
    public static final String EXPERIMENTS_QUERY = String.join("",
            "prefix lkt:   <https://orcid.org/0000-0003-4857-1083#>",
            "prefix rdf:   <http://www.w3.org/1999/02/22-rdf-syntax-ns#>",
            "prefix gn:    <https://github.com/G-Node/neuro-ontology/>",
            "prefix rdfs:  <http://www.w3.org/2000/01/rdf-schema#>",
            "prefix xs:    <http://www.w3.org/2001/XMLSchema#>",
            "prefix foaf:  <http://xmlns.com/foaf/0.1/>",
            "prefix dc:    <http://purl.org/dc/terms/>",
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

    /**
     * SPARQL query returning information centered on trial subjects.
     */
    public static final String SUBJECTS_QUERY = String.join("",
            "prefix lkt:   <https://orcid.org/0000-0003-4857-1083#>",
            "prefix rdf:   <http://www.w3.org/1999/02/22-rdf-syntax-ns#>",
            "prefix gn:    <https://github.com/G-Node/neuro-ontology/>",
            "prefix rdfs:  <http://www.w3.org/2000/01/rdf-schema#>",
            "prefix xs:    <http://www.w3.org/2001/XMLSchema#>",
            "prefix foaf:  <http://xmlns.com/foaf/0.1/>",
            "prefix dc:    <http://purl.org/dc/terms/>",
            "SELECT ?SubjectID ?PermitNumber ?SpeciesName ?ScientificName ?Sex ?BirthDate ?WithdrawalDate ",
            " ?FirstLogEntry ?FirstExperimenter ?LastLogEntry ?LastExperimenter ?LastComment ",
            "?ExitLogEntry ?ExitComment ",
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
            "OPTIONAL { ?node gn:hasSubjectLogEntry ?l2 . ?l2 gn:startedAt ?FirstLogEntry ; ",
            "gn:hasExperimenter ?expUUID . ?expUUID foaf:name ?FirstExperimenter . }",
            "OPTIONAL { ?node gn:hasSubjectLogEntry ?l3 . ?l3 gn:startedAt ?LastLogEntry ; ",
            "gn:hasExperimenter ?expUUID2 ; rdfs:comment ?LastComment . ?expUUID2 foaf:name ?LastExperimenter . }",
            "OPTIONAL { ?node gn:hasSubjectLogEntry ?l4 . ?l4 gn:startedAt ?ExitLogEntry ; ",
            "rdfs:comment ?ExitComment . FILTER regex(?ExitComment, \".*(Euthanasie|Ausgeschleust).*\", \"i\")}",
            "}",
            " ORDER BY ?SubjectID ?EntryDate"
    );

}
