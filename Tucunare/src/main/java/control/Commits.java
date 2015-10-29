package control;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import util.Connect;
import util.FormatDate;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

public class Commits {
//	Caminho completos dos arquivos alterados pelo pull request
	public static String getCommitsFilesPath(String shaHead, String shaBase, Integer commits, Integer filesPull) throws UnknownHostException{
		DB db = Connect.getInstance().getDB("ghtorrent");
		DBCollection dbcCommits = db.getCollection("commits");
		List<String> files = new ArrayList<String>();
		String shaTemp = shaHead;
		while(!shaTemp.equals(shaBase) && commits!=0){
			commits--;
			BasicDBObject queryHead = new BasicDBObject("sha",shaTemp);
			DBObject commit = null;
			BasicDBList listCommit = null, listFiles = null;
			try {
				BasicDBObject fields = new BasicDBObject();
				fields.put("parents", 1);
				fields.put("files", 1);
				fields.put("sha", 1);
				fields.put("_id", 0);
				commit = dbcCommits.findOne(queryHead,fields);
				listCommit = (BasicDBList) commit.get("parents");
				listFiles = (BasicDBList) commit.get("files");	
				for (Object object : listFiles) {
					if(!files.contains((String) ((BasicDBObject) object).get("filename")) && files.size() < filesPull)
						files.add((String) ((BasicDBObject) object).get("filename"));
				}
				if(listCommit.get("0")!=null)
					shaTemp = (String) ((BasicDBObject) listCommit.get("0")).get("sha");
				else
					break;
			} catch (NullPointerException npe) {
				System.err.println("erro ao consultar o pull de commit Head "+shaHead +" no commit "+shaTemp);
				return "";
			}
		}
		return files.toString();
	}
//	Desenvolvedor com a maior quantidade de commits nos arquivos enviados pelo pull request
	public static String getAuthorCommits(String filesNames, String shaBase, String repo, Integer days) throws UnknownHostException{
		DB db = Connect.getInstance().getDB("ghtorrent");
		DBCollection commitsC = db.getCollection("commits");
		String files[] = filesNames.split(", ");
		ArrayList<String> authors = new ArrayList<String>();
		ArrayList<String> id = new ArrayList<String>();
		List<DBObject> temp;
		Arrays.sort(files);
		int count=0,max=Integer.MIN_VALUE;
		String author="";
		for (int i=0 ; i<files.length ; i++) {
			BasicDBObject queryBaseCommit = new BasicDBObject("sha", shaBase);
			DBObject baseCommitPull = commitsC.findOne(queryBaseCommit);
			String data = FormatDate.dataLimit(((BasicDBObject) ((BasicDBObject) baseCommitPull.get("commit")).get("committer")).get("date").toString(), days);
			BasicDBObject query = new BasicDBObject("commit.committer.date", new BasicDBObject("$lt",((BasicDBObject)((BasicDBObject) baseCommitPull.get("commit")).get("committer")).get("date").toString()).append("$gt", data)); //consulta com data menor que a data do pull request
			query.append("html_url", new BasicDBObject("$regex", "("+repo+")"));
			
			query.append("files.filename", files[i]);
			//commitsC.ensureIndex("files.filename");
			
//			BasicDBObject eleMatch = new BasicDBObject("filename",files[i]);
//			BasicDBObject up = new BasicDBObject("$elemMatch",eleMatch);
			BasicDBObject fields = new BasicDBObject();
			fields.put("committer.login",1);
			//fields.put("_id",0);
//			fields.put("filename",up);
			temp = commitsC.find(query,fields).toArray();
			for (DBObject dbObject : temp) {
				if(((BasicDBObject)dbObject.get("committer")) != null & !id.contains(dbObject.get("_id").toString())){
					authors.add(((BasicDBObject)dbObject.get("committer")).get("login").toString());
					id.add(dbObject.get("_id").toString());
				}	
			}
		}
		Collections.sort(authors);
		for (int j=0; j < authors.size(); j++){  
			count = Collections.frequency(authors, authors.get(j));
			if(count>max){
				max=count;
				author=authors.get(j);
			}   
		} 
		return author;
	}
//	Quantidade de commits nos arquivos enviados pelo pull request
	public static int getCommitsByFiles (String filesNames, String pullRequestDate, String repo, Integer days) throws UnknownHostException{
		String files[] = filesNames.split(", ");
		int numCommitsNoArquivo = 0;
		DB db = Connect.getInstance().getDB("ghtorrent");
		DBCollection commitsC = db.getCollection("commits");
		String data = FormatDate.dataLimit(pullRequestDate, days);
		BasicDBObject queryHead = new BasicDBObject("commit.committer.date", new BasicDBObject("$lt",pullRequestDate).append("$gt", data)); //consulta com data menor que a data do pull request
		queryHead.append("html_url", new BasicDBObject("$regex", "("+repo+")"));

		ArrayList<String> list = new ArrayList<String>();
		Arrays.sort(files);
		BasicDBObject fields = new BasicDBObject();
		fields.put("files", 1);
		fields.put("_id", 0);
		DBCursor c = commitsC.find(queryHead);
		for (DBObject dbo: c)
			if((BasicDBObject)dbo != null){	
				BasicDBList commitFilesList = (BasicDBList) dbo.get("files");
				for (Object object : commitFilesList)
					if((BasicDBObject)object != null)
						if(((BasicDBObject) object).get("filename") != null)
							list.add((String) ((BasicDBObject) object).get("filename"));	
			}
		Collections.sort(list);
		for (int i=0 ; i<files.length ; i++) 
			numCommitsNoArquivo += Collections.frequency(list, files[i]);
		return numCommitsNoArquivo;
	}

