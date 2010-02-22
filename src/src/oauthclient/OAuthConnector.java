package oauthclient;

import oauth.signpost.OAuthProvider;
import oauth.signpost.basic.DefaultOAuthProvider;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import oauth.signpost.exception.OAuthNotAuthorizedException;
import play.Logger;
import play.libs.WS.WSRequest;

public class OAuthConnector {

	private String requestURL;
	private String accessURL;
	private String authorizeURL;
	private String consumerKey;
	private String consumerSecret;

	private WSOAuthConsumer consumer;
	private OAuthProvider provider;

	public OAuthConnector(String requestURL, String accessURL, String authorizeURL, String consumerKey, String consumerSecret) {
		this.requestURL = requestURL;
		this.accessURL = accessURL;
		this.authorizeURL = authorizeURL;
		this.consumerKey = consumerKey;
		this.consumerSecret = consumerSecret;
	}

	public WSOAuthConsumer getConsumer(IOAuthUser user) {
		if (consumer == null) {
			consumer = new WSOAuthConsumer(
				consumerKey,
				consumerSecret);
			consumer.setTokenWithSecret(user.getToken(), user.getSecret());
		}
		return consumer;
	}

	public OAuthProvider getProvider() {
		if (provider == null) {
			provider = new DefaultOAuthProvider(
					requestURL,
					accessURL,
					authorizeURL);
			provider.setOAuth10a(true);
		}
		return provider;
	}

	// Authentication

	/**
	 * Retrieve the request token, and store it in user.
	 * to in order to get the token.
	 * @param user the IOAuthUser where the oauth token and oauth secret will be set.
	 * @return the URL on the provider's site that we should redirect the user
	 */
	public String retrieveRequestToken(IOAuthUser user, String callbackURL) throws Exception {
		Logger.info("Token before request: " + getConsumer(user).getToken());
		String authUrl = getProvider().retrieveRequestToken(getConsumer(user), callbackURL);
		Logger.info("Token after request: " + getConsumer(user).getToken());
		user.setToken(consumer.getToken());
		user.setSecret(consumer.getTokenSecret());
		return authUrl;
	}

	/**
	 * Retrieve the access token, and store it in user.
	 * to in order to get the token.
	 * @param user the IOAuthUser with the request token and secret already set (using retrieveRequestToken).
	 * The access token and secret will be set these.
	 * @return the URL on the provider's site that we should redirect the user
	 * @see retrieveRequestToken
	 */
	public void retrieveAccessToken(IOAuthUser user, String verifier) throws OAuthMessageSignerException, OAuthNotAuthorizedException, OAuthExpectationFailedException, OAuthCommunicationException {
		Logger.info("Token before retrieve: " + getConsumer(user).getToken());
		Logger.info("Verifier: " + verifier);
		getProvider().retrieveAccessToken(getConsumer(user), verifier);
		user.setToken(consumer.getToken());
		user.setSecret(consumer.getTokenSecret());
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
	public String sign(IOAuthUser user, String url) throws OAuthMessageSignerException, OAuthExpectationFailedException, OAuthCommunicationException {
		return getConsumer(user).sign(url);
	}

	public WSRequest sign(IOAuthUser user, WSRequest request, String method) throws OAuthMessageSignerException, OAuthExpectationFailedException, OAuthCommunicationException {
		return getConsumer(user).sign(request, method);
	}

	

}
