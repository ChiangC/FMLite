package com.fmtech.app.entity;

import com.fmtech.fmlite.annotation.DatabaseField;
import com.fmtech.fmlite.annotation.DatabaseTable;

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

@DatabaseTable("tb_user")
public class User {
    public int user_Id=0;

    @DatabaseField("name")
    public String name;

    @DatabaseField("password")
    public String password;


    public Integer getUser_Id() {
        return user_Id;
    }

    public User( ) {

    }

    public User(Integer id, String name, String password) {
        user_Id= id;
        this.name = name;
        this.password = password;
    }

    public void setUser_Id(int user_Id) {
        this.user_Id = user_Id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "name  "+name+"  password "+password;
    }
}
