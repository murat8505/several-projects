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

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jacr.instagramtrendreader.utils.Util;

public class ImageDetailsFragment extends Fragment {

	/* Constants */
	private static final String CLASS_NAME = ImageDetailsFragment.class
			.getName();
	private static final String KEY_IMAGE = CLASS_NAME + ".image";
	private static final String KEY_URL_IMAGE = CLASS_NAME + ".url_image";
	private static final String KEY_DATE = CLASS_NAME + ".date";
	private static final String KEY_AUTHOR = CLASS_NAME + ".author";
	private static final String KEY_TAGS = CLASS_NAME + ".tags";

	/* Variables */
	private String publishDate;
	private String tags;
	private String author;
	private String imagePath;
	private String urlDowloadImage;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		publishDate = (getArguments() != null) ? getArguments().getString(
				KEY_DATE) : "";
		tags = (getArguments() != null) ? getArguments().getString(KEY_TAGS)
				: "";
		author = (getArguments() != null) ? getArguments()
				.getString(KEY_AUTHOR) : "";
		imagePath = (getArguments() != null) ? getArguments().getString(
				KEY_IMAGE) : "";
		urlDowloadImage = (getArguments() != null) ? getArguments().getString(
				KEY_URL_IMAGE) : "";

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		ViewGroup view = (ViewGroup) inflater.inflate(
				R.layout.frg_images_details, container, false);

		// Views
		TextView txtPublishDate = (TextView) view
				.findViewById(R.id.txtDateValue);
		TextView txtAuthor = (TextView) view
				.findViewById(R.id.txtUsernameValue);
		TextView txtTags = (TextView) view.findViewById(R.id.txtTagsValue);
		ImageView imv = (ImageView) view.findViewById(R.id.imvDetails);

		txtPublishDate.setText(formatearFecha(publishDate));
		txtAuthor.setText(author);
		txtTags.setText(tags);
		imv.setImageURI(Uri.fromFile(new File(imagePath)));
		imv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				final Activity act = ImageDetailsFragment.this.getActivity();
				Intent in = new Intent(act, HTMLViewer.class);
				Bundle b = new Bundle();
				b.putString(HTMLViewer.KEY_USERNAME, author);
				b.putString(HTMLViewer.KEY_URL_IMAGE, urlDowloadImage);
				in.putExtras(b);
				act.startActivity(in);
			}

		});

		return view;
	}

	/**
	 * Get formatted date.
	 * 
	 * @param fecha
	 *            date format from Instagram JSON (Unix Timestamp).
	 * @return formatted date.
	 */
	@SuppressLint({ "DefaultLocale", "SimpleDateFormat" })
	private String formatearFecha(String fecha) {
		String resultado = null;
		if (!Util.esVacio(fecha)) {
			long unixSeconds = Integer.parseInt(fecha);
			// -> 1000 for milliseconds
			Date date = new Date(unixSeconds * 1000L);
			SimpleDateFormat sdf = new SimpleDateFormat(
					"EEE dd MMM yyyy 'at' HH:mm");
			sdf.setTimeZone(TimeZone.getDefault());
			resultado = sdf.format(date);
		}
		return resultado;

	}

	public static ImageDetailsFragment newInstance(File imageFile,
			String urlDownloadImage, String publishDate, String author,
			String tags) {
		ImageDetailsFragment fragment = new ImageDetailsFragment();
		Bundle bundle = new Bundle();
		bundle.putString(KEY_DATE, publishDate);
		bundle.putString(KEY_IMAGE, imageFile.getAbsolutePath());
		bundle.putString(KEY_AUTHOR, author);
		bundle.putString(KEY_TAGS, tags);
		fragment.setArguments(bundle);
		fragment.setRetainInstance(true);
		return fragment;
	}

}
