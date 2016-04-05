package control;

import java.net.UnknownHostException;
import java.util.ArrayList;

import util.FormatDate;
import model.Settings;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;

public class DataRecoveryMethods {
	private String header;
	private String filesPath;
	private DBObject dbObject;
	private String files;
	private String shaHead;
	private String shaBase;
	private String owner;
	private String rep;
	private String user;
	private String created;
	private Settings settings;
	private int closedPullUser;
	private int totalPullUser;
	private int mergedPullUser;
	private ArrayList<String> listCoreTeam;
	private DBCollection dbcPullRequest;
	private String firstCreateDate;
	private String closed_by;

	public DataRecoveryMethods(){
	}

	public DataRecoveryMethods(DBCollection dbcPullRequest, DBObject dbObject, Settings settings){
		this.dbcPullRequest = dbcPullRequest;
		this.dbObject = dbObject;
		this.settings = settings;
		inicializa();
	}

	public void inicializa(){
		shaHead = ((BasicDBObject)dbObject.get("head")).get("sha").toString();
		shaBase = ((BasicDBObject)dbObject.get("base")).get("sha").toString();
		owner = dbObject.get("owner").toString();
		rep = dbObject.get("repo").toString();
		user = ((BasicDBObject)dbObject.get("user")).get("login")+"";
		created = dbObject.get("created_at").toString();

		try {
			listCoreTeam = Commits.getCoreTeamPullList(rep,owner);
		} catch (UnknownHostException e) {
			System.err.println("Erro ao recuperar listCoreTeam.");
			listCoreTeam = new ArrayList<String>();
		}
		firstCreateDate = getFirstCreateDate();

		try {
			closed_by = Issues.getClosedbyPull((Integer) dbObject.get("number"), rep,owner);
		} catch (UnknownHostException e) {
			System.err.println("Erro ao tentar recuperar closed_by");
		}

		loadHeader();
	}

	private String getFirstCreateDate() {
		BasicDBObject queryData = new BasicDBObject("repo", rep);
		queryData.append("owner", owner);
		BasicDBObject fields = new BasicDBObject();
		fields.put("created_at", 1);
		fields.put("_id", 0);
		BasicDBObject dbo = new BasicDBObject("created_at",1);
		DBObject p = dbcPullRequest.findOne(queryData, fields, dbo);
		return p.get("created_at").toString();

	}

	private void loadHeader() {

	}

	public String getHeader() {
		return header;
	}

	public String getAllRepoData(){
		StringBuilder result = new StringBuilder();
		result.append(owner+"/"+rep+",");
		try {
			result.append(Repos.getRepoData(owner+"/"+rep)+","); //ageRepo;stargazers_count;watchers_count;language;forks_count;open_issues_count;subscribers_count;has_wiki
			result.append(getAcceptanceRepo()+",");
			result.append(getWatchRepo());
		} catch (Exception e) {
			System.err.println("Error while trying to recover the dataRepo.");
		}
		result.append(",");
		return result.toString();
	}

	public String getAllCoreDevRecData(){
		StringBuilder result = new StringBuilder("");
		try{
			result.append(getFollowerRelation());
			result.append(getFollowingRelation());
			result.append(getPriorEvaluation());
			result.append(getRecentPulls());
			result.append(getEvaluationPulls());
			result.append(getRecentEvaluation());
			result.append(getEvaluateTime());
			result.append(getLatestTime());
			result.append(getFirstTime());

		}catch(Exception e){
			System.err.println("Erro ao tentar executar os métodos de recuperação de dados CoreDevRec.");
			return result.toString();
		}
		return result.toString();
	}

	private String getFollowerRelation() {
		try {
			return Users.getRequesterFollowsCoreTeam(user, closed_by)+",";
		} catch (UnknownHostException e) {
			System.err.println("Erro ao tentar recuperar Follower Relation.");
			return ",";
		}
	}

	private String getFollowingRelation() {
		try {
			return Users.getCoreTeamFollowsRequester(user, closed_by)+",";
		} catch (UnknownHostException e) {
			System.err.println("Erro ao tentar recuperar Following Relation.");
			return ",";
		}
	}

