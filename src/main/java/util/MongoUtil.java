package util;

import DBHelpers.MongoDBHelper;
import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import modelsMongo.RouteUser;
import modelsMongo.SeatLeft;
import modelsMongo.SeatTaken;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by raychen on 2016/11/11.
 */
public class MongoUtil {
    public static MongoDBHelper helper = MongoDBHelper.getInstance();

    public static List<RouteUser> findAllRoutes(String beginPlace, String endPlace){
        MongoCollection<Document> routesColl = helper.getCollection("routes");
        List<RouteUser> ret = new ArrayList<RouteUser>();

        MongoCursor<Document> routesCursor = routesColl.find().iterator();
        while (routesCursor.hasNext()){
            Document route = routesCursor.next();
            RouteUser routeUser = new RouteUser();
            List<Document> stations = (List<Document>) route.get("stations");
            int find = -1;
            for (Document station: stations) {
                if (find == -1 && station.getString("stationName").equals(beginPlace)){
                    routeUser.setBeginNum(station.getInteger("stationNum"));
                    find ++;
                }
                if (find == 0 && station.getString("stationName").equals(endPlace)){
                    routeUser.setEndNum(station.getInteger("stationNum"));
                    find ++;
                }
            }
            if (find == 1) {
                routeUser.setRouteId(route.getObjectId("_id"));
                ret.add(routeUser);
            }
        }
        return ret;
    }

    public static void findTimetables(List<RouteUser> routeUsers, String beginTime, String endTime){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        MongoCollection<Document> timetableColl = helper.getCollection("timetable");
        for (RouteUser routeUser: routeUsers) {
            BasicDBObject query = new BasicDBObject("routeId", new BasicDBObject("$eq", routeUser.getRouteId()));
            try {
                query.append("startTime", (new BasicDBObject("$gte", format.parse(beginTime)))
                        .append("$lte", format.parse(endTime)));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            MongoCursor<Document> timecursor = timetableColl.find(query).iterator();
            while (timecursor.hasNext()){
                Document timeItem = timecursor.next();
                List<Document> stationTimes = (List<Document>) timeItem.get("stationTimes");
                routeUser.setTimetableId(timeItem.getObjectId("_id"));
                routeUser.setBeginTime(format.format(stationTimes.get(routeUser.getBeginNum()-1).getDate("time")));
                routeUser.setEndTime(format.format(stationTimes.get(routeUser.getEndNum()-1).getDate("time")));
            }
        }
    }

    public static List<SeatTaken> getTakenSeats(ObjectId timetableId, int beginNum, int endNum){
        MongoCollection<Document> ticketsColl = helper.getCollection("tickets");
        List<SeatTaken> ret = new ArrayList<SeatTaken>();
        Bson query = Filters.and(Filters.eq("timetableId", timetableId),
                Filters.lt("beginNum", endNum),
                Filters.gt("endNum", beginNum));
        MongoCursor<Document> ticketCursor = ticketsColl.find(query).iterator();
        while (ticketCursor.hasNext()){
            Document ticket = ticketCursor.next();
            SeatTaken seatTaken = new SeatTaken();
            seatTaken.setRouteId(ticket.getObjectId("routeId"));
            seatTaken.setTimetableId(ticket.getObjectId("timetableId"));
            seatTaken.setCarriageNum(ticket.getInteger("carriageNum"));
            seatTaken.setSeatNum(ticket.getInteger("seatNum"));
            seatTaken.setType(ticket.getInteger("type"));
            ret.add(seatTaken);
        }
        return ret;
    }

    private static int getRouteCarriages(ObjectId routeId){
        MongoCollection<Document> ticketsColl = helper.getCollection("routes");
        return ticketsColl.find(Filters.eq("_id", routeId)).first().getInteger("carriages");
    }

    public static List<Integer> getAllLeftSeats(ObjectId routeId, int type, List<SeatTaken> seatTakens){
        //get route's carriages
        int carriages_all = getRouteCarriages(routeId);
        //get all seats
        List<Integer> allSeats = Util.getAllSeatsByType(type, carriages_all);
        //remove taken seats
//        System.out.println("routeId: "+routeId);
        for (int i = 0; i < seatTakens.size(); i++) {
            SeatTaken seat = seatTakens.get(i);
//            System.out.println("seat routeId: "+ seat.getRouteId() + " "+seat.getType()+" "+ seat.getCarriageNum());
            if (seat.getRouteId().equals(routeId) && seat.getType() == type)
                allSeats.remove(new Integer(seat.getCarriageNum()*100 + seat.getSeatNum()));
        }
        return allSeats;
    }

    public static String getRouteName(ObjectId routeId){
        MongoCollection<Document> ticketsColl = helper.getCollection("routes");
        return ticketsColl.find(Filters.eq("_id", routeId)).first().getString("routeName");
    }

    public static int getRouteSegments(ObjectId routeId){
        MongoCollection<Document> ticketsColl = helper.getCollection("routes");
        return ticketsColl.find(Filters.eq("_id", routeId)).first().getInteger("segments");
    }

    public static void insertTickets(List<SeatTaken> bookedSeats, int beginNum, int endNum, List<String> names, List<String> cardIds){
        MongoCollection<Document> ticketsColl = helper.getCollection("tickets");
        List<Document> tickets = new ArrayList<Document>();

        for (int i = 0; i < bookedSeats.size(); i++) {
            SeatTaken seat = bookedSeats.get(i);
            Document ticket = new Document();
            ticket.append("routeId", seat.getRouteId());
            ticket.append("timetableId", seat.getTimetableId());
            ticket.append("seatNum", seat.getSeatNum());
            ticket.append("carriageNum", seat.getCarriageNum());
            ticket.append("type", seat.getType());
            ticket.append("beginNum", beginNum);
            ticket.append("endNum", endNum);
            ticket.append("name", names.get(i));
            ticket.append("cardId", cardIds.get(i));
            ticket.append("price", 20);
            tickets.add(ticket);
        }
        ticketsColl.insertMany(tickets);
    }
}
