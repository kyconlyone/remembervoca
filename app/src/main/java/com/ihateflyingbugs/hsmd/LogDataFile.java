package com.ihateflyingbugs.hsmd;

import android.content.Context;
import android.content.ContextWrapper;
import android.os.Environment;

import com.ihateflyingbugs.hsmd.data.Config;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


public class LogDataFile {
	Context mContext;

	public static final String ExternalDirectory = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Aremember_voca/";

	public static final String DB_FILE_DIR = ExternalDirectory + "data/";
	private String DB_FILE_PATH;
	public LogDataFile(Context context){
		mContext = context;
	}

	public void input_LogData_in_file(String data){
		ContextWrapper cw = new ContextWrapper(mContext);

		DB_FILE_PATH = Config.DB_FILE_DIR + Config.Log_NAME;
		File file = new File(Config.DB_FILE_DIR);

		if(!file.exists() && !file.mkdirs())
			return;

		file = new File(DB_FILE_PATH);

		FileOutputStream fos = null;

		try {
			fos = new FileOutputStream(file, true);
			if (!file.exists()) {
				file.createNewFile();
			}

			byte[] contentInBytes = data.getBytes();
			fos.write(contentInBytes);
			fos.flush();

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			try {
				fos.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
