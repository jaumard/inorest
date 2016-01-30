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

public abstract class HttpTodoHandler<T extends Object> extends AsyncHttpResponseHandler
{
	/**
	 * Success callback
	 * @param response as T object
	 */
	public abstract void onSuccess(T response);

	/**
	 * Failure callback, need to overridden
	 * @param code error code
	 * @param msg error message
	 */
	public abstract void onFailure(int code, String msg);

	@Override
	@SuppressWarnings("unchecked")
	public void onSuccess(int code, Header[] headers, byte[] data)
	{
		try
		{
			String dataStr = new String(data, "UTF-8").trim();

			if (dataStr.isEmpty())
			{
				this.onSuccess((T) null);
			}
			else
			{
				Rest.log(Log.INFO, code + " : " + dataStr);
				ObjectMapper mapper = new ObjectMapper();
				mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);//Not fail on unknown fields
				Class<?> type = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
				Object toFollow;

				if (type == null)
				{
					toFollow = mapper.readValue(dataStr, new TypeReference<HashMap<String, String>>()
					{
					});
				}
				else
				{
					toFollow = mapper.readValue(dataStr, type);
				}

				this.onSuccess((T) toFollow);
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
	public void onFailure(int code, Header[] headers, byte[] data, Throwable arg3)
	{
		String error = null;
		try
		{
			String dataStr = new String(data, "UTF-8").trim();

			Rest.log(Log.INFO, code + " : " + dataStr);
			ObjectMapper mapper = new ObjectMapper();
			HashMap errorObj = mapper.readValue(data, HashMap.class);
			error = (String) errorObj.get("error");
		}
		catch (Exception e)
		{
			Rest.log(Log.ERROR, manageException(e));
		}
		Rest.log(Log.ERROR, "No connexion or timeout " + error);
		this.onFailure(code, error);
	}

	protected String manageException(Exception e){
		return e.getMessage()+" "+e.getCause();
	}

}
