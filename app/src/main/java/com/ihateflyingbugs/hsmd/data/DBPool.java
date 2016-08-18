package com.ihateflyingbugs.hsmd.data;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabaseLockedException;
import android.database.sqlite.SQLiteException;
import android.os.Environment;
import android.os.SystemClock;
import android.util.Log;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.ihateflyingbugs.hsmd.CSVWriter;
import com.ihateflyingbugs.hsmd.OfflineLesson.OLActivity.SampleTextPopup;
import com.ihateflyingbugs.hsmd.ReviewTutorialActivity;
import com.ihateflyingbugs.hsmd.login.MainActivitys;
import com.ihateflyingbugs.hsmd.model.WordUpdateData;
import com.ihateflyingbugs.hsmd.tutorial.Feed;

import org.json.JSONArray;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import hirondelle.date4j.DateTime;

public class DBPool {

    private String DB_FILE_PATH;

    private static DBPool instance;
    private SQLiteDatabase db;
    private Context mconContext;
    private SharedPreferences settings;

    int version;
    Activity mActivity;

    private DBPool(Context context) {

        DB_FILE_PATH = Config.DB_FILE_DIR + Config.DB_NAME;
        mconContext = context;

        settings = mconContext.getSharedPreferences(Config.PREFS_NAME, Context.MODE_WORLD_READABLE | Context.MODE_WORLD_WRITEABLE);

        File file = new File(Config.DB_FILE_DIR);
        if (!file.exists() && !file.mkdirs())
            return;

        file = new File(DB_FILE_PATH);
        if (!file.exists()) {
            // file copy
            try {
                InputStream is = context.getAssets().open(Config.DB_NAME);
                OutputStream os = new FileOutputStream(DB_FILE_PATH);

                byte[] buffer = new byte[1024];
                int len;
                while ((len = is.read(buffer)) > 0)
                    os.write(buffer, 0, len);

                os.flush();
                os.close();
                is.close();
            } catch (IOException e) {
                return;
            }
        }

        try {

            // db = SQLiteDatabase.openOrCreateDatabase(file, "test123", null);
            DBHelper helper = new DBHelper(context);
            db = helper.getWritableDatabase();
            // db = SQLiteDatabase.openDatabase(DB_FILE_PATH, null,
            // SQLiteDatabase.OPEN_READWRITE
            // | SQLiteDatabase.NO_LOCALIZED_COLLATORS);

            version = db.getVersion();
            Log.e("asdf", "" + version);

            // initialized = true;
        } catch (Exception e) {
            // if(Config.LOG)
            // Log.e(LogHelper.where(), e.toString());
            Log.e("asdf", "" + e.toString());
        }
    }

    public void resetDB(Context context) {

        db.close();

        File file = new File(DB_FILE_PATH);

        if (file.exists() && false == file.delete()) {
            return;
        }

        // file copy
        try {
            InputStream is = context.getAssets().open(Config.DB_NAME);
            OutputStream os = new FileOutputStream(DB_FILE_PATH);

            byte[] buffer = new byte[1024];
            int len;
            while ((len = is.read(buffer)) > 0)
                os.write(buffer, 0, len);

            os.flush();
            os.close();
            is.close();
        } catch (IOException e) {
            return;
        }

        try {

            // db = SQLiteDatabase.openOrCreateDatabase(file, "test123", null);
            db = SQLiteDatabase.openDatabase(DB_FILE_PATH, null, SQLiteDatabase.OPEN_READWRITE | SQLiteDatabase.NO_LOCALIZED_COLLATORS);
        } catch (Exception e) {
        }
    }

    public static DBPool getInstance(Context context) {

        if (instance == null) {
            instance = new DBPool(context);
        }

        return instance;
    }

    public void release() {

        db.close();

        if (instance != null) {
            instance = null;
        }
    }

    public void setActivity(Activity activity) {
        mActivity = activity;
    }


    public ArrayList<Word> wordsWithScore() {

        // 'forgetting_curves_test_flag'열 추가
        Log.d("test1", "WordsWithScore in");
        ArrayList<Word> words = new ArrayList<Word>();

        Cursor cursor = db.rawQuery("SELECT * FROM words WHERE Score IS NOT NULL AND Score > 0 ORDER BY Score DESC ", null);
        cursor.moveToFirst();


        Log.v("MemoryPushDialog", "wordsWithScore step1");

        // 망각곡선이 높아서 나오지 않는 단어 추가 점검 : '15.01.22(Thu) 추가
        Cursor forgettingCurvesTestFlagCursor = db.rawQuery(" SELECT * FROM words WHERE forgetting_curves_test_flag = 1 ", null);
        forgettingCurvesTestFlagCursor.moveToFirst();


        Log.v("MemoryPushDialog", "wordsWithScore step2");

        int count = forgettingCurvesTestFlagCursor.getCount();
        forgettingCurvesTestFlagCursor.close();

        if (count == 0) {
            int addReviewCount = (cursor.getCount() / 20 > 5) ? 5 : cursor.getCount() / 20;
            Log.d("test1", addReviewCount + " addReviewCount");
            try {
                db.execSQL(" UPDATE words SET forgetting_curves_test_flag = 1 " + " WHERE word_code IN( " + " 	SELECT word_code FROM("
                        + "		SELECT word_code, word, t, State, frequency, time, time-t AS dif " + "	  	FROM("
                        + "			SELECT a.word_code, a.word, a.time AS t, a.State, b.frequency, b.time "
                        + "		 	FROM words a LEFT JOIN forgetting_curves b ON a.state=b.state " + "		 	WHERE a.Score<=0  AND a.State>0 AND a.State<"
                        + stateCnt + 1 + " AND b.frequency>0 AND b.frequency<20 " + "		) AS t1 " + "  		WHERE dif>=0 ORDER BY dif DESC" + " 	)"
                        + " 	GROUP BY word_code " + " 	ORDER BY frequency, dif LIMIT " + addReviewCount + " )");
            } catch (Exception e) {
                Log.e("test1", "catch문");
            }
        }
        cursor.close();


        Log.v("MemoryPushDialog", "wordsWithScore step3");

        cursor = db.rawQuery(
                " SELECT * FROM words WHERE Score IS NOT NULL AND Score > 0 OR forgetting_curves_test_flag=1 ORDER BY Score DESC limit ?",
                new String[]{String.valueOf(Config.ONCE_WORD_COUNT),});
        cursor.moveToFirst();


        Log.v("MemoryPushDialog", "wordsWithScore step4");

        Word word = null;

        while (!cursor.isAfterLast()) {

            word = new Word(cursor.getInt(0), cursor.getString(1), cursor.getInt(2), cursor.getInt(3), cursor.getInt(4), cursor.getDouble(5),
                    cursor.getInt(6), cursor.getInt(7), cursor.getInt(8), mconContext, mActivity);

            words.add(word);
            cursor.moveToNext();
        }

        Log.v("MemoryPushDialog", "wordsWithScore step5");

        cursor.close();


        for (int i = 0; i < words.size(); i++) {
            words.get(i).setMeanList(getMean(words.get(i).get_id()));

        }


        Log.v("MemoryPushDialog", "wordsWithScore step6");

        return words;
    }

    public ArrayList<Word> wordsWithScoreInMPopup() {

        // 'forgetting_curves_test_flag'열 추가
        Log.d("test1", "WordsWithScore in");
        ArrayList<Word> words = new ArrayList<Word>();

        Cursor cursor = db.rawQuery("SELECT * FROM words WHERE Score IS NOT NULL AND Score > 0 ORDER BY Score DESC ", null);
        cursor.moveToFirst();


        Log.v("MemoryPushDialog", "wordsWithScore step1");

        // 망각곡선이 높아서 나오지 않는 단어 추가 점검 : '15.01.22(Thu) 추가
        Cursor forgettingCurvesTestFlagCursor = db.rawQuery(" SELECT * FROM words WHERE forgetting_curves_test_flag = 1 ", null);
        forgettingCurvesTestFlagCursor.moveToFirst();


        Log.v("MemoryPushDialog", "wordsWithScore step2");

        int count = forgettingCurvesTestFlagCursor.getCount();
        forgettingCurvesTestFlagCursor.close();

        if (count == 0) {
            int addReviewCount = (cursor.getCount() / 20 > 5) ? 5 : cursor.getCount() / 20;
            Log.d("test1", addReviewCount + " addReviewCount");
            try {
                db.execSQL(" UPDATE words SET forgetting_curves_test_flag = 1 " + " WHERE word_code IN( " + " 	SELECT word_code FROM("
                        + "		SELECT word_code, word, t, State, frequency, time, time-t AS dif " + "	  	FROM("
                        + "			SELECT a.word_code, a.word, a.time AS t, a.State, b.frequency, b.time "
                        + "		 	FROM words a LEFT JOIN forgetting_curves b ON a.state=b.state " + "		 	WHERE a.Score<=0  AND a.State>0 AND a.State<"
                        + stateCnt + 1 + " AND b.frequency>0 AND b.frequency<20 " + "		) AS t1 " + "  		WHERE dif>=0 ORDER BY dif DESC" + " 	)"
                        + " 	GROUP BY word_code " + " 	ORDER BY frequency, dif LIMIT " + addReviewCount + " )");
            } catch (Exception e) {
                Log.e("test1", "catch문");
            }
        }
        cursor.close();


        Log.v("MemoryPushDialog", "wordsWithScore step3");


        cursor = db.rawQuery(
                " SELECT * FROM words WHERE Score IS NOT NULL AND Score > 0 OR forgetting_curves_test_flag=1 ORDER BY Score DESC limit ?",
                new String[]{String.valueOf(Config.ONCE_WORD_COUNT),});
        cursor.moveToFirst();


        Log.v("MemoryPushDialog", "wordsWithScore step4");

        Word word = null;

        while (!cursor.isAfterLast()) {

            word = new Word(cursor.getInt(0), cursor.getString(1), cursor.getInt(2), cursor.getInt(3), cursor.getInt(4), cursor.getDouble(5),
                    cursor.getInt(6), cursor.getInt(7), cursor.getInt(8), mconContext, mActivity);

            words.add(word);
            cursor.moveToNext();
        }

        Log.v("MemoryPushDialog", "wordsWithScore step5");

        cursor.close();


        Log.v("MemoryPushDialog", "wordsWithScore step6");

        return words;
    }


    public ArrayList<Word> wordsWithUnknown() {

        ArrayList<Word> words = new ArrayList<Word>();

        Cursor cursor = db.rawQuery("SELECT * FROM words WHERE State = -1 ORDER BY Frequency DESC limit ?",
                new String[]{String.valueOf(Config.ONCE_WORD_COUNT)});

        Log.w("kjw", "wordsWithUnknown SELECT * FROM words WHERE State = -1 ORDER BY Frequency DESC limit 30");
        cursor.moveToFirst();
        Word word = null;

        while (!cursor.isAfterLast()) {

            word = new Word(cursor.getInt(0), cursor.getString(1), cursor.getInt(2), cursor.getInt(3), cursor.getInt(4), cursor.getDouble(5),
                    cursor.getInt(6), cursor.getInt(7), cursor.getInt(8), mconContext, mActivity);

            words.add(word);
            cursor.moveToNext();
        }

        cursor.close();

        try {

            db.beginTransaction();
            for (int i = 0; i < words.size(); i++) {
                words.get(i).setMeanList(getMean(words.get(i).get_id()));

            }

            db.setTransactionSuccessful();
        } catch (SQLException e) {
            // TODO: handle exception
        } finally {
            db.endTransaction();
        }
        return words;
    }

    public ArrayList<Word> wordsWithUnknown_LockScreen() {

        ArrayList<Word> words = new ArrayList<Word>();

        Cursor cursor = db.rawQuery("SELECT * FROM words WHERE State = -1 ORDER BY Frequency DESC limit ?",
                new String[]{String.valueOf(Config.ONCE_WORD_COUNT)});

        Log.w("kjw", "wordsWithUnknown SELECT * FROM words WHERE State = -1 ORDER BY Frequency DESC limit 30");
        cursor.moveToFirst();
        Word word = null;

        while (!cursor.isAfterLast()) {

            word = new Word(cursor.getInt(0), cursor.getString(1), cursor.getInt(2), cursor.getInt(3), cursor.getInt(4), cursor.getDouble(5),
                    cursor.getInt(6), cursor.getInt(7), cursor.getInt(8), mconContext, mActivity);

            words.add(word);
            cursor.moveToNext();
        }

        cursor.close();

        try {

            db.beginTransaction();

            db.setTransactionSuccessful();
        } catch (SQLException e) {
            // TODO: handle exception
        } finally {
            db.endTransaction();
        }
        return words;
    }

    public ArrayList<Word> wordsWithLevel(int level, int limit) {

        ArrayList<Word> words = new ArrayList<Word>();
        Cursor cursor;

        boolean isInsert = insertRandomValue();

        try {
            cursor = db.rawQuery("SELECT * FROM words WHERE Difficulty = ? AND State = 0 ORDER BY value DESC limit ?",
                    new String[]{String.valueOf(level), String.valueOf(limit),});
            Log.v("isInsert", "success : already_exist");

        } catch (Exception e) {
            // TODO: handle exception
            insertRandomValue();
            cursor = db.rawQuery("SELECT * FROM words WHERE Difficulty = ? AND State = 0 ORDER BY Score DESC limit ?",
                    new String[]{String.valueOf(level), String.valueOf(limit),});
            Log.v("isInsert", "success : fali_insert_and_getWord");
            Log.v("isInsert", "success : already_exist");
        }

        Log.w("kjw", "wordsWithLevel SELECT * FROM words WHERE Difficulty = " + level + " AND State = 0 ORDER BY Score DESC limit " + limit);
        cursor.moveToFirst();
        Word word = null;

        while (!cursor.isAfterLast()) {

            word = new Word(cursor.getInt(0), cursor.getString(1), cursor.getInt(2), cursor.getInt(3), cursor.getInt(4), cursor.getDouble(5),
                    cursor.getInt(6), cursor.getInt(7), cursor.getInt(8), mconContext, mActivity);

            words.add(word);
            cursor.moveToNext();
        }

        cursor.close();

        try {

            for (int i = 0; i < words.size(); i++) {
                words.get(i).setMeanList(getMean(words.get(i).get_id()));

            }
        } catch (SQLException e) {
            // TODO: handle exception
        }

        return words;
    }

    public boolean insertRandomValue() {

        boolean isInsert = false;

        try {

            Cursor cursor = db.rawQuery("SELECT sql FROM sqlite_master WHERE name='words' AND sql LIKE '%value%'", null);

            if (cursor.getCount() == 0) {
                db.execSQL("ALTER TABLE words ADD COLUMN value INTEGER");
                Log.e("isInsert", "isInsert : value is not exist, so create table");
            }

            Cursor cursor2 = db.rawQuery("select * from words where value is null", null);

            if (cursor2.getCount() > 0) {
                Log.e("isInsert", "isInsert : value already not had value, so insert radaom value");
                db.execSQL("Update words set value = abs(random())%1000000 where value is null");
            } else {
                Log.e("isInsert", "isInsert : value already had value");
            }
            // db.execSQL("CREATE INDEX value ON words(word_code, value)");

            cursor.close();
            cursor2.close();
            isInsert = true;
        } catch (Exception e) {
            // TODO: handle exception
            Log.e("isInsert", "" + e);
            isInsert = false;
        }

        return isInsert;

    }

    public ArrayList<Word> wordsWithKnown() {

        ArrayList<Word> words = new ArrayList<Word>();

        Cursor cursor = db.rawQuery("SELECT * FROM words WHERE State > 0 ORDER BY Score", null);
        cursor.moveToFirst();
        Word word = null;

        while (!cursor.isAfterLast()) {

            word = new Word(cursor.getInt(0), cursor.getString(1), cursor.getInt(2), cursor.getInt(3), cursor.getInt(4), cursor.getDouble(5),
                    cursor.getInt(6), cursor.getInt(7), cursor.getInt(8), false, mconContext, mActivity);
            words.add(word);

            cursor.moveToNext();
        }

        cursor.close();

        // try {
        //
        // db.beginTransaction();
        // for(int i =0; i <words.size();i++){
        // words.get(i).setMeanList(getMean(words.get(i).get_id()));
        //
        // }
        // }
        // catch (SQLException e) {
        // // TODO: handle exception
        // }finally{
        // db.endTransaction();
        // }
        return words;
    }

