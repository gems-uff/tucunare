package control;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.UnknownHostException;

import model.Settings;
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
	private String file;
	public static int finalizedThreads = 0;
	public static String tempo="";
	private Settings settings;

	public SaveFile(String repo, String file, Settings settings) throws UnknownHostException{
		this.repo = repo; 
		this.file = file;
		this.settings = settings;
	}

	public void run() {
		System.out.println("Processando dados do Pull Request "+ repo+"\nThread utilizada: "+ Thread.currentThread().getId());
		long tempoInicial = System.currentTimeMillis(); 
		try {
			saveFile(true, null);
			retrieveData(repo, settings);
			tempo += Thread.currentThread().getName()+": "+((System.currentTimeMillis() - tempoInicial)/1000)+" : ";
			System.out.println("HERE");
			DialogStatus.setThreads(finalizedThreads);
		} catch (UnknownHostException e) {
			System.err.println("Erro ao processar os dados do repositórios "+repo);
			e.printStackTrace();
		}
	}

	//Usar threads para recuperar os dados e salvar tudo o que foi armazenado na String em uma única escrita de arquivo.
	public String retrieveData(String repo, Settings settings) throws UnknownHostException {
		DB db = Connect.getInstance().getDB("ghtorrent");
		DBCollection dbcPullRequest = db.getCollection("pull_requests");
 
		BasicDBObject query = new BasicDBObject("repo",repo); //consulta com query
		//query.append("number", new BasicDBObject("$gt", Integer.parseInt("1243")));//maiores que number
		if (settings.getPrType() == 1)
			query.append("state", "open"); //Apenas pull requests encerrados
		if (settings.getPrType() == 2)
			query.append("state", "closed"); //Apenas pull requests encerrados
		
		try{
			DBCursor cursor = dbcPullRequest.find(query);//.sort(new BasicDBObject("number", -1));
			cursor.addOption(com.mongodb.Bytes.QUERYOPTION_NOTIMEOUT);
			for (DBObject dbObject : cursor) {
				//Variváveis
				String rep = dbObject.get("repo").toString();
				String user = ((BasicDBObject)dbObject.get("user")).get("login").toString();
				String number = dbObject.get("number").toString();
				String closed_by = Issues.getClosedbyPull((Integer) dbObject.get("number"), rep);
				
				if(dbObject!=null & !closed_by.equals(user)){
					String owner = dbObject.get("owner").toString();
					String created = dbObject.get("created_at").toString();
					String followers = Users.getFollowersUser(user);
					String following = Users.getFollowingUser(user);
					String ageUser = Users.getAgeUser(user);
					String shaHead = ((BasicDBObject)dbObject.get("head")).get("sha").toString();
					String shaBase = ((BasicDBObject)dbObject.get("base")).get("sha").toString();
					
					//Dados do projeto
					String dataRepo = Repos.getRepoData(owner+"/"+rep);//ageRepo;stargazers_count;watchers_count;language;forks_count;open_issues_count;subscribers_count;has_wiki
					String acceptanceRepo="";
					int totalPullRepoClosed = Repos.getPullRepoClosed(created, rep, owner);
					int mergedPullRepo = Repos.getPullRepoMerged(created, rep, owner);
					if(totalPullRepoClosed==0)
						acceptanceRepo = "0";
					else
						acceptanceRepo = String.valueOf(((mergedPullRepo*100)/totalPullRepoClosed));

					//Dados do requester
					int totalPullUser = Users.getPullUserTotal(user, created, rep, owner);
					int mergedPullUser = Users.getPullUserMerged(user, created, rep, owner);
					int closedPullUser = totalPullUser - mergedPullUser;

					String rejectUser;
					String acceptanceUser="";
					if(totalPullUser==0){
						acceptanceUser = "0";
						rejectUser = "0";
					}else{
						acceptanceUser = String.valueOf(((mergedPullUser*100)/totalPullUser));
						rejectUser = String.valueOf(((closedPullUser*100)/totalPullUser));
					}
					String contributors = Commits.getContributors(shaHead, rep, owner, settings.getContributorsMonths());
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
					String filesPath = "";
					filesPath = Commits.getCommitsFilesPath(shaHead, shaBase, Integer.parseInt(dbObject.get("commits").toString()), Integer.parseInt(dbObject.get("changed_files").toString()));
					String files="", commitsPorArquivos="", authorMoreCommits="";

					if(!filesPath.equals("")){
						files = filesPath.substring(1, filesPath.length()-1);
						//commitsNosArquivos na última semana.
						commitsPorArquivos = Commits.getCommitsByFiles(files, created, rep, settings.getCommitsByFilesDays());
						//Desenvolvedor com mais commits na última semana.
						authorMoreCommits = Commits.getAuthorCommits(files, shaBase, rep, settings.getAuthorCommitsDays());
					}

					String participants = Users.getParticipants(number, rep);
					participants = participants.substring(1, participants.length()-1).replaceAll(", ", "|");

					//tratamento para caminho dos arquivos para buscar o último diretório
					String dirFinal = "";
					String [] path = files.split(", ");
					for(int x=0; x < path.length; x++){
						int lastBarIndex = 0;
						lastBarIndex = path[x].lastIndexOf("/");
						if(lastBarIndex<0 && x == 0){
							dirFinal += "root";
							continue;
						}else 
							if(lastBarIndex<0){
								dirFinal += "|root";
								continue;
							}
						String str = path[x].substring(0, lastBarIndex);
						if (x>=1) 	
							dirFinal += "|"+str;
						else
							dirFinal += str;
					}

					
					//Tempo de vida de um pull request
					String lifetime = "", merged_by = "";
					if((dbObject.get("closed_at")!=null)){
						lifetime = FormatDate.getLifetime(dbObject.get("closed_at").toString(), created);
					}else{
						lifetime = ",,"; 
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

					//String enviada para o arquivo
					String resultTemp = 
							owner+"/"+rep+","+dataRepo+","+
							contributors+","+
							acceptanceRepo+","+
							followers+","+
							following+","+
							ageUser+","+
							typeDeveloper+","+
							totalPullUser+","+
							mergedPullUser+","+
							closedPullUser+","+
							rejectUser+","+
							acceptanceUser+","+
							watchRepo+","+
							followContributors+","+
							location.replace('\n', ' ').replace(',', ' ').replace('\'', '´').replace('"', ' ').replace('%', ' ').replace('/', ' ')+","+
							(Integer) dbObject.get("id")+","+
							(Integer) dbObject.get("number")+","+
							((BasicDBObject)dbObject.get("user")).get("login")+","+
							dbObject.get("state")+","+
							dbObject.get("title").toString().replace('\n', ' ').replace(',', ' ').replace('\'', '´').replace('"', ' ').replace('%', ' ')+","+
							FormatDate.getDate(dbObject.get("created_at").toString())+","+
							closedDate+","+
							mergedDate+","+
							lifetime+","+
							closed_by+","+
							merged_by+","+
							((BasicDBObject)dbObject.get("head")).get("sha")+","+
							((BasicDBObject)dbObject.get("base")).get("sha")+","+
							assignee+","+
							comments+","+
							dbObject.get("commits")+","+
							commitsPorArquivos+","+
							authorMoreCommits+","+
							participants+","+
							dbObject.get("additions")+","+
							dbObject.get("deletions")+","+
							(Integer.parseInt(dbObject.get("additions").toString())+Integer.parseInt(dbObject.get("deletions").toString()))+","+
							dbObject.get("changed_files")+","+
							dirFinal+","+
							files.replace(", ", "|");
					resultTemp += "\r\n";
					System.out.println((Integer) dbObject.get("number"));
					if (!saveFile(false, resultTemp))
						System.err.println("Erro ao tentar escrever o PR: "+(Integer) dbObject.get("number")+", do repositório: "+repo);
				}else
					continue;
				DialogStatus.addsPullRequests();
			}
			finalizedThreads++;
			return "sucess!";
		}catch(Exception ioe){
			finalizedThreads++;
			return "Erro ao recuperar dados.";
		}
	}	

	public boolean saveFile(boolean firstWritten, String pullRequestData){
		File fileTemp = new File(file+File.separator+repo+".csv");
		FileWriter fw = null;
		try {
			if (firstWritten){
				fw = new FileWriter(fileTemp);
				fw.write("owner/repo,ageRepoDays,stargazersCount,watchersCount,language,forksCount,openIssuesCount,subscribersCount,has_wiki,contributors,acceptanceRepo,"
						+ "followers,following,ageUser,typeDeveloper,totalPullDeveloper,mergedPullUser,closedPullUser,rejectUser,acceptanceDeveloper,watchRepo,followContributors,location,"
						+ "idPull,numberPull,login,state,title,createdDate,closedDate,mergedDate,lifetimeDays,lifetimeHours,lifetimeMinutes,closedBy,"
						+ "mergedBy,commitHeadSha,commitBaseSha,assignee,comments,commitsPull,commitsbyFilesPull,authorMoreCommits,participants,"
						+ "additionsLines,deletionsLines,totalLines,changedFiles,dirFinal,files");
				fw.write("\r\n");
			}else{
				fw = new FileWriter(fileTemp, true);
				fw.write(pullRequestData);
			}

		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("erro na escrita do arquivo.");
			return false;
		}
		try {
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("erro ao tentar fechar a escrita do arquivo (2).");
		}
		return true;
	}
}
