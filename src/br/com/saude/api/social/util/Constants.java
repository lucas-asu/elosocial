package br.com.saude.api.social.util;

/**
 * 
 * 
 * @author Scopus
 *
 */
public interface Constants {

	//------------------ Actions
	
	/**
	 * 
	 */
	static final String REGISTER_NEW_CLIENT = "register_new_client";
	/**
	 * 
	 */
	static final String BROADCAST_NEW_CLIENT = "broadcast_new_client";
	/**
	 * 
	 */
	static final String SEND_CLIENT_LIST = "send_client_list";
	/**
	 * 
	 */
	static final String PING_CLIENT = "ping_client";
	/**
	 * 
	 */
	static final String USER_LIST = "user_list";
	/**
	 * 
	 */
	static final String SHARE_MESSAGE = "share_message";
	/**
	 * 
	 */
	static final String LIKE_MESSAGE = "like_message";
	/**
	 * 
	 */
	static final String POST_TIMELINE = "post_timeline";

	//------------------ Keys
	/**
	 * 
	 */
	static final String ACTION_KEY = "action";
	/**
	 * 
	 */
	static final String CLIENT_KEY = "client";
	/**
	 * 
	 */
	static final String CLIENTS_KEY = "clients";
	/**
	 * 
	 */
	static final String DATA_KEY = "data";
	/**
	 * 
	 */
	static final String SENDER_KEY = "sender";
	/**
	 * 
	 */
	static final String ID_MESSAGE_LOCAL = "id_message_local";
	/**
	 * 
	 */
	static final String ID_MESSAGE = "id_message";
	/**
	 * 
	 */
	static final String NEW_CLIENT_TOPIC = "/topics/newclient";
	/**
	 * 
	 */
	static final String PING_TITLE = "Friendly Ping!";
	/**
	 * 
	 */
	static final String PING_ICON = "mipmap/ic_launcher";
	/**
	 * 
	 */
	static final String CLICK_ACTION = "ping_received";
	
	
	//------------------ GMC Server
	
	/**
	 * 
	 */
	static final String GCM_NAMESPACE = "google:mobile:data";
	/**
	 * 
	 */
	static final String GCM_ELEMENT_NAME = "gcm";
	/**
	 * 
	 */
	static final String GCM_HOST = "gcm.googleapis.com";
	/**
	 * 
	 */
	static final int GCM_CCS_PORT = 5235;

}
