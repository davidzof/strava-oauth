package org.magneato.strava;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * OAuth 2 credentials from your service provider. You will need to create an
 * account and enter your clientId and clientSecret here (don't share these
 * globally).
 * 
 * @author David George
 * @date 25 March 2015
 */
public class OAuth2Credentials implements Serializable {
	// CHANGE THESE TO YOUR VALUES
	public static final String clientId = "Your Client Id";
	public static final String clientSecret = "Your Client Secret";
	public static final String authServer = "https://www.strava.com/oauth/authorize";
	public static final String tokenServer = "https://www.strava.com/oauth/token";

	private static final Logger logger = Logger
			.getLogger(OAuth2Credentials.class.getName());

	private static final long serialVersionUID = 1L;
	/** Directory to store user credentials. */
	private static final String keyStore = System.getProperty("user.home")
			+ "/.oauthstore-strava";

	private String clientToken;

	/** Port in the "Callback URL". */
	public static final int port = 8081;

	/** Domain name in the "Callback URL". */
	public static final String domain = "localhost";

	public static void errorIfNotSpecified() {
		if (clientId.startsWith("Your ") || clientSecret.startsWith("Your ")) {
			System.out
					.println("Enter Client Id and Client Secret from you service provider "
							+ OAuth2Credentials.class);
			System.exit(1);
		}
	}

	public String getClientToken() {
		return clientToken;
	}

	public void setClientToken(String clientToken) {
		this.clientToken = clientToken;
	}

	public void Store() {
		try {
			OutputStream os = new FileOutputStream(keyStore);
			OutputStream buffer = new BufferedOutputStream(os);
			ObjectOutput output = new ObjectOutputStream(buffer);
			try {
				output.writeObject(this);
			} finally {
				output.close();
			}
		} catch (IOException ex) {
			logger.log(Level.SEVERE, "Cannot store credentials", ex);
		}
	}

	/**
	 * Check file exists java.io.FileNotFoundException: /home/david/.oauthstore-strava (No such file or directory)
	 * @return
	 */
	public static OAuth2Credentials Read() {
		OAuth2Credentials credentials = null;

		try (InputStream file = new FileInputStream(keyStore);
				InputStream buffer = new BufferedInputStream(file);
				ObjectInput input = new ObjectInputStream(buffer);) {
			// deserialize the List

			try {
				credentials = (OAuth2Credentials) input.readObject();
			} finally {
				input.close();
			}
		} catch (ClassNotFoundException ex) {
			logger.log(Level.SEVERE, "Cannot perform input. Class not found.",
					ex);
		} catch (IOException ex) {
			logger.log(Level.SEVERE, "Cannot perform input.", ex);
		}

		return credentials;
	}
}