package control;

import java.net.UnknownHostException;
import java.text.DecimalFormat;
import java.util.ArrayList;

import util.Connect;
import util.FormatDate;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

public class Issues {
	public static String getClosedbyPull(Integer numberPull, String repo) throws UnknownHostException{
		DB db = Connect.getInstance().getDB("ghtorrent");
		DBCollection dbcIssues = db.getCollection("issues");
		BasicDBObject queryIssue = new BasicDBObject("number",numberPull); 
		queryIssue.append("repo", repo);
		
		DBObject issue = dbcIssues.findOne(queryIssue);
		
		String closedbyPull="";
		if(((BasicDBObject) issue) != null )
			if( ((BasicDBObject) issue).get("closed_by") != null  )
				closedbyPull = ((BasicDBObject) ((BasicDBObject) issue).get("closed_by")).get("login").toString() ;
		return closedbyPull;
	}
	
	public static int getIssueComments(String idPullRequest, String repo) throws UnknownHostException{
		DB db = Connect.getInstance().getDB("ghtorrent");
		DBCollection dbc = db.getCollection("issue_comments");
		BasicDBObject query = new BasicDBObject("pullreq_id",Integer.parseInt(idPullRequest));
		query.append("repo", repo);
		int comments = dbc.find(query).count();
		return comments;
	}
	
	public static String getPrior_Pull(String requester, String createDate, String firstCreateDate, String repo) throws UnknownHostException{		
		DB db = Connect.getInstance().getDB("ghtorrent");
		DBCollection issues = db.getCollection("issues");
		BasicDBObject query = new BasicDBObject("created_at", new BasicDBObject("$lt",createDate).append("$gte", firstCreateDate)); //consulta com data menor que a data do pull request
		query.append("repo", repo);
		query.append("pull_request", new BasicDBObject("$exists", true));
		query.append("closed_by", new BasicDBObject("$not", new BasicDBObject("$type", 10)));
		query.append("user", new BasicDBObject("$not", new BasicDBObject("$type", 10)));
		query.append("user.login", requester);
		BasicDBObject fields = new BasicDBObject();
		fields.put("number", 1);
		fields.put("closed_by.login", 1);
		fields.put("user.login", 1);
		fields.put("_id", 0);
		ArrayList<String> listCoreTeam = Commits.getCoreTeamList(repo);
		ArrayList<String> list = new ArrayList<String>();
		for (Object memberCoreTeam : listCoreTeam) {
			query.append("closed_by.login", memberCoreTeam.toString());
//			list.add(memberCoreTeam.toString()+":"+issues.find(query, fields).count());
			int count = issues.find(query, fields).count();
			if(count == 0)
				list.add("0");
			else
				list.add(""+count);
		}
		return list.toString();
	}

	//recent recent pulls
	public static String getRecentPulls(String repo, String createDate, String firstCreateDate, Integer days) throws UnknownHostException{
		DB db = Connect.getInstance().getDB("ghtorrent");
		DBCollection issues = db.getCollection("issues");
		String data = FormatDate.dataLimit(createDate, days);
		BasicDBObject query = new BasicDBObject("repo", repo);
		query.append("pull_request", new BasicDBObject("$exists", true));
		query.append("closed_by", new BasicDBObject("$not", new BasicDBObject("$type", 10)));
		query.append("user", new BasicDBObject("$not", new BasicDBObject("$type", 10)));
		if(data.compareTo(firstCreateDate)<=1)
			query.append("created_at", new BasicDBObject("$lt",createDate).append("$gte",firstCreateDate));
		else
			query.append("created_at", new BasicDBObject("$lt",createDate).append("$gte",data));
		BasicDBObject fields = new BasicDBObject();
		fields.put("number", 1);
		fields.put("closed_by.login", 1);
		fields.put("user.login", 1);
		fields.put("_id", 0);
		ArrayList<String> listCoreTeam = Commits.getCoreTeamList(repo);
		ArrayList<String> list = new ArrayList<String>();
		for (Object memberCoreTeam : listCoreTeam) {
			query.append("user.login", new BasicDBObject("$ne",memberCoreTeam.toString()));
			query.append("closed_by.login", memberCoreTeam.toString());
//			list.add(memberCoreTeam.toString()+":"+issues.find(query, fields).count());
			int count = issues.find(query, fields).count();
			if(count == 0)
				list.add("0");
			else
				list.add(""+count);
		}
		return list.toString();
	}
	
