package control;

import java.net.UnknownHostException;
import util.Connect;
import util.FormatDate;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;

public class Repos {	
	
	public static String getOwner(String repo) throws UnknownHostException{
		DB db = Connect.getInstance().getDB("ghtorrent");
		DBCollection dbcRepos = db.getCollection("repos");
		BasicDBObject queryRepos = new BasicDBObject("full_name",repo);
		BasicDBObject fields = new BasicDBObject();
		fields.put("owner.login", 1);
		fields.put("_id", 0);
		DBObject repoObject = dbcRepos.findOne(queryRepos,fields);
		String owner = ((BasicDBObject)((BasicDBObject) repoObject).get("owner")).get("login").toString();
		return owner;
	}
	
	public static String getRepoData(String repo) throws UnknownHostException{
		DB db = Connect.getInstance().getDB("ghtorrent");
		DBCollection dbcRepos = db.getCollection("repos");
		BasicDBObject queryRepos = new BasicDBObject("full_name",repo);
		DBObject repoObject = dbcRepos.findOne(queryRepos);
		String ageRepo = FormatDate.getAge(((BasicDBObject) repoObject).get("created_at").toString());
		return ageRepo+","+((BasicDBObject) repoObject).get("stargazers_count")+","+
				((BasicDBObject) repoObject).get("watchers_count")+","+
				((BasicDBObject) repoObject).get("language")+","+
				((BasicDBObject) repoObject).get("forks_count")+","+
				((BasicDBObject) repoObject).get("open_issues_count")+","+
				((BasicDBObject) repoObject).get("subscribers_count")+","+
				((BasicDBObject) repoObject).get("has_wiki");
	}
	
	public static int getPullRepoClosed (String date, String firstCreateDate, String repo, String owner) throws UnknownHostException{
		DB db = Connect.getInstance().getDB("ghtorrent");
		DBCollection issues = db.getCollection("issues");
		BasicDBObject query = new BasicDBObject("created_at", new BasicDBObject("$lt",date).append("$gte", firstCreateDate)); //consulta com data menor que a data do pull request
		query.append("repo", repo);
		query.append("pull_request", new BasicDBObject("$exists", true));
		BasicDBObject fields = new BasicDBObject();
		fields.put("number", 1);
		fields.put("_id", 0);
		int count = issues.find(query, fields).count();
		return count;
	}
	
	public static int getPullRepoMerged (String date, String firstCreateDate, String repo, String owner) throws UnknownHostException{
		DB db = Connect.getInstance().getDB("ghtorrent");
		DBCollection pulls = db.getCollection("pull_requests");
		BasicDBObject query = new BasicDBObject("created_at", new BasicDBObject("$lt", date).append("$gte", firstCreateDate));
		query.append("merged", true);
		query.append("repo", repo);
		query.append("owner", owner);
		BasicDBObject fields = new BasicDBObject();
		fields.put("number", 1);
		fields.put("_id", 0);
		int numberPull = pulls.find(query,fields).count();
		return numberPull;
	}
	
}
