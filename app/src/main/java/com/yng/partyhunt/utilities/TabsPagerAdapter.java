package com.yng.partyhunt.utilities;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.yng.partyhunt.ListScreen;
import com.yng.partyhunt.MainScreen;
import com.yng.partyhunt.MapScreen;

public class TabsPagerAdapter extends FragmentPagerAdapter {

	public TabsPagerAdapter(FragmentManager fm) {
		super(fm);
	}

	@Override
	public Fragment getItem(int index) {

		switch (index) {
		case 0:

			return new MainScreen();
		case 1:

			return new MapScreen();
		case 2:

			return new ListScreen();
		}

		return null;
	}

	@Override
	public int getCount() {
		// get item count - equal to number of tabs
		return 3;
	}

}
