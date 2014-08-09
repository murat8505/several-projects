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

import java.io.File;

import com.jacr.instagramtrendreader.R;

import android.app.Application;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Environment;

public class Global extends Application {

	/* Constants */
	public static final String URL_INSTAGRAM_API = "https://api.instagram.com/v1/media/popular?client_id=05132c49e9f148ec9b8282af33f88ac7";
	public static final String URL_INSTAGRAM = "http://instagram.com/";
	public static final String SEPARATOR = System.getProperty("line.separator");
	public static final String BAR_TITLE_COLOR = "#FF0000";
	public static final float SWIPE_TOUCH_TOLERANCE = 4;

	/* Variables */
	private String name;
	private Drawable bgActionBar;
	private int smallTextSize;
	private int middleTextSize;
	private int greatTextSize;
	private String filesDirectoryPath;

	@Override
	public void onCreate() {
		super.onCreate();
		configuracionParametrosApp();
	}

	private void configuracionParametrosApp() {

		// final String internalFilesDirPath = getFilesDir().toString();
		final String externalFilesDirPath = Environment
				.getExternalStorageDirectory().getAbsolutePath() + "/";

		final Resources r = getResources();

		// App name
		name = getString(R.string.app_name);

		// Images dir
		filesDirectoryPath = externalFilesDirPath + "kogi_files/";
		File dirFile = new File(filesDirectoryPath);
		dirFile.mkdirs();

		// Action bar
		bgActionBar = r.getDrawable(R.drawable.bg_actionbar);

		// Text
		smallTextSize = (int) r.getDimension(R.dimen.letra_pequenha);
		middleTextSize = (int) r.getDimension(R.dimen.letra_mediana);
		greatTextSize = (int) r.getDimension(R.dimen.letra_grande);

	}

	/* Getter */
	public String getName() {
		return name;
	}

	public Drawable getBgActionBar() {
		return bgActionBar;
	}

	public int getSmallTextSize() {
		return smallTextSize;
	}

	public int getMiddleTextSize() {
		return middleTextSize;
	}

	public int getGreatTextSize() {
		return greatTextSize;
	}

	public String getFilesDirectoryPath() {
		return filesDirectoryPath;
	}

}
