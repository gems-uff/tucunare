package util;

import java.net.UnknownHostException;
import com.mongodb.DB;
import com.mongodb.MongoClient;

public class Connect {

	private static final String HOST = "localhost";
    private static final int PORT = 27017;
    private static final String DB_NAME = "ghtorrent";
    private static MongoClient instance;
	private DB db;
	
	public static synchronized MongoClient getInstance() throws UnknownHostException{
		if (instance == null) {
			instance = new MongoClient();
	    }
	    return instance;
	}
	
	public DB getDB(){
		if (db == null) {
            try {
            	instance = new MongoClient(HOST, PORT);
                db = instance.getDB(DB_NAME);
            } catch (Exception e) {
                //e.printStackTrace();
            }
        }
        return db;
	}
	
	public void closeConnect(){
		instance.close();
	}

}
