package sse.storage.dao;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import sse.storage.config.StorageConfig;

import com.xeiam.yank.DBProxy;

public abstract class BaseDao {

  protected String tableName;
  protected Class<?> beanClass;

  protected static String getDb() {
    return StorageConfig.INSTANCE.getCurrentDb().getName();
  }

  protected String table2Bean(String tableName) {
    if (tableName == null || tableName.equals("")) {
      return "";
    }
    String[] names = tableName.toLowerCase().split("_");
    String beanName = "";
    for (String name : names) {
      beanName += Character.toUpperCase(name.charAt(0)) + name.substring(1);
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

  protected int getLastInsertId() {
    String sql = "SELECT LAST_INSERT_ID() FROM " + tableName;
    BigInteger id = DBProxy.querySingleScalarSQL(getDb(), sql,
        BigInteger.class, null);
    return id.intValue();
  }
  
  public Object read(int id) {
    String sql = String.format("SELECT * FROM %s WHERE id='%d'", tableName, id);
    return DBProxy.querySingleObjectSQL(getDb(), sql, beanClass, null);
  }

  public List<?> findAll() {
    String sql = "SELECT * FROM " + tableName;
    return DBProxy.queryObjectListSQL(getDb(), sql, beanClass, null);
  }

  public int insert(Object bean) {
    Class<?> obj = bean.getClass();
    Field[] fields = obj.getDeclaredFields();
    StringBuffer sql1 = new StringBuffer();
    StringBuffer sql2 = new StringBuffer();
    List<Object> params = new ArrayList<Object>();
    int i = 0;
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
        if(value == null) {
          continue;
        }
        sql1.append(bean2Table(f.getName()) + ",");
        sql2.append("?,");
        params.add(value);
      } catch (IllegalArgumentException e) {
        e.printStackTrace();
      } catch (NoSuchMethodException e) {
        e.printStackTrace();
      } catch (SecurityException e) {
        e.printStackTrace();
      } catch (IllegalAccessException e) {
        e.printStackTrace();
      } catch (InvocationTargetException e) {
        e.printStackTrace();
      }
      ++i;
    }
    String sql = String.format("INSERT INTO %s(%s) VALUES(%s)", tableName,
        sql1.substring(0, sql1.length() - 1),
        sql2.substring(0, sql2.length() - 1));
    System.out.println(sql);
    DBProxy.executeSQL(getDb(), sql, params.toArray());
    return getLastInsertId();
  }

}