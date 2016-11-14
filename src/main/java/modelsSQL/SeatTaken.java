package modelsSQL;

/**
 * Created by raychen on 2016/11/10.
 */
public class SeatTaken {
    private long routeId;
    private int type;
    private int carriageNum;
    private int seatNum;

    public void setRouteId(long routeId) {
        this.routeId = routeId;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setCarriageNum(int carriageNum) {
        this.carriageNum = carriageNum;
    }

    public void setSeatNum(int seatNum) {
        this.seatNum = seatNum;
    }

    public long getRouteId() {
        return routeId;
    }

    public int getType() {
        return type;
    }

    public int getCarriageNum() {
        return carriageNum;
    }

    public int getSeatNum() {
        return seatNum;
    }
}
