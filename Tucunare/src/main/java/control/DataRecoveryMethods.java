package control;

import java.net.UnknownHostException;

import util.FormatDate;
import model.Settings;

import com.mongodb.BasicDBObject;
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

	public DataRecoveryMethods(){
	}

	public DataRecoveryMethods(DBObject dbObject, Settings settings){
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
		created = FormatDate.getDate(dbObject.get("created_at").toString());
		loadHeader();
	}

	private void loadHeader() {

	}

	public String getHeader() {
		return header;
	}

	public String getAllRepoData(){
		String result = owner+"/"+rep+",";
		try {
			result += Repos.getRepoData(owner+"/"+rep)+","; //ageRepo;stargazers_count;watchers_count;language;forks_count;open_issues_count;subscribers_count;has_wiki
			result += getAcceptanceRepo()+",";
			result += getWatchRepo();
		} catch (Exception e) {
			System.err.println("Error while trying to recover the dataRepo.");
		}
		return result +",";
	}

	public String getAcceptanceRepo() throws UnknownHostException{
		String acceptanceRepo="";
		int totalPullRepoClosed;
		int mergedPullRepo;
		totalPullRepoClosed = Repos.getPullRepoClosed(created, rep, owner);
		mergedPullRepo = Repos.getPullRepoMerged(created, rep, owner);

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
		String result = 
				getPRNumber() +
				getPRTitle() +
				getPRSHAs() +
				getPRDates() +
				getPRClosedMergedBy() +
				getPRLifeTime() +
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
		
		//System.out.println("numberPull,title,commitHeadSha,commitBaseSha,createdDate,closedDate,mergedDate,closedBy,mergedBy,"+
		//			"lifetimeDays,lifetimeHours,lifetimeMinutes,assignee,comments,commitsPull, files,authorMoreCommits,commitsbyFilesPull,"+
		//			"changedFiles, dirFinal, additionsLines,deletionsLines,totalLines,participants,");
		//System.out.println(result);
		
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

	//This method retrieves the contributors and the followContributors.
	public String getContributors() throws UnknownHostException{
		String result = Commits.getContributors(shaHead, rep, owner, settings.getContributorsMonths())+",";
		result += Users.getFollowersTeam(user, rep, owner);

		return result+",";
	}

	public String getId(){
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
		String result = created+",";

		if(dbObject.get("closed_at")!=null)
			result += FormatDate.getDate(dbObject.get("closed_at").toString())+",";
		else
			result += ",";

		if(dbObject.get("merged_at")!=null)
			result += FormatDate.getDate(dbObject.get("merged_at").toString());

		return result+",";
	}

	public String getPRClosedMergedBy(){
		String result = "";
		try {
			result = Issues.getClosedbyPull((Integer) dbObject.get("number"), rep)+",";
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
		else
			lifetime = ",,"; 

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
			commentsPull = PullRequestsComments.getPullComments(dbObject.get("number").toString(), rep);
			commentsIssue = Issues.getIssueComments(dbObject.get("number").toString(), dbObject.get("repo").toString());
		} catch (UnknownHostException e) {
			System.err.println("Error while trying to recover the PR comments.");
			return ",";
		} 

		return (commentsPull + commentsIssue)+",";
	}

	public String getPRCommits(){
		return dbObject.get("commits")+",";
	}

	//necessário realizar a chamada desse método caso sejam requisitados contributors, SHAs, PRFiles, 
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
		return dirFinal+",";
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
			participants = Users.getParticipants(dbObject.get("number").toString(), rep);
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
			typeDeveloper = Commits.getTypeDeveloper(user, rep, owner);
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
			mergedPullUser = Users.getPullUserMerged(user, created, rep, owner);
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
