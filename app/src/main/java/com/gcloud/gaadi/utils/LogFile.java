package com.gcloud.gaadi.utils;

import android.content.Context;
import android.os.Environment;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;

public class LogFile {
	
	//String []logLevel={"LOG_LEVEL_FINE", "LOG_LEVEL_INFO", "LOG_LEVEL_WARNING", "LOG_LEVEL_EXCEPTION"};
	
	
	public final static int LOG_LEVEL_FINE = 0;
	public final static int LOG_LEVEL_INFO = 1;
	 public final static int LOG_LEVEL_WARNING = 2;
	 public static final int LOG_LEVEL_EXCEPTION = 3;
	
	private static LogFile log;
	public static BufferedWriter out;
	File logFileInfo;
	FileWriter logWriter;
	Context cont;
	int level;
	String msg;
	String classTag;
	
	
	public static synchronized LogFile getInstance() {
		if (log == null) {
			log = new LogFile();
		}
		return log;
	}
	
	public synchronized void createFileOnDevice(Boolean append)
	{
        /*
         * Function to initially create the log file and it also writes the time of creation to file.
         */
		 try
		 {
			 File sdcardPath = Environment.getExternalStorageDirectory();
			 if(sdcardPath.canWrite())
			 {
				 logFileInfo = new File(sdcardPath, "ImageUploadLog.txt");
				 logWriter = new FileWriter(logFileInfo, append);
				 out = new BufferedWriter(logWriter);
			 }
		 }
		 catch(Exception e)
		 {
			 e.printStackTrace();
		 }
    }
	 
	 public synchronized void writeToFile(String tag, String message)
	 {
			 try {
		        	String logwrittenTime = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
		            out.write(logwrittenTime + " :: "+tag+" : "+" : "+message+"\n");
		            out.write("\n");
		            out.flush();
		        }
			 catch (IOException e)
			 {
		            e.printStackTrace();
		     }
	 }
}
