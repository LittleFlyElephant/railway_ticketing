package DBHelpers; /**
 * Created by raychen on 2016/11/8.
 */
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

public class MongoDBHelper {

    private MongoDatabase mongoDB;
    private static String DBname = "railway_ticketing";
    private static MongoDBHelper helper;

    private MongoDBHelper(){
        connect();
    }

    public static MongoDBHelper getInstance(){
        if (helper == null) helper = new MongoDBHelper();
        return helper;
    }

    public void connect(){
        try{
            // 连接到 mongodb 服务
            MongoClient mongoClient = new MongoClient("localhost" , 27017);
            // 连接到数据库
            mongoDB = mongoClient.getDatabase(DBname);
            System.out.println("Connect to database successfully");

        }catch(Exception e){
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
        }
    }

    public MongoCollection<Document> getCollection(String name){
        return mongoDB.getCollection(name);
    }

    public void createCollection(String name){
        mongoDB.createCollection(name);
    }

    public static void main(String[] args) {
        MongoDBHelper helper = new MongoDBHelper();
        helper.createCollection("routes");
    }
}
