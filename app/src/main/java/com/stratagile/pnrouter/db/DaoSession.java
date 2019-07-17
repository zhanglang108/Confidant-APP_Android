package com.stratagile.pnrouter.db;

import java.util.Map;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.AbstractDaoSession;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.identityscope.IdentityScopeType;
import org.greenrobot.greendao.internal.DaoConfig;

import com.stratagile.pnrouter.db.DraftEntity;
import com.stratagile.pnrouter.db.EmailConfigEntity;
import com.stratagile.pnrouter.db.EmailMessageEntity;
import com.stratagile.pnrouter.db.FriendEntity;
import com.stratagile.pnrouter.db.GroupEntity;
import com.stratagile.pnrouter.db.GroupVerifyEntity;
import com.stratagile.pnrouter.db.MessageEntity;
import com.stratagile.pnrouter.db.RecentFile;
import com.stratagile.pnrouter.db.RouterEntity;
import com.stratagile.pnrouter.db.RouterUserEntity;
import com.stratagile.pnrouter.db.UserEntity;
import com.stratagile.pnrouter.db.EmailAttachEntity;

import com.stratagile.pnrouter.db.DraftEntityDao;
import com.stratagile.pnrouter.db.EmailConfigEntityDao;
import com.stratagile.pnrouter.db.EmailMessageEntityDao;
import com.stratagile.pnrouter.db.FriendEntityDao;
import com.stratagile.pnrouter.db.GroupEntityDao;
import com.stratagile.pnrouter.db.GroupVerifyEntityDao;
import com.stratagile.pnrouter.db.MessageEntityDao;
import com.stratagile.pnrouter.db.RecentFileDao;
import com.stratagile.pnrouter.db.RouterEntityDao;
import com.stratagile.pnrouter.db.RouterUserEntityDao;
import com.stratagile.pnrouter.db.UserEntityDao;
import com.stratagile.pnrouter.db.EmailAttachEntityDao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * {@inheritDoc}
 * 
 * @see org.greenrobot.greendao.AbstractDaoSession
 */
public class DaoSession extends AbstractDaoSession {

    private final DaoConfig draftEntityDaoConfig;
    private final DaoConfig emailConfigEntityDaoConfig;
    private final DaoConfig emailMessageEntityDaoConfig;
    private final DaoConfig friendEntityDaoConfig;
    private final DaoConfig groupEntityDaoConfig;
    private final DaoConfig groupVerifyEntityDaoConfig;
    private final DaoConfig messageEntityDaoConfig;
    private final DaoConfig recentFileDaoConfig;
    private final DaoConfig routerEntityDaoConfig;
    private final DaoConfig routerUserEntityDaoConfig;
    private final DaoConfig userEntityDaoConfig;
    private final DaoConfig emailAttachEntityDaoConfig;

    private final DraftEntityDao draftEntityDao;
    private final EmailConfigEntityDao emailConfigEntityDao;
    private final EmailMessageEntityDao emailMessageEntityDao;
    private final FriendEntityDao friendEntityDao;
    private final GroupEntityDao groupEntityDao;
    private final GroupVerifyEntityDao groupVerifyEntityDao;
    private final MessageEntityDao messageEntityDao;
    private final RecentFileDao recentFileDao;
    private final RouterEntityDao routerEntityDao;
    private final RouterUserEntityDao routerUserEntityDao;
    private final UserEntityDao userEntityDao;
    private final EmailAttachEntityDao emailAttachEntityDao;

