package br.com.saude.api.social.dao;

import java.net.UnknownHostException;

import javax.swing.text.Document;

import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;

import br.com.saude.api.social.model.Client;

public class MongoJDBC {

	public String USER_DB = "admin";
	public String PASSWORD_DB = "Xvt-4Diz1aNF";
	public MongoClient mongoClient;
	public MongoDatabase mongoDatabase; 
	
	

	public MongoJDBC() {

//		try {
			// To connect to mongodb server
			mongoClient = new MongoClient("127.7.141.130", 27017);

			// Now connect to your databases
			mongoDatabase = mongoClient.getDatabase("espsaude");
			System.out.println("Connect to database successfully");
		
//			MongoCredential mongoClient.getCredentialsList()
			
//			MongoCollection<Documen> coll = mongoDatabase.getCollection("social.clientes");

//		} catch (UnknownHostException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} 

	}
	
	public boolean saveClient(Client cli){
//		Document doc = new BasicDBObject("name", cli.name)
//		        .append("registration_token", cli.registrationToken)
//		        .append("profile_picture_url", cli.profilePictureUrl)
//		        .append("id_message", cli.idMessage)
//		        .append("id_message_local", cli.idMessageLocal)
//		        .append("content_message", cli.contentMessage)
//		        .append("destination_token", cli.destinationToken);
//		coll.insert(doc);
		return true;
	}
	
	

}
