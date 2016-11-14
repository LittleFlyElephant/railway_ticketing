package DBHelpers;

import util.FileHelper;
import util.SqlUtil;
import util.Util;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by raychen on 2016/11/8.
 */
public class MysqlData {

    MysqlHelper helper;

    public MysqlData() {
        helper = MysqlHelper.getInstance();
    }

    private int getId(ResultSet rs) {
        int id = -1;
        if (rs != null) {
            try {
                rs.next();
                id = rs.getInt("id");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return id;
    }

    public List<Long> insertRoutesAndTimetable(String date, boolean insertRoute) {
        FileHelper fileHelper = new FileHelper();
        List<String> routes = fileHelper.getRoutes(FileHelper.filePath);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar calendar = Calendar.getInstance();
        for (int i = 0; i < routes.size(); i++) {
            try {
                calendar.setTime(format.parse(date));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            String[] sp1 = routes.get(i).split(" ");
            String routeName = sp1[0];
            if (sp1.length >= 2) {
                String[] sp2 = sp1[1].split("-");
                int num = ((int) (Math.random() * 2) + 1) * 8;
                //add route
                String sql = null;
                if (insertRoute){
                    sql = "INSERT INTO tbl_route "
                            + "(`routeName`, `beginPlace`, `endPlace`, `segments`, `carriages`, `createAt`) VALUES "
                            + "('" + routeName + "', '" + sp2[0] + "', '" + sp2[sp2.length - 1] + "', " + (sp2.length - 1) + ", " + num + ", '" + format.format(Calendar.getInstance().getTime()) + "')";
                    helper.update(sql);
                }
                //get routeId
                sql = "SELECT id FROM tbl_route WHERE routeName = '" + routeName + "'";
                ResultSet rs = helper.query(sql);
                int id = getId(rs);
                for (int j = 1; j < sp2.length; j++) {
                    //add segments of route
                    if (insertRoute){
                        sql = "INSERT INTO tbl_route_segment"
                                + "(`routeId`, `beginPlace`, `endPlace`, `segmentNum`, `distance`, `createAt`) VALUES"
                                + "(" + id + ", '" + sp2[j - 1] + "', '" + sp2[j] + "', " + j + ", 20, '" + format.format(Calendar.getInstance().getTime()) + "')";
                        helper.update(sql);
                    }
                    //get segmentId
                    sql = "SELECT id FROM tbl_route_segment WHERE routeId = " + id + " AND segmentNum = " + j;
                    rs = helper.query(sql);
                    int segmentId = getId(rs);
                    //add one day timetable
                    long time1 = calendar.getTimeInMillis();
                    long time2 = time1 + 60 * 60 * 1000;
                    sql = "INSERT INTO tbl_timetable"
                            + "(`routeId`, `routeSegmentId`, `beginTime`, `endTime`, `segmentNum`, `createAt`) VALUES"
                            + "("+id+", "+segmentId+", '"+format.format(new Date(time1))+"', '"+format.format(new Date(time2))+"', "+j+", '"+format.format(Calendar.getInstance().getTime())+"')";
                    helper.update(sql);
                    calendar.setTimeInMillis(time2);
                }
            }
        }
        List<Long> routeIds = new ArrayList<Long>();
        String sql = "SELECT id FROM tbl_route";
        ResultSet rs = helper.query(sql);
        try {
            while (rs.next()){
                routeIds.add(rs.getLong("id"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return routeIds;
    }

    public void insertTickets(long routeId, String beginTime, String endTime){
        String sql = "SELECT id FROM tbl_timetable " +
                "WHERE routeId = "+routeId+" AND beginTime > '"+beginTime+"' AND endTime < '"+endTime+"' " +
                "ORDER BY segmentNum";
        ResultSet rs = helper.query(sql);
        List<Long> times = new ArrayList<Long>();
        int carriages = SqlUtil.getRouteCarriages(routeId);
        try {
            while (rs.next()){
                times.add(rs.getLong("id"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < 4; i++) {
            List<Integer> allSeats = Util.getAllSeatsByType(i, carriages);
            int len = allSeats.size() /2;
            sql = "INSERT INTO tbl_ticket" +
                    " (`routeId`, `beginTimetableId`, `endTimetableId`, `type`, `carriageNum`, `seatNum`, `price`, `userName`, `cardId`, `createAt`) VALUES ";
            for (int j = 0; j < len; j++) {
                int index = (int) (Math.random()*allSeats.size());
                int seat = allSeats.get(index);
                allSeats.remove(index);
                int index_before = (int) (Math.random()*times.size());
                if (index_before > 0) index_before --;
                int index_after = (int) (Math.random()*(times.size()-index_before-1)) + index_before +1;
                sql += " ("+routeId+", "+times.get(index_before)+", "+times.get(index_after)+", "+i+", "+seat/100+", "+seat%100+", 20, 'test', 123, '2016-11-12 00:00:00')";
                if (j < len-1) sql += ',';
            }
            helper.update(sql);
        }
    }

    public static void main(String[] args) {
        MysqlData data = new MysqlData();
        for (int i = 12; i < 19; i++) {
            boolean b = false;
            if (i == 12) b = true;
            List<Long> routeIds = data.insertRoutesAndTimetable("2016-12-"+i+" 00:30:00", b);
            System.out.println("done day: "+ i+" time table");
            for (Long routeId: routeIds) {
                data.insertTickets(routeId, "2016-12-"+i, "2016-12-"+(i+1));
            }
            System.out.println("done tickets");
        }
    }
}
