package br.com.saude.api.social.gcm;

import org.jivesoftware.smack.packet.ExtensionElement;

import br.com.saude.api.social.util.Constants;

/**
 * Extension of Packet to allow production and consumption of packets, to and from GCM.
 */
public class GcmPacketExtension implements ExtensionElement {

	private String json;

	public GcmPacketExtension(String json) {
		this.json = json;
	}

	public String getJson() {
		return json;
	}

	@Override
	public String getNamespace() {
		return Constants.GCM_NAMESPACE;
	}

	@Override
	public String getElementName() {
		return Constants.GCM_ELEMENT_NAME;
	}

	@Override
	public CharSequence toXML() {
		return String.format("<%s xmlns=\"%s\">%s</%s>", getElementName(), getNamespace(), json, getElementName());
	}

}
