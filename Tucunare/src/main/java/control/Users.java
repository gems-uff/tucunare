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
	
	//implementar a busca de participantes
		public static String getParticipants(String idPullRequest, String repo) throws UnknownHostException{
			DB db = Connect.getInstance().getDB("ghtorrent");
			
			DBCollection dbcIssueComment = db.getCollection("issue_comments");
			BasicDBObject queryIssueComment = new BasicDBObject("issue_id",Integer.parseInt(idPullRequest)); //consulta com query
			queryIssueComment.append("repo", repo);
			DBCursor cursorIssueComment = dbcIssueComment.find(queryIssueComment);
			ArrayList<String> participants = new ArrayList<String>();
			for (DBObject issueComment : cursorIssueComment) {
				if(!participants.contains(((BasicDBObject) issueComment.get("user")).get("login").toString()))
					participants.add(((BasicDBObject) issueComment.get("user")).get("login").toString());
			}
			
			DBCollection dbcPullComment = db.getCollection("pull_request_comments");
			BasicDBObject queryPullComment = new BasicDBObject("pullreq_id",Integer.parseInt(idPullRequest)); //consulta com query
			queryPullComment.append("repo", repo);
			DBCursor cursorPullComment = dbcPullComment.find(queryPullComment);
			for (DBObject pullComments : cursorPullComment) {
				if(!participants.contains(((BasicDBObject) pullComments.get("user")).get("login").toString()))
					participants.add(((BasicDBObject) pullComments.get("user")).get("login").toString());
			}
			
			DBCollection dbcIssueEvent = db.getCollection("issue_events");
			BasicDBObject queryIssueEvent = new BasicDBObject("issue_id",Integer.parseInt(idPullRequest)); //consulta com query
			queryIssueEvent.append("repo", repo);
			DBCursor cursorIssueEvent = dbcIssueEvent.find(queryIssueEvent);
			for (DBObject issueEvent : cursorIssueEvent) {
				if(!participants.contains(((BasicDBObject) issueEvent.get("actor")).get("login").toString()))
					participants.add(((BasicDBObject) issueEvent.get("actor")).get("login").toString());
			}
			
			DBCollection dbcIssues = db.getCollection("issues");
			BasicDBObject queryIssue = new BasicDBObject("number",Integer.parseInt(idPullRequest)); 
			queryIssue.append("repo", repo);
			DBObject issue = dbcIssues.findOne(queryIssue);
			if( ((BasicDBObject) issue).get("closed_by") != null)
				if(!participants.contains(((BasicDBObject) ((BasicDBObject) issue).get("closed_by")).get("login").toString()))
					participants.add(((BasicDBObject) ((BasicDBObject) issue).get("closed_by")).get("login").toString());
			
			DBCollection dbcPull = db.getCollection("pull_requests");
			BasicDBObject queryPull = new BasicDBObject("number",Integer.parseInt(idPullRequest)); //consulta com query
			queryPull.append("repo", repo);
			DBObject pull = dbcPull.findOne(queryPull);
				if(!participants.contains(((BasicDBObject) pull.get("user")).get("login").toString()))
					participants.add(((BasicDBObject) pull.get("user")).get("login").toString());
			
			return participants.toString();
		}
}