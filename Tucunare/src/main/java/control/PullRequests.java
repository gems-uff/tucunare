package control;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;

import util.Connect;

public class PullRequests extends Thread{
//	Nome dos repositórios no formato owner+repo
	public static List<String> getAllRepos() throws UnknownHostException{
		DB db = Connect.getInstance().getDB("ghtorrent");
		DBCollection dbcPullRequest = db.getCollection("pull_requests");
		@SuppressWarnings("unchecked")
		List<String> repos = dbcPullRequest.distinct("repo");
		ArrayList<String> fullName = new ArrayList<>();
		for (String string : repos) {
			BasicDBObject queryRepos = new BasicDBObject("repo",string);
			BasicDBObject fields = new BasicDBObject();
			fields.put("owner", 1);
			fields.put("_id", 0);
			fullName.add(((BasicDBObject) dbcPullRequest.findOne(queryRepos, fields)).getString("owner")+"/"+string);
		}
		return fullName;
	}
//	Quantidade de pull requests em um projeto
	public static int getPulls(String repo, String owner, int prType) throws UnknownHostException{		
		DB db = Connect.getInstance().getDB("ghtorrent");
		System.out.println(Connect.getInstance() == null?"db é nulo":"db não é nulo");
		DBCollection dbcPullRequest = db.getCollection("pull_requests");
		BasicDBObject fields = new BasicDBObject();
		fields.put("number", 1);
		fields.put("_id", 0);
		BasicDBObject query = new BasicDBObject("repo",repo); //consulta com query
		query.append("owner", owner);
		if (prType == 1)
			query.append("state", "open"); //Apenas pull requests encerrados
		else
			if (prType == 2)
				query.append("state", "closed"); //Apenas pull requests encerrados
		
		return dbcPullRequest.find(query,fields).count();
	}
}