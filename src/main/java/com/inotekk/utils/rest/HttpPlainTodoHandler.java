package com.inotekk.utils.rest;

import android.util.Log;

import java.io.UnsupportedEncodingException;

import cz.msebera.android.httpclient.Header;

public abstract class HttpPlainTodoHandler extends HttpTodoHandler<String>
{
	/**
	 * Success callback, need to overridden
	 * @param response as text
	 */
	public abstract void onSuccess(String response);

	/**
	 * Failure callback, need to overridden
	 * @param code error code
	 * @param msg error message
	 */
	public abstract void onFailure(int code, String msg);

	@Override
	public void onSuccess(int code, Header[] headers, byte[] data)
	{
		String dataStr = null;
		try
		{
			dataStr = new String(data, "UTF-8").trim();
			this.onSuccess(dataStr);
		}
		catch (UnsupportedEncodingException e)
		{
			Rest.log(Log.ERROR, manageException(e));
			this.onFailure(-1, null);
		}
	}

	@Override
	public void onFailure(int code, Header[] headers, byte[] data, Throwable arg3)
	{
		String error = null;
		try
		{
			error = new String(data, "UTF-8").trim();
			Rest.log(Log.INFO, code + " : " + error);
		}
		catch (Exception e)
		{
			Rest.log(Log.ERROR, manageException(e));
		}
		Rest.log(Log.ERROR, "No connexion or timeout " + error);
		this.onFailure(code, error);
	}


}
