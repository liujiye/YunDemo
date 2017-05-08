package com.demo.yun.util;

import android.database.Cursor;

public class DbFieldUtil
{
	public static byte[] getBlob(Cursor cursor, String key)
	{
		if (!cursor.isNull(cursor.getColumnIndex(key)))
		{
			return cursor.getBlob(cursor.getColumnIndex(key));
		}
		return "".getBytes();
	}

	public static boolean getBoolean(Cursor cursor, String key)
	{
		if (!cursor.isNull(cursor.getColumnIndex(key)))
		{
			return cursor.getInt(cursor.getColumnIndex(key)) == 1;
		}
		return false;
	}

	public static int getInt(Cursor cursor, String key)
	{
		if (!cursor.isNull(cursor.getColumnIndex(key)))
		{
			return cursor.getInt(cursor.getColumnIndex(key));
		}
		// 0
		return 0;
	}

	public static long getLong(Cursor cursor, String key)
	{
		if (!cursor.isNull(cursor.getColumnIndex(key)))
		{
			return cursor.getLong(cursor.getColumnIndex(key));
		}
		// 0
		return 0;
	}

	public static double getDouble(Cursor cursor, String key)
	{
		if (!cursor.isNull(cursor.getColumnIndex(key)))
		{
			return cursor.getDouble(cursor.getColumnIndex(key));
		}
		// 0
		return 0;
	}

	public static String getString(Cursor cursor, String key)
	{
		if (!cursor.isNull(cursor.getColumnIndex(key)))
		{
			return cursor.getString(cursor.getColumnIndex(key));
		}
		return "";
	}
}
