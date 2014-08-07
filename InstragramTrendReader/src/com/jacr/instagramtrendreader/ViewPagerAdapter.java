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

import java.util.ArrayList;
import java.util.List;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

public class ViewPagerAdapter<E extends Fragment> extends FragmentPagerAdapter {

	private List<E> listaFragments;
	private FragmentManager fm;

	/**
	 * Class Constructor.
	 * 
	 * @param fm
	 *            Instance FragmentManager.
	 */
	public ViewPagerAdapter(FragmentManager fm) {
		super(fm);
		this.fm = fm;
		listaFragments = new ArrayList<E>();

	}

	@Override
	public Fragment getItem(int arg0) {
		return listaFragments.get(arg0);
	}

	@Override
	public int getCount() {
		return listaFragments.size();
	}

	@Override
	public CharSequence getPageTitle(int position) {
		return super.getPageTitle(position);
	}

	/*
	 * This method tells the PageAdapter that item should be removed . This
	 * allows the ViewPager update dynamically.
	 * 
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.support.v4.view.PagerAdapter#getItemPosition(java.lang.Object)
	 */
	@Override
	public int getItemPosition(Object object) {
		return POSITION_NONE;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		super.destroyItem(container, position, object);
	}

	/**
	 * Returns the list of fragments of ViewPager.
	 * 
	 * @return Lista de fragments.
	 */
	public List<E> getListaFragments() {
		return listaFragments;
	}

	/**
	 * Add fragment for ViewPager.
	 * 
	 * @param fragment
	 *            a new fragment
	 */
	public void addFragment(E fragment) {
		listaFragments.add(fragment);

		/*
		 * We notify that a new fragment was added to PageAdapter. It calls the
		 * Override methods including getItemPosition, allowing to update the
		 * content.
		 */
		notifyDataSetChanged();
	}

	/**
	 * Remove all fragments associated to ViewPager.
	 */
	public void removeAllFragments() {
		for (Fragment frag : getListaFragments()) {
			try {

				/*
				 * Remove ancient fragments of the stack of ViewPager
				 * FragmentManager. Therefore, it avodis the ViewPager to
				 * accumulate the old fragments with new ones. Use
				 * CommitAllowingStateLoss is a bit of gross deletion.
				 */
				fm.beginTransaction().remove((Fragment) frag)
						.commitAllowingStateLoss();
			} catch (Exception e) {
				// e.printStackTrace();
			}
		}
		listaFragments.clear();
	}

}
