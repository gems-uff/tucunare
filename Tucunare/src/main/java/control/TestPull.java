package control;

import java.net.UnknownHostException;

import util.Connect;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

public class TestPull {
	public static void main(String[] args) throws UnknownHostException {
		DB db = Connect.getInstance().getDB("ghtorrent");
		DBCollection dbc = db.getCollection("pull_requests");
		BasicDBObject query = new BasicDBObject("repo","noworkflow"); //consulta com query
		DBCursor cursor = dbc.find(query);
		System.out.println(cursor.count());
		//	Consulta toda a collection
		int countCreateDate = 1;
		//String firstCreateDate = "";
		for (DBObject dbObject : cursor) {
			//alocação
			String rep = dbObject.get("repo").toString();
			String owner = dbObject.get("owner").toString();
			String user = ((BasicDBObject)dbObject.get("user")).get("login").toString();
			//String number = dbObject.get("number").toString();
			String closed_by = Issues.getClosedbyPull((Integer) dbObject.get("number"), rep, owner);
			if(!closed_by.equals(""))
				if(dbObject!=null && !closed_by.equals(user)){
					//String created_at = dbObject.get("created_at").toString();
					if(countCreateDate == 1)
						//firstCreateDate = created_at;
						countCreateDate=2;
					//				String priorEvalution = Issues.getPrior_Pull(user, created_at, firstCreateDate, rep);
					//				priorEvalution = priorEvalution.substring(1, priorEvalution.length()-1).replaceAll(", ", "|");
					//				
					//				String recent_pull = Issues.getRecentPulls(rep, created_at, firstCreateDate, 30);
					//				recent_pull = recent_pull.substring(1, recent_pull.length()-1).replaceAll(", ", "|");
					//				
					//				String evaluation_pull = Issues.getEvaluatePulls(rep, created_at, firstCreateDate);
					//				evaluation_pull = evaluation_pull.substring(1, evaluation_pull.length()-1).replaceAll(", ", "|");
					//				
					//				String recent_evaluation = Issues.getRecentEvaluatePulls(user, rep, created_at, firstCreateDate, 30);
					//					recent_evaluation = recent_evaluation.substring(1, recent_evaluation.length()-1).replaceAll(", ", "|");
					//				
					//				String evaluate_time = Issues.getEvaluateTime(rep, created_at, firstCreateDate, 30);
					//				evaluate_time = evaluate_time.substring(1, evaluate_time.length()-1).replaceAll(", ", "|");
					//				
					//				String latest_time = Issues.getLatestTime(rep, created_at);
					//				latest_time = latest_time.substring(1, latest_time.length()-1).replaceAll(", ", "|");

					//				String first_time = Issues.getFirstTime(rep, created_at);
					//				first_time = first_time.substring(1, first_time.length()-1).replaceAll(", ", "|");
					//				String type2 = Commits.getTypeDeveloper(user, rep, owner);
					String created = dbObject.get("created_at").toString();
					String type3 = Commits.getTypeDeveloper3(user, rep, owner, created);
					System.out.println(
							//					dbObject.get("id")+", "+
							dbObject.get("number")+", "+//+commitsPorArquivos+", "+

//					dbObject.get("owner")+"/"+dbObject.get("repo")+", "+
//comments+", commits:"+
//					dbObject.get("commits")+", "+
user+"= "+
closed_by+", "+
//filesPath+", "+
//					dbObject.get("additions")+", "+
//					dbObject.get("deletions")//+", "+
//type2+", "+
type3

							);
				}
		}
		Connect.getInstance().close();
	}



}
