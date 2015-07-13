package model;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public  class Settings {

	private String data;
	private boolean repoData;
	private int colaboratorDays;
	private boolean number, state, title, dates, lifeTime, closedMergedBy, shas; 
	private boolean assignee, comments, modifiedLines, changedFiles, dirFinal, filesPath;
	private boolean user, age, type, pullsUser, averages, followers,following, location;

	private JSONObject jo = null;

	private List<Integer> commitsDays;

	public Settings() {
		setDefaultValues();
	}

	//{	"repo":["true","10"],
	//"number":"true","state":"true","title":"true","dates":"true","lifetime":"true","closedmergedby":"true","shas":"true","assignee":"true",
	//"assignee":"true","comments":"true","commits":["10","5"],"modifiedlines":"true","changedfiles":"true","dirfinal":"true","filespath":"true",
	//"user":"true","age":"true","type":"true","pullsuser":"true","averages":"true","followers":"true","following":"true","location":"true"
	//}

	public Settings(String data){
		this.data = data;
	}

	public boolean tryParseValues(){
		if (data==null)
			return false;

		try {
			jo = new JSONObject(data);
		} catch (JSONException e) {
			setDefaultValues();
			System.err.println("String em formato errado.\n"+data);
			return false;
		}

		try {
			tryParseRepoData();
			tryParsePRCore();
			tryParsePRFiles();
			tryparseAuthorPR();
			return true;
		} catch (JSONException e) {
			setDefaultValues();
			return false;
		}
	}

	
	//"user":"true","age":"true","type":"true","pullsuser":"true","averages":"true","followers":"true","following":"true","location":"true"
	private void tryparseAuthorPR() throws JSONException {
		user = jo.getBoolean("user");
		age = jo.getBoolean("age");
		type = jo.getBoolean("type");
		pullsUser = jo.getBoolean("pullsuser");
		averages = jo.getBoolean("averages");
		followers = jo.getBoolean("followers");
		following = jo.getBoolean("following");
		location = jo.getBoolean("location");
	}

	//"comments":"true","commits":["10","5"],"modifiedlines":"true","changedfiles":"true","dirfinal":"true","filespath":"true",
	private boolean tryParsePRFiles() throws JSONException {
		JSONArray ja = jo.getJSONArray("commits"); 
		commitsDays = new ArrayList<Integer>();

		comments = jo.getBoolean("comments");
		
		commitsDays.add(ja.getInt(0));
		commitsDays.add(ja.getInt(1));
		
		modifiedLines = jo.getBoolean("modifiedlines");
		changedFiles = jo.getBoolean("changedfiles");
		dirFinal = jo.getBoolean("dirfinal");
		filesPath = jo.getBoolean("filespath");

		return true;
	}

	//"number":"true","state":"true","title":"true","dates":"true","lifetime":"true","closedmergedby":"true","shas":"true","assignee":"true",
	private void tryParsePRCore() throws JSONException {

		number = jo.getBoolean("number");
		state = jo.getBoolean("state");
		title = jo.getBoolean("title");
		dates = jo.getBoolean("dates");
		lifeTime = jo.getBoolean("lifetime");
		closedMergedBy = jo.getBoolean("closedmergedby");
		shas = jo.getBoolean("shas");
		assignee = jo.getBoolean("assignee");
	}

	//"repo":["true","10"],
	private void tryParseRepoData() throws JSONException {
		JSONArray ja = null;
		ja = jo.getJSONArray("repo");

		repoData = ja.getBoolean(0);
		colaboratorDays = ja.getInt(1);
	}

	public void setDefaultValues(){
		colaboratorDays = 10;
		commitsDays = new ArrayList<Integer>();
		commitsDays.add(10);
		commitsDays.add(10);
		
		repoData = true; 
		number = true; 
		state = true; 
		title = true; 
		dates = true; 
		lifeTime = true; 
		closedMergedBy = true; 
		shas = true; 
		assignee = true; 
		comments = true; 
		modifiedLines = true; 
		changedFiles = true; 
		dirFinal = true; 
		filesPath = true; 
		user = true; 
		age = true; 
		type = true; 
		pullsUser = true; 
		averages = true; 
		followers = true;
		following = true; 
		location = true;
		
	}

	public boolean isRepoData() {
		return repoData;
	}

	public void setRepoData(boolean repoData) {
		this.repoData = repoData;
	}

	public int getColaboratorDays() {
		return colaboratorDays;
	}

	public void setColaboratorDays(int colaboratorDays) {
		this.colaboratorDays = colaboratorDays;
	}

	public boolean isNumber() {
		return number;
	}

	public void setNumber(boolean number) {
		this.number = number;
	}

	public boolean isState() {
		return state;
	}

	public void setState(boolean state) {
		this.state = state;
	}

	public boolean isTitle() {
		return title;
	}

	public void setTitle(boolean title) {
		this.title = title;
	}

	public boolean isDates() {
		return dates;
	}

	public void setDates(boolean dates) {
		this.dates = dates;
	}

	public boolean isLifeTime() {
		return lifeTime;
	}

	public void setLifeTime(boolean lifeTime) {
		this.lifeTime = lifeTime;
	}

	public boolean isClosedMergedBy() {
		return closedMergedBy;
	}

	public void setClosedMergedBy(boolean closedMergedBy) {
		this.closedMergedBy = closedMergedBy;
	}

	public boolean isShas() {
		return shas;
	}

	public void setShas(boolean shas) {
		this.shas = shas;
	}

	public boolean isAssignee() {
		return assignee;
	}

	public void setAssignee(boolean assignee) {
		this.assignee = assignee;
	}

	public boolean isComments() {
		return comments;
	}

	public void setComments(boolean comments) {
		this.comments = comments;
	}

	public boolean isModifiedLines() {
		return modifiedLines;
	}

	public void setModifiedLines(boolean modifiedLines) {
		this.modifiedLines = modifiedLines;
	}

	public boolean isChangedFiles() {
		return changedFiles;
	}

	public void setChangedFiles(boolean changedFiles) {
		this.changedFiles = changedFiles;
	}

	public boolean isDirFinal() {
		return dirFinal;
	}

	public void setDirFinal(boolean dirFinal) {
		this.dirFinal = dirFinal;
	}

	public boolean isFilesPath() {
		return filesPath;
	}

	public void setFilesPath(boolean filesPath) {
		this.filesPath = filesPath;
	}

	public boolean isUser() {
		return user;
	}

	public void setUser(boolean user) {
		this.user = user;
	}

	public boolean isAge() {
		return age;
	}

	public void setAge(boolean age) {
		this.age = age;
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

	public boolean isFollowers() {
		return followers;
	}

	public void setFollowers(boolean followers) {
		this.followers = followers;
	}

	public boolean isFollowing() {
		return following;
	}

	public void setFollowing(boolean following) {
		this.following = following;
	}

	public boolean isLocation() {
		return location;
	}

	public void setLocation(boolean location) {
		this.location = location;
	}

	public List<Integer> getCommitsDays() {
		return commitsDays;
	}

	public void setCommitsDays(List<Integer> commitsDays) {
		this.commitsDays = commitsDays;
	}
	
	
}
