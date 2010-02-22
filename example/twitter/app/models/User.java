package models;

import java.util.*;

import play.*;
import play.db.jpa.*;
import play.modules.oauthclient.IOAuthUser;

import javax.persistence.*;


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

	// IOAuthUser

	public String getSecret() {
		return oauth_secret;
	}

	public String getToken() {
		return oauth_token;
	}

	public void setSecret(String secret) {
		oauth_secret = secret;
		save();
	}

	public void setToken(String token) {
		oauth_token = token;
		save();
	}

}
