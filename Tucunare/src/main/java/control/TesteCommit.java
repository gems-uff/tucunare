package control;

import java.net.UnknownHostException;

import com.mongodb.BasicDBObject;

public class TesteCommit {
	/*test contributors
	 * public static void main(String[] args) throws UnknownHostException {
		DB db = Connect.getInstance().getDB("ghtorrent");
		DBCollection dbc = db.getCollection("commits");
		BasicDBObject query = new BasicDBObject("commit.author.date", new BasicDBObject("$lt", "2015-06-13T00:00:00Z").append("$gt", "2014-09-14T00:00:00Z")); //consulta com data menor que a data do pull request		
		query.append("html_url", new BasicDBObject("$regex", "("+"angular"+")"));
		DBCursor cursor = dbc.find(query);
		System.out.println(cursor.size());
		ArrayList<String> listCommitters = new ArrayList<String>();
		for (DBObject dbObject : cursor) {
			BasicDBList listParents = (BasicDBList) dbObject.get("parents");
			if(listParents.size()==1){
				if(((BasicDBObject) ((BasicDBObject) dbObject.get("commit")).get("author")).get("email")!=null)
					if(!listCommitters.contains(((BasicDBObject) ((BasicDBObject) dbObject.get("commit")).get("author")).get("email").toString()))
						listCommitters.add(((BasicDBObject) ((BasicDBObject) dbObject.get("commit")).get("author")).get("email").toString());
			}	
		}
		System.out.println(listCommitters.size());
//		Collections.sort(listCommitters);
//		for (int i = 0; i < listCommitters.size(); i++) {
//			System.out.println(listCommitters.get(i));
//		}
	}
	*/
	
	public static void main(String[] args) throws UnknownHostException {
		//DB db = Connect.getInstance().getDB("ghtorrent");
		//DBCollection dbc = db.getCollection("commits");
		BasicDBObject query = new BasicDBObject("author.login", "gdi"); //consulta com data menor que a data do pull request		
		query.append("html_url", new BasicDBObject("$regex", "("+"angular/angular"+")"));
		//DBCursor o = dbc.find(query).limit(1);
		//String type="";
		//if (o.size()>0)
			//type = "core";
		//else 
			//type = "external";
//		System.out.println(o.size());
//		System.out.println(type);
	}
}
