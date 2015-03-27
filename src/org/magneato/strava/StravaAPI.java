package org.magneato.strava;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.FormBodyPart;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class StravaAPI {
	public static long getCurrentAtheleteID(String bearer) {
		JSONObject obj = httpGetRequest(
				"https://www.strava.com/api/v3/athlete", bearer);

		return (long) obj.get("id");
	}

	public static long getActivity(String bearer, long id) {
		JSONObject obj = httpGetRequest(
				"https://www.strava.com/api/v3/activities/" + id, bearer);

		return (long) obj.get("id");
	}

	
	
	public static long uploadActivity(String bearer, String fileName) {
		JSONObject jsonObj = null;
		
		HttpClient httpClient = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost(
				"https://www.strava.com/api/v3/uploads");
		httpPost.addHeader("Authorization", "Bearer " + bearer);
		httpPost.setHeader("enctype", "multipart/form-data");

		MultipartEntity reqEntity = new MultipartEntity(
				HttpMultipartMode.BROWSER_COMPATIBLE);
		try {
			reqEntity.addPart("activity_type", new StringBody("ride"));
			reqEntity.addPart("data_type", new StringBody("fit"));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		FileBody bin = new FileBody(new File(fileName));
		reqEntity.addPart("file", bin);

		httpPost.setEntity(reqEntity);

		HttpResponse response;
		try {
			response = httpClient.execute(httpPost);

			HttpEntity respEntity = response.getEntity();

			if (respEntity != null) {
				// EntityUtils to get the response content
				String content = EntityUtils.toString(respEntity);
				System.out.println(content);
				JSONParser jsonParser = new JSONParser();
				jsonObj = (JSONObject) jsonParser.parse(content);
			}
		} catch (ParseException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		return (long) jsonObj.get("id");
	}

	// http://stackoverflow.com/questions/20370095/strava-v3-api-upload-using-scribe-on-google-app-engine

	/**
	 * Sends a REST type request to the server and returns the response as a
	 * JSON object tree
	 * 
	 * @param endPoint
	 * @param params
	 * @return
	 */
	public static JSONObject httpGetRequest(String endPoint, String bearer) {
		JSONObject jsonObj = null;
		
		try {
			HttpClient client = new DefaultHttpClient();
			HttpGet request = new HttpGet(endPoint);

			// add request header
			request.addHeader("Authorization", "Bearer " + bearer);

			HttpResponse response = client.execute(request);
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
