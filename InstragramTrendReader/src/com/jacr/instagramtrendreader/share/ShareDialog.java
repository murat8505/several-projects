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

package com.jacr.instagramtrendreader.share;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.jacr.instagramtrendreader.utils.Messages;
import com.jacr.pruebakogithree.R;

/**
 * Dialog for Social Networks.
 * 
 * @author j.castro 02/04/2014
 * 
 */
public class ShareDialog {

	private String imageUrlToShare;
	private AlertDialog dialog;
	private Activity actividad;

	public ShareDialog(Activity actividad) {
		this.actividad = actividad;
	}

	public void setImageUrlToShare(String imageUrlToShare) {
		this.imageUrlToShare = imageUrlToShare;
	}

	public AlertDialog getDialog() {
		return dialog;
	}

	/**
	 * Open Dialog.
	 */
	public void abrirDialogoRedSocial() {
		ContextThemeWrapper ctw = new ContextThemeWrapper(actividad,
				R.style.DialogTheme);
		AlertDialog.Builder builder = new AlertDialog.Builder(ctw);
		builder.setTitle(actividad
				.getString(R.string.details_dialog_title_share));
		ListView lista = new ListView(actividad);
		lista.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {

				// Background color for clicking
				LinearLayout layout = (LinearLayout) arg1
						.findViewById(R.id.layoutLvShare);
				layout.setBackgroundColor(Color.parseColor("#33B5E5"));

				TextView txtItem = (TextView) arg1.findViewById(R.id.txtLvItem);
				compartirMensaje(txtItem.getText().toString(), imageUrlToShare);

				dialog.dismiss();
			}

		});
		ListShareAdapter adapter = new ListShareAdapter(actividad,
				obtenerListaRedesSociales());
		lista.setAdapter(adapter);

		builder.setView(lista);
		dialog = builder.create();
		dialog.show();
	}

	/**
	 * Executes the share action through Intents.
	 * 
	 * @param paquete
	 *            Java package of social networking app installed or available
	 *            in the device.
	 * @param textoACompartir
	 *            Text for sharing.
	 */
	private void share(String paquete, String textoACompartir) {
		Intent sendIntent = new Intent(Intent.ACTION_SEND);

		sendIntent.setType("text/plain");
		sendIntent.putExtra(android.content.Intent.EXTRA_TEXT, textoACompartir);

		PackageManager pm = actividad.getApplicationContext()
				.getPackageManager();
		final List<ResolveInfo> matches = pm.queryIntentActivities(sendIntent,
				0);
		boolean temWhatsApp = false;
		for (final ResolveInfo info : matches) {
			if (info.activityInfo.packageName.startsWith(paquete)) {
				final ComponentName name = new ComponentName(
						info.activityInfo.applicationInfo.packageName,
						info.activityInfo.name);

				sendIntent.addCategory(Intent.CATEGORY_LAUNCHER);
				sendIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
						| Intent.FLAG_ACTIVITY_NO_HISTORY
						| Intent.FLAG_ACTIVITY_NEW_TASK);
				sendIntent.setComponent(name);
				temWhatsApp = true;
				break;
			}
		}
		if (temWhatsApp) {
			try {
				actividad.startActivity(sendIntent);
			} catch (ActivityNotFoundException e) {
				e.printStackTrace();
			}
		} else {
			Messages.showToast(actividad,
					actividad.getString(R.string.error_share_app));
		}
	}

	private void compartirMensaje(String servicioMensajeria, String cita) {
		if (servicioMensajeria.contains(actividad
				.getString(R.string.details_lv_share_twitter))) {
			share("com.twitter", cita);
		} else if (servicioMensajeria.contains(actividad
				.getString(R.string.details_lv_share_whatsapp))) {
			share("com.whatsapp", cita);
		}
	}

	/**
	 * Get items for showing in ListView.
	 * 
	 * @return List with items for showing.
	 */
	private List<ListShareItem> obtenerListaRedesSociales() {
		List<ListShareItem> items = new ArrayList<ListShareItem>();
		items.add(new ListShareItem(1, actividad
				.getString(R.string.details_lv_share_twitter),
				"drawable/twitter"));
		items.add(new ListShareItem(2, actividad
				.getString(R.string.details_lv_share_whatsapp),
				"drawable/whatsapp"));

		return items;
	}

}
