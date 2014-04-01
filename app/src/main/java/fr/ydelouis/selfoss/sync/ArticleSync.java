package fr.ydelouis.selfoss.sync;

import android.content.Context;
import android.content.Intent;

import com.j256.ormlite.dao.Dao;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.OrmLiteDao;
import org.androidannotations.annotations.RootContext;
import org.androidannotations.annotations.rest.RestService;

import java.util.List;

import fr.ydelouis.selfoss.entity.Article;
import fr.ydelouis.selfoss.model.ArticleDao;
import fr.ydelouis.selfoss.model.DatabaseHelper;
import fr.ydelouis.selfoss.rest.SelfossRest;

@EBean
public class ArticleSync {

	public static final String ACTION_SYNC = "fr.ydelouis.selfoss.article.ACTION_SYNC";
	public static final String ACTION_NEW_SYNCED = "fr.ydelouis.selfoss.article.ACTION_NEW_SYNCED";
	private static final int ARTICLES_PAGE_SIZE = 20;
	private static final int CACHE_SIZE = 50;

	@RootContext protected Context context;
	@RestService protected SelfossRest selfossRest;
	@OrmLiteDao(helper = DatabaseHelper.class, model = Article.class)
	protected ArticleDao articleDao;

	@AfterInject
	protected void init() {
		articleDao.setContext(context);
	}

	public void performSync() {
		syncCache();
		syncUnread();
		syncFavorite();
		sendSyncBroadcast();
	}

	private void syncCache() {
		int offset = 0;
		List<Article> articles;
		Article lastArticle = null;
		boolean newSynced = false;
		do {
			articles = selfossRest.listArticles(offset, ARTICLES_PAGE_SIZE);
			for (Article article : articles) {
				article.setCached(true);
				Dao.CreateOrUpdateStatus status = articleDao.createOrUpdate(article);
				if (!newSynced && status.isUpdated()) {
					sendNewSyncedBroadcast();
					newSynced = true;
				}
			}
			if (!articles.isEmpty()) {
				lastArticle = articles.get(articles.size() - 1);
			}
			offset += ARTICLES_PAGE_SIZE;
		} while (articles.size() == ARTICLES_PAGE_SIZE && offset < CACHE_SIZE);
		if (lastArticle != null) {
			articleDao.removeCachedOlderThan(lastArticle.getDateTime());
		}
	}

	private void syncUnread() {
		articleDao.deleteUnread();
		int offset = 0;
		List<Article> articles;
		do {
			articles = selfossRest.listUnreadArticles(offset, ARTICLES_PAGE_SIZE);
			for (Article article : articles) {
				articleDao.createOrUpdate(article);
			}
			offset += ARTICLES_PAGE_SIZE;
		} while (articles.size() == ARTICLES_PAGE_SIZE);
	}

	private void syncFavorite() {
		articleDao.deleteFavorite();
		int offset = 0;
		List<Article> articles;
		do {
			articles = selfossRest.listStarredArticles(offset, ARTICLES_PAGE_SIZE);
			for (Article article : articles) {
				articleDao.createOrUpdate(article);
			}
			offset += ARTICLES_PAGE_SIZE;
		} while (articles.size() == ARTICLES_PAGE_SIZE);
	}

	private void sendSyncBroadcast() {
		context.sendBroadcast(new Intent(ACTION_SYNC));
	}

	private void sendNewSyncedBroadcast() {
		context.sendBroadcast(new Intent(ACTION_NEW_SYNCED));
	}

}
