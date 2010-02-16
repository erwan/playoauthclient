package models;

import play.*;
import play.db.jpa.*;
import play.libs.WS.WSRequest;

import javax.persistence.*;

import oauth.signpost.OAuthConsumer;
import oauth.signpost.OAuthProvider;
import oauth.signpost.basic.DefaultOAuthConsumer;
import oauth.signpost.basic.DefaultOAuthProvider;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import oauth.signpost.exception.OAuthNotAuthorizedException;
import oauthclient.WSOAuthConsumer;

import java.util.*;

@Entity
public abstract class OAuthClientUser extends Model {

	String oauth_token;
	String oauth_secret;

	@Transient
	private WSOAuthConsumer consumer;

	@Transient
	private OAuthProvider provider;

	protected abstract String requestURL();
	protected abstract String accessURL();
	protected abstract String authorizeURL();
	protected abstract String consumerKey();
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

	public OAuthProvider getProvider() {
		if (provider == null) {
			provider = new DefaultOAuthProvider(
					requestURL(),
					accessURL(),
					authorizeURL());
			provider.setOAuth10a(true);
		}
		return provider;
	}

	// Authentication

	/**
	 * Return the URL on the provider's site that we should redirect the user
	 * to in order to get the token
	 */
	public String retrieveRequestToken(String callbackURL) throws Exception {
		Logger.info("Token before request: " + getConsumer().getToken());
		String authUrl = getProvider().retrieveRequestToken(getConsumer(), callbackURL);
		Logger.info("Token after request: " + getConsumer().getToken());
		save();
		return authUrl;
	}

	public void retrieveAccessToken(String verifier) throws OAuthMessageSignerException, OAuthNotAuthorizedException, OAuthExpectationFailedException, OAuthCommunicationException {
		Logger.info("Token before retrieve: " + getConsumer().getToken());
		Logger.info("Verifier: " + verifier);
		getProvider().retrieveAccessToken(getConsumer(), verifier);
		save();
	}

	// Signing requests

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
