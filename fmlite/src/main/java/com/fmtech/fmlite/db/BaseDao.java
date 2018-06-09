package com.fmtech.fmlite.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.fmtech.fmlite.annotation.DatabaseField;
import com.fmtech.fmlite.annotation.DatabaseTable;
import com.fmtech.fmlite.db.IBaseDao;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * ==================================================================
 * Copyright (C) 2018 FMTech All Rights Reserved.
 *
 * @author Drew.Chiang
 * @version v1.0.0
 * @email chiangchuna@gmail.com
 *
 * ==================================================================
 */

public abstract class BaseDao<T> implements IBaseDao<T>{

    private SQLiteDatabase mSQLiteDatabase;
    private boolean isInited = false;
    private Class<T> mEnityClass;

    /**
     * key--->table columnName
     * value ---> entity class Field
     */
    private HashMap<String, Field> mCacheMap;

    private String mTableName;

    protected synchronized boolean init(Class<T> entity, SQLiteDatabase sqLiteDatabase){
        if(!isInited){
            mEnityClass = entity;
            mSQLiteDatabase = sqLiteDatabase;

            //Mapping table name;
            if(entity.getAnnotation(DatabaseTable.class) == null){
                mTableName = entity.getClass().getSimpleName();
            }else{
                mTableName = entity.getAnnotation(DatabaseTable.class).value();
            }

            if(!mSQLiteDatabase.isOpen()){
                return false;
            }

            if(!TextUtils.isEmpty(getCreateTableSQL())){
                mSQLiteDatabase.execSQL(getCreateTableSQL());
            }

            mCacheMap = new HashMap<>();
            initCacheMap();

            isInited  = true;
        }
        return isInited;
    }

    private void initCacheMap(){
        String sql = "select * from " + mTableName + " limit 1 , 0";
        Cursor cursor = null;
        try {
            cursor = mSQLiteDatabase.rawQuery(sql, null);
            if(null == cursor){
                return;
            }

            String[] columnNames = cursor.getColumnNames();
            Field[] colmunFields = mEnityClass.getFields();
            for(Field field:colmunFields){
                field.setAccessible(true);
            }

            for(String columnName:columnNames){
                Field columnField = null;
                for(Field field:colmunFields){
                    String fieldName = null;
                    if(null != field.getAnnotation(DatabaseField.class)){
                        fieldName = field.getAnnotation(DatabaseField.class).value();
                    }else{
                        fieldName = field.getName();
                    }
                    if(columnName.equals(fieldName)){
                        columnField = field;
                        break;
                    }
                }

                if(null != columnField){
                    mCacheMap.put(columnName, columnField);
                }
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        } finally {
            if(null != cursor){
                cursor.close();
            }
        }

    }

    @Override
    public Long insert(T entity) {
        Map<String, String> map = entityToMap(entity);
        ContentValues contentValues = getContentValues(map);
        if(null == contentValues){
            return Long.valueOf(-1);
        }
        return mSQLiteDatabase.insert(mTableName, null, contentValues);
    }

    private ContentValues getContentValues(Map<String, String> map) {
        ContentValues contentValues = new ContentValues();
        if(null != map){
            Set keys = map.keySet();
            Iterator<String> iterator = keys.iterator();
            while(iterator.hasNext()){
                String key = iterator.next();
                String value = map.get(key);
                if (null != value) {
                    contentValues.put(key, value);
                }
            }
        }
        return contentValues;
    }

    @Override
    public int delete(T where) {
        Map<String, String> map = entityToMap(where);
        Condition condition = new Condition(map);
        return mSQLiteDatabase.delete(mTableName, condition.getWhereClause(), condition.getWhereArgs());
    }

    @Override
    public int update(T entity, T where) {
        Map<String, String> entityMap = entityToMap(entity);
        ContentValues contentValues = getContentValues(entityMap);

        Map<String, String> whereMap = entityToMap(where);
        Condition condition = new Condition(whereMap);
        return mSQLiteDatabase.update(mTableName, contentValues, condition.getWhereClause(), condition.getWhereArgs());
    }

    @Override
    public List<T> query(T where) {
        return query(where, null, null, null);
    }

    @Override
    public List<T> query(T where, String orderBy, Integer startIndex, Integer limit) {
        Map<String, String> map = entityToMap(where);
        Condition condition = new Condition(map);
        String limitStr = null;
        if(null != startIndex && null != limit){
            limitStr = startIndex + ","+limit;
        }
        Cursor cursor = mSQLiteDatabase.query(mTableName, null, condition.getWhereClause(), condition.getWhereArgs(),
                null, null, null, limitStr);
        return cursorToList(cursor, where);
    }

    private Map<String, String> entityToMap(T entity){
        HashMap<String, String> result = new HashMap<>();
        Iterator fieldsIterator = mCacheMap.values().iterator();
        while (fieldsIterator.hasNext()){
            Field field = (Field) fieldsIterator.next();
            String key = null;
            String value = null;
            Object fieldValue = null;
            DatabaseField fieldAnnotation = field.getAnnotation(DatabaseField.class);
            if(null != fieldAnnotation){
                key = fieldAnnotation.value();
            }else{
                key = field.getName();
            }

            try {
                fieldValue = field.get(entity);
                if(null == fieldValue){
                    continue;
                }
                value = fieldValue.toString();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }

            result.put(key, value);
        }
        return result;
    }

    private List<T> cursorToList(Cursor cursor, T where){
        List<T> result = new ArrayList<>();
        if(null != cursor){
            Object item;
            while (cursor.moveToNext()){

                try {
                    item = where.getClass().newInstance();
                    Iterator iterator= mCacheMap.entrySet().iterator();
                    while(iterator.hasNext()){
                        Map.Entry entry = (Map.Entry)iterator.next();
                        String columnName = (String) entry.getKey();
                        Integer columnIndex = cursor.getColumnIndex(columnName);

                        Field field = (Field)entry.getValue();
                        Class type = field.getType();
                        if (columnIndex != -1) {
                            if (type == String.class) {
                                field.set(item, cursor.getString(columnIndex));
                            }else if(type == Double.class){
                                field.set(item, cursor.getDouble(columnIndex));
                            }else if(type == Integer.class){
                                field.set(item, cursor.getInt(columnIndex));
                            }else if(type == Long.class){
                                field.set(item, cursor.getLong(columnIndex));
                            }else if(type == byte[].class){
                                field.set(item, cursor.getBlob(columnIndex));
                            }else{
                                //not supported type
                                continue;
                            }
                        }
                    }
                    result.add((T)item);
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
            cursor.close();
        }
        return result;
    }

    protected abstract String getCreateTableSQL();

    class Condition {
        private String whereClause;
        private String[] whereArgs;

        public Condition(Map<String, String> whereClause){
            StringBuilder whereClauseBuilder = new StringBuilder();
            ArrayList<String> values = new ArrayList<>();
            whereClauseBuilder.append("1=1 ");
            Set<String> keys = whereClause.keySet();
            Iterator iterator = keys.iterator();
            while (iterator.hasNext()){
                String key = (String)iterator.next();
                String value = whereClause.get(key);

                whereClauseBuilder.append("and "+key+"=?");
                values.add(value);
            }

            this.whereClause = whereClauseBuilder.toString();
            this.whereArgs = values.toArray(new String[values.size()]);
        }

        public String getWhereClause() {
            return whereClause;
        }

        public String[] getWhereArgs() {
            return whereArgs;
        }
    }
}
