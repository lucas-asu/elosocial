package br.com.saude.api.social.model;

public class Message {
	
	private String registrationTokenId;
	
	private String idMessage;
	
	private String idMessageLocal;
	
	private String contentMessage;

	public String getRegistrationTokenId() {
		return registrationTokenId;
	}

	public void setRegistrationTokenId(String registrationTokenId) {
		this.registrationTokenId = registrationTokenId;
	}

	public String getIdMessage() {
		return idMessage;
	}

	public void setIdMessage(String idMessage) {
		this.idMessage = idMessage;
	}

	public String getIdMessageLocal() {
		return idMessageLocal;
	}

	public void setIdMessageLocal(String idMessageLocal) {
		this.idMessageLocal = idMessageLocal;
	}

	public String getContentMessage() {
		return contentMessage;
	}

	public void setContentMessage(String contentMessage) {
		this.contentMessage = contentMessage;
	}
}
