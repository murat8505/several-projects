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

package com.jacr.instagramtrendreader.core;

import android.os.Bundle;
import android.os.StrictMode;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.jacr.instagramtrendreader.R;
import com.jacr.instagramtrendreader.share.ShareDialog;

public abstract class BaseActivity extends SherlockFragmentActivity {

	private ShareDialog sDialog = new ShareDialog(this);

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		
		/* More Permissions */
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
				.permitAll().build();
		StrictMode.setThreadPolicy(policy);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			finish();
		} else if (item.getItemId() == R.id.menu_share) {
			sDialog.abrirDialogoRedSocial();
		}
		return true;
	}

	public ShareDialog getDialog() {
		return sDialog;
	}

	public ActionBar getActionBar(boolean incluyeReturn) {
		ActionBar actionBar = getSupportActionBar();
		actionBar.setBackgroundDrawable(((Global) getApplicationContext())
				.getBgActionBar());
		int flagActionBar = ActionBar.DISPLAY_SHOW_TITLE
				| ActionBar.DISPLAY_SHOW_HOME;
		if (incluyeReturn) {
			flagActionBar |= ActionBar.DISPLAY_HOME_AS_UP;
		}
		actionBar.setDisplayOptions(flagActionBar);
		return actionBar;
	}

}
