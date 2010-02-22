package models.oauthclient;

import play.*;
import play.db.jpa.*;

import javax.persistence.*;
import java.util.*;

@Entity
public class Credentials extends Model {
	public String token;
	public String secret;
}
