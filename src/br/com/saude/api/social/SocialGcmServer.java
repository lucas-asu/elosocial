package br.com.saude.api.social;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jivesoftware.smack.util.StringUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import br.com.saude.api.social.gcm.GcmServer;
import br.com.saude.api.social.model.Client;
import br.com.saude.api.social.model.Message;
import br.com.saude.api.social.util.Constants;

/**
 * SocialGcmServer é a classe responsável de tratar todos os 
 * pings enviados pelo cliente através do Servidor GCM
 * 
 * @author Scopus
 *
 */
public class SocialGcmServer extends GcmServer {
	
	/**
	 * Identificação do APK registrado no Google
	 */
	private String SENDER_ID;
	/**
	 * Chave de acesso da API no Servidor do Google
	 */
	private String SERVER_API_KEY;
	/**
	 * Variável de acesso ao logger da classe
	 */
	private static final Logger logger = Logger.getLogger("SocialGcmServer");
	/**
	 * Gson é um objeto da classe que auxiliar o tratamento do JSON enviado e recebido pelo Servidor.
	 */
	private Gson gson;
	/**
	 * Map com os clientes registrados com o Servidor.
	 */
	private Map<String, Client> clientMap;
	/**
	 * Lista de clientes
	 */
	private ArrayList<Client> clientList;
	/**
	 * Lista de mensagens 
	 */
	private ArrayList<Message> messageList;

	/**
	 * Método Construtor 
	 * 
	 * @param apiKey
	 * @param senderId
	 * @param serviceName
	 */
	public SocialGcmServer(String apiKey, String senderId, String serviceName) {
		super(apiKey, senderId, serviceName);
		
		SERVER_API_KEY = apiKey;
		SENDER_ID = senderId;
		
		clientList = new ArrayList<Client>();
		messageList = new ArrayList<Message>();
		Collections.synchronizedList(clientList);
		Collections.synchronizedList(messageList);
		clientMap = new ConcurrentHashMap<String, Client>();

		Client serverClient = createServerClient();
		clientMap.put(serverClient.registrationToken, serverClient);
		clientList.add(serverClient);

		gson = new GsonBuilder().create();
	}
	
	/**
	 * Método que cria um objeto da classe Cliente para ser utilizada
	 * nos pings com o Servidor.
	 *
	 * @return Server Client.
	 */
	private Client createServerClient() {
		Client client = new Client();
		client.name = "Larry";
		client.registrationToken = SENDER_ID + "@gcm.googleapis.com";
		client.profilePictureUrl = "https://lh3.googleusercontent.com/-Y86IN-vEObo/AAAAAAAAAAI/AAAAAAADO1I/QzjOGHq5kNQ/photo.jpg?sz=50";
		return client;
	}

	
	@Override
	public void onMessage(String from, JsonObject jData) {

		Client client = checkClientObject(jData);

		if (jData.has(Constants.ACTION_KEY)) {
			String action = jData.get(Constants.ACTION_KEY).getAsString();
			if (action.equals(Constants.REGISTER_NEW_CLIENT)) {
				registerNewClient(jData);
			} else if (action.equals(Constants.PING_CLIENT)) {
				if (StringUtils.isNotEmpty(client.destinationToken)
						&& StringUtils.isNotEmpty(client.destinationToken)) {
					pingClient(client.destinationToken, client.registrationToken, null);
				} else {
					logger.info("Unable to ping unless to and sender tokens are available.");
				}
			} else if (action.equals(Constants.USER_LIST)) {
				sendContactList(client.registrationToken, client);
			} else if (action.equals(Constants.SHARE_MESSAGE)) {
				sendClientMessage(client.destinationToken, client.registrationToken, client, Constants.SHARE_MESSAGE);
			} else if (action.equals(Constants.LIKE_MESSAGE)) {
				String destinationToken = getTokenByIdMessage(client);
				sendClientMessage((StringUtils.isEmpty(destinationToken) ? client.destinationToken : destinationToken),
						client.registrationToken, client, Constants.LIKE_MESSAGE);

			} else if (action.equals(Constants.POST_TIMELINE)) {
				sendClientMessage(client.registrationToken, client.registrationToken, client, Constants.POST_TIMELINE);
			}
		} else {
			logger.info("No action found. Message received missing action.");
		}
	}

	/**
	 * Método de validação do Cliente 
	 * 
	 * @param jData - objeto JSON 
	 * @return client - objeto do Cliente
	 */
	private Client checkClientObject(JsonObject jData) {
		String destinationToken = (jData.get("to") == null ? "" : jData.get("to").getAsString()); // destination
		// token
		String senderToken = (jData.get("sender") == null ? "" : jData.get("sender").getAsString()); // sender
		// token
		String contentMessage = (jData.get("content_message") == null ? ""
				: jData.get("content_message").getAsString());
		String name = (jData.get("name") == null ? "" : jData.get("name").getAsString());
		String url = (jData.get("profile_picture_url") == null ? "" : jData.get("profile_picture_url").getAsString());
		String idMessage = (jData.get("id_message") == null ? "" : jData.get("id_message").getAsString());
		String idMessageLocal = (jData.get("id_message_local") == null ? ""
				: jData.get("id_message_local").getAsString());

		return loadObjectClient(name, url, senderToken, idMessage, contentMessage, destinationToken, idMessageLocal);
	}

