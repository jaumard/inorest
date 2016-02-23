package com.inotekk.utils.rest;

import android.content.Context;
import android.util.Log;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.PersistentCookieStore;
import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.RequestParams;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import cz.msebera.android.httpclient.entity.ByteArrayEntity;
import cz.msebera.android.httpclient.impl.cookie.BasicClientCookie;

/**
 * Created by jaumard on 27/01/2016.
 */
public class Rest
{
	private static Rest singleton;
	private Context context;
	private String rootURL;
	private PersistentCookieStore myCookieStore;
	private AsyncHttpClient client = new AsyncHttpClient();
	public static boolean IS_DEBUG = true;
	public static final String TAG = "REST LOG";

	/**
	 * Create Rest object
	 * @param context
	 * @param rootURL
	 */
	public Rest(Context context, String rootURL)
	{
		this.myCookieStore = new PersistentCookieStore(context);
		this.client.setCookieStore(this.myCookieStore);
		this.context = context;
		this.rootURL = rootURL;
	}

	/**
	 *
	 * @return Rest
	 */
	public static Rest getSingleton()
	{
		return getSingleton(null, null);
	}

	/**
	 * Get Rest singleton
	 * @return Rest singleton to chain call
	 */
	public static Rest i()
	{
		return getSingleton(null, null);
	}

	/**
	 * Get Rest singleton
	 * @param context
	 * @param rootURL
	 * @return Rest
	 */
	public static Rest getSingleton(Context context, String rootURL)
	{
		if (singleton == null)
		{
			singleton = new Rest(context, rootURL);
		}

		return singleton;
	}

	/**
	 * Init Rest singleton object
	 * @param context
	 * @param rootURL
	 * @return Rest
	 */
	public static Rest init(Context context, String rootURL)
	{
		if (singleton == null)
		{
			singleton = new Rest(context, rootURL);
		}

		return singleton;
	}

	/**
	 * Log messages into console
	 * @param level log level
	 * @param message to log
	 */
	public static void log(int level, String message)
	{
		log(level, message, false);
	}

	/**
	 * Log messages into console
	 * @param level log level
	 * @param message to log
	 * @param forceLog force to log even is IS_DEBUG is false
	 */
	public static void log(int level, String message, boolean forceLog)
	{
		if (IS_DEBUG || forceLog)
		{
			switch (level)
			{
				case Log.ASSERT:
				case Log.DEBUG:
					Log.d(TAG, message);
					break;
				case Log.ERROR:
					Log.e(TAG, message);
					break;
				case Log.INFO:
					Log.i(TAG, message);
					break;
				case Log.VERBOSE:
					Log.v(TAG, message);
					break;
				case Log.WARN:
					Log.w(TAG, message);
					break;
				default:
					Log.i(TAG, message);
					break;
			}
		}
	}

	/**
	 *
	 * @param part
	 * @return full URL
	 */
	private String getUrl(String part){
		String url = rootURL+part;
		if(part.contains("://")){
			url = part;
		}
		return url;
	}

	/**
	 * Add given headers and remove previous ones
	 * @param headers to add
	 */
	private void manageHeaders(Map<String, String> headers)
	{
		if (headers != null)
		{
			client.removeAllHeaders();
			for (Map.Entry<String, String> entry : headers.entrySet())
			{
				String key = entry.getKey();
				String value = entry.getValue();

				client.addHeader(key, value);
			}
		}
	}

	/**
	 *
	 * @param url url or sub url to send data
	 * @param headers to add to the request
	 * @param todo handlers
	 * @return RequestHandle
	 */
	public RequestHandle get(String url, Map<String, String> headers, HttpTodoHandler todo)
	{
		return this.get(url, null, headers, todo);
	}

	/**
	 *
	 * @param url url or sub url to send data
	 * @param params Params to send
	 * @param headers to add to the request
	 * @param todo handlers
	 * @return RequestHandle
	 */
	public RequestHandle get(String url, RequestParams params, Map<String, String> headers, HttpTodoHandler todo)
	{
		this.manageHeaders(headers);
		return client.get(getUrl(url), params, todo);
	}

	/**
	 *
	 * @param url url or sub url to send data
	 * @param params Params to send
	 * @param todo handlers
	 * @return RequestHandle
	 */
	public RequestHandle get(String url, RequestParams params, HttpTodoHandler todo)
	{
		return this.get(url, params, null, todo);
	}

	/**
	 *
	 * @param url url or sub url to send data
	 * @param params Params to send
	 * @param todo handlers
	 * @return RequestHandle
	 */
	public RequestHandle get(int url, RequestParams params, HttpTodoHandler todo)
	{
		return this.get(this.context.getString(url), params, null, todo);
	}

	/**
	 * Make get request
	 * @param url url or sub url to send data
	 * @param todo handlers
	 * @return RequestHandle
	 */
	public RequestHandle get(String url, HttpTodoHandler todo)
	{
		return this.get(url, null, null, todo);
	}