    public Word wordWithScore() {

        Cursor cursor = db.rawQuery("SELECT * FROM words WHERE Score IS NOT NULL AND Score > 0 ORDER BY Score DESC limit 1", null);

        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            Word word;

            word = new Word(cursor.getInt(0), cursor.getString(1), cursor.getInt(2), cursor.getInt(3), cursor.getInt(4), cursor.getDouble(5),
                    cursor.getInt(6), cursor.getInt(7), cursor.getInt(8), mconContext, mActivity);

            cursor.close();
            word.setMeanList(getMean(word.get_id()));

            return word;
        } else {
            cursor.close();
            return null;
        }
    }

    // public ArrayList<Word> wordWithUnknown(){
    //
    // Cursor cursor =
    // db.rawQuery("SELECT * FROM words WHERE State = -1 ORDER BY Score DESC limit 1",
    // null);
    // cursor.moveToFirst();
    // Word word;
    //
    // while(!cursor.isAfterLast()){
    //
    // word = new Word(cursor.getInt(0),
    // cursor.getString(1),
    // cursor.getString(2),
    // cursor.getInt(3),
    // cursor.getInt(4),
    // cursor.getDouble(5),
    // cursor.getInt(6),
    // cursor.getInt(7),
    // cursor.getInt(8));
    //
    // words.add(word);
    // cursor.moveToNext();
    // }
    // cursor.close();
    // return words;
    // }

    public float getProbabilityWithStateAndHour(int hour, int state) {

        if (state == 0 || hour > 720 || hour < 1) {
            return -1;
        }

        // if(state == -1)
        // return 0;

        String column = "nine";

        switch (state) {
            case 1:
                column = "one";
                break;
            case 2:
                column = "two";
                break;
            case 3:
                column = "three";
                break;
            case 4:
                column = "four";
                break;
            case 5:
                column = "five";
                break;
            case 6:
                column = "six";
                break;
            case 7:
                column = "seven";
                break;
            case 8:
                column = "eight";
                break;
            case 9:
                column = "nine";
                break;
            default:
                column = "nine";
                break;
        }

        // updateProbabilityCount(hour, updateColumn);

        Cursor cursor = db.rawQuery("SELECT * FROM times WHERE Time = " + hour, null);
        cursor.moveToFirst();

        float probability = (float) cursor.getFloat(cursor.getColumnIndex(column));

        cursor.close();
        // return probability / 100;
        return probability;
    }

    synchronized public boolean updateProbabilityCount(int hour, int state) {

        String column = "ninth";

        switch (state) {
            case 1:
                column = "first";
                break;
            case 2:
                column = "second";
                break;
            case 3:
                column = "third";
                break;
            case 4:
                column = "fourth";
                break;
            case 5:
                column = "fifth";
                break;
            case 6:
                column = "sixth";
                break;
            case 7:
                column = "seventh";
                break;
            case 8:
                column = "eighth";
                break;
            case 9:
                column = "ninth";
                break;
            default:
                column = "ninth";
                break;
        }

        Cursor cursor = db.rawQuery("SELECT * FROM times WHERE Time = " + hour, null);
        cursor.moveToFirst();

        int value = cursor.getInt(cursor.getColumnIndex(column));

        ContentValues values = new ContentValues();
        values.put(column, value + 1);

        // values.put("Frequency", ++frequency);
        cursor.close();
        return db.update("times", values, "Time = ?", new String[]{String.valueOf(hour),}) >= 0;
    }

    synchronized public void insertLevel(Word word, boolean isKnown) {

        Log.d("kjw", "insert level difficulty = " + word.getDifficulty() + "    s = " + isKnown);

        // ContentValues values = new ContentValues();
        // values.put("Difficulty", difficulty);
        // values.put("isKnown", isKnown);
        //
        // int result = (int) db.insert("level", null, values);

        try {
            if (word.getExState() == 0) {
                db.execSQL("insert into level(difficulty, isKnown) values (" + word.getDifficulty() + ", '" + isKnown + "')");
            }
        } catch (NumberFormatException e) {
            // TODO: handle exception
        }

        // Log.d("kjw", "insert result = " + result);
        // return db.update("level", values, "Time = ?", new
        // String[]{String.valueOf(hour),}) >= 0;
    }

    public void insertTrueCount(int difficulty) {

        int count = Config.CHANGE_LEVEL_COUNT;
        for (int i = 0; i < count; i++) {
            db.execSQL("insert into level(difficulty, isKnown) values (" + difficulty + ", '" + true + "')");
        }
        // Log.d("kjw", "insert result = " + result);
        // return db.update("level", values, "Time = ?", new
        // String[]{String.valueOf(hour),}) >= 0;
    }

    public void insertFalseCount(int difficulty) {

        int count = 1;

        db.execSQL("insert into level(difficulty, isKnown) values (" + difficulty + ", '" + false + "')");

        // Log.d("kjw", "insert result = " + result);
        // return db.update("level", values, "Time = ?", new
        // String[]{String.valueOf(hour),}) >= 0;
    }

    public String[] get_index(int index) {
        String[] index_name = new String[2];
        switch (index) {
            case 1:
                index_name[0] = "one";
                index_name[1] = "first";
                break;
            case 2:
                index_name[0] = "two";
                index_name[1] = "second";
                break;
            case 3:
                index_name[0] = "three";
                index_name[1] = "third";
                break;
            case 4:
                index_name[0] = "four";
                index_name[1] = "fourth";
                break;
            case 5:
                index_name[0] = "five";
                index_name[1] = "fifth";
                break;
            case 6:
                index_name[0] = "six";
                index_name[1] = "sixth";
                break;
            case 7:
                index_name[0] = "seven";
                index_name[1] = "seventh";
                break;
            case 8:
                index_name[0] = "eight";
                index_name[1] = "eighth";
                break;
            case 9:
                index_name[0] = "nine";
                index_name[1] = "ninth";
                break;
            default:
                index_name[0] = "nine";
                index_name[1] = "ninth";
                break;
        }
        return index_name;
    }

    synchronized public void test_TimeTable(int index) {

        Log.e("kjws", "calcProbility    start");
        if (index < 1 || index > 720) {

            Log.e("kjws", "calcProbility    return");
            return;

        }

        String[] string = get_index(index);

        // Cursor cursor =
        // db.rawQuery("SELECT Time, one, first, two, second, three, third, four, fourth, five, fifth,"
        // +
        // " six, sixth, seven, seventh, eight, eighth, nine, ninth, nine, ninth"
        // +
        // " FROM times WHERE first IS NOT NULL or second IS NOT NULL or third IS NOT NULL or fourth IS NOT NULL or fifth IS NOT NULL or sixth IS NOT NULL or seventh IS NOT NULL or "
        // +
        // "eighth IS NOT NULL or ninth IS NOT NULL " , null);

        Cursor cursor = db.rawQuery("SELECT Time, one, first, two, second, three, third, four, fourth, five, fifth,"
                + " six, sixth, seven, seventh, eight, eighth, nine, ninth" + " FROM times WHERE Time =  " + index, null);

        float probility0 = -1, probility1 = -1, probility2 = -1;
        int count0 = -1, count1 = -1, time0 = -1, time1 = -1, time2 = -1;

        boolean isCountUpdate = false;
        String query1 = "";
        String query2 = "";

        int Time = -1;
        float[] column0 = {-1, -1, -1, -1, -1, -1, -1, -1, -1};
        Integer[] column1 = {-1, -1, -1, -1, -1, -1, -1, -1, -1};

        cursor.moveToFirst();
        int j = 0;
        String value = "";
        String value2 = "";
        for (int i = 0; i < 19; i++) {
            if (i == 0) {
                Time = cursor.getInt(0);
                value += Time + "\n";
            } else if (i % 2 == 1) {
                column0[i / 2] = cursor.getFloat(i);
                value += cursor.getFloat(i) + ",  ";
            } else if (i % 2 == 0) {
                column1[i / 2 - 1] = cursor.getInt(i);
                value2 += cursor.getInt(i) + ",  ";
            }
        }
        Log.d("column", value + "\n" + value2);

        // String query = "";
        // if(isCountUpdate){
        // query = query1+ " End, "+ query2+" End";
        // }else{
        // query = query1+ " End";
        // }
        //
        // if(cursor.getCount()>0){
        // db.execSQL(query);
        // }
        cursor.close();

    }

    /**
     * 2014/03/10 업데이트 대규모 수정 들어감 (Transaction사용, array와 query문 동시 사용)
     */
    public void calcProbility() {

        try {

            db.beginTransaction();
            // x가 9 까지인 이유는 망각곡선의 갯수가 9개가 있으므로 one~nine first~ninth
            for (int x = 1; x <= stateCnt + 1; x++) // for(int x = 1; x <= 9;
            // x++)
            {

                Log.e("alarms", "calcProbility large for loop : " + x);
                int index = x;

                if (index < 0 || index > stateCnt + 1) {

                    db.endTransaction();
                    Log.e("alarms", "idx exception");
                    return;

                }

                int tmp = 1;

                String[] string = get_index(index);

                // 타임 테이블에 한 한 컬럼 쌍을 가져온다 (ex, one&first 컬럼)
                Log.e("alarms", "cursor raw query");
                Cursor cursor = db.rawQuery("SELECT Time, " + string[0] + ", " + string[1] + " FROM times WHERE " + string[1] + " IS NOT NULL", null);
                // 해당 index에 대해, 단어가 노출된 지 얼마나 지났는지 모두 선택
                // 초기화
                float probility0 = -1, probility1 = -1, probility2 = -1;
                int count0 = -1, count1 = -1, time0 = -1, time1 = -1, time2 = -1;

                List<Time> list_time = new ArrayList<Time>();

				/*
                 *
				 * 커서가 0이 아닐때 time이라는 객체에 결과값을 담고 array리스트에 삽입
				 *
				 * 커서가 0일때
				 *
				 * 아무것도 없으므로 함수 종료
				 */
                if (cursor.getCount() != 0) {
                    cursor.moveToFirst();
                    do {
                        Time time = new Time(cursor.getInt(0), cursor.getFloat(1), cursor.getInt(2)); // Time,
                        // cardinal
                        // number(기수),
                        // ordinal
                        // number(서수)
                        list_time.add(time);
                    } while (cursor.moveToNext());
                } else {
                    ;
                    // 19 modify : make annotation
                    // cursor.close();
                    // return;
                }

				/*
                 * list_time 의 사이즈가 1일
				 *
				 * 한 first열에 정보가 1개만 들어가 있으므로 해당열 기준으로 위와 밑으로 곡선을 그린다 (y =
				 * 1/(x+1)의 그래프)
				 *
				 *
				 * list_time 의 사이즈가 1보다 클때
				 *
				 * 2개 이상의 값이 있따는 뜻이므로 리스트 두 값사이의 확률은 평균값으로 그 이외에 위와 밑으로는 곡선을 그린다
				 * (y = 1/(x+1) 의 그래프)
				 *
				 * 그뒤에 각 확률간의 차가 0.005보다 크면 다시 한번 while문을 돌아서 평균값을 줄여서 각 값간의 차가
				 * 0.005이하로 만든다 (플로팅 작업)
				 */

                cursor.moveToFirst();
                if (list_time.size() == 1) {
                    Log.e("alarms", "if clause, list_time.size() : " + list_time.size());
                    if (probility0 == -1 && time0 == -1) {
                        time0 = list_time.get(0).get_Time();
                        probility0 = list_time.get(0).get_column0();

                        double a = (1 - probility0) / (probility0 * time0);

                        // 19 modify
                        // string[0] < probility0이 필요한가??????
                        db.execSQL("update times set " + string[0] + " = 1 /((time * ?) + 1) WHERE time < ?", // string[0]
                                // :
                                // probabiltiy
                                new String[]{String.valueOf(a), String.valueOf(time0)});

                        db.execSQL("update times set " + string[0] + " = 1 /((time * ?) + 1) WHERE time > ?", new String[]{String.valueOf(a),
                                String.valueOf(time0)});

						/*
                         * db.execSQL("update times set " + string[0] +
						 * " = 1 /((time * ?) + 1) WHERE time < ? and " +
						 * string[0] +" < ?", //string[0] : probabiltiy new
						 * String[]{ String.valueOf(a), String.valueOf(time0),
						 * String.valueOf(probility0),});
						 *
						 * db.execSQL("update times set " + string[0] +
						 * " = 1 /((time * ?) + 1) WHERE time > ? and " +
						 * string[0] +"> ?", new String[]{ String.valueOf(a),
						 * String.valueOf(time0), String.valueOf(probility0),});
						 */
                    }

                    tmp = 0;

                    // 19 modify : make annotation
                    // cursor.close();

                    // return;

                } else if (list_time.size() > 1) {
                    // Log.v("alarms", "calcProbility");
                    Log.e("alarms", "else if clause, list_time.size() : " + list_time.size());
                    int loop_cnt = 0; // added!!
                    while (tmp == 1) {
                        int position = 0;
                        tmp = 0;
                        loop_cnt++; // loop cnt is added!!

                        // int idx=0;
                        // while(idx++<2){ //counting
                        while (list_time.size() - 1 != position) { // counting
                            if (probility0 == -1 && time0 == -1) {
                                time0 = list_time.get(position).get_Time();
                                count0 = list_time.get(position).get_column1();
                                probility0 = list_time.get(position).get_column0();
                                // Log.e("pro0 is -1", "pro0 is -1");
                            }

                            if (probility1 == -1 && time1 == -1) {

                                position++;

                                time1 = list_time.get(position).get_Time();
                                count1 = list_time.get(position).get_column1();
                                probility1 = list_time.get(position).get_column0();

                                if (time1 == time0) // error handling
                                {
                                    position++;
                                    continue;
                                }

                                float m = (float) (probility1 - probility0) / (time1 - time0); // 기울기

                                // 시간이 더 흐른뒤에의 알 확률이 높아졌을 플로팅 작업을 하는 부분
                                if (m >= 0) {
                                    // (P1*Np1+P2*Np2)/(Np1+Np2)

                                    float t = (float) (probility0 * count0 + probility1 * count1) / (count0 + count1); // mean

                                    list_time.get(position).set_column0(t);
                                    list_time.get(position - 1).set_column0(t);

                                    int c = (count0 + count1) / 2; // 나중에 이 결과가
                                    // 상관있지
                                    // 않을까????
                                    list_time.get(position).set_column1(c);
                                    list_time.get(position - 1).set_column1(c);
									/*
									 * 나중에 loop_cnt 값으로 로그를 심어놓구 그런경우가 어느정도
									 * 발생하는지 평균을 내보자 !
									 */
                                    if (m > 0.005) {
                                        tmp = 1; // 루프 더 돌아라
                                    }
                                }
                                // 일반적인 망강곡선의 값을 가졌을땐 else문으로
                                else {

                                    // /////////////
                                    list_time.get(position - 1).set_column0(m * time0 - m * time0 + probility0); // 아무
                                    // 의미
                                    // 없다
                                    list_time.get(position).set_column0(m * time1 - m * time0 + probility0); //

                                }

                            }

                            time0 = list_time.get(position).get_Time(); // 19
                            // modify
                            count0 = list_time.get(position).get_column1(); // 19
                            // modify
                            probility0 = list_time.get(position).get_column0(); // 19
                            // modify

                            // time0 = time1; //for next step
                            // count0 = count1; //
                            // probility0 = probility1; //

                            time1 = -1; //
                            count1 = -1; //
                            probility1 = -1; //

                            time2 = -1; //
                            probility2 = -1; //

                        }
                        time0 = -1; // initialize
                        count0 = -1; //
                        probility0 = -1; //

                    }
                    // Log.v("alarms", "calcProbility");

					/*
					 * 평탄화 작업이 끝나면 리스트에 담긴 값을 대상으로 time을 업데이트 한다
					 */
                    Log.e("alarms", "calcProbility changing is ended");
                    for (int i = 0; i < list_time.size(); i++) {
                        Log.e("test", "probability" + i + " : " + list_time.get(i).get_column0());
                    }
                    if (tmp != 1) {
                        // Log.d("alarms", "calcProbility");
                        Log.e("alarms", "execute query");

                        if (list_time.size() > 1) {

                            time0 = -1;
                            count0 = -1;
                            probility0 = -1;

                            time1 = -1;
                            count1 = -1;
                            probility1 = -1;

                            time2 = -1;
                            probility2 = -1;

                            int position = 0;
                            // int idx=0;
                            // while(idx++<2){ //counting
                            while (position != list_time.size() - 1) { // counting

                                if (probility0 == -1 && time0 == -1) {
                                    time0 = list_time.get(position).get_Time();
                                    count0 = list_time.get(position).get_column1();
                                    probility0 = list_time.get(position).get_column0();

                                }

                                if (probility1 == -1 && time1 == -1) {

                                    position++;
                                    time1 = list_time.get(position).get_Time();
                                    count1 = list_time.get(position).get_column1();
                                    probility1 = list_time.get(position).get_column0();

                                    if (time1 == time0) // error handling
                                    {
                                        position++;
                                        continue;
                                    }

                                    float m = (float) (probility1 - probility0) / (time1 - time0);

                                    // update times set first = one * time WHERE
                                    // time between 1 and 5

                                    // 19 modify : cardinal num: probability
                                    // which is common part
                                    db.execSQL(
                                            "update times set " + string[0] + " = ? * (time - ?) + ? WHERE time between ? and ?",
                                            new String[]{String.valueOf(m), String.valueOf(time0), String.valueOf(probility0),
                                                    String.valueOf(time0), String.valueOf(time1)});

                                    if (m >= 0) // 비정상적 기울기
                                    {
                                        // (P1*Np1+P2*Np2)/(Np1+Np2)
										/*
										 * //19 modify : make annotation //float
										 * t = (float)(probility0 * count0 +
										 * probility1 * count1) / (count0 +
										 * count1); //mean
										 *
										 * //int c = (count0 + count1)/2;
										 * //count
										 *
										 * db.execSQL("update times set " +
										 * string[0] +
										 * " = ? WHERE time between ? and ?",
										 * //cardinal num: probability new
										 * String[]{ String.valueOf(t),
										 * String.valueOf(time0),
										 * String.valueOf(time1),});
										 *
										 * db.execSQL("update times set " +
										 * string[1] +
										 * " = ? WHERE time = ? or time = ?",
										 * //ordinal num: count new String[]{
										 * String.valueOf(c),
										 * String.valueOf(time0),
										 * String.valueOf(time1),});
										 */

                                        // 19 modify : ordinal num: count
                                        // update count separately
                                        db.execSQL("update times set " + string[1] + " = ? WHERE time = ?", new String[]{String.valueOf(count0),
                                                String.valueOf(time0)});
                                        //
                                        db.execSQL("update times set " + string[1] + " = ? WHERE time = ?", new String[]{String.valueOf(count1),
                                                String.valueOf(time1)});

                                    } else // 정상적
                                    {
                                        ;
                                    }

                                }

								/*
								 * 19 modify if(list_time.get(0).get_Time()!=1){
								 * //list_time에서 Time의 첫번째 값이 1이 아닐 때 : 그래프는
								 * (0,1)을 지나야 한다 -> 연결해줌 time0 =
								 * list_time.get(0).get_Time(); probility0 =
								 * list_time.get(0).get_column0();
								 *
								 * double a = ( 1 - probility0 ) / (probility0 *
								 * time0);
								 *
								 * db.execSQL("update times set " + string[0] +
								 * " = 1 /((time * ?) + 1) WHERE time < ? and "
								 * + string[0] +" < ?", new String[]{
								 * String.valueOf(a), String.valueOf(time0),
								 * String.valueOf(probility0),}); }
								 *
								 * if(list_time.get(position).get_Time()!=720&&
								 * list_time.size()-1==position){ //list_time에서
								 * Time의 마지막 값이 720이 아닐 때 : 연결해줌 time0 =
								 * list_time.get(position).get_Time();
								 * probility0 =
								 * list_time.get(position).get_column0();
								 *
								 * double a = ( 1 - probility0 ) / (probility0 *
								 * time0); db.execSQL("update times set " +
								 * string[0] +
								 * " = 1 /((time * ?) + 1) WHERE time > ? and "
								 * + string[0] +"> ?", new String[]{
								 * String.valueOf(a), String.valueOf(time0),
								 * String.valueOf(probility0),});
								 *
								 * }
								 */
                                time0 = time1;
                                count0 = count1;
                                probility0 = probility1;

                                time1 = -1;
                                count1 = -1;
                                probility1 = -1;
                            }

                            // 19 modify
                            if (list_time.get(0).get_Time() != 1) { // list_time에서
                                // Time의 첫번째
                                // 값이 1이 아닐
                                // 때 : 그래프는
                                // (0,1)을
                                // 지나야 한다 ->
                                // 연결해줌
                                time0 = list_time.get(0).get_Time();
                                probility0 = list_time.get(0).get_column0();

                                double a = (1 - probility0) / (probility0 * time0);

                                db.execSQL("update times set " + string[0] + " = 1 /((time * ?) + 1) WHERE time < ?",
                                        new String[]{String.valueOf(a), String.valueOf(time0)});
                            }
                            if (list_time.get(list_time.size() - 1).get_Time() != 720) { // 19
                                // modify
                                // 조건
                                // //list_time에서
                                // Time의
                                // 마지막
                                // 값이
                                // 720이
                                // 아닐
                                // 때
                                // :
                                // 연결해줌
                                time0 = list_time.get(position).get_Time();
                                probility0 = list_time.get(position).get_column0();

                                double a = (1 - probility0) / (probility0 * time0);
                                db.execSQL("update times set " + string[0] + " = 1 /((time * ?) + 1) WHERE time > ?",
                                        new String[]{String.valueOf(a), String.valueOf(time0)});

                            }
                        }

                        Log.d("alarms", "calcProbility");

                    }
                } else {
                    Log.e("alarms", "else clause, list_time.size() : " + list_time.size());
                }
                Log.e("alarms", "calcProbility large for loop : " + x + " is ended");
                cursor.close(); // 19 modify
            }
            Log.e("alarms", "transaction successful");
            db.setTransactionSuccessful();

        } catch (SQLException e) {
            // TODO: handle exception
        } finally {
            db.endTransaction();
        }
        Log.e("alarms", "calcProbility is ended");
    }

	/*
	 * ■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■
	 * ■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■ 다량의
	 * 데이터를 insert시킬때 시간을 최소화 시키는 방법은 트랜잭션이다. 25000 건 인서트에 0.914초
	 *
	 * but mysql 에서는 그것보다 시간이 더 걸린다.
	 * ■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■
	 * ■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■
	 * ■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■
	 *
	 *
	 * try{ db.beginTransaction(); db.insert(TABLE_NAME, null, VALUE);
	 * db.setTransactionSuccessful(); } catch (SQLException e){ } finally {
	 * db.endTransaction(); }
	 */

    public void test_calcScore(int Time_to_count) {

        try {
            db.beginTransaction();

            List<Time> list_time = new ArrayList<Time>();

            Cursor cursor_time = db.rawQuery("SELECT Time, one, first, two, second, three, third, four, fourth, five, fifth,"
                    + " six, sixth, seven, seventh, eight, eighth, nine, ninth" + " FROM times", null);
            cursor_time.moveToFirst();

            Log.e("almas", "asdfasdfasdf");

            while (!cursor_time.isAfterLast()) {
                Time time = new Time(cursor_time.getInt(0), cursor_time.getFloat(1), cursor_time.getInt(2), cursor_time.getFloat(3),
                        cursor_time.getInt(4), cursor_time.getFloat(5), cursor_time.getInt(6), cursor_time.getFloat(7), cursor_time.getInt(8),
                        cursor_time.getFloat(9), cursor_time.getInt(10), cursor_time.getFloat(11), cursor_time.getInt(12), cursor_time.getFloat(13),
                        cursor_time.getInt(14), cursor_time.getFloat(15), cursor_time.getInt(16), cursor_time.getFloat(17), cursor_time.getInt(18));
                list_time.add(time);
                cursor_time.moveToNext();

            }

            cursor_time.close();

            Cursor cursor = db.rawQuery("SELECT * FROM words WHERE State > 0", null);

            cursor.moveToFirst();
            Word word;

            Random random = new Random();
            random.setSeed(System.currentTimeMillis());

            while (!cursor.isAfterLast()) {

                word = new Word(cursor.getInt(0), cursor.getString(1), cursor.getInt(2), cursor.getInt(3), cursor.getInt(4), cursor.getDouble(5),
                        cursor.getInt(6), cursor.getInt(7), cursor.getInt(8), mconContext, mActivity);

                float probability = list_time.get(word.getTime(Time_to_count)).get_property(word.getState());
                // float probability =
                // getProbabilityWithStateAndHour(word.getTime(Time_to_count),
                // word.getFrequency());
                double score;
                if (probability == -1) {
                    cursor.moveToNext();
                    continue;
                }

                // else if(probability == 0)
                // score = word.getScore() * 10;

                // 올림((0.95+0.05/exp(random*20)-n번 외운 단어의 k시간대의 확률)*1000
                else
                    score = 0.95 - probability;
                // score = 0.95 + 0.05 / Math.exp(random.nextDouble() * 20) -
                // probability;

                updateScore(1, score, 1);
                // updateWordTime(word.get_id(), word.getTime());

                cursor.moveToNext();
            }

            cursor.close();
            db.setTransactionSuccessful();
        } catch (SQLException e) {
        } finally {
            db.endTransaction();
        }
    }

    public void calcScore(int Time_to_count) {

        try {
            db.beginTransaction();

            List<Time> list_time = new ArrayList<Time>();

            // 타임 테이블을 모두 가져온다
            Cursor cursor_time = db.rawQuery("SELECT Time, one, first, two, second, three, third, four, fourth, five, fifth,"
                    + " six, sixth, seven, seventh, eight, eighth, nine, ninth" + " FROM times", null);
            cursor_time.moveToFirst();

            Log.e("almas", "asdfasdfasdf");

            // 타임테이블을 arraylist에 담는다
            while (!cursor_time.isAfterLast()) {
                Time time = new Time(cursor_time.getInt(0), cursor_time.getFloat(1), cursor_time.getInt(2), cursor_time.getFloat(3),
                        cursor_time.getInt(4), cursor_time.getFloat(5), cursor_time.getInt(6), cursor_time.getFloat(7), cursor_time.getInt(8),
                        cursor_time.getFloat(9), cursor_time.getInt(10), cursor_time.getFloat(11), cursor_time.getInt(12), cursor_time.getFloat(13),
                        cursor_time.getInt(14), cursor_time.getFloat(15), cursor_time.getInt(16), cursor_time.getFloat(17), cursor_time.getInt(18));
                list_time.add(time);
                cursor_time.moveToNext();

            }

            cursor_time.close();

            // 현재 안다고 했던 단어들을 모두 가져온다
            Cursor cursor = db.rawQuery("SELECT * FROM words WHERE State > 0", null);

            cursor.moveToFirst();
            Word word;

            Random random = new Random();
            random.setSeed(System.currentTimeMillis());

            // 해쉬맵에 키값으로
            HashMap<String, String> hm = new HashMap<String, String>();
            while (!cursor.isAfterLast()) {

                int state = cursor.getInt(6);
                int time = cursor.getInt(7);

				/*
				 *
				 * state +s time으로 key값을 만듬 ex) state = 7, time = 20이면 key =
				 * 7s20;
				 */

                String key = String.valueOf(state) + "s" + String.valueOf(time);

                float probability;

                // 타임 테이블은 720이 끝이므로 720 으로 조건문걸어준다
                if (time + Time_to_count <= 719) {
                    probability = list_time.get(time + Time_to_count).get_property(state);
                } else {
                    probability = list_time.get(719).get_property(state);
                }

                double score;

                if (probability == -1) {
                    cursor.moveToNext();
                    continue;
                }

                // 올림((0.95+0.05/exp(random*20)-n번 외운 단어의 k시간대의 확률)*1000
                else
                    score = 0.90 + 0.10 / Math.exp(random.nextDouble() * 30) - probability;
                // score = 0.95 - probability;
                hm.put(key, String.valueOf(score));

                cursor.moveToNext();
            }
            if (hm.size() == 0) {
                return;
            }
            updateScore(hm, Time_to_count);
            cursor.close();
            db.setTransactionSuccessful();
        } catch (SQLException e) {
        } finally {
            db.endTransaction();
        }
    }

    synchronized public void updateScore(HashMap<String, String> hm, int Time_to_count) {

        try {
            db.beginTransaction();
            Set<String> st = hm.keySet();
            Iterator<String> it = st.iterator();

            while (it.hasNext()) {

                String key = it.next();
                String[] words = key.split("s");
                String state = words[0];
                String time = words[1];
                String value = hm.get(key);

                ContentValues values = new ContentValues();
                values.put("Score", value);

                db.update("words", values, "time = ? and state = ?", new String[]{time, state});

            }

            db.execSQL("update words set Time = Time +" + Time_to_count + " WHERE State>0");
            db.setTransactionSuccessful();
        } catch (SQLException e) {
        } finally {
            db.endTransaction();
        }

    }

    public void currentLeveling(int difficulty) {
        Cursor cursor = db.rawQuery("SELECT * FROM level WHERE difficulty = ? ORDER BY rowid DESC LIMIT 10 ",
                new String[]{String.valueOf(difficulty),});
        cursor.moveToFirst();
        cursor.close();
    }

    int lv_difficulty;
    boolean isShownDial = false;

    public int calcLevel(int count) {

        lv_difficulty = 1;
        Log.e("level", "calcLevel()   difficulty: " + String.valueOf(Config.Difficulty) + "   " + Config.MIN_DIFFICULTY);
        lv_difficulty = Config.Difficulty;
        int origin_diff = Config.Difficulty;

        boolean isCheck = false;
        for (int i = Config.MIN_DIFFICULTY; i <= lv_difficulty; i++) {
            Cursor cursor = db.rawQuery("SELECT Difficulty, " + "count(case when isKnown='true' then 1 else null end) as truecount, "
                    + "count(case when isKnown='false' then 1 else null end) as falsecount "
                    + "FROM (select * from level where difficulty = ? ORDER BY rowid DESC limit ?  ) " + "GROUP BY Difficulty ", new String[]{
                    String.valueOf(i), String.valueOf(count),});

            cursor.moveToFirst();

            Log.d("level", "query = "
                    + String.format("SELECT Difficulty, " + "count(case when isKnown='true' then 1 else null end) as truecount, "
                    + "count(case when isKnown='false' then 1 else null end) as falsecount "
                    + "FROM (select * from level where difficulty = %d ORDER BY rowid DESC limit 10  ) " + "GROUP BY Difficulty ", i));

            Log.e("level", "calcLevel()   .getCount: " + String.valueOf(cursor.getCount() + "    " + count));

            if (cursor.getCount() > 0) {

                int trueCount = cursor.getInt(cursor.getColumnIndex("truecount"));
                int falseCount = cursor.getInt(cursor.getColumnIndex("falsecount"));

                if (i == lv_difficulty) {
                    if (trueCount == count && lv_difficulty <= Config.MAX_DIFFICULTY) {
                        lv_difficulty++;
                    }
                } else {
                    Log.e("cursor.getCount() <= 0", "cursor.getCount() <= 0");
                    float c = (float) (trueCount / (trueCount + falseCount));
                    if (c < 0.9) {
                        if (Config.CHANGE_LEVEL_COUNT < 40) {
                            Config.CHANGE_LEVEL_COUNT += 10;
                            settings.edit().putString(MainActivitys.GpreLevelCounting, "" + Config.CHANGE_LEVEL_COUNT).commit();
                        }
                        lv_difficulty = i;
                        isCheck = true;
                        break;
                    }
                }

            }
            if (isCheck) {
                break;
            }
            cursor.close();
        }

        return lv_difficulty;
    }

	/*
	 * 우저 테이블을 생성후에 아래에 있는 words 다 테이블 이름으로 바꿔주면 될듯
	 */

    synchronized public boolean updateWordInfo(Word word, boolean isKnown) {

        // insertLevel(word.getDifficulty(), isKnown);

        boolean isTimeUpdate = true, isWordUpdate, isProbabilityUpdate = true;

        int state = word.getState();
        int preState = state;

        String column0 = null, column1 = null;

        switch (state) {
            case -1:
                column0 = null;
                column1 = null;
                break;
            case 0:
                column0 = null;
                column1 = null;
                break;

            case 1:
                column0 = "one";
                column1 = "first";
                break;
            case 2:
                column0 = "two";
                column1 = "second";
                break;
            case 3:
                column0 = "three";
                column1 = "third";
                break;
            case 4:
                column0 = "four";
                column1 = "fourth";
                break;
            case 5:
                column0 = "five";
                column1 = "fifth";
                break;
            case 6:
                column0 = "six";
                column1 = "sixth";
                break;
            case 7:
                column0 = "seven";
                column1 = "seventh";
                break;
            case 8:
                column0 = "eight";
                column1 = "eighth";
                break;
            case 9:
                column0 = "nine";
                column1 = "ninth";
                break;
            default:
                column0 = "nine";
                column1 = "ninth";
                break;
        }

        // Log.d("kjw", "SELECT * FROM times WHERE Time = " + word.getTime());
        Cursor cursor = db.rawQuery("SELECT * FROM times WHERE Time = ?", new String[]{String.valueOf(word.getTime(Config.Time_ONE_HOUR)),});
        cursor.moveToFirst();

        Log.d("kjw", column0 + "   " + column1);
        int time_value = 0;

        if (state > 0) {
            int saveValue = cursor.getInt(cursor.getColumnIndex(column1));
            float probability = cursor.getFloat(cursor.getColumnIndex(column0));

            int currentValue = saveValue + 1;
            int temp;
            time_value = currentValue;
            temp = isKnown ? 1 : 0;

            if (column1 != null) {
                ContentValues timeValues = new ContentValues();
                timeValues.put(column1, currentValue);
                isTimeUpdate = db.update("times", timeValues, "Time = ?", new String[]{String.valueOf(word.getTime(Config.Time_ONE_HOUR)),}) >= 0;
                // Log.d("kjw", "update time info " + word.getTime() + "  " +
                // column1 + " = " + currentValue);
            }

            if (column0 != null) {
                float calcProbability = (float) (probability * currentValue + temp) / (currentValue + 1);
                Log.e("kjw", "@@@p calc   (" + probability + " * " + currentValue + " + " + temp + ") / " + " ( " + currentValue + " + 1 " + ") = "
                        + calcProbability);
                ContentValues probabilityValues = new ContentValues();
                probabilityValues.put(column0, calcProbability);
                isProbabilityUpdate = db.update("times", probabilityValues, "Time = ?",
                        new String[]{String.valueOf(word.getTime(Config.Time_ONE_HOUR)),}) >= 0;

                // db.execSQL("UPDATE times set " + column0 + " = " +
                // calcProbability + " where Time = " + word.getTime());
                // Log.w("kjw", "UPDATE times set " + column0 + " = " +
                // calcProbability + " where Time = " + word.getTime());

                Log.v("kjw", "probability time = " + word.getTime(Config.Time_ONE_HOUR) + " column =  " + column0 + " = " + calcProbability);
            }
        }

        ContentValues values = new ContentValues();
        // values.put("Time", 1);
        values.put("Frequency", word.getFrequency() + 1);

        if (isKnown) {
            if (state == 0) {
                state = 9;

            } else if (state == -1) {
                state = 1;
            } else {
                state = state + 1;
            }
        } else {
            state = -1;
        }
		/*
		 * preState>0은 내가 이전에 안다라고 체크한 단어일텐데 그것 외에는 score나 time을 0으로 세팅을 해주지 않는다
		 * 297 4639
		 */

        if (preState > 0) {
            values.put("Score", 0);
            values.put("Time", 0);

            db.execSQL("update words set Score = null, time = null, forgetting_curves_test_flag = 0 WHERE Word_Code = " + word.get_id());
        }

        values.put("forgetting_curves_test_flag", 0);
        values.put("State", state);
        word.setState(state);
        // Log.d("kjw", "update word info = " + word.get_id() + "  " +
        // word.getWord() + "   " + state);
        isWordUpdate = db.update("words", values, "Word_Code = ?", new String[]{String.valueOf(word.get_id()),}) >= 0;

        cursor.close();
        return isWordUpdate && isTimeUpdate && isProbabilityUpdate;
    }

    synchronized public boolean updateWordFrequency(int wordCode, int frequency, boolean isKnown, int state) {

        ContentValues values = new ContentValues();
        values.put("Time", 1);
        values.put("Frequency", ++frequency);

        if (isKnown) {
            if (state != -1)
                values.put("State", ++state);
            else
                values.put("State", 1);
        } else
            values.put("State", -1);

        return db.update("words", values, "Word_Code = ?", new String[]{String.valueOf(wordCode),}) >= 0;
    }


    public ArrayList<Word> pwordsWithKnownAndQuery(String query) {

        ArrayList<Word> words = new ArrayList<Word>();

        // Cursor cursor =
        // db.rawQuery("SELECT * FROM words WHERE State > 0 ORDER BY Score",
        // null);
        Cursor cursor = db.rawQuery("SELECT * FROM words WHERE State > 0 And word_code in " + "(SELECT word_code FROM pwords " + query + ")", null);

        cursor.moveToFirst();
        Word word = null;

        while (!cursor.isAfterLast()) {

            word = new Word(cursor.getInt(0), cursor.getString(1), cursor.getInt(2), cursor.getInt(3), cursor.getInt(4), cursor.getDouble(5),
                    cursor.getInt(6), cursor.getInt(7), cursor.getInt(8), mconContext, mActivity);

            words.add(word);
            cursor.moveToNext();
        }

        cursor.close();
        return words;
    }

    public ArrayList<Publish> publishs() {

        ArrayList<Publish> arrays = new ArrayList<Publish>();

        Cursor cursor = db.rawQuery("SELECT * " + "FROM publish " + "ORDER BY publish_id", null);

        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {

            arrays.add(new Publish(cursor.getInt(1), cursor.getString(0)));
            cursor.moveToNext();
        }

        cursor.close();
        return arrays;
    }

    public ArrayList<Publish> publishs(int grade) {

        ArrayList<Publish> arrays = new ArrayList<Publish>();

        Cursor cursor = db.rawQuery("SELECT * " + "FROM publish " + "WHERE publish_id in ( SELECT publisher_code FROM publish_info WHERE grade = ?)"
                + "ORDER BY publish_id", new String[]{String.valueOf(grade),});

        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {

            arrays.add(new Publish(cursor.getInt(1), cursor.getString(0)));
            cursor.moveToNext();
        }

        cursor.close();
        return arrays;
    }

    public int lesson(int grade, int publish_id) {

        Cursor cursor = db.rawQuery("SELECT final_lesson FROM publish_info WHERE grade = ? AND publisher_code = ?",
                new String[]{String.valueOf(grade), String.valueOf(publish_id),});

        cursor.moveToFirst();

        int l = cursor.getInt(0);
        cursor.close();
        return l;
    }

    synchronized private boolean updateWordTime(int wordCode, int time) {

        // Calendar mCalendar = Calendar.getInstance();
        // mCalendar.setTimeInMillis(System.currentTimeMillis());
        //
        // int hour = mCalendar.getTime().getHours();

        ContentValues values = new ContentValues();
        values.put("Time", ++time);

        return db.update("words", values, "Word_Code = ?", new String[]{String.valueOf(wordCode),}) >= 0;
    }

    synchronized public boolean updateScore(int wordCode, double score, int hour) {

        ContentValues values = new ContentValues();
        values.put("Score", score);
        values.put("Time", hour);

        return db.update("words", values, "Word_Code = ?", new String[]{String.valueOf(wordCode),}) >= 0;
    }

    public int getRightWordCount() {
        Cursor cursor = db.rawQuery("SELECT * " + "FROM words " + "WHERE state > 0", null);

        int count = cursor.getCount();
        cursor.close();
        return count;
    }

    public int getNoZeroState() {
        Cursor cursor = db.rawQuery("SELECT * " + "FROM words " + "WHERE state > 0", null);

        int count = cursor.getCount();
        cursor.close();
        return count;
    }

    public boolean getReviewWordExist() {
        Cursor cursor = db.rawQuery("SELECT * " + "FROM words " + "WHERE state > 0 and state < " + stateCnt + 1, null);

        int count = cursor.getCount();
        cursor.close();

        if (count == 0) {
            return false;
        } else {
            return true;
        }
    }

    public Word getWord(int idx) {
        Cursor cursor = db.rawQuery("SELECT * " + "FROM words WHERE Word_Code = ?", new String[]{String.valueOf(idx),});

        Word word = null;
        if (cursor.getCount() == 1) {
            cursor.moveToFirst();

            word = new Word(cursor.getInt(0), cursor.getString(1), cursor.getInt(2), cursor.getInt(3), cursor.getInt(4), cursor.getDouble(5),
                    cursor.getInt(6), cursor.getInt(7), cursor.getInt(8), mconContext, mActivity);
        }

        cursor.close();
        return word;
    }

    public List<Mean> getMean(int idx) {
        Cursor cursor = db.rawQuery("SELECT * " + "FROM means WHERE Word_Code = ?", new String[]{String.valueOf(idx),});

        List<Mean> mean = new ArrayList<Mean>();
        if (cursor.getCount() != 0) {
            cursor.moveToFirst();

            do {
                mean.add(new Mean(cursor.getInt(0), cursor.getInt(1), cursor.getInt(2), cursor.getString(3)));
            } while (cursor.moveToNext());
            // int _id, String meaning, int w_class, int w_priority

        }

        cursor.close();
        return mean;

    }

    public Mean checkMeantbl(int idx) {
        Cursor cursor = db.rawQuery("SELECT * " + "FROM means WHERE Mean_Code = ?", new String[]{String.valueOf(idx),});

        Mean mean = null;
        if (cursor.getCount() == 1) {
            cursor.moveToFirst();

            mean = new Mean(cursor.getInt(0), cursor.getInt(1), cursor.getInt(2), cursor.getString(3));

        }

        cursor.close();
        return mean;

    }

    public int getWorngWordCount() {
        Cursor cursor = db.rawQuery("SELECT * " + "FROM words WHERE state = -1", null);

        int count = cursor.getCount();
        cursor.close();
        return count;
    }

    public int getNoneWordCount() {
        Cursor cursor = db.rawQuery("SELECT * " + "FROM words WHERE state = 0", null);

        int count = cursor.getCount();
        cursor.close();
        return count;
    }

    private void writeFile(String fileName, String s) {
        try {

            // File saveFile = new
            // File(Environment.getExternalStorageDirectory().getAbsolutePath()
            // + "/test_log.txt");
            File saveFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + fileName);
            if (!saveFile.exists()) {
                saveFile.createNewFile();
            }

            FileWriter fw = new FileWriter(saveFile, true);
            BufferedWriter bw = new BufferedWriter(fw);

            bw.write(s);
            bw.flush();
            bw.close();
            fw.close();
        } catch (IOException e) {
            Log.e("kjw", "write error = " + e.getMessage());
        } finally {

        }
    }

	/*
	 * ■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■
	 * ■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■
	 *
	 * 유저 테이블 관리
	 *
	 * ■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■
	 * ■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■
	 */

    public boolean insertCurrentWord(Word word, int id) {
        ContentValues values = new ContentValues();
        values.put("Word_Code", word.get_id());
        values.put("isRight", word.isRight() ? 1 : 0);
        values.put("isWrong", word.isWrong() ? 1 : 0);
        values.put("wrongCount", word.getWrongCount());
        values.put("exState", word.getState());

        return db.insert("user_words", null, values) >= 0;

    }

    synchronized public void deleteCurrentWord(int _id) {
        db.execSQL("DELETE FROM user_words WHERE Word_Code = " + _id);
    }

    synchronized public void deleteAllCurrentWord() {
        try {
            db.beginTransaction();
            db.execSQL("DELETE FROM user_words ");

            db.setTransactionSuccessful();
        } catch (SQLException e) {
            // TODO: handle exception
        } finally {
            db.endTransaction();
        }
    }

    synchronized public boolean updateRightWrong(boolean isRight, int _id) {
        ContentValues values = new ContentValues();

        if (isRight) {
            values.put("isRight", 1);
            values.put("isWrong", 0);
        } else {
            values.put("isRight", 0);
            values.put("isWrong", 1);
        }
        boolean isCheck = false;
        try {
            db.beginTransaction();
            isCheck = db.update("user_words", values, "Word_Code = ?", new String[]{String.valueOf(_id),}) >= 0;
            db.setTransactionSuccessful();
        } catch (SQLException e) {
            // TODO: handle exception
        } finally {
            db.endTransaction();
        }
        return isCheck;
    }

    public int getRightCount() {
        Cursor cursor = db.rawQuery("SELECT * FROM user_words WHERE isRight = 1", null);

        int count = cursor.getCount();
        cursor.close();

        return count;
    }

	/*
	 * 학습 피드백 관련 함수들
	 * ★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★
	 * ★★★★★★★★★★★★★★★★★★★★★★
	 */

    public boolean putNotice(String contents) {
        Date date = new Date();
        String day = date.get_today();

        Cursor cursor = db.rawQuery("SELECT * FROM feeds WHERE Date = ?", new String[]{day});

        if (cursor.getCount() == 0) {

            ContentValues values = new ContentValues();
            values.put("Type", 1);
            values.put("Date", day);
            values.put("Title", "공지사항");
            values.put("Contents", contents);

            cursor.close();

            return db.insert("feeds", null, values) >= 0;

        } else {
            cursor.close();
            return false;
        }
    }

    // public boolean putChangeLevel(int ex_lv, int after_lv) {
    // Date date = new Date();
    // String day = date.get_today();
    //
    // ContentValues values = new ContentValues();
    // values.put("Type", 2);
    // values.put("Date", day);
    // values.put("Title", "Lv. " + ex_lv + " -> Lv. " + after_lv);
    // settings.edit().putInt(MainValue.GpreLevel, after_lv).commit();
    //
    // Config.Difficulty = after_lv;
    // if (ex_lv < after_lv) {
    // values.put("Contents", "한단계 더 높은 수준의 단어가 제공됩니다.");
    // } else {
    // values.put("Contents", "조금 낮은 수준의 단어가 제공됩니다.");
    // }
    //
    // return db.insert("feeds", null, values) >= 0;
    //
    // }

    public boolean putDay_Of_Study() {

        MildangDate date = new MildangDate();
        String day = date.get_yesterday();

        Cursor cursor = db.rawQuery("SELECT * FROM feeds WHERE Date = ? AND Type = 3 ", new String[]{day});

        if (cursor.getCount() == 0) {
            int total_count = 0;
            int mForget = getMforget();
            if (mForget > 0) {
                total_count = mForget + getReviewWord();
            } else {
                total_count = getReviewWord();
            }

            ContentValues values = new ContentValues();
            values.put("Type", 3);
            values.put("Date", day);
            values.put("Title", "학습 피드백");
            values.put("Total_Count", total_count);
            values.put("Do_count", getReviewWord());
            values.put("New_count", getStudyWord());
            Log.e("feeds", "insert : " + cursor.getCount());

            cursor.close();

			/*
			 * 서버에 학습시간 보내기 !
			 */
            // ///////////////////////////////////////////
            // settings.getInt(MainValue.GpreUseMin, 0);
            // settings.getString(MainActivitys.GpreID,"000000");
            // Date uploadDate = new Date();
            // uploadDate.get_yesterday();

            long temp = db.insert("feeds", null, values);
            Log.d("feeds", temp + " " + (temp >= 0));
            return temp >= 0;

        } else {
            Log.e("feeds", "insert fail");
            cursor.close();
            return false;
        }
    }

    public int getWeeks_Of_Study() {

        Date date = new Date();
        String day = date.get_yesterday();

        Cursor cursor = db.rawQuery("SELECT SUM( Total_Count - Do_count ) FROM feeds WHERE Type = 3 ORDER BY Feed_Code DESC LIMIT 7", null);
        cursor.moveToFirst();
        if (cursor.getCount() != 0) {
            int count = cursor.getInt(0);
            cursor.close();
            return count;

        } else {
            Log.e("feeds", "insert fail");
            cursor.close();
            return 0;
        }
    }

    public int getPRememberCount() {
        Cursor cursor = db.rawQuery("SELECT * FROM words WHERE Score IS NOT NULL AND State > 0 AND Score <= 0", null);

        int count = cursor.getCount();
        cursor.close();

        return count;
    }

    public int getMforget() {
        Cursor cursor = db.rawQuery("SELECT * FROM words WHERE Score > 0 OR forgetting_curves_test_flag=1 ", null); // '15.01.22(Thu)
        // 망각곡선
        // 높아서
        // 안나오는
        // 부분
        // 추가

        int count = cursor.getCount();
        cursor.close();

        Log.e("c_date", "" + count);

        return count;
    }

    public int getKnownCount() {
        Cursor cursor = db.rawQuery("SELECT * FROM words WHERE Score IS NOT NULL AND State > 0 ", null);

        int count = cursor.getCount();
        cursor.close();

        return count;
    }

    public int getTotalWordCount(int i) {
        int count;
        Cursor cursor = db.rawQuery("SELECT SUM(1) FROM words", null);

        cursor.moveToFirst();

        count = cursor.getInt(0);
        cursor.close();

        return count;
    }

    public Integer[] getForgetCount() {
        Integer[] count = new Integer[2];
        Cursor cursor = db.rawQuery("SELECT SUM(" + scoreCriterion + " - Score), SUM(1) FROM words where State > 0 AND Score > 0", null);

        cursor.moveToFirst();

        count[0] = cursor.getInt(0);
        count[1] = cursor.getInt(1) - count[0];
        cursor.close();

        return count;
    }

    public int getUnKnownCount() {
        Cursor cursor = db.rawQuery("SELECT * FROM words WHERE State <= 0 ", null);

        int count = cursor.getCount();
        cursor.close();

        return count;
    }

    public int getNotRememCount() {
        Cursor cursor = db.rawQuery("SELECT * FROM words WHERE State < 0 ", null);

        int count = cursor.getCount();
        cursor.close();

        return count;
    }

    public int getNotShownCount() {
        Cursor cursor = db.rawQuery("SELECT * FROM words WHERE State = 0 ", null);

        int count = cursor.getCount();
        cursor.close();

        return count;
    }

    public int getTotalStudy() {
        Cursor cursor = db.rawQuery("SELECT * FROM words WHERE State IS NOT 0", null);

        int count = cursor.getCount();
        cursor.close();

        return count;
    }

    public int getWrongCount() {
        Cursor cursor = db.rawQuery("SELECT * FROM user_words WHERE isWrong = 1", null);

        int count = cursor.getCount();
        cursor.close();

        return count;
    }

    public ArrayList<Feed> getFeed() {
        ArrayList<Feed> arrays = new ArrayList<Feed>();

        Cursor cursor = db.rawQuery("SELECT * FROM feeds ORDER BY Feed_Code DESC", null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            Feed feed = null;

            while (!cursor.isAfterLast()) {
                switch (cursor.getInt(1)) {
                    case Feed.NOTICE:
                        feed = new Feed(cursor.getInt(1), cursor.getString(2), cursor.getString(3), cursor.getString(4));
                        break;
                    case Feed.LEVEL_UP:
                        feed = new Feed(cursor.getInt(1), cursor.getString(2), cursor.getString(3), cursor.getString(4));
                        break;
                    case Feed.STUDY_FEEDBACK:
                        feed = new Feed(cursor.getInt(1), cursor.getString(2), cursor.getString(3), cursor.getInt(5), cursor.getInt(6), cursor.getInt(7));
                        break;

                }

                arrays.add(feed);
                cursor.moveToNext();
            }
        }
        cursor.close();
        return arrays;
    }

	/*
	 * user table 가져오는 부분
	 * ★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★
	 * ★★★★★★★★★★★★★★★★★★★★★ 2015.01.27
	 *
	 * 양대현 수정
	 */

    public ArrayList<Word> getCurrentWords() {
        ArrayList<Word> arrays = new ArrayList<Word>();

        Cursor cursor = db.rawQuery("SELECT * FROM words a INNER JOIN user_words b ON a.Word_Code=b.Word_Code" + " WHERE isRight = 0", null);

        cursor.moveToFirst();

        Word word;

        while (!cursor.isAfterLast()) {

            // public Word(int _id, String word, String meaning, int difficulty,
            // int priority, double score, int state, int time, int frequency,
            // boolean isRight, boolean isWrong, int wrongCount)

			/*
			 * int _id, String word, String meaning, int difficulty, int
			 * priority, double score, int state, int time, int frequency,
			 * boolean isRight, boolean isWrong, int wrongCount, int exState)
			 */

            word = new Word(cursor.getInt(cursor.getColumnIndex("word_code")), cursor.getString(cursor.getColumnIndex("word")), cursor.getInt(cursor
                    .getColumnIndex("p_wordclass")), cursor.getInt(cursor.getColumnIndex("difficulty")), cursor.getInt(cursor
                    .getColumnIndex("Priority")), cursor.getDouble(cursor.getColumnIndex("Score")), cursor.getInt(cursor.getColumnIndex("State")),
                    cursor.getInt(cursor.getColumnIndex("Time")), cursor.getInt(cursor.getColumnIndex("Frequency")), cursor.getInt(cursor
                    .getColumnIndex("isRight")) > 0, cursor.getInt(cursor.getColumnIndex("isWrong")) > 0, cursor.getInt(cursor
                    .getColumnIndex("wrongCount")), cursor.getInt(cursor.getColumnIndex("exState")), mconContext, mActivity);

            arrays.add(word);
            cursor.moveToNext();
        }

        cursor.close();

        try {

            db.beginTransaction();

            for (int i = 0; i < arrays.size(); i++) {
                arrays.get(i).setMeanList(getMean(arrays.get(i).get_id()));

            }

            db.setTransactionSuccessful();
        } catch (SQLException e) {
            // TODO: handle exception
        } finally {
            db.endTransaction();
        }
        return arrays;
    }

    public ArrayList<Word> getCurrentWords_LockScreen() {
        ArrayList<Word> arrays = new ArrayList<Word>();

        Cursor cursor = db.rawQuery("SELECT * FROM words a INNER JOIN user_words b ON a.Word_Code=b.Word_Code" + " WHERE isRight = 0", null);

        cursor.moveToFirst();

        Word word;

        while (!cursor.isAfterLast()) {

            // public Word(int _id, String word, String meaning, int difficulty,
            // int priority, double score, int state, int time, int frequency,
            // boolean isRight, boolean isWrong, int wrongCount)

			/*
			 * int _id, String word, String meaning, int difficulty, int
			 * priority, double score, int state, int time, int frequency,
			 * boolean isRight, boolean isWrong, int wrongCount, int exState)
			 */

            word = new Word(cursor.getInt(cursor.getColumnIndex("word_code")), cursor.getString(cursor.getColumnIndex("word")), cursor.getInt(cursor
                    .getColumnIndex("p_wordclass")), cursor.getInt(cursor.getColumnIndex("difficulty")), cursor.getInt(cursor
                    .getColumnIndex("Priority")), cursor.getDouble(cursor.getColumnIndex("Score")), cursor.getInt(cursor.getColumnIndex("State")),
                    cursor.getInt(cursor.getColumnIndex("Time")), cursor.getInt(cursor.getColumnIndex("Frequency")), cursor.getInt(cursor
                    .getColumnIndex("isRight")) > 0, cursor.getInt(cursor.getColumnIndex("isWrong")) > 0, cursor.getInt(cursor
                    .getColumnIndex("wrongCount")), cursor.getInt(cursor.getColumnIndex("exState")), mconContext, mActivity);

            arrays.add(word);
            cursor.moveToNext();
        }

        cursor.close();

        try {

            db.beginTransaction();

            db.setTransactionSuccessful();
        } catch (SQLException e) {
            // TODO: handle exception
        } finally {
            db.endTransaction();
        }
        return arrays;
    }

    public int getExState(int word_code) {

        int ex_state = 0;
        Cursor cursors = db.rawQuery("SELECT * FROM user_words WHERE Word_Code = '" + word_code + "'", null);

        cursors.moveToFirst();

        if (cursors.getCount() != 0) {
            ex_state = cursors.getInt(4);
        }

        Log.e("exstate", "" + ex_state);

        cursors.close();

        return ex_state;
    }

    public void testex() {
        Cursor cursor = db.rawQuery("SELECT * FROM user_words", null);

        cursor.moveToFirst();

        do {
            Log.e("exstate", "" + cursor.getInt(4));

        } while (cursor.moveToNext());
    }

    public List<Feed> getLevelList() {
        List<Feed> list = new ArrayList<Feed>();
        Cursor cursor = db.rawQuery("SELECT * FROM feeds WHERE Type = 2 ORDER BY Date DESC", null);
        cursor.moveToFirst();
        if (cursor.getCount() == 0) {
            return null;
        }

        while (cursor.moveToNext()) {
            list.add(new Feed(3, cursor.getString(2), cursor.getString(3), cursor.getString(4)));
        }
        cursor.close();

        return list;
    }

    public List<Feed> getStudyList() {
        List<Feed> list = new ArrayList<Feed>();
        list.clear();
        Cursor cursor = db.rawQuery("SELECT * FROM feeds WHERE Type = 3 ORDER BY Date DESC", null);
        cursor.moveToFirst();
        if (cursor.getCount() == 0) {
            return null;
        }

        do {
            list.add(new Feed(3, cursor.getString(2), cursor.getString(3), cursor.getInt(5), cursor.getInt(6), cursor.getInt(7)));

        } while (cursor.moveToNext());
        cursor.close();

        return list;
    }

    // 튜토리얼 부분 단어셋을 가져오기위한 부분
    public ArrayList<Word> get_test_db(int type) {

        ArrayList<Word> arrays = new ArrayList<Word>();

        Cursor cursor = db.rawQuery("SELECT * FROM words a INNER JOIN test t ON a.Word_Code=t.Word_Code" + " WHERE Type = ?",
                new String[]{String.valueOf(type),});

        cursor.moveToFirst();
        Word word;

		/*
		 * int _id, String word, String meaning, int difficulty, int priority,
		 * double score, int state, int time, int frequency, boolean isRight,
		 * boolean isWrong, int wrongCount)
		 */

        while (!cursor.isAfterLast()) {

            word = new Word(cursor.getInt(0), cursor.getString(1), cursor.getInt(2), cursor.getInt(3), cursor.getInt(4), cursor.getDouble(5),
                    cursor.getInt(6), cursor.getInt(7), cursor.getInt(8), false, false, 0, mconContext, mActivity);

            arrays.add(word);
            cursor.moveToNext();
        }
        cursor.close();
        try {

            db.beginTransaction();
            for (int i = 0; i < arrays.size(); i++) {
                arrays.get(i).setMeanList(getMean(arrays.get(i).get_id()));
            }
        } catch (SQLException e) {
            // TODO: handle exception
        } finally {
            db.endTransaction();
        }

        return arrays;
    }

    public Integer[][] getLevelCounting() {
        // TODO Auto-generated method stub

        Integer DB_level_counting[][] = new Integer[5][2];

        try {

            db.beginTransaction();

            for (int i = 1; i <= 5; i++) {
                Cursor cursor = db.rawQuery("SELECT " + "count(case when Difficulty= ? then 1 else null end) as totalcount, "
                                + "count(case when state > 0 then 1 else null end) as studycount " + "FROM words " + "GROUP BY Difficulty ",
                        new String[]{String.valueOf(i),});

                cursor.moveToFirst();

                if (cursor.getCount() > 0) {
                    cursor.moveToPosition(i - 1);

                    int totalcount = cursor.getInt(cursor.getColumnIndex("totalcount"));
                    int studycount = cursor.getInt(cursor.getColumnIndex("studycount"));
                    if (studycount == 0) {
                        DB_level_counting[i - 1][0] = 0;
                        DB_level_counting[i - 1][1] = totalcount;
                    } else {
                        DB_level_counting[i - 1][0] = studycount;
                        DB_level_counting[i - 1][1] = totalcount;
                    }

                } else {
                    DB_level_counting[i - 1][0] = 0;
                    DB_level_counting[i - 1][1] = 0;
                }

                cursor.close();
            }
        } catch (SQLException e) {
            // TODO: handle exception
        } finally {
            db.endTransaction();
        }

        return DB_level_counting;
    }

    public void calcReview(int level) {
        // asdf
        // Cursor cursor =
        // db.rawQuery("SELECT * FROM level WHERE difficulty = ? ORDER BY rowid DESC LIMIT 10 ",
        // new String[]{String.valueOf(level),});
        // cursor.moveToFirst();
    }

    // ///////////////////////////////////////////////////////////////
    // //////////// fit forgetting curve : 9th curve /////////////////
    // ///////////////////////////////////////////////////////////////

    public void fitNinthCurve() {
        Cursor cursor = db.rawQuery("SELECT * FROM times WHERE nine < 90", null);
        if (cursor.getCount() > 0) {
            db.execSQL("UPDATE times SET nine=100, ninth=10000 WHERE nine < 90");
        }

        cursor.close();
    }

    // ///////////////////////////////////////////////////////////////
    // ///////////////////////////////////////////////////////////////
    // ///////// Download Server DB(word, mean table) ////////////////
    // ///////////////////////////////////////////////////////////////
    // ///////////////////////////////////////////////////////////////

    public void addWordnMeanVersionColumn() { // add Word and Mean Version
        // Column

        Cursor cursor = db.rawQuery("SELECT * FROM words WHERE rowid=0", null);
        if (cursor.getColumnCount() == 10) { // old version : w_modify and
            // w_version is not contained
            db.execSQL("ALTER TABLE words ADD COLUMN w_modify INTEGER");
            db.execSQL("ALTER TABLE words ADD COLUMN w_version INTEGER");
            Log.e("downloadServerDB", "add Word Version Column");
        } else {
            Log.e("downloadServerDB", "no need to add Word Version Column");
        }
        cursor.close();

        cursor = db.rawQuery("SELECT * FROM means WHERE rowid=0", null);
        if (cursor.getColumnCount() == 5) { // old version : w_modify and
            // w_version is not contained
            db.execSQL("ALTER TABLE means ADD COLUMN m_modify INTEGER");
            db.execSQL("ALTER TABLE means ADD COLUMN m_version INTEGER");
            Log.e("downloadServerDB", "add Mean Version Column");
        } else {
            Log.e("downloadServerDB", "no need to add Mean Version Column");
        }
        cursor.close();

    }

    public int getMaxWordVersion(String modify) { // get max word version as
        // modify, namely INSERT,
        // UPDATE
        Log.e("downloadServerDB", "get Max Word Version " + modify);

        int version = 0;

        Cursor cursor = db.rawQuery("SELECT MAX(w_version) FROM words WHERE w_modify='" + modify + "'", null);
        cursor.moveToFirst();
        version = cursor.getInt(0);
        cursor.close();

        return version;
    }

    public int getMaxMeanVersion(String modify) { // get max mean version as
        // modify, namely INSERT,
        // UPDATE
        Log.e("downloadServerDB", "get Max Mean Version " + modify);

        int version = 0;

        Cursor cursor = db.rawQuery("SELECT MAX(m_version) FROM means WHERE m_modify='" + modify + "'", null);
        cursor.moveToFirst();
        version = cursor.getInt(0);
        cursor.close();

        return version;
    }

    public void modifyUserWord(int modify, JSONArray word_code, JSONArray word, JSONArray typ_class, JSONArray difficulty, JSONArray priority,
                               JSONArray real_time, JSONArray w_modify, JSONArray w_version) {
        // modify user word table : INSERT, UPDATE or DELETE

        Log.e("downloadServerDB", "DBPool:modifyUserWord");

        if (modify == 0) { // insert
            for (int i = 0; i < word_code.length(); i++) {
                db.execSQL("INSERT INTO words(word_code, word, p_wordclass, difficulty," + "				  Priority, Score, State, Time,"
                        + "				  Frequency, Realtime, w_modify, w_version)" + "VALUES(" + "'"
                        + word_code.optInt(i)
                        + "', "
                        + "'"
                        + word.optString(i)
                        + "', "
                        + "'"
                        + typ_class.optInt(i)
                        + "', "
                        + "'"
                        + difficulty.optInt(i)
                        + "', "
                        + "'"
                        + priority.optInt(i)
                        + "', "
                        + "'0', '0', '0', '0', "
                        + "'"
                        + real_time.optString(i)
                        + "', " + "'" + w_modify.optString(i) + "', " + "'" + w_version.optInt(i) + "')");
            }
            Log.e("downloadServerDB", "DBPool:modifyUserWord:INSERT");

        } else if (modify == 1) { // update
            for (int i = 0; i < word_code.length(); i++) {
                db.execSQL("UPDATE words " + "SET word = '" + word.optString(i) + "', " + "	p_wordclass = '" + typ_class.optInt(i) + "', "
                        + "	difficulty = '" + difficulty.optInt(i) + "', " + "	Priority = '" + priority.optInt(i) + "', " + "	Realtime = '"
                        + real_time.optString(i) + "', " + "	w_modify = '" + w_modify.optString(i) + "', " + "	w_version = '" + w_version.optInt(i)
                        + "'" + "WHERE word_code = '" + word_code.optInt(i) + "'");
            }
            Log.e("downloadServerDB", "DBPool:modifyUserWord:UPDATE");

        } else if (modify == 2) { // delete
            for (int i = 0; i < word_code.length(); i++) {
                db.execSQL("DELETE FROM words " + "WHERE word_code = '" + word_code.optInt(i) + "'");
            }
            Log.e("downloadServerDB", "DBPool:modifyUserWord:DELETE");

        } else { // error
            ;
        }
    }

    public void modifyUserMean(int modify, JSONArray mean_code, JSONArray _word_code, JSONArray _class, JSONArray mean, JSONArray priority,
                               JSONArray m_modify, JSONArray m_version) {
        // modify user mean table : INSERT, UPDATE or DELETE

        Log.e("downloadServerDB", "DBPool_modifyUserMean");

        if (modify == 0) { // insert
            for (int i = 0; i < mean_code.length(); i++) {
                db.execSQL("INSERT INTO means(Mean_Code, Word_Code, Class, Mean," + "				  M_Priority, m_modify, m_version)" + "VALUES(" + "'"
                        + mean_code.optInt(i) + "', " + "'" + _word_code.optInt(i) + "', " + "'" + _class.optInt(i) + "', " + "'" + mean.optString(i)
                        + "', " + "'" + priority.optInt(i) + "', " + "'" + m_modify.optString(i) + "', " + "'" + m_version.optInt(i) + "')");
            }
            Log.e("downloadServerDB", "DBPool_modifyUserMean:INSERT");

        } else if (modify == 1) { // update
            for (int i = 0; i < mean_code.length(); i++) {
                db.execSQL("UPDATE means " + "SET Word_Code = '" + _word_code.optInt(i) + "', " + "	Class = '" + _class.optInt(i) + "', "
                        + "	Mean = '" + mean.optString(i) + "', " + "	M_Priority = '" + priority.optInt(i) + "', " + "	m_modify = '"
                        + m_modify.optString(i) + "', " + "	m_version = '" + m_version.optInt(i) + "'" + "WHERE Mean_Code = '" + mean_code.optInt(i)
                        + "'");
            }
            Log.e("downloadServerDB", "DBPool_modifyUserMean:UPDATE");

        } else if (modify == 2) { // delete
            for (int i = 0; i < mean_code.length(); i++) {
                db.execSQL("DELETE FROM means " + "WHERE Mean_Code = '" + mean_code.optInt(i) + "'");
            }
            Log.e("downloadServerDB", "DBPool_modifyUserMean:DELETE");

        } else { // error
            ;
        }
    }

    public boolean putCalendarData(String date) {
        String sql = "SELECT * FROM calendar_data WHERE date = " + date;
        Cursor c = null;
        c = db.rawQuery(sql, null);

        ContentValues values = new ContentValues();

        // 수정했음. 네임이 문제였음.
        values.put("date", date);
        values.put("study_time", getTime()[0]); // 오늘 공부 한
        // 시간
        values.put("goal_time", getGoalTime()); // 목표 공부 시간
        values.put("new_count", getStudyWord()); // 오늘 새로 공부한 단어
        values.put("review_count", getReviewWord()); // 오늘 복습한 단어
        // 지금 당장엔 생각이 안나서 일단은 getmforget으로 한다. 계싼할때는 복습단어 갯수가 남은갯수보다
        values.put("will_review_count", getMforget() + getReviewWord()); // 복습
        // 해야
        // 할
        // 단어
        Log.e("config_new_count", "config_new_count	:" + getStudyWord() + " db : " + getKnownCount());
        // Log.e("dbdata", "new_count  		:" +
        // Integer.parseInt(settings.getString(MainValue.GpreTodayLearnCnt,
        // "0")));
        // Log.e("dbdata", "review_count 		:" +
        // Integer.parseInt(settings.getString(MainValue.GpreTodayReviewCnt,
        // "0")));
        // Log.e("dbdata", "will_review_count	:" + (getMforget() +
        // Integer.parseInt(settings.getString(MainValue.GpreTodayReviewCnt,
        // "0"))));

        // sdfg
        if (c.getCount() == 0) { // 아직 해당날짜에 대한 값이 데이터베이스에 없으면 insert
            return db.insert("calendar_data", null, values) == 1;
        } else if (c.getCount() == 1) { // 해당 날짜에 대한 값이 이미 데이터베이스에 있으면 update
            Log.e("dbdata", "update  date :" + date);
            Log.e("dbdata", "update  update :" + (db.update("calendar_data", values, "date = " + date, null) == 1));
            return db.update("calendar_data", values, "date = " + date, null) == 1;
        }
        c.close();
        return false;
    }

    public boolean putCalendarData_customTime(String date, int time, int learn_cnt, int review_cnt) {
        String sql = "SELECT * FROM calendar_data WHERE date = " + date;
        Cursor c = null;
        c = db.rawQuery(sql, null);

        ContentValues values = new ContentValues();

        // 수정했음. 네임이 문제였음.
        values.put("date", date);
        values.put("study_time", time); // 오늘 공부 한 시간
        values.put("goal_time", getGoalTime()); // 목표공부시간
        values.put("new_count", learn_cnt); // 오늘 새로 공부 한 단어
        values.put("review_count", review_cnt); // 오늘 복습 한 단어
        // 지금 당장엔 생각이 안나서 일단은 getmforget으로 한다. 계싼할때는 복습단어 갯수가 남은갯수보다
        values.put("will_review_count", getMforget() + getReviewWord()); // 복습해야
        // 할
        // 단어
        Log.e("dbdata", "new_count  		:" + Integer.parseInt(settings.getString(MainValue.GpreTodayLearnCnt, "0")));
        Log.e("dbdata", "review_count 		:" + Integer.parseInt(settings.getString(MainValue.GpreTodayReviewCnt, "0")));
        Log.e("dbdata", "will_review_count	:" + (getMforget() + Integer.parseInt(settings.getString(MainValue.GpreTodayReviewCnt, "0"))));

        // sdfg
        if (c.getCount() == 0) { // 아직 해당날짜에 대한 값이 데이터베이스에 없으면 insert
            return db.insert("calendar_data", null, values) == 1;
        } else if (c.getCount() == 1) { // 해당 날짜에 대한 값이 이미 데이터베이스에 있으면 update
            Log.e("dbdata", "update  date :" + date);
            Log.e("dbdata", "update  update :" + (db.update("calendar_data", values, "date = " + date, null) == 1));
            return db.update("calendar_data", values, "date = " + date, null) == 1;
        }
        c.close();
        return false;
    }

    public int getReviewCountByCalendar() {
        String sql = "SELECT review_count FROM calendar_data";
        Cursor c = null;

        int reviewCount = 0;

        try {
            c = db.rawQuery(sql, null);
            c.moveToFirst();

            int i = 0;

            while (c.moveToNext()) {
                reviewCount += c.getInt(0);
            }

        } catch (Exception e) {
            // Log.e("review_count", e.getMessage());
        }

        return reviewCount;
    }

    public int[] getCalendarTimeData(String date) {
        String sql = "SELECT * FROM calendar_data WHERE date = " + date;
        Cursor c = null;
        int timeData[] = new int[4];

        try {
            c = db.rawQuery(sql, null);
            if (c.getCount() == 0) {
                Log.d("Database", "" + c.getCount());
                return null;
            }
            c.moveToFirst();

            Log.d("Database", "" + c.getCount());

            timeData[0] = c.getInt(1);
            timeData[1] = c.getInt(2);
            timeData[2] = c.getInt(4);
            timeData[3] = c.getInt(5);
        } finally {
            if (c != null) {
                c.close();
            }
        }

        return timeData;
    }

    public int[] getCalendarCountData(String date) {
        String sql = "SELECT * FROM calendar_data WHERE date = " + date;
        Cursor c = null;
        int wordCount[] = new int[3];

        try {
            c = db.rawQuery(sql, null);
            if (c.getCount() == 0) {
                Log.d("Database", "" + c.getCount());
                return null;
            }
            Log.d("check", "try");
            c.moveToFirst();

            Log.d("Database", "" + c.getCount());
            wordCount[0] = c.getInt(3);
            wordCount[1] = c.getInt(4);
            wordCount[2] = c.getInt(5);

        } finally {
            if (c != null) {
                c.close();
            }
        }
        Log.d("check", "fail");
        return wordCount;
    }

    public int getMonthUseTime(String date) {
        int sum = 0;
        int year, month, day;
        year = Integer.parseInt(date.substring(0, 4));
        month = Integer.parseInt(date.substring(4, 6));
        day = Integer.parseInt(date.substring(6, 8));

        String tempDate = date.substring(0, 6) + "%"; // 해당 월로 시작하는 값들을 다 불러온다.

        String sql = "SELECT study_time FROM calendar_data WHERE date LIKE \'" + tempDate + "\'";
        Cursor c = null;

        DateTime dateTime = DateTime.forDateOnly(year, month, day);

        int numDayInMonth = dateTime.getNumDaysInMonth();

        try {
            c = db.rawQuery(sql, null);
            if (c.getCount() == 0) {
                Log.d("Database", "" + c.getCount());
                return 0;
            }
            int columnIndex = c.getColumnIndex("study_time");
            c.moveToFirst();
            do {
                sum += c.getInt(columnIndex);
            } while (c.moveToNext());

            // Log.d("Database", "" + c.getCount() + sum);

        } finally {
            if (c != null) {
                c.close();
            }
        }
        return sum;
    }

    public int[] getMonthAverageTime(String date) {
        int sum = 0;
        int year, month, day;
        year = Integer.parseInt(date.substring(0, 4));
        month = Integer.parseInt(date.substring(4, 6));
        day = Integer.parseInt(date.substring(6, 8));

        DateTime dtime = DateTime.forDateOnly(year, month, day);
        dtime = dtime.minusDays(28);

        String time = dateTimeToString(dtime);

        String sql = "SELECT * FROM calendar_data WHERE (date > " + time + " OR date = " + time + ") AND date != " + date
                + " ORDER BY date ASC Limit 28";

        int count = 1;

        Cursor c = null;

        int value[] = new int[2];

        try {
            c = db.rawQuery(sql, null);
            if (c.getCount() == 0) {
                value[0] = 0;
                value[1] = 0;
                return value;
            } else {
                count = c.getCount();
            }

            int columnIndex = c.getColumnIndex("study_time");
            c.moveToFirst();

            do {
                sum += c.getInt(columnIndex);
            } while (c.moveToNext());

        } finally {
            if (c != null) {
                c.close();
            }
        }
        value[0] = sum;
        value[1] = count;
        return value;
    }

    public int getWeekAverageTime(String date) {
        int sum = 0;
        int year, month, day;
        year = Integer.parseInt(date.substring(0, 4));
        month = Integer.parseInt(date.substring(4, 6));
        day = Integer.parseInt(date.substring(6, 8));

        DateTime dtime = DateTime.forDateOnly(year, month, day);
        dtime = dtime.minusDays(7);

        String time = dateTimeToString(dtime);

        String sql = "SELECT * FROM calendar_data WHERE (date > " + time + " OR date = " + time + ") AND date != " + date
                + " ORDER BY date ASC Limit 7";

        int count = 1;

        Cursor c = null;

        try {
            c = db.rawQuery(sql, null);
            if (c.getCount() == 0) {
                // Log.d("DatabaseWeek", "null 인 경우" + c.getCount());
                return 0;
            } else {
                count = c.getCount();
            }

            int columnIndex = c.getColumnIndex("study_time");
            c.moveToFirst();

            do {
                sum += c.getInt(columnIndex);
                // Log.d("DatabaseWeek", "공부시간 " + c.getInt(columnIndex) +
                // ", 날짜 : " + c.getString(c.getColumnIndex("date")));
            } while (c.moveToNext());

            // Log.d("DatabaseWeek", "" + c.getCount() + " " + sum);

        } finally {
            if (c != null) {
                c.close();
            }
        }
        // Log.d("DatabaseWeek", "최종 값 : " + c.getCount() + " " + sum);
        return sum / count;
    }

    // public int getWeekAverageTime(String date) {
    // int sum = 0;
    // int year, month, day;
    // year = Integer.parseInt(date.substring(0, 4));
    // month = Integer.parseInt(date.substring(4, 6));
    // day = Integer.parseInt(date.substring(6, 8));
    //
    // DateTime dateTime = DateTime.forDateOnly(year, month, day);
    //
    // int weekDay = dateTime.getWeekDay();
    //
    // int min, max;
    //
    // max = 7 - weekDay;
    // min = 6 - max;
    //
    // DateTime startDay = dateTime.minusDays(min);
    // DateTime endDay = dateTime.plusDays(max);
    //
    // String startStr = dateTimeToString(startDay);
    // String endStr = dateTimeToString(endDay);
    //
    // // Log.d("DatabaseWeek", "날짜 : " + startStr + " " + endStr);
    //
    // // String sql = "SELECT * FROM calendar_data WHERE date >= " + (startStr)
    // // + " AND " + "date <= " + (endStr);
    // Cursor c = null;
    //
    // try {
    // c = db.rawQuery(sql, null);
    // if (c.getCount() == 0) {
    // // Log.d("DatabaseWeek", "null 인 경우" + c.getCount());
    // return 0;
    // }else{
    // }
    //
    // int columnIndex = c.getColumnIndex("study_time");
    // c.moveToFirst();
    //
    // do {
    // sum += c.getInt(columnIndex);
    // // Log.d("DatabaseWeek", "공부시간 " + c.getInt(columnIndex) +
    // // ", 날짜 : " + c.getString(c.getColumnIndex("date")));
    // } while (c.moveToNext ());
    //
    // // Log.d("DatabaseWeek", "" + c.getCount() + " " + sum);
    //
    // } finally {
    // if (c != null) {
    // c.close();
    // }
    // }
    // // Log.d("DatabaseWeek", "최종 값 : " + c.getCount() + " " + sum);
    // return sum / 7;
    // }

    public int getGoalAchievement(String date) {
        int sum = 0;
        int year, month, day;
        year = Integer.parseInt(date.substring(0, 4));
        month = Integer.parseInt(date.substring(4, 6));
        day = Integer.parseInt(date.substring(6, 8));

        DateTime dtime = DateTime.forDateOnly(year, month, day);

        dtime = dtime.minusDays(7);

        String time = dateTimeToString(dtime);

        // DateTime endDay = DateTime.forDateOnly(year, month, day);
        //
        // int weekDay = endDay.getWeekDay();
        //
        // DateTime startDay = endDay.minusDays(6);
        //
        // String startStr = dateTimeToString(startDay);
        // String endStr = dateTimeToString(endDay);
        //
        // Log.d("SUMSUM", "날짜 : " + startStr + " " + endStr);

        // String sql = "SELECT * FROM calendar_data WHERE date >= " +
        // (startStr) + " AND " + "date <= " + (endStr);

        String sql = "SELECT * FROM calendar_data WHERE (date > " + time + " OR date = " + time + ") AND date != " + date
                + " ORDER BY date ASC Limit 7";

        Cursor c = null;
        int count = 0;

        try {
            c = db.rawQuery(sql, null);
            c.moveToFirst();
            if (c.getCount() == 0) {
                Log.d("SUMSUM", "null 인 경우" + c.getCount());
                return 0;
            }

            int columnIndexStudy = c.getColumnIndex("study_time");
            int columnIndexGoal = c.getColumnIndex("goal_time");
            count = c.getCount();
            while (!c.isAfterLast()) {
                Log.d("SUMSUM", "" + ", " + c.getInt(columnIndexStudy) + " " + c.getInt(columnIndexGoal));
                if (c.getInt(columnIndexStudy) >= c.getInt(columnIndexGoal)) {
                    sum++;
                }
                c.moveToNext();
                // Log.d("DatabaseWeek", "공부시간 " + c.getInt(columnIndex) +
                // ", 날짜 : " + c.getString(c.getColumnIndex("date")));
            }
            ;
            // Log.d("DatabaseWeek", "" + c.getCount() + " " + sum);

        } catch (android.database.sqlite.SQLiteException e) {

        } finally {
            if (c != null) {
                c.close();
            }
        }

        return (sum * 100) / count;
    }

    public int getAccumulateStudyTime() {
        int sum = 0;

        String sql = "SELECT study_time FROM calendar_data";
        Cursor c = null;

        try {
            c = db.rawQuery(sql, null);
            c.moveToFirst();
            if (c.getCount() == 0) {
                return 0;
            }
            int columnIndexStudy = c.getColumnIndex("study_time");

            while (!c.isAfterLast()) {
                sum += c.getInt(columnIndexStudy);
                c.moveToNext();
                // Log.d("DatabaseWeek", "공부시간 " + c.getInt(columnIndex) +
                // ", 날짜 : " + c.getString(c.getColumnIndex("date")));
            }
            ;
            // Log.d("DatabaseWeek", "" + c.getCount() + " " + sum);

        } finally {
            if (c != null) {
                c.close();
            }
        }

        return sum;
    }

    public String dateTimeToString(DateTime dateTime) {

        String year, month, day;

        year = dateTime.getYear().toString();
        if (dateTime.getMonth() < 10) {
            month = "0" + dateTime.getMonth().toString();
        } else {
            month = dateTime.getMonth().toString();
        }
        if (dateTime.getDay() < 10) {
            day = "0" + dateTime.getDay().toString();
        } else {
            day = dateTime.getDay().toString();
        }

        return year + month + day;
    }

    /**
     * Title : Upgrade Algorithm Programmer : Kang Il Gu Date : 14.09.03(WED) ~
     * 14.09.22(MON), '14.10.13(MON) ~ '14.10.14(TUE) Cooperated :
     * 'DBPool.java', 'DBState.java', 'DBService.java', "WordListFragment.java'
     * Contents : 1. Calculate Forgetting Curves 2. Calculate Score And Time Of
     * Words 3. Update Forgetting Curves By New Inputs As Using App
     */

    public static final int CHANGED = 0;
    public static final int UNCHANGED = 1;
    public static int stateCnt = 10; // ignore 9th state
    public static int Maxstate = 100; // ignore 9th state
    public static final int infinite = 50000; // beyond max time, which is 26280
    // = 24(hour) * 365(day) *
    // 3(year) : period of high
    // school days
    public static final double delta = (double) 0.001; // allowed error

    public void calcMaxCnt() {
        Cursor cursor = db.rawQuery("SELECT MAX(state) AS state FROM forgetting_curves", null);

        cursor.moveToFirst();

        stateCnt = cursor.getInt(cursor.getColumnIndex("state"));
    }

    // //////////////////////////////////////////////////////////
    // ////////// 1. Calculate Forgetting Curves ////////////////
    // //////////////////////////////////////////////////////////

    // ///////////////////// Control DB /////////////////////////

    // get forgetting curves as state
    public DBState[] getForgettingCurvesAsDBState() {
        DBState[] st = new DBState[stateCnt];
        Log.e("error_check", "stateCnt : " + stateCnt);
        boolean isNan = false;
        for (int i = 0; i < stateCnt; i++) {
            Cursor cursor = db.rawQuery(" SELECT * FROM forgetting_curves WHERE state = " + (i + 1) + " ORDER BY time", null);
            cursor.moveToFirst();
            if (cursor.getCount() < 3) {
                st[i] = insertinitailCurve(i + 1);
            } else {
                st[i] = new DBState(cursor.getCount()); // declare object
                for (int j = 0; j < st[i].count; j++) { // assign values
                    st[i].time[j] = cursor.getInt(cursor.getColumnIndex("time")); // time

                    st[i].prob[j] = cursor.getDouble(cursor.getColumnIndex("probability")); // probability

                    st[i].freq[j] = cursor.getInt(cursor.getColumnIndex("frequency")); // frequency
                    cursor.moveToNext();

                    if (Double.isNaN(st[i].prob[j])) {
                        isNan = true;
                        break;
                    }

                }
            }
            cursor.close();
            if (isNan) {
                break;
            }
        }
        if (isNan) {
            MildangDate date = new MildangDate();
            st = null;
            Cursor cursor = db.rawQuery(" SELECT * FROM forgetting_curves WHERE probability = 'NaN' GROUP BY state", null);
            cursor.moveToFirst();
            if (cursor.getCount() > 0) {
                do {
                    db.execSQL("DELETE FROM forgetting_curves WHERE state ='" + cursor.getInt(cursor.getColumnIndex("state")) + "'");
                } while (cursor.moveToNext());
            }
            //3.MMMMMMMMMMMMMMMMMMMst = initialForgettingCurve();
        }
        Log.e("New Algorithm", "getForgettingCurvesAsDBState");
        return st;
    }

    public DBState insertinitailCurve(int state) {


        db.execSQL("INSERT OR REPLACE into forgetting_curves(id, state, time, probability, frequency) "
                + "values((SELECT id FROM forgetting_curves WHERE state = " + state + " AND time = '50000')," + state + ", 0, 1, 0)");
        db.execSQL("INSERT OR REPLACE into forgetting_curves(id, state, time, probability, frequency) "
                + "values((SELECT id FROM forgetting_curves WHERE state = " + state + " AND time = '50000')," + state + ", 24, " + 0.98 + ", 1)");
        db.execSQL("INSERT OR REPLACE into forgetting_curves(id, state, time, probability, frequency) "
                + "values((SELECT id FROM forgetting_curves WHERE state = " + state + " AND time = '50000')," + state + ", 50000, 0, 0)");

        Cursor cursor = db.rawQuery(" SELECT * FROM forgetting_curves WHERE state = " + state + " ORDER BY time", null);
        cursor.moveToFirst();
        DBState dbstate = new DBState(cursor.getCount());
        for (int j = 0; j < dbstate.count; j++) { // assign values
            dbstate.time[j] = cursor.getInt(cursor.getColumnIndex("time")); // time

            dbstate.prob[j] = cursor.getDouble(cursor.getColumnIndex("probability")); // probability

            dbstate.freq[j] = cursor.getInt(cursor.getColumnIndex("frequency")); // frequency
            cursor.moveToNext();
        }
        cursor.close();
        return dbstate;
    }


    public DBState[] initialForgettingCurve() {

        double initail_pro = 0.69;

        db.beginTransaction();
        int deleted = db.delete("forgetting_curves", null, null);
        db.setTransactionSuccessful();
        db.endTransaction();


        DBState[] st = new DBState[11];

        for (int i = 1; i < 11; i++) {
            st[i - 1] = new DBState(3);
            for (int j = 1; j < 4; j++) {
                ContentValues values = new ContentValues();
                switch (j) {
                    case 1:
                        values.put("state", i);
                        values.put("time", 0);
                        values.put("probability", 1);
                        values.put("frequency", 0);

                        st[i - 1].time[j - 1] = 0;
                        st[i - 1].prob[j - 1] = 1;
                        st[i - 1].freq[j - 1] = 0;
                        break;
                    case 2:
                        values.put("state", i);
                        values.put("time", 24);
                        values.put("probability", initail_pro + ((double) i * 0.02));
                        values.put("frequency", 1);

                        st[i - 1].time[j - 1] = 24;
                        st[i - 1].prob[j - 1] = initail_pro + (i * 0.02);
                        st[i - 1].freq[j - 1] = 1;
                        break;
                    case 3:
                        values.put("state", i);
                        values.put("time", 50000);
                        values.put("probability", 0);
                        values.put("frequency", 0);

                        st[i - 1].time[j - 1] = 50000;
                        st[i - 1].prob[j - 1] = 0;
                        st[i - 1].freq[j - 1] = 0;
                        break;
                }
                Log.e("tptptp", "" + st[i - 1].freq[j - 1]);
                db.insert("forgetting_curves", null, values);
            }
        }
        return st;

    }

    // update a changed forgetting curve data
    public void updateAForgettingCurveTable(int st_idx, DBState origin_st, DBState st) {
        try {
            db.beginTransaction();
            for (int i = 0; i < st.count; i++) {
                if (origin_st.prob[i] != st.prob[i]) {
                    db.execSQL(" UPDATE forgetting_curves " + " SET probability = '" + st.prob[i] + "' " + ", frequency = '" + st.freq[i] + "'"
                            + " WHERE state = '" + (st_idx + 1) + "' and time = '" + st.time[i] + "' ");
                }
            }
            db.setTransactionSuccessful();
        } catch (SQLException e) {
            // TODO: handle exception
        } finally {
            db.endTransaction();
        }
    }

    // ////////////////////// Algorithm /////////////////////////

    // fit forgetting curves : new version of "calcProbability()"
    // call fitAForgettingCurveToDecreasingFunction(),
    // fitForgettingCurvesBetweenTwoStates()
    public void fitForgettingCurves(DBState[] st) {

        int loopCnt = 0;
        int fitToDecreasingFunctionFlag = UNCHANGED; // whether data is changed
        // or not after
        // fitForgettingCurveToDecreasingGraph()
        int fitBetweenTwoStatesFlag = UNCHANGED; // whether data is changed or
        // not after
        // fitFourPointsOnDifferentStates()
        int totalStatesFittingFlag = UNCHANGED; // whether data is fitted or not
        // : UNCHANGED means fitting is
        // done.
        int totalStatesFittingFlagArray[] = new int[stateCnt - 1]; // segments
        // of
        // totalStatesFittingFlag

        // temporarily store original value of st[] to compare with changed st[]
        DBState[] origin_st = new DBState[stateCnt]; // original value of st[]
        for (int i = 0; i < stateCnt; i++) {
            origin_st[i] = new DBState(st[i].count);
            for (int j = 0; j < st[i].count; j++) { // assign values
                origin_st[i].time[j] = st[i].time[j]; // time
                origin_st[i].prob[j] = st[i].prob[j]; // probability
                origin_st[i].freq[j] = st[i].freq[j]; // frequency

                Log.d("DBSTATE", st[i].time[j] + ", " + st[i].prob[j] + ", " + st[i].freq[j]);
            }
        }

        Log.e("New Algorithm", "fitForgettingCurves() - assign");
        // fitting
        int m = 0;
        int n = 0;
        float current = SystemClock.currentThreadTimeMillis();
        float after;
        boolean isInfinite = false;
        boolean isSend = false;
        MildangDate date = new MildangDate();

        while (true) { // fit total states
            m++;
            totalStatesFittingFlag = UNCHANGED;
            for (int i = 0; i < stateCnt - 1; i++) {
                n++;
                loopCnt = 0;
                while (true) { // fit near two states
                    after = SystemClock.currentThreadTimeMillis();

                    if ((after - current) > 1000 && !isInfinite) {
                        // Log.d("dddddddddddddd", "if in");
                        if (!isSend || settings.getBoolean(MainValue.GpreErrorSQLSend, true)) {

//                            new Async_upload_error_sqlite_file(exportWordsTableToJson(), exportForgettingCurvesTableToJson(),
//                                    exportCalendarDataTableToJson(), mconContext).execute();

                            settings.edit().putString(MainActivitys.GpreTime, String.valueOf(System.currentTimeMillis())).commit();

                            isSend = true;
                            settings.edit().putBoolean(MainValue.GpreErrorSQLSend, false).commit();
                        }
                        current = SystemClock.currentThreadTimeMillis();
                        isInfinite = true;
                        break;
                    }

                    fitToDecreasingFunctionFlag = UNCHANGED;
                    fitBetweenTwoStatesFlag = UNCHANGED;
                    st[i].progress = 0;
                    st[i + 1].progress = 0;
                    fitToDecreasingFunctionFlag = (fitAForgettingCurveToDecreasingFunction(st[i]) * fitAForgettingCurveToDecreasingFunction(st[i + 1])); // fit
                    // decreasing
                    // function
                    fitBetweenTwoStatesFlag = fitForgettingCurvesBetweenTwoStates(st[i], st[i + 1]);
                    if (i == 11) {
                        Log.e("reviewtime", "st[i]    " + i);
                    }

                    // Log.d("New Algorithms", fitToDecreasingFunctionFlag + " "
                    // + fitBetweenTwoStatesFlag + " " + i);
                    if (fitToDecreasingFunctionFlag == UNCHANGED && fitBetweenTwoStatesFlag == UNCHANGED) { // Both
                        // UNCHANGED
                        totalStatesFittingFlagArray[i] = (loopCnt == 0) ? UNCHANGED : CHANGED; // in
                        // case
                        // of
                        // innerLoopCnt
                        // is
                        // 0,
                        // there's no fitted data
                        break;
                    }
                    loopCnt++;

                }
                if (isInfinite) {
                    break;
                }

            }
            Log.e("fitForgettingCurves", "a : " + totalStatesFittingFlag);
            for (int k = 0; k < stateCnt - 1; k++) {
                totalStatesFittingFlag *= totalStatesFittingFlagArray[k];
            }
            if (totalStatesFittingFlag == UNCHANGED) {
                break;
            }
            if (isInfinite) {
                break;
            }

        }

        Log.e("New Algorithm", "fitForgettingCurves() - while loop end");

        // update changed st[] data
        for (int i = 0; i < stateCnt; i++) {
            updateAForgettingCurveTable(i, origin_st[i], st[i]);
        }
    }

    // fit forgetting curve(which is on one state) to decreasing function
    // called by fitForgettingCurves()
    public int fitAForgettingCurveToDecreasingFunction(DBState st) {
        double n1, n2; // frequency
        double p1, p2; // probability
        int loopCnt = 0; // count of loop execution
        int changeFlag; // whether changed or not

        while (true) {
            changeFlag = UNCHANGED;
            for (int i = 0; i < (st.count - 1); i++) { // fitting between two
                // points
                n1 = st.freq[i]; // frequency
                n2 = st.freq[i + 1]; //
                p1 = st.prob[i]; // probability
                p2 = st.prob[i + 1]; //
                if (p2 > p1 + delta) { // fitting
					/*
					 * 2015 02 23 버리는부분 int adjuster = (n1 + n2) % 2; // handle
					 * the case that sum of
					 */
                    // int adjuster = 0;
                    // frequency is odd number
                    if (n1 > 0) { // if(n1==0) n1 is start point -
                        // '15.01.06(Tue) update
                        st.freq[i] = (n1 + n2) / 2; // frequency
                        st.freq[i + 1] = (n1 + n2) / 2; //
                    }
                    st.prob[i] = (n2 * p1 + n1 * p2) / (n1 + n2); // probability
                    st.prob[i + 1] = st.prob[i]; //
                    changeFlag *= CHANGED;
                }
            }
            if (changeFlag == UNCHANGED) { // needless to change(fit) on every
                // points
                break;
            }
            loopCnt++;
        }

        return (loopCnt == 0) ? UNCHANGED : CHANGED; // UNCHANGED : function is
        // not need to fit /
        // CHANGED : function is
        // fitted
    }

    // fit forgetting curves between two states : make that bigger state graph
    // is larger(or same) than(to) smaller state graph
    // it contains fitFourPointsOnDifferentStates() method.
    // called by fitForgettingCurves()
    public int fitForgettingCurvesBetweenTwoStates(DBState st1, DBState st2) { // st2
        // is
        // next
        // one
        // of
        // st1
        int fitFourPointFlag = UNCHANGED;
        while (true) {
            if ((st1.progress + 1 == st1.count - 1) && (st2.progress + 1 == st2.count - 1)) {
                // Log.d("BREAK", "break");
                break;
            }
            fitFourPointFlag *= fitFourPointsOnDifferentStates(st1, st2);
            // Log.d("UNCHANGED", fitFourPointFlag + " fitFourPointFlag");
        }
        return fitFourPointFlag;
    }

    // fit two points which are on different states respectively : ex) state1 :
    // (t1,p1), (t2,p2) / state2 : (t3,p3), (t4,p4)
    public static int fitFourPointsOnDifferentStates(DBState st1, DBState st2) {
        final int PROPER = 1;
        final int IMPROPER = 0;

        int changeFlag = UNCHANGED;

        // index on state
        int idx1 = st1.progress;
        int idx2 = st2.progress;

        // time

        int t1 = st1.time[idx1];
        int t2 = st1.time[idx1 + 1];
        int t3 = st2.time[idx2];
        int t4 = st2.time[idx2 + 1];


        // probability
        double p1 = st1.prob[idx1];
        double p2 = st1.prob[idx1 + 1];
        double p3 = st2.prob[idx2];
        double p4 = st2.prob[idx2 + 1];

        // // frequency
        double n1 = st1.freq[idx1];
        double n2 = st1.freq[idx1 + 1];
        double n3 = st2.freq[idx2];
        double n4 = st2.freq[idx2 + 1];

        // relative frequency
        double N1 = n2 + n3 + n4;
        double N2 = n1 + n3 + n4;
        double N3 = n1 + n2 + n4;
        double N4 = n1 + n2 + n3;

        if (t1 == 0)
            N1 = 0; // concerning start point : should not be changed
        if (t3 == 0)
            N3 = 0; //
        if (t2 == infinite)
            N2 = 0; // concerning end point : should not be changed
        if (t4 == infinite)
            N4 = 0; //

        // check whether bigger state graph is upper than smaller state graph
        // PROPER : value of smaller state is smaller than(or equal to) value of
        // bigger state
        // IMPROPER : value of smaller state is bigger than value of bigger
        // state
        int graphPositionFlag = PROPER; // t1, t3
        if (t1 >= t3) { // bigger state graph : p = ((p4-p3)/(t4-t3))*(t-t3) +
            // p3
            graphPositionFlag *= (p1 <= ((p4 - p3) / (t4 - t3)) * (t1 - t3) + p3 + delta) ? PROPER : IMPROPER;
        } else { // t1<t3 //smaller state graph : p = ((p2-p1)/(t2-t1))*(t-t1) +
            // p1
            graphPositionFlag *= (p3 + delta >= ((p2 - p1) / (t2 - t1)) * (t3 - t1) + p1) ? PROPER : IMPROPER;
        }
        // t2, t4
        if (t2 <= t4) { // bigger state graph : p = ((p4-p3)/(t4-t3))*(t-t3) +
            // p3
            graphPositionFlag *= (p2 <= ((p4 - p3) / (t4 - t3)) * (t2 - t3) + p3 + delta) ? PROPER : IMPROPER;
        } else { // t2>t4 //smaller state graph : p = ((p2-p1)/(t2-t1))*(t-t1) +
            // p1
            graphPositionFlag *= (p4 + delta >= ((p2 - p1) / (t2 - t1)) * (t4 - t1) + p1) ? PROPER : IMPROPER;
        }

        // fitting
        if (graphPositionFlag == PROPER) {
            changeFlag = UNCHANGED; // do nothing
        } else {
            changeFlag = CHANGED;
            // find intersection
            // Four points will be fitted as p1, p2 are decreased, and p3, p4
            // are increased.
            // Because smaller state graph should be always lower than(or equal
            // to) bigger state graph.
            // (t1,p1) -> (t1,p1-N1*x), (t2,p2) -> (t2,p2-N2*x), (t3,p3) ->
            // (t3,p3+N3*x), (t4,p4) -> (t4,p4+N4*x)
            double x; // variable, which is adjuster, is adjusted to fit two
            // linear graph

            // min(t2,t4)의 point가 다른 state의 직선 위에 올라가도록 fitting
            if (t2 >= t4) {
                // Linear graph which has two points (t1,p1-N1*x), (t2,p2-N2*x)
                // : p' = (((p2-p1)-(N2-N1)*x)/(t2-t1))*(t-t1) + (p1-N1*x)
                // This function should contain (t4,p4+N4*x) by
                // adjusting 'x'. So substitute (t4,p4+N4*x) on linear function.
                x = ((p1 - p4) + (t4 - t1) * (p2 - p1) / (t2 - t1)) / ((N1 + N4) + (double) (t4 - t1) * (N2 - N1) / (t2 - t1));
                // Log.d("ttttt", "aaa " + x );
            } else {
                // Linear graph which has two points (t3,p3+N3*x), (t4,p4+N4*x)
                // : p' = (((p4-p3)+(N4-N3)*x)/(t4-t3))*(t-t4) + (p4+N4*x)
                // This function should contain (t2,p2-N2*x) by
                // adjusting 'x'. So substitute (t2,p2-N2*x) on linear function.
                x = ((p2 - p4) - (t2 - t4) * (p4 - p3) / (t4 - t3)) / ((N2 + N4) + (double) (t2 - t4) * (N4 - N3) / (t4 - t3));
            }

            // frequency - '15.01.06(Tue) update
            if (n1 == 0 && n3 == 0) {
                st1.freq[idx1] = 0;
                st1.freq[idx1 + 1] = (n1 + n2 + n3 + n4) / 2;
                st2.freq[idx2] = 0;
                st2.freq[idx2 + 1] = (n1 + n2 + n3 + n4) / 2;
            } else if (n1 == 0 && n3 != 0) {
                st1.freq[idx1] = 0;
                st1.freq[idx1 + 1] = (n1 + n2 + n3 + n4) / 3;
                st2.freq[idx2] = (n1 + n2 + n3 + n4) / 3;
                st2.freq[idx2 + 1] = (n1 + n2 + n3 + n4) / 3;
            } else if (n1 != 0 && n3 == 0) {
                st1.freq[idx1] = (n1 + n2 + n3 + n4) / 3;
                st1.freq[idx1 + 1] = (n1 + n2 + n3 + n4) / 3;
                st2.freq[idx2] = 0;
                st2.freq[idx2 + 1] = (n1 + n2 + n3 + n4) / 3;
            } else {
                st1.freq[idx1] = (n1 + n2 + n3 + n4) / 4;
                st1.freq[idx1 + 1] = (n1 + n2 + n3 + n4) / 4;
                st2.freq[idx2] = (n1 + n2 + n3 + n4) / 4;
                st2.freq[idx2 + 1] = (n1 + n2 + n3 + n4) / 4;
            }
            // st1.freq[idx1] = (n1 + n2 + n3 + n4) / temp_n;
            // st1.freq[idx1 + 1] = (n1 + n2 + n3 + n4) / temp_n;
            // st2.freq[idx2] = (n1 + n2 + n3 + n4) / temp_n;
            // st2.freq[idx2 + 1] = (n1 + n2 + n3 + n4) / temp_n + adjuster;

            // probability
            st1.prob[idx1] = p1 - N1 * x; // p1 -> p1 - N1*x
            st1.prob[idx1 + 1] = p2 - N2 * x; // p2 -> p2 - N2*x
            st2.prob[idx2] = p3 + N3 * x; // p3 -> p3 + N3*x
            st2.prob[idx2 + 1] = p4 + N4 * x; // p4 -> p4 + N4*x

            // //////////////////////////
            // prob가 1보다 크거나 0보다 작아지는 경우에 최대 최소값을 지정한다.

            if (st1.prob[idx1] > 1) {
                st1.prob[idx1] = 1;
            } else if (st1.prob[idx1] < 0) {
                st1.prob[idx1] = 0;
            }
            if (st1.prob[idx1 + 1] > 1) {
                st1.prob[idx1 + 1] = 1;
            } else if (st1.prob[idx1 + 1] < 0) {
                st1.prob[idx1 + 1] = 0;
            }
            if (st2.prob[idx2] > 1) {
                st2.prob[idx2] = 1;
            } else if (st2.prob[idx2] < 0) {
                st2.prob[idx2] = 0;
            }
            if (st2.prob[idx2 + 1] > 1) {
                st2.prob[idx2 + 1] = 1;
            } else if (st2.prob[idx2 + 1] < 0) {
                st2.prob[idx2 + 1] = 0;
            }
            // //////////////////////////
        }

        if ((t2 <= t4) && (st1.progress + 1 < st1.count - 1)) {
            st1.progress++;
        } else if ((t2 >= t4) && (st2.progress + 1 < st2.count - 1)) {
            st2.progress++;
        } else {
            ;
        }


        return changeFlag;
    }

    // //////////////////////////////////////////////////////////
    // //////// 2. Calculate Score And Time Of Words ////////////
    // //////////////////////////////////////////////////////////

    final double scoreCriterion = 0.80;
    final double scoreDelta = 0.1;
    final double scoreUpperBound = 0.99;


    // calculate score of words as forgetting curves data : new version of
    // "calcScore()"
    public void calculateAndReassignScoreOfWords(DBState[] st, double elapsedTime) {

        try {

            db.beginTransaction();

            boolean isDouble = false;
            Cursor typeCursor = db.rawQuery("PRAGMA table_info(words)", null);
            typeCursor.moveToFirst();
            while (typeCursor.moveToNext()) {
                if (typeCursor.getString(typeCursor.getColumnIndex("name")).equals("Time")) {
                    if (typeCursor.getString(typeCursor.getColumnIndex("type")).equals("DOUBLE")) {
                        isDouble = true;
                    }
                    break;
                }
            }

            Cursor cursor = db.rawQuery(" SELECT * FROM words WHERE State > 0 GROUP BY State, round(Time-0.49999999999) ", null); // reason
            // of
            // grouping
            // is
            // that
            // update
            // words
            cursor.moveToFirst(); // by one time of updating
            while (!cursor.isAfterLast()) {
                if (cursor.getInt(cursor.getColumnIndex("State")) >= stateCnt + 1) {
                    ; // ignore 9th state
                } else {
                    double prob = getEstimatedProbabilityByForgettingCurves(
                            (int) Math.floor(cursor.getFloat(cursor.getColumnIndex("Time")) + elapsedTime),
                            st[cursor.getInt(cursor.getColumnIndex("State")) - 1]);

                    double score = scoreCriterion - prob; // edit

                    if (isDouble) {
                        Log.e("reviewtime", "isDouble true");
                        db.execSQL(" UPDATE words " + " SET Score = " + score + " , Time = Time + " + elapsedTime + " WHERE State = "
                                + cursor.getInt(cursor.getColumnIndex("State")) + " AND round(Time-0.49999999999) = " // 15.03.03
                                // 양대현,
                                // x시간후
                                // 복습단어가
                                // 0개로
                                // 나오는
                                // 문제
                                // 수정.
                                // -0.5를
                                // 반올림하면
                                // 0
                                // 이
                                // 될거라고
                                // 생각했으나
                                // -1이
                                // 되어서
                                // 발생한
                                // 문제
                                + cursor.getInt(cursor.getColumnIndex("Time")));
                        // db.execSQL(" UPDATE words " + " SET Score = " + score
                        // + " , Time = Time + " + elapsedTime
                        // + " WHERE State = "
                        // + cursor.getInt(cursor.getColumnIndex("State"))
                        // + " AND Time = "
                        // + cursor.getDouble(cursor.getColumnIndex("Time")));

                    } else {

                        Log.e("reviewtime", "isDouble false");

                        db.execSQL(" UPDATE words " + " SET Score = " + score + " , Time = Time + " + elapsedTime + " WHERE State = "
                                + cursor.getInt(cursor.getColumnIndex("State")) + " AND round(Time-0.49999999999) = " // 15.03.03
                                // 양대현,
                                // x시간후
                                // 복습단어가
                                // 0개로
                                // 나오는
                                // 문제
                                // 수정.
                                // -0.5를
                                // 반올림하면
                                // 0
                                // 이
                                // 될거라고
                                // 생각했으나
                                // -1이
                                // 되어서
                                // 발생한
                                // 문제
                                + cursor.getInt(cursor.getColumnIndex("Time")));
                    }
                }
                cursor.moveToNext();
            }
            cursor.close();
            Log.e("New Algorithm", "calculateScoreOfWords()");

            db.setTransactionSuccessful();

        } catch (SQLException e) {
            // TODO: handle exception
        } finally {
            db.endTransaction();
        }

    }

    // get estimated probability by forgetting curves
    public double getEstimatedProbabilityByForgettingCurves(int t, DBState st) {
        int index = getIndexByBisectionMethod(st, t); // get index which satisfy
        // that ( st.time[index]
        // <= t <
        // st.time[index+1] )
        double p;

        int t1 = st.time[index];
        int t2 = st.time[index + 1];
        double p1 = st.prob[index];
        double p2 = st.prob[index + 1];
        if (t2 != infinite) {
            p = ((p2 - p1) / (t2 - t1)) * (t - t1) + p1;
        } else { // t2==infinite -> t1 is end point on forgetting_curves
            p1 = (p1 > scoreUpperBound) ? scoreUpperBound : p1; // '15.01.21(Wed)
            // update :
            // introduce
            // upper bound
            p = Math.pow(Math.E, (Math.log(p1) / t1) * t);
        }

        return p;
    }

    // get index by bisection method : st.time[index] <= value <
    // st.time[index+1]
    public int getIndexByBisectionMethod(DBState st, int value) {
        int a = 0; // start point(alpha)
        int w = st.count - 1; // end point(omega)
        int mid = (a + w) / 2; // mid point
        int preMid; // previous mid point
        do {
            preMid = mid;
            if (value >= st.time[mid]) {
                a = mid;
                mid = (mid + w) / 2;
            } else {
                w = mid;
                mid = (a + mid) / 2;
            }
        } while (mid != preMid);

        return mid;
    }

    // //////////////////////////////////////////////////////////
    // 3. Update Forgetting Curves By New Inputs As Using App //
    // //////////////////////////////////////////////////////////

    // get inputs of forgetting curves, then update values : new version of
    // "updateWordInfo()"
    public void updateForgettingCurvesByNewInputs(Word word, int location, boolean knownFlag) {

        // update or insert forgetting_curves data
        if (word.getState() > 0 && word.getState() < stateCnt + 1) { // forgetting
            // curves,
            // whose state is 0
            // or -1, is not
            // exist

            DBState st = getProbAndFreqOnAForgettingCurve(word); // use just one
            // storage
            // of array
            // as struct
            // in C
            // language

            double freq = st.freq[0]; // frequency of corresponding word status
            double prob = st.prob[0]; // probability of corresponding word
            // status

            // update frequency and probability on a forgetting curve
            int delta = (knownFlag == true) ? 1 : 0; // known or not
            double updateProb = (freq * prob + delta) / (freq + 1);

            if (word.getState() < 100) {

                if (freq == 0 && prob == 0) { // point(state, time) is not existed
                    db.execSQL(" INSERT INTO forgetting_curves(state, time, probability, frequency) " + " VALUES( " + word.getState() + ", "
                            + word.getTime(0) + ", " + // state, time
                            updateProb + ", " + (freq + 1) + " ) "); // probability,
                    Log.e("DBPool", "insert forgetting : " + word.getState() + "  " + (freq + 1));
                    // frequency
                } else { // point(state, time) is existed
                    if (word.getTime(0) > 0) { // forgetting_curve time이 0일때 수정하지
                        // 않도록 하기 위해 추가
                        try {
                            db.execSQL(" UPDATE forgetting_curves " + " SET frequency = " + (freq + 1) + " , probability = " + updateProb
                                    + " WHERE state = " + word.getState() + " AND time = " + word.getTime(0));
                            Log.e("DBPool", "UPDATE forgetting : " + word.getState() + "  " + (freq + 1));
                        } catch (Exception e) {
                            // TODO: handle exception
                            // 3.0.4bug need for check
                        }

                    }
                }
            }

        }

        // update word information : frequency, state, score, and time
        if (word.getState() < 0 && knownFlag == false) {
            ; // state -1 -> state -1 : still don't know word
        } else {
            ContentValues values = new ContentValues();
            values.put("Frequency", word.getFrequency() + 1); // frequency
            if (knownFlag == true) { // word which is known //state
                switch (word.getState()) {
                    case -1: // not remember word
                        values.put("State", 1);
                        insertWordLog(word.get_id(), location, word.getState(), 1);
                        break;
                    case 0: // new word
                        values.put("State", 100);
                        insertWordLog(word.get_id(), location, word.getState(), 100);
                        break;
                    default: // studied word
                        values.put("State", word.getState() + 1);
                        insertWordLog(word.get_id(), location, word.getState(), word.getState() + 1);
                        break;
                }
            } else { // word which is unknown
                values.put("State", -1);
                insertWordLog(word.get_id(), location, word.getState(), -1);
            }

            if (word.getState() > 0) { // score and time on studied word :
                // prevent to show word again on this
                // time of studying.
                values.put("Score", 0);
                values.put("Time", 0);
            } else {
                ; // needless to initialization : score and time is 0 when
                // word.getState() = -1 || 0
            }

            values.put("forgetting_curves_test_flag", "0");

            db.update("words", values, "word_code = " + word.get_id(), null); // update

        }
    }

    public void isMaxState(int value) {
        int upper_state = value + 1;

        Log.e("DBPool", "isMaxState start");

		/*
		 * forgetting_curves에 state 9가 존재 하지 않으면 만들어줘야함.
		 */
        Cursor isState9 = db.rawQuery("SELECT * FROM forgetting_curves WHERE state = " + upper_state, null);

        isState9.moveToFirst();


        if (isState9.getCount() == 0) {
            if (value < 100) {
                db.execSQL("insert OR REPLACE into forgetting_curves(state, time, probability, frequency) " + "values(" + upper_state + ", 0, 1, 0)" +
                        ",(" + upper_state + ", 24, " + 0.95 + ", 1),(" + upper_state + ", 50000, 0, 0)");
            }
        }
        isState9.close();

        if (value > 7) {

            Cursor cursor = db.rawQuery("SELECT MAX(state) AS state, probability FROM forgetting_curves WHERE time = 24", null);
            cursor.moveToFirst();

            if (cursor.getCount() == 0) {
                return;
            }

            double MAX_Probability = 0.999;
            if (cursor.getDouble(cursor.getColumnIndex("probability")) < 1.0) {
                MAX_Probability = cursor.getDouble(cursor.getColumnIndex("probability"))
                        + (1.0 - cursor.getDouble(cursor.getColumnIndex("probability"))) / 2;
            }

            if (upper_state > cursor.getInt(cursor.getColumnIndex("state")) && upper_state < Maxstate) {

                db.execSQL("insert OR REPLACE into forgetting_curves(state, time, probability, frequency) " + "values(" + upper_state + ", 0, 1, 0)" +
                        ",(" + upper_state + ", 24, " + MAX_Probability + ", 1),(" + upper_state + ", 50000, 0, 0)");

                Log.e("DBPool", "isMaxState : make next state");
            }

            cursor.close();

        }
        calcMaxCnt();
    }

    // get probability and frequency on a forgetting curves if existed
    public DBState getProbAndFreqOnAForgettingCurve(Word word) {
        DBState st = new DBState(1); // use freq, prob : frequency and
        // probability of corresponding word
        // status

        // get frequency and probability on current word status
        Cursor cursor = db.rawQuery(" SELECT probability, frequency " + " FROM forgetting_curves " + " WHERE state = " + word.getState()
                + " AND time = " + word.getTime(0), null);
        cursor.moveToFirst();
        if (cursor.getCount() == 0) { // point(state, time) is not existed
            st.freq[0] = 0;
            st.prob[0] = 0;
        } else { // point(state, time) is existed
            st.freq[0] = cursor.getInt(cursor.getColumnIndex("frequency"));
            st.prob[0] = cursor.getDouble(cursor.getColumnIndex("probability"));
        }
        cursor.close();

        return st;
    }

    /**
     * Title : Upload State0 Flag Programmer : Kang Il Gu Date : '15.01.21(Wed)
     * Cooperated :
     */

    /**
     * Title : Upload State0 Flag Programmer : Kang Il Gu Date : 14.09.22(MON) ~
     * 'Async_upload_state0_flag.java', 'WordListFragment.java',
     * 'upload_state0_flag.php'
     */

    public Word[] getState0FlagTable() {
        Cursor cursor = db.rawQuery(" SELECT * FROM state0_flag ORDER BY word_code ASC ", null);
        cursor.moveToFirst();

        Word[] word = new Word[cursor.getCount()];
        if (cursor.getCount() != 0) {
            for (int i = 0; i < cursor.getCount(); i++) {

                word[i] = new Word(cursor.getInt(cursor.getColumnIndex("word_code")), cursor.getInt(cursor.getColumnIndex("known_flag")), null, null);
                cursor.moveToNext();
            }
        }


        cursor.close();

        return word;

    }

    // upload state0_flag table (after upload, delete all)
    // public void deleteState0FlagTable(int word_code) {
    public void deleteState0FlagTable() {
        // db.execSQL(" DELETE FROM state0_flag WHERE word_code <= " +
        // word_code); // delete
        db.execSQL(" DELETE FROM state0_flag"); // delete
        // all

        Log.e("State0 Flag", "deleteState0FlagTable()");
    }

    public void insertState0FlagTableElement(Word word, boolean knownFlag) {
        if (word.getState() == 0) {
            db.execSQL(" INSERT INTO state0_flag(word_code, known_flag) " + " VALUES( " + word.get_id() + ", " + ((knownFlag == true) ? 1 : 0) + " )");
        }

        Log.e("State0 Flag", "insertState0FlagTableElement()");
    }

    /**
     * Title : Learning Status - 'WILL' Part : upload use time of user
     * Programmer : Kang Il Gu Date : 14.10.02(Thu) ~ 14.10.06(Mon),
     * '14.12.09(Tue) Description : '14.12.09(Tue) Add 'use_time_sec' column and
     * change name of columns Cooperated : 'DBPool.java', 'UseTime.java',
     * 'UploadUseTime.java', 'Async_upload_use_time.java', 'upload_use_time.php'
     */

    // get use_time_tbl
    public UseTime getUseTimeTable() {

        Cursor cursor = db.rawQuery(" SELECT * FROM use_time_tbl ORDER BY use_time_date ASC ", null);
        cursor.moveToFirst();

        UseTime useTime = new UseTime(cursor.getCount() + 1); // the reason of
        // size, which
        // is (count+1),
        // is for
        // current use
        // time data
        // count + 1 =
        // existed data
        // + new data
        if (useTime.count > 1) { // data is existed
            for (int i = 0; i < useTime.count - 1; i++) {
                useTime.grade[i] = cursor.getInt(cursor.getColumnIndex("grde")); // grade

                useTime.use_time[i][0] = cursor.getInt(cursor.getColumnIndex("use_time")); // use_time
                // :
                // converted
                // to
                // second
                useTime.use_time[i][1] = cursor.getInt(cursor.getColumnIndex("use_time_date")); // use_time
                // :
                // date(yyyymmdd)
                cursor.moveToNext();
            }
        } else { // no existed data
            Log.e("Will part", "No existed data()");
            ;
        }

        cursor.close();
        Log.e("Will part", "checkUserTimeTableDataExist()");
        return useTime;

    }

    // insert user use time
    public void insertUseTimeData(UseTime useTime) {
        for (int i = 0; i < useTime.count; i++) {
            db.execSQL(" INSERT INTO use_time_tbl(grade, use_time_sec, use_time_date) " + " VALUES( " + useTime.grade[i] + ", "
                    + useTime.use_time[i][0] + ", " + useTime.use_time[i][2] + ") ");
        }

        Log.e("Will part", "insertUseTimeData()");
    }

    // delete use_time_tbl
    public void deleteUseTimeData() {
        db.execSQL(" DELETE FROM use_time_tbl ");

        Log.e("Will part", "deleteUseTimeData()");
    }

    /**
     * Title : Learning Status - 'Forgetting Curves(Memory)' Part : notify next
     * review time Programmer : Kang Il Gu Date : 14.10.07(Tue) ~ 14.10.08(Wed)
     * Cooperated : 'DBPool.java', 'GetReviewTime.java'
     */

    // get max time on each states
    public int[] getMaxTimeOnEachStates() {
        Cursor cursor = db.rawQuery(" SELECT state, MAX(time) AS time " + " FROM words " + " WHERE state > 0 AND state <= " + stateCnt
                + " GROUP BY state ORDER BY state ", null);

        cursor.moveToFirst();
        // Log.e("reviewtime",
        // "" + cursor.getCount() + " "
        // + cursor.getInt(cursor.getColumnIndex("State")) + "  "
        // + cursor.getInt(cursor.getColumnIndex("time")));
        int[] time = new int[stateCnt];
        for (int i = 0; i < stateCnt; i++) {
            time[i] = -50000;
        }

        if (cursor.getCount() > 0) {
            for (int i = 0; i < cursor.getCount(); i++) {
                time[cursor.getInt(cursor.getColumnIndex("State")) - 1] = cursor.getInt(cursor.getColumnIndex("time")); // max
                // time
                // on a
                // state
                cursor.moveToNext();
            }
        }

        cursor.close();
        return time; // max time on a state
    }

    // get start time of review
    public int[] getStartTimeOfReview() {
        // get time max(bigger than scoreCriterion) and min(smaller than
        // scoreCriterion)
        Cursor cursor = db.rawQuery(" SELECT state, MAX(time) AS time, probability " + // biggest
                // time
                // among
                // under
                // scoreCriterion
                " FROM forgetting_curves " + //
                " WHERE probability >= " + scoreCriterion + //
                " GROUP BY state " + //

                " UNION " +

                " SELECT state, MIN(time), probability" + // smallest
                // time
                // among
                // below
                // scoreCriterion
                " FROM forgetting_curves " + //
                " WHERE probability < " + scoreCriterion + //
                " GROUP BY state " + //
                " ORDER BY state, time", null);

        cursor.moveToFirst();

        DBState[] st = new DBState[stateCnt];
        int[] time = new int[stateCnt];
        for (int i = 0; i < stateCnt; i++) {
            st[i] = new DBState(2);
            st[i].time[0] = cursor.getInt(cursor.getColumnIndex("time")); // biggest
            // time
            // among
            // under
            // scoreCriterion
            st[i].prob[0] = cursor.getDouble(cursor.getColumnIndex("probability")); // corresponding
            // probability
            cursor.moveToNext();
            st[i].time[1] = cursor.getInt(cursor.getColumnIndex("time")); // smallest
            // time
            // among
            // below
            // scoreCriterion
            st[i].prob[1] = cursor.getDouble(cursor.getColumnIndex("probability")); // corresponding
            // probability
            cursor.moveToNext();

            // Cursor has just existing points.
            // Namely time interval of two points(st[].time[0], st[].time[1]) on
            // a state would be various.
            // Time interval what needed is 1, so estimate corresponding time
            // Below is graph which will be helpful to understand : one dash(-)
            // is 1 hour
            // calculateStartTimeOfReview return t2 when start time of review
            // o-o
            // o----------------------------o
            // 0 -------------------------------------------------> t axis
            // st[].time[0] t1 t2 st[].time[1]

            time[i] = calculateStartTimeOfReview(st[i]); // get
        }
        cursor.close();

        return time;
    }

    public boolean isReviewpriod() {
        Cursor cursor = db.rawQuery("Select SUM(1)FROM user_words where exState > 0", null);
        cursor.moveToFirst();
        Log.d("isReviewPriod", cursor.getInt(0) + " isReviewPirod");
        if (cursor.getInt(0) > 0) {
            cursor.close();
            return true;
        } else {
            cursor.close();
            return false;
        }
    }

    // get count of review word
    public int getCountOfReviewWord(int state, int time, int add_time) {
        Cursor cursor = db.rawQuery(
                " SELECT COUNT(*) AS count " + " FROM words  WHERE state = " + state + " AND round(time-0.49999999999) = " + time, null);// 15.03.03
        // 양대현,
        // x시간후
        // 복습단어가
        // 0개로
        // 나오는
        // 문제
        // 수정.
        // -0.5를 반올림하면 0 이 될거라고 생각했으나 -1이 되어서 발생한 문제

        // Log.d("DB Error", " SELECT COUNT(*) AS count " +
        // " FROM words  WHERE state = "
        // + state + " AND round(time-0.49999999999) = " + time);
        cursor.moveToFirst();
        int count = cursor.getInt(cursor.getColumnIndex("count"));
        int addReviewCount = (count / 20 > 5) ? 5 : (count / 20);
        Cursor addCursor = db.rawQuery(" 	SELECT * FROM("
                + "		SELECT word_code, word, t, State, frequency, time, time-t AS dif "
                + "	  	FROM("
                + "			SELECT a.word_code, a.word, (round(a.time-0.49999999999) + "// 15.03.03
                // 양대현,
                // x시간후
                // 복습단어가
                // 0개로
                // 나오는
                // 문제
                // 수정.
                // -0.5를 반올림하면 0 이 될거라고 생각했으나 -1이 되어서 발생한 문제
                + add_time + " ) AS t, a.State, b.frequency, b.time " + "		 	FROM words a LEFT JOIN forgetting_curves b ON a.state=b.state "
                + "		 	WHERE a.Score<=0  AND a.State>0 AND a.State<" + stateCnt + 1 + " AND b.frequency>0 AND b.frequency<20 " + "		) AS t1 "
                + "  		WHERE dif>=0 ORDER BY dif DESC" + " 	)" + " 	GROUP BY word_code " + " 	ORDER BY frequency, dif LIMIT " + addReviewCount, null);

        cursor.close();

        return count + addCursor.getCount();
    }

    public int calculateStartTimeOfReview(DBState st) {
        // Linear graph which pass by two points (t1,p1), (t2,p2) : p =
        // ((p2-p1)/(t2-t1))*(t-t1) + p1
        int t1 = st.time[0];
        int t2 = st.time[1];
        double p1 = st.prob[0];
        double p2 = st.prob[1];

        // Transport equation which is about t : t = (p-p1)*((t2-t1)/(p2-p1)) +
        // t1
        // get time which is rounded up : When (time, scoreCriterion) is passed
        // by linear graph,
        // probability, as rounded up time, is smaller than scoreCriterion.
        // It means that, on that time, user would be needed to review.
        int time = (int) Math.ceil((scoreCriterion - p1) * ((t2 - t1) / (p2 - p1)) + t1);
        return time;
    }

    /**
     * Title : wordset level changing history Programmer : Seo kang seok Date :
     * 14.10.15(Wed) Cooperated : 'DBPool.java', 'TwoButtonPopup.java'
     */

    public void insertLevelHistory(int level) {

        Date currentDate = new Date();

        try {
            db.execSQL("insert into level_history(level, time) values (" + level + ", \"" + currentDate.get_currentTime() + "\")");
            Log.d("level_change", Integer.toString(level) + ", " + currentDate.get_currentTime());

        } catch (Exception e) {
            // TODO: handle exception
            Log.d("level_change", e.getMessage());
        }

    }

    /**
     * Title : Learning Status - 'Memory' Part - Coach ment : Get Coach
     * announcment of Memory part Programmer : Kang Il Gu Date : '14.12.15(Mon)
     * Description : '14.12.15(Mon) 1. 시간에 따른 망각률 / 2. 평균 복습 시간 / 3. 줄어든 복습 시간에
     * 따른 줄어든 망각률 Cooperated : 'GetMemoryCoachMent.java', 'DBPool.java'
     */

    // get count of studied word on specific state
    public int getStudyCountOnState(int state) {
        Cursor cursor = db.rawQuery(" SELECT * FROM remember_history WHERE state = " + state, null);
        cursor.moveToFirst();
        int count = cursor.getCount();
        cursor.close();
        return count;
    }

    // get probability as time
    public double getProbabilityAsTime(int state, double forgetting_time) {
        int[] time = new int[2]; // {small time, large time}
        double[] prob = new double[2]; // {probability of small time,
        // probability of large time}

        // get time and probabiltiy around 'forgetting_time'
        Cursor cursor = db.rawQuery(" SELECT * FROM " + "		(SELECT * FROM forgetting_curves " + "		 WHERE state= " + state + " AND time<= "
                + forgetting_time + " ORDER BY time DESC LIMIT 1) " + " UNION " + " SELECT * FROM " + "		(SELECT * FROM forgetting_curves "
                + "		 WHERE state= " + state + " AND time> " + forgetting_time + " ORDER BY time ASC LIMIT 1) " + "	ORDER BY time ASC ", null);
        cursor.moveToFirst();
        for (int i = 0; i < time.length; i++) {
            time[i] = cursor.getInt(cursor.getColumnIndex("time"));
            prob[i] = cursor.getDouble(cursor.getColumnIndex("probability"));
            cursor.moveToNext();
        }

        // calculate accurate probability
        double probability = ((prob[1] - prob[0]) / (time[1] - time[0])) * (forgetting_time - time[1]) + prob[1];
        Log.e("valuecheck", "state : " + state + "  probability : " + probability);
        cursor.close();
        return probability;
    }

    // get average review time to get memory review time
    public void insertReviewTimeToGetMemoryCoachMent(Word word) {
        MildangDate mildangDate = new MildangDate();
        if (word.getState() > 0) {
            db.execSQL(" INSERT INTO remember_history(state, time, date) " + " VALUES( " + word.getState() + ", " + word.getTime(0) + ", "
                    + mildangDate.get_today() + " ) ");
        }

        Log.e("Memory part", "insertReviewTime()");
    }

    public double getReviewTime(int state) {
        MildangDate mildangDate = new MildangDate();
        Cursor cursor = db.rawQuery(
                " SELECT * FROM remember_history " + " WHERE state = " + state + " AND date >= " + (Integer.parseInt(mildangDate.get_today()) - 7),
                null);
        cursor.moveToFirst();

        double reviewSummation = 0;
        double averageReviewTime = 0;
        for (int i = 0; i < cursor.getCount(); i++) {
            reviewSummation += cursor.getInt(cursor.getColumnIndex("time"));
            cursor.moveToNext();
        }
        if (cursor.getCount() > 0) {
            averageReviewTime = reviewSummation / cursor.getCount();
        } else {
            averageReviewTime = 0;
        }

        cursor.close();
        return averageReviewTime;
    }

    public double getScoreCriterion() {
        return scoreCriterion;
    }

    // get time as probabiltiy
    public double getTimeAsProbability(int state, double probability) {
        int[] time = new int[2]; // {small probability of time, large
        // probability of time}
        double[] prob = new double[2]; // {small probability, large probability}

        // get time and probabiltiy around 'probaiblity'
        Cursor cursor = db.rawQuery(" SELECT * FROM " + "		(SELECT * FROM forgetting_curves " + "		 WHERE state= " + state + " AND probability<= "
                + probability + " ORDER BY probability DESC LIMIT 1) " + " UNION " + " SELECT * FROM " + "		(SELECT * FROM forgetting_curves "
                + "		 WHERE state= " + state + " AND probability> " + probability + " ORDER BY probability ASC LIMIT 1) "
                + "	ORDER BY probability ASC ", null);
        cursor.moveToFirst();
        for (int i = 0; i < time.length; i++) {
            time[i] = cursor.getInt(cursor.getColumnIndex("time"));
            prob[i] = cursor.getDouble(cursor.getColumnIndex("probability"));
            cursor.moveToNext();
        }

        // calculate accurate probability
        double forgetting_time = ((time[1] - time[0]) / (prob[1] - prob[0])) * (probability - prob[1]) + time[1];

        cursor.close();
        return forgetting_time;
    }

    /**
     * Title : Get count of words user studied Programmer : Kang Il Gu Date :
     * '14.12.17(Wed) Description : '14.12.17(Wed) 단어장별 외운 단어 수 Cooperated :
     */

    // get count of user studied words as grade
    public int[][] getCountOfUserStudiedWords() {
        Cursor cursor = db.rawQuery(" SELECT grade, COUNT(*) AS count FROM words WHERE Score IS NOT NULL AND State > 0 GROUP BY grade ", null);

        cursor.moveToFirst();

        int[][] countOfUserStudiedWords = new int[cursor.getCount()][2];
        for (int i = 0; i < cursor.getCount(); i++) {
            countOfUserStudiedWords[i][0] = cursor.getInt(cursor.getColumnIndex("grade"));
            countOfUserStudiedWords[i][1] = cursor.getInt(cursor.getColumnIndex("count"));
            cursor.moveToNext();
        }

        cursor.close();

        return countOfUserStudiedWords;
    }

    // get count of words as grade : total count
    public int[][] getCountOfWordsAsGrade() {
        Cursor cursor = db.rawQuery(" SELECT grade, COUNT(*) AS count FROM words GROUP BY grade ", null);
        cursor.moveToFirst();

        int[][] countOfWordsAsGrade = new int[cursor.getCount()][2];
        for (int i = 0; i < cursor.getCount(); i++) {
            countOfWordsAsGrade[i][0] = cursor.getInt(cursor.getColumnIndex("grade"));
            countOfWordsAsGrade[i][1] = cursor.getInt(cursor.getColumnIndex("count"));
            cursor.moveToNext();
        }

        cursor.close();
        return countOfWordsAsGrade;
    }

    // ///////////////////////////////////////////
    // 날짜, 분, 초, 새 학습단어, 복습단어를 임시로 저장하는 테이블
    // preference 대용
    // ///////////////////////////////////////////

    // 테이블 생성 date, minute, second, study_word, review_word
    public void createStudyTime() {

        // db.execSQL("CREATE TABLE IF NOT EXISTS study_time ("
        // + "idx INTEGER PRIMARY KEY, " + "date TEXT, "
        // + "minute INTEGER, " + "second INTEGER, "
        // + "study_word INTEGER, " + "review_word INTEGER, "
        // + "study_lv INTEGER" + ")");

        try {
            MildangDate date = new MildangDate();
            String current_date = date.get_year() + date.get_Month() + date.get_day();

            db.execSQL(" insert into study_time(date, minute, second, study_word, review_word, study_lv, idx) values('" + current_date
                    + "', 0, 0, 0, 0, 1, 1)");

        } catch (Exception e) {
            Log.e("Exception_message", e.getMessage());
        }

    }

    public String getDate() {
        MildangDate date = new MildangDate();
        String current_date = date.get_year() + date.get_Month() + date.get_day();

        Cursor c = db.rawQuery("select * from study_time where idx = 1", null);

        c.moveToFirst();
        try {
            current_date = c.getString(c.getColumnIndex("date"));
        } catch (Exception e) {
            // TODO: handle exception
            current_date = date.get_year() + date.get_Month() + date.get_day();
        }

        c.close();

        return current_date;

    }

    public void insertDate(String date) {
        db.execSQL("update study_time set date = '" + date + "' where idx = 1");
    }

    public int[] getTime() {

        int time[] = {0, 0};

        Cursor c = db.rawQuery("select * from study_time where idx = 1", null);
        c.moveToFirst();

        time[0] = c.getInt(c.getColumnIndex("minute"));
        time[1] = c.getInt(c.getColumnIndex("second"));

        c.close();

        return time;
    }

    // 시간 저장
    public void insertTime(int min, int sec) {
        Cursor c = db.rawQuery("select * from study_time where idx = 1", null);
        c.moveToFirst();


        try {
            db.execSQL("update study_time set minute = " + min + ", second = " + sec + " where idx = 1");
        } catch (SQLiteDatabaseLockedException e) {
            // TODO: handle exception
        }
    }

    public int getStudyWord() {
        int study_word = 0;
        try {
            Cursor c = db.rawQuery("select * from study_time", null);
            c.moveToFirst();

            study_word = c.getInt(c.getColumnIndex("study_word"));

            c.close();
        } catch (CursorIndexOutOfBoundsException e) {
            // TODO: handle exception
            Log.e("getStudyWord", "error : " + e);
            study_word = 1;
        }

        return study_word;
    }

    public int getLockStudyWord() {
        int study_word = 0;
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("select * from study_time", null);
            cursor.moveToFirst();

            study_word = cursor.getInt(cursor.getColumnIndex("study_word"));

            cursor.close();
        } catch (CursorIndexOutOfBoundsException e) {
            // TODO: handle exception
            Log.e("getStudyWord", "error : " + e);
            study_word = -1;
        } finally {
            cursor.close();
        }

        return study_word;
    }


    // 학습단어 저장
    public void insertStudyWord(int study_word) {
        db.execSQL("update study_time set study_word = " + study_word + " where idx = 1");
    }

    public int getReviewWord() {
        int review_word = 0;

        Cursor cursor = db.rawQuery("select * from study_time where idx = 1", null);
        cursor.moveToFirst();

        review_word = cursor.getInt(cursor.getColumnIndex("review_word"));

        cursor.close();
        return review_word;
    }

    // 복습단어 저장
    public void insertReviewWord(int review_word) {
        db.execSQL("update study_time set review_word = " + review_word + " where idx = 1");
    }

    public int getStudyLv() {
        int study_lv = 0;

        Cursor c = db.rawQuery("select * from study_time where idx = 1", null);
        c.moveToFirst();

        study_lv = c.getInt(c.getColumnIndex("study_lv"));

        c.close();
        return study_lv;
    }

    // 레벨 저장
    public void insertStudyLv(int study_lv) {
        db.execSQL("update study_time set study_lv = " + study_lv + " where idx = 1");
    }

    // 학습시간, 학습단어, 복습단어 초기화
    public void removeStudyTime() {
        db.execSQL("update study_time set minute = 0, second = 0, study_word = 0, review_word = 0 where idx = 1");
    }

    public void insertGoalTime(int goalTime) {
        Log.e("rrrr", "" + goalTime);
        db.execSQL("update study_time set goal_time = " + goalTime + " where idx = 1");
    }

    public int getGoalTime() {
        int goal_time = 0;
        Cursor c = null;
        try {
            c = db.rawQuery("select goal_time from study_time where idx = 1", null);
            c.moveToFirst();

            goal_time = c.getInt(c.getColumnIndex("goal_time"));

            if (goal_time < 1) {
                goal_time = 10;
                db.execSQL("update study_time set goal_time = 10" + " where idx = 1");
            }
            Log.d("goal_time", goal_time + "");
            c.close();
        } catch (CursorIndexOutOfBoundsException e) {
            // TODO: handle exception
            MildangDate date = new MildangDate();
            String current_date = date.get_year() + date.get_Month() + date.get_day();

            db.execSQL("insert into study_time(date, minute, second, study_word, review_word, study_lv, idx) values('" + current_date
                    + "', 0, 0, 0, 0, 1, 1)");
            goal_time = 10;
        }

        return goal_time;
    }

    public int[] getForgetWords() {
        Cursor cursor = db.rawQuery("SELECT * FROM words WHERE Score > 0 OR forgetting_curves_test_flag=1 ", null);

        int[] word_codes = new int[cursor.getCount()];
        cursor.moveToFirst();
        for (int i = 0; i < word_codes.length; i++) {
            word_codes[i] = cursor.getInt(cursor.getColumnIndex("word_code"));
            cursor.moveToNext();
        }
        cursor.close();

        return word_codes;
    }

    public int[] getForgetWordCountByGrade() {

        Cursor cursor = db.rawQuery("SELECT grade, count(*) AS count FROM words WHERE score > 0 or forgetting_curves_test_flag=1 GROUP BY grade",
                null);

        int[] word_count = {0, 0, 0, 0, 0};

        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            word_count[cursor.getInt(cursor.getColumnIndex("grade")) - 1] = cursor.getInt(cursor.getColumnIndex("count"));
            cursor.moveToNext();
        }
        cursor.close();

        return word_count;
    }

    public String getJoinDate() {
        Cursor c = db.rawQuery("SELECT date FROM calendar_data ORDER By date asc LIMIT 1", null);
        c.moveToFirst();
        String joinDate;
        try {
            joinDate = c.getString(c.getColumnIndex("date"));
            c.close();
        } catch (android.database.CursorIndexOutOfBoundsException e) {
            // TODO: handle exception
            joinDate = new MildangDate().get_today();
        }
        c.close();
        return joinDate;
    }

    public String getLastLoginDate() {

        Cursor c = db.rawQuery("SELECT date FROM calendar_data ORDER By date desc LIMIT 1", null);
        String lastDate;
        try {
            lastDate = c.getString(c.getColumnIndex("date"));
            c.close();
        } catch (android.database.CursorIndexOutOfBoundsException e) {
            // TODO: handle exception
            lastDate = new MildangDate().get_today();
        }
        c.close();
        return lastDate;
    }

    public void insertName(String name) {
        db.execSQL("update user_info set name = " + name + " where rowid = 1");
    }

    public String getName() {

        Cursor c = db.rawQuery("SELECT name FROM user_info where rowid = 1", null);
        c.moveToFirst();
        String name;
        try {
            name = c.getString(c.getColumnIndex("name"));
        } catch (android.database.CursorIndexOutOfBoundsException e) {
            // TODO: handle exception
            name = "이름없음";
        }
        c.close();
        return name;
    }

    public void insertSystemName(String systemName) {
        db.execSQL("update user_info set system_name = " + systemName + " where rowid = 1");
    }

    public String getSystemName() {

        Cursor c = db.rawQuery("SELECT system_name FROM user_info where rowid = 1", null);
        c.moveToFirst();
        String system_name;
        try {
            system_name = c.getString(c.getColumnIndex("system_name"));
        } catch (android.database.CursorIndexOutOfBoundsException e) {
            // TODO: handle exception
            system_name = "AA00000";
        }
        c.close();
        return system_name;
    }

    public void insertLoginId(String loginid) {
        db.execSQL("update user_info set kakao_id = " + loginid + " where rowid = 1");
    }

    public void insertStudentID(String studentid) {
        db.execSQL("update user_info set student_id = " + studentid + " where rowid = 1");
    }

    public String getStudentId() {

        Cursor c = db.rawQuery("SELECT student_id FROM user_info where rowid = 1 AND student_id IS NOT NULL", null);
        c.moveToFirst();
        String student_id;
        try {
            student_id = c.getString(c.getColumnIndex("student_id"));
        } catch (android.database.CursorIndexOutOfBoundsException e) {
            // TODO: handle exception
            student_id = "0";
        }
        c.close();
        return student_id;
    }

    public String getLoginId() {

        Cursor c = db.rawQuery("SELECT kakao_id FROM user_info where rowid = 1", null);
        c.moveToFirst();
        String login_id;
        try {
            login_id = c.getString(c.getColumnIndex("kakao_id"));
        } catch (android.database.CursorIndexOutOfBoundsException e) {
            // TODO: handle exception
            login_id = "0";
        }
        c.close();
        return login_id;
    }

    public void insertWordLevel(int wordLevel) {
        db.execSQL("update user_info set word_level = " + wordLevel + " where rowid = 1");
    }

    public int getWordLevel() {

        Cursor c = db.rawQuery("SELECT word_level FROM user_info where rowid = 1", null);
        c.moveToFirst();
        int word_level;
        try {
            word_level = c.getInt(c.getColumnIndex("word_level"));
        } catch (android.database.CursorIndexOutOfBoundsException e) {
            // TODO: handle exception
            word_level = 1;
        }
        c.close();
        return word_level;
    }

    public void insertTutorial(boolean tutorial) {
        int value;
        if (tutorial)
            value = 1;
        else
            value = 0;
        db.execSQL("update user_info set tutorial = " + value + " where rowid = 1");
    }

    public boolean getTutorial() {

        Cursor c = db.rawQuery("SELECT tutorial FROM user_info where rowid = 1", null);
        c.moveToFirst();
        int tutorial;
        try {
            tutorial = c.getInt(c.getColumnIndex("tutorial"));
            Log.d("tutorialDB", tutorial + "try");
        } catch (android.database.CursorIndexOutOfBoundsException e) {
            // TODO: handle exception
            tutorial = 1;
            Log.d("tutorialDB", tutorial + "catch" + e.toString());
        }
        c.close();

        if (tutorial == 1)
            return true;
        else
            return false;
    }

    public void insertTutorialWill(boolean tutorial) {
        int value;
        if (tutorial)
            value = 1;
        else
            value = 0;
        db.execSQL("update user_info set tutorial_will = " + value + " where rowid = 1");
    }

    public boolean getTutorialWill() {

        Cursor c = db.rawQuery("SELECT tutorial_will FROM user_info where rowid = 1", null);
        c.moveToFirst();
        int tutorial;
        try {
            tutorial = c.getInt(c.getColumnIndex("tutorial_will"));
        } catch (android.database.CursorIndexOutOfBoundsException e) {
            // TODO: handle exception
            tutorial = 1;
        }
        c.close();

        if (tutorial == 1)
            return true;
        else
            return false;
    }

    public void insertTutorialMemory(boolean tutorial) {
        int value;
        if (tutorial)
            value = 1;
        else
            value = 0;
        db.execSQL("update user_info set tutorial_memory = " + value + " where rowid = 1");
    }

    public boolean getTutorialMemory() {

        Cursor c = db.rawQuery("SELECT tutorial_memory FROM user_info where rowid = 1", null);
        c.moveToFirst();
        int tutorial;
        try {
            tutorial = c.getInt(c.getColumnIndex("tutorial_memory"));
        } catch (android.database.CursorIndexOutOfBoundsException e) {
            // TODO: handle exception
            tutorial = 1;
        }
        c.close();

        if (tutorial == 1)
            return true;
        else
            return false;
    }

    public void insertTutorialHit(boolean tutorial) {
        int value;
        if (tutorial)
            value = 1;
        else
            value = 0;
        db.execSQL("update user_info set tutorial_hit = " + value + " where rowid = 1");
    }

    public boolean getTutorialHit() {

        Cursor c = db.rawQuery("SELECT tutorial_hit FROM user_info where rowid = 1", null);
        c.moveToFirst();
        int tutorial;
        try {
            tutorial = c.getInt(c.getColumnIndex("tutorial_hit"));
        } catch (android.database.CursorIndexOutOfBoundsException e) {
            // TODO: handle exception
            tutorial = 1;
        }
        c.close();

        if (tutorial == 1)
            return true;
        else
            return false;
    }

    public int getCalendarDataCount() {
        Cursor c = db.rawQuery("SELECT COUNT(date) as count FROM calendar_data", null);
        int count = c.getCount();
        c.close();

        return count;
    }

    public void LevelUp_Word() {
        db.execSQL("update user_info set word_level = word_level+1 where rowid = 1");
    }

    public boolean exportForgettingCurvesTable() {
        File exportDir = new File(Config.DB_FILE_DIR, "");

        if (!exportDir.exists()) {
            exportDir.mkdirs();
        }

        File file = new File(exportDir, "forgetting_curves.csv");
        try {
            if (!file.exists()) {
                file.createNewFile();
            }

            CSVWriter csvWrite = new CSVWriter(new FileWriter(file));
            Cursor curCSV = db.rawQuery("SELECT * FROM forgetting_curves", null);
            csvWrite.writeNext(curCSV.getColumnNames());
            while (curCSV.moveToNext()) {
                String arrStr[] = {curCSV.getString(0), curCSV.getString(1), curCSV.getString(2), curCSV.getString(3), curCSV.getString(4)};
                // curCSV.getString(3),curCSV.getString(4)};
                csvWrite.writeNext(arrStr);
            }
            csvWrite.close();
            curCSV.close();
            return true;
        } catch (SQLException sqlEx) {
            Log.e("Export_Table", sqlEx.getMessage(), sqlEx);
            return false;
        } catch (IOException e) {
            Log.e("Export_Table", e.getMessage(), e);
            return false;
        }
    }

    public boolean exportWordsTable() {
        File exportDir = new File(Config.DB_FILE_DIR, "");

        if (!exportDir.exists()) {
            exportDir.mkdirs();
        }

        File file = new File(exportDir, "words.csv");
        try {
            CSVWriter csvWrite = new CSVWriter(new FileWriter(file, true));
            Cursor curCSV = db.rawQuery("SELECT word_code, Score, State, Time, Frequency FROM words WHERE State IS NOT 0", null);
            csvWrite.writeNext(curCSV.getColumnNames());
            while (curCSV.moveToNext()) {
                String arrStr[] = {curCSV.getString(0), curCSV.getString(1), curCSV.getString(2), curCSV.getString(3), curCSV.getString(4)};
                // curCSV.getString(3),curCSV.getString(4)};
                csvWrite.writeNext(arrStr);
            }
            csvWrite.close();
            curCSV.close();
            return true;
        } catch (SQLException sqlEx) {
            Log.e("Export_Table", sqlEx.getMessage(), sqlEx);
            return false;
        } catch (IOException e) {
            Log.e("Export_Table", e.getMessage(), e);
            return false;
        }
    }

    public void initailizeInfo() {
        db.execSQL("UPDATE user_info SET name = '이름없음', kakao_id = '0',student_id = '0', system_name = 'AA00000', tutorial = 0, tutorial_will = 0, tutorial_hit = 0, tutorial_memory = 0, word_level = 1 WHERE rowid = 1");
    }

    public String exportForgettingCurvesTableToJson() {
        Cursor cursor = null;
        JsonArray myCustomArray = new JsonArray();
        String temp = "";
        int i = 0;
        try {
            cursor = db.rawQuery("SELECT * FROM forgetting_curves", null);
            int state_idx = cursor.getColumnIndex("state");
            int time_idx = cursor.getColumnIndex("time");
            int probability_idx = cursor.getColumnIndex("probability");
            int frequency_idx = cursor.getColumnIndex("frequency");
            int count = 0;
            count = cursor.getCount();

            if (count != 0) {
                cursor.moveToFirst();

                do {
                    JsonObject json = new JsonObject();
                    json.addProperty("state", cursor.getString(state_idx));
                    json.addProperty("time", cursor.getString(time_idx));
                    json.addProperty("probability", cursor.getString(probability_idx));
                    json.addProperty("frequency", cursor.getString(frequency_idx));

                    myCustomArray.add(json);
                    i++;
                } while (cursor.moveToNext());
                Log.e("forgetting_logs", "" + i);


            } else {
                JsonObject json = new JsonObject();
                json.addProperty("state", 0);
                json.addProperty("time", 0);
                json.addProperty("probability", 0);
                json.addProperty("frequency", 0);

                myCustomArray.add(json);
            }
        } catch (Exception e) {

        } finally {

            if (cursor != null) {
                cursor.close();
            }
        }
        if (myCustomArray.size() <= 24) {
            myCustomArray = new JsonArray();
            JsonObject json = new JsonObject();
            json.addProperty("state", 0);
            json.addProperty("time", 0);
            json.addProperty("probability", 0);
            json.addProperty("frequency", 0);

            myCustomArray.add(json);
        }
        temp = myCustomArray.toString();
        return temp;

    }

    public String[] exportWordsTableToJson() {
        Cursor cursor = null;
        JsonArray myCustomArray = new JsonArray();
        String temp[] = new String[2];
        try {
            cursor = db.rawQuery("SELECT word_code, Score, State, Time, Frequency FROM words WHERE State IS NOT 0", null);
            int word_code_idx = cursor.getColumnIndex("word_code");
            int score_idx = cursor.getColumnIndex("Score");
            int state_idx = cursor.getColumnIndex("State");
            int time_idx = cursor.getColumnIndex("Time");
            int frequency_idx = cursor.getColumnIndex("Frequency");
            int count = 0;
            count = cursor.getCount();

            if (count != 0) {
                cursor.moveToFirst();

                do {
                    JsonObject json = new JsonObject();

                    json.addProperty("word_code", cursor.getString(word_code_idx));
                    json.addProperty("score", cursor.getString(score_idx));
                    json.addProperty("state", cursor.getString(state_idx));
                    json.addProperty("time", cursor.getString(time_idx));
                    json.addProperty("frequency", cursor.getString(frequency_idx));

                    myCustomArray.add(json);

                } while (cursor.moveToNext());

                if (count == myCustomArray.size()) {
                    temp[1] = "1";
                } else {
                    temp[1] = "-1";
                }

            } else {
                JsonObject json = new JsonObject();

                json.addProperty("word_code", 0);
                json.addProperty("score", 0);
                json.addProperty("state", 0);
                json.addProperty("time", 0);
                json.addProperty("frequency", 0);

                myCustomArray.add(json);
                temp[1] = "0";
            }
        } catch (Exception e) {

        } finally {

            if (cursor != null) {
                cursor.close();
            }
        }

        temp[0] = myCustomArray.toString();

        return temp;

    }

    public String[] exportCalendarDataTableToJson() {
        Cursor cursor = null;
        JsonArray myCustomArray = new JsonArray();
        String temp[] = new String[2];
        try {
            cursor = db.rawQuery("SELECT date, study_time, goal_time, new_count, review_count, will_review_count FROM calendar_data", null);
            int date_idx = cursor.getColumnIndex("date");
            int study_time_idx = cursor.getColumnIndex("study_time");
            int goal_time_idx = cursor.getColumnIndex("goal_time");
            int new_count_idx = cursor.getColumnIndex("new_count");
            int review_count_idx = cursor.getColumnIndex("review_count");
            int will_review_count_idx = cursor.getColumnIndex("will_review_count");
            int count = 0;
            count = cursor.getCount();

            if (count != 0) {
                cursor.moveToFirst();
                String check_date = null;
                do {
                    JsonObject json = new JsonObject();
                    if (cursor.isLast()) {
                        check_date = cursor.getString(date_idx);
                    }
                    json.addProperty("date", cursor.getString(date_idx));
                    json.addProperty("study_time", cursor.getString(study_time_idx));
                    json.addProperty("goal_time", cursor.getString(goal_time_idx));
                    json.addProperty("new_count", cursor.getString(new_count_idx));
                    json.addProperty("review_count", cursor.getString(review_count_idx));
                    json.addProperty("will_review_count", cursor.getString(will_review_count_idx));

                    myCustomArray.add(json);

                } while (cursor.moveToNext());

                MildangDate date = new MildangDate();
                JsonObject json2 = new JsonObject();

                if (!check_date.equals(date.get_today())) {

                    json2.addProperty("date", date.get_today());
                    json2.addProperty("study_time", getTime()[0]);
                    json2.addProperty("goal_time", getGoalTime());
                    json2.addProperty("new_count", getStudyWord());
                    json2.addProperty("review_count", getReviewWord());
                    json2.addProperty("will_review_count", getMforget() + getReviewWord());
                    myCustomArray.add(json2);

                }


                if (count == myCustomArray.size()) {
                    temp[1] = "1";
                } else {
                    temp[1] = "-1";
                }

            } else {

                MildangDate date = new MildangDate();
                JsonObject json2 = new JsonObject();

                json2.addProperty("date", date.get_today());
                json2.addProperty("study_time", getTime()[0]);
                json2.addProperty("goal_time", getGoalTime());
                json2.addProperty("new_count", getStudyWord());
                json2.addProperty("review_count", getReviewWord());
                json2.addProperty("will_review_count", getMforget() + getReviewWord());

                myCustomArray.add(json2);
                temp[1] = "0";
            }
        } catch (Exception e) {
            JsonObject json = new JsonObject();

            json.addProperty("date", 0);
            json.addProperty("study_time", 0);
            json.addProperty("goal_time", 0);
            json.addProperty("new_count", 0);
            json.addProperty("review_count", 0);
            json.addProperty("will_review_count", 0);

            myCustomArray.add(json);


        } finally {

            if (cursor != null) {
                cursor.close();
            }
        }

        if (myCustomArray.size() > 2000) {
            myCustomArray = new JsonArray();
            JsonObject json = new JsonObject();

            json.addProperty("date", 0);
            json.addProperty("study_time", 0);
            json.addProperty("goal_time", 0);
            json.addProperty("new_count", 0);
            json.addProperty("review_count", 0);
            json.addProperty("will_review_count", 0);
            myCustomArray.add(json);

        }

        temp[0] = myCustomArray.toString();

        return temp;

    }

    public void createLocation(double Longitude, double Latitude) {
        db.execSQL("CREATE TABLE IF NOT EXISTS location (" + "Longitude VARCHAR DEFAULT '0', Latitude VARCHAR DEFAULT '0')");

        db.execSQL("DELETE FROM location ");

        db.execSQL("insert into location(Longitude, Latitude) values (" + Longitude + "," + Latitude + ")");

    }

    public String[] getLocation() {

        String[] location = {"0", "0"};

        try {
            Cursor cursor = db.rawQuery("SELECT * FROM location", null);
            cursor.moveToFirst();

            if (cursor.getCount() != 0) {
                location[0] = "" + cursor.getString(cursor.getColumnIndex("Longitude"));
                location[1] = "" + cursor.getString(cursor.getColumnIndex("Latitude"));
            }
        } catch (SQLiteException e) {
            // TODO: handle exception
        }

        return location;

    }

    public void makePushTable() {
        db.execSQL("CREATE TABLE IF NOT EXISTS push (" + "type INTEGER DEFAULT 0, isOpened INTEGER DEFAULT 0)");
    }

    public void setPushTable(int type, int isOpened) {
        try {
            db.execSQL("INSERT OR REPLACE "
                    + "INTO push(rowid,type, isOpened) "
                    + "values ((SELECT rowid FROM push WHERE type = " + type + "),"
                    + " " + type + ", " + isOpened + ")");
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    public boolean getPushTable(int type) {
        boolean isOpened = false;
        try {
            Cursor cursor = db.rawQuery("SELECT isOpened FROM push WHERE type = " + type, null);

            cursor.moveToFirst();

            int opened = cursor.getInt(cursor.getColumnIndex("isOpened"));

            if (opened == 1) {
                isOpened = true;
            }
        } catch (SQLiteException e) {

        } finally {
            return isOpened;
        }
    }

    public void deletePushTable() {
        db.execSQL("DELETE FROM push");
    }

    public void makeFreeTable() {
        db.execSQL("CREATE TABLE IF NOT EXISTS fdate (" + "type VARCHAR DEFAULT 'false', Update_Date VARCHAR DEFAULT '00/00/00')");

    }

    public void setFreeTable(String type, String Update_Date) {
        try {
            db.execSQL("INSERT OR REPLACE "
                    + "INTO fdate(rowid, type, Update_Date) "
                    + "values ('1',"
                    + " '" + type + "', '" + Update_Date + "')");
        } catch (Exception e) {
            // TODO: handle exception
            Log.d("isGotFreeDate", "" + e.toString());
        }
    }

    public String[] getFreeTable() {
        boolean isOpened = false;
        String[] data = {"00/00/00", "false"};

        try {
            Cursor cursor = db.rawQuery("SELECT * FROM fdate", null);
            cursor.moveToFirst();
            data[0] = cursor.getString(cursor.getColumnIndex("Update_Date"));
            data[1] = cursor.getString(cursor.getColumnIndex("type"));

        } catch (SQLiteException e) {

        } finally {
            return data;
        }

    }

    public void wordModify() {
        // 2015.05.19 smack, ravel 단어 동작하지 않는 문제 수정
        db.execSQL("UPDATE words SET p_wordclass = 2 WHERE (word_code = 5715 OR word_code = 12164) AND p_wordclass = 1");
        // 2015.05.19 5개단어 뜻 품사가 맞지 않는 문제 수정
        db.execSQL("UPDATE means SET Class = 4 WHERE Mean_Code = 25872 AND Class = 2");
        db.execSQL("UPDATE means SET Class = 2 WHERE Mean_Code = 20616 AND Class = 1");
        db.execSQL("UPDATE means SET Class = 1 WHERE Mean_Code = 5499 AND Class = 3");
        db.execSQL("UPDATE means SET Class = 2 WHERE Mean_Code = 12034 AND Class = 3");
        db.execSQL("UPDATE means SET Class = 3 WHERE Mean_Code = 31828 AND Class = 2");
    }

    public void makeLockScreenTable() {
        db.execSQL("CREATE TABLE IF NOT EXISTS setting (" + "sort VARCHAR, isOn VARCHAR DEFAULT '0')");
    }

    public void injectionStudentIdColumn() {
        try {
            db.execSQL("ALTER TABLE user_info ADD COLUMN student_id VARCHAR(20)");
        } catch (SQLException e) {
            // TODO: handle exception
        }
    }

    public void setLockScreenTable(String isOn) {
        try {
            db.execSQL("INSERT OR REPLACE "
                    + "INTO setting(rowid,sort, isOn) "
                    + "values ((SELECT rowid FROM setting WHERE sort = 'lockscreen'),"
                    + " 'lockscreen', " + isOn + ")");

            if (isOn.equals("1")) {
                MainValue.LockScreenOn = "true";
            } else {
                MainValue.LockScreenOn = "false";
            }

        } catch (Exception e) {
            // TODO: handle exception
            db.execSQL("CREATE TABLE IF NOT EXISTS setting (" + "sort VARCHAR, isOn VARCHAR DEFAULT '0')");

        }
    }

    public boolean getLockScreenTable() {
        boolean isOn = false;
        try {
            Cursor cursor = db.rawQuery("SELECT isOn FROM setting WHERE sort = 'lockscreen'", null);

            cursor.moveToFirst();

            int On = cursor.getInt(cursor.getColumnIndex("isOn"));

            if (On == 1) {
                isOn = true;
            }
        } catch (SQLiteException e) {
            db.execSQL("CREATE TABLE IF NOT EXISTS setting (" + "sort VARCHAR, isOn VARCHAR DEFAULT '0')");
        } finally {

            if (isOn) {
                MainValue.LockScreenOn = "true";
            } else {
                MainValue.LockScreenOn = "false";
            }

            return isOn;
        }
    }

    public int updateWordtbl(Word word) {

        ContentValues values = new ContentValues();
        values.put("State", word.getState());
        values.put("Frequency", word.getFrequency());
        values.put("Time", word.getTime());

        int result = db.update("words", values, "word_code = ?", new String[]{"" + word.get_id()});
        return result;
    }

    public int updateForgetting_Curve_tbl(ForgettingCurve fc) {
        int result = 0;
        if (fc.getState() < 100) {
            Cursor cursor = db.rawQuery("INSERT OR REPLACE into forgetting_curves(id, state, time, probability, frequency) "
                            + "values((SELECT id FROM forgetting_curves WHERE state = " + fc.getState() + " AND time = "
                            + fc.getTime() + ")," + fc.getState() + ", " + fc.getTime() + ", " + fc.getProbability() + ", " + fc.getFrequency() + ")",
                    new String[]{});

            cursor.moveToFirst();
            Log.e("userdb", "" + cursor.getCount() + "   " + cursor.getColumnIndex("time"));
            result = cursor.getCount();
            cursor.close();
        } else {
            result = 1;
        }

        return result;
    }


    public int updateCalendar_tbl(Calendar calendar) {
        Cursor cursor = db.rawQuery("INSERT OR REPLACE into calendar_data(rowid, date, study_time, goal_time, new_count, review_count, will_review_count) "
                        + "values((SELECT rowid FROM calendar_data WHERE date = " + calendar.getDate() + "),"
                        + calendar.getCalendar_date() + ", " + calendar.getStudy_time() + ", " + calendar.getGoal_time() + ", " + calendar.getNew_count() + ", " + calendar.getReview_count()
                        + ", " + calendar.getWill_review_count() + ")",
                new String[]{});

        cursor.moveToFirst();
        Log.e("userdb", "" + cursor.getCount() + "   " + cursor.getColumnIndex("date"));
        int result = cursor.getCount();
        cursor.close();
        return result;
    }


    public boolean updateword(String[] line) {

        Cursor cursor = db.rawQuery("SELECT * FROM words WHERE word_code = '" + line[0] + "'", null);
        cursor.moveToFirst();
        Log.e("cvsread", "" + cursor.getCount() + "    code" + line[0]);

        if (cursor.getCount() > 0) {
            //업데이트
            cursor.close();

            ContentValues values = new ContentValues();
            values.put("word", line[1]);
            values.put("p_wordclass", line[2]);
            values.put("difficulty", line[3]);
            values.put("grade", line[3]);

            return db.update("words", values, "word_code = " + line[0], null) >= 0; // update
        } else {
            //입력
            cursor.close();
            ContentValues values = new ContentValues();
            values.put("word_code", line[0]);
            values.put("word", line[1]);
            values.put("p_wordclass", line[2]);
            values.put("difficulty", line[3]);
            values.put("Score", "" + 0.0);
            values.put("State", "" + 0);
            values.put("Time", "" + 0);
            values.put("Frequency", "" + 0);
            values.put("grade", line[3]);
            values.put("forgetting_curves_test_flag", "" + 0);
            return db.insert("words", null, values) >= 0;

        }
    }

    public boolean updateMean(String[] line) {

        Cursor cursor = db.rawQuery("SELECT * FROM means WHERE Mean_Code = '" + line[0] + "'", null);
        cursor.moveToFirst();
        Log.e("cvsread", "" + cursor.getCount() + "    code" + line[0]);

        //입력
        if (!(cursor.getCount() > 0)) {
            ContentValues values = new ContentValues();
            values.put("Mean_Code", line[0]);
            values.put("Word_Code", line[1]);
            values.put("Class", line[2]);
            values.put("Mean", line[3]);
            values.put("M_Priority", line[4]);
            cursor.close();
            return db.insert("means", null, values) >= 0;
        } else {
            cursor.close();
            return false;
        }

    }


    public boolean maketbl_Wrongrate() {
        boolean isExist = false;
        String colCheck = "PRAGMA table_info(words)";
        Cursor cursor = db.rawQuery(colCheck, null);
        cursor.moveToFirst();
        while (cursor.moveToNext()) {
            if (cursor.getString(cursor.getColumnIndex("name")).equals("wrong_rate")) {
                isExist = true;
                break;
            }
        }

        if (!isExist) {
            db.execSQL("Alter Table words ADD COLUMN wrong_rate varchar(10)");
        }
        return isExist;
    }

    public boolean check_Wrongrate() {
        boolean isExist = false;
        try {
            String colCheck = "SELECT * FROM words WHERE wrong_rate IS NOT NULL";
            Cursor cursor = db.rawQuery(colCheck, null);
            cursor.moveToFirst();


            if (cursor.getCount() > 0) {
                isExist = true;
            }
        } catch (Exception e) {

        } finally {
            return isExist;
        }
    }

    public boolean update_wrongrate(String[] line) {

        ContentValues values = new ContentValues();
        values.put("wrong_rate", line[1]);

        return db.update("words", values, "word_code = " + line[0], null) >= 0; // update
    }


    public void deleteWord(int Word_Code) {
        db.execSQL("DELETE FROM words WHERE Word_Code = " + Word_Code);
        db.execSQL("DELETE FROM means WHERE Word_Code = " + Word_Code);
    }

    public void otaUpdate(int type, int code, String text) {

        ContentValues values = new ContentValues();
        if (type == 1) {
            values.put("word", text);
            db.update("words", values, "word_code = " + code, null);
        } else if (type == 2) {
            values.put("Mean", text);
            db.update("means", values, "Mean_Code = " + code, null);
        }

    }


	/*
	 * added karam function 2015.08.07 3.5.1
	 */

    // KARAM
    public ArrayList<Word> WordArrayOneSet(Activity activity) {
        SharedPreferences settings;
        settings = activity.getSharedPreferences(Config.PREFS_NAME, Context.MODE_WORLD_READABLE | Context.MODE_WORLD_WRITEABLE);

        ArrayList<Word> mOneSetWordArray = new ArrayList<Word>();

        mOneSetWordArray = getCurrentWords();


        if (mOneSetWordArray.size() > 0) {
            for (int i = 0; i < mOneSetWordArray.size(); i++) {
                if (mOneSetWordArray.get(i).getExState() > 0) {
                    String value = settings.getString(MainActivitys.GpreReviewTutorial, "0");

                    if (value.equals("0")) {
                        if (settings.getBoolean(MainValue.GpreReviewFlag, true) == true)
                            activity.startActivity(new Intent(activity, ReviewTutorialActivity.class));
                    }
                    break;
                }
            }
        } else {
            mOneSetWordArray = wordsWithScore();
            mOneSetWordArray.addAll(wordsWithUnknown());

            LinkedHashSet<Word> linkedhashset = new LinkedHashSet<Word>();
            linkedhashset.addAll(mOneSetWordArray);
            mOneSetWordArray.clear();
            mOneSetWordArray.addAll(linkedhashset);

            int level = Config.Difficulty;

            int wordsCount = mOneSetWordArray.size();
            int remainderCount = Config.ONCE_WORD_COUNT - wordsCount;

            if (remainderCount > 0) {
                int restCount = remainderCount;
                ArrayList<Word> temp;
                int tempCount;

                do {
                    temp = wordsWithLevel(level, restCount);
                    tempCount = temp.size();
                    mOneSetWordArray.addAll(temp);
                    restCount -= tempCount;

                    if (restCount > 0) {
                        if (level < Config.MAX_DIFFICULTY) {
                            new AlertDialog.Builder(activity)
                                    .setMessage("해당 등급의 단어를 모두 외우셨습니다. 다음 등급으로 넘어갑니다.")
                                    .setPositiveButton("확인", null)
                                    .show();

                            insertTrueCount(level);
                            level++;
                            Config.Difficulty = level;
                            insertWordLevel(Config.Difficulty);
                        } else break;
                    } else break;
                } while (restCount > 0);
            }

            while (mOneSetWordArray.size() > Config.ONCE_WORD_COUNT)
                mOneSetWordArray.remove(mOneSetWordArray.size() - 1);


            int i = 1;
            for (Word word : mOneSetWordArray) {
                insertCurrentWord(word, i);
                mOneSetWordArray.get(i - 1).setExState(mOneSetWordArray.get(i - 1).getState());
                i++;
            }
            mOneSetWordArray.clear();
            mOneSetWordArray = getCurrentWords();

        }
        return sortWord(mOneSetWordArray);
    }

    public ArrayList<Word> sortWord(ArrayList<Word> origin_list) {
        ArrayList<Word> sorted_list = new ArrayList<Word>();
        try {
            for (int i = 0; i < origin_list.size(); i++) {
                if (origin_list.get(i).getExState() > 0) {
                    Log.e("sorted_list", "i   :   " + origin_list.get(i).getWord());
                    sorted_list.add(origin_list.get(i));
                    origin_list.remove(i);
                    i--;

                }
            }

            for (int i = 0; i < origin_list.size(); i++) {
                if (origin_list.get(i).getExState() == -1) {
                    Log.e("sorted_list", "i   :   " + origin_list.get(i).getWord());
                    sorted_list.add(origin_list.get(i));
                    origin_list.remove(i);
                    i--;
                }
            }
            sorted_list.addAll(origin_list);
        } catch (Exception e) {
            // TODO: handle exception
            Log.e("sorted_list", "" + e.toString());
        }

        return sorted_list;
    }

    /**
     * added karam function 2015.08.014 3.5.1
     */

    // KARAM
    public ArrayList<Word> WordArrayOneSet_LockScreen(Activity activity) {
        SharedPreferences settings;
        settings = activity.getSharedPreferences(Config.PREFS_NAME, Context.MODE_WORLD_READABLE | Context.MODE_WORLD_WRITEABLE);

        ArrayList<Word> mOneSetWordArray = new ArrayList<Word>();

        mOneSetWordArray = getCurrentWords_LockScreen();


        if (mOneSetWordArray.size() > 0) {
            for (int i = 0; i < mOneSetWordArray.size(); i++) {
                if (mOneSetWordArray.get(i).getExState() > 0) {
                    String value = settings.getString(MainActivitys.GpreReviewTutorial, "0");

                    if (value.equals("0")) {
                    }
                    break;
                }
            }
        } else {
            mOneSetWordArray = wordsWithScoreInMPopup();
            mOneSetWordArray.addAll(wordsWithUnknown_LockScreen());

            LinkedHashSet<Word> linkedhashset = new LinkedHashSet<Word>();
            linkedhashset.addAll(mOneSetWordArray);
            mOneSetWordArray.clear();
            mOneSetWordArray.addAll(linkedhashset);

            int level = getWordLevel();

            int wordsCount = mOneSetWordArray.size();
            int remainderCount = Config.ONCE_WORD_COUNT - wordsCount;

            if (remainderCount > 0) {
                int restCount = remainderCount;
                ArrayList<Word> temp;
                int tempCount;

                do {
                    temp = wordsWithLevel(level, restCount);
                    tempCount = temp.size();
                    mOneSetWordArray.addAll(temp);
                    restCount -= tempCount;

                    if (restCount > 0) {
                        if (level < Config.MAX_DIFFICULTY) {
                            new AlertDialog.Builder(activity)
                                    .setMessage("해당 등급의 단어를 모두 외우셨습니다. 다음 등급으로 넘어갑니다.")
                                    .setPositiveButton("확인", null)
                                    .show();

                            insertTrueCount(level);
                            level++;
                            Config.Difficulty = level;
                            insertWordLevel(Config.Difficulty);
                        } else break;
                    } else break;
                } while (restCount > 0);
            }

            while (mOneSetWordArray.size() > Config.ONCE_WORD_COUNT)
                mOneSetWordArray.remove(mOneSetWordArray.size() - 1);


            Log.e("KARAM", "DBPool Size : " + mOneSetWordArray.size());

            int i = 1;
            for (Word word : mOneSetWordArray) {
                insertCurrentWord(word, i);
                mOneSetWordArray.get(i - 1).setExState(mOneSetWordArray.get(i - 1).getState());
                i++;
            }
            mOneSetWordArray.clear();
            mOneSetWordArray = getCurrentWords_LockScreen();

        }
        return sortWord(mOneSetWordArray);
    }

    public String getSpecialRate(int wordcode) {
        String mWrongRate = null;

        try {
            Cursor cursor = db.rawQuery("Select Wrong_rate FROM words WHERE Word_Code = " + wordcode, null);

            cursor.moveToFirst();


            mWrongRate = cursor.getString(0);
        } catch (Exception e) {

        } finally {
            return mWrongRate;
        }
    }


    public void makeLockinfoTable(double during, int word_count, int info_type, int finish_type) {
        Date date = new Date();
        Log.e("checkdate", "" + date.get_year() + " " + date.get_Month() + " " + date.get_day()
                + " " + date.get_hour() + " " + date.get_minute() + " " + date.get_second());

        db.execSQL("CREATE TABLE IF NOT EXISTS lockinfo ("
                + "user_id INTEGER, during INTEGER, word_count INTEGER, info_type INTEGER, "
                + "finish_type INTEGER, sqltime TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL)");

        ContentValues values = new ContentValues();

        values.put("user_id", getStudentId());
        values.put("during", during);
        values.put("word_count", word_count);
        values.put("info_type", info_type);
        values.put("finish_type", finish_type);
        values.put("sqltime", date.get_year() + "-" + date.get_Month() + "-" + date.get_day()
                + " " + date.get_hour() + ":" + date.get_minute() + ":" + date.get_second());

        db.insert("lockinfo", null, values);
    }

    public ArrayList<LockInfo> getLockInfo() {
        ArrayList<LockInfo> list = new ArrayList<LockInfo>();
        list.clear();
        Cursor cursor = db.rawQuery("SELECT * FROM lockinfo", null);

        cursor.moveToFirst();

        if (cursor.getCount() > 0) {
            do {
                LockInfo info = new LockInfo(getStudentId(), cursor.getInt(cursor.getColumnIndex("during")),
                        cursor.getInt(cursor.getColumnIndex("word_count")), cursor.getInt(cursor.getColumnIndex("info_type")),
                        cursor.getInt(cursor.getColumnIndex("finish_type")), cursor.getString(cursor.getColumnIndex("sqltime")));
                list.add(info);
            } while (cursor.moveToNext());
        }
        cursor.close();

        return list;
    }

    public boolean deleteLockInfo() {
        db.execSQL("DELETE FROM lockinfo");
        return true;
    }

    public void createWordLogTbl() {
        try {
            db.execSQL("CREATE TABLE word_log("
                    + "word_code INTEGER( 10 ) ,"
                    + "location INTEGER( 5 ) ,"
                    + "pre_state VARCHAR( 5 ) DEFAULT  '0',"
                    + "after_state VARCHAR( 5 )  DEFAULT  '1',"
                    + "isUpload int( 5 )  DEFAULT  '0',"
                    + "reg_date VARCHAR( 20 ) DEFAULT '2015-01-01 01:01:00')");

            db.execSQL("CREATE INDEX wl_word_code ON word_log (word_code)");
            db.execSQL("CREATE INDEX wl_location ON word_log (location)");
            db.execSQL("CREATE INDEX wl_pre_state ON word_log (pre_state)");
            db.execSQL("CREATE INDEX wl_after_state ON word_log (after_state)");
            db.execSQL("CREATE INDEX wl_reg_date ON word_log (reg_date)");
        } catch (Exception e) {
            Log.e("word_log", "Exception : " + e.toString());
            try {

                db.execSQL("ALTER TABLE word_log ADD COLUMN location INTEGER DEFAULT '1'");
            } catch (Exception exception) {

            }
            try {
                db.execSQL("ALTER TABLE word_log ADD COLUMN isUpload INTEGER DEFAULT '0'");
            } catch (Exception exception) {

            }

        }


    }

    public void insertWordLog(int word_code, int location, int pre_state, int after_state) {

        Date date = new Date();

        try {
            db.execSQL("insert into word_log(word_code, location, pre_state, after_state, reg_date) "
                    + "values (" + word_code + ", " + location + ", " + pre_state + ", " + after_state + ", '" + date.get_currentTime() + "')");
        } catch (Exception e) {
            // TODO: handle exception
            createWordLogTbl();
            db.execSQL("insert into word_log(word_code, location, pre_state, after_state, reg_date) "
                    + "values (" + word_code + ", " + location + ", " + pre_state + ", " + after_state + ", '" + date.get_currentTime() + "')");
        }
    }

    public void insertKnownWord(List<SampleTextPopup.LocationTextview> sentence_split, Context context) {
        db.beginTransaction();

        MildangDate mildangDate = new MildangDate();
        int insertstudyWord = 0;
        int insertReviewWord = 0;
        String isMaxState = "";
        String insertReviewTimeToGetMemoryCoachMent = " INSERT OR REPLACE INTO remember_history(state, time, date) " + " VALUES";
        String insertLevel = "insert into level(difficulty, isKnown) values ";
        int GpreCheckCurve = 0;
        String deleteCurrentWord = "";

        SharedPreferences sharedPreferences = context.getSharedPreferences(Config.PREFS_NAME,
                context.MODE_WORLD_READABLE | context.MODE_WORLD_WRITEABLE
                        | context.MODE_MULTI_PROCESS);


        for (int i = 0; i < sentence_split.size(); i++) {
            try {
                if (!sentence_split.get(i).getSelect()) {
                    Log.e("check_time", "i : " + i);
                    Word word = getWord(sentence_split.get(i).getWord_id());
                    int ex_state = word.getState();

                    switch (ex_state) {
                        case -1: // 몰랐다가 알게된 경우    ? -> 0
                            //새로학습한단어
                            insertStudyWord(getStudyWord() + 1);
                            insertstudyWord++;
                            break;
                        case 0: // 처음 보는 단어
                            switch (ex_state) {
                                case -1:
                                    // 처음 본 모르는 단어를 외웠을 때 X -> ? -> 0
                                    //새로학습한단어

                                    insertStudyWord(getStudyWord() + 1);
                                    insertstudyWord++;
                                    Config.new_to_unknw_to_knw++;
                                    break;
                                default: // case 0
                                    // 원래 아는 던어 X -> 0
                                    //새로학습한단어

                                    insertStudyWord(getStudyWord() + 1);
                                    insertstudyWord++;
                                    Config.new_to_knw++;
                                    break;
                            }
                            break;
                        default: // 아는 단어
                            switch (ex_state) {
                                case -1:
                                    // 외웠다가 까먹은 단어 ! -> ? -> 0
                                    //새로학습한단어

                                    insertStudyWord(getStudyWord() + 1);
                                    insertstudyWord++;
                                    Config.knw_to_unknw_to_knw++;
                                    break;
                                default:
                                    // 까먹지 않은 단어 ! -> 0
                                    // 기억이 연장된단어

                                    insertReviewWord(getReviewWord() + 1);
                                    insertReviewWord++;
                                    Config.knw_to_knw++;
                                    break;
                            }
                            break;
                    }

                    Log.e("check_time", "1 : " + i);

                    isMaxState(word.getState());
                    Log.e("check_time", "3 : " + i);

                    Log.e("check_time", "4 : " + i);
                    updateForgettingCurvesByNewInputs(word, Config.OFFLINELESSON, true);

                    Log.e("check_time", "5 : " + i);
                    if (word.getState() > 0) {
                        insertReviewTimeToGetMemoryCoachMent += "(" + word.getState() + ", " + word.getTime(0) + ", "
                                + mildangDate.get_today() + "),";
                    }

                    Log.e("check_time", "6 : " + i);
                    insertLevel += "(" + word.getDifficulty() + ", '" + true + "'),";

                    Word word_for_write = getWord(word.get_id());
                    word.setState(word_for_write.getState());

                    if (ex_state > 0) {
                        GpreCheckCurve++;
                    }

                    deleteCurrentWord(word.get_id());

                    Log.e("check_time", "7 : " + i);

                    if (ex_state > 0) {
                        sharedPreferences.edit().putInt(MainValue.GpreCheckCurve, sharedPreferences.getInt(MainValue.GpreCheckCurve, 0) + GpreCheckCurve).commit();
                    }

                }
            } catch (NullPointerException e) {

            }


        }



        try{
            db.execSQL(insertReviewTimeToGetMemoryCoachMent.substring(0, insertReviewTimeToGetMemoryCoachMent.length() - 1));
            db.execSQL(insertLevel.substring(0, insertLevel.length() - 1));
        }catch(SQLiteException e){

        }
        db.setTransactionSuccessful();
        db.endTransaction();
    }

    public class WordLog_for_exam {
        int c;
        double f;

        public WordLog_for_exam(int word_code, double forgetting_pct) {
            this.c = word_code;
            this.f = forgetting_pct;
        }

        public int getWord_code() {
            return c;
        }

        public void setWord_code(int word_code) {
            this.c = word_code;
        }


        public double getForgetting_pct() {
            return f;
        }

        public void setForgetting_pct(double forgetting_pct) {
            this.f = forgetting_pct;
        }
    }


    public ArrayList<WordLog_for_exam> getExamWord(String date, int MAX_COUNT) {
        ArrayList<WordLog_for_exam> list = new ArrayList<DBPool.WordLog_for_exam>();
        try {
            Log.v("test_logs", "" + date + "   " + MAX_COUNT);
            Cursor cursor = db.rawQuery("SELECT * "
                    + "FROM word_log as t1 INNER JOIN words as t2 ON t1.word_code = t2.word_code "
                    + "WHERE t1.reg_date >= '" + date + "' AND t1.pre_state<1 AND t1.after_state > 0 AND t2.state>0"
                    + " GROUP BY t1.word_code ORDER by random() LIMIT " + MAX_COUNT, null);

            cursor.moveToFirst();

            if (cursor.getCount() > 0) {
                while (!cursor.isAfterLast()) {
                    Word word = getWord(cursor.getInt(cursor.getColumnIndex("word_code")));
                    List<Mean> meanlist = getMean(cursor.getInt(cursor.getColumnIndex("word_code")));
                    String mean = "";
                    for (int i = 0; i < meanlist.size(); i++) {
                        mean += meanlist.get(i).getMeaning() + ", ";
                    }
                    mean = mean.substring(0, mean.length() - 2);
                    Log.e("test_log", "word : " + word.getWord() + "  " + cursor.getInt(cursor.getColumnIndex("word_code")));
                    list.add(new WordLog_for_exam(cursor.getInt(cursor.getColumnIndex("word_code")),
                            0.80 - cursor.getDouble(cursor.getColumnIndex("Score"))));
                    cursor.moveToNext();
                }
            }
            Log.v("test_log", "   " + list.size());
        } catch (RuntimeException e) {
            // TODO: handle exception
            Log.v("test_log", " asdfasdfasdfasdfasdfasdfasfasdfasdfasdfasdf  ");
        }


        return list;
    }


    public String get_each_word_count(int difficulty) {
        Cursor cursor = db.rawQuery("SELECT count(*) as count "
                + "FROM words WHERE difficulty =" + difficulty + " AND state > 0", null);
        cursor.moveToFirst();

        return cursor.getString(cursor.getColumnIndex("count"));
    }

    public ArrayList<WordLog_for_exam> getExamWord_difficulty(int MAX_COUNT) {
        ArrayList<WordLog_for_exam> list = new ArrayList<DBPool.WordLog_for_exam>();

        int difficulty = getWordLevel();

        Cursor cursor = db.rawQuery("SELECT * FROM words WHERE state >0 AND difficulty = " + difficulty + " ORDER by random() LIMIT " + MAX_COUNT, null);

        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            while (!cursor.isAfterLast()) {
                Word word = getWord(cursor.getInt(cursor.getColumnIndex("word_code")));
                List<Mean> meanlist = getMean(cursor.getInt(cursor.getColumnIndex("word_code")));
                Log.e("word_update", "" + word.get_id());
                String mean = "";
                for (int i = 0; i < meanlist.size(); i++) {
                    mean += meanlist.get(i).getMeaning() + ", ";
                }
                mean = mean.substring(0, mean.length() - 2);
                Log.e("test_log", "word : " + word.getWord() + "  " + cursor.getInt(cursor.getColumnIndex("word_code")));
                list.add(new WordLog_for_exam(cursor.getInt(cursor.getColumnIndex("word_code")),
                        0.80 - cursor.getDouble(cursor.getColumnIndex("Score"))));
                cursor.moveToNext();
            }
        }

        return list;

    }

    public ArrayList<WordLog_for_exam> getExamWord_All(int MAX_COUNT) {
        ArrayList<WordLog_for_exam> list = new ArrayList<DBPool.WordLog_for_exam>();

        Cursor cursor = db.rawQuery("SELECT * FROM words WHERE state >0 ORDER by random() LIMIT " + MAX_COUNT, null);

        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            while (!cursor.isAfterLast()) {
                Word word = getWord(cursor.getInt(cursor.getColumnIndex("word_code")));
                List<Mean> meanlist = getMean(cursor.getInt(cursor.getColumnIndex("word_code")));
                String mean = "";
                for (int i = 0; i < meanlist.size(); i++) {
                    mean += meanlist.get(i).getMeaning() + ", ";
                }
                mean = mean.substring(0, mean.length() - 2);
                Log.e("test_log", "word : " + word.getWord() + "  " + cursor.getInt(cursor.getColumnIndex("word_code")));
                list.add(new WordLog_for_exam(cursor.getInt(cursor.getColumnIndex("word_code")),
                        0.80 - cursor.getDouble(cursor.getColumnIndex("Score"))));
                cursor.moveToNext();
            }
        }

        return list;

    }

    public ArrayList<Integer> get_known_word() {
        ArrayList<Integer> list = new ArrayList<Integer>();
        Cursor cursor = db.rawQuery("SELECT * FROM words WHERE state >0 ORDER BY word_code ASC", null);

        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            while (!cursor.isAfterLast()) {
                int word_code = cursor.getInt(cursor.getColumnIndex("word_code"));
                list.add(word_code);
                cursor.moveToNext();
            }
        }

        return list;
    }

    public int get_new_word_count() {
        int total_count = 0;
		/*java.util.Date mDate = new java.util.Date(System.currentTimeMillis()-9000000);
		SimpleDateFormat format_date_to_string = new SimpleDateFormat("yyyyMMdd");
		String today = format_date_to_string.format(mDate);
		ArrayList<Integer> list = new ArrayList<Integer>();
		Cursor cursor = db.rawQuery("SELECT SUM(new_count) AS new_count FROM calendar_data WHERE date !="+today,null);

		cursor.moveToFirst();
		if(cursor.getCount()>0){
			total_count = cursor.getInt(cursor.getColumnIndex("new_count"));
			cursor.close();
			Cursor cursor2 = db.rawQuery("SELECT study_word FROM study_time" ,null);
			cursor2.moveToFirst();
			total_count += cursor2.getInt(cursor2.getColumnIndex("study_word"));
			cursor2.close();

		}else{
			cursor.close();
			Cursor cursor2 = db.rawQuery("SELECT study_word FROM study_time" ,null);
			cursor2.moveToFirst();
			total_count += cursor2.getInt(cursor2.getColumnIndex("study_word"));
			cursor2.close();
		}*/

        int i = 0;
        for (i = 0; i < 5; i++) {
            total_count += Integer.valueOf(get_each_word_count(i + 1));
        }

        return total_count;
    }

    public void backupDB(File src, File dst) throws IOException {
        DB_FILE_PATH = Config.DB_FILE_DIR + Config.DB_NAME;


        InputStream in = new FileInputStream(src);
        OutputStream out = new FileOutputStream(dst);
        try {
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
        } catch (Exception e) {
            // TODO: handle exception
        }
        // Transfer bytes from in to out

        in.close();
        out.close();
    }

    public void setWordState(int word_code, int state) {
        db.execSQL("update words set state = " + state + " where word_code = '" + word_code + "'");
    }

    public class word_log {
        int word_code;
        int location;
        int prestate;
        int afterstate;
        String reg_date;

        public word_log(int code, int location, int p_state, int a_state, String date) {
            this.word_code = code;
            this.location = location;
            this.prestate = p_state;
            this.afterstate = a_state;
            this.reg_date = date;
        }
    }


    public ArrayList<word_log> get_word_log() {
        ArrayList<word_log> list = new ArrayList<DBPool.word_log>();
        try {
            Cursor cursor = db.rawQuery("SELECT * FROM word_log WHERE isUpload = 0 ORDER BY reg_date ASC", null);

            cursor.moveToFirst();
            if (cursor.getCount() > 0) {
                while (!cursor.isAfterLast()) {
                    int word_code = cursor.getInt(cursor.getColumnIndex("word_code"));
                    int location = cursor.getInt(cursor.getColumnIndex("location"));
                    int pre_state = cursor.getInt(cursor.getColumnIndex("pre_state"));
                    int after_state = cursor.getInt(cursor.getColumnIndex("after_state"));
                    String reg_date = cursor.getString(cursor.getColumnIndex("reg_date"));
                    list.add(new word_log(word_code, location, pre_state, after_state, reg_date));
                    cursor.moveToNext();
                }
            }

            return list;

        } catch (Exception e) {
            // TODO: handle exception
            try {
                db.execSQL("ALTER TABLE word_log ADD COLUMN isUpload INTEGER DEFAULT '0'");
                db.execSQL("ALTER TABLE word_log ADD COLUMN location INTEGER DEFAULT '1'");
                return list;
            } catch (Exception e2) {
                // TODO: handle exception
                createWordLogTbl();
                db.execSQL("ALTER TABLE word_log ADD COLUMN isUpload INTEGER DEFAULT '0'");
                db.execSQL("ALTER TABLE word_log ADD COLUMN location INTEGER DEFAULT '1'");
                return list;
            }
        }
    }

    public void update_word_log() {
        db.execSQL("update word_log set isUpload = " + 1 + " where isUpload = '" + 0 + "'");

    }

    public boolean isExistStudentId() {
        Cursor cursor = db.rawQuery("SELECT count(*) as count FROM user_info WHERE length(student_id) >0", null);

        cursor.moveToFirst();
        try {
            if (cursor.getInt(cursor.getColumnIndex("count")) == 0) {
                return false;
            } else {
                return true;
            }
        } catch (Exception e) {
            return false;
        }


    }


    public void initialWord() {
        db.execSQL("update words set Score = 0.0, State = 0, Time=0,Frequency=0");

    }

    public void initialCalendarData() {
        db.execSQL("DELETE FROM calendar_data");
    }


    public void sendQuery(WordUpdateData word_mean) {
        // TODO Auto-generated method stub

        List<JsonNode> word_query = null;
        List<JsonNode> mean_query = null;
        int version = 1;
        word_query = word_mean.getWords();
        mean_query = word_mean.getMeans();
        version = word_mean.getCurrent_word_version();
        Log.e("word_update", "word : " + word_query.size() + "  means : " + mean_query.size() + "  version : " + version);


        try {
            db.beginTransaction();
            for (int i = 0; i < word_query.size(); i++) {
                db.execSQL(word_query.get(i).asText());

            }
            for (int i = 0; i < mean_query.size(); i++) {

                db.execSQL(mean_query.get(i).asText());
            }

            db.execSQL("Update words set value = abs(random())%1000000");
            db.execSQL("Update words set State = 0, Score = 0, Frequency = 0, Time = 0, forgetting_curves_test_flag = 0 where Score is null  ");
            Log.e("dbversion", "getversion : " + db.getVersion());
            Log.e("word_update", "setTransactionSuccessful");
            try {
                db.execSQL("ALTER TABLE user_info ADD COLUMN db_version VARCHAR(20) DEFAULT 0");
            } catch (SQLException e) {
                // TODO: handle exception
                Log.e("word_update", "ALTER exception : " + e.toString());
            } finally {
                db.execSQL("UPDATE user_info set db_version = " + version);
            }
            db.setTransactionSuccessful();
        } catch (SQLException e) {
            Log.e("word_update", "sql_exception : " + e.toString());
        } finally {

            Log.e("dbversion", "retroUpdateWord finally ");
            db.endTransaction();
        }


    }

    public int getDBversion() {
        int version = 0;
        try {
            Cursor c = db.rawQuery("SELECT db_version FROM user_info LIMIT 1", null);
            c.moveToFirst();
            version = c.getInt(c.getColumnIndex("db_version"));
            c.close();
        } catch (Exception e) {
            // TODO: handle exception
            Log.e("word_update", "exception : " + e.toString());
            db.execSQL("ALTER TABLE user_info ADD COLUMN db_version VARCHAR(20) DEFAULT 0");
        }

        return version;
    }

//	
//	public void sendQuery(String query) {
//		// TODO Auto-generated method stub
//		
//		try{
//			db.beginTransaction(); 
//			
//			db.execSQL(query);
//			db.setTransactionSuccessful();
//		} catch (SQLException e){
//			Log.e("word_update","exception : "+e.toString());
//		} finally {
//			db.endTransaction();
//		}
//		
//		
//	}

}
