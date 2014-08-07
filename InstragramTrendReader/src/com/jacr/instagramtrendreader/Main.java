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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpStatus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.jacr.instagramtrendreader.core.BaseActivity;
import com.jacr.instagramtrendreader.core.Global;
import com.jacr.instagramtrendreader.core.IBase;
import com.jacr.instagramtrendreader.utils.Messages;
import com.jacr.instagramtrendreader.utils.Util;
import com.jacr.pruebakogithree.R;

/**
 * Main Class.
 * 
 * @author j.castro 28/07/2014
 * 
 */
public class Main extends BaseActivity {

	private static final String CLASS_NAME = Main.class.getName();
	public static final String ACTION_IMAGE_CLICK = CLASS_NAME
			+ ".action_image_click";

	private TableLayout layoutThumbnail;
	private FeedReader feedReader;
	private ViewPagerAdapter<MainFragment> mAdapter;
	private ViewPager mPager;
	private BroadcastReceiver mReceiver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		setContentView(R.layout.activity_main);

		/* Customizing ActionBar */
		Resources r = getResources();
		ActionBar ab = super.getActionBar(false);
		ab.setIcon(r.getDrawable(R.drawable.ic_menu_home));
		ab.setTitle(Util
				.getTitleActivity(getString(R.string.title_my_gallery_app)));

		/* Views */
		layoutThumbnail = (TableLayout) findViewById(R.id.layoutThumbnail);
		layoutThumbnail.setPadding(1, 1, 1, 1);

		/* Setting Viewpager and Indicator */
		mPager = (ViewPager) findViewById(R.id.pagerMain);
		mAdapter = new ViewPagerAdapter<MainFragment>(
				getSupportFragmentManager());
		mPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageScrollStateChanged(int arg0) {
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}

