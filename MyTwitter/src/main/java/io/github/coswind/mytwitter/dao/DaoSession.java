package io.github.coswind.mytwitter.dao;

import android.database.sqlite.SQLiteDatabase;

import java.util.Map;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.AbstractDaoSession;
import de.greenrobot.dao.identityscope.IdentityScopeType;
import de.greenrobot.dao.internal.DaoConfig;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * {@inheritDoc}
 * 
 * @see de.greenrobot.dao.AbstractDaoSession
 */
public class DaoSession extends AbstractDaoSession {

    private final DaoConfig statusDaoConfig;

    private final StatusDao statusDao;

    public DaoSession(SQLiteDatabase db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        statusDaoConfig = daoConfigMap.get(StatusDao.class).clone();
        statusDaoConfig.initIdentityScope(type);

        statusDao = new StatusDao(statusDaoConfig, this);

        registerDao(TwitterStatus.class, statusDao);
    }
    
    public void clear() {
        statusDaoConfig.getIdentityScope().clear();
    }

    public StatusDao getStatusDao() {
        return statusDao;
    }

}
