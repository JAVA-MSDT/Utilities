/**
 * Utility to generate a large CSV file for testing CsvToSqlBatch.
 */
package com.javamsdt;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class CsvDataGenerator {

    public static void main(String[] args) throws IOException {
        String csvPath = "src/main/resources/csv/csvFile.csv";

        int totalRecords = 16250; // total number of data rows (excluding header)

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(csvPath))) {
            // Header
            writer.write("id, username, password, email");
            writer.newLine();

            // Preserve the two original sample records first
            writer.write("1, serenitydiver, pass, serenitydiver@hotmail.com");
            writer.newLine();
            writer.write("2, username, password, email@email.com");
            writer.newLine();

            // Generate the remaining records from id=3 up to totalRecords
            for (int id = 3; id <= totalRecords; id++) {
                String username = "user" + id;
                String password = "pass" + id;
                String email = "user" + id + "@example.com";

                // Match the spacing style used in the existing CSV
                writer.write(id + ", " + username + ", " + password + ", " + email);
                writer.newLine();
            }
        }

        System.out.println("CSV generated at: " + new java.io.File(csvPath).getAbsolutePath());
        System.out.println("Total data rows (excluding header): " + totalRecords);
    }
}
