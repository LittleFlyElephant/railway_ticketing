package modelsMongo;

import org.bson.types.ObjectId;

import java.util.List;

/**
 * Created by raychen on 2016/11/11.
 */
public class RouteUser {
    private ObjectId routeId;
    private int beginNum;
    private int endNum;
    private ObjectId timetableId;
    private String beginTime;
    private String endTime;

    public void setTimetableId(ObjectId timetableId) {
        this.timetableId = timetableId;
    }

    public ObjectId getTimetableId() {
        return timetableId;
    }

    public ObjectId getRouteId() {
        return routeId;
    }

    public int getBeginNum() {
        return beginNum;
    }

    public int getEndNum() {
        return endNum;
    }

    public void setRouteId(ObjectId routeId) {
        this.routeId = routeId;
    }

    public void setBeginNum(int beginNum) {
        this.beginNum = beginNum;
    }

    public void setEndNum(int endNum) {
        this.endNum = endNum;
    }

    public void setBeginTime(String beginTime) {
        this.beginTime = beginTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getBeginTime() {
        return beginTime;
    }

    public String getEndTime() {
        return endTime;
    }
}
