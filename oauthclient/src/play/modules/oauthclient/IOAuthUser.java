package play.modules.oauthclient;

public interface IOAuthUser {

	public void setToken(String token);

	public void setSecret(String secret);

	public String getToken();

	public String getSecret();

}
