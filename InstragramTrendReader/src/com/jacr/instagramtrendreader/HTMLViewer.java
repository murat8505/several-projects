/*
 * Copyright (C) 2014 Jesus A. Castro R.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.jacr.instagramtrendreader;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.jacr.instagramtrendreader.core.BaseActivity;
import com.jacr.instagramtrendreader.core.Global;
import com.jacr.instagramtrendreader.utils.Util;
import com.jacr.pruebakogithree.R;

public class HTMLViewer extends BaseActivity {

	/* Constants */
	private static final String CLASS_NAME = HTMLViewer.class.getName();
	public static final String KEY_USERNAME = CLASS_NAME + ".username";
	public static final String KEY_URL_IMAGE = CLASS_NAME + ".url_image";

	private WebView webView;
	private ProgressBar progressBar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		setContentView(R.layout.activity_visor_html);
		if (getIntent() != null) {
			Resources r = getResources();
			Bundle b = getIntent().getExtras();
			String username = b.getString(KEY_USERNAME);

			// ActionBar
			final String title = getString(R.string.profile_title) + " - "
					+ username;
			ActionBar actionBar = super.getActionBar(true);
			actionBar.setIcon(r
					.getDrawable(R.drawable.ic_menu_search_holo_light));
			actionBar.setTitle(Util.getTitleActivity(title));

			// Dialog
			getDialog().setImageUrlToShare(Global.URL_INSTAGRAM + username);

			// Views
			webView = (WebView) findViewById(R.id.vistaHtml);
			progressBar = (ProgressBar) findViewById(R.id.progressbar);

			abrirEnlace(Global.URL_INSTAGRAM + username);
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.image_details, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		return true;
	}

	@SuppressLint("SetJavaScriptEnabled")
	private void abrirEnlace(String url) {

		// Enabling javascript and zoom
		webView.getSettings().setJavaScriptEnabled(true);
		webView.getSettings().setBuiltInZoomControls(true);

		webView.loadUrl(url);

		webView.setWebViewClient(new WebViewClient() {
			// prevents outside links open our app in the android browser
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				return false;
			}

		});

		webView.setWebChromeClient(new WebChromeClient() {

			@Override
			public void onProgressChanged(WebView view, int progress) {
				progressBar.setProgress(0);
				progressBar.setVisibility(View.VISIBLE);
				HTMLViewer.this.setProgress(progress * 1000);

				progressBar.incrementProgressBy(progress);

				if (progress == 100) {
					progressBar.setVisibility(View.GONE);
				}
			}
		});
	}

}