			@Override
			public void onPageSelected(int arg0) {
				int key = feedReader.getListThumbnailKeys().get(arg0);
				feedReader.highlightThumbnail(key);

			}

		});

		/* Receiver */
		mReceiver = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				String action = intent.getAction();
				Bundle extras = intent.getExtras();
				if (action.contentEquals(ACTION_IMAGE_CLICK)) {
					int key = extras.getInt(ACTION_IMAGE_CLICK);
					if (key != -1) {
						Intent in = new Intent(Main.this, ImageDetails.class);
						Bundle b = new Bundle();

						/*
						 * Warning with Error FAILED BINDED TRANSACTION: it
						 * happens When the transfer of "extras" out of memory.
						 * in This case, when images are sent in intent.
						 */
						b.putSerializable(ImageDetails.KEY_THUMBNAIL_DATA,
								feedReader.getListThumbnailData());
						b.putSerializable(ImageDetails.KEY_THUMBNAIL_KEYS,
								feedReader.getListThumbnailKeys());
						b.putInt(ImageDetails.KEY_THUMBNAIL_ACTUAL_KEY, key);
						in.putExtras(b);
						startActivity(in);
					}

				}
			}
		};
		IntentFilter filter = new IntentFilter();
		filter.addAction(ACTION_IMAGE_CLICK);
		registerReceiver(mReceiver, filter);

		/* Load data from Instagram */
		cargarFeedReader();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.menu_refresh) {
			cargarFeedReader();
		}
		return true;
	}

	@Override
	protected void onDestroy() {
		if (mReceiver != null) {
			// Remove receiver
			unregisterReceiver(mReceiver);
			mReceiver = null;
		}
		super.onDestroy();

	}

	private boolean isOnline(int timeout) {
		// Test 1: Is GPRS / Wifi port ON?
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnected()) {
			try {
				// Test 2: Ping specific url
				URL url = new URL("http://www.google.com");
				HttpURLConnection urlc = (HttpURLConnection) url
						.openConnection();
				urlc.setConnectTimeout(timeout);
				urlc.connect();
				if (urlc.getResponseCode() == HttpURLConnection.HTTP_OK) {
					return true;
				}
			} catch (MalformedURLException e1) {
				e1.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	private void cargarFeedReader() {
		if (!isOnline(3000)) { // Max 3s
			final String title = getString(R.string.error_title);
			final String content = getString(R.string.error_nored);
			Messages.abrirDialogoConfirmacion(Main.this, title, content);
		} else {

			feedReader = new FeedReader(this);
			feedReader.execute();
		}
	}

	private void addPage(String imageFile, int key) {
		mAdapter.addFragment(MainFragment.newInstance(key, imageFile));
	}

	private void setPageNumber(int page) {
		mPager.setCurrentItem(page);
	}

	private void setPageAdapter() {
		mPager.setAdapter(mAdapter);
	}

	private void cleanViewPager() {
		mAdapter.removeAllFragments();
		mPager.removeAllViews();
	}

	class FeedReader extends AsyncTask<Void, Void, String> implements IBase {

		// Constants to access data thumbnails
		public static final int THUMBNAIL_AUTHOR = 0;
		public static final int THUMBNAIL_DATE = 1;
		public static final int THUMBNAIL_TAGS = 2;
		public static final int THUMBNAIL_CAPTION = 3;
		public static final int THUMBNAIL_URL = 4;

		private ArrayList<Integer> listThumbnailKeys = new ArrayList<Integer>();
		private ArrayList<List<String>> listThumbnailData = new ArrayList<List<String>>();

		private Context mContext;

		FeedReader(Context mContext) {
			this.mContext = mContext;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			Messages.abrirDialogoEspera(mContext, "",
					getString(R.string.msg_load_feed));
		}

		@Override
		protected String doInBackground(Void... params) {

			InputStream in = null;
			String response = null;
			try {

				/* URL */
				// System.setProperty("jsse.enableSNIExtension", "false");
				URL url = new URL(Global.URL_INSTAGRAM_API);
				Messages.log(getClass(), "Opening URL " + url.toString());

				/* Sending GET Http request */
				HttpURLConnection urlConnection = (HttpURLConnection) url
						.openConnection();
				urlConnection.setRequestMethod("GET");
				urlConnection.setDoOutput(false);
				urlConnection.connect();

				/* Connection Status */
				int status = urlConnection.getResponseCode();
				if (status >= HttpStatus.SC_BAD_REQUEST) {

					in = urlConnection.getErrorStream();
					Messages.log(getClass(), Util.streamToString(in));

				} else {

					in = urlConnection.getInputStream();

					/* Get response */
					response = Util.streamToString(in);
					Messages.log(getClass(), response);

				}

			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (ProtocolException pe) {
				pe.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (in != null) {
					try {
						in.close();
					} catch (Exception e) {
					}
				}
			}
			return response;
		}

		@Override
		protected void onPostExecute(String response) {
			super.onPostExecute(response);

			if (!Util.esVacio(response)) {

				// Thumbnail Index to highlight (default)
				final int idxDefaultThumbnail = 0;
				final int numColumnas = 3; // default columns per row
				int idxColumna = 0; // Column index
				boolean status = false;
				boolean flagHighlightDefaultThumbnail = false;
				ImageView defaultThumbnail = null;

				Messages.log(getClass(), "Processing data from feed ... ");

				// Instance for first row
				TableRow row = getTableRow();

				// Removing Thumbnails
				cleanThumbnails();
				try {

					// JSON file starts with "meta" and "data" keys.
					JSONArray jsonArray = new JSONObject(response)
							.getJSONArray("data");

					for (int i = 0, dataSize = jsonArray.length(); i < dataSize; i++) {
						JSONObject iterador = (JSONObject) jsonArray.get(i);

						// Thumbnail instance
						ImageView imgThumb = new ImageView(mContext);
						imgThumb.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								ImageView imv = (ImageView) v;

								setPageNumber(listThumbnailKeys.indexOf(imv
										.hashCode()));
								highlightThumbnail(imv.hashCode());
							}

						});

						// Instance ID (hash)
						int key = imgThumb.hashCode();

						// ID Instance list
						listThumbnailKeys.add(key);

						// Data associated list (per thumbnail)
						listThumbnailData.add(getThumbnailData(iterador));

						/* Setting thumbnail */
						addThumbnail(imgThumb,
								getThumbnailDataByKey(key, THUMBNAIL_URL));

						/* Adding rows in layout */

						if (idxColumna < numColumnas) {
							idxColumna++;
						} else {
							idxColumna = 1;

							layoutThumbnail.addView(row);

							if (flagHighlightDefaultThumbnail) {

								// Highlight default Thumbnail
								highlightThumbnail(defaultThumbnail.hashCode());
								flagHighlightDefaultThumbnail = false;
							}

							// Instance for next row
							row = getTableRow();
						}

						addImageAtRow(row, imgThumb,
								getThumbnailDataByKey(key, THUMBNAIL_CAPTION));

						if (i == idxDefaultThumbnail) {
							flagHighlightDefaultThumbnail = true;
							// Default Page
							setPageNumber(idxDefaultThumbnail);
							// Default Item
							defaultThumbnail = imgThumb;

						}

					}

					Messages.log(getClass(), "Total thumbnails: "
							+ countThumbnails());

					// Add last row in layout
					layoutThumbnail.addView(row);

					setPageAdapter();

					status = true;

				} catch (JSONException e) {
					e.printStackTrace();
				} catch (MalformedURLException e) {
					e.printStackTrace();
				} catch (ProtocolException pe) {
					pe.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					if (!status) {
						Messages.abrirDialogoConfirmacion(mContext,
								getString(R.string.error_title),
								getString(R.string.error_nomanejado));
					}
				}
			} else {
				Messages.abrirDialogoConfirmacion(mContext,
						getString(R.string.error_title),
						getString(R.string.error_nomanejado));

			}
			Messages.cerrarDialogoEspera();
		}

		@Override
		public String getThumbnailDataByKey(int key, int valueType) {
			List<String> lista = getThumbnailData(key);
			if (lista != null) {
				return lista.get(valueType);
			}
			return null;
		}

		private List<String> getThumbnailData(int key) {
			int idx = listThumbnailKeys.indexOf(key);
			if (idx != -1) {
				return listThumbnailData.get(idx);
			}
			return null;
		}

		public ArrayList<Integer> getListThumbnailKeys() {
			return listThumbnailKeys;
		}

		public ArrayList<List<String>> getListThumbnailData() {
			return listThumbnailData;
		}

		public int countThumbnails() {
			return listThumbnailKeys.size();
		}

		private List<String> getThumbnailData(JSONObject json)
				throws JSONException {

			// User
			String username = "";
			if (json.get("user") instanceof JSONObject) {
				username = ((JSONObject) (json.get("user"))).get("username")
						.toString();
			}

			Messages.log(getClass(), "Dowloading images from user: " + username);

			// Published date (warning: unix format)
			String date = Util.nulo2Vacio(json.getString("created_time"));

			// Thumbnail Text
			String text = "";
			if (json.get("caption") instanceof JSONObject) {
				text = ((JSONObject) (json.get("caption"))).get("text")
						.toString();
			}

			// tags
			String tags = "";
			final String separadorTags = ",";
			JSONArray jsonArrTags = (JSONArray) json.get("tags");
			for (int idx = 0; idx < jsonArrTags.length(); idx++) {
				tags += "#" + (jsonArrTags.getString(idx)) + ",";
			}
			if (!Util.esVacio(tags) && tags.lastIndexOf(",") != -1) {
				tags = tags.substring(0, tags.lastIndexOf(separadorTags));
			}

			/*
			 * There are 3 kind of images: thumbnail, low_resolution y
			 * standard_resolution. For reasons of download bit rate, we only
			 * use thumbnails.
			 */
			String imageUrl = null;
			if (json.get("images") instanceof JSONObject) {
				JSONObject jsonImages = (JSONObject) json.get("images");
				imageUrl = ((JSONObject) jsonImages.get("thumbnail"))
						.getString("url");
			}

			ArrayList<String> listUser = new ArrayList<String>();
			listUser.add(username);
			listUser.add(date);
			listUser.add(tags);
			listUser.add(text);
			listUser.add(imageUrl);
			return listUser;
		}

		private void addThumbnail(ImageView imv, String url)
				throws MalformedURLException, IOException {
			InputStream in = new java.net.URL(url).openStream();
			Bitmap image = BitmapFactory.decodeStream(in);
			in.close();
			imv.setImageBitmap(image);
			createThumbnailFile(image, imv.hashCode());

		}

		private void createThumbnailFile(Bitmap bmp, int key) {
			String dir = ((Global) mContext.getApplicationContext())
					.getFilesDirectoryPath();
			String file = dir + key;
			File targetFile = new File(file);
			if (!targetFile.exists()) {
				try {
					FileOutputStream fos = new FileOutputStream(file);
					bmp.compress(Bitmap.CompressFormat.PNG, 100, fos);
					fos.flush();
					fos.close();
					// Add image in ViewPager
					addPage(file, key);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		public void highlightThumbnail(int hashcodeThumbnail) {
			final int colorSeleccion = mContext.getResources().getColor(
					R.color.thumbnail_highlight);
			final int colorNoSeleccion = mContext.getResources().getColor(
					R.color.thumbnail_gallery);

			for (int i = 0; i < layoutThumbnail.getChildCount(); i++) {
				View vRowIterador = layoutThumbnail.getChildAt(i);
				if (vRowIterador instanceof TableRow) {
					TableRow rowIterador = (TableRow) vRowIterador;
					for (int j = 0; j < rowIterador.getChildCount(); j++) {
						LinearLayout llvRow = (LinearLayout) (rowIterador
								.getChildAt(j));
						FrameLayout flvRow = (FrameLayout) ((LinearLayout) llvRow)
								.getChildAt(0);
						ImageView imvRow = (ImageView) ((FrameLayout) flvRow)
								.getChildAt(0);
						flvRow.setBackgroundColor(imvRow.hashCode() == hashcodeThumbnail ? colorSeleccion
								: colorNoSeleccion);
					}

				}
			}

		}

		private void cleanThumbnails() {

			listThumbnailKeys.clear();
			listThumbnailData.clear();
			layoutThumbnail.removeAllViews();

			// Remove saved images in directory
			Util.deleteAllFileDirectory(mContext);

			// Clean Viewpager
			cleanViewPager();
		}

		private void addImageAtRow(TableRow row, ImageView imv, String caption) {

			/* Layout Image */
			FrameLayout.LayoutParams imvLp = new FrameLayout.LayoutParams(
					FrameLayout.LayoutParams.WRAP_CONTENT,
					FrameLayout.LayoutParams.WRAP_CONTENT);
			imvLp.setMargins(3, 3, 3, 3);
			imv.setLayoutParams(imvLp);

			/* Layout to highilight */
			FrameLayout ff = new FrameLayout(mContext);
			LinearLayout.LayoutParams ffLp = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.WRAP_CONTENT,
					LinearLayout.LayoutParams.WRAP_CONTENT);
			ff.setLayoutParams(ffLp);

			/* Image Text */
			TextView txt = new TextView(mContext);
			txt.setText(caption);
			txt.setLayoutParams(new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.WRAP_CONTENT,
					LinearLayout.LayoutParams.WRAP_CONTENT));
			txt.setTextSize(((Global) mContext.getApplicationContext())
					.getSmallTextSize());
			txt.setMaxWidth(imv.getDrawable().getMinimumWidth());
			// Setting up three dots effect.
			txt.setEllipsize(TextUtils.TruncateAt.END);
			txt.setSingleLine(true);

			/* Layout for symmetric width column */
			LinearLayout llu = new LinearLayout(mContext);
			TableRow.LayoutParams lluLp = new TableRow.LayoutParams(
					LinearLayout.LayoutParams.MATCH_PARENT,
					LinearLayout.LayoutParams.WRAP_CONTENT);
			lluLp.weight = 1.0f;
			lluLp.gravity = Gravity.CENTER;
			llu.setLayoutParams(lluLp);
			llu.setGravity(Gravity.CENTER);
			llu.setOrientation(LinearLayout.VERTICAL);

			// Add views
			ff.addView(imv);
			llu.addView(ff, 0);
			llu.addView(txt, 1);
			row.addView(llu);

		}

		private TableRow getTableRow() {

			/* Layout for row */
			TableRow row = new TableRow(mContext);
			TableLayout.LayoutParams rowLp = new TableLayout.LayoutParams(
					TableRow.LayoutParams.MATCH_PARENT,
					TableRow.LayoutParams.WRAP_CONTENT);
			row.setLayoutParams(rowLp);
			row.setPadding(2, 2, 2, 2);
			return row;
		}

	}

}
