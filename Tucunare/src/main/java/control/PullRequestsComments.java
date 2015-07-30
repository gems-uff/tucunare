package control;

import java.net.UnknownHostException;

import util.Connect;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;

public class PullRequestsComments {
	public static int getPullComments(String idPullRequest, String repo) throws UnknownHostException{
		DB db = Connect.getInstance().getDB("ghtorrent");
		DBCollection dbc = db.getCollection("pull_request_comments");
		BasicDBObject query = new BasicDBObject("pullreq_id",Integer.parseInt(idPullRequest)); //consulta com query
		query.append("repo", repo);
		int comments = dbc.find(query).count();
		return comments;
	}
}
