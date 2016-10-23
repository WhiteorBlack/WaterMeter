package com.android.blm.watermeter.adapter;

import java.util.List;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;

public class FragmentAdapter extends FragmentPagerAdapter {

	private List<Fragment> fragments;
	private List<String> titles;
	
	public FragmentAdapter(FragmentManager fm, List<Fragment> fragments,List<String> titles) {
		super(fm);
		// TODO Auto-generated constructor stub
		this.fragments=fragments;
		this.titles=titles;
	}

	@Override
	public Fragment getItem(int arg0) {
		// TODO Auto-generated method stub
		return fragments.get(arg0);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return fragments.size();
	}

	@Override
	public CharSequence getPageTitle(int position) {
		// TODO Auto-generated method stub
		if (titles!=null) {
			return titles.get(position);
		}
		return super.getPageTitle(position);
	}
	
}