	/**
	 * Método que registra um novo Cliente com os dados recebidos pelo Servidor por JSON, 
	 * realiza um broadcast para avisar que um novo Cliente foi registrado e
	 * envia a lista atualizada de clientes.
	 *
	 * @param jData - JSON data containing properties of new Client. 
	 */
	private void registerNewClient(JsonObject jData) {
		Client newClient = gson.fromJson(jData, Client.class);
		if (newClient.isValid()) {
			addClient(newClient);
			broadcastNewClient(newClient);
			sendClientList(newClient);
		} else {
			logger.log(Level.WARNING, "Could not unpack received data into a Client.");
		}
	}

	/**
	 * Método responsável por carregar as informações do cliente em um objeto.
	 * 
	 * @param name
	 * @param profilePictureUrl
	 * @param registrationToken
	 * @param idMessage
	 * @param contentMessage
	 * @param destinationToken
	 * @param idMessageLocal
	 * @return
	 */
	private Client loadObjectClient(String name, String profilePictureUrl, String registrationToken, String idMessage,
			String contentMessage, String destinationToken, String idMessageLocal) {
		Client client = new Client();
		client.name = name;
		client.registrationToken = registrationToken;
		client.profilePictureUrl = profilePictureUrl;
		client.idMessage = idMessage;
		client.contentMessage = contentMessage;
		client.destinationToken = destinationToken;
		client.idMessageLocal = idMessageLocal;
		return client;

	}

	/**
	 * Método de LOG de uma mensagem de teste
	 * 
	 * @param jData
	 */
	private void logMessageTest(JsonObject jData) {
		System.out.println(jData.get("name").getAsString());
		System.out.println(jData.get("profile_picture_url").getAsString());
		System.out.println(jData.get("to").getAsString()); // destination token
		System.out.println(jData.get("sender").getAsString()); // sender token
		System.out.println(jData.get("content_message").getAsString());
		System.out.println(jData.get("id_message").getAsString());
	}

	/**
	 * Método que adiciona um cliente em uma lista e Map
	 *
	 * @param client
	 *            Client to be added.
	 */
	private void addClient(Client client) {
		clientMap.put(client.registrationToken, client);
		clientList.add(client);
	}

	/**
	 * Método que envia o broadcast do novo cliente registrado.
	 * 
	 * Broadcast the newly registered client to clients that have already been
	 * registered. The broadcast is sent via the PubSub topic "/topics/newuser"
	 * all registered clients should be subscribed to this topic.
	 *
	 * @param client
	 *            Newly registered client.
	 */
	private void broadcastNewClient(Client client) {
		JsonObject jBroadcast = new JsonObject();

		JsonObject jData = new JsonObject();
		jData.addProperty(Constants.ACTION_KEY, Constants.BROADCAST_NEW_CLIENT);

		JsonObject jClient = gson.toJsonTree(client).getAsJsonObject();
		jData.add(Constants.CLIENT_KEY, jClient);

		jBroadcast.add(Constants.DATA_KEY, jData);
		send(Constants.NEW_CLIENT_TOPIC, jBroadcast);
	}

	/**
	 * Método que envia uma nova lista de clientes
	 * 
	 * Send client list to newly registered client. When a new client is
	 * registered, that client must be informed about the other registered
	 * clients.
	 *
	 * @param client
	 *            Newly registered client.
	 */
	private void sendClientList(Client client) {

		JsonElement clientElements = gson.toJsonTree(clientList, new TypeToken<Collection<Client>>() {
		}.getType());
		if (clientElements.isJsonArray()) {
			JsonObject jSendClientList = new JsonObject();

			JsonObject jData = new JsonObject();
			jData.addProperty(Constants.ACTION_KEY, Constants.SEND_CLIENT_LIST);
			jData.add(Constants.CLIENTS_KEY, clientElements);

			jSendClientList.add(Constants.DATA_KEY, jData);
			send(client.registrationToken, jSendClientList);
		}
	}

	/**
	 * Método que envia uma lista de clientes para o GCM
	 * @param tokenAddress
	 * @param client
	 */
	private void sendContactList(String tokenAddress, Client client) {

		if (clientMap.size() != clientList.size()) {
			for (Entry<String, Client> clientEntry : clientMap.entrySet()) {
				Client currentClient = clientEntry.getValue();
				if (!currentClient.registrationToken.equals(client.registrationToken)) {
					clientList.add(client);
					break;
				}
			}
		}

		JsonElement clientElements = gson.toJsonTree(clientList, new TypeToken<Collection<Client>>() {
		}.getType());
		if (clientElements.isJsonArray()) {
			JsonObject jSendClientList = new JsonObject();

			JsonObject jData = new JsonObject();
			jData.addProperty(Constants.ACTION_KEY, Constants.USER_LIST);
			jData.add(Constants.CLIENTS_KEY, clientElements);

			jSendClientList.add(Constants.DATA_KEY, jData);
			send(tokenAddress, jSendClientList);
		}
	}

