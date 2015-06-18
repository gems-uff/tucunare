package control;

import java.net.UnknownHostException;
import java.util.ArrayList;

import util.Connect;
import util.FormatDate;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

public class Users {
	public static String getFollowersUser (String user) throws UnknownHostException{
		DB db = Connect.getInstance().getDB("ghtorrent");
		DBCollection users = db.getCollection("users");
		BasicDBObject query = new BasicDBObject("login", user);
		DBObject dboUser = users.findOne(query);
		return dboUser.get("followers").toString();
	}
	
	public static String getFollowingUser (String user) throws UnknownHostException{
		DB db = Connect.getInstance().getDB("ghtorrent");
		DBCollection users = db.getCollection("users");
		BasicDBObject query = new BasicDBObject("login", user);
		DBObject dboUser = users.findOne(query);
		return dboUser.get("following").toString();
	}
	
	public static String getLocationUser (String user) throws UnknownHostException{
		DB db = Connect.getInstance().getDB("ghtorrent");
		DBCollection users = db.getCollection("users");
		BasicDBObject query = new BasicDBObject("login", user);
		DBObject dboUser = users.findOne(query);
		String location="";
		if(dboUser.get("location")!=null)
			location = dboUser.get("location").toString();
		return location; 
	}
	
	public static String getAgeUser (String user) throws UnknownHostException{
		DB db = Connect.getInstance().getDB("ghtorrent");
		DBCollection users = db.getCollection("users");
		BasicDBObject query = new BasicDBObject("login", user);
		DBObject dboUser = users.findOne(query);
		return FormatDate.getAge(dboUser.get("created_at").toString());
	}
	
	public static int getPullUserTotal (String user, String date, String repo, String owner) throws UnknownHostException{
		DB db = Connect.getInstance().getDB("ghtorrent");
		DBCollection pulls = db.getCollection("pull_requests");
		BasicDBObject query = new BasicDBObject("user.login", user);
		query.append("created_at", new BasicDBObject("$lt", date));
		query.append("html_url", new BasicDBObject("$regex", "("+owner+"/"+repo+")"));
		int numberPull = pulls.find(query).count();
		return numberPull;
	}
	
	public static int getPullUserMerged (String user, String date, String repo, String owner) throws UnknownHostException{
		DB db = Connect.getInstance().getDB("ghtorrent");
		DBCollection pulls = db.getCollection("pull_requests");
		BasicDBObject query = new BasicDBObject("user.login", user);
		query.append("created_at", new BasicDBObject("$lt", date));
		query.append("merged", true);
		query.append("html_url", new BasicDBObject("$regex", "("+owner+"/"+repo+")"));
		int numberPull = pulls.find(query).count();
		return numberPull;
	}
	
	public static boolean getWatcherRepo (String user, String date, String repo, String owner) throws UnknownHostException{
		DB db = Connect.getInstance().getDB("ghtorrent");
		DBCollection watchers = db.getCollection("watchers");
		BasicDBObject query = new BasicDBObject("login", user);
		query.append("created_at", new BasicDBObject("$lt", date));
		query.append("repo", repo);
		query.append("owner", owner);
		int numberWachter = watchers.find(query).count();
		if(numberWachter>0)
			return true;
		else
			return false;
	}
	
	public static boolean getFollowersTeam (String user, String repo, String owner) throws UnknownHostException{
		DB db = Connect.getInstance().getDB("ghtorrent");
		DBCollection followers = db.getCollection("followers");
		BasicDBObject query = new BasicDBObject("login", user);
		DBCursor cursor = followers.find(query);
		ArrayList<String> listContributors = Commits.getContributorsList(repo, owner);
		for (DBObject dbFollower : cursor) {
			if(listContributors.contains(dbFollower.get("follows").toString()))
				return true;
		}
		return false;
	}
}
