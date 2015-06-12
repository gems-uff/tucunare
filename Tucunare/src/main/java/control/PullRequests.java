package control;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.UnknownHostException;

import util.Connect;
import util.FormatDate;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

public class PullRequests {
/*	public static void main(String[] args) throws UnknownHostException {
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
			String commitsPorArquivos = Commits.getCommitsByFiles(files, dbObject.get("created_at").toString(), dbObject.get("repo").toString());
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
*/
	public void saveFile(String repo) throws UnknownHostException{
		DB db = Connect.getInstance().getDB("ghtorrent");
		DBCollection dbc = db.getCollection("pull_requests");
		BasicDBObject query = new BasicDBObject("repo",repo); //consulta com query
		File dir =  new File("D:\\files");
		File file = new File(dir, repo+"7.csv");
		try{
			FileWriter fw = new FileWriter(file, false);
			fw.write("owner/repo;ageRepoDays;stargazers_count;watchers_count;language;forks_count;open_issues_count;subscribers_count;"
					+ "id;number;login;state;title;created_at;closed_at;merged_at;lifetimeDays;lifetimeHours;lifetimeMinutes;closed_by;merged_by;commit_head_sha;"
					+ "commit_base_sha;assignee;comments;commitsPull;commitsbyFilesPull; authorMoreCommits;"
					+ "additions;deletions;changed_files;files\n");
			DBCursor cursor = dbc.find(query);
			//Estimar o tempo para terminar a consulta
			System.out.println(cursor.count());
			for (DBObject dbObject : cursor) {
				//Dados do projeto
				String dataRepo = Repos.getRepoData(dbObject.get("owner")+"/"+dbObject.get("repo"));//ageRepo;stargazers_count;watchers_count;language;forks_count;open_issues_count;subscribers_count
				
				//Dados do Pull Request
				String assignee = "";
				if(dbObject.get("assignee")==null)
					assignee = "null";
				else
					assignee = (String) ((BasicDBObject)dbObject.get("assignee")).get("login");
				//comentários
				long comments = PullRequestsComments.getPullComments(dbObject.get("number").toString());
				//arquivos
				String filesPath = Commits.getCommitsFilesPath(((BasicDBObject)dbObject.get("head")).get("sha").toString(), ((BasicDBObject)dbObject.get("base")).get("sha").toString(), Integer.parseInt(dbObject.get("commits").toString()));
				String files = filesPath.substring(1, filesPath.length()-1);
				//commitsNosArquivos na última semana.
				String commitsPorArquivos = Commits.getCommitsByFiles(files, dbObject.get("created_at").toString(), dbObject.get("repo").toString());
				//Desenvolvedor com mais commits na última semana.
				String authorMoreCommits = Commits.getAuthorCommits(files, ((BasicDBObject)dbObject.get("base")).get("sha").toString(), dbObject.get("repo").toString());
				System.out.println((Integer) dbObject.get("number")+", "+authorMoreCommits);
				//Tempo de vida de um pull request
				String lifetime = "",closed_by = "", merged_by = "";
				if((dbObject.get("closed_at")!=null)){
					lifetime = FormatDate.getLifetime(dbObject.get("closed_at").toString(), dbObject.get("created_at").toString());
					closed_by = Issues.getClosedbyPull((Integer) dbObject.get("number"), dbObject.get("repo").toString());
				}else{
					lifetime = ";;"; 
				}
				//Desenvolvedor que integrou ou rejeitou o código do pull request
				if((dbObject.get("merged_by"))!=null)
					merged_by = ((BasicDBObject)dbObject.get("merged_by")).get("login").toString();
				
				fw.write(dbObject.get("owner")+"/"+dbObject.get("repo")+"; "+
						dataRepo+"; "+
						(Integer) dbObject.get("id")+"; "+
						(Integer) dbObject.get("number")+"; "+
						((BasicDBObject)dbObject.get("user")).get("login")+"; "+
						dbObject.get("state")+"; "+
						dbObject.get("title").toString().replace('\n', ' ').replace(';', ' ')+"; "+
						dbObject.get("created_at")+"; "+
						dbObject.get("closed_at")+"; "+
						dbObject.get("merged_at")+"; "+
						lifetime+"; "+
						closed_by+"; "+
						merged_by+"; "+
						((BasicDBObject)dbObject.get("head")).get("sha")+"; "+
						((BasicDBObject)dbObject.get("base")).get("sha")+"; "+
						assignee+"; "+
						comments+"; "+
						dbObject.get("commits")+"; "+
						commitsPorArquivos+"; "+
						authorMoreCommits+"; "+
						dbObject.get("additions")+"; "+
						dbObject.get("deletions")+"; "+
						dbObject.get("changed_files")+"; "+
						files+"\n");
			}
			fw.close();
			Connect.getInstance().close();
		}catch(IOException ioe){
			System.err.println("Erro na escrita do arquivo!");
		}
	}	
}
