package com.fmtech.app;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.fmtech.app.dao.DownDao;
import com.fmtech.app.dao.UserDao;
import com.fmtech.app.entity.DownFile;
import com.fmtech.app.entity.User;
import com.fmtech.fmlite.db.BaseDaoFactory;
import com.fmtech.fmlite.db.IBaseDao;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "FMLite";

    IBaseDao<User> baseDao;
    IBaseDao<DownFile> fileDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkPermission();


        findViewById(R.id.deleteUser).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteUser();
            }
        });

    }

    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 100);
        } else {
            baseDao = BaseDaoFactory.getInstance().getDataHelper(UserDao.class, User.class);
//            fileDao=BaseDaoFactory.getInstance().getDataHelper(DownDao.class,DownFile.class);
        }
    }

    public void save(View view) {
//        for (int i=0;i<20;i++)
//        {
//            DownFile user=new DownFile("2016-1-11","123456");
//            fileDao.insert(user);
//        }
        for (int i = 0; i < 5; i++) {
            User user = new User(i, "Andrew", "123456");
            baseDao.insert(user);
        }
    }

    public void deleteUser() {
        User user = new User();
        user.setName("David");
        baseDao.delete(user);
    }

    public void update(View view) {
        User where = new User();
        where.setName("Andrew");

        User user = new User(1, "David", "123456789");
        baseDao.update(user, where);
    }

    public void queryList(View view) {
//         DownFile downFile=new DownFile();
//        downFile.setTime("2016-1-11");
//        List<DownFile> list=fileDao.query(downFile);
//        for (DownFile down:list)
//        {
//            Log.i(TAG,down.getPath()+"  time  "+down.getTime());
//        }
        User where = new User();
        where.setName("Andrew");
        where.setUser_Id(2);
        List<User> list = baseDao.query(where);
        Log.i(TAG, "查询到 " + list.size() + " 条数据:"+list.toString());
    }

}
