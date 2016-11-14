package MysqlLogic;

import DBHelpers.MysqlHelper;
import modelsSQL.RouteUser;
import modelsSQL.SeatLeft;
import modelsSQL.SeatTaken;
import models.Ticket;
import util.SqlUtil;
import util.Util;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by raychen on 2016/11/8.
 */
public class TicketHelper {

    MysqlHelper helper;
    SeatHelper seatHelper;

    public TicketHelper(){
        helper = MysqlHelper.getInstance();
        seatHelper = new SeatHelper();
    }

    public List<SeatLeft> showLeftTickets(String beginTime, String endTime, String beginPlace, String endPlace, int type){
        //find all routes
        List<RouteUser> allRoutes = SqlUtil.getAllRoutes(beginPlace, endPlace);
        //get taken seats
        List<SeatTaken> seatsTaken = seatHelper.queryTakenSeats(beginTime, endTime, beginPlace, endPlace, type);
        //show left tickets
        List<SeatLeft> all_lefts = new LinkedList<SeatLeft>();
        for (RouteUser routeUser: allRoutes) {
            for (int i = 0; i < 4; i++) {
                //增加时刻表
                SqlUtil.getTimetableId(routeUser, beginTime, endTime, true);
                SqlUtil.getTimetableId(routeUser, beginTime, endTime, false);
                //
                if (type != -1) i = type;
                List<Integer> seatLefts = seatHelper.getAllLeftSeats(routeUser.getRouteId(), i, seatsTaken);
                //售票策略
                Util.reduceSeats(seatLefts, SqlUtil.getRouteSegs(routeUser.getRouteId()), routeUser.getEndSeg()-routeUser.getBeginSeg()+1);
                SeatLeft left = new SeatLeft();
                left.setType(i);
                left.setRouteId(routeUser.getRouteId());
                left.setSeats(seatLefts);
                left.setRouteUser(routeUser);
                all_lefts.add(left);
                if (type != -1) break;
            }
        }
        return all_lefts;
    }

    public List<Ticket> bookTickets(String beginPlace, String endPlace, String beginTime, String endTime, long routeId, int type, int num, List<String> names, List<String> cardIds){
        //先查询余票
        List<SeatLeft> all_lefts = this.showLeftTickets(beginTime, endTime, beginPlace, endPlace, type);
        //寻找路线
        List<RouteUser> allRoutes = SqlUtil.getAllRoutes(beginPlace, endPlace);
        List<Ticket> ret = new ArrayList<Ticket>();
        for (int i = 0; i < allRoutes.size(); i++) {
            if (allRoutes.get(i).getRouteId() == routeId){
                RouteUser route = allRoutes.get(i);
                for (SeatLeft left: all_lefts) {
                    if (left.getRouteId() == routeId && left.getType() == type){
                        List<Integer> allSeats = left.getSeats();
                        //not enough
                        if (num > allSeats.size()) return null;
                        //book num tickets
                        //增加时刻表
                        long id_before = SqlUtil.getTimetableId(route, beginTime, endTime, true);
                        long id_end = SqlUtil.getTimetableId(route, beginTime, endTime, false);
                        List<SeatTaken> booked = new LinkedList<SeatTaken>();
                        int bookNum = (int)(Math.random()*allSeats.size());
                        int k = 0;
                        for (int j = bookNum; j < bookNum+num; j++) {
                            int b = j % allSeats.size();
                            SeatTaken seat = new SeatTaken();
                            seat.setRouteId(routeId);
                            seat.setType(type);
                            seat.setCarriageNum(allSeats.get(b)/100);
                            seat.setSeatNum(allSeats.get(b)%100);
                            booked.add(seat);
                            //增加票model
                            Ticket ticket = new Ticket();
                            ticket.setBeginPlace(beginPlace);
                            ticket.setBeginTime(route.getBeginTime());
                            ticket.setEndPlace(endPlace);
                            ticket.setEndTime(route.getEndTime());
                            ticket.setCarraiageNum(allSeats.get(b)/100);
                            ticket.setSeatNum(allSeats.get(b)%100);
                            ticket.setRoute(SqlUtil.getRouteName(routeId));
                            ticket.setCardId(cardIds.get(k));
                            ticket.setName(names.get(k));
                            ticket.setPrice(Util.getTypePrice(type));
                            k++;
                            ret.add(ticket);
                        }
                        //update to database
                        for (int j = 0; j < booked.size(); j++) {
                            String sql = "INSERT INTO tbl_ticket" +
                                    " (`routeId`, `beginTimetableId`, `endTimetableId`, `type`, `carriageNum`, `seatNum`, `price`, `userName`, `cardId`, `createAt`) VALUES " +
                                    " ("+routeId+", "+id_before+", "+id_end+", "+type+", "+booked.get(j).getCarriageNum()+", "+booked.get(j).getSeatNum()+", 20, 'cr', 123, '2016-11-12 00:00:00')";
                            helper.update(sql);
                        }
                        break;
                    }
                }
                break;
            }
        }
        return ret;
    }
}
