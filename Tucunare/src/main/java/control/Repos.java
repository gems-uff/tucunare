package control;

import java.net.UnknownHostException;

import util.Connect;
import util.FormatDate;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;

public class Repos {
	public static String getRepoData(String repo) throws UnknownHostException{
		DB db = Connect.getInstance().getDB("ghtorrent");
		DBCollection dbcRepos = db.getCollection("repos");
		BasicDBObject queryRepos = new BasicDBObject("full_name",repo);
		DBObject repoObject = dbcRepos.findOne(queryRepos);
		String ageRepo = FormatDate.getAgeRepo(((BasicDBObject) repoObject).get("created_at").toString());
		return ageRepo+";"+((BasicDBObject) repoObject).get("stargazers_count")+";"+
				((BasicDBObject) repoObject).get("watchers_count")+";"+
				((BasicDBObject) repoObject).get("language")+";"+
				((BasicDBObject) repoObject).get("forks_count")+";"+
				((BasicDBObject) repoObject).get("open_issues_count")+";"+
				((BasicDBObject) repoObject).get("subscribers_count");
	}
}
