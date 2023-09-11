package com.hekai.Mercator;

import com.hekai.Mercator.Pixel;
import com.hekai.Mercator.Tile;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import org.osgeo.proj4j.BasicCoordinateTransform;
import org.osgeo.proj4j.CRSFactory;
import org.osgeo.proj4j.CoordinateReferenceSystem;
import org.osgeo.proj4j.ProjCoordinate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/*************************************
 *Class Name:GeoTransform
 *Description:<坐标转换工具类>
 *@since 1.0.0
 *************************************/
@Component
public class GeoTransform {
    /**
     * 赤道半径
     */
    private final static double EarthRadius = 6378137.0;
    /**
     * 地球周长
     */
    private final static double EarthPerimeter = 2 * Math.PI * EarthRadius;
    /**
     * 瓦片大小,默认256
     */
    private final static int tileSize = 256;
    /**
     * 初始像素分辨率.
     */
    private final static double initialResolution = EarthPerimeter / tileSize;
    /**
     * 坐标原点
     */
    private final static Coordinate origin = new Coordinate(-EarthPerimeter / 2.0, EarthPerimeter / 2.0);

    private final static BasicCoordinateTransform transform1;
    private final static BasicCoordinateTransform transform2;
    private final static CRSFactory crsFactory = new CRSFactory();
    private final static CoordinateReferenceSystem WGS84CRS = crsFactory.createFromName("EPSG:4326");
    private final static CoordinateReferenceSystem WebMercatorCRS = crsFactory.createFromName("EPSG:3857");

    static {
        transform1 = new BasicCoordinateTransform(WGS84CRS, WebMercatorCRS);
        transform2 = new BasicCoordinateTransform(WebMercatorCRS, WGS84CRS);
    }

    /**
     * 缩放级别换算地图分辨率
     *
     * @param zoom 级别
     */
    public double zoomToResolution(int zoom) {
        return initialResolution / Math.pow(2, zoom);
    }

    /**
     * 经纬度转墨卡托
     *
     * @param pt 经纬度坐标
     * @return 墨卡托坐标
     */
    public Coordinate geographic2Mercator(Coordinate pt) {

        synchronized (transform1) {
            ProjCoordinate pt1 = new ProjCoordinate(pt.x, pt.y);
            ProjCoordinate pt2 = new ProjCoordinate();
            transform1.transform(pt1, pt2);
            return new Coordinate(pt2.x, pt2.y);
        }
    }

    /**
     * 墨卡托转经纬度
     *
     * @param pt 墨卡托坐标
     * @return 经纬度坐标
     */
    public Coordinate mercator2Geographic(Coordinate pt) {

        synchronized (transform2) {
            ProjCoordinate pt1 = new ProjCoordinate(pt.x, pt.y);
            ProjCoordinate pt2 = new ProjCoordinate();
            transform2.transform(pt1, pt2);
            return new Coordinate(pt2.x, pt2.y);
        }
    }

    /**
     * 墨卡托转像素
     *
     * @param pt   墨卡托坐标
     * @param zoom 缩放级别
     * @return 像素坐标
     */
    public Pixel mercator2Pixel(Coordinate pt, int zoom) {
        double res = zoomToResolution(zoom);
        Double px = (pt.x - origin.x) / res;
        Double py = -(pt.y - origin.y) / res;
        //System.out.println(px+","+py);
        //fixme 精度向下取整
        return new Pixel((long) Math.floor(px), (long) Math.floor(py));
    }

    /**
     * 像素转墨卡托
     *
     * @param pixel 像素坐标
     * @param zoom  缩放级别
     * @return 墨卡托坐标
     */
    public Coordinate pixel2Mercator(Pixel pixel, int zoom) {
        double res = zoomToResolution(zoom);
        double x = pixel.getX() * res + origin.x;
        double y = origin.y - pixel.getY() * res;
        return new Coordinate(x, y);
    }

    /**
     * 像素坐标所在瓦片
     *
     * @param pixel 像素坐标
     * @return 瓦片坐标
     */
    public Tile pixelAtTile(Pixel pixel) {
        long tileX = pixel.getX() / tileSize;
        long tileY = pixel.getY() / tileSize;
        return new Tile(tileX, tileY);
    }

    /**
     * 像素转瓦片内像素
     *
     * @param pixel 像素坐标
     * @param tile  瓦片坐标
     * @return 瓦片内像素坐标
     */
    public Pixel pixel2Tile(Pixel pixel, Tile tile) {
        long pX = pixel.getX() - tile.getX() * tileSize;
        long pY = pixel.getY() - tile.getY() * tileSize;
        return new Pixel(pX, pY);
    }

    /**
     * 瓦片内像素转像素
     *
     * @param p    瓦片内像素坐标
     * @param tile 瓦片坐标
     * @return 像素坐标
     */
    public Pixel tile2Pixel(Pixel p, Tile tile) {
        long pixelX = p.getX() + tile.getX() * tileSize;
        long pixelY = p.getY() + tile.getY() * tileSize;
        return new Pixel(pixelX, pixelY);
    }

    /**
     * 墨卡托转瓦片内像素
     *
     * @param pt   墨卡托坐标
     * @param tile 瓦片坐标
     * @param zoom 缩放级别
     * @return 瓦片内像素坐标
     */
    public Pixel mercator2Tile(Coordinate pt, Tile tile, int zoom) {
        Pixel p = mercator2Pixel(pt, zoom);
        return pixel2Tile(p, tile);
    }

    /**
     * 经纬度转像素
     *
     * @param pt   经纬度坐标
     * @param zoom 缩放级别
     * @return 像素坐标
     */
    public Pixel geographic2Pixel(Coordinate pt, int zoom) {
        Coordinate mpt = geographic2Mercator(pt);
        return mercator2Pixel(mpt, zoom);
    }

    /**
     * 经纬度转瓦片内像素
     *
     * @param pt   经纬度坐标
     * @param tile 瓦片坐标
     * @param zoom 缩放级别
     * @return 瓦片内像素坐标
     */
    public Pixel geographic2Tile(Coordinate pt, Tile tile, int zoom) {
        Pixel pixel = geographic2Pixel(pt, zoom);
        return this.pixel2Tile(pixel, tile);
    }

    /**
     * 像素转经纬度
     *
     * @param pixel 像素坐标
     * @param zoom  缩放级别
     * @return 经纬度坐标
     */
    public Coordinate pixel2Geographic(Pixel pixel, int zoom) {
        Coordinate mpt = pixel2Mercator(pixel, zoom);
        return this.mercator2Geographic(mpt);
    }


}