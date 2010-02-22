package play.modules.oauthclient;

import models.oauthclient.Credentials;
import oauth.signpost.OAuthProvider;
import oauth.signpost.basic.DefaultOAuthProvider;
import play.Logger;
import play.libs.WS.WSRequest;
import play.mvc.results.Redirect;

public class OAuthClient {

	private String requestURL;
	private String accessURL;
	private String authorizeURL;
	private String consumerKey;
	private String consumerSecret;

	private WSOAuthConsumer consumer;
	private OAuthProvider provider;

	public OAuthClient(String requestURL, String accessURL, String authorizeURL, String consumerKey, String consumerSecret) {
		this.requestURL = requestURL;
		this.accessURL = accessURL;
		this.authorizeURL = authorizeURL;
		this.consumerKey = consumerKey;
		this.consumerSecret = consumerSecret;
	}

	public WSOAuthConsumer getConsumer(Credentials cred) {
		if (consumer == null) {
			consumer = new WSOAuthConsumer(
				consumerKey,
				consumerSecret);
			consumer.setTokenWithSecret(cred.token, cred.secret);
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

	public void authenticate(Credentials cred, String callbackURL) throws Exception {
		throw new Redirect(retrieveRequestToken(cred, callbackURL));
	}

	/**
	 * Retrieve the request token, and store it in user.
	 * to in order to get the token.
	 * @param cred the Credentials where the oauth token and oauth secret will be set.
	 * @param callbackURL: the URL the user should be redirected after he grants the rights to our app
	 * @return the URL on the provider's site that we should redirect the user
	 */
	public String retrieveRequestToken(Credentials cred, String callbackURL) throws Exception {
		Logger.info("Consumer key: " + getConsumer(cred).getConsumerKey());
		Logger.info("Consumer secret: " + getConsumer(cred).getConsumerSecret());
		Logger.info("Token before request: " + getConsumer(cred).getToken());
		String authUrl = getProvider().retrieveRequestToken(getConsumer(cred), callbackURL);
		Logger.info("Token after request: " + getConsumer(cred).getToken());
		cred.token = consumer.getToken();
		cred.secret = consumer.getTokenSecret();
		cred.save();
		return authUrl;
	}

	/**
	 * Retrieve the access token, and store it in user.
	 * to in order to get the token.
	 * @param user the Credentials with the request token and secret already set (using retrieveRequestToken).
	 * The access token and secret will be set these.
	 * @return the URL on the provider's site that we should redirect the user
	 * @see retrieveRequestToken
	 */
	public void retrieveAccessToken(Credentials user, String verifier) throws Exception {
		Logger.info("Token before retrieve: " + getConsumer(user).getToken());
		Logger.info("Verifier: " + verifier);
		getProvider().retrieveAccessToken(getConsumer(user), verifier);
		user.token = consumer.getToken();
		user.token = consumer.getTokenSecret();
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
	public String sign(Credentials user, String url) throws Exception {
		return getConsumer(user).sign(url);
	}

	public WSRequest sign(Credentials user, WSRequest request, String method) throws Exception {
		return getConsumer(user).sign(request, method);
	}

}
