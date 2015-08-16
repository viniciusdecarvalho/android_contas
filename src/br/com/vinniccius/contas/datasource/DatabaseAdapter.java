package br.com.vinniccius.contas.datasource;

import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import br.com.vinniccius.contas.R;

public class DatabaseAdapter {

	private static final String LOG = "DatabaseAdapter";
	
	private final Context context;
	private final SQLiteOpenHelper dbHelper;
	private SQLiteDatabase database;

	private String dbName;
	private int dbVersion;
	private String dbCreate;

	public DatabaseAdapter(Context context) {
		this(context, true);
	}
	
	public DatabaseAdapter(Context context, boolean opened) {
		this.context = context;
		this.dbHelper = new DbHelper();
		if (opened) {
			openDatabase();
		}
	}

	public Context getContext() {
		return context;
	}

	private String getDbName() {
		if (dbName == null) {
			this.dbName = context.getResources().getString(R.string.db_name);
		}
		return dbName;
	}

	private int getDbVersion() {
		if (dbVersion == 0) {
			String version = context.getResources().getString(R.string.db_version);
			this.dbVersion = version.length() > 0 ? Integer.parseInt(version) : 1;
		}
		return dbVersion;
	}

	private String getDbCreate() {
		if (dbCreate == null) {
			InputStream stream = context.getResources().openRawResource(R.raw.create_db);
			try {
				byte[] buffer = new byte[stream.available()];
				stream.read( buffer );	
				this.dbCreate = new String( buffer );
			} catch (IOException e) {
				Log.w(LOG, "create db read failed. " + e.getMessage());
				throw new SQLException(e.getMessage());
			} finally {
				try {
					stream.close();
				} catch (IOException e) {
					Log.w(LOG, e.getMessage());
				}
			}
		}
		return dbCreate;
	}
	
	public void openDatabase() {
		this.database = dbHelper.getWritableDatabase();
	}
	
	public boolean isOpen() {
		return getDatabase() != null && getDatabase().isOpen();
	}
	
	public void closeDatabase() {
		if (isOpen()) 
			database.close();
	}
	
	public SQLiteDatabase getDatabase() {
		return database;
	}
	
	public class DbHelper extends SQLiteOpenHelper {

		private static final String SEPARATOR = ";;";

		public DbHelper() {
			super(getContext(), getDbName(), null, getDbVersion());
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			try {
				String[] sqls = getDbCreate().split(SEPARATOR);
				for (String sql : sqls) {
					db.execSQL(sql);
				}
			} catch (SQLException e) {
				Log.w(LOG, "criação da base de dados sqlite falhou. " + e.getMessage() );
				throw new SQLException(e.getMessage());
			}
			Log.i(LOG, "Base de dados sqlite criada com sucesso.");
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.i(LOG, "Atualizando base de dados sqlite");			
			onCreate(db);
		}
	}

}
