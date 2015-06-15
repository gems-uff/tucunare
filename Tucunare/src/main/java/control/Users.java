package control;

import java.net.UnknownHostException;

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
}
