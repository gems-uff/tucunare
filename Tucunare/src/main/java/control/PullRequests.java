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
	public void saveFile(String repo, String file) throws UnknownHostException{
		DB db = Connect.getInstance().getDB("ghtorrent");
		DBCollection dbcPullRequest = db.getCollection("pull_requests");
		BasicDBObject query = new BasicDBObject("repo",repo); //consulta com query
		//File dirTemp =  new File(dir);
		int i = file.lastIndexOf("\\");
		File dir = new File(file.substring(0, i)); 
		File fileTemp = new File(dir, file.substring(i, file.length()));
		try{
			FileWriter fw = new FileWriter(fileTemp, false);
			fw.write("owner/repo;ageRepoDays;stargazers_count;watchers_count;language;forks_count;open_issues_count;subscribers_count;"
					+ "followers;following;ageUser;"
					+ "id;number;login;state;title;created_at;closed_at;merged_at;lifetimeDays;lifetimeHours;lifetimeMinutes;closed_by;"
					+ "merged_by;commit_head_sha;commit_base_sha;assignee;comments;commitsPull;commitsbyFilesPull; authorMoreCommits;"
					+ "additions;deletions;changed_files;files\n");
			DBCursor cursor = dbcPullRequest.find(query);
			cursor.addOption(com.mongodb.Bytes.QUERYOPTION_NOTIMEOUT);
			//Estimar o tempo para terminar a consulta
			//System.out.println(cursor.count());
			for (DBObject dbObject : cursor) {
				if(dbObject!=null){
				//Dados do projeto
				String dataRepo = Repos.getRepoData(dbObject.get("owner")+"/"+dbObject.get("repo"));//ageRepo;stargazers_count;watchers_count;language;forks_count;open_issues_count;subscribers_count
				
				//Dados do requester
				String followers = Users.getFollowersUser(((BasicDBObject)dbObject.get("user")).get("login").toString()); 
				String following = Users.getFollowingUser(((BasicDBObject)dbObject.get("user")).get("login").toString());
				String ageUser = Users.getAgeUser(((BasicDBObject)dbObject.get("user")).get("login").toString());
				
				BasicDBObject queryUser = new BasicDBObject("user.login", ((BasicDBObject)dbObject.get("user")).get("login").toString());
				int totalPullUser = dbcPullRequest.find(queryUser).count();
				queryUser.append("merged_at", null);
				int mergedPullUser = dbcPullRequest.find(queryUser).count();
				double acceptanceUser = ((double) mergedPullUser*100)/totalPullUser;

				
				//Dados do Pull Request
				String assignee = "";
				if(dbObject.get("assignee")!=null)
					assignee = (String) ((BasicDBObject)dbObject.get("assignee")).get("login");
				//comentários
				int commentsPull = PullRequestsComments.getPullComments(dbObject.get("number").toString(), dbObject.get("repo").toString()); 
				int commentsIssue = Issues.getIssueComments(dbObject.get("number").toString(), dbObject.get("repo").toString());
				long comments = commentsPull + commentsIssue;
				//arquivos
				String filesPath = Commits.getCommitsFilesPath(((BasicDBObject)dbObject.get("head")).get("sha").toString(), ((BasicDBObject)dbObject.get("base")).get("sha").toString(), Integer.parseInt(dbObject.get("commits").toString()));
				String files="", commitsPorArquivos="", authorMoreCommits="";
				if(!filesPath.equals("")){
					files = filesPath.substring(1, filesPath.length()-1);
					//commitsNosArquivos na última semana.
					commitsPorArquivos = Commits.getCommitsByFiles(files, dbObject.get("created_at").toString(), dbObject.get("repo").toString());
					//Desenvolvedor com mais commits na última semana.
					authorMoreCommits = Commits.getAuthorCommits(files, ((BasicDBObject)dbObject.get("base")).get("sha").toString(), dbObject.get("repo").toString());
				}
					
				System.out.println((Integer) dbObject.get("number"));
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
				//Escrevendo no arquivo
				fw.write(dbObject.get("owner")+"/"+dbObject.get("repo")+"; "+
						dataRepo+"; "+
						followers+"; "+
						following+"; "+
						ageUser+"; "+
						totalPullUser+"; "+
						acceptanceUser+"; "+
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
				}else
					fw.write("\n");
			}
			fw.close();
			Connect.getInstance().close();
		}catch(IOException ioe){
			System.err.println("Erro na escrita do arquivo!");
		}
	}	
}
