package util;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by raychen on 2016/11/10.
 */
public class Util {
    public static String getTypeName(int type){
        switch (type){
            case 0:
                return "商务座";
            case 1:
                return "一等座";
            case 2:
                return "二等座";
            case 3:
                return "无座";
        }
        return "错误";
    }

    public static int getTypePrice(int type){
        switch (type){
            case 0:
                return 100;
            case 1:
                return 60;
            case 2:
                return 30;
            case 3:
                return 20;
        }
        return -1;
    }

    public static List<Integer> getAllSeatsByType(int type, int carriages_all){
        List<Integer> allSeats = new LinkedList<Integer>();
        int carriageStart = -1;
        int carriages = 0;
        int seats = 0;
        switch (type) {
            case 0 :
                carriages = carriages_all/8;
                carriageStart = 1;
                seats = 24;
                break;
            case 1 :
                carriages = carriages_all/4;
                carriageStart = carriages_all/8+1;
                seats = 60;
                break;
            case 2 :
                carriages = carriages_all/8*5;
                carriageStart = carriages_all/8*3+1;
                seats = 80;
                break;
            case 3 :
                carriages = carriages_all/8*5;
                carriageStart = carriages_all/8*3+1;
                seats = 20;
                break;
        }
        //add all seats
        for (int i = carriageStart; i < carriageStart+carriages; i++) {
            for (int j = 1; j <= seats; j++) {
                allSeats.add(i*100+j);
            }
        }
        return allSeats;
    }

    public static void reduceSeats(List<Integer> seats, int len, int segLen){
        double rate = (double) segLen / len;
        if (rate < 0.3) rate = 0.3;
        else if (rate >= 0.8) rate = 1;
        int reduceSize = (int) (seats.size() * (1.0-rate));
        for (int i = 0; i < reduceSize; i++) {
            int index = (int) (Math.random()*seats.size());
            seats.remove(index);
        }
    }
}
