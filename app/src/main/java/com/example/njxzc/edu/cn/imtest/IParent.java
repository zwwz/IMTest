package com.example.njxzc.edu.cn.imtest;

import android.support.v4.app.Fragment;

import com.alibaba.mobileim.channel.cloud.contact.YWProfileInfo;

/**
 * Created by wangyan on 2018/3/30.
 */

public interface IParent {

    //Fragment跳转相关

    public void addFragment(Fragment fragment, boolean addToBackStack);

    public void finish(boolean POP_BACK_STACK_INCLUSIVE);

    public YWProfileInfo getYWProfileInfo();

    public void setYWProfileInfo(YWProfileInfo ywProfileInfo) ;

    public boolean isHasContactAlready();

    public void setHasContactAlready(boolean hasContactAlready);
}