	public static String getEvaluatePulls(String repo, String createDate, String firstCreateDate) throws UnknownHostException{
		DB db = Connect.getInstance().getDB("ghtorrent");
		DBCollection issues = db.getCollection("issues");
		BasicDBObject query = new BasicDBObject("created_at", new BasicDBObject("$lt",createDate).append("$gte", firstCreateDate));
		query.append("repo", repo);
		query.append("pull_request", new BasicDBObject("$exists", true));
		query.append("closed_by", new BasicDBObject("$not", new BasicDBObject("$type", 10)));
		query.append("user", new BasicDBObject("$not", new BasicDBObject("$type", 10)));
		BasicDBObject fields = new BasicDBObject();
		fields.put("number", 1);
		fields.put("closed_by.login", 1);
		fields.put("user.login", 1);
		fields.put("_id", 0);
		ArrayList<String> listCoreTeam = Commits.getCoreTeamList(repo);
		ArrayList<String> list = new ArrayList<String>();
		for (Object memberCoreTeam : listCoreTeam) {
			query.append("closed_by.login", memberCoreTeam.toString());
			query.append("user.login", new BasicDBObject("$ne",memberCoreTeam.toString()));
//			list.add(memberCoreTeam.toString()+":"+issues.find(query, fields).count());
			int count = issues.find(query, fields).count();
			if(count == 0)
				list.add("0");
			else
				list.add(""+count);
		}
		return list.toString();
	}
	
	public static String getRecentEvaluatePulls(String requester, String repo, String createDate, String firstCreateDate, Integer days) throws UnknownHostException{
		DB db = Connect.getInstance().getDB("ghtorrent");
		DBCollection issues = db.getCollection("issues");
		String data = FormatDate.dataLimit(createDate, days);
		BasicDBObject query = new BasicDBObject("repo", repo); 
		if(data.compareTo(firstCreateDate)<=1)
			query.append("created_at", new BasicDBObject("$lt",createDate).append("$gte",firstCreateDate));
		else
			query.append("created_at", new BasicDBObject("$lt",createDate).append("$gte",data));
		query.append("pull_request", new BasicDBObject("$exists", true));
		query.append("closed_by", new BasicDBObject("$not", new BasicDBObject("$type", 10)));
		query.append("user", new BasicDBObject("$not", new BasicDBObject("$type", 10)));
		query.append("user.login", requester);
		BasicDBObject fields = new BasicDBObject();
		fields.put("number", 1);
		fields.put("closed_by.login", 1);
		fields.put("user.login", 1);
		fields.put("_id", 0);
		ArrayList<String> listCoreTeam = Commits.getCoreTeamList(repo);
		ArrayList<String> list = new ArrayList<String>();
		for (Object memberCoreTeam : listCoreTeam) {
			query.append("closed_by.login", memberCoreTeam.toString());
			int count = issues.find(query, fields).count();
			if(count == 0)
				list.add("0");
			else
				list.add(""+count);
		}
		return list.toString();
	}
	
