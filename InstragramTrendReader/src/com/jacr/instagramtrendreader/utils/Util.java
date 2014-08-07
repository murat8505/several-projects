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

package com.jacr.instagramtrendreader.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.util.TypedValue;

import com.jacr.instagramtrendreader.core.Global;

/**
 * Utils.
 * 
 * @author j.castro 28/07/2014
 * 
 */
public class Util {

	/**
	 * Check if a package is installed on the phone.
	 * 
	 * @param ctx
	 *            Context.
	 * @param targetPackage
	 *            Java Package to query.
	 * @return True, it exits. False, no.
	 */
	public static boolean existePaqueteSoftware(Context ctx,
			String targetPackage) {
		List<ApplicationInfo> packages;
		PackageManager pm;
		pm = ctx.getPackageManager();
		packages = pm.getInstalledApplications(0);
		for (ApplicationInfo packageInfo : packages) {
			if (targetPackage != null
					&& packageInfo.packageName.equals(targetPackage)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Check if the string is null or empty.
	 * 
	 * @param dato
	 *            String with the input value.
	 * @return true if null or empty, otherwise false.
	 */
	public static boolean esVacio(String dato) {
		boolean resultado = (dato != null && !dato.isEmpty()) ? false : true;
		return resultado;
	}

	/**
	 * Converts to empty if it's the case.
	 * 
	 * @param dato
	 *            String,
	 * @return Empty string if it's null, otherwise the original string.
	 */
	public static String nulo2Vacio(String dato) {
		return esVacio(dato) ? "" : dato;
	}

	public static String streamToString(InputStream is) throws IOException {
		String str = "";
		if (is != null) {
			StringBuilder sb = new StringBuilder();
			String line;
			try {
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(is));
				while ((line = reader.readLine()) != null) {
					sb.append(line);
				}
				reader.close();
			} finally {
				is.close();
			}
			str = sb.toString();
		}
		return str;
	}

	public static float sp2px(Context ctx, int sp) {
		Resources r = ctx.getResources();
		float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp,
				r.getDisplayMetrics());
		return px;
	}

	public static Spanned getTitleActivity(String title) {
		return Html.fromHtml("<font color='" + Global.BAR_TITLE_COLOR + "'>"
				+ title + "</font>");
	}

	public static Uri getUriFromFileDirectory(Context ctx, String fileName) {
		String dir = ((Global) ctx.getApplicationContext())
				.getFilesDirectoryPath();
		return Uri.fromFile(new File(dir + fileName));
	}

	public static void deleteAllFileDirectory(Context ctx) {
		String dir = ((Global) ctx.getApplicationContext())
				.getFilesDirectoryPath();
		File fDir = new File(dir);
		if (fDir.listFiles()!=null){
			for (File f : fDir.listFiles()) {
				f.delete();
			}
		}
	}

	public static File[] readFilesFromFileDirectory(Context ctx) {
		String dir = ((Global) ctx.getApplicationContext())
				.getFilesDirectoryPath();
		File fDir = new File(dir);
		return fDir.listFiles();
	}

	public static void sendBroadcast(Context ctx, String action, Bundle bundle) {
		Intent i = new Intent();
		i.setAction(action);
		i.putExtras(bundle);
		ctx.sendBroadcast(i);

	}

}
