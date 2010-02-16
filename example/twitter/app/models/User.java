package models;

import java.util.*;

import play.*;
import play.db.jpa.*;

import javax.persistence.*;

@Entity
public class User extends OAuthClientUser {

	String username;

	public User(String username) {
		this.username = username;
	}

	public static User findOrCreate(String username) {
		User user = User.find("username", username).first();
		if (user == null) {
			user = new User(username);
		}
		return user;
	}

	@Override
	protected String accessURL() {
		return "http://twitter.com/oauth/access_token";
	}

	@Override
	protected String authorizeURL() {
		return "http://twitter.com/oauth/authorize";
	}

	@Override
	protected String consumerKey() {
		return "eevIR82fiFK3e6VrGpO9rw"; 
	}

	@Override
	protected String consumerSecret() {
		return "OYCQA6fpsLiMVaxqqm1EqDjDWFmdlbkSYYcIbwICrg";
	}

	@Override
	protected String requestURL() {
		return "http://twitter.com/oauth/request_token";
	}

}
