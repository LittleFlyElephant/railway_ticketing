package DBHelpers;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import modelsMongo.RouteUser;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import util.FileHelper;
import util.MongoUtil;
import util.Util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by raychen on 2016/11/10.
 */
public class MongoDBInitiate {

    private MongoDBHelper helper;

    public MongoDBInitiate(){
        helper = MongoDBHelper.getInstance();
    }

    public void createRoutesCollection(String name, List<String> routes){
        MongoCollection<Document> collection = helper.getCollection(name);
        collection.drop();
        helper.createCollection(name);
        collection = helper.getCollection(name);
        List<Document> routeDocs = new ArrayList<Document>();
        int t = 0;
        for (String route: routes) {
            String[] sp1 = route.split(" ");
            Document aRoute = new Document();
            aRoute.append("routeName", sp1[0]);
            int num = ((int) (Math.random() * 2) + 1) * 8;
            //special
            if (t==0) {num = 8;t++;}
            aRoute.append("carriages", num);
            String[] sp2 = sp1[1].split("-");
            aRoute.append("segments", sp2.length);
            List<Document> stations = new ArrayList<Document>();
            for (int i = 0; i < sp2.length; i++) {
                Document station = new Document();
                station.append("stationName", sp2[i]);
                station.append("stationNum", i+1);
                stations.add(station);
            }
            aRoute.append("stations", stations);
            routeDocs.add(aRoute);
        }
        collection.insertMany(routeDocs);
    }

    public void createTimetableCollection(String name, String time, boolean isFirst){
        MongoCollection<Document> collection = helper.getCollection(name);
        if (isFirst){
            collection.drop();
            helper.createCollection(name);
            collection = helper.getCollection(name);
        }
        MongoCollection<Document> routesColl = helper.getCollection("routes");
        //set time
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar time1 = Calendar.getInstance();

        //add documents
        MongoCursor<Document> allRoutes = routesColl.find().iterator();
        List<Document> allTimetables = new ArrayList<Document>();
        while (allRoutes.hasNext()){
            try {
                time1.setTime(format.parse(time));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Document timetable = new Document();
            Document route = allRoutes.next();
            timetable.append("routeId", route.getObjectId("_id"));
            timetable.append("startTime", time1.getTime());
            List<Document> stations = (List<Document>) route.get("stations");
            List<Document> stationsTimes = new ArrayList<Document>();
            for (Document station: stations) {
                Document stationTime = new Document();
                int stationNum = station.getInteger("stationNum");
                stationTime.append("time", time1.getTime());
                stationTime.append("stationNum", stationNum);
                stationsTimes.add(stationTime);
                long timeInMill = time1.getTimeInMillis();
                timeInMill += 60*60*1000;
                time1.setTimeInMillis(timeInMill);
            }
            timetable.append("stationTimes", stationsTimes);
            allTimetables.add(timetable);
        }
        collection.insertMany(allTimetables);
    }

    public void createTicketCollection(String name, String beginTime, String endTime, boolean isFirst){
        MongoCollection<Document> collection = helper.getCollection(name);
        if (isFirst){
            collection.drop();
            helper.createCollection(name);
            collection = helper.getCollection(name);
        }
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        MongoCursor<Document> routes = helper.getCollection("routes").find().iterator();
        List<Document> tickets = new ArrayList<Document>();
        while (routes.hasNext()){
            Document route = routes.next();
            Bson query = null;
            try {
                query = Filters.and(Filters.eq("routeId", route.getObjectId("_id")),
                        Filters.gte("startTime", format.parse(beginTime)),
                        Filters.lte("startTime", format.parse(endTime)));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Document timetable = helper.getCollection("timetable").find(query).first();
            List<Document> stationTimes = (List<Document>) timetable.get("stationTimes");
            int carriages = route.getInteger("carriages");
            for (int i = 0; i < 4; i++) {
                List<Integer> allSeats = Util.getAllSeatsByType(i, carriages);
                int len = allSeats.size() /2;
                for (int j = 0; j < len; j++) {
                    int index = (int) (Math.random()*allSeats.size());
                    int seat = allSeats.get(index);
                    allSeats.remove(index);
                    int index_before = (int) (Math.random()*stationTimes.size());
                    if (index_before > 0) index_before --;
                    int index_after = (int) (Math.random()*(stationTimes.size()-index_before-1)) + index_before +1;

                    Document ticket = new Document();
                    ticket.append("routeId", route.getObjectId("_id"));
                    ticket.append("timetableId", timetable.getObjectId("_id"));
                    ticket.append("seatNum", seat%100);
                    ticket.append("carriageNum", seat/100);
                    ticket.append("type", i);
                    ticket.append("beginNum", stationTimes.get(index_before).getInteger("stationNum"));
                    ticket.append("endNum", stationTimes.get(index_after).getInteger("stationNum"));
                    ticket.append("name", "测试人员");
                    ticket.append("cardId", "3891x");
                    ticket.append("price", 20);
                    tickets.add(ticket);
                }
            }
        }
//        int[] seatNums = {1, 1, 1, 4};
//        int[] carriageNums = {1, 2, 2, 3};
//        int[] types = {0, 1, 1, 1};
//        int[] beginNums = {1, 1, 3, 1};
//        int[] endNums = {4, 2, 4, 3};
//        List<Document> tickets = new ArrayList<Document>();
//
//        for (int i = 0; i < seatNums.length; i++) {
//        }
        collection.insertMany(tickets);
    }

    public static void main(String[] args) {
        MongoDBInitiate initiate = new MongoDBInitiate();
        FileHelper fileHelper = new FileHelper();
        MongoDBHelper mongoDBHelper = MongoDBHelper.getInstance();
        List<String> routes = fileHelper.getRoutes(FileHelper.filePath);

        initiate.createRoutesCollection("routes", routes);
        for (int i = 12; i < 19; i++) {
            boolean b = false;
            if (i == 12) b = true;
            initiate.createTimetableCollection("timetable", "2016-12-"+i+" 00:30:00",b);
            System.out.println("done day: "+ i+" time table");
            initiate.createTicketCollection("tickets", "2016-12-"+i+" 00:00:00", "2016-12-"+(i+1)+" 00:00:00", b);
            System.out.println("done tickets");
        }
//        MongoCollection<Document> times = mongoDBHelper.getCollection("timetable");
//        MongoCollection<Document> route = mongoDBHelper.getCollection("routes");
//        Document test1 = times.find().first();
//        ObjectId rid = test1.getObjectId("rid");
//        System.out.println(rid);
////        BasicDBObject query = new BasicDBObject();
////        query.put("_id", rid);
//        Document rs = route.find(Filters.eq("_id", rid)).first();
//        List<Document> stations = (List<Document>) rs.get("stations");
//        System.out.println(stations.size());

    }
}
