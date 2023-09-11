package com.hekai.csv;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * \* User: hekaijie
 * \* Date: 2023/8/7
 * \* Time: 18:14
 * \* Description:
 * \
 */
public class readByLine {
    public static void main(String[] args) {
        String address = "C:\\Users\\hekaijie\\Desktop\\test.csv";
        try (Reader reader = Files.newBufferedReader(Paths.get(address));
             CSVReader csvReader = new CSVReader(reader)) {

            String[] record;
            while ((record = csvReader.readNext()) != null) {
                System.out.println("[" + String.join(", ", record) + "]");
            }
        } catch (IOException | CsvValidationException ex) {
            ex.printStackTrace();

        }
    }
}
