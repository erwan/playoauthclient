package models;

import java.util.*;

import play.*;
import play.db.jpa.*;

import javax.persistence.*;

import oauthclient.IOAuthUser;

@Entity
public class User extends Model implements IOAuthUser {

	String username;

	String oauth_token;

	String oauth_secret;

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
	public String getSecret() {
		return oauth_secret;
	}

	@Override
	public String getToken() {
		return oauth_token;
	}

	@Override
	public void setSecret(String secret) {
		oauth_secret = secret;
		save();
	}

	@Override
	public void setToken(String token) {
		oauth_token = token;
		save();
	}

}
