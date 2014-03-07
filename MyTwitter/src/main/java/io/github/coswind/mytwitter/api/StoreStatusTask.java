package io.github.coswind.mytwitter.api;

import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

import java.util.ArrayList;

import io.github.coswind.mytwitter.constant.CacheConstants;
import io.github.coswind.mytwitter.dao.StatusDao;
import io.github.coswind.mytwitter.dao.TwitterStatus;
import twitter4j.Twitter;

/**
 * Created by coswind on 14-2-20.
 */
public class StoreStatusTask extends AsyncTask<ArrayList<TwitterStatus>, Void, Void> {
    private StatusDao statusDao;
    private SQLiteDatabase sqLiteDatabase;
    private boolean fromTop;
    private ArrayList<TwitterStatus> dbStatusList;

    public StoreStatusTask(StatusDao statusDao, SQLiteDatabase sqLiteDatabase, boolean fromTop, ArrayList<TwitterStatus> dbStatusList) {
        this.statusDao = statusDao;
        this.sqLiteDatabase = sqLiteDatabase;
        this.fromTop = fromTop;
        this.dbStatusList = dbStatusList;
    }

    @Override
    protected Void doInBackground(ArrayList<TwitterStatus>... params) {
        ArrayList<TwitterStatus> statuses = params[0];
        long dbCount = dbStatusList.size();

        if (fromTop) {
            statusDao.insertInTx(statuses);
            if (dbCount + statuses.size() > CacheConstants.DEFAULT_DATABASE_ITEM_LIMIT) {
                sqLiteDatabase.delete(statusDao.getTablename(), StatusDao.Properties.StatusId.columnName + "<?",
                        new String[]{String.valueOf(dbStatusList.get(
                                CacheConstants.DEFAULT_DATABASE_ITEM_LIMIT - statuses.size()).getId())});
            }
        } else {
            ArrayList<TwitterStatus> statusArrayList = new ArrayList<TwitterStatus>();
            int insertLen = Math.min(CacheConstants.DEFAULT_DATABASE_ITEM_LIMIT - dbStatusList.size(), statuses.size());
            for (int i = 0; i < insertLen; i++) {
                statusArrayList.add(statuses.get(i));
            }
            statusDao.insertInTx(statusArrayList);
        }

        return null;
    }
}