	/**
	 * Send message to Client with matching toToken. The validity of to and
	 * sender tokens should be check before this method is called.
	 *
	 * @param toToken
	 *            Token of recipient of ping.
	 * @param senderToken
	 *            Token of sender of ping.
	 */
	private void pingClient(String toToken, String senderToken, Client client) {
		Client senderClient;
		// If the server is the recipient of the ping, send ping to sender,
		// otherwise send ping to
		// toToken.
		if (toToken.equals(SENDER_ID + "@" + Constants.GCM_HOST)) {
			senderClient = clientMap.get(toToken);
			toToken = senderToken;
		} else {
			senderClient = clientMap.get(senderToken);
		}
		JsonObject jPing = new JsonObject();

		JsonObject jData = new JsonObject();
		jData.addProperty(Constants.ACTION_KEY, Constants.PING_CLIENT);
		jData.addProperty(Constants.SENDER_KEY, senderClient.registrationToken);

		// Create notification that is handled appropriately on the receiving
		// platform.

		JsonObject jNotification = new JsonObject();
		jNotification.addProperty("id_message", getRandomMessageId());
		jNotification.addProperty("content_message", senderClient.name + " is pinging you.");
		jNotification.addProperty("sender", senderToken);
		jNotification.addProperty("to", toToken);
		jNotification.addProperty("title", Constants.PING_TITLE);
		jNotification.addProperty("icon", Constants.PING_ICON);
		jNotification.addProperty("sound", "default");
		jNotification.addProperty("click_action", Constants.CLICK_ACTION);

		jPing.add(Constants.DATA_KEY, jData);
		jPing.add("notification", jNotification);

		send(toToken, jPing);
	}

	/**
	 * 
	 * @param toToken
	 * @param senderToken
	 * @param client
	 * @param action
	 */
	private void sendClientMessage(String toToken, String senderToken, Client client, String action) {
		Client senderClient;
		String contentMessage = null;
		Message message = new Message();
		// If the server is the recipient of the ping, send ping to sender,
		// otherwise send ping to
		// toToken.
		if (toToken.equals(SENDER_ID + "@" + Constants.GCM_HOST)) {
			senderClient = clientMap.get(toToken);
			toToken = senderToken;
		} else {
			senderClient = clientMap.get(senderToken);
		}

		JsonObject jData = new JsonObject();
		jData.addProperty(Constants.ACTION_KEY, action);
		jData.addProperty(Constants.SENDER_KEY, senderClient.registrationToken);

		// Create notification that is handled appropriately on the receiving
		// platform.

		if (messageList.size() == 0) {
			message.setContentMessage(client.contentMessage);
		} else {
			for (Message messageItem : messageList) {
				if (StringUtils.isNotEmpty(messageItem.getIdMessage())
						&& messageItem.getIdMessage().equals(client.idMessage)) {
					contentMessage = messageItem.getContentMessage();
					message.setContentMessage(contentMessage);
				}
			}
		}

		if (action.equals(Constants.POST_TIMELINE)) {
			String idMessage = getRandomMessageId();
			jData.addProperty("id_message", idMessage);
			message.setIdMessage(idMessage);
			message.setContentMessage(client.contentMessage);
			message.setRegistrationTokenId(client.registrationToken);
		} else {
			message.setIdMessage(client.idMessage);
			message.setRegistrationTokenId(senderToken);
			jData.addProperty("id_message", client.idMessage);
		}
		jData.addProperty("id_message_local", client.idMessageLocal);
		jData.addProperty("content_message", contentMessage);
		jData.addProperty("sender", senderToken);
		jData.addProperty("to", toToken);

		message.setIdMessageLocal(client.idMessageLocal);
		storeMessage(message);

		JsonObject jPing = new JsonObject();
		jPing.add(Constants.DATA_KEY, jData);

		send(toToken, jPing);
	}

	/**
	 * 
	 * @param message
	 */
	private void storeMessage(Message message) {
		messageList.add(message);
	}

	/**
	 * Retorna um ID randomico da mensagem
	 * @return
	 */
	private String getRandomMessageId() {
		return Long.toString(System.currentTimeMillis() * 100);
	}

	/**
	 * Método para pegar o token pelo ID da mensagem
	 * 
	 * @param client
	 * @return
	 */
	private String getTokenByIdMessage(Client client) {
		String destinationToken = null;

		for (Message messageItem : messageList) {
			if (StringUtils.isNotEmpty(messageItem.getIdMessage())
					&& messageItem.getIdMessage().equals(client.idMessage)) {
				destinationToken = messageItem.getRegistrationTokenId();
			}
		}
		return destinationToken;
	}

}
