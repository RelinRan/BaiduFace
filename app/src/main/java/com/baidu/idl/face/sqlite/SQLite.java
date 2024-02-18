package com.baidu.idl.face.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 抽象类是基础utils在你扩展这个
 * 使用数据库时可以使用。在createTable()方法中创建表
 * 在upGradeDatabase()中更新你的表数据库
 */

public class SQLite implements OnSQLiteOpenListener {

    public static final String TAG = SQLite.class.getSimpleName();
    /**
     * 值是一个 NULL值
     */
    public static final String FIELD_TYPE_NULL = "NULL";
    /**
     * 有符号整形，根据值的大小以1,2,3,4,6或8字节存放
     */
    public static final String FIELD_TYPE_INTEGER = "INTEGER";
    /**
     * 浮点型值，以8字节IEEE浮点数存放
     */
    public static final String FIELD_TYPE_REAL = "REAL";
    /**
     * 数值型数据在被插入之前，需要先被转换为文本格式，之后再插入到目标字段中。
     * 文本字符串，使用数据库编码（UTF-8，UTF-16BE或者UTF-16LE）存放
     */
    public static final String FIELD_TYPE_TEXT = "TEXT";
    /**
     * 一个数据块，完全按照输入存放（即没有准换）
     */
    public static final String FIELD_TYPE_BLOB = "BLOB";
    /**
     * 当文本数据被插入到亲缘性为NUMERIC的字段中时，如果转换操作不会导致数据信息丢失以及完全可逆，
     * 那么SQLite就会将该文本数据转换为INTEGER或REAL类型的数据，如果转换失败，SQLite仍会以TEXT方式存储该数据。
     * 对于NULL或BLOB类型的新数据，SQLite将不做任何转换，直接以NULL或BLOB的方式存储该数据。需要额外说明的是，
     * 对于浮点格式的常量文本，如"30000.0"，如果该值可以转换为INTEGER同时又不会丢失数值信息，那么SQLite就会将其转换为INTEGER的存储方式。
     */
    public static final String FIELD_TYPE_NUMERIC = "numeric";

    private final int DATABASE_VERSION = 1;
    private final String DATABASE_NAME = "face.db";
    protected final String CREATE_TABLE_HEAD = "CREATE TABLE IF NOT EXISTS ";
    protected final String CREATE_PRIMARY_KEY = "TAB_ID INTEGER PRIMARY KEY AUTOINCREMENT,";

    private int version;
    private Context context;
    private SQLiteDatabase db;
    private String databaseName;
    private SQLiteHelper sqLiteOpen;
    private static SQLite sqLite;
    private OnSQLiteOpenListener listener;

    /**
     * 基础的数据库构造方法<br/>
     *
     * @param context 上下文
     */
    private SQLite(Context context) {
        this.context = context;
        databaseName = DATABASE_NAME;
        version = DATABASE_VERSION;
        if (sqLiteOpen == null) {
            sqLiteOpen = new SQLiteHelper(context, databaseName, null, version, this);
            db = sqLiteOpen.getWritableDatabase();
        }
        onCreate(db);
        if (new File(db.getPath()).exists()) {
            onCreate(db);
        }
    }

    /**
     * 自定义数据库名称及路劲和版本的构造方法
     *
     * @param context
     * @param databaseVersion
     */
    private SQLite(Context context, int databaseVersion) {
        this.context = context;
        databaseName = DATABASE_NAME;
        if (sqLiteOpen == null) {
            sqLiteOpen = new SQLiteHelper(context, databaseName, null, databaseVersion, this);
            db = sqLiteOpen.getWritableDatabase();
        }
        onCreate(db);
        if (new File(db.getPath()).exists()) {
            onCreate(db);
        }
    }