	private String getPriorEvaluation() {
		String prior_evalution;
		try {
			prior_evalution = Issues.getPrior_Pull(user, created, firstCreateDate, rep,owner, listCoreTeam);
			return prior_evalution.substring(1, prior_evalution.length()-1).replaceAll(", ", ",")+",";
		} catch (UnknownHostException e) {
			System.err.println("Erro ao tentar recuperar Prior Evaluation.");
			return ",";
		}
	}

	private String getRecentPulls() {
		String recent_pull;
		try {
			recent_pull = Issues.getRecentPulls(rep,owner, created, firstCreateDate, 30, listCoreTeam);
			return recent_pull.substring(1, recent_pull.length()-1).replaceAll(", ", ",")+",";

		} catch (UnknownHostException e) {
			System.err.println("Erro ao tentar recuperar Recent Pulls.");
			return ",";
		}
	}

	private String getEvaluationPulls() {
		String evaluation_pull;
		try {
			evaluation_pull = Issues.getEvaluatePulls(rep,owner, created, firstCreateDate, listCoreTeam);
			return evaluation_pull.substring(1, evaluation_pull.length()-1).replaceAll(", ", ",")+",";
		} catch (UnknownHostException e) {
			System.err.println("Erro ao tentar recuperar Evaluation Pulls.");
			return ",";
		}
	}

	private String getRecentEvaluation() {
		String recent_evaluation;
		try {
			recent_evaluation = Issues.getRecentEvaluatePulls(user, rep,owner, created, firstCreateDate, 30, listCoreTeam);
			return recent_evaluation.substring(1, recent_evaluation.length()-1).replaceAll(", ", ",")+",";
		} catch (UnknownHostException e) {
			System.err.println("Erro ao tentar recuperar Recent Evaluation.");
			return ",";
		}
	}

	private String getEvaluateTime() {
		String evaluate_time;
		try {
			evaluate_time = Issues.getEvaluateTime(rep,owner, created, firstCreateDate, 30, listCoreTeam);
			return evaluate_time.substring(1, evaluate_time.length()-1).replaceAll(", ", ",")+",";
		} catch (UnknownHostException e) {
			System.err.println("Erro ao tentar recuperar Evaluate Time.");
			return ",";
		}
	}

	private String getLatestTime() {
		String latest_time;
		try {
			latest_time = Issues.getLatestTime(rep,owner, created, listCoreTeam);
			return latest_time.substring(1, latest_time.length()-1).replaceAll(", ", ",")+",";
		} catch (UnknownHostException e) {
			System.err.println("Erro ao tentar recuperar Latest Time.");
			return ",";
		}
	}

	private String getFirstTime() {
		String first_time;
		try {
			first_time = Issues.getFirstTime(rep,owner, created, listCoreTeam);
			return first_time.substring(1, first_time.length()-1).replaceAll(", ", ",")+",";
		} catch (UnknownHostException e) {
			System.err.println("Erro ao tentar recuperar First Time.");
			return ",";
		}
	}

	public String getAcceptanceRepo() throws UnknownHostException{
		String acceptanceRepo="";
		int totalPullRepoClosed;
		int mergedPullRepo;
		totalPullRepoClosed = Repos.getPullRepoClosed(created, firstCreateDate, rep, owner);
		mergedPullRepo = Repos.getPullRepoMerged(created, firstCreateDate, rep, owner);

		if(totalPullRepoClosed==0)
			acceptanceRepo = "0";
		else
			acceptanceRepo = String.valueOf(((mergedPullRepo*100)/totalPullRepoClosed));

		return acceptanceRepo;
	}

	public boolean getWatchRepo() throws UnknownHostException{
		return Users.getWatcherRepo (user, created, rep, owner);
	}

	public String getAllPRData(){
		/**
		 * idPull,statePull,numberPull,title,commitHeadSha,commitBaseSha,createdDate,closedDate,mergedDate,lifetimeMinutes,"+
		 * "closedBy,mergedBy,assignee,comments,commitsPull,files,authorMoreCommits,commitsbyFilesPull,"+
					"changedFiles, dirFinal, additionsLines,deletionsLines,totalLines,participants
		 * */
		String result = 
				getPRId() +
				getPRState()+
				getPRNumber() +
				getPRTitle() +
				getPRSHAs() +
				getPRDates() +
				getPRLifeTime() +
				getPRClosedMergedBy() +

				getPRAssignee() +
				getPRComments() +
				getPRCommits() +
				getPRFiles() +
				getPRAuthorMoreCommits() +
				getPRCommitsByFiles() +
				getPRChangedFiles() +
				getPRRootDirectory() +
				getPRModifiedLines() + 
				getPRParticipants();

		return result;
	}

