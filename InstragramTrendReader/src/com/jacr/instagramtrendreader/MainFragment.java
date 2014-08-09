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

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.jacr.instagramtrendreader.utils.Util;

public class MainFragment extends Fragment {

	/* Constants */
	private static final String CLASS_NAME = MainFragment.class.getName();
	private static final String KEY_IMAGE = CLASS_NAME + ".image";
	private static final String KEY = CLASS_NAME + ".key";

	/* Variables */
	private int key;
	private String imagePath;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		key = (getArguments() != null) ? getArguments().getInt(KEY) : -1;
		imagePath = (getArguments() != null) ? getArguments().getString(
				KEY_IMAGE) : null;

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		ViewGroup view = (ViewGroup) inflater.inflate(R.layout.frg_main,
				container, false);

		ImageView imv = (ImageView) view.findViewById(R.id.upperImg);
		imv.setImageURI(Uri.fromFile(new File(imagePath)));
		imv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Bundle b = new Bundle();
				b.putInt(Main.ACTION_IMAGE_CLICK, key);
				Util.sendBroadcast(getActivity(), Main.ACTION_IMAGE_CLICK, b);

			}

		});

		return view;

	}

	public static MainFragment newInstance(int key, String filePath) {
		MainFragment fragment = new MainFragment();
		Bundle bundle = new Bundle();
		bundle.putInt(KEY, key);
		bundle.putString(KEY_IMAGE, filePath);
		fragment.setArguments(bundle);
		fragment.setRetainInstance(true);
		return fragment;
	}

}
