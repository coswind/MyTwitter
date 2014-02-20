package io.github.coswind.mytwitter.utils;

import android.content.Context;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import io.github.coswind.mytwitter.model.Account;
import io.github.coswind.mytwitter.sp.BaseSpUtils;

/**
 * Created by coswind on 14-2-19.
 */
public class AccountUtils extends BaseSpUtils {
    public final static String ACCOUNT = "account";

    private static AccountUtils accountUtils;

    private Account account;

    private AccountUtils() {
        spFileName = "account";
    }

    public static AccountUtils getInstance() {
        if (accountUtils == null) {
            accountUtils = new AccountUtils();
        }
        return accountUtils;
    }

    public void setAccount(Context context, Account account) {
        putString(context, ACCOUNT, JSONObject.toJSONString(account));
    }

    public Account getAccount(Context context) {
        if (account == null) {
            try {
                String jsonString = getString(context, ACCOUNT);

                if (!TextUtils.isEmpty(jsonString)) {
                    account = JSON.parseObject(jsonString, Account.class);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return account;
    }
}
