package com.itheima.mobilesafe.db.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.itheima.mobilesafe.db.BlackNumberDBOpenHelper;
import com.itheima.mobilesafe.domain.BlackNumberInfo;

/**
 * 黑名单数据库的增删改查业务类
 * 
 * @ClassName: BlackNumberDao
 * @author JinHeng
 * @date 2015年1月9日 上午10:31:19
 */
public class BlackNumberDao {

	private BlackNumberDBOpenHelper helper;

	/**
	 * 构造方法
	 * 
	 * @param context
	 */
	public BlackNumberDao(Context context) {
		helper = new BlackNumberDBOpenHelper(context);
	}

	/**
	 * 查询黑名单号码是是否存在
	 * 
	 * @Title: find
	 * @author JinHeng
	 * @date 2015年1月9日 上午10:43:42
	 * @throws
	 */
	public boolean find(String number) {
		boolean result = false;
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cursor = db.rawQuery("select * from blacknumber where number=?", new String[] { number });
		if (cursor.moveToNext()) {
			result = true;
		}
		cursor.close();
		db.close();
		return result;

	}

	/**
	 * 查询黑名单号码的拦截模式 ，返回号码的拦截模式，不是黑名单号码返回null
	 * 
	 * @Title: findMode
	 * @author JinHeng
	 * @date 2015年1月9日 上午10:44:22
	 * @throws
	 */
	public String findMode(String number) {
		String result = null;
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cursor = db.rawQuery("select mode from blacknumber where number=?", new String[] { number });
		if (cursor.moveToNext()) {
			result = cursor.getString(0);
		}

		cursor.close();
		db.close();
		return result;
	}

	/**
	 * 查询全部黑名单号码
	 * 
	 * @Title: findAll
	 * @author JinHeng
	 * @date 2015年1月9日 上午10:49:13
	 * @throws
	 */
	public List<BlackNumberInfo> findAll() {
		List<BlackNumberInfo> result = new ArrayList<BlackNumberInfo>();
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cursor = db.rawQuery("select number,mode from blacknumber order by _id desc", null);
		while (cursor.moveToNext()) {
			BlackNumberInfo info = new BlackNumberInfo();
			String number = cursor.getString(0);
			String mode = cursor.getString(1);
			info.setMode(mode);
			info.setNumber(number);
			result.add(info);
		}
		cursor.close();
		db.close();
		return result;
	}

	/**
	 * 添加黑名单号码
	 * 
	 * @param number
	 *            黑名单号码
	 * @param mode
	 *            拦截模式 1.电话拦截 2.短信拦截 3.全部拦截
	 * @Title: add
	 * @author JinHeng
	 * @date 2015年1月9日 上午10:52:23
	 * @throws
	 */
	public void add(String number, String mode) {
		SQLiteDatabase db = helper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("number", number);
		values.put("mode", mode);
		db.insert("blacknumber", null, values);
		db.close();
	}

	/**
	 * 修改黑名单号码的拦截模式
	 * 
	 * @param number
	 *            要修改的黑名单号码
	 * @param newmode
	 *            新的拦截模式
	 * @Title: update
	 * @author JinHeng
	 * @date 2015年1月9日 上午10:53:42
	 * @throws
	 */
	public void update(String number, String newmode) {
		SQLiteDatabase db = helper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("mode", newmode);
		db.update("blacknumber", values, "number=?", new String[] { number });
		db.close();
	}

	/**
	 * 删除黑名单号码
	 * 
	 * @param number
	 *            要删除的黑名单号码
	 * @Title: delete
	 * @author JinHeng
	 * @date 2015年1月9日 上午10:56:10
	 * @throws
	 */
	public void delete(String number) {
		SQLiteDatabase db = helper.getWritableDatabase();
		db.delete("blacknumber", "number=?", new String[] { number });
		db.close();
	}
}
