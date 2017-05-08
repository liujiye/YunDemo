package com.demo.yun.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.RandomAccessFile;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Date;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * when you start the program, you must call createLogFile at the main LAUNCHER
 * class , it create the log file at sd root ,
 * filename=mainclass_month_date_hour_minite.log. LOG_ENABLED is read only ,you
 * can set at build . you can change the logLever at run, by setLogLever. you
 * set the level hight,the log rec litter, VERBOSE rec all, ERROR rec ERROR
 * only. you can open cur log file, by call openFileBySystem(). you need add
 * <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
 */
public class LogUtil
{
	private static final int VERBOSE = 2;
	private static final int DEBUG = 3;
	private static final int INFO = 4;
	private static final int WARN = 5;
	private static final int ERROR = 6;

	private static final boolean LOG_ENABLED = true;
	private static final boolean WRITE_TO_FILE_ENABLED = false;
	private static String log_tag = "";

	private static int LOG_LEVEL = 2;// show all level you can change it at any
	// where
	private static final String LOG_ENTRY_FORMAT = "[%tF %tT][%s][%s]%s"; // [2010-01-22
	private static PrintStream logStream;
	private static File logFile;
	private static String lineSeparator;
	private static final String LOG_FILE_NAME_PREFIX = "MoToliet";
	private static final String LOG_FILE_NAME_SUFFIX = ".log";
	private static final byte[] AES_KEY =
	{
		'T', 'O', 'L', 'I', 'E', 'T', 'S', 'E', 'A', 'T', 'T', 'O', 'L', 'I', 'E', 'T'
	};

	// you must call it at the LAUNCHER class
	public static void init(Context context)
	{
		log_tag = context.getPackageName();
		if (LOG_ENABLED && WRITE_TO_FILE_ENABLED)
		{
			File sdRoot = getSDPath(context);
			if (sdRoot != null)
			{
				// logFile = new File(sdRoot, LOG_FILE_NAME_PREFIX +
				// getLogDate() + LOG_FILE_NAME_SUFFIX);
				logFile = new File(sdRoot, LOG_FILE_NAME_PREFIX + LOG_FILE_NAME_SUFFIX);
				try
				{
					logStream = new PrintStream(new FileOutputStream(logFile, true), true);
				}
				catch (FileNotFoundException e)
				{
					e.printStackTrace();
				}
				lineSeparator = System.getProperty("line.separator");
			}
		}
	}

	/**
	 * you can change at run
	 * 
	 * @param level VERBOSE,DEBUG,INFO,WARN,ERROR
	 */
	public static void setLogLevel(int level)
	{
		if (LOG_ENABLED)
		{
			if (level < Log.VERBOSE || level > Log.ERROR)
			{
				return;
			}
			else
			{
				LOG_LEVEL = level;
			}
		}
	}

	public static void v(String tAG2, String string)
	{
		if (LOG_ENABLED && (LOG_LEVEL <= Log.VERBOSE))
		{
			Log.v(log_tag, tAG2 + " : " + string);
			if (logStream != null)
			{
				write("v", tAG2, string, null);
			}
		}

	}

	public static void v(String tAG2, String string, Throwable err)
	{
		if (LOG_ENABLED && (LOG_LEVEL <= Log.VERBOSE))
		{
			Log.v(log_tag, tAG2 + " : " + string);
			if (err != null)
			{
				Log.v(log_tag, tAG2 + " : " + Log.getStackTraceString(err));
			}
			if (logStream != null)
			{
				write("v", tAG2, string, err);
			}
		}

	}

	public static void v(String msg)
	{
		if (LOG_ENABLED && (LOG_LEVEL <= Log.VERBOSE))
		{
			Log.v(log_tag, msg);
			if (logStream != null)
			{
				write("v", log_tag, msg, null);
			}
		}

	}

	public static void d(String tag, String msg, Throwable err)
	{
		if (LOG_ENABLED && (LOG_LEVEL <= Log.DEBUG))
		{
			Log.d(log_tag, tag + " : " + msg);
			if (err != null)
			{
				Log.e(log_tag, tag + " : " + Log.getStackTraceString(err));
			}
			if (logStream != null)
			{
				write("D", tag, msg, err);
			}
		}
	}

	public static void d(String tag, String msg)
	{
		if (LOG_ENABLED && (LOG_LEVEL <= Log.DEBUG))
		{
			Log.d(log_tag, tag + " : " + msg);
			if (logStream != null)
			{
				write("D", tag, msg, null);
			}
		}
	}

	public static void d(String msg)
	{
		if (LOG_ENABLED && (LOG_LEVEL <= Log.DEBUG))
		{
			Log.d(log_tag, msg);
			if (logStream != null)
			{
				write("D", log_tag, msg, null);
			}
		}
	}

	public static void i(String tag, String msg, Throwable err)
	{
		if (LOG_ENABLED && (LOG_LEVEL <= Log.INFO))
		{
			Log.i(log_tag, tag + " : " + msg);
			if (err != null)
			{
				Log.e(log_tag, tag + " : " + Log.getStackTraceString(err));
			}
			if (logStream != null)
			{
				write("I", tag, msg, err);
			}
		}
	}

	public static void i(String tag, String msg)
	{
		if (LOG_ENABLED && (LOG_LEVEL <= Log.INFO))
		{
			Log.i(log_tag, tag + " : " + msg);
			if (logStream != null)
			{
				write("I", tag, msg, null);
			}
		}
	}

	public static void i(String msg)
	{
		if (LOG_ENABLED && (LOG_LEVEL <= Log.INFO))
		{
			Log.i(log_tag, msg);
			if (logStream != null)
			{
				write("I", log_tag, msg, null);
			}
		}
	}

