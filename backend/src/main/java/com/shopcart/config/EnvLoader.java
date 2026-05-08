package com.shopcart.config;
import com.shopcart.util.FileUtil;

import io.github.cdimascio.dotenv.Dotenv;

/**
 * Configuration class for loading environment variables from a .env file.
 *
 * <p>Uses dotenv-java library to load environment variables.</p>
 *
 * <p><b>NOTE:</b> The .env file should be located at root/backend.</p>
 */
public class EnvLoader {
    static {
        // Check if .env exit in backend (fail fast if any are missing)
        FileUtil.checkFileExits(".env");

        // Load environment variables from .env file
        Dotenv dotenv = Dotenv.configure()
                            .load();
        
        // Export all .env variables to JVM system properties for global access
        dotenv.entries().forEach(entry -> {
            System.setProperty(entry.getKey(), entry.getValue());
        });

        // Validate required environment variables (fail fast if any are missing)
        validate(dotenv, 
            "SPRING_DATABASE_URL", 
            "SPRING_DATABASE_USERNAME",
            "SPRING_DATABASE_PASSWORD",
            "SERVER_PORT"
        );
    }

    /**
     * Validates required environment variables.
     *
     * <p>Collects all missing or empty variables and throws a single exception
     * to help debugging instead of failing one by one.</p>
     *
     * @param dotenv the loaded Dotenv instance
     * @param keys   list of required environment variable names
     * @throws IllegalStateException if any required variable is missing or blank
     */
    private static void validate(Dotenv dotenv, String... keys){
        // Create list missing key
        StringBuilder missing = new StringBuilder();

        // Add all keys is missing in to missing variable
        for(String key: keys){
            String value = dotenv.get(key);
            if(value == null || value.isBlank())
                missing.append("\n").append(key);
        }

        // If exits missing key => throw IllegalStateException exception
        if(missing.length() > 0)
            throw new IllegalStateException(
                "Missing required env: [" + missing + "]"
            );
    }
}