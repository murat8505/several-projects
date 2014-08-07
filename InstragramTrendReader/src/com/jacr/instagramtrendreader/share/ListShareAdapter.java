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

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jacr.pruebakogithree.R;

/**
 * Adapter for Social network listview.
 * 
 * @author j.castro 02/04/2014
 * 
 */
public class ListShareAdapter extends BaseAdapter {

	private Activity activity;
	private List<ListShareItem> items;

	public ListShareAdapter(Activity activity, List<ListShareItem> items) {
		this.activity = activity;
		this.items = items;
	}

	@Override
	public int getCount() {
		return items.size();
	}

	@Override
	public Object getItem(int position) {
		return items.get(position);
	}

	@Override
	public long getItemId(int position) {
		return items.get(position).getId();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View vi = convertView;
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) activity
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			vi = inflater.inflate(R.layout.lv_dialog_share, null);
		}

		ListShareItem item = items.get(position);

		// Background color
		LinearLayout layout = (LinearLayout) vi
				.findViewById(R.id.layoutLvShare);
		layout.setBackgroundColor(Color.parseColor("#ffffff"));

		// Another views
		ImageView imagenFotoPerfil = (ImageView) vi
				.findViewById(R.id.imgLvItem);
		TextView nombre = (TextView) vi.findViewById(R.id.txtLvItem);

		// Setting all values in listview
		cargarImagenDrawable(imagenFotoPerfil, item.getRutaImagen());
		nombre.setText(item.getNombre());

		return vi;
	}

	/**
	 * Upload an image (located in drawables) .
	 * 
	 * @param img
	 *            Image to load.
	 * @param rutaImagen
	 *            Path where the image is founded (Ex. drawable/img1). Don't
	 *            write filetype extension.
	 */
	private void cargarImagenDrawable(ImageView img, String rutaImagen) {
		int idRecursoDrawable = activity.getResources().getIdentifier(
				rutaImagen, null, activity.getPackageName());
		img.setImageResource(idRecursoDrawable);
	}

}
