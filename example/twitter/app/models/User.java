package models;

import java.util.*;

import play.*;
import play.db.jpa.*;

import javax.persistence.*;

import models.oauthclient.Credentials;


@Entity
public class User extends Model {

	public String username;

	public Credentials twitterCreds;

	public User(String username) {
		this.username = username;
		this.twitterCreds = new Credentials();
		this.twitterCreds.save();
	}

	public static User findOrCreate(String username) {
		User user = User.find("username", username).first();
		if (user == null) {
			user = new User(username);
		}
		return user;
	}

}
