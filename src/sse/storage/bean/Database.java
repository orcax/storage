package sse.storage.bean;

import java.util.Properties;

import com.xeiam.yank.PropertiesUtils;

public class Database {
  private String id;
  private String name;
  private String driver;
  private String user;
  private String password;
  private String url;
  private String maxconn;
  private String sqlProps;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDriver() {
    return driver;
  }

  public void setDriver(String driver) {
    this.driver = driver;
  }

  public String getUser() {
    return user;
  }

  public void setUser(String user) {
    this.user = user;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public String getMaxconn() {
    return maxconn;
  }

  public void setMaxconn(String maxconn) {
    this.maxconn = maxconn;
  }

  public Properties getSqlProps() {
    return PropertiesUtils.getPropertiesFromClasspath(sqlProps);
  }

  public void setSqlProps(String sqlProps) {
    this.sqlProps = sqlProps;
  }

  public Properties getDbProps() {
    Properties props = new Properties();
    props.setProperty("driverclassname", driver);
    props.setProperty(name + ".url", url);
    props.setProperty(name + ".user", user);
    props.setProperty(name + ".password", password);
    props.setProperty(name + ".maxconn", maxconn);
    return props;
  }

}
