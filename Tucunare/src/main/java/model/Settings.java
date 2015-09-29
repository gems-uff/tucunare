package model;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public  class Settings {
	private String header = "";
	private JSONObject data;
	private boolean allRepoData, contributors;
	private int contributorsMonths = -1;
	private boolean allAuthorData, prNumber, prState, title, prDates, prLifeTime, prClosedMergedBy, prShas, prAuthorMoreCommits, prCommitsByFiles; 
	private boolean allPRData, prAssignee, prComments, prCommits, prModifiedLines, prChangedFiles, prRootDirectory, prFiles, prParticipants;
	private boolean user, userAge, userType, userPulls, userAverages, userFollowers, userFollowing, userLocation; 
	private int prType = 0;
	private int authorCommitsDays, commitsByFilesDays;

	public Settings() {
		setDefaultValues();
	}

	public Settings(JSONObject data){
		this.data = data;
	}

	public boolean tryParseValues(){
		try {
			tryParseRepoData();
			tryParsePRCore();
			tryParsePRFiles();
			tryparseAuthorPR();
			return true;
		} catch (JSONException e) {
			System.err.println(e.getMessage());
			setDefaultValues();
			return false;
		}
	}


	private void tryparseAuthorPR() throws JSONException {
		allAuthorData = data.getBoolean("allauthordata");
		user = data.getBoolean("user");
		userAge = data.getBoolean("age");
		userType = data.getBoolean("type");
		userPulls = data.getBoolean("pullsuser");
		userAverages = data.getBoolean("averages");
		userFollowers = data.getBoolean("followers");
		userFollowing = data.getBoolean("following");
		userLocation = data.getBoolean("location");
	}

	//"comments":"true","commits":["true","10","5"],"modifiedlines":"true","changedfiles":"true","dirfinal":"true","filespath":"true",
	private void tryParsePRFiles() throws JSONException {
		JSONArray ja = data.getJSONArray("commitsdays"); 

		prComments = data.getBoolean("comments");
		prCommits = data.getBoolean("commits");
		//dias do author, dias dos arquivos
		authorCommitsDays = ja.getInt(0);
		commitsByFilesDays = ja.getInt(1);
		contributorsMonths = ja.getInt(2);

		prModifiedLines = data.getBoolean("modifiedlines");
		prChangedFiles = data.getBoolean("changedfiles");
		prRootDirectory = data.getBoolean("dirfinal");
		prFiles = data.getBoolean("files");
		prParticipants = data.getBoolean("participants");
	}

	//"number":"true","state":"true","title":"true","dates":"true","lifetime":"true","closedmergedby":"true","shas":"true","assignee":"true",
	private void tryParsePRCore() throws JSONException {
		allPRData = data.getBoolean("allprdata");
		prType = data.getInt("prtype");
		prNumber = data.getBoolean("number");
		prState = data.getBoolean("state");
		title = data.getBoolean("title");
		prDates = data.getBoolean("dates");
		prLifeTime = data.getBoolean("lifetime");
		prClosedMergedBy = data.getBoolean("closedmergedby");
		prShas = data.getBoolean("shas");
		prAssignee = data.getBoolean("assignee");
		prAuthorMoreCommits = data.getBoolean("authormorecommits");
		prCommitsByFiles = data.getBoolean("commitsbyfiles");
	}

	//"repo":"true",
	private void tryParseRepoData() throws JSONException {
		allRepoData = data.getBoolean("allrepodata");
		contributors = data.getBoolean("contributors");
	}

	public void setDefaultValues(){
		prType = 0;
		contributorsMonths = 1;
		authorCommitsDays = 7;
		commitsByFilesDays = 7;

		allAuthorData = true;
		allPRData = true;
		allRepoData = true; 
		prNumber = true; 
		prState = true; 
		title = true; 
		prDates = true; 
		prLifeTime = true; 
		prClosedMergedBy = true; 
		prShas = true; 
		prAssignee = true; 
		prComments = true; 
		prModifiedLines = true; 
		prChangedFiles = true; 
		prRootDirectory = true; 
		prFiles = true; 
		user = true; 
		userAge = true; 
		userType = true; 
		userPulls = true; 
		userAverages = true; 
		userFollowers = true;
		userFollowing = true; 
		userLocation = true;
		prParticipants = true;

	}

	public String getHeader() {
		return header;
	}

	public int getPrType(){
		return prType;
	}

	public int getContributorsMonths() {
		return contributorsMonths;
	}

	public void setContributorsMonths(int contributorsMonths) {
		this.contributorsMonths = contributorsMonths;
	}

	public int getAuthorCommitsDays() {
		return authorCommitsDays;
	}

	public void setAuthorCommitsDays(int authorCommitsDays) {
		this.authorCommitsDays = authorCommitsDays;
	}

	public int getCommitsByFilesDays() {
		return commitsByFilesDays;
	}

	public void setCommitsByFilesDays(int commitsByFilesDays) {
		this.commitsByFilesDays = commitsByFilesDays;
	}

	public List<String> getMethods(){
		List<String> methods = new ArrayList<String>();

		if (allRepoData){
			methods.add("getAllRepoData");
			header += "owner/repo,ageRepo,stargazers_count,watchers_count,language,forks_count,open_issues_count,subscribers_count,has_wiki,acceptanceRepo,watchRepo,";
		}

		if (contributors){
			methods.add("getContributors");
			header += "contributors,followContributors,";
		}
		//pelo menos o método de recuperação do id e o state do PR será executado.
		methods.add("getId");
		methods.add("getPRState");
		header += "idPull,state,";

		if (allPRData){
			methods.add("getAllPRData");
			header += "numberPull,title,commitHeadSha,commitBaseSha,createdDate,closedDate,mergedDate,closedBy,mergedBy,"+
					"lifetimeDays,lifetimeHours,lifetimeMinutes,assignee,comments,commitsPull,files,authorMoreCommits,commitsbyFilesPull,"+
					"changedFiles, dirFinal, additionsLines,deletionsLines,totalLines,participants,";
		}
		else{
			if (prFiles){
				methods.add("getPRFiles");
				header += "files,";
			}
			if (prNumber){
				methods.add("getPRNumber");
				header += "numberPull,";
			}
			if (title){
				methods.add("getPRTitle");
				header += "title,";
			}
			if (prShas){
				methods.add("getPRSHAs");
				header += "commitHeadSha,commitBaseSha,";
			}
			if (prDates){
				methods.add("getPRDates");
				header += "createdDate,closedDate,mergedDate,";
			}
			if (prClosedMergedBy){
				methods.add("getPRClosedMergedBy");
				header += "closedBy,mergedBy,";
			}
			if (prLifeTime){
				methods.add("getPRLifeTime");
				header += "lifetimeDays,lifetimeHours,lifetimeMinutes,";
			}
			if (prAssignee){
				methods.add("getPRAssignee");
				header += "assignee,";
			}
			if (prComments){
				methods.add("getPRComments");
				header += "comments,";
			}
			if (prCommits){
				methods.add("getPRCommits");
				header += "commitsPull,";
			}
			if(prAuthorMoreCommits){
				methods.add("getPRAuthorMoreCommits");
				header += "authorMoreCommits,";
			}
			if (prCommitsByFiles){
				methods.add("getPRCommitsByFiles");
				header += "commitsbyFilesPull,";
			}
			if (prChangedFiles){
				methods.add("getPRChangedFiles");
				header += "changedFiles,";
			}
			if (prRootDirectory){
				methods.add("getPRRootDirectory");
				header += "dirFinal,";
			}
			if (prModifiedLines){
				methods.add("getPRModifiedLines");
				header += "additionsLines,deletionsLines,totalLines,";
			}
			if (prParticipants){
				methods.add("participants");
				header += "participants,";
			}
		}

		if (allAuthorData){
			methods.add("getAllAuthorData");
			header += "login,ageUser,typeDeveloper,totalPullDeveloper,mergedPullUser,closedPullUser,rejectUser, acceptanceUser,"+
					"userFollowers,userFollowing,location,";
		}else{
			if (user){
				methods.add("getUser");
				header += "login,";
			}
			if (userAge){
				methods.add("getUserAge");
				header += "ageUser,";
			}
			if (userType){
				methods.add("getUserType");
				header += "typeDeveloper,"; 
			}
			if (userPulls){
				methods.add("getUserPulls");
				header += "totalPullDeveloper,mergedPullUser,closedPullUser,";
			}
			if (userAverages){
				methods.add("getUserAverages");
				header += "rejectUser, acceptanceUser,";
			}
			if (userFollowers){
				methods.add("getUserFollowers");
				header += "userFollowers,";
			}
			if (userFollowing){
				methods.add("getUserFollowing");
				header += "userFollowing,";
			}
			if (userLocation){
				methods.add("getUserLocation");
				header += "location,";
			}
		} 

		return methods;
	}

	@Override
	public String toString() {
		return "Settings [allRepoData=" + allRepoData + ", contributors="
				+ contributors + ", contributorsMonths=" + contributorsMonths
				+ ", allAuthorData=" + allAuthorData + ", prNumber=" + prNumber
				+ ", prState=" + prState + ", title=" + title + ", prDates="
				+ prDates + ", prLifeTime=" + prLifeTime
				+ ", prClosedMergedBy=" + prClosedMergedBy + ", prShas="
				+ prShas + ", prAuthorMoreCommits=" + prAuthorMoreCommits
				+ ", prCommitsByFiles=" + prCommitsByFiles + ", allPRData="
				+ allPRData + ", prAssignee=" + prAssignee + ", prComments="
				+ prComments + ", prCommits=" + prCommits
				+ ", prModifiedLines=" + prModifiedLines + ", prChangedFiles="
				+ prChangedFiles + ", prRootDirectory=" + prRootDirectory
				+ ", prFiles=" + prFiles + ", user=" + user + ", userAge="
				+ userAge + ", userType=" + userType + ", userPulls="
				+ userPulls + ", userAverages=" + userAverages
				+ ", userFollowers=" + userFollowers + ", userFollowing="
				+ userFollowing + ", userLocation=" + userLocation
				+ ", prType=" + prType + ", authorCommitsDays="
				+ authorCommitsDays + ", commitsByFilesDays="
				+ commitsByFilesDays + ", prParticipants="+prParticipants+"]";
	}


}
