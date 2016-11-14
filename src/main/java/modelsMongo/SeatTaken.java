package modelsMongo;

import org.bson.types.ObjectId;

/**
 * Created by raychen on 2016/11/11.
 */
public class SeatTaken {
    private ObjectId routeId;
    private ObjectId timetableId;
    private int seatNum;
    private int carriageNum;
    private int type;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setRouteId(ObjectId routeId) {
        this.routeId = routeId;
    }

    public void setTimetableId(ObjectId timetableId) {
        this.timetableId = timetableId;
    }

    public void setSeatNum(int seatNum) {
        this.seatNum = seatNum;
    }

    public void setCarriageNum(int carriageNum) {
        this.carriageNum = carriageNum;
    }

    public ObjectId getRouteId() {
        return routeId;
    }

    public ObjectId getTimetableId() {
        return timetableId;
    }

    public int getSeatNum() {
        return seatNum;
    }

    public int getCarriageNum() {
        return carriageNum;
    }
}
