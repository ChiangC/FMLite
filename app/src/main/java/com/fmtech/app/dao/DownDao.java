package com.fmtech.app.dao;


import com.fmtech.fmlite.db.BaseDao;

public class DownDao extends BaseDao {
    @Override
    protected String getCreateTableSQL() {
        return "create table if not exists tb_down(tb_time varchar(20),tb_path varchar(10))";

    }

}
