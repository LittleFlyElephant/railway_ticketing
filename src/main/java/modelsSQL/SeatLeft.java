package modelsSQL;

import java.util.List;

/**
 * Created by raychen on 2016/11/10.
 */
public class SeatLeft {
    private long routeId;
    private int type;
    private List<Integer> seats;
    private RouteUser routeUser;

    public long getRouteId() {
        return routeId;
    }

    public int getType() {
        return type;
    }

    public List<Integer> getSeats() {
        return seats;
    }

    public void setRouteId(long routeId) {
        this.routeId = routeId;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setSeats(List<Integer> seats) {
        this.seats = seats;
    }

    public RouteUser getRouteUser() {
        return routeUser;
    }

    public void setRouteUser(RouteUser routeUser) {
        this.routeUser = routeUser;
    }
}
