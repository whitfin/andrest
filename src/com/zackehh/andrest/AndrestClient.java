package com.zackehh.andrest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Main controller for the REST client. The request you wish to make can
 * be called by either calling the request() method and passing the type
 * of request you will be to make, or by accessing the methods directly.
 * 
 * Uses the RESTException to report errors, but this is extremely basic,
 * and left alone due to the fact people should rather implement their
 * own error codes and systems. You can pass a JSONObject to the Exception
 * by calling createErrorObject().
 * 
 * @author 	Isaac Whitfield
 * @version 09/03/2014
 * 
 */
public class AndrestClient {

	// The client to use for requests
	DefaultHttpClient client = new DefaultHttpClient();
	
	/**
	 * Main controller for the client, has the ability to create any of the other methods. Call with 
	 * your connection type and data, and everything is handled for you.
	 * 
	 * @param 	url			the url you wish to call
	 * @param 	method		the method you wish to call wish
	 * @param 	data		the data you want to pass to the URL, can be null
	 * @return 	JSON		the JSONObject returned from the request
	 */
	public JSONObject request(String url, String method, Map<String, Object> data) throws RESTException {
		if (method.matches("GET")) {
			return get(url);
		} else if (method.matches("POST")) {
			return post(url, data);
		} else if (method.matches("PUT")) {
			return put(url, data);
		} else if (method.matches("DELETE")) {
			return delete(url);
		}
		throw new RESTException("Error! Incorrect method provided: " + method);
	}
	
	/**
	 * Calls a GET request on a given url. Doesn't take a data object (yet), so pass all get parameters 
	 * alongside the url.
	 * 
	 * @param 	url		the url you wish to connect to
	 * @return 	JSON	the JSON response from the call		
	 */
	public JSONObject get(String url) throws RESTException {
		HttpGet request = new HttpGet(url);
		try {
			HttpResponse response = client.execute(request);
			int statusCode = response.getStatusLine().getStatusCode();
			if(statusCode != 200){
				throw new Exception("Error executing GET request! Received error code: " + response.getStatusLine().getStatusCode());
			}
			return new JSONObject(readInput(response.getEntity().getContent()));
		} catch (Exception e) {
			throw new RESTException(e.getMessage());
		}
	}
	
	/**
	 * Calls a POST request on a given url. Takes a data object in the form of a HashMap to POST.
	 * 
	 * @param 	url		the url you wish to connect to
	 * @param	data	the data object to post to the url
	 * @return 	JSON	the JSON response from the call		
	 */
	public JSONObject post(String url, Map<String, Object> data) throws RESTException {
		HttpPost request = new HttpPost(url);
		List<NameValuePair> nameValuePairs = setParams(data);
		try {
			request.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			HttpResponse response = client.execute(request);

			int statusCode = response.getStatusLine().getStatusCode();
			if(statusCode != 200){
				throw new Exception("Error executing POST request! Received error code: " + response.getStatusLine().getStatusCode());
			}
			
			return new JSONObject(readInput(response.getEntity().getContent()));
		} catch (Exception e) {
			throw new RESTException(e.getMessage());
		}
	}

	/**
	 * Calls a PUT request on a given url. Takes a data object in the form of a HashMap to PUT.
	 * 
	 * @param 	url		the url you wish to connect to
	 * @param	data	the data object to post to the url
	 * @return 	JSON	the JSON response from the call		
	 */
	public JSONObject put(String url, Map<String, Object> data) throws RESTException {
		HttpPut request = new HttpPut(url);
		List<NameValuePair> nameValuePairs = setParams(data);
		try {
			request.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			HttpResponse response = client.execute(request);

			int statusCode = response.getStatusLine().getStatusCode();
			if(statusCode != 200){
				throw new Exception("Error executing PUT request! Received error code: " + response.getStatusLine().getStatusCode());
			}
			
			return new JSONObject(readInput(response.getEntity().getContent()));
		} catch (Exception e) {
			throw new RESTException(e.getMessage());
		}
	}

	/**
	 * Calls a DELETE request on a given url.
	 * 
	 * @param 	url		the url you wish to connect to
	 * @return 	JSON	the JSON response from the call		
	 */
	public JSONObject delete(String url) throws RESTException {
		HttpDelete request = new HttpDelete(url);
		try {
			HttpResponse response = client.execute(request);

			int statusCode = response.getStatusLine().getStatusCode();
			if(statusCode != 200){
				throw new Exception("Error executing DELETE request! Received error code: " + response.getStatusLine().getStatusCode());
			}
			
			return new JSONObject(readInput(response.getEntity().getContent()));
		} catch (Exception e) {
			throw new RESTException(e.getMessage());
		}
	}
	
	/**
	 * Adds a set of cookies to the HTTPClient. Pass in a HashMap of <String, Object>
	 * to add the cookie values to the client. These cookies persist until removed.
	 * 
	 * @param cookies		a HashMap of cookies
	 * @param url			the domain URL of the cookie
	 */
	public void setCookies(HashMap<String, String> cookies, String url){
		if(cookies == null){
			return;
		}

		// Set cookies
		client.getParams().setParameter(ClientPNames.COOKIE_POLICY, CookiePolicy.BROWSER_COMPATIBILITY);
		
		// Create cookie store
		BasicCookieStore store = (BasicCookieStore) (client.getCookieStore() == null ? new BasicCookieStore() : client.getCookieStore());

		// Add cookies to request
		for(Map.Entry<String, String> entry : cookies.entrySet()) {
			BasicClientCookie cookie = new BasicClientCookie(entry.getKey(), entry.getValue());
			if(url != null){
				cookie.setDomain(url);
			}
			cookie.setPath("/");
			store.addCookie(cookie);
		}

		// Add the cookie
		client.setCookieStore(store);
	}
	
	/**
	 * Helper method for creating an error object for better reporting. Not needed, 
	 * but has proven useful before and can be used to pass objects to the error 
	 * class RESTException.
	 * 
	 * @param 	keys		a list of keys
	 * @param 	values		a list of values
	 * @return 	JSON		a JSONObject to be used as an error
	 */
	@SuppressWarnings("unused")
	private JSONObject createErrorObject(String[] keys, Object[] values){
		JSONObject json = new JSONObject();
		try {
			for(int i = 0; i < keys.length; i++){
				json.put(keys[i], values[i]);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Loops all data inside the data object and maps to a list of Name/Values.
	 * Used alongside POST and PUT requests to neaten the parameters, rather than
	 * attempting to stringify the data.
	 * 
	 * @param 	data	the data object we're going to pass
	 * @return	List	a list of Name/Value pairs
	 */
	private List<NameValuePair> setParams(Map<String, Object> data){
		List<NameValuePair> nameValuePairs = null;
		if (data != null && !data.isEmpty()) {
			nameValuePairs = new ArrayList<NameValuePair>(2);
			Iterator<String> it = data.keySet().iterator();
			while (it.hasNext()) {
				String name = it.next();
				Object value = data.get(name);
				nameValuePairs.add(new BasicNameValuePair(name, value.toString()));
			}
		}
		return nameValuePairs;
	}
	
	/**
	 * Generic handler to retrieve the result of a request. Simply reads the input stream
	 * and returns the string;
	 * 
	 * @param 	is		the InputStream we're reading, usually from getEntity().getContent()
	 * @return	JSON 	the read-in JSON data as a string
	 */
	private String readInput(InputStream is) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		String json = "", line;
		while ((line = br.readLine()) != null){
			json += line;
		}
		return json;
	}
}