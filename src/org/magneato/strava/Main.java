package org.magneato.strava;


/**
 * Demo application showing how to OAuth2 authenticate with Strava from a Java
 * Application. OAuth2 allows applications to authenticate with services without
 * revealing the user's username and password to the application (well that's
 * the theory, anyway). The principal use case is to allow one web service to
 * authenticate with another web service. For example to allow Instagram to post
 * images to your Twitter feed.
 * 
 * Dependency on: com.googlecode.json-simple apache.httpclient org.junit
 * 
 * @author David George
 * @date March 2015
 */
public class Main {

	public static void main(String[] args) {
		OAuth2Credentials.errorIfNotSpecified();
		String token = null;

		OAuth2Credentials credentials = OAuth2Credentials.Read();
		if (credentials != null) {
			// see if we can use them to talk to server
			System.out.println("retrieved credentials "
					+ credentials.getClientToken());
			token = Authentification.getBearerToken(credentials);
			System.out.println("bearer token.1 " + token);
		}
		if (credentials == null || token == null) {
			String code = Authentification.getOAuth2Credentials();
			System.out.println("code" + code);
			credentials = new OAuth2Credentials();
			credentials.setClientToken(code);
			credentials.Store();
			token = Authentification.getBearerToken(credentials);
			System.out.println("bearer token.2 " + token);
		}

		long athleteId = StravaAPI.getCurrentAtheleteID(token);
		System.out.println("athleteId " + athleteId);
		StravaAPI.uploadActivity(token, "/home/david/Dropbox/2015_03_27__14_51.fit");
		//StravaAPI.getActivity(token, 310106815);

	}
}
