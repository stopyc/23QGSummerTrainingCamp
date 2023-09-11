package com.hekai.test;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * \* User: hekaijie
 * \* Date: 2023/8/6
 * \* Time: 17:50
 * \* Description:
 * \
 */
public class testJson {
    public static void main(String[] args) {
        List<Point> points = new ArrayList<>();

        // 添加坐标点到集合
        points.add(new Point(1.0, 2.0));
        points.add(new Point(3.5, 4.2));
        points.add(new Point(-2.0, 0.5));

        Gson gson = new Gson();
        String json = gson.toJson(points);
        System.out.println(json);


        Point[] pointArray = gson.fromJson(json, Point[].class);
        List<Point> points1 = Arrays.asList(pointArray);

        System.out.println("===========================");

        // 遍历并打印每个坐标点
        for (Point point : points1) {
            System.out.println("x = " + point.getX() + ", y = " + point.getY());
        }
    }

}
