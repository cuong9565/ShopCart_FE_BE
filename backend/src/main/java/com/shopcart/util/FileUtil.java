package com.shopcart.util;

import java.io.File;

/**
 * Utility class for file validation.
 *
 * <p>Provides common helper methods to verify file existence.
 * Designed to be reusable across the application.</p>
 */
public class FileUtil {
    // Private constructor to prevent instantiation
    private FileUtil() {}

    /**
     * Checks whether a file exists at the given path.
     *
     * <p>This method performs a fail-fast validation. If the file does not exist,
     * an exception is thrown immediately to prevent further processing.</p>
     *
     * @param path the relative or absolute file path
     * @throws IllegalStateException if the file does not exist
     */
    public static void checkFileExits(String path){
        File file = new File(path);

        if(!file.exists()){
            throw new IllegalStateException(
                "Not found file at path: root/backend/" + path 
            );
        }
    }
}