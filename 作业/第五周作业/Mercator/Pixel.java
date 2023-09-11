package com.hekai.Mercator;
import lombok.Data;
/**
 * \* User: hekaijie
 * \* Date: 2023/8/7
 * \* Time: 15:34
 * \* Description:
 * \
 */

/**
 *
 * 屏幕像素坐标类，该类为基础类（单位：像素）。
 * 像素坐标系（Pixel Coordinates）单位。
 * 以左上角为原点(0,0)，向右向下为正方向。
 */
@Data
public class Pixel {
    /**
     * 横向像素
     */
    long x;
    /**
     * 纵向像素
     */
    long y;

    /**
     * 根据给定参数构造Pixel的新实例
     * @param x 横向像素
     * @param y 纵向像素
     */
    public Pixel(long x, long y){
        this.x=x;
        this.y=y;
    }
}