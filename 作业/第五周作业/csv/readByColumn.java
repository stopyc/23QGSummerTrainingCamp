package com.hekai.csv;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import com.opencsv.exceptions.CsvValidationException;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * \* User: hekaijie
 * \* Date: 2023/8/7
 * \* Time: 18:27
 * \* Description:
 * \
 */
public class readByColumn {
    public static void main(String[] args) {
        String address = "C:\\Users\\hekaijie\\Desktop\\test.csv";
        try (Reader reader = Files.newBufferedReader(Paths.get(address));
             CSVReader csvReader = new CSVReader(reader)) {

            //String[]保存每一行的数据
            //List保存的是每一行构成的数据集合,获取csv文件所有信息
            List<String[]> records = csvReader.readAll();

            //如果获取到了数据
            if (!records.isEmpty()) {
                //循环列,获取本次需要获得的列的索引
                for (int columnIndex = 1; columnIndex < records.get(0).length; columnIndex++) {
                    //循环,读取每一行对应列的值
                    for (String[] record : records) {
                        if (record[columnIndex].equals("")) {
                            //列尾或者断层
                            break;
                        } else {
                            System.out.println(record[columnIndex]);
                        }
                    }
                    //分层
                    System.out.println();
                }
            }
        } catch (IOException | CsvException ex) {
            ex.printStackTrace();
        }
    }
}
