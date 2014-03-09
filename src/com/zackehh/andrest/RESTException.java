package com.zackehh.andrest;

import org.json.JSONObject;

/**
 * Extremely simple Exception class to be used alongside Andrest.
 * 
 * Not entirely sure where this will go, but the ability to pass a
 * JSONObject means that custom errors can be built easily (e.g.
 * for passing things like status codes and metadata).
 * 
 * @author 	Isaac Whitfield
 * @version	09/03/2014
 *
 */
public class RESTException extends Exception {
	
	private static final long serialVersionUID = 4491098305202657442L;

	public RESTException(String message){
		super(message);
	}
	
	public RESTException(JSONObject errorObject){
		super(errorObject.toString());
	}
}
