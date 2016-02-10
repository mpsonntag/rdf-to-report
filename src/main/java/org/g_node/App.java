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

import org.apache.log4j.Logger;
import org.g_node.micro.commons.AppUtils;

/**
 * Main application class used to parse command line input and pass
 * information to the appropriate modules.
 *
 * This application is a prototype, don't hate me if stuff is partially suboptimal or outright sucks.
 *
 * @author Michael Sonntag (sonntag@bio.lmu.de)
 */
public class App {

    /**
     * Access to the main log4j LOGGER.
     */
    private static final Logger LOGGER = Logger.getLogger(App.class.getName());

    /**
     * Main method of the CLI framework.
     * @param args Command line input arguments.
     */
    public static void main(final String[] args) {

        App.LOGGER.info("\n");
        App.LOGGER.info(String.join("", AppUtils.getTimeStamp("dd.MM.yyyy HH:mm"), ", Starting logfile."));
        App.LOGGER.info(String.join("", "Input arguments: '", String.join(" ", args), "'"));

        if (args.length > 0) {
            System.out.println("Implement CLI argument parsing here.");
        } else {
            App.LOGGER.error(
                    String.join("", "No existing report tool selected!",
                            "\n\t Please use syntax 'java -jar rdf-to-report.jar [reporter] [options]'",
                            "\n\t e.g. 'java -jar rdf-to-report.jar default -i RdfFile.ttl -o outFile -f CVS'"
                    )
            );
        }
    }

}
