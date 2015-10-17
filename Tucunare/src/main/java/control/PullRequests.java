package control;

import java.net.UnknownHostException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

import util.Connect;
import util.FormatDate;

public class PullRequests extends Thread{
	@SuppressWarnings("unchecked")
	public static List<String> getAllRepos() throws UnknownHostException{
		DB db = Connect.getInstance().getDB("ghtorrent");
		DBCollection dbcPullRequest = db.getCollection("pull_requests");
		return dbcPullRequest.distinct("repo");
	}
	
	public static int getPulls(String repo, int prType) throws UnknownHostException{		
		DB db = Connect.getInstance().getDB("ghtorrent");
		System.out.println(Connect.getInstance() == null?"db é nulo":"db não é nulo");
		DBCollection dbcPullRequest = db.getCollection("pull_requests");
		BasicDBObject fields = new BasicDBObject();
		fields.put("number", 1);
		BasicDBObject query = new BasicDBObject("repo",repo); //consulta com query
		if (prType == 1)
			query.append("state", "open"); //Apenas pull requests encerrados
		else
			if (prType == 2)
				query.append("state", "closed"); //Apenas pull requests encerrados
		
		return dbcPullRequest.find(query,fields).count();
	}
}