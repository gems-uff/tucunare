package util;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

public class PullProject {

	public static void main(String[] args) throws UnknownHostException {
		DB db = Connect.getInstance().getDB("ghtorrent");
		DBCollection dbcPullRequest = db.getCollection("pull_requests");
		BasicDBObject fields = new BasicDBObject();
		fields.put("number", 1);
		fields.put("_id", 0);
		fields.put("repo", 1);
		fields.put("owner", 1);
		BasicDBObject query = new BasicDBObject("state", "closed"); //consulta com query
		Object [] repos = dbcPullRequest.distinct("repo", query).toArray();
		for (Object object : repos) {
			query.append("repo", object.toString());
			DBCollection dbcRepo = db.getCollection("pull_requests");
			String repo = object.toString();
			BasicDBObject queryRepo = new BasicDBObject("repo", repo);
			String owner = (String) dbcRepo.findOne(queryRepo).get("owner");
			System.out.println(owner+"/"+object.toString()+","+dbcPullRequest.find(query,fields).count());
		}
	}
}
