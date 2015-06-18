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

public class PullRequests extends Thread{
	public static int getPulls(String repo) throws UnknownHostException{
		DB db = Connect.getInstance().getDB("ghtorrent");
		DBCollection dbcPullRequest = db.getCollection("pull_requests");
		BasicDBObject query = new BasicDBObject("repo",repo); //consulta com query
		return dbcPullRequest.find(query).count();
	}
	
	public void saveFile(String repo, String file) throws UnknownHostException{
		DB db = Connect.getInstance().getDB("ghtorrent");
		DBCollection dbcPullRequest = db.getCollection("pull_requests");
		BasicDBObject query = new BasicDBObject("repo",repo); //consulta com query
		int i = file.lastIndexOf("\\");
		File dir = new File(file.substring(0, i)); 
		File fileTemp = new File(dir, file.substring(i, file.length()));
		try{
			FileWriter fw = new FileWriter(fileTemp, false);
			fw.write("owner/repo;ageRepoDays;stargazersCount;watchersCount;language;forksCount;openIssuesCount;subscribersCount;has_wiki;contributors;acceptanceRepo;"
					+ "followers;following;ageUser;typeDeveloper;totalPullDeveloper;acceptanceDeveloper;watchRepo;followContributors;location;"
					+ "idPull;numberPull;login;state;title;createdDate;closedDate;mergedDate;lifetimeDays;lifetimeHours;lifetimeMinutes;closedBy;"
					+ "mergedBy;commitHeadSha;commitBaseSha;assignee;comments;commitsPull;commitsbyFilesPull; authorMoreCommits;"
					+ "additionsLines;deletionsLines;totalLines;changedFiles;files\n");
			DBCursor cursor = dbcPullRequest.find(query);
			cursor.addOption(com.mongodb.Bytes.QUERYOPTION_NOTIMEOUT);
			//Estimar o tempo para terminar a consulta
			//System.out.println(cursor.count());
			for (DBObject dbObject : cursor) {
				//Variváveis
				String owner = dbObject.get("owner").toString();
				String rep = dbObject.get("repo").toString();
				String user = ((BasicDBObject)dbObject.get("user")).get("login").toString();
				String created = dbObject.get("created_at").toString();
				String followers = Users.getFollowersUser(user);
				String following = Users.getFollowingUser(user);
				String ageUser = Users.getAgeUser(user);
				String shaHead = ((BasicDBObject)dbObject.get("head")).get("sha").toString();
				String shaBase = ((BasicDBObject)dbObject.get("base")).get("sha").toString();
				String number = dbObject.get("number").toString();
				
				if(dbObject!=null){
				//Dados do projeto
				String dataRepo = Repos.getRepoData(owner+"/"+rep);//ageRepo;stargazers_count;watchers_count;language;forks_count;open_issues_count;subscribers_count;has_wiki
				String acceptanceRepo="";
				int totalPullRepoClosed = Repos.getPullRepoClosed(created, rep, owner);
				int mergedPullRepo = Repos.getPullRepoMerged(created, rep, owner);
				if(totalPullRepoClosed==0)
					acceptanceRepo = "FirstPull";
				else
					acceptanceRepo = String.valueOf(((mergedPullRepo*100)/totalPullRepoClosed));
				
				//Dados do requester
				int totalPullUser = Users.getPullUserTotal(user, created, rep, owner);
				int mergedPullUser = Users.getPullUserMerged(user, created, rep, owner);
				String acceptanceUser="";
				if(totalPullUser==0)
					acceptanceUser = "FirstPull";
				else
					acceptanceUser = String.valueOf(((mergedPullUser*100)/totalPullUser));
				String contributors = Commits.getContributors(shaHead, rep, owner);
				String typeDeveloper = Commits.getTypeDeveloper(user, rep, owner);
				
				boolean watchRepo = Users.getWatcherRepo (user, created, rep, owner);
				boolean followContributors = Users.getFollowersTeam(user, rep, owner);
				String location = Users.getLocationUser(user);
				
				//Dados do Pull Request
				String assignee = "";
				if(dbObject.get("assignee")!=null)
					assignee = (String) ((BasicDBObject)dbObject.get("assignee")).get("login");
				
				//comentários
				int commentsPull = PullRequestsComments.getPullComments(number, rep); 
				int commentsIssue = Issues.getIssueComments(number, dbObject.get("repo").toString());
				long comments = commentsPull + commentsIssue;
				
				//arquivos
				String filesPath = Commits.getCommitsFilesPath(shaHead, shaBase, Integer.parseInt(dbObject.get("commits").toString()));
				String files="", commitsPorArquivos="", authorMoreCommits="";
				if(!filesPath.equals("")){
					files = filesPath.substring(1, filesPath.length()-1);
					//commitsNosArquivos na última semana.
					commitsPorArquivos = Commits.getCommitsByFiles(files, created, rep);
					//Desenvolvedor com mais commits na última semana.
					authorMoreCommits = Commits.getAuthorCommits(files, shaBase, rep);
				}
					
				System.out.println((Integer) dbObject.get("number"));
				//Tempo de vida de um pull request
				String lifetime = "",closed_by = "", merged_by = "";
				if((dbObject.get("closed_at")!=null)){
					lifetime = FormatDate.getLifetime(dbObject.get("closed_at").toString(), created);
					closed_by = Issues.getClosedbyPull((Integer) dbObject.get("number"), rep);
				}else{
					lifetime = ";;"; 
				}
				//Desenvolvedor que integrou ou rejeitou o código do pull request
				if((dbObject.get("merged_by"))!=null)
					merged_by = ((BasicDBObject)dbObject.get("merged_by")).get("login").toString();
				//Datas
				String closedDate = "";
				if(dbObject.get("closed_at")!=null)
					closedDate = FormatDate.getDate(dbObject.get("closed_at").toString());
				String mergedDate = "";
				if(dbObject.get("merged_at")!=null)
					mergedDate = FormatDate.getDate(dbObject.get("merged_at").toString());
				
				
				//Escrevendo no arquivo
				fw.write(owner+"/"+rep+"; "+
						dataRepo+"; "+
						contributors+"; "+
						acceptanceRepo+"; "+
						followers+"; "+
						following+"; "+
						ageUser+"; "+
						typeDeveloper+"; "+
						totalPullUser+"; "+
						acceptanceUser+"; "+
						watchRepo+"; "+
						followContributors+"; "+
						location+"; "+
						(Integer) dbObject.get("id")+"; "+
						(Integer) dbObject.get("number")+"; "+
						((BasicDBObject)dbObject.get("user")).get("login")+"; "+
						dbObject.get("state")+"; "+
						dbObject.get("title").toString().replace('\n', ' ').replace(';', ' ')+"; "+
						FormatDate.getDate(dbObject.get("created_at").toString())+"; "+
						closedDate+"; "+
						mergedDate+"; "+
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
						(Integer.parseInt(dbObject.get("additions").toString())+Integer.parseInt(dbObject.get("deletions").toString()))+"; "+
						dbObject.get("changed_files")+"; "+
						files+"\n");
				//System.out.println(contributors);
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
