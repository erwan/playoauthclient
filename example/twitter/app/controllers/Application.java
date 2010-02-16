package controllers;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import models.*;

import play.libs.WS;
import play.libs.WS.HttpResponse;
import play.libs.WS.WSRequest;
import play.mvc.*;
import play.mvc.Scope.Flash;

import oauth.signpost.OAuthProvider;
import oauth.signpost.basic.DefaultOAuthProvider;

public class Application extends Controller {
	
	public static void index() throws Exception {
		String url = "http://twitter.com/statuses/mentions.xml";
		String mentions = twitterCall(url);
		render(mentions);
	}

	public static void authenticate(String callback) throws Exception {
		// 1: get the request token
		User user = getUser();
		Map<String, Object> args = new HashMap<String, Object>();
		args.put("callback", callback);
		String callbackURL = Router.getFullUrl(request.controller + ".oauthCallback", args);
		String authUrl = user.retrieveRequestToken(callbackURL);
		redirect(authUrl);
	}

	public static void oauthCallback(String callback, String oauth_token, String oauth_verifier) throws Exception {
		// 2: get the access token
		User user = getUser();
		user.retrieveAccessToken(oauth_verifier);
		redirect(callback);
	}

	private static String twitterCall(String url) throws Exception {
		User user = getUser();
		HttpResponse resp = play.libs.WS.url(user.sign(url)).get();
		System.out.println(resp.getString());
		return resp.getString();
	}

	public static void setStatus(String status) throws Exception {
		User user = getUser();
		String url = "http://twitter.com/statuses/update.xml?status=" + URLEncoder.encode(status, "utf-8");
		String response = user.sign(WS.url(url), "POST").post().getString();
		renderText(response);
	}

	private static User getUser() {
		return User.findOrCreate("erwan");
	}

}
