/**
 * Copyright (c) 2025: Ahmed Samy, All rights reserved.
 * LinkedIn: https://www.linkedin.com/in/java-msdt/
 * GitHub: https://github.com/JAVA-MSDT
 * Email: serenitydiver@hotmail.com
 */
package com.javamsdt;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

import java.io.*;

public class CsvToSqlBatch {

    public static void main(String[] args) throws IOException {
        String sqlInserts = "/csv/csvFile.csv";
        String sqlFile = "src/main/resources/sql/batch_csvSqlFile.sql";
        int batchSize = 5000;
        int batchCount = 1;
        int totalRecords = 0;

        try (InputStream inputStream = CSVToSQL.class.getResourceAsStream(sqlInserts);
             CSVReader reader = new CSVReader(new InputStreamReader(inputStream));
             BufferedWriter writer = new BufferedWriter(new FileWriter(sqlFile))) {
            String[] nextLine;
            // Skip the header line
            reader.readNext();

            boolean inBatch = false;
            int currentBatchCount = 0; // how many records written in current batch

            while ((nextLine = reader.readNext()) != null) {
                // The structure below is matching the CSV file structure,
                // For your need , you can adapt this call to match your structure
                // id, username, password, email
                int id = Integer.parseInt(nextLine[0]);
                String username = nextLine[1].trim(); // .trim() for strings to avoid inserting clean value into the database without whitespaces.
                String password = nextLine[2].trim();
                String email = nextLine[3].trim();

                // Start a new batch statement if needed
                if (!inBatch) {
                    System.out.println("-- Batch " + batchCount + " --\n");
                    writer.write("INSERT INTO users (id, username, password, email) VALUES\n");
                    inBatch = true;
                    currentBatchCount = 0;
                }

                // Write tuple, with comma if not the first in the batch
                if (currentBatchCount > 0) {
                    writer.write(",\n");
                }
                writer.write(String.format("(%d, '%s', '%s', '%s')", id, username, password, email));

                currentBatchCount++;
                totalRecords++;

                // If batch is full, close it with semicolon and prepare for next
                if (currentBatchCount == batchSize) {
                    writer.write(";\n\n");
                    inBatch = false;
                    batchCount++;
                }
            }

            // Close the last open batch if it wasn't exactly on the boundary
            if (inBatch && currentBatchCount > 0) {
                writer.write(";\n\n");
            }
            long lineCount = reader.getLinesRead() - 1; // - 1 to not count the Header as a record.
            System.out.println("Done writing " + lineCount + " records into file=" + sqlInserts);

        } catch (IOException | CsvValidationException e) {
            throw new RuntimeException(e);
        }

        System.out.println("Batching Complete! written a total of " + totalRecords);
    }
}

