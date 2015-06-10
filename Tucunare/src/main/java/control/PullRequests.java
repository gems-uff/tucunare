package control;

import java.net.UnknownHostException;

import util.Connect;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

public class PullRequests {
	public static void main(String[] args) throws UnknownHostException {
		DB db = Connect.getInstance().getDB("ghtorrent");
		DBCollection dbc = db.getCollection("pull_requests");
		BasicDBObject query = new BasicDBObject("repo","angular"); //consulta com query
		DBCursor cursor = dbc.find(query);
		System.out.println(cursor.count());
//		Consulta toda a collection
		for (DBObject dbObject : cursor) {
			
			//alocação
			String assignee = "";
			if(dbObject.get("assignee")==null)
				assignee = "null";
			else
				assignee = (String) ((BasicDBObject)dbObject.get("assignee")).get("login");
			//comentários
			long comments = PullRequestsComments.getPullComments(dbObject.get("number").toString());
			//commits
			//long commits = Commits.getCommits(((BasicDBObject)dbObject.get("head")).get("sha").toString(), ((BasicDBObject)dbObject.get("base")).get("sha").toString());
			//estatisticas
			//String statistics = Commits.getCommitStats(((BasicDBObject)dbObject.get("head")).get("sha").toString(), ((BasicDBObject)dbObject.get("base")).get("sha").toString());
			//arquivos
			//String files = Commits.getCommitsFiles(((BasicDBObject)dbObject.get("head")).get("sha").toString(), ((BasicDBObject)dbObject.get("base")).get("sha").toString());
			//arquivos
			String filesPath = Commits.getCommitsFilesPath(((BasicDBObject)dbObject.get("head")).get("sha").toString(), ((BasicDBObject)dbObject.get("base")).get("sha").toString(), Integer.parseInt(dbObject.get("commits").toString()));
			//commitsNosArquivos nos últimos 3 meses.
			String files = filesPath.substring(1, filesPath.length()-1);
			String commitsPorArquivos = Commits.getCommitsByFiles(files, dbObject.get("created_at").toString(), ((BasicDBObject)dbObject.get("user")).getString("login").toString());
			System.out.println(dbObject.get("id")+", "+
					dbObject.get("number")+", "+commitsPorArquivos+", "+
					((BasicDBObject)dbObject.get("user")).get("login")+", "+
					dbObject.get("state")+", "+
					dbObject.get("title")+", "+
					dbObject.get("created_at")+", "+
					dbObject.get("update_at")+", "+
					dbObject.get("closed_at")+", "+
					dbObject.get("merged_at")+", "+
					dbObject.get("merge_commit_sha")+", "+
					((BasicDBObject)dbObject.get("head")).get("sha")+", "+
					((BasicDBObject)dbObject.get("base")).get("sha")+", "+
					assignee+", "+
					dbObject.get("owner")+"/"+dbObject.get("repo")+", "+
					comments+", commits:"+
					dbObject.get("commits")+", "+
					dbObject.get("changed_files")+"= "+
					files+", "+
					dbObject.get("additions")+", "+
					dbObject.get("deletions")//+", "+
					//files//+", "+
					//commitsPorArquivos
					
			);
				
		}
		Connect.getInstance().close();
	}

}
