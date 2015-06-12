package control;

import java.net.UnknownHostException;
import util.Connect;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;

public class Issues {
	public static String getClosedbyPull(Integer numberPull, String repo) throws UnknownHostException{
		DB db = Connect.getInstance().getDB("ghtorrent");
		DBCollection dbcIssues = db.getCollection("issues");
		BasicDBObject queryIssue = new BasicDBObject("number",numberPull); 
		queryIssue.append("repo", repo);
		DBObject issue = dbcIssues.findOne(queryIssue);
		String closedbyPull="";
		closedbyPull = ((BasicDBObject) ((BasicDBObject) issue).get("closed_by")).get("login").toString() ;
		return closedbyPull;
	}
}
