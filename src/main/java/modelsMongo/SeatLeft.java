package modelsMongo;

import org.bson.types.ObjectId;

import java.util.List;

/**
 * Created by raychen on 2016/11/11.
 */
public class SeatLeft {
    private ObjectId routeId;
    private int type;
    private List<Integer> leftSeats;
    private RouteUser routeUser;

    public RouteUser getRouteUser() {
        return routeUser;
    }

    public void setRouteUser(RouteUser routeUser) {
        this.routeUser = routeUser;
    }

    public void setRouteId(ObjectId routeId) {
        this.routeId = routeId;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setLeftSeats(List<Integer> leftSeats) {
        this.leftSeats = leftSeats;
    }

    public ObjectId getRouteId() {
        return routeId;
    }

    public int getType() {
        return type;
    }

    public List<Integer> getLeftSeats() {
        return leftSeats;
    }
}
