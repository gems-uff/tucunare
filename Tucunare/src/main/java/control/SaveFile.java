package control;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Map;

import teste.DialogStatus;
import util.Connect;
import util.FormatDate;
import view.RetrievePullRequest;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

public class SaveFile implements Runnable {
	private String repo = ""; 
	private static String file;
	private List<String> selectedFields; 
	private Map<String, Integer> txtFieldsDays;
	public static int finalizedThreads = 0;
	private static String result="";

	@SuppressWarnings("static-access")
	public SaveFile(String repo, String file,
			List<String> selectedFields, Map<String, Integer> txtFieldsDays) throws UnknownHostException{
		this.repo = repo; 
		this.file = file;
		this.selectedFields = selectedFields;
		this.txtFieldsDays  = txtFieldsDays;
	}

	public void run() {
		System.out.println("Processando dados do Pull Request "+ repo+"\nThread utilizada: "+ Thread.currentThread().getId());
		try {
			System.out.println("Resultado da recuperação do repositório "+repo+"\n"+retrieveData(repo, selectedFields, txtFieldsDays));
			if (finalizedThreads==RetrievePullRequest.total){
				System.out.println(saveFile());
			}
			DialogStatus.setjLabel(finalizedThreads);
		} catch (UnknownHostException e) {
			System.err.println("Erro ao processar os dados do repositórios "+repo);
			e.printStackTrace();
		}
	}

	//Usar threads para recuperar os dados e salvar tudo o que foi armazenado na String em uma única escrita de arquivo.
	public String retrieveData(String repo, List<String> selectedFields, 
			Map<String, Integer> txtFieldsDays) throws UnknownHostException { 
		DB db = Connect.getInstance().getDB("ghtorrent2");
		DBCollection dbcPullRequest = db.getCollection("pull_requests");
		BasicDBObject query = new BasicDBObject("repo",repo); //consulta com query
		try{
			DBCursor cursor = dbcPullRequest.find(query);
			if (cursor==null){
				Connect.getInstance().close();
				finalizedThreads++;
				return "couldn't find the repository "+repo;
			}
			cursor.addOption(com.mongodb.Bytes.QUERYOPTION_NOTIMEOUT);
			//Estimar o tempo para terminar a consulta
			System.out.println(cursor.count());
			//Variável que guarda o conteúdo que está sendo concatenado, seu valor será repassado para a variável static result; 
			String resultTemp="";
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
				resultTemp +=  selectedFields.contains("owner")? (owner+"/"):"";				
				resultTemp += selectedFields.contains("repository")? (rep+", "):",";
				resultTemp += selectedFields.contains("others")? (dataRepo+", "):""; //dados gerais do repositório
				resultTemp += selectedFields.contains("contributors")? (contributors+", "):"";
				resultTemp += selectedFields.contains("acceptance average")? (acceptanceUser+", "):"";
				resultTemp += selectedFields.contains("followers")? (followers+", "):"";
				resultTemp += selectedFields.contains("following")? (following+", "):"";
				resultTemp += selectedFields.contains("age user")? (ageUser+", "):"";
				resultTemp += selectedFields.contains("type")? (typeDeveloper+", "):"";
				resultTemp += selectedFields.contains("total PR by user")? (totalPullUser+", "):"";
				resultTemp += selectedFields.contains("acceptance average")? (acceptanceUser+", "):"";
				resultTemp += selectedFields.contains("watch repo")? (watchRepo+", "):"";
				resultTemp += selectedFields.contains("follow contributors")? followContributors+", ":"";
				resultTemp += selectedFields.contains("location")? location+", ":"";
				resultTemp += selectedFields.contains("id")? ((Integer) dbObject.get("id")+", "):"";
				resultTemp += selectedFields.contains("number")? ((Integer) dbObject.get("number")+", "):"";
				resultTemp += selectedFields.contains("author")? (((BasicDBObject)dbObject.get("user")).get("login")+"; "):"";
				resultTemp += selectedFields.contains("state")? (dbObject.get("state")+", "):"";
				resultTemp += selectedFields.contains("title")? (dbObject.get("title").toString().replace('\n', ' ').replace(';', ' ')+", "):"";
				resultTemp += selectedFields.contains("created at")? (FormatDate.getDate(dbObject.get("created_at").toString())+", "):"";
				resultTemp += selectedFields.contains("closed at")? (closedDate+", "):"";
				resultTemp += selectedFields.contains("merged at")? mergedDate+", ":"";
				resultTemp += selectedFields.contains("lifetime")? (lifetime+", "):"";
				resultTemp += selectedFields.contains("closed by")? (closed_by+", "):"";
				resultTemp += selectedFields.contains("merged by")? merged_by+", ":"";
				if (selectedFields.contains("sha (head e base)")){
					resultTemp += ((BasicDBObject)dbObject.get("head")).get("sha")+", ";
					resultTemp += ((BasicDBObject)dbObject.get("base")).get("sha")+", ";
				}
				resultTemp += selectedFields.contains("assignee")? (assignee+", "):"";
				resultTemp += selectedFields.contains("comments")? (comments+", "):"";
				resultTemp += selectedFields.contains("commits")? (dbObject.get("commits")+", "):"";
				resultTemp += selectedFields.contains("commits by files")? (commitsPorArquivos+", "):"";
				resultTemp += selectedFields.contains("author more commits")? (authorMoreCommits+", "):"";
				resultTemp += selectedFields.contains("lines added")? (dbObject.get("additions")+", "):"";
				resultTemp += selectedFields.contains("deleted lines")? (dbObject.get("deletions")+"; "):";";

				if (selectedFields.contains("lines added") && selectedFields.contains("deleted lines"))
					resultTemp += Integer.parseInt(dbObject.get("additions").toString())+Integer.parseInt(dbObject.get("deletions").toString())+", ";

				resultTemp += selectedFields.contains("changed files")? (dbObject.get("changed_files")+", "):"";		
				resultTemp += selectedFields.contains("files")? (files):"";
				resultTemp += "\r\n";
			}

			result += resultTemp;
			//Esse método fechava as conexões das outras threads.
			//Connect.getInstance().close();

			finalizedThreads++;
			return "sucess!";
		}catch(IOException ioe){
			finalizedThreads++;
			return "Erro na escrita do arquivo";
		}
	}	

	public static String saveFile(){
		int i = file.lastIndexOf("\\");
		File dir = new File(file.substring(0, i)); 
		File fileTemp = new File(dir, file.substring(i, file.length()));
		FileWriter fw = null;
		try {
			fw = new FileWriter(fileTemp, false);

			fw.write("owner/repo;ageRepoDays;stargazersCount;watchersCount;language;forksCount;openIssuesCount;subscribersCount;has_wiki;contributors;acceptanceRepo;"
					+ "followers;following;ageUser;typeDeveloper;totalPullDeveloper;acceptanceDeveloper;watchRepo;followContributors;location;"
					+ "idPull;numberPull;login;state;title;createdDate;closedDate;mergedDate;lifetimeDays;lifetimeHours;lifetimeMinutes;closedBy;"
					+ "mergedBy;commitHeadSha;commitBaseSha;assignee;comments;commitsPull;commitsbyFilesPull; authorMoreCommits;"
					+ "additionsLines;deletionsLines;totalLines;changedFiles;files");
			fw.write("\r\n");
			fw.write(result);

		} catch (IOException e) {
			e.printStackTrace();
			return "erro na escrita do arquivo.";
		}
		try {
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
			return "erro na escrita do arquivo (2).";
		}
		return "Arquivo escrito com sucesso.";
	}
}
