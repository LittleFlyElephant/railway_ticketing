package util;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by raychen on 2016/11/8.
 */
public class FileHelper {
    public static String filePath = "src/main/resources/r.txt";

    public List<String> getRoutes(String file){
        List<String> routes = new LinkedList<String>();
        try {
            FileReader freader = new FileReader(file);
            BufferedReader breader = new BufferedReader(freader);
            String s;
            try {
                while ((s = breader.readLine())!=null){
                    routes.add(s);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return routes;
    }

    public static void main(String[] args) {
        FileHelper helper = new FileHelper();
        List<String> routes = helper.getRoutes(FileHelper.filePath);
        System.out.println(routes.get(0));
    }
}
