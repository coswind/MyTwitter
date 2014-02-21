package io.github.coswind.mytwitter.sp;

import android.content.Context;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import io.github.coswind.mytwitter.model.Account;

/**
 * Created by coswind on 14-2-19.
 */
public class AccountSpUtils extends BaseSpUtils {
    public final static String ACCOUNT = "account";

    private static AccountSpUtils accountSpUtils;

    private AccountSpUtils() {
        spFileName = "account";
    }

    public static AccountSpUtils getInstance() {
        if (accountSpUtils == null) {
            accountSpUtils = new AccountSpUtils();
        }
        return accountSpUtils;
    }

    public void setAccount(Context context, Account account) {
        putString(context, ACCOUNT, JSONObject.toJSONString(account));
    }

    public Account getAccount(Context context) {
        Account account = null;
        try {
            String jsonString = getString(context, ACCOUNT);

            if (!TextUtils.isEmpty(jsonString)) {
                account = JSON.parseObject(jsonString, Account.class);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return account;
    }
}
