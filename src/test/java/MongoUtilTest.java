import com.mongodb.BasicDBObject;
import modelsMongo.RouteUser;
import modelsMongo.SeatTaken;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.junit.Before;
import org.junit.Test;
import util.MongoUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

/**
 * Created by raychen on 2016/11/11.
 */
public class MongoUtilTest {

    @Test
    public void testShowRoutes(){
        List<RouteUser> routeUsers = MongoUtil.findAllRoutes("济南西", "南京南");
        for (RouteUser routeUser: routeUsers) {
            System.out.println(routeUser.getRouteId()+" "+ routeUser.getBeginNum()+" "+routeUser.getEndNum());
        }
    }

    @Test
    public void testShowTimetable(){
        List<RouteUser> routeUsers = MongoUtil.findAllRoutes("北京南", "南京南");
        MongoUtil.findTimetables(routeUsers, "2016-11-12 00:00:00", "2016-11-13 00:00:00");
        for (RouteUser routeUser: routeUsers) {
            System.out.println(routeUser.getTimetableId());
        }
    }

    @Test
    public void testGetTakenseats(){
        List<SeatTaken> seatTakens = MongoUtil.getTakenSeats(new ObjectId("5825365d98c27f2edc281b7c"), 3, 4);
        for (SeatTaken seat: seatTakens) {
            System.out.println(seat.getCarriageNum()+" "+seat.getSeatNum()+" "+seat.getType());
        }
    }
}
