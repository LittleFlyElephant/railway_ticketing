import MysqlLogic.TicketHelper;
import modelsSQL.SeatLeft;
import models.Ticket;
import org.junit.Before;
import org.junit.Test;
import util.SqlUtil;
import util.Util;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by raychen on 2016/11/10.
 */
public class MysqlTest {

    private TicketHelper helper;

    @Before
    public void setUp(){
        helper = new TicketHelper();
    }

    @Test
    public void testShowTickets(){
        String beginTime = "2016-12-12 00:00:00";
        String endTime = "2016-12-13 00:00:00";
        String beginPlace = "北京南";
        String endPlace = "南京南";
        int type = 0;
        List<SeatLeft> lefts = helper.showLeftTickets(beginTime, endTime, beginPlace, endPlace, type);
        System.out.println("--------------------------------------------");
        System.out.println("为您查询到的车票信息如下:");
        for (SeatLeft left: lefts) {
            System.out.println("高铁"+ SqlUtil.getRouteName(left.getRouteId())+"次列车 "+ Util.getTypeName(type) +" 剩余票数: "+ left.getSeats().size()+" 发车时间: "+left.getRouteUser().getBeginTime()+" 预计到达时间: "+left.getRouteUser().getEndTime());
        }
    }

    @Test
    public void testBookTickets(){
        String beginTime = "2016-12-12 00:00:00";
        String endTime = "2016-12-13 00:00:00";
        String beginPlace = "北京南";
        String endPlace = "南京南";
        long routeChoose = 1;
        int typeChoose = 0;
        int num = 3;
        List<String> names = new ArrayList<String>();
        List<String> cardIds = new ArrayList<String>();
        names.add("陈睿");names.add("陈小恩");names.add("陈晓妮");
        cardIds.add("1283");cardIds.add("3231");cardIds.add("121x");
        List<Ticket> res = helper.bookTickets(beginPlace, endPlace, beginTime, endTime, routeChoose, typeChoose, num, names, cardIds);
        if (res == null || res.size() == 0){
            System.out.println("购票失败");
        }else {
            System.out.println("购票成功, 购票信息如下:");
            for (Ticket ticket: res) {
                String s = ticket.getCarraiageNum()+"车厢 "+ticket.getSeatNum()+"号座位 ";
                if (typeChoose == 3) s = "无座";
                System.out.println("--------------------------------------------");
                System.out.println("车次: "+ticket.getRoute() +" 座位: "+s);
                System.out.println("出发地: "+ beginPlace+"  到达地: "+endPlace);
                System.out.println("购票人姓名: "+ticket.getName()+" 身份证: "+ ticket.getCardId());
                System.out.println("预计发车时间: "+ticket.getBeginTime());
            }
        }
    }
}
