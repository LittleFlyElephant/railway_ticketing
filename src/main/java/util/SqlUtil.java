package util;

import DBHelpers.MysqlHelper;
import modelsSQL.RouteUser;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by raychen on 2016/11/10.
 */
public class SqlUtil {
    private static MysqlHelper helper = MysqlHelper.getInstance();

    public static List<RouteUser> getAllRoutes(String beginPlace, String endPlace){
        String sql = "SELECT seg1.routeId, seg1.segmentNum AS beginSeg, seg2.segmentNum AS endSeg" +
                " FROM tbl_route_segment seg1" +
                " JOIN tbl_route_segment seg2 ON seg1.routeId = seg2.routeId" +
                " WHERE seg1.beginPlace = '"+beginPlace+"' AND seg2.endPlace = '"+endPlace+"' AND seg1.segmentNum <= seg2.segmentNum";
        List<RouteUser> allRoutes = new LinkedList<RouteUser>();
        ResultSet rs = helper.query(sql);
        try {
            while (rs.next()) {
                RouteUser routeUser = new RouteUser();
                routeUser.setRouteId(rs.getLong("routeId"));
                routeUser.setBeginSeg(rs.getInt("beginSeg"));
                routeUser.setEndSeg(rs.getInt("endSeg"));
                allRoutes.add(routeUser);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return allRoutes;
    }

    public static String getRouteName(long routeId){
        String sql = "SELECT routeName FROM tbl_route WHERE id = "+ routeId;
        ResultSet rs = helper.query(sql);
        String routeName = "-G";
        try {
            while (rs.next()) routeName = rs.getString("routeName");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return routeName;
    }

    public static int getRouteCarriages(long routeId){
        String sql = "SELECT carriages FROM tbl_route WHERE id = "+ routeId;
        ResultSet rs = helper.query(sql);
        int carriages_all = -1;
        try {
            while (rs.next()) carriages_all = rs.getInt("carriages");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return carriages_all;
    }

    public static long getTimetableId(RouteUser routeUser, String beginTime, String endTime, boolean isBegin){
        int segNum = isBegin ? routeUser.getBeginSeg(): routeUser.getEndSeg();
        String sql = "SELECT id, beginTime, endTime FROM tbl_timetable" +
                " WHERE segmentNum = "+segNum+" AND routeId = "+routeUser.getRouteId() +" AND beginTime > '"+beginTime+"' AND beginTime < '"+endTime+"'";
        ResultSet rs = helper.query(sql);
        try {
            while (rs.next()) {
                if (isBegin) routeUser.setBeginTime(rs.getString("beginTime"));
                else routeUser.setEndTime(rs.getString("endTime"));
                return rs.getLong("id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static int getRouteSegs(long routeId){
        String sql = "SELECT segments FROM tbl_route WHERE id = "+ routeId;
        ResultSet rs = helper.query(sql);
        int segments = -1;
        try {
            while (rs.next()) segments = rs.getInt("segments");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return segments;
    }
}
