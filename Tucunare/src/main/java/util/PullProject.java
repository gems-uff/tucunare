package util;

import java.net.UnknownHostException;
import java.util.List;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;

public class PullProject {

	public static void main(String[] args) throws UnknownHostException {
		DB db = Connect.getInstance().getDB("ghtorrent");
		DBCollection dbcPullRequest = db.getCollection("pull_requests");
		BasicDBObject fields = new BasicDBObject();
		fields.put("number", 1);
		fields.put("_id", 0);
		fields.put("repo", 1);
		BasicDBObject query = new BasicDBObject("state", "closed"); //consulta com query
		
		Object [] repos = dbcPullRequest.distinct("repo", query).toArray();
		for (Object object : repos) {
			query.append("repo", object.toString());
			System.out.println(object.toString()+","+dbcPullRequest.find(query,fields).count());
		}
		 

	}

}
