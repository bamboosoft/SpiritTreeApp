package com.bamboosoft.spirittreeapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.bamboosoft.spirittreeapp.R;
import com.bamboosoft.spirittreeapp.ui.user.StatisticsActivity;
import com.bamboosoft.spirittreeapp.ui.user.UsersActivity;

public class MainActivity extends AppCompatActivity {

	private DrawerLayout mDrawerLayout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_act);

		setupToolbar();

		setupNavigationDrawer();


	}


	private void setupToolbar() {
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		ActionBar ab = getSupportActionBar();
		ab.setHomeAsUpIndicator(R.drawable.ic_menu);
		ab.setDisplayHomeAsUpEnabled(true);
	}

	private void setupNavigationDrawer() {
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerLayout.setStatusBarBackground(R.color.colorPrimaryDark);
		NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
		if (navigationView != null) {
			//setupDrawerContent(navigationView);
		}
	}



	private void setupDrawerContent(NavigationView navigationView) {
		navigationView.setNavigationItemSelectedListener(
				new NavigationView.OnNavigationItemSelectedListener() {
					@Override
					public boolean onNavigationItemSelected(MenuItem menuItem) {
						switch (menuItem.getItemId()) {
							case R.id.list_navigation_menu_item:
								// Do nothing, we're already on that screen
								// 什么都不做，我们已经在屏幕上了
								break;
							case R.id.statistics_navigation_menu_item:
								Intent intent =
										new Intent(UsersActivity.this, StatisticsActivity.class);
								intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
										| Intent.FLAG_ACTIVITY_CLEAR_TASK);
								startActivity(intent);
								break;
							default:
								break;
						}
						// Close the navigation drawer when an item is selected.
						// 选择一个项目时关闭导航抽屉。
						menuItem.setChecked(true);
						mDrawerLayout.closeDrawers();
						return true;
					}
				});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}