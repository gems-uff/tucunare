package control;

import java.net.UnknownHostException;

import util.Connect;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;

public class PullRequestsComments {
	//	Quantidade de comentários emum pull request
	public static int getPullComments(String idPullRequest, String repo, String owner) throws UnknownHostException{
		DB db = Connect.getInstance().getDB("ghtorrent");
		DBCollection dbc = db.getCollection("pull_request_comments");
		BasicDBObject query = new BasicDBObject("pullreq_id",Integer.parseInt(idPullRequest)); //consulta com query
		query.append("repo", repo);
		query.append("owner", owner);
		BasicDBObject fields = new BasicDBObject();
		fields.put("pullreq_id", 1);
		fields.put("_id", 0);
		int comments = dbc.find(query,fields).count();
		return comments;
	}
}