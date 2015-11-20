package control;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import model.Settings;
import teste.DialogStatus;
import util.Connect;
import util.FormatDate;
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

		String [] r = repo.split("/");
		String nameRepo = r[1];
		String ownerRepo = r[0];
		BasicDBObject query = new BasicDBObject("repo",r[1]); //consulta com query
		query.append("owner", ownerRepo);
//		query.append("number", new BasicDBObject("$gt", Integer.parseInt("5")));//maiores que number
		if (settings.getPrType() == 1)
			query.append("state", "open"); //Apenas pull requests encerrados
		if (settings.getPrType() == 2)
			query.append("state", "closed"); //Apenas pull requests encerrados

		//Dados globais para o projeto
		ArrayList<String> listCoreTeam = Commits.getCoreTeamPullList(nameRepo,ownerRepo);
		//ArrayList<String> listCoreTeamTeste = Commits.getCoreTeamList3(nameRepo,ownerRepo);
		String firstCreateDate = "";
		BasicDBObject queryData = new BasicDBObject("repo",nameRepo);
		queryData.append("owner", ownerRepo);
//		queryData.append("number", 1);//primeiro pull
		BasicDBObject fields = new BasicDBObject();
		fields.put("created_at", 1);
		fields.put("_id", 0);
		BasicDBObject dbo = new BasicDBObject("created_at",1);
		DBObject p = dbcPullRequest.findOne(queryData, fields, dbo);
		firstCreateDate = p.get("created_at").toString();
		
		try{
			DBCursor cursor = dbcPullRequest.find(query);//.sort(new BasicDBObject("number", -1));
			cursor.addOption(com.mongodb.Bytes.QUERYOPTION_NOTIMEOUT);
			int total_pull = 0;
			for (DBObject dbObject : cursor) {
				//Variváveis
				//String rep = dbObject.get("repo").toString();
				String user = ((BasicDBObject)dbObject.get("user")).get("login").toString();
				String number = dbObject.get("number").toString();
				//String owner = dbObject.get("owner").toString();//Repos.getOwner(repo);
				String closed_by = Issues.getClosedbyPull((Integer) dbObject.get("number"), nameRepo,ownerRepo);
				if(!closed_by.equals("")){
					if(dbObject!=null && !closed_by.equals(user)){
						String dataRepo = Repos.getRepoData(ownerRepo+"/"+nameRepo);//ageRepo;stargazers_count;watchers_count;language;forks_count;open_issues_count;subscribers_count;has_wiki
						String created = dbObject.get("created_at").toString();
						String followers = Users.getFollowersUser(user);
						String following = Users.getFollowingUser(user);
						String ageUser = Users.getAgeUser(user);
						String shaHead = ((BasicDBObject)dbObject.get("head")).get("sha").toString();
						String shaBase = ((BasicDBObject)dbObject.get("base")).get("sha").toString();

						
						//Dados do projeto
						String acceptanceRepo="";
						int totalPullRepoClosed = Repos.getPullRepoClosed(created, firstCreateDate, nameRepo,ownerRepo);
						int mergedPullRepo = Repos.getPullRepoMerged(created, firstCreateDate, nameRepo,ownerRepo);
						if(totalPullRepoClosed==0)
							acceptanceRepo = "0";
						else
							acceptanceRepo = String.valueOf(((mergedPullRepo*100)/totalPullRepoClosed));

						//Dados do requester
						int totalPullUser = Users.getPullUserTotal(user, created, nameRepo, firstCreateDate);
						int mergedPullUser = Users.getPullUserMerged(user, created, nameRepo, firstCreateDate, ownerRepo);
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

						//					verificar uso do listTeam no lugar de contributors
						//String contributors = Commits.getContributors(shaHead, rep, owner, settings.getContributorsMonths());
						//String typeDeveloper = Commits.getTypeDeveloper(user, rep, owner);

						boolean watchRepo = Users.getWatcherRepo (user, created, nameRepo,ownerRepo);
						//					boolean followContributors = Users.getFollowersContributors(user, rep, owner);
						String location = Users.getLocationUser(user);

						//Dados do Pull Request
						String assignee = "";
						if(dbObject.get("assignee")!=null)
							assignee = (String) ((BasicDBObject)dbObject.get("assignee")).get("login");

						//comentários
						int commentsPull = PullRequestsComments.getPullComments(number, nameRepo,ownerRepo); 
						int commentsIssue = Issues.getIssueComments(number, nameRepo,ownerRepo);
						long comments = commentsPull + commentsIssue;

						//arquivos
						String filesPath = "";
						filesPath = Commits.getCommitsFilesPath(shaHead, shaBase, Integer.parseInt(dbObject.get("commits").toString()), Integer.parseInt(dbObject.get("changed_files").toString()));
						String files="", authorMoreCommits="";
						int commitsPorArquivos=0;
						if(!filesPath.equals("")){
							files = filesPath.substring(1, filesPath.length()-1);
							//commitsNosArquivos na última semana.
							commitsPorArquivos = Commits.getCommitsByFiles(files, created, ownerRepo+"/"+nameRepo, settings.getCommitsByFilesDays());
							//Desenvolvedor com mais commits na última semana.
							authorMoreCommits = Commits.getAuthorCommits(files, shaBase, ownerRepo+"/"+nameRepo, settings.getAuthorCommitsDays());
						}

						String participants = Users.getParticipants(number, nameRepo, user, closed_by, ownerRepo);
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
						if(((BasicDBObject) dbObject).get("closed_at")!=null){
							lifetime = FormatDate.getLifetime(dbObject.get("closed_at").toString(), created);
						}else{
							lifetime = ""; 
						}
						//
						//Desenvolvedor que integrou ou rejeitou o código do pull request
						if((dbObject.get("merged_by"))!=null)
							merged_by = ((BasicDBObject)dbObject.get("merged_by")).get("login").toString();

						//Datas
						String closedDate = "";
						if(((BasicDBObject) dbObject).get("closed_at")!=null)
							closedDate = FormatDate.getDate(dbObject.get("closed_at").toString());
						String mergedDate = "";
						if(((BasicDBObject) dbObject).get("merged_at")!=null)
							mergedDate = FormatDate.getDate(dbObject.get("merged_at").toString());
						//String createDate = FormatDate.getDate(dbObject.get("created_at").toString());

						String ano = created.substring(0, 4);
						String mes = created.substring(5, 7);
						String mesAno = mes+ano;

						String status = "";
						if(mergedDate.equals(""))
							status = "closed";
						else
							status = "merged";

						//Dados da equipe principal
//						String coreTeamTeste="";
//						if(listCoreTeamTeste.contains(user))
//							coreTeamTeste = "core";
//						else
//							coreTeamTeste = "external";

						//Atributos CoreDevRec
						String typeDeveloper = Commits.getTypeDeveloper3(user, nameRepo, ownerRepo, created);
						boolean requesterFollowsCoreTeam = Users.getRequesterFollowsCoreTeam(user, closed_by);
						boolean coreTeamFollowsRequester = Users.getCoreTeamFollowsRequester(user, closed_by);

						String prior_evalution = Issues.getPrior_Pull(user, created, firstCreateDate, nameRepo,ownerRepo, listCoreTeam);
						prior_evalution = prior_evalution.substring(1, prior_evalution.length()-1).replaceAll(", ", ",");

						String recent_pull = Issues.getRecentPulls(nameRepo,ownerRepo, created, firstCreateDate, 30, listCoreTeam);
						recent_pull = recent_pull.substring(1, recent_pull.length()-1).replaceAll(", ", ",");

						String evaluation_pull = Issues.getEvaluatePulls(nameRepo,ownerRepo, created, firstCreateDate, listCoreTeam);
						evaluation_pull = evaluation_pull.substring(1, evaluation_pull.length()-1).replaceAll(", ", ",");

						String recent_evaluation = Issues.getRecentEvaluatePulls(user, nameRepo,ownerRepo, created, firstCreateDate, 30, listCoreTeam);
						recent_evaluation = recent_evaluation.substring(1, recent_evaluation.length()-1).replaceAll(", ", ",");

						String evaluate_time = Issues.getEvaluateTime(nameRepo,ownerRepo, created, firstCreateDate, 30, listCoreTeam);
						evaluate_time = evaluate_time.substring(1, evaluate_time.length()-1).replaceAll(", ", ",");

						String latest_time = Issues.getLatestTime(nameRepo,ownerRepo, created, listCoreTeam);
						latest_time = latest_time.substring(1, latest_time.length()-1).replaceAll(", ", ",");

						String first_time = Issues.getFirstTime(nameRepo,ownerRepo, created, listCoreTeam);
						first_time = first_time.substring(1, first_time.length()-1).replaceAll(", ", ",");

						//String total_pull = ""+Issues.getTotalPull(rep, created, firstCreateDate);
						total_pull++;

						//String enviada para o arquivo
						String resultTemp = 
										ownerRepo+"/"+nameRepo+","+
										dataRepo+","+
										//contributors+","+
										acceptanceRepo+","+
										followers+","+
										following+","+
										ageUser+","+
										//typeDeveloper+","+
										totalPullUser+","+
										mergedPullUser+","+
										closedPullUser+","+
										rejectUser+","+
										acceptanceUser+","+
										watchRepo+","+
										//followContributors+","+
										location.replace('\n', ' ').replace(',', ' ').replace('\'', '´').replace('"', ' ').replace('%', ' ').replace('/', ' ')+","+
										(Integer) dbObject.get("id")+","+
										(Integer) dbObject.get("number")+","+
										((BasicDBObject)dbObject.get("user")).get("login")+","+
										dbObject.get("state")+","+
										dbObject.get("title").toString().replace('\n', ' ').replace(',', ' ').replace('\'', '´').replace('"', ' ').replace('%', ' ')+","+
										created+","+
										mesAno+","+
										closedDate+","+
										mergedDate+","+
										lifetime+","+
										closed_by+","+
										merged_by+","+
										status+","+
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
										files.replace(", ", "|")+","+
//										coreTeamTeste+","+
										typeDeveloper+","+
										requesterFollowsCoreTeam+","+
										coreTeamFollowsRequester+","+
										prior_evalution+","+
										recent_pull+","+
										evaluation_pull+","+
										recent_evaluation+","+
										evaluate_time+","+
										latest_time+","+
										first_time+","+
										total_pull
										;
										resultTemp += "\r\n";
						if (!saveFile(false, resultTemp))
							System.err.println("Erro ao tentar escrever o PR: "+(Integer) dbObject.get("number")+", do repositório: "+repo);
					}else{
						continue;
					}	
				}else{
					continue;	
				}
				DialogStatus.addsPullRequests();
				System.out.println((Integer) dbObject.get("number"));
			}//fim cursor
			finalizedThreads++;
			return "sucess!";
		}catch(Exception ioe){
			finalizedThreads++;
			return "Erro ao recuperar dados.";
		}//fim método
	}	

	public boolean saveFile(boolean firstWritten, String pullRequestData){
		String [] r = repo.split("/");
		String nameRepo = r[1];
		String ownerRepo = r[0];
		File fileTemp = new File(file+File.separator+nameRepo+".csv");
		FileWriter fw = null;
		try {
			if (firstWritten){
				fw = new FileWriter(fileTemp);
				ArrayList<String> prior_evalutionList = Commits.getCoreTeamPullList(nameRepo, ownerRepo);
				for (int index = 0; index < prior_evalutionList.size(); index++)
					prior_evalutionList.set(index, "pe_"+prior_evalutionList.get(index));
				String pe = prior_evalutionList.toString();
				pe = pe.substring(1, pe.length()-1).replaceAll(", ", ",");

				ArrayList<String> recent_pullList = Commits.getCoreTeamPullList(nameRepo, ownerRepo);
				for (int index = 0; index < recent_pullList.size(); index++)
					recent_pullList.set(index, "rp_"+recent_pullList.get(index));
				String rp = recent_pullList.toString();
				rp = rp.substring(1, rp.length()-1).replaceAll(", ", ",");

				ArrayList<String> evaluation_pullList = Commits.getCoreTeamPullList(nameRepo, ownerRepo);
				for (int index = 0; index < evaluation_pullList.size(); index++)
					evaluation_pullList.set(index, "ep_"+evaluation_pullList.get(index));
				String ep = evaluation_pullList.toString();
				ep = ep.substring(1, ep.length()-1).replaceAll(", ", ",");

				ArrayList<String> recent_evaluationList = Commits.getCoreTeamPullList(nameRepo, ownerRepo);
				for (int index = 0; index < recent_evaluationList.size(); index++)
					recent_evaluationList.set(index, "re_"+recent_evaluationList.get(index));
				String re = recent_evaluationList.toString();
				re = re.substring(1, re.length()-1).replaceAll(", ", ",");

				ArrayList<String> evaluate_timeList = Commits.getCoreTeamPullList(nameRepo, ownerRepo);
				for (int index = 0; index < evaluate_timeList.size(); index++)
					evaluate_timeList.set(index, "et_"+evaluate_timeList.get(index));
				String et = evaluate_timeList.toString();
				et = et.substring(1, et.length()-1).replaceAll(", ", ",");

				ArrayList<String> latest_timeList = Commits.getCoreTeamPullList(nameRepo, ownerRepo);
				for (int index = 0; index < latest_timeList.size(); index++)
					latest_timeList.set(index, "lt_"+latest_timeList.get(index));
				String lt = latest_timeList.toString();
				lt = lt.substring(1, lt.length()-1).replaceAll(", ", ",");

				ArrayList<String> first_timeList = Commits.getCoreTeamPullList(nameRepo, ownerRepo);
				for (int index = 0; index < first_timeList.size(); index++)
					first_timeList.set(index, "ft_"+first_timeList.get(index));
				String ft = first_timeList.toString();
				ft = ft.substring(1, ft.length()-1).replaceAll(", ", ",");

				fw.write("owner/repo,ageRepoDays,stargazersCount,watchersCount,language,forksCount,openIssuesCount,subscribersCount,has_wiki,acceptanceRepo,"
						+ "followers,following,ageUser,totalPullDeveloper,mergedPullUser,closedPullUser,rejectUser,acceptanceDeveloper,watchRepo,location,"
						+ "idPull,numberPull,login,state,title,createdDate,mesAno,closedDate,mergedDate,lifetimeMinutes,closedBy,mergedBy,"
						+ "status,commitHeadSha,commitBaseSha,assignee,comments,commitsPull,commitsbyFilesPull,authorMoreCommits,participants,"
						+ "additionsLines,deletionsLines,totalLines,changedFiles,dirFinal,files,typeDeveloper,requesterFollowsCoreTeam,coreTeamFollowsRequester,"
						//+ "prior_evalution,recent_pull,evaluation_pull,recent_evaluation,evaluate_time,latest_time,first_time,total_pull");
						+pe+","+rp+","+ep+","+re+","+et+","+lt+","+ft+","
						+ "total_pull");
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