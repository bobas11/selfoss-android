package fr.ydelouis.selfoss.activity;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.ViewById;

import fr.ydelouis.selfoss.R;
import fr.ydelouis.selfoss.adapter.ArticlePagerAdapter;
import fr.ydelouis.selfoss.entity.Article;
import fr.ydelouis.selfoss.entity.Filter;
import fr.ydelouis.selfoss.util.SelfossUtil;

@EActivity(R.layout.activity_article)
public class ArticleActivity extends Activity implements ViewPager.OnPageChangeListener {

	@Extra protected Article article;
	@Extra protected Filter filter;

	@Bean protected SelfossUtil util;
	@Bean ArticlePagerAdapter adapter;

	@ViewById protected ViewPager pager;

	@AfterViews
	protected void initViews() {
		getActionBar().setDisplayHomeAsUpEnabled(true);
		adapter.setFilter(filter);
		adapter.setArticle(article);
		setArticle(article);
		pager.setAdapter(adapter);
		pager.setOnPageChangeListener(this);
		pager.setCurrentItem(adapter.getPosition(article));
	}

	private void setArticle(Article article) {
		setTitle(article.getSourceTitle());
		if (article.hasIcon()) {
            Picasso.with(this).load(util.faviconUrl(article)).into(new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    int size = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, 48, getResources().getDisplayMetrics());
                    Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, size, size, true);
                    getActionBar().setIcon(new BitmapDrawable(getResources(), scaledBitmap));
                }

                @Override
                public void onBitmapFailed(Drawable errorDrawable) {

                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {

                }
            });
		} else {
			getActionBar().setIcon(R.drawable.ic_selfoss);
		}
	}

	@Override
	@OptionsItem(android.R.id.home)
	public void finish() {
		super.finish();
	}

	@Override
	public void onPageSelected(int position) {
		setArticle(adapter.getArticle(position));
	}

	@Override
	public void onPageScrollStateChanged(int state) {}

	@Override
	public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

}
