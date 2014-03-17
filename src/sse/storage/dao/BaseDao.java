/*
 * Chris X.
 * 
 * Copyright 2012-2014 Zhang Chenxi Project, SSE, Tongji University.
 * 
 * This software is the confidential and proprietary information of 
 * Zhang Chenxi project. You shall not disclose such Confidential 
 * Information and shall use it only in accordance with the terms of 
 * the license agreement you participate in the project work. 
 */
package sse.storage.dao;

import static sse.storage.etc.Toolkit.*;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import sse.storage.bean.Cluster;
import sse.storage.bean.Database;
import sse.storage.etc.Config;
import sse.storage.except.BlockException;

import com.xeiam.yank.DBProxy;

/**
 * Class BaseDao
 * 
 * @version 2014.3.10
 * @author Chris X.
 */
public abstract class BaseDao {

    protected String prefix = "fs_";
    protected String tableName;
    protected Class<?> beanClass;
    protected String dbId;

    /**
     * db initially refers to master database.
     */
    protected BaseDao() {
        Cluster cluster = Config.getInstance().getMasterCluster();
        dbId = Config.getInstance().getDatabase(cluster.getDbId()).getId();
    }

    protected String table2Bean(String tableName) {
        if (tableName == null || tableName.equals("")) {
            return "";
        }
        String[] names = tableName.toLowerCase().split("_");
        String beanName = "";
        for (String name : names) {
            beanName += Character.toUpperCase(name.charAt(0))
                    + name.substring(1);
        }
        return beanName;
    }

    protected String bean2Table(String beanName) {
        if (beanName == null || beanName.equals("")) {
            return "";
        }
        String tableName = "";
        for (int i = 0; i < beanName.length(); i++) {
            if (Character.isUpperCase(beanName.charAt(i)) && i != 0) {
                tableName += "_";
            }
            tableName += beanName.charAt(i);
        }
        return tableName.toLowerCase();
    }

    /**
     * Only works for MySQL or other databases supporting LAST_INSERT_ID
     * function.
     */
    protected int getLastInsertId() {
        String sql = "SELECT LAST_INSERT_ID() FROM " + tableName;
        BigInteger id = DBProxy.querySingleScalarSQL(dbId, sql,
                BigInteger.class, null);
        return id.intValue();
    }

    /* Public Interface */

    public void changeDb(String dbId) {
        if (Config.getInstance().getDatabase(dbId) == null) {
            error("Database " + dbId + " is not defined");
            return;
        }
        this.dbId = dbId;
    }

    public void changeCluster(String clusterId) {
        Database db = Config.getInstance().getCurrDb(clusterId);
        if (db == null) {
            return;
        }
        this.dbId = db.getId();
    }

    public void createTable() {
        String sqlKey = "CREATE_TABLE_" + tableName.toUpperCase();
        DBProxy.executeSQLKey(dbId, sqlKey, null);
        info("Table " + tableName + " is created");
    }

    public Object read(int id) {
        String sql = String.format("SELECT * FROM %s WHERE id='%d'", tableName,
                id);
        info("Executing: " + sql);
        return DBProxy.querySingleObjectSQL(dbId, sql, beanClass, null);
    }

    public List<?> findAll() {
        String sql = "SELECT * FROM " + tableName;
        info("Executing: " + sql);
        return DBProxy.queryObjectListSQL(dbId, sql, beanClass, null);
    }

    public List<?> find(String condition) {
        String sql = "SELECT * FROM " + tableName + " WHERE " + condition;
        info("Executing: " + sql);
        return DBProxy.queryObjectListSQL(dbId, sql, beanClass, null);
    }

    public synchronized int update(Object bean) {
        Class<?> obj = bean.getClass();
        Integer id = null;
        try {
            Method get = obj.getMethod("getId");
            id = (Integer) get.invoke(bean);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
        if (id == null) {
            error("Update ID is null");
            return 0;
        }
        Field[] fields = obj.getDeclaredFields();
        StringBuffer sql = new StringBuffer("UPDATE " + tableName + " SET ");
        List<Object> params = new ArrayList<Object>();
        for (Field f : fields) {
            if (f.getName().equals("id")) {
                continue;
            }
            String methodName = "get"
                    + String.valueOf(f.getName().charAt(0)).toUpperCase()
                    + f.getName().substring(1);
            try {
                Method get = obj.getMethod(methodName);
                Object value = get.invoke(bean);
                if (value == null) {
                    continue;
                }
                sql.append(f.getName() + "=?,");
                if (f.getType().getSimpleName().equals("Timestamp")) {
                    SimpleDateFormat sdf = new SimpleDateFormat(
                            "yyyy-MM-dd HH:mm:ss");
                    value = sdf.format(value);
                }
                params.add(value);
            } catch (Exception e) {
                e.printStackTrace();
                return 0;
            }
        }
        if (params.size() == 0) {
            error("No attribute is updated");
            return 0;
        }
        sql.deleteCharAt(sql.length() - 1);
        sql.append(" WHERE id=" + id + ";");
        info("Executing: " + sql);
        return DBProxy.executeSQL(dbId, sql.toString(), params.toArray());
    }

    public synchronized int insert(Object bean) {
        Class<?> obj = bean.getClass();
        Field[] fields = obj.getDeclaredFields();
        StringBuffer sql1 = new StringBuffer();
        StringBuffer sql2 = new StringBuffer();
        List<Object> params = new ArrayList<Object>();
        for (Field f : fields) {
            String methodName = "get"
                    + String.valueOf(f.getName().charAt(0)).toUpperCase()
                    + f.getName().substring(1);
            try {
                Method get = obj.getMethod(methodName);
                Object value = get.invoke(bean);
                if (value == null) {
                    continue;
                }
                sql1.append(bean2Table(f.getName()) + ",");
                sql2.append("?,");
                if (f.getType().getSimpleName().equals("Timestamp")) {
                    SimpleDateFormat sdf = new SimpleDateFormat(
                            "yyyy-MM-dd HH:mm:ss");
                    value = sdf.format(value);
                }
                params.add(value);
            } catch (Exception e) {
                e.printStackTrace();
                return -1;
            }
        }
        String sql = String.format("INSERT INTO %s(%s) VALUES(%s)", tableName,
                sql1.substring(0, sql1.length() - 1),
                sql2.substring(0, sql2.length() - 1));
        info("Executing: " + sql);
        DBProxy.executeSQL(dbId, sql, params.toArray());
        return getLastInsertId();
    }

}
