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
 * 
 * The control flow is:
 * <ul>
 * <li>Service A registers with Service B
 * 
 * <li>You ask service A to post to Service B
 * <li>Service A redirects your web browser to Service B's authorization service
 * with Service A's client code and a callback URL on Service A
 * <li>You log in (if necessary) with Service B and grant Service A the
 * authorization it is requesting
 * <li>Service B redirects your browser to Service A's callback URL with an
 * access code as a parameter
 * <li>Service A request's a bearer token using the access code and a client
 * secret
 * </ul>
 * This falls down a bit for authorizing applications because you need to be
 * able to
 * <ol>
 * <li>Launch a Web Brower
 * <li>Start a server to listen for authorization callbacks.
 * </ol>
 * 
 * Dependency on: com.googlecode.json-simple
 * 
 * @author David George
 * @date March 2015
 */
public class Authentification {
	public static String getOAuth2Credentials() {
		String code = null;

		// Start web server listening on user port on localhost, we hope this is
		// not already in use.
		InetSocketAddress addr = new InetSocketAddress(OAuth2Credentials.port);
		HttpServer server;

		try {
			server = HttpServer.create(addr, 0);

			OAuthHandler handler = new OAuthHandler();

			HttpContext context = server.createContext("/", handler);
			context.getFilters().add(new ParameterFilter());
			server.setExecutor(Executors.newCachedThreadPool());
			server.start();
			System.out.println("Server is listening on port "
					+ OAuth2Credentials.port);

			// Start Browser and point to Strava's authorization URL with client
			// id.
			String url = OAuth2Credentials.authServer + "?" + "client_id="
					+ OAuth2Credentials.clientId + "&response_type=code"
					+ "&redirect_uri=http://" + OAuth2Credentials.domain + ":"
					+ OAuth2Credentials.port + "&scope=write"
					+ "&state=mystate" + "&approval_prompt=force";

			// note this only works for Moz on Unix/Linux
			Runtime runtime = Runtime.getRuntime();
			runtime.exec("/usr/bin/firefox -new-window " + url);

			// Wait for client to authenticate and get returned code
			code = handler.getCode();
			server.stop(0);
			System.out.println("server stopped");

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return code;

	}

	public static String getBearerToken(OAuth2Credentials credentials) {
		// Request parameters and other properties.
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("client_id",
				OAuth2Credentials.clientId));
		params.add(new BasicNameValuePair("client_secret",
				OAuth2Credentials.clientSecret));
		params.add(new BasicNameValuePair("code", credentials.getClientToken()));
		JSONObject obj = httpRequest(OAuth2Credentials.tokenServer, params);

		return (String) obj.get("access_token");
	}

	/**
	 * Sends a REST type request to the server and returns the response as a
	 * JSON object tree
	 * 
	 * @param endPoint
	 * @param params
	 * @return
	 */
	public static JSONObject httpRequest(String endPoint,
			List<NameValuePair> params) {
		JSONObject jsonObj = null;
		try {
			HttpClient httpClient = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost(endPoint);

			httpPost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));

			HttpResponse response;
			response = httpClient.execute(httpPost);
			HttpEntity respEntity = response.getEntity();

			if (respEntity != null) {
				// EntityUtils to get the response content
				String content = EntityUtils.toString(respEntity);
				System.out.println(content);
				JSONParser jsonParser = new JSONParser();
				jsonObj = (JSONObject) jsonParser.parse(content);
			}
		} catch (IOException | ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return jsonObj;
	}
}
