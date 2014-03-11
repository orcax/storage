package sse.storage.bean;

import java.util.Properties;

import static sse.storage.config.Toolkit.*;

public class Database {

  public static final String TYPE_MASTER = "master";
  public static final String TYPE_BACKUP = "backup";

  private String id;
  private String type;
  private String name;
  private String driver;
  private String user;
  private String password;
  private String url;
  private String maxconn;

  public boolean isValid() {
    return !isEmpty(id, type, name, driver, user, password, url, maxconn);
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

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
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

  @Override
  public String toString() {
    return "Database [id=" + id + ", type=" + type + ", name=" + name
        + ", driver=" + driver + ", user=" + user + ", password=" + password
        + ", url=" + url + ", maxconn=" + maxconn + "]";
  }

}
