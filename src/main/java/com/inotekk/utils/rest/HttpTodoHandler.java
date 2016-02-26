package com.inotekk.utils.rest;

import android.util.Log;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.loopj.android.http.AsyncHttpResponseHandler;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.ParameterizedType;
import java.util.HashMap;

import cz.msebera.android.httpclient.Header;

public abstract class HttpTodoHandler<T extends Object, E extends Object> extends AsyncHttpResponseHandler
{
	/**
	 * Success callback
	 * @param response as T object
	 */
	public abstract void onSuccess(T response, Header[] headers);

	/**
	 * Failure callback, need to overridden
	 * @param code error code
	 * @param error error message
	 */
	public abstract void onFailure(int code, E error);

	private Object parseResponse(String json, Class<?> type) throws IOException
	{
		Object toFollow = null;

		try
		{
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);//Not fail on unknown fields

			if (type == null)
			{
				toFollow = mapper.readValue(json, new TypeReference<HashMap<String, String>>()
				{
				});
			}
			else
			{
				toFollow = mapper.readValue(json, type);
			}
		}
		catch (Exception e){
			throw e;
		}
		return toFollow;
	}

	@Override
	@SuppressWarnings("unchecked")
	public void onSuccess(int code, Header[] headers, byte[] data)
	{
		try
		{
			String dataStr = new String(data, "UTF-8").trim();

			if (dataStr.isEmpty())
			{
				this.onSuccess((T) null, headers);
			}
			else
			{
				Rest.log(Log.INFO, code + " : " + dataStr);
				Class<?> type = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];

				this.onSuccess((T) this.parseResponse(dataStr, type), headers);
			}
		}
		catch (UnsupportedEncodingException e)
		{
			Rest.log(Log.ERROR, manageException(e));
			Rest.log(Log.ERROR, code + " but data can't be encode in string");
			this.onFailure(-1, null);
		}
		catch (JsonParseException e)
		{
			Rest.log(Log.ERROR, code + " but data can't be parsed in json");
			this.onFailure(-1, null);
			Rest.log(Log.ERROR, manageException(e));
		}
		catch (JsonMappingException e)
		{
			Rest.log(Log.ERROR, code + " but data can't be mapped as object");
			this.onFailure(-1, null);
			Rest.log(Log.ERROR, manageException(e));
		}
		catch (IOException e)
		{
			Rest.log(Log.ERROR, code + " but data can't be readed");
			this.onFailure(-1, null);
			Rest.log(Log.ERROR, manageException(e));
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public void onFailure(int code, Header[] headers, byte[] data, Throwable arg3)
	{
		String error = null;
		try
		{
			String dataStr = new String(data, "UTF-8").trim();
			Rest.log(Log.INFO, code + " : " + dataStr);
			Class<?> type = (Class<E>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];

			this.onFailure(code, (E) this.parseResponse(dataStr, type));
		}
		catch (Exception e)
		{
			Rest.log(Log.ERROR, manageException(e));
			this.onFailure(-1, null);
		}
	}

	protected String manageException(Exception e){
		return e.getMessage()+" "+e.getCause();
	}

}
