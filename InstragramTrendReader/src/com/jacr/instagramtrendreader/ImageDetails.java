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
import java.util.ArrayList;
import java.util.List;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.view.ViewPager;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.jacr.instagramtrendreader.Main.FeedReader;
import com.jacr.instagramtrendreader.core.BaseActivity;
import com.jacr.instagramtrendreader.core.IBase;
import com.jacr.instagramtrendreader.utils.Util;
import com.viewpagerindicator.CirclePageIndicator;

public class ImageDetails extends BaseActivity implements IBase {

	private static final String CLASS_NAME = ImageDetails.class.getName();
	public static final String KEY_THUMBNAIL_ACTUAL_KEY = CLASS_NAME
			+ ".thumbnail_actual_key";
	public static final String KEY_THUMBNAIL_DATA = CLASS_NAME
			+ ".thumbnail_data";
	public static final String KEY_THUMBNAIL_KEYS = CLASS_NAME
			+ ".thumbnail_keys";

	private ArrayList<Integer> listThumbnailKeys = null;
	private ArrayList<List<String>> listThumbnailData = null;
	private ViewPagerAdapter<ImageDetailsFragment> mAdapter;
	private ActionBar actionBar;

	@SuppressWarnings({ "unchecked" })
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activitiy_image_details);

		if (getIntent() != null) {
			Bundle b = getIntent().getExtras();
			listThumbnailKeys = (ArrayList<Integer>) b.get(KEY_THUMBNAIL_KEYS);
			listThumbnailData = (ArrayList<List<String>>) b
					.get(KEY_THUMBNAIL_DATA);
			final int currentKey = b.getInt(KEY_THUMBNAIL_ACTUAL_KEY);

			/* Views */
			ViewPager mPager = (ViewPager) findViewById(R.id.pagerMain);
			CirclePageIndicator mIndicator = (CirclePageIndicator) findViewById(R.id.indicator);

			/* ActionBar */
			Resources r = getResources();
			actionBar = super.getActionBar(true);
			actionBar.setIcon(r.getDrawable(R.drawable.ic_menu_gallery));

			/* Viewpager and Indicator */
			mAdapter = new ViewPagerAdapter<ImageDetailsFragment>(
					getSupportFragmentManager());

			/*
			 * With the name of the image, extract the hash for the Associated
			 * data (we don't use listThumbnailKeys for now).
			 */
			File[] imageList = Util.readFilesFromFileDirectory(this);
			for (int i = 0; i < imageList.length; i++) {
				int keyIterator = Integer.parseInt(imageList[i].getName());
				String date = Util.nulo2Vacio(getThumbnailDataByKey(
						keyIterator, FeedReader.THUMBNAIL_DATE));
				String author = Util.nulo2Vacio(getThumbnailDataByKey(
						keyIterator, FeedReader.THUMBNAIL_AUTHOR));
				String tags = Util.nulo2Vacio(getThumbnailDataByKey(
						keyIterator, FeedReader.THUMBNAIL_TAGS));
				String urlImage = Util.nulo2Vacio(getThumbnailDataByKey(
						keyIterator, FeedReader.THUMBNAIL_URL));
				mAdapter.addFragment(ImageDetailsFragment.newInstance(
						imageList[i], urlImage, date, author, tags));

			}
			mPager.setAdapter(mAdapter);
			mIndicator.setViewPager(mPager);
			mIndicator.setHorizontalScrollBarEnabled(true);
			mIndicator.setHorizontalFadingEdgeEnabled(true);
			mIndicator.setBackgroundColor(r.getColor(R.color.white));
			mIndicator
					.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

						@Override
						public void onPageSelected(int arg0) {
							setDataRelationshipViewPager(arg0);
						}

						@Override
						public void onPageScrolled(int arg0, float arg1,
								int arg2) {

						}

						@Override
						public void onPageScrollStateChanged(int arg0) {

						}
					});

			// Show image
			int idxKey = listThumbnailKeys.indexOf(currentKey);
			mPager.setCurrentItem(idxKey);
			setDataRelationshipViewPager(idxKey);

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

	private void setDataRelationshipViewPager(int page) {
		String title = listThumbnailData.get(page).get(
				FeedReader.THUMBNAIL_CAPTION);
		String url = listThumbnailData.get(page).get(FeedReader.THUMBNAIL_URL);
		actionBar.setTitle(Util.getTitleActivity(title));
		if (getDialog() != null) {
			getDialog().setImageUrlToShare(url);
		}

	}

	@Override
	public String getThumbnailDataByKey(int key, int valueType) {
		int idx = listThumbnailKeys.indexOf(key);
		if (idx != -1) {
			return listThumbnailData.get(idx).get(valueType);
		}
		return null;
	}

}
