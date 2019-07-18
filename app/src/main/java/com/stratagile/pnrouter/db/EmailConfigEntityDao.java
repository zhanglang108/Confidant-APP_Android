package com.stratagile.pnrouter.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "EMAIL_CONFIG_ENTITY".
*/
public class EmailConfigEntityDao extends AbstractDao<EmailConfigEntity, Long> {

    public static final String TABLENAME = "EMAIL_CONFIG_ENTITY";

    /**
     * Properties of entity EmailConfigEntity.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property SmtpPort = new Property(1, int.class, "smtpPort", false, "SMTP_PORT");
        public final static Property PopPort = new Property(2, int.class, "popPort", false, "POP_PORT");
        public final static Property ImapPort = new Property(3, int.class, "imapPort", false, "IMAP_PORT");
        public final static Property SmtpHost = new Property(4, String.class, "smtpHost", false, "SMTP_HOST");
        public final static Property PopHost = new Property(5, String.class, "popHost", false, "POP_HOST");
        public final static Property ImapHost = new Property(6, String.class, "imapHost", false, "IMAP_HOST");
        public final static Property Account = new Property(7, String.class, "account", false, "ACCOUNT");
        public final static Property Password = new Property(8, String.class, "password", false, "PASSWORD");
        public final static Property UnReadCount = new Property(9, int.class, "unReadCount", false, "UN_READ_COUNT");
        public final static Property GarbageCount = new Property(10, int.class, "garbageCount", false, "GARBAGE_COUNT");
        public final static Property UnReadMenu = new Property(11, String.class, "unReadMenu", false, "UN_READ_MENU");
        public final static Property StarMenu = new Property(12, String.class, "starMenu", false, "STAR_MENU");
        public final static Property DrafMenu = new Property(13, String.class, "drafMenu", false, "DRAF_MENU");
        public final static Property SendMenu = new Property(14, String.class, "sendMenu", false, "SEND_MENU");
        public final static Property GarbageMenu = new Property(15, String.class, "garbageMenu", false, "GARBAGE_MENU");
        public final static Property DeleteMenu = new Property(16, String.class, "deleteMenu", false, "DELETE_MENU");
        public final static Property IsChoose = new Property(17, Boolean.class, "isChoose", false, "IS_CHOOSE");
    }


    public EmailConfigEntityDao(DaoConfig config) {
        super(config);
    }
    
    public EmailConfigEntityDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"EMAIL_CONFIG_ENTITY\" (" + //
                "\"_id\" INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: id
                "\"SMTP_PORT\" INTEGER NOT NULL ," + // 1: smtpPort
                "\"POP_PORT\" INTEGER NOT NULL ," + // 2: popPort
                "\"IMAP_PORT\" INTEGER NOT NULL ," + // 3: imapPort
                "\"SMTP_HOST\" TEXT," + // 4: smtpHost
                "\"POP_HOST\" TEXT," + // 5: popHost
                "\"IMAP_HOST\" TEXT," + // 6: imapHost
                "\"ACCOUNT\" TEXT," + // 7: account
                "\"PASSWORD\" TEXT," + // 8: password
                "\"UN_READ_COUNT\" INTEGER NOT NULL ," + // 9: unReadCount
                "\"GARBAGE_COUNT\" INTEGER NOT NULL ," + // 10: garbageCount
                "\"UN_READ_MENU\" TEXT," + // 11: unReadMenu
                "\"STAR_MENU\" TEXT," + // 12: starMenu
                "\"DRAF_MENU\" TEXT," + // 13: drafMenu
                "\"SEND_MENU\" TEXT," + // 14: sendMenu
                "\"GARBAGE_MENU\" TEXT," + // 15: garbageMenu
                "\"DELETE_MENU\" TEXT," + // 16: deleteMenu
                "\"IS_CHOOSE\" INTEGER);"); // 17: isChoose
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"EMAIL_CONFIG_ENTITY\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, EmailConfigEntity entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
        stmt.bindLong(2, entity.getSmtpPort());
        stmt.bindLong(3, entity.getPopPort());
        stmt.bindLong(4, entity.getImapPort());
 
        String smtpHost = entity.getSmtpHost();
        if (smtpHost != null) {
            stmt.bindString(5, smtpHost);
        }
 
        String popHost = entity.getPopHost();
        if (popHost != null) {
            stmt.bindString(6, popHost);
        }
 
        String imapHost = entity.getImapHost();
        if (imapHost != null) {
            stmt.bindString(7, imapHost);
        }
 
        String account = entity.getAccount();
        if (account != null) {
            stmt.bindString(8, account);
        }
 
        String password = entity.getPassword();
        if (password != null) {
            stmt.bindString(9, password);
        }
        stmt.bindLong(10, entity.getUnReadCount());
        stmt.bindLong(11, entity.getGarbageCount());
 
        String unReadMenu = entity.getUnReadMenu();
        if (unReadMenu != null) {
            stmt.bindString(12, unReadMenu);
        }
 
        String starMenu = entity.getStarMenu();
        if (starMenu != null) {
            stmt.bindString(13, starMenu);
        }
 
        String drafMenu = entity.getDrafMenu();
        if (drafMenu != null) {
            stmt.bindString(14, drafMenu);
        }
 
        String sendMenu = entity.getSendMenu();
        if (sendMenu != null) {
            stmt.bindString(15, sendMenu);
        }
 
        String garbageMenu = entity.getGarbageMenu();
        if (garbageMenu != null) {
            stmt.bindString(16, garbageMenu);
        }
 
        String deleteMenu = entity.getDeleteMenu();
        if (deleteMenu != null) {
            stmt.bindString(17, deleteMenu);
        }
 
        Boolean isChoose = entity.getIsChoose();
        if (isChoose != null) {
            stmt.bindLong(18, isChoose ? 1L: 0L);
        }
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, EmailConfigEntity entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
        stmt.bindLong(2, entity.getSmtpPort());
        stmt.bindLong(3, entity.getPopPort());
        stmt.bindLong(4, entity.getImapPort());
 
        String smtpHost = entity.getSmtpHost();
        if (smtpHost != null) {
            stmt.bindString(5, smtpHost);
        }
 
        String popHost = entity.getPopHost();
        if (popHost != null) {
            stmt.bindString(6, popHost);
        }
 
        String imapHost = entity.getImapHost();
        if (imapHost != null) {
            stmt.bindString(7, imapHost);
        }
 
        String account = entity.getAccount();
        if (account != null) {
            stmt.bindString(8, account);
        }
 
        String password = entity.getPassword();
        if (password != null) {
            stmt.bindString(9, password);
        }
        stmt.bindLong(10, entity.getUnReadCount());
        stmt.bindLong(11, entity.getGarbageCount());
 
        String unReadMenu = entity.getUnReadMenu();
        if (unReadMenu != null) {
            stmt.bindString(12, unReadMenu);
        }
 
        String starMenu = entity.getStarMenu();
        if (starMenu != null) {
            stmt.bindString(13, starMenu);
        }
 
        String drafMenu = entity.getDrafMenu();
        if (drafMenu != null) {
            stmt.bindString(14, drafMenu);
        }
 
        String sendMenu = entity.getSendMenu();
        if (sendMenu != null) {
            stmt.bindString(15, sendMenu);
        }
 
        String garbageMenu = entity.getGarbageMenu();
        if (garbageMenu != null) {
            stmt.bindString(16, garbageMenu);
        }
 
        String deleteMenu = entity.getDeleteMenu();
        if (deleteMenu != null) {
            stmt.bindString(17, deleteMenu);
        }
 
        Boolean isChoose = entity.getIsChoose();
        if (isChoose != null) {
            stmt.bindLong(18, isChoose ? 1L: 0L);
        }
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    @Override
    public EmailConfigEntity readEntity(Cursor cursor, int offset) {
        EmailConfigEntity entity = new EmailConfigEntity( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.getInt(offset + 1), // smtpPort
            cursor.getInt(offset + 2), // popPort
            cursor.getInt(offset + 3), // imapPort
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // smtpHost
            cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5), // popHost
            cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6), // imapHost
            cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7), // account
            cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8), // password
            cursor.getInt(offset + 9), // unReadCount
            cursor.getInt(offset + 10), // garbageCount
            cursor.isNull(offset + 11) ? null : cursor.getString(offset + 11), // unReadMenu
            cursor.isNull(offset + 12) ? null : cursor.getString(offset + 12), // starMenu
            cursor.isNull(offset + 13) ? null : cursor.getString(offset + 13), // drafMenu
            cursor.isNull(offset + 14) ? null : cursor.getString(offset + 14), // sendMenu
            cursor.isNull(offset + 15) ? null : cursor.getString(offset + 15), // garbageMenu
            cursor.isNull(offset + 16) ? null : cursor.getString(offset + 16), // deleteMenu
            cursor.isNull(offset + 17) ? null : cursor.getShort(offset + 17) != 0 // isChoose
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, EmailConfigEntity entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setSmtpPort(cursor.getInt(offset + 1));
        entity.setPopPort(cursor.getInt(offset + 2));
        entity.setImapPort(cursor.getInt(offset + 3));
        entity.setSmtpHost(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setPopHost(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
        entity.setImapHost(cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6));
        entity.setAccount(cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7));
        entity.setPassword(cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8));
        entity.setUnReadCount(cursor.getInt(offset + 9));
        entity.setGarbageCount(cursor.getInt(offset + 10));
        entity.setUnReadMenu(cursor.isNull(offset + 11) ? null : cursor.getString(offset + 11));
        entity.setStarMenu(cursor.isNull(offset + 12) ? null : cursor.getString(offset + 12));
        entity.setDrafMenu(cursor.isNull(offset + 13) ? null : cursor.getString(offset + 13));
        entity.setSendMenu(cursor.isNull(offset + 14) ? null : cursor.getString(offset + 14));
        entity.setGarbageMenu(cursor.isNull(offset + 15) ? null : cursor.getString(offset + 15));
        entity.setDeleteMenu(cursor.isNull(offset + 16) ? null : cursor.getString(offset + 16));
        entity.setIsChoose(cursor.isNull(offset + 17) ? null : cursor.getShort(offset + 17) != 0);
     }
    
    @Override
    protected final Long updateKeyAfterInsert(EmailConfigEntity entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(EmailConfigEntity entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(EmailConfigEntity entity) {
        return entity.getId() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}