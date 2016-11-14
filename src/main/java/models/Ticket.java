package models;

/**
 * Created by raychen on 2016/11/8.
 */
public class Ticket {
    private String route;
    private String beginPlace;
    private String endPlace;
    private String beginTime;
    private String endTime;
    private String rootBeginPlace;
    private String rootEndPlace;
    private int price;
    private int carraiageNum;
    private int seatNum;
    private String name;
    private String cardId;

    public String getRoute() {
        return route;
    }

    public String getBeginPlace() {
        return beginPlace;
    }

    public String getEndPlace() {
        return endPlace;
    }

    public String getBeginTime() {
        return beginTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public String getRootBeginPlace() {
        return rootBeginPlace;
    }

    public String getRootEndPlace() {
        return rootEndPlace;
    }

    public int getPrice() {
        return price;
    }

    public int getCarraiageNum() {
        return carraiageNum;
    }

    public int getSeatNum() {
        return seatNum;
    }

    public String getName() {
        return name;
    }

    public String getCardId() {
        return cardId;
    }

    public void setRoute(String route) {
        this.route = route;
    }

    public void setBeginPlace(String beginPlace) {
        this.beginPlace = beginPlace;
    }

    public void setEndPlace(String endPlace) {
        this.endPlace = endPlace;
    }

    public void setBeginTime(String beginTime) {
        this.beginTime = beginTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public void setRootBeginPlace(String rootBeginPlace) {
        this.rootBeginPlace = rootBeginPlace;
    }

    public void setRootEndPlace(String rootEndPlace) {
        this.rootEndPlace = rootEndPlace;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public void setCarraiageNum(int carraiageNum) {
        this.carraiageNum = carraiageNum;
    }

    public void setSeatNum(int seatNum) {
        this.seatNum = seatNum;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCardId(String cardId) {
        this.cardId = cardId;
    }
}
