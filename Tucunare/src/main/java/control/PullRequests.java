package control;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Map;

import util.Connect;
import util.FormatDate;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

public class PullRequests extends Thread{
	public static int getPulls(String repo) throws UnknownHostException{
		DB db = Connect.getInstance().getDB("ghtorrent2");
		DBCollection dbcPullRequest = db.getCollection("pull_requests");
		BasicDBObject query = new BasicDBObject("repo",repo); //consulta com query
		return dbcPullRequest.find(query).count();
	}

	public String saveFile(String repo, String file,
			List<String> selectedFields, Map<String, Integer> txtFieldsDays) throws UnknownHostException {

		DB db = Connect.getInstance().getDB("ghtorrent2");
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
			if (cursor==null){
				fw.close();
				Connect.getInstance().close();
				return "couldn't find the repository "+repo;
			}
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
				String contributors = "";
				if (txtFieldsDays.containsKey("contributorMonths"))
					contributors = Commits.getContributors(shaHead, rep, owner, txtFieldsDays.get("contributorMonths"));
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
					if (txtFieldsDays.containsKey("commByFilesDays"))
						commitsPorArquivos = Commits.getCommitsByFiles(files, created, rep, txtFieldsDays.get("commByFilesDays"));
					
					//Desenvolvedor com mais commits na última semana.
					if (txtFieldsDays.containsKey("authorMoreCommDays"))
						authorMoreCommits = Commits.getAuthorCommits(files, shaBase, rep, txtFieldsDays.get("authorMoreCommDays"));
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

				//Não gostei de ter que fazer isso.
				String result =  selectedFields.contains("owner")? (owner+"/"):"";
				result += selectedFields.contains("repository")? (rep+"; "):"";
				result += selectedFields.contains("others")? (dataRepo+"; "):""; //dados gerais do repositório
				result += selectedFields.contains("contributors")? (contributors+"; "):"";
				result += selectedFields.contains("acceptance average")? (acceptanceUser+"; "):"";
				result += selectedFields.contains("followers")? (followers+"; "):"";
				result += selectedFields.contains("following")? (following+"; "):"";
				result += selectedFields.contains("age user")? (ageUser+"; "):"";
				result += selectedFields.contains("type")? (typeDeveloper+"; "):"";
				result += selectedFields.contains("total PR by user")? (totalPullUser+"; "):"";
				result += selectedFields.contains("acceptance average")? (acceptanceUser+"; "):"";
				result += selectedFields.contains("watch repo")? (watchRepo+"; "):"";
				result += selectedFields.contains("follow contributors")? followContributors+"; ":"";
				result += selectedFields.contains("location")? location+"; ":"";
				result += selectedFields.contains("id")? ((Integer) dbObject.get("id")+"; "):"";
				result += selectedFields.contains("number")? ((Integer) dbObject.get("number")+"; "):"";
				result += selectedFields.contains("author")? (((BasicDBObject)dbObject.get("user")).get("login")+"; "):"";
				result += selectedFields.contains("state")? (dbObject.get("state")+"; "):"";
				result += selectedFields.contains("title")? (dbObject.get("title").toString().replace('\n', ' ').replace(';', ' ')+"; "):"";
				result += selectedFields.contains("created at")? (FormatDate.getDate(dbObject.get("created_at").toString())+"; "):"";
				result += selectedFields.contains("closed at")? (closedDate+"; "):"";
				result += selectedFields.contains("merged at")? mergedDate+"; ":"";
				result += selectedFields.contains("lifetime")? (lifetime+"; "):"";
				result += selectedFields.contains("closed by")? (closed_by+"; "):"";
				result += selectedFields.contains("merged by")? merged_by+"; ":"";
				if (selectedFields.contains("sha (head e base)")){
					result += ((BasicDBObject)dbObject.get("head")).get("sha")+"; ";
					result += ((BasicDBObject)dbObject.get("base")).get("sha")+"; ";
				}
				result += selectedFields.contains("assignee")? (assignee+"; "):"";
				result += selectedFields.contains("comments")? (comments+"; "):"";
				result += selectedFields.contains("commits")? (dbObject.get("commits")+"; "):"";
				result += selectedFields.contains("commits by files")? (commitsPorArquivos+"; "):"";
				result += selectedFields.contains("author more commits")? (authorMoreCommits+"; "):"";
				result += selectedFields.contains("lines added")? (dbObject.get("additions")+"; "):"";
				result += selectedFields.contains("deleted lines")? (dbObject.get("deletions")+"; "):"";
				
				if (selectedFields.contains("lines added") && selectedFields.contains("deleted lines"))
					result += Integer.parseInt(dbObject.get("additions").toString())+Integer.parseInt(dbObject.get("deletions").toString())+"; ";
				
				result += selectedFields.contains("changed files")? (dbObject.get("changed_files")+"; "):"";		
				result += selectedFields.contains("files")? (files+"\n"):"";
				System.out.println("Result: "+result);
				fw.write(result);
			}
			fw.close();
			Connect.getInstance().close();
			return "sucess!";
		}catch(IOException ioe){
			return "Erro na escrita do arquivo";
		}

	}	
}