	public static String getEvaluateTime(String repo, String createDate, String firstCreateDate, Integer days) throws UnknownHostException{
		DB db = Connect.getInstance().getDB("ghtorrent");
		DBCollection issues = db.getCollection("issues");
		String data = FormatDate.dataLimit(createDate, days);
		BasicDBObject query = new BasicDBObject("repo", repo); 
		if(data.compareTo(firstCreateDate)<=1)
			query.append("created_at", new BasicDBObject("$lt",createDate).append("$gte",firstCreateDate));
		else
			query.append("created_at", new BasicDBObject("$lt",createDate).append("$gte",data));
		query.append("pull_request", new BasicDBObject("$exists", true));
		query.append("closed_by", new BasicDBObject("$not", new BasicDBObject("$type", 10)));
		query.append("user", new BasicDBObject("$not", new BasicDBObject("$type", 10)));
		BasicDBObject fields = new BasicDBObject();
		fields.put("closed_at", 1);
		fields.put("closed_by.login", 1);
		fields.put("created_at", 1);
		fields.put("_id", 0);
		ArrayList<String> listCoreTeam = Commits.getCoreTeamList(repo);
		ArrayList<String> list = new ArrayList<String>();
		for (Object memberCoreTeam : listCoreTeam) {
			double time = 0, count = 0, memberCount = 0;
			query.append("closed_by.login", memberCoreTeam.toString());
			DBCursor cursorIssue = issues.find(query, fields);
			for (DBObject dbObject : cursorIssue) {
				if(dbObject != null && memberCount == 0){
					time += Integer.parseInt(FormatDate.getLifetime(dbObject.get("closed_at").toString(), dbObject.get("created_at").toString()));
					count++;
				}	
			}
			
			if(memberCount != 0)
				time = time/count;
			if(time == 0)
				list.add("");
			else
				list.add(""+String.valueOf(new DecimalFormat("0.##").format(time).replace(',', '.')));
			memberCount++;
		}
		return list.toString();
	}
	
	
	public static String getLatestTime(String repo, String createDate) throws UnknownHostException{
		DB db = Connect.getInstance().getDB("ghtorrent");
		DBCollection issues = db.getCollection("issues");
		BasicDBObject query = new BasicDBObject("closed_at", new BasicDBObject("$lte",createDate).append("$not", new BasicDBObject("$type", 10)));
		query.append("repo", repo);
		query.append("pull_request", new BasicDBObject("$exists", true));
		query.append("closed_by", new BasicDBObject("$not", new BasicDBObject("$type", 10)));
		BasicDBObject fields = new BasicDBObject();
		fields.put("closed_at", 1);
		fields.put("closed_by.login", 1);
		fields.put("_id", 0);
		ArrayList<String> listCoreTeam = Commits.getCoreTeamList(repo);
		ArrayList<String> list = new ArrayList<String>();
		for (Object memberCoreTeam : listCoreTeam) {
			double time = 0;
			query.append("closed_by.login", memberCoreTeam.toString());
			BasicDBObject dbo = new BasicDBObject("closed_at",-1);
			DBObject issue = issues.findOne(query, fields, dbo);//.sort(new BasicDBObject("closed_at",-1)).limit(1);
			if(issue != null)
				time = Integer.parseInt(FormatDate.getLifetime(createDate, issue.get("closed_at").toString()));
			if(time == 0)
				list.add("");
			else
				list.add(""+String.valueOf(new DecimalFormat("0.##").format(time).replace(',', '.')));
		}
		return list.toString();
	}
	
	public static String getFirstTime(String repo, String createDate) throws UnknownHostException{
		DB db = Connect.getInstance().getDB("ghtorrent");
		DBCollection issues = db.getCollection("issues");
		BasicDBObject query = new BasicDBObject("closed_at", new BasicDBObject("$lte",createDate).append("$not", new BasicDBObject("$type", 10)));
		query.append("repo", repo);
		query.append("pull_request", new BasicDBObject("$exists", true));
		query.append("closed_by", new BasicDBObject("$not", new BasicDBObject("$type", 10)));
		BasicDBObject fields = new BasicDBObject();
		fields.put("closed_at", 1);
		fields.put("closed_by.login", 1);
		fields.put("_id", 0);
		ArrayList<String> listCoreTeam = Commits.getCoreTeamList(repo);
		ArrayList<String> list = new ArrayList<String>();
		for (Object memberCoreTeam : listCoreTeam) {
			double time = 0;
			query.append("closed_by.login", memberCoreTeam.toString());
			BasicDBObject dbo = new BasicDBObject("closed_at",1);
			DBObject issue = issues.findOne(query, fields, dbo);//.sort(new BasicDBObject("closed_at",-1)).limit(1);
			if(issue != null)
					time = Integer.parseInt(FormatDate.getLifetime(createDate, issue.get("closed_at").toString()));
			if(time == 0)
				list.add("");
			else
				list.add(""+String.valueOf(new DecimalFormat("0.##").format(time).replace(',', '.')));
		}
		return list.toString();
	}
	
	public static int getTotalPull(String repo, String createDate, String firstCreateDate) throws UnknownHostException{		
		DB db = Connect.getInstance().getDB("ghtorrent");
		DBCollection issues = db.getCollection("issues");
		BasicDBObject query = new BasicDBObject("created_at", new BasicDBObject("$lt",createDate).append("$gte", firstCreateDate)); //consulta com data menor que a data do pull request
		query.append("repo", repo);
		query.append("pull_request", new BasicDBObject("$exists", true));
		query.append("closed_by", new BasicDBObject("$not", new BasicDBObject("$type", 10)));
		query.append("user", new BasicDBObject("$not", new BasicDBObject("$type", 10)));
		BasicDBObject fields = new BasicDBObject();
		fields.put("number", 1);
		fields.put("_id", 0);
		return issues.find(query).count();
	}
	
}
