package fr.ydelouis.selfoss.model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

import fr.ydelouis.selfoss.entity.Article;
import fr.ydelouis.selfoss.entity.Source;
import fr.ydelouis.selfoss.entity.Tag;
import fr.ydelouis.selfoss.sync.ArticleSyncAction;

public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

	private static final String DATABASE_NAME = "selfoss.db";
	private static final int DATABASE_VERSION = 3;

	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource) {
		try {
			TableUtils.createTable(connectionSource, Tag.class);
			TableUtils.createTable(connectionSource, Source.class);
			TableUtils.createTable(connectionSource, Article.class);
			TableUtils.createTable(connectionSource, ArticleSyncAction.class);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource, int oldVersion, int newVersion) {
		try {
			if (oldVersion < 2) {
				TableUtils.dropTable(connectionSource, Article.class, true);
				TableUtils.createTable(connectionSource, Article.class);
			}
			if (oldVersion < 3) {
				TableUtils.createTable(connectionSource, Source.class);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
