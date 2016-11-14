package MysqlLogic;

import DBHelpers.MysqlHelper;
import modelsSQL.SeatTaken;
import util.SqlUtil;
import util.Util;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by raychen on 2016/11/10.
 */
public class SeatHelper {

    MysqlHelper helper;

    public SeatHelper(){
        helper = MysqlHelper.getInstance();
    }

    public List<Integer> getAllLeftSeats(long routeId, int type, List<SeatTaken> seatTakens){
        //get route's carriages
        int carriages_all = SqlUtil.getRouteCarriages(routeId);
        //get all seats
        List<Integer> allSeats = Util.getAllSeatsByType(type, carriages_all);
        //remove taken seats
        for (int i = 0; i < seatTakens.size(); i++) {
            SeatTaken seat = seatTakens.get(i);
            if (seat.getRouteId() == routeId && seat.getType() == type)
                allSeats.remove(new Integer(seat.getCarriageNum()*100 + seat.getSeatNum()));
        }
        return allSeats;
    }

    public List<SeatTaken> queryTakenSeats(String beginTime, String endTime, String beginPlace, String endPlace, int type){
        String typeOption = "";
        if (type > 0) typeOption = " AND ticket.type = "+type;
        String sql = "SELECT" +
                "  ticket.routeId," +
                "  ticket.carriageNum as carriageNum," +
                "  ticket.seatNum as seatNum," +
                "  type " +
                "FROM tbl_ticket ticket " +
                "  JOIN tbl_timetable time1 ON ticket.beginTimetableId = time1.id" +
                "  JOIN tbl_timetable time2 ON ticket.endTimetableId = time2.id" +
                "  JOIN (SELECT seg1.routeId, seg1.segmentNum AS beginSeg, seg2.segmentNum AS endSeg" +
                "        FROM tbl_route_segment seg1" +
                "          JOIN tbl_route_segment seg2 ON seg1.routeId = seg2.routeId" +
                "        WHERE seg1.beginPlace = '"+beginPlace+"' AND seg2.endPlace = '"+endPlace+"' AND seg1.segmentNum <= seg2.segmentNum) AS place" +
                "  ON ticket.routeId = place.routeId " +
                "WHERE time1.beginTime < '"+endTime+"' AND time2.endTime > '"+beginTime+"'" +
                "  AND time1.segmentNum <= place.endSeg AND time2.segmentNum >= place.beginSeg " +
                typeOption ;
        List<SeatTaken> leftTickets = new LinkedList<SeatTaken>();
        ResultSet rs = helper.query(sql);
        try {
            while (rs.next()) {
                SeatTaken tickets = new SeatTaken();
                tickets.setRouteId(rs.getLong("routeId"));
                tickets.setType(rs.getInt("type"));
                tickets.setCarriageNum(rs.getInt("carriageNum"));
                tickets.setSeatNum(rs.getInt("seatNum"));
                leftTickets.add(tickets);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return leftTickets;
    }
}
