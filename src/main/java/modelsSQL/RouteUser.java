package modelsSQL;

/**
 * Created by raychen on 2016/11/10.
 */
public class RouteUser {
    private long routeId;
    private int beginSeg;
    private int endSeg;
    private String beginTime;
    private String endTime;

    public String getBeginTime() {
        return beginTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setBeginTime(String beginTime) {
        this.beginTime = beginTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public long getRouteId() {
        return routeId;
    }

    public int getBeginSeg() {
        return beginSeg;
    }

    public int getEndSeg() {
        return endSeg;
    }

    public void setRouteId(long routeId) {
        this.routeId = routeId;
    }

    public void setBeginSeg(int beginSeg) {
        this.beginSeg = beginSeg;
    }

    public void setEndSeg(int endSeg) {
        this.endSeg = endSeg;
    }
}