    public DaoSession(Database db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        draftEntityDaoConfig = daoConfigMap.get(DraftEntityDao.class).clone();
        draftEntityDaoConfig.initIdentityScope(type);

        emailConfigEntityDaoConfig = daoConfigMap.get(EmailConfigEntityDao.class).clone();
        emailConfigEntityDaoConfig.initIdentityScope(type);

        emailMessageEntityDaoConfig = daoConfigMap.get(EmailMessageEntityDao.class).clone();
        emailMessageEntityDaoConfig.initIdentityScope(type);

        friendEntityDaoConfig = daoConfigMap.get(FriendEntityDao.class).clone();
        friendEntityDaoConfig.initIdentityScope(type);

        groupEntityDaoConfig = daoConfigMap.get(GroupEntityDao.class).clone();
        groupEntityDaoConfig.initIdentityScope(type);

        groupVerifyEntityDaoConfig = daoConfigMap.get(GroupVerifyEntityDao.class).clone();
        groupVerifyEntityDaoConfig.initIdentityScope(type);

        messageEntityDaoConfig = daoConfigMap.get(MessageEntityDao.class).clone();
        messageEntityDaoConfig.initIdentityScope(type);

        recentFileDaoConfig = daoConfigMap.get(RecentFileDao.class).clone();
        recentFileDaoConfig.initIdentityScope(type);

        routerEntityDaoConfig = daoConfigMap.get(RouterEntityDao.class).clone();
        routerEntityDaoConfig.initIdentityScope(type);

        routerUserEntityDaoConfig = daoConfigMap.get(RouterUserEntityDao.class).clone();
        routerUserEntityDaoConfig.initIdentityScope(type);

        userEntityDaoConfig = daoConfigMap.get(UserEntityDao.class).clone();
        userEntityDaoConfig.initIdentityScope(type);

        emailAttachEntityDaoConfig = daoConfigMap.get(EmailAttachEntityDao.class).clone();
        emailAttachEntityDaoConfig.initIdentityScope(type);

        draftEntityDao = new DraftEntityDao(draftEntityDaoConfig, this);
        emailConfigEntityDao = new EmailConfigEntityDao(emailConfigEntityDaoConfig, this);
        emailMessageEntityDao = new EmailMessageEntityDao(emailMessageEntityDaoConfig, this);
        friendEntityDao = new FriendEntityDao(friendEntityDaoConfig, this);
        groupEntityDao = new GroupEntityDao(groupEntityDaoConfig, this);
        groupVerifyEntityDao = new GroupVerifyEntityDao(groupVerifyEntityDaoConfig, this);
        messageEntityDao = new MessageEntityDao(messageEntityDaoConfig, this);
        recentFileDao = new RecentFileDao(recentFileDaoConfig, this);
        routerEntityDao = new RouterEntityDao(routerEntityDaoConfig, this);
        routerUserEntityDao = new RouterUserEntityDao(routerUserEntityDaoConfig, this);
        userEntityDao = new UserEntityDao(userEntityDaoConfig, this);
        emailAttachEntityDao = new EmailAttachEntityDao(emailAttachEntityDaoConfig, this);

        registerDao(DraftEntity.class, draftEntityDao);
        registerDao(EmailConfigEntity.class, emailConfigEntityDao);
        registerDao(EmailMessageEntity.class, emailMessageEntityDao);
        registerDao(FriendEntity.class, friendEntityDao);
        registerDao(GroupEntity.class, groupEntityDao);
        registerDao(GroupVerifyEntity.class, groupVerifyEntityDao);
        registerDao(MessageEntity.class, messageEntityDao);
        registerDao(RecentFile.class, recentFileDao);
        registerDao(RouterEntity.class, routerEntityDao);
        registerDao(RouterUserEntity.class, routerUserEntityDao);
        registerDao(UserEntity.class, userEntityDao);
        registerDao(EmailAttachEntity.class, emailAttachEntityDao);
    }
    
    public void clear() {
        draftEntityDaoConfig.clearIdentityScope();
        emailConfigEntityDaoConfig.clearIdentityScope();
        emailMessageEntityDaoConfig.clearIdentityScope();
        friendEntityDaoConfig.clearIdentityScope();
        groupEntityDaoConfig.clearIdentityScope();
        groupVerifyEntityDaoConfig.clearIdentityScope();
        messageEntityDaoConfig.clearIdentityScope();
        recentFileDaoConfig.clearIdentityScope();
        routerEntityDaoConfig.clearIdentityScope();
        routerUserEntityDaoConfig.clearIdentityScope();
        userEntityDaoConfig.clearIdentityScope();
        emailAttachEntityDaoConfig.clearIdentityScope();
    }

    public DraftEntityDao getDraftEntityDao() {
        return draftEntityDao;
    }

    public EmailConfigEntityDao getEmailConfigEntityDao() {
        return emailConfigEntityDao;
    }

    public EmailMessageEntityDao getEmailMessageEntityDao() {
        return emailMessageEntityDao;
    }

    public FriendEntityDao getFriendEntityDao() {
        return friendEntityDao;
    }

    public GroupEntityDao getGroupEntityDao() {
        return groupEntityDao;
    }

    public GroupVerifyEntityDao getGroupVerifyEntityDao() {
        return groupVerifyEntityDao;
    }

    public MessageEntityDao getMessageEntityDao() {
        return messageEntityDao;
    }

    public RecentFileDao getRecentFileDao() {
        return recentFileDao;
    }

    public RouterEntityDao getRouterEntityDao() {
        return routerEntityDao;
    }

    public RouterUserEntityDao getRouterUserEntityDao() {
        return routerUserEntityDao;
    }

    public UserEntityDao getUserEntityDao() {
        return userEntityDao;
    }

    public EmailAttachEntityDao getEmailAttachEntityDao() {
        return emailAttachEntityDao;
    }

}
