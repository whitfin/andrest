Andrest
=======

Andrest is a basic implementation of the REST protocol for Android, allowing for connections via `GET`, `POST`, `PUT` and `DELETE`. It's very simple to use, just specify your URL, payload (if there is one), and your method. It's designed as a basic introduction to REST in Android, although it will be fine for use with JSON responses/requests.

- [Setup](#setup)
- [Usage](#usage)
- [Payloads](#payloads)
- [Responses](#responses)
- [Exceptions](#exceptions)
- [Misc.](#misc)
 
### Setup ###

I can only speak for Eclipse, but if you want to include this as a library, just download the project and import it into Eclipse as "Existing Android Code" and then flag it as a library (Properties > Android). From there, you should be able to mark it as a required library for whichever project you're using.

### Usage ###

For your convenience, included in the src is `MainActivity.java` which includes an example of each request being made. Take a look at this if you want to see how easy it is to call Andrest.

Just to get across how simple it is:

```
AndrestClient rest = new AndrestClient();

rest.request(url, method, data);
```

Although, unfortunately due to Android's policies it's necessary to call the `.request()` from a non-UI thread, either by means of a `Thread` or an `AsyncTask`. The example usage in the `src/` demonstrates a simply construction of an AsyncTask which can be reused to handle all requests. 

Should you prefer the syntax, you can always call the methods directly:

```
AndrestClient rest = new AndrestClient();

rest.get(url);
```

### Payloads ###

After debating for a little while, I decided on using a `HashMap` for the POST and PUT data. This is what you will want to pass as your data object to `.request()`.

```
Map<String, Object> data = new HashMap<String, Object>();

data.put("string", "This is a test string.");
data.put("boolean", true);
data.put("integer", 10);

rest.request(url, "POST", data);
```

### Responses ###

All requests return a `JSONObject` (or throw an Exception), meaning you can parse/do whatever you want with it. In the example, the response is simply alerted to the user, however you can easily handle the response like you would anywhere else using the `JSONObject` library. There are standard error checks for response codes etc, but this can always be edited to fit the users needs (sometimes a `201` response is alright).

### Exceptions ###

As mentioned, there are standard status code checks and Exceptions are thrown if something looks wrong. However, it's fairly easy to implement your own error handling should you wish, via `RESTException`. This is a small extension of the standard Exception to include the ability to pass a JSONObject along (meaning you can just throw out the entire response if you wish). There's also a method called `createErrorObject()` to assist in any creation here.

Here are a couple of error examples:

```
// Using RESTException with a string
if(statusCode != 200){
	throw new RESTException("Could not complete GET request due to error code: " + statusCode);
}

// Using RESTException with a JSONObject
if(statusCode != 200){
	JSONObject json = new JSONObject();
	json.put("status", "fail");
	json.put("statusCode", statusCode);
	throw new RESTException(json);
}

// Using RESTException with a JSONObject and createErrorObject()
if(statusCode != 200){
	String[] keys = { "status", "statusCode" };
	Object[] vals = { "fail", statusCode };
	throw new RESTException(createErrorObject(keys, vals));
}
```

The above failures will result in the following errors for the user:

```
// Using string 
Could not complete GET request due to error code: 401

// Using object
{"status":"fail","statusCode":401}
```

The idea here is that you customize the thrown `RESTException` to match your needs, so feel free to get in there and fiddle about. 

### Misc. ###

This was written in a couple of hours, so I expect issues/improvements. If you have any, feel free to put them into the [issues](https://github.com/iwhitfield/andrest/issues "Andrest Issues"), but keep in mind that it's meant to just be a simple library at the moment.