	public String getAllAuthorData(){
		String result = 
				getUser() +
				getUserAge() +
				getUserType() +
				getUserPulls() +
				getUserAverages() +
				getUserFollowers() +
				getUserFollowing() +
				getUserLocation();		
		return result;
	}

	public String getPRId(){
		return dbObject.get("id")+",";
	}

	public String getPRNumber(){
		return dbObject.get("number").toString()+",";
	}

	public String getPRState(){
		return dbObject.get("state").toString()+",";
	}

	public String getPRTitle(){
		return dbObject.get("title").toString().replace('\n', ' ').replace(',', ' ').replace('\'', '´').replace('"', ' ').replace('%', ' ')+",";
	}

	public String getPRSHAs(){
		return shaHead+", "+shaBase+",";
	}

	public String getPRDates(){
		StringBuilder result = new StringBuilder(created+",");

		result.append(created.substring(0, 4));
		result.append(created.substring(5, 7));
		result.append(",");

		if(dbObject.get("closed_at")!=null)
			result.append(FormatDate.getDate(dbObject.get("closed_at").toString())+",");
		else
			result.append(",");

		if(dbObject.get("merged_at")!=null)
			result.append(FormatDate.getDate(dbObject.get("merged_at").toString()));

		result.append(",");
		return result.toString();
	}

	public String getPRClosedMergedBy(){
		String result = "";
		try {
			result = Issues.getClosedbyPull((Integer) dbObject.get("number"), rep, owner)+",";
		} catch (UnknownHostException e) {
			result = ",";
		}
		//Desenvolvedor que integrou ou rejeitou o código do pull request
		if((dbObject.get("merged_by"))!=null)
			result += ((BasicDBObject)dbObject.get("merged_by")).get("login").toString();

		return result+",";
	}

	//Tempo de vida de um pull request
	public String getPRLifeTime(){
		String lifetime = "";
		if((dbObject.get("closed_at")!=null))
			lifetime = FormatDate.getLifetime(dbObject.get("closed_at").toString(), dbObject.get("created_at").toString());

		return lifetime+",";
	}

	public String getPRAssignee(){
		String assignee = "";
		if(dbObject.get("assignee")!=null)
			assignee = (String) ((BasicDBObject)dbObject.get("assignee")).get("login");
		return assignee+",";
	}

	//comentários
	public String getPRComments(){
		int commentsPull = 0, commentsIssue = 0;
		try {
			commentsPull = PullRequestsComments.getPullComments(dbObject.get("number").toString(), rep, owner);
			commentsIssue = Issues.getIssueComments(dbObject.get("number").toString(), rep, owner);
		} catch (UnknownHostException e) {
			System.err.println("Error while trying to recover the PR comments.");
			return ",";
		} 

		return (commentsPull + commentsIssue)+",";
	}

	public String getPRCommits(){
		return dbObject.get("commits")+",";
	}

	//necessário realizar a chamada desse método caso sejam requisitados SHAs, PRFiles, 
	public void getCommitsFilesPath(){
		try {
			filesPath = Commits.getCommitsFilesPath(shaHead, shaBase, Integer.parseInt(dbObject.get("commits").toString()),Integer.parseInt(dbObject.get("changed_files").toString()));
		} catch (Exception e) {
			System.err.println("Erro ao tentar recuperar o caminho dos arquivos do commit.");;
		}
	}

	//recuperar o autor com mais commits é necessário recuperar os arquivos.
	public String getPRAuthorMoreCommits(){
		String result = "";
		try {
			result = Commits.getAuthorCommits(files, shaBase, owner+"/"+rep, settings.getAuthorCommitsDays());
		} catch (UnknownHostException e) {
			System.err.println("Error while trying to get the PRAuthorMoreCommits");
		}
		return result+",";
	}

	//para recuperar os commits por arquivos é necessário recuperar os arquivos.	
	public String getPRCommitsByFiles(){
		String result = "";
		try {
			result = Commits.getCommitsByFiles(files, dbObject.get("created_at").toString(), owner+"/"+rep, settings.getCommitsByFilesDays())+"";
		} catch (UnknownHostException e) {
			System.err.println("Error while trying to get PRCommitsByFiles");
		}	
		return result+",";
	}