    /**
     * 自定义数据库名称及路劲和版本的构造方法
     *
     * @param context      上下文
     * @param databaseName 数据库名称
     * @param version      数据库版本
     */
    private SQLite(Context context, String databaseName, int version) {
        this.context = context;
        if (sqLiteOpen == null) {
            sqLiteOpen = new SQLiteHelper(context, databaseName, null, version, this);
            db = sqLiteOpen.getWritableDatabase();
        }
        onCreate(db);
        if (new File(db.getPath()).exists()) {
            onCreate(db);
        }
    }

    /**
     * 获取数据库对象
     *
     * @param context 上下文对象
     * @return
     */
    public static SQLite initialize(Context context) {
        if (sqLite == null) {
            synchronized (SQLite.class) {
                if (sqLite == null) {
                    sqLite = new SQLite(context);
                }
            }
        }
        return sqLite;
    }

    /**
     * 获取数据库助手
     *
     * @param context      上下文
     * @param databaseName 数据库名称
     * @param version      数据库版本号
     * @return
     */
    public static SQLite initialize(Context context, String databaseName, int version) {
        if (sqLite == null) {
            synchronized (SQLite.class) {
                if (sqLite == null) {
                    sqLite = new SQLite(context, databaseName, version);
                }
            }
        }
        return sqLite;
    }

    /**
     * 获取数据库助手
     *
     * @param context         上下文
     * @param databaseVersion 数据库版本号
     * @return
     */
    public static SQLite initialize(Context context, int databaseVersion) {
        if (sqLite == null) {
            synchronized (SQLite.class) {
                if (sqLite == null) {
                    sqLite = new SQLite(context, databaseVersion);
                }
            }
        }
        return sqLite;
    }

    /**
     * @return 数据库管理员
     */
    public static SQLite administrator() {
        if (sqLite == null) {
            String msg = "The SQLite database is not initialized, please initialize it in Application.";
            new RuntimeException(msg).printStackTrace();
            Log.e(TAG, msg);
        }
        return sqLite;
    }

    /**
     * @return 数据库操作对象
     */
    public SQLiteDatabase database() {
        return db;
    }

    /**
     * 设置数据库打开监听监听
     *
     * @param listener
     */
    public void setOnSQLiteOpenListener(OnSQLiteOpenListener listener) {
        this.listener = listener;
    }