	/**
	 * Make get request
	 * @param url url or sub url to send data
	 * @param todo handlers
	 * @return RequestHandle
	 */
	public RequestHandle get(int url, HttpTodoHandler todo)
	{
		return this.get(context.getString(url), null, null, todo);
	}

	/**
	 * Make post request with json or key/value params
	 * @param url url or sub url to send data
	 * @param params object or RequestParams to send
	 * @param todo handlers
	 * @return RequestHandle
	 */
	public RequestHandle post(String url, Object params, HttpTodoHandler todo)
	{
		return this.post(getUrl(url), params, null, todo);
	}

	/**
	 * Make post request with json or key/value params
	 * @param url url or sub url to send data
	 * @param params object or RequestParams to send
	 * @param todo handlers
	 * @return RequestHandle
	 */
	public RequestHandle post(int url, Object params, HttpTodoHandler todo)
	{
		return this.post(context.getString(url), params, null, todo);
	}

	/**
	 * Make post request with json or key/value params
	 * @param url url or sub url to send data
	 * @param params object or RequestParams to send
	 * @param headers to add to the request
	 * @param todo handlers
	 * @return RequestHandle
	 */
	public RequestHandle post(String url, Object params, Map<String, String> headers, HttpTodoHandler todo)
	{
		RequestHandle result = null;
		this.manageHeaders(headers);
		if (params instanceof RequestParams)
		{
			result = client.post(getUrl(url), (RequestParams) params, todo);
		}
		else
		{
			try
			{
				ObjectMapper mapper = new ObjectMapper();
				mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);//disable exception on empty bean
				Rest.log(Log.INFO, "send " + mapper.writeValueAsString(params));
				Rest.log(Log.INFO, "to " + url);
				result = client.post(context, getUrl(url), new ByteArrayEntity(mapper.writeValueAsString(params)
						.getBytes("UTF-8")), "application/json", todo);
			}
			catch (UnsupportedEncodingException e)
			{
				Rest.log(Log.ERROR, "can't transform request object to json");
				e.printStackTrace();
				todo.onFailure(-1, null);

			}
			catch (JsonProcessingException e)
			{
				e.printStackTrace();
				Rest.log(Log.ERROR, "can't transform request object to json");
				todo.onFailure(-1, null);

			}
		}
		return result;
	}

	/**
	 * Make put request with json or key/value params
	 * @param url url or sub url to send data
	 * @param params object or RequestParams to send
	 * @param todo handlers
	 * @return RequestHandle
	 */
	public RequestHandle put(String url, Object params, HttpTodoHandler todo)
	{
		return this.put(getUrl(url), params, null, todo);
	}
	/**
	 * Make put request with json or key/value params
	 * @param url url or sub url to send data
	 * @param params object or RequestParams to send
	 * @param todo handlers
	 * @return RequestHandle
	 */
	public RequestHandle put(int url, Object params, HttpTodoHandler todo)
	{
		return this.put(context.getString(url), params, null, todo);
	}

	/**
	 * Make put request with json or key/value params
	 * @param url url or sub url to send data
	 * @param params object or RequestParams to send
	 * @param headers to add to the request
	 * @param todo handlers
	 * @return RequestHandle
	 */
	public RequestHandle put(String url, Object params, Map<String, String> headers, HttpTodoHandler todo)
	{
		RequestHandle result = null;
		this.manageHeaders(headers);
		if (params instanceof RequestParams)
		{
			result = client.put(getUrl(url), (RequestParams) params, todo);
		}
		else
		{
			try
			{
				ObjectMapper mapper = new ObjectMapper();
				mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);//disable exception on empty bean
				Rest.log(Log.INFO, "send " + mapper.writeValueAsString(params));
				Rest.log(Log.INFO, "to " + url);
				result = client.put(context, getUrl(url), new ByteArrayEntity(mapper.writeValueAsString(params)
						.getBytes("UTF-8")), "application/json", todo);

			}
			catch (UnsupportedEncodingException e)
			{
				Rest.log(Log.ERROR, "can't transform request object to json");
				e.printStackTrace();
				todo.onFailure(-1, null);

			}
			catch (JsonProcessingException e)
			{
				e.printStackTrace();
				Rest.log(Log.ERROR, "can't transform request object to json");
				todo.onFailure(-1, null);

			}
		}
		return result;
	}

	/**
	 * Make delete request
	 * @param url url or sub url to send data
	 * @param todo handlers
	 * @return RequestHandle
	 */
	public RequestHandle delete(String url, HttpTodoHandler todo)
	{
		return this.delete(getUrl(url), null, todo);
	}

	/**
	 * Make delete request
	 * @param url url or sub url to send data
	 * @param todo handlers
	 * @return RequestHandle
	 */
	public RequestHandle delete(int url, HttpTodoHandler todo)
	{
		return this.delete(context.getString(url), null, todo);
	}

	/**
	 * Make delete request
	 * @param url url or sub url to send data
	 * @param headers to add to the request
	 * @param todo handlers
	 * @return RequestHandle
	 */
	public RequestHandle delete(String url, Map<String, String> headers, HttpTodoHandler todo)
	{
		this.manageHeaders(headers);
		return client.delete(getUrl(url), todo);
	}

	/**
	 * Make patch request with json or key/value params
	 * @param url url or sub url to send data
	 * @param params object or RequestParams to send
	 * @param todo handlers
	 * @return RequestHandle
	 */
	public RequestHandle patch(String url, Object params, HttpTodoHandler todo)
	{
		return this.patch(getUrl(url), params, null, todo);
	}

	/**
	 * Make patch request with json or key/value params
	 * @param url url or sub url to send data
	 * @param params object or RequestParams to send
	 * @param todo handlers
	 * @return RequestHandle
	 */
	public RequestHandle patch(int url, Object params, HttpTodoHandler todo)
	{
		return this.patch(context.getString(url), params, null, todo);
	}

	/**
	 * Make patch request with json or key/value params
	 * @param url url or sub url to send data
	 * @param params object or RequestParams to send
	 * @param headers to add to the request
	 * @param todo handlers
	 * @return RequestHandle
	 */
	public RequestHandle patch(String url, Object params, Map<String, String> headers, HttpTodoHandler todo)
	{
		RequestHandle result = null;
		this.manageHeaders(headers);
		if (params instanceof RequestParams)
		{
			result = client.patch(getUrl(url), (RequestParams) params, todo);
		}
		else
		{
			try
			{
				ObjectMapper mapper = new ObjectMapper();
				mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);//disable exception on empty bean
				Rest.log(Log.INFO, "send " + mapper.writeValueAsString(params));
				Rest.log(Log.INFO, "to " + url);
				result = client.patch(context, getUrl(url), new ByteArrayEntity(mapper.writeValueAsString(params)
						.getBytes("UTF-8")), "application/json", todo);
			}
			catch (UnsupportedEncodingException e)
			{
				Rest.log(Log.ERROR, "can't transform request object to json");
				e.printStackTrace();
				todo.onFailure(-1, null);

			}
			catch (JsonProcessingException e)
			{
				e.printStackTrace();
				Rest.log(Log.ERROR, "can't transform request object to json");
				todo.onFailure(-1, null);

			}
		}

		return result;
	}

	/**
	 * Make head request with key/value params
	 * @param url url or sub url to send data
	 * @param params object or RequestParams to send
	 * @param headers to add to the request
	 * @param todo handlers
	 * @return RequestHandle
	 */
	public RequestHandle head(String url, RequestParams params, Map<String, String> headers, HttpTodoHandler todo)
	{
		this.manageHeaders(headers);
		return client.head(getUrl(url), params, todo);
	}

	/**
	 * Make head request with key/value params
	 * @param url url or sub url to send data
	 * @param params object or RequestParams to send
	 * @param todo handlers
	 * @return RequestHandle
	 */
	public RequestHandle head(int url, RequestParams params, HttpTodoHandler todo)
	{
		return this.head(context.getString(url), params, null, todo);
	}

	/**
	 * Get AsyncHttpClient object use for requests
	 * @return AsyncHttpClient
	 */
	public AsyncHttpClient getClient()
	{
		return client;
	}

	/**
	 *
	 * @return
	 */
	public Context getContext()
	{
		return context;
	}

	/**
	 * Set context
	 * @param context
	 * @return Rest singleton to chain call
	 */
	public Rest context(Context context)
	{
		this.context = context;
		return this;
	}

	/**
	 * Get root URL
	 * @return string root url
	 */
	public String getRootURL()
	{
		return rootURL;
	}

	/**
	 * Set root URL
	 * @param rootURL
	 */
	public void setRootURL(String rootURL)
	{
		this.rootURL = rootURL;
	}

	/**
	 * Add a cookie from cookieStore
	 * @param cookie
	 * @return Rest singleton to chain call
	 */
	public Rest addCookie(BasicClientCookie cookie){
		myCookieStore.addCookie(cookie);
		return this;
	}

	/**
	 * Remove a cookie from cookieStore
	 * @param cookie
	 * @return Rest singleton to chain call
	 */
	public Rest removeCookie(BasicClientCookie cookie){
		myCookieStore.deleteCookie(cookie);
		return this;
	}

	/**
	 * Clear all cookies from cookieStore
	 * @return Rest singleton to chain call
	 */
	public Rest clearCookies(){
		myCookieStore.clear();
		return this;
	}

	/**
	 * Cancel requests for the given context
	 * @param context of request to cancel
	 * @return Rest singleton to chain call
	 */
	public Rest cancelRequest(Context context){
		client.cancelRequests(context, true);
		return this;
	}
}
