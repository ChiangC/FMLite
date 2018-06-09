package com.fmtech.fmlite.db;

import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

/**
 * ==================================================================
 * Copyright (C) 2018 FMTech All Rights Reserved.
 *
 * @author Drew.Chiang
 * @version v1.0.0
 * @email chiangchuna@gmail.com
 * <p>
 * ==================================================================
 */

public class BaseDaoFactory {
    private String mDataBasePath;
    private SQLiteDatabase mSQLiteDatabase;
    private static BaseDaoFactory sInstance = new BaseDaoFactory();

    private BaseDaoFactory(){
        mDataBasePath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/user.db";
        openDatabase();
    }

    private void openDatabase(){
        mSQLiteDatabase = SQLiteDatabase.openOrCreateDatabase(mDataBasePath, null);
    }

    public static BaseDaoFactory getInstance(){
        return sInstance;
    }

    public synchronized <T extends BaseDao<M>, M> T getDataHelper(Class<T> baseDaoClazz, Class<M> entityClazz){
        BaseDao baseDao = null;
        try {
            baseDao = baseDaoClazz.newInstance();
            baseDao.init(entityClazz, mSQLiteDatabase);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return (T)baseDao;
    }


}
