package com.demo.yun.util;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class UIUtil
{
	public static void showToastShort(Context context, int resId)
	{
		showToast(context, resId, Toast.LENGTH_SHORT);
	}

	public static void showToastShort(Context context, String text)
	{
		showToast(context, text, Toast.LENGTH_SHORT);
	}

	public static void showToastLong(Context context, int resId)
	{
		showToast(context, resId, Toast.LENGTH_LONG);
	}

	public static void showToastLong(Context context, String text)
	{
		showToast(context, text, Toast.LENGTH_LONG);
	}

	private static void showToast(Context context, int resId, int duration)
	{
		String text = context.getString(resId);
		showToast(context, text, duration);
	}

	private static void showToast(Context context, String text, int duration)
	{
		Toast toast = Toast.makeText(context, text, duration);
		toast.show();
	}

	/**
	 * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
	 */
	public static int dip2px(Context context, float dpValue)
	{
		if (context == null)
		{
			return 0;
		}
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	/**
	 * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
	 */
	public static float dp2px(Context context, float dpValue)
	{
		if (context == null)
		{
			return 0;
		}
		final float scale = context.getResources().getDisplayMetrics().density;
		return (dpValue * scale + 0.5f);
	}

	/**
	 * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
	 */
	public static int px2dip(Context context, float pxValue)
	{
		if (context == null)
		{
			return 0;
		}
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

	/**
	 * 返回列表是否为空（为null或者列表中没有元素）
	 * 
	 * @param list 列表
	 * */
	public static <E extends Object> boolean isListEmpty(List<E> list)
	{
		if (list == null || list.size() == 0)
		{
			return true;
		}

		return false;
	}

	/**
	 * 返回列表是否不为空(不为null且列表中有元素)
	 * 
	 * @param list 列表
	 * */
	public static <E extends Object> boolean isListNotEmpty(List<E> list)
	{
		return !isListEmpty(list);
	}


	/**
	 * 设置组合图标（适用于宽高一样的图片）
	 * <p>
	 * 一般只需要设置上下左右中的一个
	 *
	 * @param textView 文本视图
	 * @param drawable 图片
	 * @param nWhich 0: 左, 1: 上, 2: 右, 3: 下
	 * @param nWidth 宽高
	 */
	public static void setCompoundDrawables(TextView textView, Drawable drawable, final int nWhich, final int nWidth)
	{
		setCompoundDrawables(textView, drawable, nWhich, 0, 0, nWidth, nWidth);
	}

	/**
	 * 设置组合图标
	 * <p>
	 * 一般只需要设置上下左右中的一个
	 * 
	 * @param textView 文本视图
	 * @param drawable 图片
	 * @param nWhich 0: 左, 1: 上, 2: 右, 3: 下
	 * @param nLeft 左侧范围
	 * @param nTop 上侧范围
	 * @param nRight 右侧范围（nRight - nLeft 就是宽度）
	 * @param nBottom 下侧范围（nBottom - nTop 就是高度）
	 */
	public static void setCompoundDrawables(TextView textView, Drawable drawable, final int nWhich, final int nLeft, final int nTop,
			final int nRight, final int nBottom)
	{
		try
		{
			if (textView != null)
			{
				// 设置范围
				if (drawable != null)
				{
					drawable.setBounds(nLeft, nTop, nRight, nBottom);
				}

				Drawable[] drawables = textView.getCompoundDrawables();

				switch (nWhich)
				{
				// 左
				case 0:
					textView.setCompoundDrawables(drawable, drawables[1], drawables[2], drawables[3]);
					break;

				// 上
				case 1:
					textView.setCompoundDrawables(drawables[0], drawable, drawables[2], drawables[3]);
					break;

				// 右
				case 2:
					textView.setCompoundDrawables(drawables[0], drawables[1], drawable, drawables[3]);
					break;

				// 下
				case 3:
					textView.setCompoundDrawables(drawables[0], drawables[1], drawables[2], drawable);
					break;

				default:
					break;
				}
			}
		}
		catch (Exception e)
		{
		}
	}
}
