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
	//Quantidade de seguidores do requester
	public static String getFollowersUser (String user) throws UnknownHostException{
		DB db = Connect.getInstance().getDB("ghtorrent");
		DBCollection users = db.getCollection("users");
		BasicDBObject query = new BasicDBObject("login", user);
		BasicDBObject fields = new BasicDBObject();
		fields.put("followers", 1);
		fields.put("_id", 0);
		DBObject dboUser = users.findOne(query,fields);
		String followers = "";
		if(dboUser.get("followers") != null)
			followers = dboUser.get("followers").toString(); 
		return followers;
	}
	//Quantidade de seguidos pelo requester
	public static String getFollowingUser (String user) throws UnknownHostException{
		DB db = Connect.getInstance().getDB("ghtorrent");
		DBCollection users = db.getCollection("users");
		BasicDBObject query = new BasicDBObject("login", user);
		BasicDBObject fields = new BasicDBObject();
		fields.put("following", 1);
		fields.put("_id", 0);
		DBObject dboUser = users.findOne(query,fields);
		String following = "";
		if(dboUser.get("following") != null)
			following = dboUser.get("following").toString(); 
		return following;
	}
	//Localização geográfica do requester
	public static String getLocationUser (String user) throws UnknownHostException{
		DB db = Connect.getInstance().getDB("ghtorrent");
		DBCollection users = db.getCollection("users");
		BasicDBObject query = new BasicDBObject("login", user);
		BasicDBObject fields = new BasicDBObject();
		fields.put("location", 1);
		fields.put("_id", 0);
		DBObject dboUser = users.findOne(query,fields);
		String location="";
		if(dboUser.get("location")!=null)
			location = dboUser.get("location").toString();
		return location; 
	}
	//Idade em dias do requester no GitHub
	public static String getAgeUser (String user) throws UnknownHostException{
		DB db = Connect.getInstance().getDB("ghtorrent");
		DBCollection users = db.getCollection("users");
		BasicDBObject query = new BasicDBObject("login", user);
		BasicDBObject fields = new BasicDBObject();
		fields.put("created_at", 1);
		fields.put("_id", 0);
		DBObject dboUser = users.findOne(query,fields);
		String created_at="";
		if(dboUser.get("created_at")!=null)
			created_at = FormatDate.getAge(dboUser.get("created_at").toString());
		return created_at;
	}
	//Total de pull requests enviados pelo requester anteriormente
	public static int getPullUserTotal (String user, String date, String repo, String firstCreateDate) throws UnknownHostException{
		DB db = Connect.getInstance().getDB("ghtorrent");
		DBCollection issues = db.getCollection("issues");
		BasicDBObject query = new BasicDBObject("created_at", new BasicDBObject("$lt",date).append("$gte", firstCreateDate)); //consulta com data menor que a data do pull request
		query.append("repo", repo);
		query.append("pull_request", new BasicDBObject("$exists", true));
		query.append("user", new BasicDBObject("$not", new BasicDBObject("$type", 10)));
		query.append("user.login", user);
		BasicDBObject fields = new BasicDBObject();
		fields.put("number", 1);
		fields.put("_id", 0);
		int count = issues.find(query, fields).count();
		return count;
	}
	//Total de pull requests aceitos enviados pelo requester anteriormente
	public static int getPullUserMerged (String user, String date, String repo, String firstCreateDate, String owner) throws UnknownHostException{
		DB db = Connect.getInstance().getDB("ghtorrent");
		DBCollection pulls = db.getCollection("pull_requests");
		BasicDBObject query = new BasicDBObject("user.login", user);
		query.append("created_at", new BasicDBObject("$lt", date).append("$gte", firstCreateDate));
		query.append("merged", true);
		query.append("repo", repo);
		query.append("owner", owner);
		BasicDBObject fields = new BasicDBObject();
		fields.put("number", 1);
		fields.put("_id", 0);
		int numberPull = pulls.find(query,fields).count();
		return numberPull;
	}
	//Se o requester é obervador do projeto
	public static boolean getWatcherRepo (String user, String date, String repo, String owner) throws UnknownHostException{
		DB db = Connect.getInstance().getDB("ghtorrent");
		DBCollection watchers = db.getCollection("watchers");
		BasicDBObject query = new BasicDBObject("login", user);
		query.append("created_at", new BasicDBObject("$lt", date));
		query.append("repo", repo);
		query.append("owner", owner);
		BasicDBObject fields = new BasicDBObject();
		fields.put("id", 1);
		fields.put("_id", 0);
		int numberWachter = watchers.find(query,fields).count();
		if(numberWachter>0)
			return true;
		else
			return false;
	}
	//Se o requester segue o desenvolvedor do time principal
	public static boolean getRequesterFollowsCoreTeam (String user, String closed_by) throws UnknownHostException{
		DB db = Connect.getInstance().getDB("ghtorrent");
		DBCollection followers = db.getCollection("followers");
		BasicDBObject query = new BasicDBObject("login", user);
		query.append("follows", closed_by);
		BasicDBObject fields = new BasicDBObject();
		fields.put("login", 1);
		fields.put("follows", 1);
		fields.put("_id", 0);
		DBObject f = followers.findOne(query,fields);
		if(f != null)
			return true;
		return false;
	}
	//Se o desenvolvedor do time primcipal segue o requester
	public static boolean getCoreTeamFollowsRequester (String user, String closed_by) throws UnknownHostException{
		DB db = Connect.getInstance().getDB("ghtorrent");
		DBCollection followers = db.getCollection("followers");
		BasicDBObject query = new BasicDBObject("login", closed_by);
		query.append("follows", user);
		BasicDBObject fields = new BasicDBObject();
		fields.put("login", 1);
		fields.put("follows", 1);
		fields.put("_id", 0);
		DBObject f = followers.findOne(query,fields);
		if(f != null)
			return true;
		return false;
	}
	
	public static boolean getFollowersCoreTeam (String user, String repo, String owner) throws UnknownHostException{
		DB db = Connect.getInstance().getDB("ghtorrent");
		DBCollection followers = db.getCollection("followers");
		ArrayList<String> listCoreTeam = Commits.getCoreTeamList(repo, owner);
		boolean followerCoreTeam = false;
		for (String string : listCoreTeam) {
			BasicDBObject query = new BasicDBObject("login", string);
			BasicDBObject fields = new BasicDBObject();
			fields.put("follows", 1);
			fields.put("_id", 0);
			DBCursor cursor = followers.find(query,fields);
			for (DBObject dbFollower : cursor) {
				if(dbFollower != null)
					if(dbFollower.get("follows").toString().equals(user)){
						followerCoreTeam = true;
						break;
					}	
			}
			followerCoreTeam = false;
		}
		return followerCoreTeam;
	}
	//Nome dos desenvolvedores que participaram do ciclo de vida do pull request
	public static String getParticipants(String idPullRequest, String repo, String user, String closed_by, String owner) throws UnknownHostException{
		DB db = Connect.getInstance().getDB("ghtorrent");
		
		DBCollection dbcIssueComment = db.getCollection("issue_comments");
		BasicDBObject queryIssueComment = new BasicDBObject("issue_id",Integer.parseInt(idPullRequest)); //consulta com query
		queryIssueComment.append("repo", repo);
		queryIssueComment.append("owner", owner);
		BasicDBObject fields = new BasicDBObject();
		fields.put("user.login",1);
		fields.put("_id", 0);
		DBCursor cursorIssueComment = dbcIssueComment.find(queryIssueComment,fields);
		ArrayList<String> participants = new ArrayList<String>();
		participants.add(user);
		if( cursorIssueComment != null )
		for (DBObject issueComment : cursorIssueComment) {
		  if(((BasicDBObject) issueComment) != null )
			if((BasicDBObject) issueComment.get("user") != null)
				if(!participants.contains(((BasicDBObject) issueComment.get("user")).get("login").toString()))
					participants.add(((BasicDBObject) issueComment.get("user")).get("login").toString());
		}
		
		DBCollection dbcPullComment = db.getCollection("pull_request_comments");
		BasicDBObject queryPullComment = new BasicDBObject("pullreq_id",Integer.parseInt(idPullRequest)); //consulta com query
		queryPullComment.append("repo", repo);
		queryPullComment.append("owner", owner);
		DBCursor cursorPullComment = dbcPullComment.find(queryPullComment,fields);
		if( cursorPullComment != null )
		for (DBObject pullComments : cursorPullComment) {
		  if(((BasicDBObject) pullComments) != null )	
			if((BasicDBObject) pullComments.get("user") != null)
				if(!participants.contains(((BasicDBObject) pullComments.get("user")).get("login").toString()))
					participants.add(((BasicDBObject) pullComments.get("user")).get("login").toString());
		}
		
		DBCollection dbcIssueEvent = db.getCollection("issue_events");
		BasicDBObject queryIssueEvent = new BasicDBObject("issue_id",Integer.parseInt(idPullRequest)); //consulta com query
		queryIssueEvent.append("repo", repo);
		queryIssueEvent.append("owner", owner);
		BasicDBObject fields2 = new BasicDBObject();
		fields.put("actor.login",1);
		fields.put("_id", 0);
		DBCursor cursorIssueEvent = dbcIssueEvent.find(queryIssueEvent,fields2);
		if( cursorIssueEvent != null )
		for (DBObject issueEvent : cursorIssueEvent) {
		  if(((BasicDBObject) issueEvent) != null )
			if((BasicDBObject) issueEvent.get("actor") != null)
				if(!participants.contains(((BasicDBObject) issueEvent.get("actor")).get("login").toString()))
					participants.add(((BasicDBObject) issueEvent.get("actor")).get("login").toString());
		}
		
		if(!participants.contains(closed_by))
			participants.add(closed_by);

		return participants.toString();
	}

}