	//não utilizamos mais - V1
	public static String getContributors(String shaBase, String repo, String owner, Integer days) throws UnknownHostException{
		DB db = Connect.getInstance().getDB("ghtorrent");
		DBCollection commitsC = db.getCollection("commits");
		BasicDBObject queryBaseCommit = new BasicDBObject("sha", shaBase);
		DBObject baseCommitPull = commitsC.findOne(queryBaseCommit);
		String data = FormatDate.dataLimit(((BasicDBObject) ((BasicDBObject) baseCommitPull.get("commit")).get("committer")).get("date").toString(), days);
		BasicDBObject query = new BasicDBObject("commit.committer.date", new BasicDBObject("$lt",((BasicDBObject)((BasicDBObject) baseCommitPull.get("commit")).get("author")).get("date").toString()).append("$gt", data)); //consulta com data menor que a data do pull request
		query.append("html_url", new BasicDBObject("$regex", "("+owner+"/"+repo+")"));
		BasicDBObject fields = new BasicDBObject();
		fields.put("committer.login", 1);
		fields.put("_id", 0);
		Object[] contributorsList = commitsC.distinct("committer.login", query).toArray();
		return ""+contributorsList.length;
	}

	//retorna os contribuidores para coleta dos dados sociais (V1)
	public static Object [] getContributorsList(String repo, String owner) throws UnknownHostException{
		DB db = Connect.getInstance().getDB("ghtorrent");
		DBCollection commitsC = db.getCollection("commits");
		BasicDBObject queryCommit = new BasicDBObject("html_url", new BasicDBObject("$regex", "("+owner+"/"+repo+")"));
		BasicDBObject fields = new BasicDBObject();
		fields.put("committer.login", 1);
		fields.put("_id", 0);
		Object[] contributorsList = commitsC.distinct("committer.login", queryCommit).toArray();
		return contributorsList;
	}

	//retorna os contribuidores para coleta dos dados sociais V1
	public static ArrayList<String> getCoreTeamList(String repo,String owner) throws UnknownHostException{
		DB db = Connect.getInstance().getDB("ghtorrent");
		DBCollection issueC = db.getCollection("issues");
		BasicDBObject queryIssue = new BasicDBObject("repo", repo);
		queryIssue.append("pull_request", new BasicDBObject("$exists", true));
		queryIssue.append("owner", owner);
		queryIssue.append("closed_by", new BasicDBObject("$not", new BasicDBObject("$type", 10)));
		BasicDBObject fields = new BasicDBObject();
		fields.put("closed_by.login", 1);
		fields.put("user.login", 1);
		fields.put("pull_request.url", 1);
		fields.put("_id", 0);
//		primeira forma
		DBCursor teste =  issueC.find(queryIssue,fields);
		ArrayList<String> list = new ArrayList<String>();
		for (DBObject dbObject : teste) {
			String closed_by = (((BasicDBObject)dbObject.get("closed_by")).get("login").toString());
			String user = (((BasicDBObject)dbObject.get("user")).get("login").toString());
			if(((BasicDBObject)dbObject.get("closed_by"))!=null) 
				if(!closed_by.equals(user))
					if(!list.contains((((BasicDBObject)dbObject.get("closed_by")).get("login").toString())))
						list.add((((BasicDBObject)dbObject.get("closed_by")).get("login").toString()));
		}
//		Object[] coreTeamList = list.toArray();
//		Segunda forma
//		Object[] coreTeamList2 = issueC.distinct("closed_by.login", queryIssue).toArray();
		Collections.sort(list);
		return list;
	}


	//type developer V1
	public static String getTypeDeveloper(String user, String repo, String owner) throws UnknownHostException{
		DB db = Connect.getInstance().getDB("ghtorrent");
		DBCollection dbc = db.getCollection("commits");
		BasicDBObject query = new BasicDBObject("committer.login", user); 		
		query.append("html_url", new BasicDBObject("$regex", "("+owner+"/"+repo+")"));
		DBCursor o = dbc.find(query).limit(1);
		String type="";
		if (o.size()>0)
			type = "core";
		else 
			type = "external";
		return type;
	}
//	Tipo de desenvolvedor
	public static String getTypeDeveloper3(String user, String repo, String owner) throws UnknownHostException{
		DB db = Connect.getInstance().getDB("ghtorrent");
		DBCollection dbcCommit= db.getCollection("commits");
		BasicDBObject queryCommit = new BasicDBObject("committer.login", user); 		
		queryCommit.append("html_url", new BasicDBObject("$regex", "("+owner+"/"+repo+")"));
		BasicDBObject fieldsCommit = new BasicDBObject();
		fieldsCommit.put("sha", 1);
		fieldsCommit.put("_id", 0);
		DBCollection dbcPull = db.getCollection("pull_requests");
		BasicDBObject fieldsPull = new BasicDBObject();
		fieldsPull.put("head.sha", 1);
		fieldsPull.put("_id", 0);
		BasicDBObject queryPull = new BasicDBObject("repo",repo);
		queryPull.append("owner", owner);
		DBCursor objListCommit = dbcCommit.find(queryCommit,fieldsCommit);
		
		String type="";
		for (DBObject dbObject : objListCommit) {
			queryPull.append("head.sha", dbObject.get("sha").toString());
			DBObject objSHA =  dbcPull.findOne(queryPull,fieldsPull);
			String sha="";
			if(objSHA != null)
				sha = (((BasicDBObject)objSHA.get("head")).get("sha").toString());
			if(!dbObject.get("sha").toString().equals(sha))
				type = "core";
			else 
				type = "external";
		}
		return type;
	}
}
