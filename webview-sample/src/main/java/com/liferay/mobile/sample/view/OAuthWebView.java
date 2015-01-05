/**
 * Copyright (c) 2000-2014 Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.mobile.sample.view;

import android.content.Context;

import android.net.Uri;

import android.os.AsyncTask;

import android.util.AttributeSet;

import android.webkit.WebView;

import com.liferay.mobile.android.oauth.OAuthCallback;
import com.liferay.mobile.android.oauth.OAuthConfig;
import com.liferay.mobile.android.oauth.receiver.OAuthBroadcastReceiver;
import com.liferay.mobile.android.oauth.receiver.OnOpenBrowserListener;
import com.liferay.mobile.android.oauth.task.AccessTokenAsyncTask;
import com.liferay.mobile.android.oauth.task.RequestTokenAsyncTask;

/**
 * @author Bruno Farache
 */
public class OAuthWebView extends WebView implements OnOpenBrowserListener {

	public OAuthWebView(Context context) {
		this(context, null);
	}

	public OAuthWebView(Context context, AttributeSet attributes) {
		super(context, attributes);
	}

	@Override
	public void onOpenBrowser(String URL) {
		loadUrl(URL);
	}

	public void start(OAuthConfig config, OAuthCallback callback) {
		_config = config;

		getSettings().setJavaScriptEnabled(true);
		setWebViewClient(new OAuthWebClient(_config.getCallbackURL()));

		_receiver = new OAuthBroadcastReceiver(getContext(), callback, this);
		_receiver.register();

		AsyncTask task = new RequestTokenAsyncTask(getContext(), _config);
		task.execute();
	}

	protected void onCallbackURL(Uri uri) {
		String verifier = uri.getQueryParameter("oauth_verifier");
		_config.setVerifier(verifier);

		AccessTokenAsyncTask task = new AccessTokenAsyncTask(
			getContext(), _config);

		task.execute();
	}

	private OAuthConfig _config;
	private OAuthBroadcastReceiver _receiver;

}