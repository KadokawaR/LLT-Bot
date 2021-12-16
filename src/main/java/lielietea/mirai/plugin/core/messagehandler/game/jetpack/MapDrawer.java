package lielietea.mirai.plugin.core.messagehandler.game.jetpack;

import lielietea.mirai.plugin.utils.image.ImageCreater;

import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

import static java.lang.Math.pow;

public class MapDrawer extends BaiduAPI {

    //判断是否更加接近于点1
    public static boolean nearerToPoint1(double point1x, double point1y, double point2x, double point2y, double pointcx, double pointcy) {
        double distance1 = Math.sqrt(pow(pointcx - point1x, 2) + pow(pointcy - point1y, 2));
        double distance2 = Math.sqrt(pow(pointcx - point2x, 2) + pow(pointcy - point2y, 2));
        return (distance1 < distance2);
    }

    //判断坐标是否在以CurrentLocation按照zoomLevel生成的图片里
    public static boolean isInsidePicture(Location currentLocation, int zoomLevel, Location loc) throws Exception {
        LocationMercator currentLocationM = wgsToMercator(currentLocation);
        LocationMercator locM = wgsToMercator(loc);
        double meter = pow(2, 18 - zoomLevel);
        assert currentLocationM != null;
        double latMin = currentLocationM.y - meter * 300;
        double latMax = currentLocationM.y + meter * 300;
        double lngMin = currentLocationM.x - meter * 400;
        double lngMax = currentLocationM.x + meter * 400;
        assert locM != null;
        return (locM.x >= lngMin) && (locM.x <= lngMax) && (locM.y >= latMin) && (locM.y <= latMax);
    }

    public static double[] replacePoints(double[] res, Location loc, Location currentLocation, int zoomLevel) throws Exception {
        if (isInsidePicture(currentLocation, zoomLevel, loc)) {
            double meter = pow(2, 18 - zoomLevel);
            double pointcx = 400 + (Objects.requireNonNull(wgsToMercator(loc)).x - Objects.requireNonNull(wgsToMercator(currentLocation)).x) / meter;
            double pointcy = 300 - (Objects.requireNonNull(wgsToMercator(loc)).y - Objects.requireNonNull(wgsToMercator(currentLocation)).y) / meter;
            if (nearerToPoint1(res[0], res[1], res[2], res[3], pointcx, pointcy)) {
                res[0] = pointcx;
                res[1] = pointcy;
            } else {
                res[2] = pointcx;
                res[3] = pointcy;
            }
        }
        return res;
    }

    public static BufferedImage drawFlag(Location loc, Location currentLocation, int zoomLevel, BufferedImage originImage) {
        try {
            if (isInsidePicture(currentLocation, zoomLevel, loc)) {
                double meter = pow(2, 18 - zoomLevel);
                double pointcx = 400 + (Objects.requireNonNull(wgsToMercator(loc)).x - Objects.requireNonNull(wgsToMercator(currentLocation)).x) / meter;
                double pointcy = 300 - (Objects.requireNonNull(wgsToMercator(loc)).y - Objects.requireNonNull(wgsToMercator(currentLocation)).y) / meter;
                return ImageCreater.addImage(originImage, ImageCreater.getImageFromResource("/pics/jetpack/flag.png"), (int) pointcx - 30, (int) pointcy - 30);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return originImage;
    }

    public static BufferedImage drawAvatar(BufferedImage originImage){
        try {
            return ImageCreater.addImageAtCenter(originImage, ImageCreater.getImageFromResource("/pics/jetpack/avatar-circle-100-100.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static BufferedImage drawTrack(Location loc1, Location loc2, Location currentLocation, int zoomLevel, BufferedImage ground){

        double tan = (loc2.lat - loc1.lat) / (loc2.lng - loc1.lng);
        double point1x, point2x, point1y, point2y;
        if (tan >= 0.75) {
            point1x = 400 + 300 / tan;
            point2x = 400 - 300 / tan;
            point1y = 0;
            point2y = 600;
        } else if (tan >= 0) {
            point1x = 800;
            point2x = 0;
            point1y = 300 - 400 * tan;
            point2y = 300 + 400 * tan;
        } else if (tan >= -0.75) {
            point1x = 0;
            point2x = 800;
            point1y = 300 + 400 * tan;
            point2y = 300 - 400 * tan;
        } else {
            point1x = 400 + 300 / tan;
            point2x = 400 - 300 / tan;
            point1y = 0;
            point2y = 600;
        }

        double[] points = new double[4];
        points[0] = point1x;
        points[1] = point1y;
        points[2] = point2x;
        points[3] = point2y;

        double[] points2 = new double[0];
        double[] points3 = new double[0];
        try {
            points2 = replacePoints(points, loc1, currentLocation, zoomLevel);
            points3 = replacePoints(points2, loc2, currentLocation, zoomLevel);
        } catch (Exception e) {
            e.printStackTrace();
        }

        float[] dashingPattern1 = {15f, 15f};
        Stroke stroke1 = new BasicStroke(6f, BasicStroke.CAP_BUTT,
                BasicStroke.JOIN_MITER, 1.0f, dashingPattern1, 2.0f);

        Graphics2D g2d = ground.createGraphics();
        g2d.setStroke(stroke1);
        g2d.setColor(Color.BLUE);
        g2d.draw(new Line2D.Double(points3[0], points3[1], points3[2], points3[3]));
        g2d.dispose();
        ground = drawFlag(loc1, currentLocation, zoomLevel, ground);
        ground = drawFlag(loc2, currentLocation, zoomLevel, ground);
        ground = drawAvatar(ground);
        return ground;
    }
}
