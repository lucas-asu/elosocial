package br.com.saude.api.social.model;

import org.jivesoftware.smack.util.StringUtils;

import com.google.gson.annotations.SerializedName;

public class Client {

	public String name;
	@SerializedName("registration_token")
	public String registrationToken;
	@SerializedName("profile_picture_url")
	public String profilePictureUrl;
	@SerializedName("id_message")
	public String idMessage;
	@SerializedName("id_message_local")
	public String idMessageLocal;
	@SerializedName("content_message")
	public String contentMessage;
	@SerializedName("destination_token")
	public String destinationToken;

	public boolean isValid() {
		return StringUtils.isNotEmpty(name) && StringUtils.isNotEmpty(registrationToken)
				&& StringUtils.isNotEmpty(profilePictureUrl);
	}
}
