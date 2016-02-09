/**
 * Copyright (c) 2016, German Neuroinformatics Node (G-Node)
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted under the terms of the BSD License. See
 * LICENSE file in the root of the Project.
 */

package org.g_node.commons;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Locale;

/**
 * Main service class for dealing with files.
 *
 * @author Michael Sonntag (sonntag@bio.lmu.de)
 */
public final class FileService {
    /**
     * Method for validating that the file actually exists.
     * @param checkFile Path and filename of the provided file.
     * @return True if the file exists, false otherwise.
     */
    public static boolean checkFile(final String checkFile) {

        return Files.exists(Paths.get(checkFile))
                && Files.exists(Paths.get(checkFile).toAbsolutePath());

    }

    /**
     * Method for validating that the provided file is of a supported file
     * type defined in the input fileTypes.
     * @param checkFile Path and filename of the provided file.
     * @param fileTypes List containing all supported file types.
     * @return True if the file is of the supported file type, false otherwise.
     */
    public static boolean checkFileType(final String checkFile, final List<String> fileTypes) {

        boolean correctFileType;

        final int i = checkFile.lastIndexOf('.');
        if (i > 0) {
            final String checkExtension = checkFile.substring(i + 1);
            correctFileType = fileTypes.contains(checkExtension.toUpperCase(Locale.ENGLISH));
        } else {
            correctFileType = false;
        }

        return correctFileType;
    }
}
