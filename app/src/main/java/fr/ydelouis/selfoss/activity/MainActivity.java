package fr.ydelouis.selfoss.activity;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.view.MenuItem;
import android.view.View;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.FragmentById;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.ViewById;

import fr.ydelouis.selfoss.R;
import fr.ydelouis.selfoss.account.SelfossAccount;
import fr.ydelouis.selfoss.account.SelfossAccountActivity_;
import fr.ydelouis.selfoss.entity.ArticleType;
import fr.ydelouis.selfoss.entity.Tag;
import fr.ydelouis.selfoss.fragment.ArticleListFragment;
import fr.ydelouis.selfoss.fragment.MenuFragment;
import fr.ydelouis.selfoss.sync.SyncManager;
import fr.ydelouis.selfoss.sync.Uploader;

@EActivity(R.layout.activity_main)
@OptionsMenu(R.menu.activity_main)
public class MainActivity extends Activity implements MenuFragment.Listener, DrawerLayout.DrawerListener {

	@Bean
	protected SelfossAccount account;
	@Bean
	protected SyncManager syncManager;
	@Bean
	protected Uploader uploader;

	@ViewById
	protected DrawerLayout drawer;
	@FragmentById
	protected ArticleListFragment list;
	@FragmentById
	protected MenuFragment menu;
	private ActionBarDrawerToggle drawerToggle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (savedInstanceState == null) {
			if (isConfigFilled()) {
				synchronize();
			} else {
				startConfig();
			}
		}
	}

	private boolean isConfigFilled() {
		return account.getAccount() != null;
	}

	protected void synchronize() {
		if (!syncManager.isActive()) {
			syncManager.requestSync();
		}
	}

	private void startConfig() {
		SelfossAccountActivity_.intent(this).start();
	}

	@AfterViews
	protected void initDrawer() {
		drawerToggle = new ActionBarDrawerToggle(this, drawer, R.drawable.ic_drawer, R.string.app_name, R.string.app_name);
		drawer.setDrawerListener(this);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);
		menu.setListener(this);
		setTypeAndTagInTitle();
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		drawerToggle.syncState();
	}

	@Override
	protected void onDestroy() {
		upload();
		super.onDestroy();
	}

	@Background
	protected void upload() {
		try {
			uploader.performSync();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		drawerToggle.onConfigurationChanged(newConfig);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (drawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onAccountActivityStarted() {
		drawer.closeDrawers();
	}

	@Override
	public void onArticleTypeChanged(ArticleType type) {
		list.setType(type);
		setTypeAndTagInTitle();
		drawer.closeDrawers();
	}

	@Override
	public void onTagChanged(Tag tag) {
		list.setTag(tag);
		setTypeAndTagInTitle();
		drawer.closeDrawers();
	}

	private void setTypeAndTagInTitle() {
		ArticleType type = list.getType();
		Tag tag = list.getArticleTag();
		setTitle(String.format("%s (%s)", tag.getName(this), type.getName(this)));
	}

	@Override
	public void onDrawerSlide(View drawerView, float slideOffset) {
		drawerToggle.onDrawerSlide(drawerView, slideOffset);
	}

	@Override
	public void onDrawerOpened(View drawerView) {
		menu.onOpened();
		drawerToggle.onDrawerOpened(drawerView);
	}

	@Override
	public void onDrawerClosed(View drawerView) {
		drawerToggle.onDrawerClosed(drawerView);
	}

	@Override
	public void onDrawerStateChanged(int newState) {
		drawerToggle.onDrawerStateChanged(newState);
	}
}
