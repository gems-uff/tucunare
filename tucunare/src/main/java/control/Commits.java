package util;

import java.util.Iterator;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;

public class Commits {
	 
	public static long getCommits(String shaHead, String shaBase){
		long numCommits = 1L;
		DB db = new Connect().getDB();
		DBCollection dbc = db.getCollection("commits");
		BasicDBObject queryHead = new BasicDBObject("sha",shaHead); //consulta com query
		DBObject commit = dbc.findOne(queryHead);
		BasicDBList list = (BasicDBList) commit.get("parents");
		if(list.isEmpty())
			return 1;
		String shaTemp = (String) ((BasicDBObject) list.get("0")).get("sha");
		while(!shaTemp.equals(shaBase)){
			numCommits++;
			queryHead = new BasicDBObject("sha",shaTemp);
			commit = dbc.findOne(queryHead);
			list = (BasicDBList) commit.get("parents");
			if(list.isEmpty())
				return 1;
			shaTemp = (String) ((BasicDBObject) list.get("0")).get("sha");
		}
		return numCommits;
	}
	//retorna a estatistica dos commits
	public static String getCommitStats(String shaHead, String shaBase){
		long totalLines = 0L, additions = 0L, deletions = 0L;
		DB db = new Connect().getDB();
		DBCollection dbc = db.getCollection("commits");
		BasicDBObject queryHead = new BasicDBObject("sha",shaHead); //consulta com query
		DBObject commit = dbc.findOne(queryHead);
		Integer totalTemp = (Integer) ((BasicDBObject)commit.get("stats")).get("total");
		Integer additionsTemp = (Integer) ((BasicDBObject)commit.get("stats")).get("additions");
		Integer deletionsTemp = (Integer) ((BasicDBObject)commit.get("stats")).get("deletions");
		String stats = totalTemp+", "+additionsTemp+", "+deletionsTemp;
		
		BasicDBList list = (BasicDBList) commit.get("parents");
		String shaTemp = (String) ((BasicDBObject) list.get("0")).get("sha");
		while(!shaTemp.equals(shaBase)){
			queryHead = new BasicDBObject("sha",shaTemp);
			commit = dbc.findOne(queryHead);
			list = (BasicDBList) commit.get("parents");
			if(list.isEmpty()){
				totalLines = 0;
				return stats;
			}
			Integer total = (Integer) ((BasicDBObject)commit.get("stats")).get("total");
			Integer add = (Integer) ((BasicDBObject)commit.get("stats")).get("additions");
			Integer del = (Integer) ((BasicDBObject)commit.get("stats")).get("deletions");
			totalLines += total;
			additions +=add;
			deletions += del;
			shaTemp = (String) ((BasicDBObject) list.get("0")).get("sha");
		}
		return (totalLines+totalTemp)+", "+(additions+additionsTemp)+", "+(deletions+deletionsTemp);
	}
	
	public static String getCommitsFiles(String shaHead, String shaBase){
		DB db = new Connect().getDB();
		DBCollection dbc = db.getCollection("commits");
		BasicDBObject queryHead = new BasicDBObject("sha",shaHead); //consulta com query
		DBObject commit = dbc.findOne(queryHead);
		
		
		BasicDBList list = (BasicDBList) commit.get("files");
		//Iterator<Object> arq = list.listIterator();
		String filesTemp="", files = "";
		long countFilesTemp = 0L, countFiles = 0L;
		for (Object object : list) {
			String [] temp = ((String) ((BasicDBObject) object).get("filename")).split("/");
			filesTemp += temp[temp.length-1]+"; ";
		}
		countFilesTemp = list.size();
		BasicDBList listCommit = (BasicDBList) commit.get("parents");
		String shaTemp = (String) ((BasicDBObject) listCommit.get("0")).get("sha");
		while(!shaTemp.equals(shaBase)){
			queryHead = new BasicDBObject("sha",shaTemp);
			commit = dbc.findOne(queryHead);
			listCommit = (BasicDBList) commit.get("parents");
			if(listCommit.isEmpty()){
				return countFilesTemp+"; "+filesTemp;
			}
			list = (BasicDBList) commit.get("files");
			for (Object object : list) {
				String [] temp = ((String) ((BasicDBObject) object).get("filename")).split("/");
				files += filesTemp+temp[temp.length-1]+"; ";
			}
			countFiles += countFilesTemp+list.size();
			shaTemp = (String) ((BasicDBObject) listCommit.get("0")).get("sha");
		}
		return (countFilesTemp+countFiles)+"; "+files+filesTemp;
	}
	/*
	//teste
	public static void main(String [] args){
		DB db = new Connect().getDB();
		DBCollection dbc = db.getCollection("commits");
		BasicDBObject queryHead = new BasicDBObject("sha","d7d52aaef2b034f5507209640f073fd575ae073a"); //consulta com query

		DBObject commit = dbc.findOne(queryHead);
		BasicDBList list = (BasicDBList) commit.get("parents");
		System.out.println(((BasicDBObject) list.get("0")).get("sha"));
	}
	*/
}
