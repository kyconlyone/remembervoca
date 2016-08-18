package com.ihateflyingbugs.hsmd.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;

public class UserDBPool {

	private String DB_FILE_PATH;
	
	private static final String TABLE_CREATE_CURRENTWORDS = 
		
		"CREATE TABLE IF NOT EXISTS Current_Words ( "
		+ "_id 				INTEGER PRIMARY KEY, "
		+ "word				TEXT, "
		+ "meaning			TEXT, "
		+ "difficulty		INTEGER NOT NULL, "
		+ "priority			INTEGER NOT NULL, "
		+ "score			DOUBLE NOT NULL, "
		+ "state			INTEGER NOT NULL, "
		+ "time				INTEGER NOT NULL, "
		+ "frequency		INTEGER NOT NULL, "
		+ "isRight			BOOL NOT NULL, "
		+ "isWrong			BOOL NOT NULL, "
		+ "wrongCount		INTEGER NOT NULL"
		+ " );";

//	private int _id;
//	
//	private String word;
//	private String meaning;
//	
//	private int difficulty;
//	private int priority;
//	
//	private double score;
//	private int state;
//	private int time;
//	private int frequency;
//	
//	private boolean isRight = false;
//	private boolean isWrong = false;
//	private int wrongCount = 0;

	private static UserDBPool instance;
	private SQLiteDatabase db;
	
	
	private UserDBPool(){
		DB_FILE_PATH = Config.DB_FILE_DIR + Config.DB_USER_NAME;
		
		File file = new File(Config.DB_FILE_DIR);
		if(!file.exists() && !file.mkdirs())
			return;
		
		file = new File(DB_FILE_PATH);

		if(!file.exists()){
			try{
				db = SQLiteDatabase.openOrCreateDatabase(DB_FILE_PATH, null);
				db.execSQL(TABLE_CREATE_CURRENTWORDS);
				
			}catch(Exception e){
			}
		}
		else{
			Log.v("kjw", "file exist!!");
			db = SQLiteDatabase.openDatabase(DB_FILE_PATH, null, SQLiteDatabase.OPEN_READWRITE);
			
//			db.execSQL(TABLE_CREATE_CATEGORY);
//			
//			ArrayList<Category> insertTypes = new ArrayList<Category>();
//			insertTypes.add(new Category(0, "�Ļ�"));
//			insertTypes.add(new Category(1, "����"));
//			insertTypes.add(new Category(2, "����Ʈ"));
//			insertTypes.add(new Category(3, "����"));
//			insertTypes.add(new Category(4, "��Ȱǰ��"));
//			insertTypes.add(new Category(5, "����"));
//			insertTypes.add(new Category(6, "������"));
//			insertTypes.add(new Category(7, "��ȭ"));
//			insertTypes.add(new Category(8, "�ǰ�"));
//			insertTypes.add(new Category(9, "�̿�"));
//			insertTypes.add(new Category(10, "��Ÿ"));
//			
//			for(Category c : insertTypes)
//			{
//				updateCategory(c);
//			}
		}
	}
	
	public void resetDB(){
	
		db.delete("progresses", null, null);
		db.delete("schedule", "status != ?", new String[]{"1",});
		
		ContentValues values = new ContentValues();
		
		
		db.update("schedule", values, "status = ?", new String[]{"1",});
	}
	
	public static UserDBPool getInstance(){
		
		if(instance == null){
			instance = new UserDBPool();
		}
		return instance;
	}
	
	public void release(){
		
		db.close();
		
		if(instance != null){
			instance = null;
		}
	}
	
//	private int _id;
//	
//	private String word;
//	private String meaning;
//	
//	private int difficulty;
//	private int priority;
//	
//	private double score;
//	private int state;
//	private int time;
//	private int frequency;
//	
//	private boolean isRight = false;
//	private boolean isWrong = false;
//	private int wrongCount = 0;
	
	
	
	public boolean insertCurrentWord(Word word)
	{
		ContentValues values = new ContentValues();
		values.put("_id", word.get_id());
		values.put("word", word.getWord());
		//values.put("meaning", word.getMeaning());
		values.put("difficulty", word.getDifficulty());
		//values.put("priority", word.getPriority());
		values.put("score", word.getScore());
		values.put("state", word.getState());
		values.put("time", word.getTime(Config.Time_ONE_HOUR));
		values.put("frequency", word.getFrequency());
		values.put("isRight", word.isRight()?1:0);
		values.put("isWrong", word.isWrong()?1:0);
		values.put("wrongCount", word.getWrongCount());
		
		return db.insert("Current_Words", null, values) >= 0;
		
	}
	
	synchronized public void deleteCurrentWord(int _id)
	{
		db.execSQL("DELETE FROM Current_Words WHERE _id = " + _id);
	}
	
	synchronized public void deleteAllCurrentWord()
	{
		db.execSQL("DELETE FROM Current_Words ");
	}
	
	synchronized public boolean updateRightWrong(boolean isRight, int _id){
		ContentValues values = new ContentValues();
		
		if(isRight){
			values.put("isRight", 1);
			values.put("isWrong", 0);
		}else{
			values.put("isRight", 0);
			values.put("isWrong", 1);
		}
		return db.update("Current_Words", values, "_id = ?", new String[]{String.valueOf(_id),}) >= 0;
	}

	public int getRightCount()
	{
		Cursor cursor = db.rawQuery("SELECT * FROM Current_Words WHERE isRight = 1" , null);
		
		int count = cursor.getCount();
		cursor.close();
		
		return count;
	}
	
	public int getWrongCount()
	{
		Cursor cursor = db.rawQuery("SELECT * FROM Current_Words WHERE isWrong = 1" , null);
		
		int count = cursor.getCount();
		cursor.close();
		
		return count;
	}
	
	public ArrayList<Word> getCurrentWords(){

		ArrayList<Word> arrays = new ArrayList<Word>();
//
//		Cursor cursor = db.rawQuery("SELECT * FROM Current_Words WHERE isRight = 0" , null);
//		
//		cursor.moveToFirst();
//		Word word;
//
//		while(!cursor.isAfterLast()){
//
////			public Word(int _id, String word, String meaning, int difficulty, int priority, double score, int state, int time, int frequency,
////					boolean isRight, boolean isWrong, int wrongCount)
//			word = new Word(cursor.getInt(0),
//					cursor.getString(1),
//					cursor.getString(2),
//					cursor.getInt(3),
//					cursor.getInt(4),
//					cursor.getDouble(5),
//					cursor.getInt(6),
//					cursor.getInt(7),
//					cursor.getInt(8),
//					cursor.getInt(9) > 0,
//					cursor.getInt(10) > 0,
//					cursor.getInt(11));
//			
//			arrays.add(word);
//			cursor.moveToNext();
//		}
//
//		cursor.close();
		return arrays;
	}
	
}
