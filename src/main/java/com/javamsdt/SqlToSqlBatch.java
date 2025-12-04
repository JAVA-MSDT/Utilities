/**
 * Copyright (c) 2025: Ahmed Samy, All rights reserved.
 * LinkedIn: https://www.linkedin.com/in/java-msdt/
 * GitHub: https://github.com/JAVA-MSDT
 * Email: serenitydiver@hotmail.com
 */
package com.javamsdt;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class SqlToSqlBatch {

    public static void main(String[] args) throws IOException {
        String sqlInserts = "/sql/sqlFile.sql";
        String sqlFile = "src/main/resources/sql/batch_sqlFile.sql";

        int batchSize = 5000;
        List<String> batch = new ArrayList<>();
        int batchCount = 1;
        int totalRecords = 0;

        try (InputStream inputStream = SqlToSqlBatch.class.getResourceAsStream(sqlInserts);
             BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
             BufferedWriter bw = new BufferedWriter(new FileWriter(sqlFile))) {

            String line;

            while ((line = br.readLine()) != null) {
                if (line.trim().toUpperCase().startsWith("INSERT INTO")) {
                    batch.add(line);
                    if (batch.size() == batchSize) {
                        writeBatch(bw, batch, batchCount);
                        batch.clear();
                        batchCount++;
                        totalRecords += batchSize;
                    }
                }
            }

            if (!batch.isEmpty()) {
                writeBatch(bw, batch, batchCount);
                totalRecords += batch.size();
            }
        }

        System.out.println("Batching Complete! written a total of " + totalRecords);
    }

    private static void writeBatch(BufferedWriter writer, List<String> batch, int batchCount) throws IOException {
        System.out.println("-- Batch " + batchCount + " (" + batch.size() + " records) --\n");

        String firstLine = batch.getFirst()
                .replace("VALUES ", "VALUES \n")
                .replace(";", ",");
        writer.write(firstLine);
        writer.write("\n");

        for (int i = 1; i < batch.size() - 1; i++) {

            String statement = batch.get(i)
                    .replace("INSERT INTO users (id, username, password, email) VALUES", "")
                    .replace(";", ",");

            writer.write(statement);
            writer.write("\n");
        }
        String lastLine = batch.getLast()
                .replace("INSERT INTO users (id, username, password, email) VALUES", "");
        writer.write(lastLine);
        writer.write("\n");
        writer.write("\n");
    }
}