	public static void w(String tag, String msg, Throwable err)
	{
		if (LOG_ENABLED && (LOG_LEVEL <= Log.WARN))
		{
			Log.w(log_tag, tag + " : " + msg);
			if (err != null)
			{
				Log.e(log_tag, tag + " : " + Log.getStackTraceString(err));
			}
			if (logStream != null)
			{
				write("W", tag, msg, err);
			}
		}
	}

	public static void w(String tag, String msg)
	{
		if (LOG_ENABLED && (LOG_LEVEL <= Log.WARN))
		{
			Log.w(log_tag, tag + " : " + msg);
			if (logStream != null)
			{
				write("W", tag, msg, null);
			}
		}
	}

	public static void w(String msg)
	{
		if (LOG_ENABLED && (LOG_LEVEL <= Log.WARN))
		{
			Log.w(log_tag, msg);
			if (logStream != null)
			{
				write("W", log_tag, msg, null);
			}
		}
	}

	public static void e(String tag, String msg, Throwable err)
	{
		if (LOG_ENABLED && (LOG_LEVEL <= Log.ERROR))
		{
			Log.e(log_tag, tag + " : " + msg);
			if (err != null)
			{
				Log.e(log_tag, tag + " : " + Log.getStackTraceString(err));
			}
			if (logStream != null)
			{
				write("E", tag, msg, err);
			}
		}
	}

	public static void e(String tag, String msg)
	{
		if (LOG_ENABLED && (LOG_LEVEL <= Log.ERROR))
		{
			Log.e(log_tag, tag + " : " + msg);
			if (logStream != null)
			{
				write("E", tag, msg, null);
			}
		}
	}

	public static void e(String msg)
	{
		if (LOG_ENABLED && (LOG_LEVEL <= Log.ERROR))
		{
			Log.e(log_tag, msg);
			if (logStream != null)
			{
				write("E", log_tag, msg, null);
			}
		}
	}

	@SuppressLint("DefaultLocale")
	private synchronized static void write(String level, String tag, String msg, Throwable error)
	{
		Date now = new Date();
		// String str = String.format(LOG_ENTRY_FORMAT, now, now, level, tag,
		// msg);
		// logStream.printf(LOG_ENTRY_FORMAT, now, now, level, tag, msg);
		// logStream.println();
		try
		{
			// SecretKeySpec secretKey = new SecretKeySpec(AES_KEY, "AES");
			// CipherOutputStream cos = null;
			// cos = appendAES(logFile, secretKey);
			// cos.write((str + lineSeparator).getBytes("UTF-8"));

			logStream.printf(LOG_ENTRY_FORMAT, now, now, level, tag, msg);
			logStream.println();

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			// try
			// {
			// if (cos != null)
			// cos.close();
			// }
			// catch (IOException e)
			// {
			// e.printStackTrace();
			// }
		}

		if (error != null)
		{
			error.printStackTrace(logStream);
			logStream.println();
		}
	}

	@Override
	protected void finalize() throws Throwable
	{
		super.finalize();
		if (logStream != null)
		{
			logStream.close();
		}
	}

	private static boolean isSdCardAvailable()
	{
		return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
	}

	private static File getSDPath(Context context)
	{
		if (isSdCardAvailable())
		{
			return context.getExternalFilesDir(null);
		}
		else
		{
			return null;
		}
	}

	public static CipherOutputStream appendAES(File file, SecretKeySpec key, SecureRandom sr) throws IOException, NoSuchAlgorithmException,
			NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException
	{
		RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
		byte[] iv = new byte[16];
		byte[] lastBlock = null;
		if (randomAccessFile.length() % 16L != 0L)
		{
			throw new IllegalArgumentException("Invalid file length (not a multiple of block size)");
		}
		else if (randomAccessFile.length() == 16)
		{
			throw new IllegalArgumentException("Invalid file length (need 2 blocks for iv and data)");
		}
		else if (randomAccessFile.length() == 0L)
		{
			// new file: start by appending an IV
			if (sr == null)
				sr = new SecureRandom();
			sr.nextBytes(iv);
			randomAccessFile.write(iv);
		}
		else
		{
			// file length is at least 2 blocks
			randomAccessFile.seek(randomAccessFile.length() - 32);
			randomAccessFile.read(iv);
			byte[] lastBlockEnc = new byte[16];
			randomAccessFile.read(lastBlockEnc);
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(iv));
			lastBlock = cipher.doFinal(lastBlockEnc);
			randomAccessFile.seek(randomAccessFile.length() - 16);
		}
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(iv));
		byte[] out;
		if (lastBlock != null)
		{
			out = cipher.update(lastBlock);
			if (out != null)
				randomAccessFile.write(out);
		}
		CipherOutputStream cos = new CipherOutputStream(new FileOutputStream(randomAccessFile.getFD()), cipher);
		return cos;
	}

	public static CipherOutputStream appendAES(File file, SecretKeySpec key) throws IOException, NoSuchAlgorithmException, NoSuchPaddingException,
			InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException
	{
		return appendAES(file, key, null);
	}

	public static CipherInputStream decryptAES(File file, SecretKeySpec key) throws IOException, NoSuchAlgorithmException, NoSuchPaddingException,
			InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException
	{
		FileInputStream fin = new FileInputStream(file);
		byte[] iv = new byte[16];
		if (fin.read(iv) < 16)
		{
			throw new IllegalArgumentException("Invalid file length (needs a full block for iv)");
		}
		;
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(iv));
		CipherInputStream cis = new CipherInputStream(fin, cipher);
		return cis;
	}
}
