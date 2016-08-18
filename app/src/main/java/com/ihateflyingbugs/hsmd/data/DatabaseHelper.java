package com.ihateflyingbugs.hsmd.data;

import android.content.Context;
import android.content.res.AssetManager;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class DatabaseHelper extends SQLiteOpenHelper{

	private static final String DATABASE_PATH = "/data/data/com.ihateflyingbugs.calapp/databases/";
	private static final String DATABASE_NAME = "Calapp1.db";
	
	public static final int DB_VERSION = 34;
	
	private String TAG = "DatabaseHelper";
	
	private Context mCtx;

	private SQLiteDatabase mDatabase = null;
	private boolean mIsInitializing = false;

	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DB_VERSION);
		mCtx = context;
		
		
//		if (checkDataBase()) {
//			SQLiteDatabase.openDatabase(DATABASE_PATH + DATABASE_NAME, null, SQLiteDatabase.OPEN_READWRITE);
//        } else {
//            try {
//                this.getReadableDatabase();
//                copyDataBase();
//                this.close();
//                SQLiteDatabase.openDatabase(DATABASE_PATH + DATABASE_NAME, null, SQLiteDatabase.OPEN_READWRITE);
//            } catch (IOException e) {
//                 throw new Error("Error copying database");
//            }
//        }
		
	}
	
	@Override
	public synchronized SQLiteDatabase getWritableDatabase() {
		if (mDatabase != null && mDatabase.isOpen() && !mDatabase.isReadOnly()) {
			return mDatabase;  // The database is already open for business
		}

		if (mIsInitializing) {
			throw new IllegalStateException("getWritableDatabase called recursively");
		}

		boolean success = false;
		SQLiteDatabase db = null;
		//if (mDatabase != null) mDatabase.lock();
		try {
			mIsInitializing = true;

			db = createOrOpenDatabase(false);

			onOpen(db);
			success = true;
			return db;
		} finally {
			mIsInitializing = false;
			if (success) {
				if (mDatabase != null) {
					try { mDatabase.close(); } catch (Exception e) { }
					//mDatabase.unlock();
				}
				mDatabase = db;
			} else {
				//if (mDatabase != null) mDatabase.unlock();
				if (db != null) db.close();
			}
		}

	}
	
//	@Override
//	public synchronized SQLiteDatabase getReadableDatabase() {
//		if (mDatabase != null && mDatabase.isOpen()) {
//			return mDatabase;  // The database is already open for business
//		}
//
//		if (mIsInitializing) {
//			throw new IllegalStateException("getReadableDatabase called recursively");
//		}
//
//		try {
//			return getWritableDatabase();
//		} catch (SQLiteException e) {
//			if (mName == null) throw e;  // Can't open a temp database read-only!
//			Log.e(TAG, "Couldn't open " + mName + " for writing (will try read-only):", e);
//		}
//
//		SQLiteDatabase db = null;
//		try {
//			mIsInitializing = true;
//			String path = mContext.getDatabasePath(mName).getPath();
//			db = SQLiteDatabase.openDatabase(path, mFactory, SQLiteDatabase.OPEN_READONLY);
//			if (db.getVersion() != mNewVersion) {
//				throw new SQLiteException("Can't upgrade read-only database from version " +
//						db.getVersion() + " to " + mNewVersion + ": " + path);
//			}
//
//			onOpen(db);
//			Log.w(TAG, "Opened " + mName + " in read-only mode");
//			mDatabase = db;
//			return mDatabase;
//		} finally {
//			mIsInitializing = false;
//			if (db != null && db != mDatabase) db.close();
//		}
//	}
	
	@Override
	public synchronized void close() {
		if (mIsInitializing) throw new IllegalStateException("Closed during initialization");

		if (mDatabase != null && mDatabase.isOpen()) {
			mDatabase.close();
			mDatabase = null;
		}
	}

	
	@Override
	public void onCreate(SQLiteDatabase db) {

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		Log.i("?�비", "???�비 버전 : " + newVersion);
		
//		db.execSQL("DROP TABLE IF EXISTS Food;");
//		db.execSQL("DROP TABLE IF EXISTS Category;");
//		db.execSQL("DROP TABLE IF EXISTS Brand;");
//		db.execSQL("DROP TABLE IF EXISTS Eat;");
//		db.execSQL("DROP TABLE IF EXISTS MyFood;");
//		db.execSQL("DROP TABLE IF EXISTS Image;");
		
//		onCreate(db);
			
	}

	private SQLiteDatabase createOrOpenDatabase(boolean force) throws SQLiteAssetException {		
		SQLiteDatabase db = returnDatabase();
		if (db != null) {
			// database already exists
			if (force) {
				Log.w(TAG, "forcing database upgrade!");
				copyDataBase();
				db = returnDatabase();
			}
			return db;
		} else {
			// database does not exist, copy it from assets and return it
			copyDataBase();
			db = returnDatabase();
			return db;
		}
	}

	private SQLiteDatabase returnDatabase(){
		try {
			SQLiteDatabase db = SQLiteDatabase.openDatabase(DATABASE_PATH + DATABASE_NAME, null, SQLiteDatabase.OPEN_READWRITE);
			Log.i(TAG, "successfully opened database " + DATABASE_NAME);
			return db;
		} catch (SQLiteException e) {
			Log.w(TAG, "could not open database " + DATABASE_NAME + " - " + e.getMessage());
			return null;
		}
	}
	
//	private boolean checkDataBase() {
//		
//		File file = new File(DATABASE_PATH + DATABASE_NAME);
//		Log.d(TAG, "FILE EXIST : " + (file.exists()==true ? "TRUE" : "FALSE"));
//		
//		return file.exists();
//	}
	
	private void copyDataBase() {
		  
		File dir = new File(DATABASE_PATH);   
         
        if( !dir.exists() )
        	dir.mkdirs();
        
		File outfile = new File(DATABASE_PATH + DATABASE_NAME);
		  
		AssetManager assetManager = mCtx.getResources().getAssets();
		  
		InputStream is = null; 
		    
		FileOutputStream fos = null;
		BufferedOutputStream bos = null;
		
		try {
			is = assetManager.open("databases/" + DATABASE_NAME, AssetManager.ACCESS_BUFFER);
			BufferedInputStream bis = new BufferedInputStream(is);
			
			if (outfile.exists()) {
			    outfile.delete();
			    outfile.createNewFile();
			}
			
			fos = new FileOutputStream(outfile);
			bos = new BufferedOutputStream(fos);
			
			int read = -1;
			byte[] buffer = new byte[1024];
			
			while ((read = bis.read(buffer, 0, 1024)) != -1) {
				bos.write(buffer, 0, read);
				}
			
			bos.flush();
			
			bos.close();
			fos.close();
			bis.close();
			is.close();
		} catch (IOException e) { 
			e.printStackTrace(); 
		}
	 }
	
	public class SQLiteAssetException extends SQLiteException {

		public SQLiteAssetException() {}

	    public SQLiteAssetException(String error) {
	        super(error);
	    }
	}
	
}
