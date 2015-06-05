package control;


import util.Connect;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;

public class PullRequestsComments {


		public static int getPullComments(String idPullRequest){
			DB db = new Connect().getDB();
			DBCollection dbc = db.getCollection("pull_request_comments");
			BasicDBObject query = new BasicDBObject("pullreq_id",Integer.parseInt(idPullRequest)); //consulta com query
			int comments = dbc.find(query).count();
			return comments;
				
		}
//	public static void main(String [] args){
//		DB db = new Connect().getDB();
//		
//		DBCollection dbc = db.getCollection("pull_request_comments");
//		BasicDBObject query = new BasicDBObject("pullreq_id",44); //consulta com query
//		DBCursor cursor = dbc.find(query);
////		System.out.println(cursor);
//		System.out.println(cursor.count());
//		for (DBObject dbObject : cursor) {
//			System.out.println(dbObject.get("pullreq_id"));
//		}
////		System.out.println(comments);
////		return comments;
//	}
}