    /**
     * 创建数据表
     * 例如: create table if not exists user (_id integer primary key autoincrement,user_name text,user_sex text,user_pwd text)
     *
     * @param db
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        if (listener != null) {
            listener.onCreate(db);
        }
    }

    /**
     * 升级数据库
     *
     * @param db         数据库对象
     * @param oldVersion 旧版本号
     * @param newVersion 新版本号
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (listener != null) {
            listener.onUpgrade(db, version, newVersion);
        }
    }

    /**
     * 建表
     *
     * @param table   表名
     * @param columns 列名
     * @return
     */
    public void create(String table, String[] columns) {
        if (TextUtils.isEmpty(table)) {
            return;
        }
        if (columns == null) {
            return;
        }
        if (columns.length == 0) {
            return;
        }
        StringBuffer sb = new StringBuffer();
        sb.append(CREATE_TABLE_HEAD + table);
        sb.append(" (");
        sb.append(CREATE_PRIMARY_KEY);
        for (String key : columns) {
            sb.append(key + " text");
            sb.append(",");
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.append(")");
        Log.i(TAG, "create table = " + sb.toString());
        db.execSQL(sb.toString());
    }

    /**
     * 建表
     *
     * @param table      表名
     * @param columns    列名
     * @param fieldTypes 数据类型
     * @return
     */
    public void create(String table, String[] columns, String[] fieldTypes) {
        if (TextUtils.isEmpty(table)) {
            return;
        }
        if (columns == null) {
            return;
        }
        if (columns.length == 0) {
            return;
        }
        StringBuffer sb = new StringBuffer();
        sb.append(CREATE_TABLE_HEAD + table);
        sb.append(" (");
        sb.append(CREATE_PRIMARY_KEY);
        for (int i = 0; i < columns.length; i++) {
            sb.append(columns[i] + " " + fieldTypes[i]);
            sb.append(",");
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.append(")");
        Log.i(TAG, "create table = " + sb.toString());
        db.execSQL(sb.toString());
    }

    /**
     * 创建表
     *
     * @param cls 类名
     */
    public void create(Class<?> cls) {
        if (cls == null) {
            return;
        }
        StringBuffer sb = new StringBuffer();
        sb.append(CREATE_TABLE_HEAD + cls.getSimpleName());
        sb.append(" (");
        sb.append(CREATE_PRIMARY_KEY);
        Field[] fields = cls.getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            field.setAccessible(true);
            String name = field.getName();
            if (!name.startsWith("$") && !name.equals("serialVersionUID")) {
                Class fieldType = field.getType();
                if (fieldType == int.class || fieldType == Integer.class || fieldType == short.class || fieldType == Short.class) {
                    sb.append(name + " " + FIELD_TYPE_INTEGER);
                } else if (fieldType == float.class || fieldType == Float.class || fieldType == double.class || fieldType == Double.class) {
                    sb.append(name + " " + FIELD_TYPE_REAL);
                } else if (fieldType == long.class || fieldType == Long.class) {
                    sb.append(name + " " + FIELD_TYPE_NUMERIC);
                } else {
                    sb.append(name + " " + FIELD_TYPE_TEXT);
                }
                sb.append(",");
            }
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.append(")");
        Log.i(TAG, "create table = " + sb.toString());
        db.execSQL(sb.toString());
    }

    /**
     * 插入数据
     *
     * @param table
     * @param contentValues
     * @return
     */
    public long insert(String table, ContentValues contentValues) {
        long result = -1;
        db.beginTransaction();
        try {
            result = db.insert(table, null, contentValues);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "insert exception = " + e.toString());
        }
        db.endTransaction();
        return result;
    }

    /**
     * 插入对象数据
     *
     * @param obj
     * @return 插入数据
     */
    public long insert(Object obj) {
        Class<?> cls = obj.getClass();
        Field[] fields = cls.getDeclaredFields();
        ContentValues contentValues = new ContentValues();
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            field.setAccessible(true);
            String key = field.getName();
            try {
                Class<?> fieldType = field.getType();
                if (!key.equals("$change") && !key.equals("serialVersionUID")) {
                    if (fieldType == String.class || fieldType == Character.class) {
                        contentValues.put(key, (String) field.get(obj));
                    }
                    if (fieldType == int.class || fieldType == Integer.class) {
                        contentValues.put(key, (int) field.get(obj));
                    }
                    if (fieldType == long.class || fieldType == Long.class) {
                        contentValues.put(key, (long) field.get(obj));
                    }
                    if (fieldType == double.class || fieldType == Double.class) {
                        contentValues.put(key, (double) field.get(obj));
                    }
                    if (fieldType == float.class || fieldType == Float.class) {
                        contentValues.put(key, (float) field.get(obj));
                    }
                    if (fieldType == boolean.class || fieldType == Boolean.class) {
                        contentValues.put(key, (boolean) field.get(obj));
                    }
                    if (fieldType == short.class || fieldType == Short.class) {
                        contentValues.put(key, (short) field.get(obj));
                    }
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return insert(cls.getSimpleName(), contentValues);
    }

    /**
     * 插入数据
     *
     * @param sql sql语句
     * @return
     */
    public void insert(String sql) {
        execSQL(sql);
    }

    /**
     * 删除数据
     *
     * @param table       表
     * @param whereClause 条件表达式，例如"name = ?"
     * @param whereArgs   条件值，例如new String[]{"Mary"}
     * @return 删除的条数
     */
    public int delete(String table, String whereClause, String[] whereArgs) {
        int result = -1;
        db.beginTransaction();
        try {
            result = db.delete(table, whereClause, whereArgs);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.i(TAG, "delete exception" + e.toString());
        }
        db.endTransaction();
        return result;
    }

    /**
     * 删除数据
     *
     * @param table       表
     * @param whereClause 条件表达式，例如"name = ?"
     * @param whereArgs   条件值，例如new String[]{"Mary"}
     * @return 删除的条数
     */
    public int delete(Class table, String whereClause, String[] whereArgs) {
        int result = -1;
        db.beginTransaction();
        try {
            result = db.delete(table.getSimpleName(), whereClause, whereArgs);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.i(TAG, "delete exception = " + e.toString());
        }
        db.endTransaction();
        return result;
    }

    /**
     * 删除数据
     *
     * @param sql sql语句
     * @return
     */
    public void delete(String sql) {
        execSQL(sql);
    }

    /**
     * 更新数据
     *
     * @param table         表
     * @param contentValues 更新值
     * @param whereClause   条件表达式，例如"name = ?"
     * @param whereArgs     条件值，例如new String[]{"Mary"}
     * @return
     */
    public int update(String table, ContentValues contentValues, String whereClause, String[] whereArgs) {
        int result = -1;
        db.beginTransaction();
        try {
            result = db.update(table, contentValues, whereClause, whereArgs);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.i(TAG, "update exception = " + e.toString());
        }
        db.endTransaction();
        return result;
    }

    /**
     * 更新数据
     *
     * @param obj         对象
     * @param whereClause 条件表达式，例如"name = ?"
     * @param whereArgs   条件值，例如new String[]{"Mary"}
     * @return
     */
    public int update(Object obj, String whereClause, String[] whereArgs) {
        int result = -1;
        db.beginTransaction();
        ContentValues contentValues = new ContentValues();
        Class cls = obj.getClass();
        Field[] fields = cls.getDeclaredFields();
        for (Field field : fields) {
            if (field != null) {
                field.setAccessible(true);
                String name = field.getName();
                String value = "";
                try {
                    value = String.valueOf(field.get(obj));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                if (!name.equals("$change") && !name.equals("serialVersionUID")) {
                    contentValues.put(name, value);
                }
            }
        }
        try {
            result = db.update(obj.getClass().getSimpleName(), contentValues, whereClause, whereArgs);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.i(TAG, "update exception = " + e.toString());
        }
        db.endTransaction();
        return result;
    }

    /**
     * 更新数据
     * 例如 update user set user_name = 'Jerry' where user_name = 'Mary'
     *
     * @param sql
     * @return
     */
    public void update(String sql) {
        execSQL(sql);
    }

    /**
     * 查询数据
     *
     * @param sql 数据库语句
     * @return
     */
    public List<Map<String, String>> query(String sql) {
        Cursor cursor = db.rawQuery(sql, null);
        String[] columnNames = cursor.getColumnNames();
        List<Map<String, String>> queryList = new ArrayList<>();
        while (cursor.moveToNext()) {
            Map<String, String> map = new HashMap<>();
            for (int i = 0; i < columnNames.length; i++) {
                map.put(columnNames[i], cursor.getString(cursor.getColumnIndex(columnNames[i])));
            }
            queryList.add(map);
        }
        cursor.close();
        return queryList;
    }

    /**
     * 查询数据
     *
     * @param clazz 实体类
     * @param <T>   实体类泛型
     * @return
     */
    public <T> List<T> query(Class<T> clazz) {
        return query(clazz, "select * from " + clazz.getSimpleName());
    }

    /**
     * 查询数据
     *
     * @param clazz 实体类
     * @param sql   sql语句
     * @param <T>   实体类泛型
     * @return 实体列表
     */
    public <T> List<T> query(Class<T> clazz, String sql) {
        Cursor cursor = db.rawQuery(sql, null);
        String[] columnNames = cursor.getColumnNames();
        List<T> list = new ArrayList<T>();
        while (cursor.moveToNext()) {
            T bean = null;
            try {
                bean = clazz.newInstance();
                for (int i = 0; i < columnNames.length; i++) {
                    Field field = findField(clazz, columnNames[i]);
                    if (field != null) {
                        field.setAccessible(true);
                        Class fieldType = field.getType();
                        if (fieldType == String.class || fieldType == CharSequence.class) {
                            field.set(bean, cursor.getString(cursor.getColumnIndex(columnNames[i])));
                        }
                        if (fieldType == long.class || fieldType == Long.class) {
                            field.set(bean, cursor.getLong(cursor.getColumnIndex(columnNames[i])));
                        }
                        if (fieldType == int.class || fieldType == Integer.class) {
                            field.set(bean, cursor.getInt(cursor.getColumnIndex(columnNames[i])));
                        }
                        if (fieldType == float.class || fieldType == Float.class) {
                            field.set(bean, cursor.getFloat(cursor.getColumnIndex(columnNames[i])));
                        }
                        if (fieldType == double.class || fieldType == Double.class) {
                            field.set(bean, cursor.getDouble(cursor.getColumnIndex(columnNames[i])));
                        }
                        if (fieldType == boolean.class || fieldType == Boolean.class) {
                            String value = cursor.getString(cursor.getColumnIndex(columnNames[i]));
                            if (value.equals("true") || value.equals("1")) {
                                field.set(bean, true);
                            } else {
                                field.set(bean, false);
                            }
                        }
                    }
                }
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } finally {
                if (bean != null) {
                    list.add(bean);
                }
            }
        }
        return list;
    }

    /**
     * @param clazz 类
     * @return 查找声明的字段
     */
    protected Field[] findDeclaredFields(Class clazz) {
        List<Field> fields = new ArrayList<>();
        while (clazz != null) {
            for (Field field : clazz.getDeclaredFields()) {
                field.setAccessible(true);
                fields.add(field);
            }
            clazz = clazz.getSuperclass();
        }
        return fields.toArray(new Field[fields.size()]);
    }

    /**
     * 查找存在的字段
     *
     * @param clazz 类
     * @param name  字段名
     * @return
     */
    protected Field findField(Class clazz, String name) {
        for (Field field : findDeclaredFields(clazz)) {
            if (field.getName().equals(name)) {
                return field;
            }
        }
        return null;
    }

    /**
     * 执行SQL语句
     *
     * @param sql sql语句
     */
    public void execSQL(String sql) {
        Log.i(TAG, "exec sql sql = " + sql);
        db.beginTransaction();
        try {
            db.execSQL(sql);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "exec sql exception = " + e.toString());
        }
        db.endTransaction();
    }

    /**
     * 删除表
     *
     * @param table 数据表
     */
    public void dropTable(String table) {
        db.beginTransaction();
        String sql = "drop table if exists " + table;
        try {
            db.execSQL(sql);
            Log.i(TAG, "drop table sql = " + sql);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, TAG + "exec sql exception =" + e.toString());
        }
        db.endTransaction();
    }

    /**
     * 删除表中的数据
     *
     * @param table
     */
    public void deleteTable(Class table) {
        deleteTable(table.getSimpleName());
    }

    /**
     * 清除表中数据
     *
     * @param table 数据表
     */
    public void deleteTable(String table) {
        db.beginTransaction();
        //除去表内的数据，但并不删除表本身
        String sql = "delete from " + table;
        try {
            db.execSQL(sql);
            Log.i(TAG, "drop table sql = " + sql);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "exec sql exception = " + e.toString());
        }
        db.endTransaction();
    }

    /**
     * 删除数据库
     */
    public void deleteDatabase() {
        context.deleteDatabase(sqLiteOpen.getDatabaseName());
        Log.i(TAG, "drop database database Name = " + sqLiteOpen.getDatabaseName());
    }

}
