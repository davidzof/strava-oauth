package org.magneato.strava;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpServer;

/**
 * Demo application showing how to OAuth2 authenticate with Strava from a Java
 * Application. OAuth2 allows applications to authenticate with services without
 * revealing the user's username and password to the application (well that's
 * the theory, anyway). The principal use case is to allow one web service to
 * authenticate with another web service. For example to allow Instagram to post
 * images to your Twitter feed.
 * 
 * Dependency on:
 * com.googlecode.json-simple
 * apache.httpclient
 * org.junit
 * 
 * @author David George
 * @date March 2015
 */
public class Main {

	public static void main(String[] args) {
		OAuth2Credentials.errorIfNotSpecified();
		OAuth2Credentials credentials = OAuth2Credentials.Read();
		String token = null;
		
		if (credentials != null) {
			// see if we can use them to talk to server
			credentials = new OAuth2Credentials();
			Authentification.getBearerToken(credentials);
			
		}
		if (credentials == null || token == null){
			token = Authentification.getOAuth2Credentials();
			credentials = new OAuth2Credentials();
			credentials.setClientToken(token);
			credentials.Store();
		}
	}
}
