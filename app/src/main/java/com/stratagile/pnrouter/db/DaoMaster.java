package com.stratagile.pnrouter.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.util.Log;

import org.greenrobot.greendao.AbstractDaoMaster;
import org.greenrobot.greendao.database.StandardDatabase;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseOpenHelper;
import org.greenrobot.greendao.identityscope.IdentityScopeType;


// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/**
 * Master of DAO (schema version 71): knows all DAOs.
 */
public class DaoMaster extends AbstractDaoMaster {
    public static final int SCHEMA_VERSION = 71;

    /** Creates underlying database table using DAOs. */
    public static void createAllTables(Database db, boolean ifNotExists) {
        DraftEntityDao.createTable(db, ifNotExists);
        EmailAttachEntityDao.createTable(db, ifNotExists);
        EmailConfigEntityDao.createTable(db, ifNotExists);
        EmailContactsEntityDao.createTable(db, ifNotExists);
        EmailMessageEntityDao.createTable(db, ifNotExists);
        FriendEntityDao.createTable(db, ifNotExists);
        GroupEntityDao.createTable(db, ifNotExists);
        GroupVerifyEntityDao.createTable(db, ifNotExists);
        MessageEntityDao.createTable(db, ifNotExists);
        RecentFileDao.createTable(db, ifNotExists);
        RouterEntityDao.createTable(db, ifNotExists);
        RouterUserEntityDao.createTable(db, ifNotExists);
        UserEntityDao.createTable(db, ifNotExists);
    }

    /** Drops underlying database table using DAOs. */
    public static void dropAllTables(Database db, boolean ifExists) {
        DraftEntityDao.dropTable(db, ifExists);
        EmailAttachEntityDao.dropTable(db, ifExists);
        EmailConfigEntityDao.dropTable(db, ifExists);
        EmailContactsEntityDao.dropTable(db, ifExists);
        EmailMessageEntityDao.dropTable(db, ifExists);
        FriendEntityDao.dropTable(db, ifExists);
        GroupEntityDao.dropTable(db, ifExists);
        GroupVerifyEntityDao.dropTable(db, ifExists);
        MessageEntityDao.dropTable(db, ifExists);
        RecentFileDao.dropTable(db, ifExists);
        RouterEntityDao.dropTable(db, ifExists);
        RouterUserEntityDao.dropTable(db, ifExists);
        UserEntityDao.dropTable(db, ifExists);
    }

    /**
     * WARNING: Drops all table on Upgrade! Use only during development.
     * Convenience method using a {@link DevOpenHelper}.
     */
    public static DaoSession newDevSession(Context context, String name) {
        Database db = new DevOpenHelper(context, name).getWritableDb();
        DaoMaster daoMaster = new DaoMaster(db);
        return daoMaster.newSession();
    }

    public DaoMaster(SQLiteDatabase db) {
        this(new StandardDatabase(db));
    }

    public DaoMaster(Database db) {
        super(db, SCHEMA_VERSION);
        registerDaoClass(DraftEntityDao.class);
        registerDaoClass(EmailAttachEntityDao.class);
        registerDaoClass(EmailConfigEntityDao.class);
        registerDaoClass(EmailContactsEntityDao.class);
        registerDaoClass(EmailMessageEntityDao.class);
        registerDaoClass(FriendEntityDao.class);
        registerDaoClass(GroupEntityDao.class);
        registerDaoClass(GroupVerifyEntityDao.class);
        registerDaoClass(MessageEntityDao.class);
        registerDaoClass(RecentFileDao.class);
        registerDaoClass(RouterEntityDao.class);
        registerDaoClass(RouterUserEntityDao.class);
        registerDaoClass(UserEntityDao.class);
    }

    public DaoSession newSession() {
        return new DaoSession(db, IdentityScopeType.Session, daoConfigMap);
    }

    public DaoSession newSession(IdentityScopeType type) {
        return new DaoSession(db, type, daoConfigMap);
    }

    /**
     * Calls {@link #createAllTables(Database, boolean)} in {@link #onCreate(Database)} -
     */
    public static abstract class OpenHelper extends DatabaseOpenHelper {
        public OpenHelper(Context context, String name) {
            super(context, name, SCHEMA_VERSION);
        }

        public OpenHelper(Context context, String name, CursorFactory factory) {
            super(context, name, factory, SCHEMA_VERSION);
        }

        @Override
        public void onCreate(Database db) {
            Log.i("greenDAO", "Creating tables for schema version " + SCHEMA_VERSION);
            createAllTables(db, false);
        }
    }

    /** WARNING: Drops all table on Upgrade! Use only during development. */
    public static class DevOpenHelper extends OpenHelper {
        public DevOpenHelper(Context context, String name) {
            super(context, name);
        }

        public DevOpenHelper(Context context, String name, CursorFactory factory) {
            super(context, name, factory);
        }

        @Override
        public void onUpgrade(Database db, int oldVersion, int newVersion) {
            Log.i("greenDAO", "Upgrading schema from version " + oldVersion + " to " + newVersion + " by dropping all tables");
            dropAllTables(db, true);
            onCreate(db);
        }
    }

}