	public String getPRChangedFiles(){
		return dbObject.get("changed_files")+",";
	}

	public String getPRFiles(){
		getCommitsFilesPath();
		files = filesPath.substring(1, filesPath.length()-1);
		return files.replace(", ", "|")+",";
	}

	public String getPRRootDirectory(){
		StringBuilder dirFinal = new StringBuilder("");
		String [] path = files.split(", ");
		for(int x=0; x < path.length; x++){
			int lastBarIndex = 0;
			lastBarIndex = path[x].lastIndexOf("/");
			if(lastBarIndex<0 && x == 0){
				dirFinal.append("root");
				continue;
			}else 
				if(lastBarIndex<0){
					dirFinal.append("|root");
					continue;
				}
			String str = path[x].substring(0, lastBarIndex);
			if (x>=1) 	
				dirFinal.append("|"+str);
			else
				dirFinal.append(str);
		}
		dirFinal.append(",");
		return dirFinal.toString();
	}

	public String getPRModifiedLines(){
		String result = dbObject.get("additions")+","+ 
				dbObject.get("deletions")+","+ 
				(Integer.parseInt(dbObject.get("additions").toString())+Integer.parseInt(dbObject.get("deletions").toString()));
		return result+",";
	}

	public String getPRParticipants(){
		String participants ="";
		try {
			participants = Users.getParticipants(dbObject.get("number").toString(), rep, user, closed_by, owner);
			participants = participants.substring(1, participants.length()-1).replaceAll(", ", "|");
		} catch (UnknownHostException e) {
			System.err.println("Error while trying to retrieve the partcipants.");
		}
		return participants+",";
	}

	public String getUser(){
		return user+",";
	}

	public String getUserAge(){
		String ageUser = "";
		try {
			ageUser = Users.getAgeUser(user);
		} catch (UnknownHostException e) {
			System.err.println("Error while trying to recover the user age.");
		}
		return ageUser+",";
	}

	public String getUserType(){
		String typeDeveloper = "";
		try {
			typeDeveloper = Commits.getTypeDeveloper3(user, rep, owner, created);
		} catch (UnknownHostException e) {
			System.err.println("Error while trying to recover the user developer type.");
		}
		return typeDeveloper+",";
	}

	public String getUserPulls(){
		totalPullUser = 0;
		mergedPullUser = 0; 
		try {
			totalPullUser = Users.getPullUserTotal(user, created, rep, owner);
			mergedPullUser = Users.getPullUserMerged(user, created, rep, firstCreateDate, owner);
		} catch (UnknownHostException e) {
			System.err.println("Error while trying to recover the User Pull Requests.");
		}

		closedPullUser = totalPullUser - mergedPullUser;

		return totalPullUser+"," + mergedPullUser+"," + closedPullUser+",";
	}

	//para recuperar as médias de aceitação e rejeição do PR o usuário deve recuperar os Pull requests do user.
	public String getUserAverages(){
		String rejectUser;
		String acceptanceUser="";
		if(totalPullUser==0){
			acceptanceUser = "0";
			rejectUser = "0";
		}else{
			acceptanceUser = String.valueOf(((mergedPullUser*100)/totalPullUser));
			rejectUser = String.valueOf(((closedPullUser*100)/totalPullUser));
		}

		return rejectUser+"," + acceptanceUser+",";
	}

	public String getUserFollowers(){
		String result = "";
		try {
			result = Users.getFollowersUser(user);
		} catch (UnknownHostException e) {
			System.err.println("Error while trying to recover the user followers.");
		}
		return result+",";
	}

	public String getUserFollowing(){
		String result="";
		try {
			result = Users.getFollowingUser(user);
		} catch (UnknownHostException e) {
			System.err.println("Error while trying to recover the user followings.");
		}
		return result+",";
	}

	public String getUserLocation(){
		String result = "";
		try {
			result = Users.getLocationUser(user);
			result.replace('\n', ' ').replace(',', ' ').replace('\'', '´').replace('"', ' ').replace('%', ' ').replace('/', ' ');
		} catch (UnknownHostException e) {
			System.err.println("Error while trying to recover the User Location.");
		}

		return result+",";
	}

}
