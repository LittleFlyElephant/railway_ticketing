package MongoDBLogic;

import models.Ticket;
import modelsMongo.RouteUser;
import modelsMongo.SeatLeft;
import modelsMongo.SeatTaken;
import org.bson.types.ObjectId;
import util.MongoUtil;
import util.SqlUtil;
import util.Util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by raychen on 2016/11/11.
 */
public class TicketHelper {
    public List<SeatLeft> showLeftTickets(String beginTime, String endTime, String beginPlace, String endPlace, int type){
        //find all routes
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        List<RouteUser> allRoutes = MongoUtil.findAllRoutes(beginPlace, endPlace);
        MongoUtil.findTimetables(allRoutes, beginTime, endTime);
        //show left tickets
        List<SeatLeft> all_lefts = new LinkedList<SeatLeft>();
        for (RouteUser routeUser: allRoutes) {
            for (int i = 0; i < 4; i++) {
                if (type != -1) i = type;
                List<SeatTaken> seatsTaken = MongoUtil.getTakenSeats(routeUser.getTimetableId(), routeUser.getBeginNum(), routeUser.getEndNum());
                List<Integer> seatLefts = MongoUtil.getAllLeftSeats(routeUser.getRouteId(), i, seatsTaken);
                //售票策略
                Util.reduceSeats(seatLefts, MongoUtil.getRouteSegments(routeUser.getRouteId()), routeUser.getEndNum()-routeUser.getBeginNum()+1);
                SeatLeft left = new SeatLeft();
                left.setRouteId(routeUser.getRouteId());
                left.setType(i);
                left.setLeftSeats(seatLefts);
                left.setRouteUser(routeUser);
                all_lefts.add(left);
                if (type != -1) break;
            }
        }
        return all_lefts;
    }

    public List<Ticket> bookTickets(String beginPlace, String endPlace, String beginTime, String endTime, ObjectId routeId, int type, int num, List<String> names, List<String> cardIds){
        List<SeatLeft> all_lefts = this.showLeftTickets(beginTime, endTime, beginPlace, endPlace, type);
        List<RouteUser> allRoutes = MongoUtil.findAllRoutes(beginPlace, endPlace);
        MongoUtil.findTimetables(allRoutes, beginTime, endTime);
        List<Ticket> ret = new ArrayList<Ticket>();
        for (int i = 0; i < allRoutes.size(); i++) {
            if (allRoutes.get(i).getRouteId().equals(routeId)){
                RouteUser route = allRoutes.get(i);
                for (SeatLeft left: all_lefts) {
                    if (left.getRouteId().equals(routeId) && left.getType() == type){
                        List<Integer> allSeats = left.getLeftSeats();
                        //not enough
                        if (num > allSeats.size()) return null;
                        //book num tickets
                        List<SeatTaken> booked = new LinkedList<SeatTaken>();
                        int bookNum = (int)(Math.random()*allSeats.size());
                        int k = 0;
                        for (int j = bookNum; j < bookNum+num; j++) {
                            int b = j % allSeats.size();
                            SeatTaken seat = new SeatTaken();
                            seat.setRouteId(routeId);
                            seat.setTimetableId(route.getTimetableId());
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
                            ticket.setRoute(MongoUtil.getRouteName(route.getRouteId()));
                            ticket.setCardId(cardIds.get(k));
                            ticket.setName(names.get(k));
                            ticket.setPrice(Util.getTypePrice(type));
                            k++;
                            ret.add(ticket);
                        }
                        //update to database
                        MongoUtil.insertTickets(booked, route.getBeginNum(), route.getEndNum(), names, cardIds);
                        break;
                    }
                }
                break;
            }
        }
        return ret;
    }
}
