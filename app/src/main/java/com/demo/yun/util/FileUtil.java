package com.demo.yun.util;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.os.StatFs;
import android.provider.MediaStore;
import android.util.Base64;
import android.webkit.MimeTypeMap;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class FileUtil
{
	private static final long RATIO = 1024;
	public static final long SIZE_UNIT_B = 1;
	public static final long SIZE_UNIT_KB = SIZE_UNIT_B * RATIO;
	public static final long SIZE_UNIT_MB = SIZE_UNIT_KB * RATIO;
	public static final long SIZE_UNIT_GB = SIZE_UNIT_MB * RATIO;

	public static final int FILE_TAG_UNKNOWN = 0;
	public static final int FILE_TAG_FILE = 1;
	public static final int FILE_TAG_DIR = 2;
	public static final int FILE_TAG_ALL = FILE_TAG_FILE | FILE_TAG_DIR;

	/**
	 * 获得手机内部的app root路径。通常是 "/data/data/{App Package Name}/files"
	 */
	public static String getInternalDir(Context context)
	{
		File dir = context.getFilesDir();
		if (dir != null)
		{
			return dir.getPath();
		}

		return "";
	}

	/**
	 * 获得sdcard的挂载路径。通常是 "/mnt/sdcard"
	 */
	public static String getSDCardRootDir()
	{
		if (isSDCardAvailable())
		{
			File dir = Environment.getExternalStorageDirectory();
			if (dir != null)
			{
				return dir.getPath();
			}
		}

		return "";
	}

	/**
	 * 获得应用在sdcard上的目录。通常是"/mnt/sdcard/Android/data/{App Package Name}/files" <br>
	 * android v2.2以上版本，当某个应用被卸载时，该目录下的内容自动被清除。
	 */
	public static String getSDCardAppRootDir(Context context)
	{
		if (isSDCardAvailable())
		{
			File dir = context.getExternalFilesDir(null);
			if (dir != null)
			{
				return dir.getPath();
			}
		}

		return "";
	}

	/**
	 * 获得应用在sdcard上的目录。通常是"/mnt/sdcard/Android/data/{App Package Name}/files" <br>
	 * android v2.2以上版本，当某个应用被卸载时，该目录下的内容自动被清除。
	 * 
	 * @param type
	 *            files下的文件夹名称
	 */
	public static String getSDCardAppRootDir(Context context, String type)
	{
		if (isSDCardAvailable())
		{
			File dir = context.getExternalFilesDir(type);
			if (dir != null)
			{
				return dir.getPath();
			}
		}

		return "";
	}

	/**
	 * 获取系统相册目录
	 */
	public static String getSDCardPictureDir()
	{
		if (isSDCardAvailable())
		{
			File dir = Environment
					.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
			if (dir != null)
			{
				return dir.getPath();
			}
		}

		return "";
	}

	/**
	 * 获取sdcard的总容量（单位：字节）。 <br>
	 * return: -1，sdcard不存在。 other，sdcard实际大小。
	 */
	public static long getSDCardTotalSize()
	{
		if (isSDCardAvailable() == false)
			return -1;

		StatFs fs = new StatFs(getSDCardRootDir());
		long blockSize = fs.getBlockSize();
		long blockCount = fs.getBlockCount();
		long byteSize = blockCount * blockSize;

		return byteSize;
	}

	public static long getSDCardFreeSize()
	{
		if (isSDCardAvailable() == false)
			return -1;

		StatFs fs = new StatFs(getSDCardRootDir());
		long blockSize = fs.getBlockSize();
		long blockCount = fs.getAvailableBlocks();
		long byteSize = blockCount * blockSize;

		return byteSize;
	}

	public static String readFile(String fileName)
	{
		String content = "";
		BufferedReader reader = null;
		try
		{
			File file = new File(fileName);
			InputStream inputStream = new FileInputStream(file);
			// reader = new BufferedReader(new InputStreamReader(inputStream));
			// String line;
			// while ((line = reader.readLine()) != null)
			// {
			// content += line;
			// }
			int length = inputStream.available();
			byte[] buf = new byte[length];
			inputStream.read(buf);
			// content = new String(AESUtil.getInstance().decryptBytes(buf,
			// buf.length));
			content = new String(buf);
		}
		catch (Exception e)
		{
		}
		finally
		{
			try
			{
				if (reader != null)
				{
					reader.close();
				}
			}
			catch (IOException e)
			{
			}
		}
		return content;
	}

	public static boolean writeFile(String text, String filename)
	{
		File dstFile = new File(filename);

		try
		{
			FileOutputStream out = new FileOutputStream(dstFile);
			out.write(text.getBytes("utf-8"));
			// byte[] bytes = text.getBytes("utf-8");
			// out.write(AESUtil.getInstance().encryptBytes(bytes,
			// bytes.length));
			out.close();
		}
		catch (Exception e)
		{
			return false;
		}

		return true;
	}

	public static boolean writeFile(InputStream is, String desFileName)
	{
		// File dstFile = new File(desFileName);
		createNewFile(desFileName, true);

		try
		{
			FileOutputStream fos = new FileOutputStream(desFileName);

			byte[] buf = new byte[8 * 1024];
			int count = 0;
			while ((count = is.read(buf)) != -1)
			{
				fos.write(buf, 0, count);
			}

			fos.flush();
			is.close();
			fos.close();
		}
		catch (Exception e)
		{
			return false;
		}

		return true;
	}

	/**
	 * 调用系统工具打开文件
	 * 
	 * @param mContext
	 * @param path
	 * @param mimeType
	 */
	public static boolean openFile(Context mContext, String path, String mimeType)
	{
		Intent intent = new Intent();
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setAction(Intent.ACTION_VIEW);

		/* 设置intent的file与MimeType */
		intent.setDataAndType(Uri.fromFile(new File(path)), mimeType);
		LogUtil.e("mimeType" + mimeType);
		try
		{
			mContext.startActivity(intent);
		}
		catch (ActivityNotFoundException e)
		{
			return false;
		}

		return true;
	}

	public static long getRomTotalSize()
	{
		StatFs fs = new StatFs(Environment.getDataDirectory().getPath());
		long blockSize = fs.getBlockSize();
		long blockCount = fs.getBlockCount();
		long byteSize = blockCount * blockSize;

		return byteSize;
	}

	public static long getRomFreeSize()
	{
		StatFs fs = new StatFs(Environment.getDataDirectory().getPath());
		long blockSize = fs.getBlockSize();
		long blockCount = fs.getAvailableBlocks();
		long byteSize = blockCount * blockSize;

		return byteSize;
	}

	public static String FormetFileSize(long fileS)
	{
		// 转换文件大小
		DecimalFormat df = new DecimalFormat("#.00");
		String fileSizeString = "";
		if (fileS < 1024)
		{
			fileSizeString = df.format((double) fileS) + "B";
		}
		else if (fileS < 1048576)
		{
			fileSizeString = df.format((double) fileS / 1024) + "K";
		}
		else if (fileS < 1073741824)
		{
			fileSizeString = df.format((double) fileS / 1048576) + "M";
		}
		else
		{
			fileSizeString = df.format((double) fileS / 1073741824) + "G";
		}
		return fileSizeString;
	}

	/**
	 * 获得某个目录（中的所有文件）占用的存储空间。 useOccupy 是否使用占用空间计算。
	 */
	public static long getDirSize(String path, boolean useOccupy)
	{
		if (StringUtil.isEmpty(path))
			return -1;

		File file = new File(path);
		if (isFileExist(file) == false)
			return -1;
		if (file.isDirectory() == false)
			return -1;

		List<File> list = new ArrayList<File>();
		getListInDir(path, list, FILE_TAG_FILE, true);

		long totalSize = 0;

		if (useOccupy == false)
		{
			for (int i = 0; i < list.size(); i++)
				totalSize += list.get(i).length();
		}
		else
		{
			StatFs fs = new StatFs(path);
			long blocksize = fs.getBlockSize();
			long quotient; // 商
			long remain; // 余
			long filesize; // 文件实际大小。

			for (int i = 0; i < list.size(); i++)
			{
				filesize = list.get(i).length();
				quotient = filesize / blocksize;
				remain = filesize % blocksize;
				if (remain != 0)
					quotient += 1;

				totalSize += (quotient * blocksize);
			}
		}

		return totalSize;
	}

	public static long getFileSize(String path)
	{
		return getFileSize(new File(path));
	}

	public static long getFileSize(File file)
	{
		return file.length();
	}

	public static String loadAssetsFile(Context context, String filename)
	{
		String content = "";
		BufferedReader reader = null;
		try
		{
			reader = new BufferedReader(new InputStreamReader(context.getResources().getAssets()
					.open(filename)));
			String line;
			while ((line = reader.readLine()) != null)
			{
				content += line;
			}
		}
		catch (Exception e)
		{
		}
		finally
		{
			try
			{
				if (reader != null)
				{
					reader.close();
				}
			}
			catch (IOException e)
			{
			}
		}
		return content;
	}

	public static boolean isSDCardAvailable()
	{
		return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
	}

	public static boolean isFileExist(File file)
	{
		if (file != null)
		{
			return file.exists();
		}

		return false;
	}

	public static boolean isFileExist(String filePath)
	{
		if (StringUtil.isEmpty(filePath))
			return false;

		return isFileExist(new File(filePath));
	}

	public static String getFileName(String filePath)
	{
		if (StringUtil.isEmpty(filePath))
		{
			return "";
		}

		File file = new File(filePath);
		return file.getName();
	}

	public static String getFileBaseName(String filePath)
	{
		if (StringUtil.isEmpty(filePath))
			return null;

		return getFileBaseName(new File(filePath));
	}

	public static String getFileBaseName(File file)
	{
		if (file == null)
			return null;

		String strFileName = file.getName();

		int index = strFileName.lastIndexOf(".");
		if (index == -1)
			return strFileName;

		return strFileName.substring(0, index);
	}

	public static String getFileExtName(File file)
	{
		if (file == null)
			return "";

		return getFileExtName(file.getName());
	}

	public static String getFileExtName(String fileName)
	{
		if (StringUtil.isEmpty(fileName))
			return "";

		int index = fileName.lastIndexOf(".");
		if (index == -1)
			return "";

		return fileName.substring(index + 1, fileName.length());
	}

	// 创建目录
	public static boolean createDir(String dirname)
	{
		if (StringUtil.isEmpty(dirname))
			return false;

		File file = new File(dirname);
		return file.mkdirs();
	}

	/**
	 * 指定一个文件名，创建新文件。如果文件已存在，返回false。文件创建失败，返回false。<br>
	 * overrideExist 是否允许覆盖已存在的文件。
	 */
	public static boolean createNewFile(String filename, boolean overrideExist)
	{
		// LogUtil.d("filename:" + filename);
		if (StringUtil.isEmpty(filename))
			return false;

		File file = new File(filename);

		boolean blnFileExist = isFileExist(file);
		if (blnFileExist && file.isDirectory())
			return false;

		if (blnFileExist == true)
		{
			if (overrideExist == false)
				return false;
			else
				file.delete();
		}

		File parentFile = file.getParentFile();
		if (parentFile != null)
		{
			parentFile.mkdirs();
		}
		// file.getParentFile().mkdirs();

		try
		{
			return file.createNewFile();
		}
		catch (IOException e)
		{
			return false;
		}
	}

	public static byte[] getFileBytes(String filename)
	{
		byte[] result = null;
		File file = new File(filename);

		if (FileUtil.isFileExist(file))
		{
			// if (file.length() > 0)
			{
				// result = new byte[(int) file.length()];
				try
				{
					FileInputStream in = new FileInputStream(file);
					int length = in.available();
					result = new byte[length];
					int count = in.read(result);
					closeIOStream(in);
				}
				catch (Exception e)
				{
				}
			}
		}

		return result;
	}

	/**
	 * 拷贝文件
	 */
	public static boolean copyFile(File srcFile, File dstFile)
	{
		int BUFF_SIZE = 4 * 1024;
		byte[] buffer = new byte[BUFF_SIZE];
		FileInputStream in = null;
		FileOutputStream out = null;

		try
		{
			in = new FileInputStream(srcFile);
			out = new FileOutputStream(dstFile);

			while (true)
			{
				int count = in.read(buffer);
				if (count == -1)
				{
					in.close();
					out.flush();
					out.close();
					break;
				}
				else
					out.write(buffer, 0, count);
			}
		}
		catch (IOException e)
		{
			closeIOStream(in);
			closeIOStream(out);
			return false;
		}

		return true;
	}

	/**
	 * 递归实现目录和文件的拷贝。
	 */
	public static boolean xcopy(String from, String to)
	{
		File fileFrom = new File(from);
		if (!fileFrom.exists())
			return false;

		File fileTo = new File(to);

		if (fileFrom.isFile())
		{
			File path = fileTo.getParentFile();
			if (path != null)
				path.mkdirs();

			if (copyFile(fileFrom, fileTo) == false)
				return false;
		}
		else if (fileFrom.isDirectory())
		{
			if (!fileTo.exists())
				fileTo.mkdirs();

			String[] fileList;
			fileList = fileFrom.list();
			for (int i = 0; i < fileList.length; i++)
			{
				if (xcopy(from + File.separator + fileList[i], to + File.separator + fileList[i]) == false)
					return false;
			}
		}

		return true;
	}

	/**
	 * 开启后台线程删除文件
	 * 
	 * @return
	 */
	public static void deleteFileBackThread(final String filepath)
	{
		if (filepath == null)
		{
			return;
		}
		new Thread(new Runnable()
		{

			@Override
			public void run()
			{
				try
				{

					deleteFile(new File(filepath));
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}

			}
		}).start();

	}

	/**
	 * 删除文件（包括文件夹）
	 */
	public static boolean deleteFile(final String path)
	{
		return deleteFile(new File(path));
	}

	/**
	 * 删除文件（包括文件夹）
	 */
	public static boolean deleteFile(final File file)
	{
		if (file != null && file.exists())
		{
			if (file.isFile())
			{
				return file.delete();
			}
			else
			{
				File[] files = file.listFiles();
				if (files != null && files.length > 0)
				{
					for (File f : files)
					{
						if (f.isFile())
						{
							f.delete();
						}
						else
						{
							deleteFile(f);
						}
					}
				}

				return file.delete();
			}
		}

		return false;
	}

	/**
	 * 将图片文件转成bitmap
	 * */
	public static Bitmap getFileBitmap(String filename)
	{
		FileInputStream fis = null;

		try
		{
			fis = new FileInputStream(filename);
			return BitmapFactory.decodeStream(fis);
		}
		catch (Exception e)
		{
			closeIOStream(fis);
			return null;
		}
	}

	public static void closeIOStream(Closeable stream)
	{
		if (stream == null)
			return;

		try
		{
			stream.close();
		}
		catch (IOException e)
		{
		}
	}

	/**
	 * 扫描指定的dirPath目录，将符合filetag的文件或目录填充到list中。
	 * 
	 * @param dirPath
	 *            : 需要扫描的目录。
	 * @param list
	 *            : 填充到该list。
	 * @param filetag
	 *            ：FILE_TAG_FILE 只选择文件；FILE_TAG_DIR 只选择目录；FILE_TAG_ALL 文件和目录都要。
	 * @param recursion
	 *            : 是否递归扫描子目录。
	 */
	public static void getListInDir(String dirPath, List<File> list, int filetag, boolean recursion)
	{
		File dir = new File(dirPath);

		File[] files = dir.listFiles();
		if (files != null && files.length > 0)
		{
			for (File f : files)
			{
				if (checkFileTag(filetag, f))
				{
					list.add(f);
				}

				if (recursion && f.isDirectory())
				{
					getListInDir(f.getPath(), list, filetag, recursion);
				}
			}
		}
	}

	public static boolean copyStream(InputStream in, OutputStream out)
	{
		try
		{
			try
			{
				byte[] buffer = new byte[4 * 1024];
				int bytesRead;
				while ((bytesRead = in.read(buffer)) >= 0)
				{
					out.write(buffer, 0, bytesRead);
				}
			}
			finally
			{
				out.flush();
			}
			return true;
		}
		catch (Exception e)
		{
			return false;
		}
	}

	/**
	 * 将assets中的某个文件拷贝出来（拷贝到sd卡或手机内存中）。
	 */
	public static boolean copyAssetsFile(Context context, String assetFileName, String dstFileName)
	{
		InputStream in = null;
		OutputStream out = null;

		try
		{
			in = context.getResources().getAssets().open(assetFileName);
			out = new FileOutputStream(new File(dstFileName));

			if (copyStream(in, out) == false)
				return false;
		}
		catch (IOException e)
		{
			return false;
		}
		finally
		{
			closeIOStream(in);
			closeIOStream(out);
		}

		return true;
	}

	public static boolean UnZip(String sourceFileName, String desDir)
	{
		try
		{
			ZipFile zf = new ZipFile(new File(sourceFileName));
			Enumeration<ZipEntry> en = (Enumeration<ZipEntry>) zf.entries();

			int length = 0;
			byte[] b = new byte[4 * 1024];
			File fileDir = new File(desDir);

			while (en.hasMoreElements())
			{
				ZipEntry ze = en.nextElement();
				File f = new File(fileDir, ze.getName());
				if (ze.isDirectory())
				{
					f.mkdirs();
				}
				else
				{
					if (!f.getParentFile().exists())
					{
						f.getParentFile().mkdirs();
					}

					OutputStream outputStream = new FileOutputStream(f);
					InputStream inputStream = zf.getInputStream(ze);

					while ((length = inputStream.read(b)) > 0)
						outputStream.write(b, 0, length);

					inputStream.close();
					outputStream.close();
				}
			}

			zf.close();
		}
		catch (Exception e)
		{
			LogUtil.e("Zip error = " + e.toString());
			return false;
		}

		return true;
	}

	/**
	 * 从图片url中获得bitmap
	 * */
	public static Bitmap getBitmapFromUrl(String imgUrl)
	{
		URL url;
		Bitmap bitmap = null;
		try
		{
			url = new URL(imgUrl);
			InputStream is = url.openConnection().getInputStream();
			BufferedInputStream bis = new BufferedInputStream(is);
			bitmap = BitmapFactory.decodeStream(bis);
			bis.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return bitmap;
	}

	public static String getFileSizeString(long fileSize)
	{
		String strSize = "";
		if (fileSize < 1024)
		{
			strSize = fileSize + " b";
		}
		else if (fileSize >= 1024 && fileSize < 1024 * 1024)
		{
			strSize = fileSize / 1024 + " kb";
		}
		else if (fileSize >= 1024 * 1024)
		{
			strSize = fileSize / (1024 * 1024) + "M";
		}

		return strSize;
	}

	/**
	 * 文件追加。把dstFilePath追加到srcFilePath中
	 */
	public static boolean appendFile(String srcFilePath, String dstFilePath)
	{
		try
		{
			// 使用FileOutputStream，在构造FileOutputStream时，把第二个参数设为true
			FileOutputStream fos = new FileOutputStream(srcFilePath, true);
			FileInputStream fis = new FileInputStream(dstFilePath);
			byte[] buf = new byte[2 * 1024];
			int count = 0;
			while ((count = fis.read(buf)) != -1)
			{
				fos.write(buf, 0, count);
			}

			fis.close();
			fos.flush();
			fos.close();

			return true;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return false;
	}

	/**
	 * @param srcFilePath
	 *            原始文件
	 * @param dstFilePath
	 *            要追加的文件
	 * @return
	 */
	public static boolean appendFile2(String srcFilePath, String dstFilePath)
	{
		try
		{
			RandomAccessFile randomFile = new RandomAccessFile(srcFilePath, "rw");
			FileInputStream fls = new FileInputStream(new File(dstFilePath));
			byte[] b = new byte[fls.available()];
			fls.read(b);
			long l = randomFile.length();
			// 将指针移到末尾
			randomFile.seek(l);
			randomFile.write(b);
			fls.close();
			randomFile.close();
			return true;
		}
		catch (IOException e)
		{
			System.out.println(e);
		}
		return false;
	}

	private static boolean checkFileTag(int tag, File file)
	{
		int fileTag = getFileTag(file);
		int result = fileTag & tag;

		return (result != 0);
	}

	private static int getFileTag(File file)
	{
		if (file.isFile())
			return FILE_TAG_FILE;

		if (file.isDirectory())
			return FILE_TAG_DIR;

		return FILE_TAG_UNKNOWN;
	}

	public static String getFileType(String fileName)
	{
		String[] fileTypes = new String[] { "doc", "docx", "epub", "pdf", "ppt", "pptx", "txt",
				"wps", "xls", "xlsx", "zip" };

		for (String fileType : fileTypes)
		{
			if (fileName.endsWith("." + fileType))
			{
				return fileType;
			}
		}

		return "unknown";
	}

	/**
	 * 通过使用自带的文件管理器选中文件，解析它的路径
	 * 
	 * @param context
	 * @param uri
	 * @return
	 */
	public static String getFilePath(Context context, Uri uri)
	{
		LogUtil.i("uri : " + uri);
		LogUtil.i("uri scheme : " + uri.getScheme());
		if ("content".equalsIgnoreCase(uri.getScheme()))
		{
			// String[] projection = { MediaStore.Images.Media.DATA };
			Cursor cursor = null;

			try
			{
				InputStream inputStream = context.getContentResolver().openInputStream(uri);
				if (inputStream != null)
				{
					cursor = context.getContentResolver().query(uri, null, null, null, null);
					cursor.moveToFirst();
					String displayName = DbFieldUtil.getString(cursor,
							MediaStore.Images.Media.DISPLAY_NAME);
					String mimeType = DbFieldUtil.getString(cursor,
							MediaStore.Images.Media.MIME_TYPE);
					if (StringUtil.isEmpty(FileUtil.getFileExtName(displayName))
							&& mimeType != null && mimeType.startsWith("image/"))
					{
						displayName = displayName
								+ "."
								+ mimeType
										.substring(mimeType.indexOf("image/") + "image/".length());
					}

					String dstFileName = FileUtil.getSDCardAppRootDir(context, "tempFile") + "/"
							+ displayName;
					OutputStream out = new FileOutputStream(new File(dstFileName));
					if (FileUtil.copyStream(inputStream, out))
					{
						return dstFileName;
					}
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		else if ("file".equalsIgnoreCase(uri.getScheme()))
		{
			return uri.getPath();
		}

		return "";
	}

	/**
	 * 把bitmap保存成图片
	 */
	public static File saveBitmapAsFile(Bitmap bitmap, Bitmap.CompressFormat format,
			String fileFullPath)
	{
		File file = new File(fileFullPath);
		try
		{
			file.createNewFile();
			FileOutputStream fOut = null;
			try
			{
				fOut = new FileOutputStream(file);
			}
			catch (FileNotFoundException e)
			{
				e.printStackTrace();
			}

			bitmap.compress(format, 100, fOut);

			try
			{
				fOut.flush();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
			try
			{
				fOut.close();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		catch (IOException e1)
		{
			e1.printStackTrace();
		}

		return file;
	}

	/**
	 * 使用系统当前时间作为拍照的文件名
	 * */
	public static String createPhotoName()
	{
		String fileName = "";

		Date date = new Date(System.currentTimeMillis()); // 系统当前时间
		SimpleDateFormat dateFormat = new SimpleDateFormat("'IMG'_yyyyMMdd_HHmmss");

		fileName = dateFormat.format(date) + ".jpg";
		return fileName;
	}

	/**
	 * 获取文件的mime type
	 * */
	public static String getMimeType(String url)
	{
		String type = "";
		String extension = MimeTypeMap.getFileExtensionFromUrl(url);
		if (extension != null)
		{
			MimeTypeMap mime = MimeTypeMap.getSingleton();
			type = mime.getMimeTypeFromExtension(extension);
		}
		return type;
	}

	/**
	 * 将图片转为base64字符串
	 * */
	public static String encodeBase64Bitmap(Bitmap bitmap, int quality)
	{
		String str = "";
		ByteArrayOutputStream baos = null;
		try
		{
			baos = new ByteArrayOutputStream();
			bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos);
			str = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);
		}
		catch (Exception e)
		{
		}
		finally
		{
			try
			{
				if (baos != null)
					baos.close();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		return str;
	}

	/**
	 * <p>
	 * 将文件转成base64 字符串
	 * </p>
	 * 
	 * @param path
	 *            文件路径
	 * @return
	 */
	public static String encodeBase64File(String path)
	{
		try
		{
			File file = new File(path);
			FileInputStream inputFile;
			inputFile = new FileInputStream(file);
			byte[] buffer = new byte[(int) file.length()];
			inputFile.read(buffer);
			inputFile.close();
			return Base64.encodeToString(buffer, Base64.DEFAULT);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return "";
	}

	/**
	 * <p>
	 * 将base64字符解码保存文件
	 * </p>
	 * 
	 * @param base64Code
	 * @param targetPath
	 */
	public static void decoderBase64File(String base64Code, String targetPath)
	{
		try
		{
			byte[] baseByte = Base64.decode(base64Code, Base64.DEFAULT);

			FileOutputStream out = new FileOutputStream(targetPath);
			out.write(baseByte);
			out.close();
		}
		catch (Exception e)
		{
		}
	}
}
