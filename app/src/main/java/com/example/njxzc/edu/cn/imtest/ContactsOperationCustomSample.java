package com.example.njxzc.edu.cn.imtest;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

import com.alibaba.mobileim.YWConstants;
import com.alibaba.mobileim.aop.Pointcut;
import com.alibaba.mobileim.aop.custom.IMContactsOperation;
import com.alibaba.mobileim.channel.event.IWxCallback;
import com.alibaba.mobileim.channel.util.YWLog;
import com.alibaba.mobileim.contact.IYWContact;
import com.alibaba.mobileim.contact.IYWContactService;
import com.alibaba.mobileim.contact.YWContactManager;
import com.alibaba.mobileim.conversation.EServiceContact;
import com.alibaba.mobileim.fundamental.widget.YWAlertDialog;
import com.alibaba.mobileim.ui.contact.util.RandomNameUtil;
import com.alibaba.mobileim.utility.IMNotificationUtils;

/**
 * 联系人界面业务的定制点(根据需要实现相应的接口来达到定制联系人界面的业务)，不设置则使用云旺默认的实现
 * 调用方设置的回调，必须继承BaseAdvice 根据不同的需求实现 不同的 开放的 Advice
 * com.alibaba.mobileim.aop.pointcuts包下开放了不同的Advice.通过实现多个接口，组合成对不同的界面的定制
 需要在application中将这个Advice绑定。设置以下代码
 * AdviceBinder.bindAdvice(PointCutEnum.CONTACTS_OP_POINTCUT, ContactsOperationCustomSample.class);
 *
 * @author shuheng
 */
public class ContactsOperationCustomSample extends IMContactsOperation {

    private String TAG=ContactsOperationCustomSample.class.getSimpleName();

    public ContactsOperationCustomSample(Pointcut pointcut) {
        super(pointcut);
    }

    /**
     * 是否同步联系人在线状态
     *
     * @param fragment 联系人页面fragment
     * @param context  联系人页面context
     * @return
     */
    @Override
    public boolean enableSyncContactOnlineStatus(Fragment fragment, Context context) {
        return true;
    }

    /**
     * 定制点击事件
     *
     * @param fragment
     * @param contact
     * @return true: 使用用户自定义点击事件；false：使用默认点击事件
     */
    @Override
    public boolean onListItemClick(Fragment fragment, IYWContact contact) {
        Intent intent =LoginSampleHelper.getInstance().getIMKit().getChattingActivityIntent(contact.getUserId(), contact.getAppKey());
        if(intent!=null)
            fragment.getActivity().startActivity(intent);
        return true;
    }

}
