package model;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public  class Settings {

	private JSONObject data;
	private boolean repoData, contributors;
	private int contributorsMonths = -1;
	private boolean prNumber, prState, title, prDates, prLifeTime, prClosedMergedBy, prShas; 
	private boolean prAssignee, prComments, prCommits, prModifiedLines, prChangedFiles, prDirFinal, prFiles;
	private boolean user, userAge, type, pullsUser, averages, userFollowers, userFollowing, userLocation; 
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
			user = data.getBoolean("user");
			userAge = data.getBoolean("age");
			type = data.getBoolean("type");
			pullsUser = data.getBoolean("pullsuser");
			averages = data.getBoolean("averages");
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
			prDirFinal = data.getBoolean("dirfinal");
			prFiles = data.getBoolean("files");
	}

	//"number":"true","state":"true","title":"true","dates":"true","lifetime":"true","closedmergedby":"true","shas":"true","assignee":"true",
	private void tryParsePRCore() throws JSONException {
			prType = data.getInt("prtype");
			prNumber = data.getBoolean("number");
			prState = data.getBoolean("state");
			title = data.getBoolean("title");
			prDates = data.getBoolean("dates");
			prLifeTime = data.getBoolean("lifetime");
			prClosedMergedBy = data.getBoolean("closedmergedby");
			prShas = data.getBoolean("shas");
			prAssignee = data.getBoolean("assignee");
	}

	//"repo":"true",
	private void tryParseRepoData() throws JSONException {
			repoData = data.getBoolean("repo");
			contributors = data.getBoolean("contributors");
	}

	public void setDefaultValues(){
		prType = 0;
		contributorsMonths = 1;
		authorCommitsDays = 7;
		commitsByFilesDays = 7;

		repoData = true; 
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
		prDirFinal = true; 
		prFiles = true; 
		user = true; 
		userAge = true; 
		type = true; 
		pullsUser = true; 
		averages = true; 
		userFollowers = true;
		userFollowing = true; 
		userLocation = true;

	}

	public boolean isRepoData() {
		return repoData;
	}

	public void setRepoData(boolean repoData) {
		this.repoData = repoData;
	}

	public boolean isContributors() {
		return contributors;
	}

	public void setContributors(boolean contributors) {
		this.contributors = contributors;
	}

	public int getContributorsMonths() {
		return contributorsMonths;
	}

	public void setContributorsMonths(int contributorsMonths) {
		this.contributorsMonths = contributorsMonths;
	}

	public boolean isPrNumber() {
		return prNumber;
	}

	public void setPrNumber(boolean prNumber) {
		this.prNumber = prNumber;
	}

	public boolean isPrState() {
		return prState;
	}

	public void setPrState(boolean prState) {
		this.prState = prState;
	}

	public boolean isTitle() {
		return title;
	}

	public void setTitle(boolean title) {
		this.title = title;
	}

	public boolean isPrDates() {
		return prDates;
	}

	public void setPrDates(boolean prDates) {
		this.prDates = prDates;
	}

	public boolean isPrLifeTime() {
		return prLifeTime;
	}

	public void setPrLifeTime(boolean prLifeTime) {
		this.prLifeTime = prLifeTime;
	}

	public boolean isPrClosedMergedBy() {
		return prClosedMergedBy;
	}

	public void setPrClosedMergedBy(boolean prClosedMergedBy) {
		this.prClosedMergedBy = prClosedMergedBy;
	}

	public boolean isPrShas() {
		return prShas;
	}

	public void setPrShas(boolean prShas) {
		this.prShas = prShas;
	}

	public boolean isPrAssignee() {
		return prAssignee;
	}

	public void setPrAssignee(boolean prAssignee) {
		this.prAssignee = prAssignee;
	}

	public boolean isPrComments() {
		return prComments;
	}

	public void setPrComments(boolean prComments) {
		this.prComments = prComments;
	}

	public boolean isPrCommits() {
		return prCommits;
	}

	public void setPrCommits(boolean prCommits) {
		this.prCommits = prCommits;
	}

	public boolean isPrModifiedLines() {
		return prModifiedLines;
	}

	public void setPrModifiedLines(boolean prModifiedLines) {
		this.prModifiedLines = prModifiedLines;
	}

	public boolean isPrChangedFiles() {
		return prChangedFiles;
	}

	public void setPrChangedFiles(boolean prChangedFiles) {
		this.prChangedFiles = prChangedFiles;
	}

	public boolean isPrDirFinal() {
		return prDirFinal;
	}

	public void setPrDirFinal(boolean prDirFinal) {
		this.prDirFinal = prDirFinal;
	}

	public boolean isPrFiles() {
		return prFiles;
	}

	public void setPrFiles(boolean prFiles) {
		this.prFiles = prFiles;
	}

	public boolean isUser() {
		return user;
	}

	public void setUser(boolean user) {
		this.user = user;
	}

	public boolean isUserAge() {
		return userAge;
	}

	public void setUserAge(boolean userAge) {
		this.userAge = userAge;
	}

	public boolean isType() {
		return type;
	}

	public void setType(boolean type) {
		this.type = type;
	}

	public boolean isPullsUser() {
		return pullsUser;
	}

	public void setPullsUser(boolean pullsUser) {
		this.pullsUser = pullsUser;
	}

	public boolean isAverages() {
		return averages;
	}

	public void setAverages(boolean averages) {
		this.averages = averages;
	}

	public boolean isUserFollowers() {
		return userFollowers;
	}

	public void setUserFollowers(boolean userFollowers) {
		this.userFollowers = userFollowers;
	}

	public boolean isUserFollowing() {
		return userFollowing;
	}

	public void setUserFollowing(boolean userFollowing) {
		this.userFollowing = userFollowing;
	}

	public boolean isUserLocation() {
		return userLocation;
	}

	public void setUserLocation(boolean userLocation) {
		this.userLocation = userLocation;
	}

	public int getPrType() {
		return prType;
	}

	public void setPrType(int prType) {
		this.prType = prType;
	}
	
	public int getAuthorCommitsDays() {
		return authorCommitsDays;
	}
	
	public void setAuthorCommitsDays(int days) {
		authorCommitsDays = days;
	}
	
	public int getCommitsByFilesDays() {
		return commitsByFilesDays;
	}
	
	public void setCommitsByFilesDays(int days) {
		commitsByFilesDays = days;
	}

	@Override
	public String toString() {
		return "Settings [repoData=" + repoData + ", contributors="
				+ contributors + ", contributorsMonths=" + contributorsMonths
				+ ", prNumber=" + prNumber + ", prState=" + prState
				+ ", title=" + title + ", prDates=" + prDates + ", prLifeTime="
				+ prLifeTime + ", prClosedMergedBy=" + prClosedMergedBy
				+ ", prShas=" + prShas + ", prAssignee=" + prAssignee
				+ ", prComments=" + prComments + ", prCommits=" + prCommits
				+ ", prModifiedLines=" + prModifiedLines + ", prChangedFiles="
				+ prChangedFiles + ", prDirFinal=" + prDirFinal + ", prFiles="
				+ prFiles + ", user=" + user + ", userAge=" + userAge
				+ ", type=" + type + ", pullsUser=" + pullsUser + ", averages="
				+ averages + ", userFollowers=" + userFollowers
				+ ", userFollowing=" + userFollowing + ", userLocation="
				+ userLocation + ", prType=" + prType + ", commitsByFilesDays=" + commitsByFilesDays + ", authorCommitsDays="+authorCommitsDays+"]";
	}
}
