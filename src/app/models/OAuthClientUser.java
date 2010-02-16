package models;

import play.*;
import play.db.jpa.*;
import play.libs.WS.WSRequest;

import javax.persistence.*;

import oauth.signpost.OAuthConsumer;
import oauth.signpost.OAuthProvider;
import oauth.signpost.basic.DefaultOAuthConsumer;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import oauthclient.WSOAuthConsumer;

import java.util.*;

@Entity
public abstract class OAuthClientUser extends Model {

	String oauth_token;
	String oauth_secret;

	@Transient
	WSOAuthConsumer consumer;

	/**
	 * The consumer key for your application, that the provider gives
	 * you when you register an app
	 * @return
	 */
	protected abstract String consumerKey();
	/**
	 * The consumer secret for your application, that the provider gives
	 * you when you register an app
	 * @return
	 */
	protected abstract String consumerSecret();

	public WSOAuthConsumer getConsumer() {
		if (consumer == null) {
			consumer = new WSOAuthConsumer(
				consumerKey(),
				consumerSecret());
			consumer.setTokenWithSecret(oauth_token, oauth_secret);
		}
		return consumer;
	}

	/**
	 * Sign the url with the OAuth tokens for the user. This method can only be used for GET requests.
	 * @param url
	 * @return
	 * @throws OAuthMessageSignerException
	 * @throws OAuthExpectationFailedException
	 * @throws OAuthCommunicationException
	 */
	public String sign(String url) throws OAuthMessageSignerException, OAuthExpectationFailedException, OAuthCommunicationException {
		return getConsumer().sign(url);
	}

	public WSRequest sign(WSRequest request, String method) throws OAuthMessageSignerException, OAuthExpectationFailedException, OAuthCommunicationException {
		return getConsumer().sign(request, method);
	}

	@Override
	public <T extends JPASupport> T save() {
		if (consumer != null && consumer.getToken() != null) {
			oauth_token = consumer.getToken();
		}
		if (consumer != null && consumer.getTokenSecret() != null) {
			oauth_secret = consumer.getTokenSecret();
		}
		return super.save();
	}

}
