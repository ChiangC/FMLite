package com.fmtech.app.dao;

import com.fmtech.fmlite.db.BaseDao;

import java.util.List;

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

public class UserDao<User> extends BaseDao<User> {
    @Override
    protected String getCreateTableSQL() {
        return "create table if not exists tb_user(user_Id int,name varchar(20),password varchar(10))";
    }
}
