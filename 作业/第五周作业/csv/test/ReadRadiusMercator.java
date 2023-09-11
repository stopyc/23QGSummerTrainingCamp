package com.hekai.csv.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hekai.Mercator.GeoTransform;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import com.vividsolutions.jts.geom.Coordinate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * \* User: hekaijie
 * \* Date: 2023/8/7
 * \* Time: 21:46
 * \* Description:
 * \
 */

//读取经纬度
@Component
public class ReadRadiusMercator {
    String address = "C:\\Users\\hekaijie\\Desktop\\test.csv";
    GeoTransform geoTransform = new GeoTransform();

    public List<String> readRadius() {
        //json化
        ObjectMapper mapper = new ObjectMapper();
        List<String> result = new ArrayList<>();
        // 读取行数-1 即为需要传的地区数目
        // 1.读取全部数据
        // 2.判断数据是否为空
        // 3.获取列索引,设置列的坐标链表
        // 4.存储列的坐标
        // 5.转成json存在List<String>里

        try (Reader reader = Files.newBufferedReader(Paths.get(address));
             CSVReader csvReader = new CSVReader(reader)) {
            //获取全部数据
            List<String[]> records = csvReader.readAll();
            //判断数据是否为空
            if (records.isEmpty()) {
                //没有数据,返回空
                System.out.println("数据为空");
                return null;
            }


            //有数据,进行读取
            //获取第一行的列数确定数据的列数,第一列是id忽略
            for (int columnIndex = 1; columnIndex < records.get(0).length; columnIndex++) {
                // 先读单数, 后columnIndex+1
                // stringInColumn存储每一行的数据

                List<CoordinateInSql> stringInColumn = new ArrayList<>();
                List<Coordinate> LongitudeAndLatitude = new ArrayList<>();

                // 单数表示为x坐标
                for (String[] record : records) {
                    if (record[columnIndex].equals("")) {
                        //列尾或者断层
                        System.out.println("当前列读完");
                        break;
                    } else {
                        //存储单个坐标
                        CoordinateInSql cs = new CoordinateInSql();
                        cs.setX(record[columnIndex]);
                        cs.setY("0");
                        stringInColumn.add(cs);
                    }
                }
                // 读双数,为y坐标
                columnIndex ++;
                for (int lineIndex = 0; lineIndex < records.size(); lineIndex++) {
                    String[] record = records.get(lineIndex);
                    if (record[columnIndex].equals("")) {
                        //列尾或者断层
                        System.out.println("当前列读完");
                        break;
                    } else {
                        CoordinateInSql cs = stringInColumn.get(lineIndex);
                        cs.setY(record[columnIndex]);
                        stringInColumn.set(lineIndex, cs);
                    }
                }

                // 墨卡托转成经纬度
                for (int i = 1; i < stringInColumn.size(); i++) {
                    Coordinate geographicCoordinate =
                            new Coordinate(Double.parseDouble(stringInColumn.get(i).getX()),
                                           Double.parseDouble(stringInColumn.get(i).getY()));
                    //获取坐标
                    geographicCoordinate = geoTransform.geographic2Mercator(geographicCoordinate);
                    LongitudeAndLatitude.add(geographicCoordinate);
                }

                String json = mapper.writeValueAsString(LongitudeAndLatitude);
                result.add(json);
            }
            return result;

        } catch (IOException | CsvException ex) {
            ex.printStackTrace();
        }

        return null;
    }
}
