package br.com.saude.api.social;

import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Logger;

import br.com.saude.api.social.dao.MongoJDBC;
import br.com.saude.api.social.model.Client;

/**
 * EloSocial é a classe principal do servidor. 
 * 
 * @author Scopus
 * 
 */
public class EloSocial {

	/**
	 * Variável de acesso ao logger da classe
	 */
	private static final Logger logger = Logger.getLogger("EloSocial");
	/**
	 * Identificação do APK registrado no Google
	 */
	private static final String SENDER_ID = "142734828433";
	/**
	 * Chave de acesso da API no Servidor do Google
	 */
	private static final String SERVER_API_KEY = "AIzaSyCuk8rOLWKO3O0Ah3aABw4NUA3VRCxHyL0";
	/**
	 * Nome definido para ser apresentado no Servidor do Google
	 */
	private static final String SERVICE_NAME = "Elo Social Server";
	/**
	 * Objeto da classe que gerencia todas as interações com os clientes
	 */
	private SocialGcmServer socialGcmServer;

	/**
	 * Método construtor
	 *  
	 * @param apiKey
	 * @param senderId
	 */
	public EloSocial(String apiKey, String senderId) {
		
		socialGcmServer = new SocialGcmServer(apiKey, senderId, SERVICE_NAME);
	}

	/**
	 * Método principal da classe
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		// Initialize FriendlyPingServer with appropriate API Key and SenderID.
		new EloSocial(SERVER_API_KEY, SENDER_ID);

		populateDB();
		
		// Keep main thread alive.
		CountDownLatch latch = new CountDownLatch(1);
		try {
			latch.await();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void populateDB(){
		
		System.out.println("Populating Mongo DB");
		MongoJDBC mongo = new MongoJDBC();	
		
		Client client = new Client();
		client.name = "Larry";
		client.registrationToken = SENDER_ID + "@gcm.googleapis.com";
		client.profilePictureUrl = "https://lh3.googleusercontent.com/-Y86IN-vEObo/AAAAAAAAAAI/AAAAAAADO1I/QzjOGHq5kNQ/photo.jpg?sz=50";
		
		System.out.println("Is Client add? "+mongo.saveClient(client));
	}
}
