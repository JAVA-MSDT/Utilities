/**
 * Copyright (c) 2024: Ahmed Samy, All rights reserved.
 * LinkedIn: https://www.linkedin.com/in/java-msdt/
 * GitHub: https://github.com/JAVA-MSDT
 * Email: serenitydiver@hotmail.com
 */

package com.javamsdt;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

import java.io.*;

/**
 * This class is a simple helper class to convert a CSV file into a SQL file.
 * It's not a full reusable implementation for everything.
 * The implementation is straight forward, you can adapt to meet your needs.
 * Adding more CSV header to read from, or change the names as you like.
 * Feel free to cpy the code and enhance it as you see it is good fit for your task.
 */
public class CSVToSQL {
    public static void main(String[] args) {
        String csvFilePath = "/csv/csvFile.csv"; // You can here use your own file
        String sqlFilePath = "src/main/resources/sql/sqlFile.sql"; // You can here use your own file

        try (InputStream inputStream = CSVToSQL.class.getResourceAsStream(csvFilePath);
             CSVReader reader = new CSVReader(new InputStreamReader(inputStream));
             BufferedWriter writer = new BufferedWriter(new FileWriter(sqlFilePath))) {
            String[] nextLine;
            reader.readNext(); // To avoid writing the header (Skip Header).

            while ((nextLine = reader.readNext()) != null) {
                // The structure below is matching the CSV file structure,
                // For your need , you can adapt this call to match your structure
                // id, username, password, email
                int id = Integer.parseInt(nextLine[0]);
                String username = nextLine[1].trim(); // .trim() for strings to avoid inserting clean value into the database without whitespaces.
                String password = nextLine[2].trim();
                String email = nextLine[3].trim();

                String SqlInsert = String.format("INSERT INTO users (id, username, password, email) VALUES (%d, '%s', '%s', '%s')",
                        id, username, password, email);

                writer.write(SqlInsert);
                writer.newLine();
            }
            long lineCount = reader.getLinesRead() - 1; // - 1 to not count the Header as a record.
            System.out.println("Done writing " + lineCount + " records into file=" + sqlFilePath);

        } catch (IOException | CsvValidationException e) {
            throw new RuntimeException(e);
        }
    }
}
