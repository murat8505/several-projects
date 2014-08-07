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

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.widget.Toast;

/**
 * Messages.
 * 
 * @author j.castro 28/07/2014
 * 
 */
public class Messages {

	private static Toast toast;
	private static ProgressDialog progressDialog;

	public static void showToast(Context context, String text) {
		if (toast != null) {
			toast.cancel();
		}
		toast = Toast.makeText(context, text, Toast.LENGTH_LONG);
		toast.show();
	}

	public static void showToastActivity(final Activity act, final String text) {
		act.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				showToast(act, text);
			}

		});
	}

	public static void log(Class<?> clas, String text) {
		Log.v(clas.getName(), text);
	}

	public static void log(Context context, String text) {
		Log.v(context.getClass().getName(), text);
	}

	public static void log(Class<?> clas, Exception e) {
		Log.v(clas.getName(), e.getMessage());
	}

	public static void abrirDialogoConfirmacion(Context ctx, String titulo,
			String contenido) {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ctx);
		alertDialogBuilder.setTitle(titulo).setMessage(contenido)
				.setCancelable(false)
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				});
		AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.show();
	}

	public static void abrirDialogoEspera(Context ctx, String titulo,
			String contenido) {
		progressDialog = new ProgressDialog(ctx);
		progressDialog.setTitle(titulo);
		progressDialog.setMessage(contenido);
		progressDialog.setCancelable(false);
		progressDialog.setIndeterminate(true);
		progressDialog.show();
	}

	public static void cerrarDialogoEspera() {
		if (progressDialog != null && progressDialog.isShowing()) {
			progressDialog.dismiss();
			// progressDialog.cancel();
			progressDialog = null;
		}
	}

}
