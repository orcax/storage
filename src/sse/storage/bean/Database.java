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
package sse.storage.bean;

import java.util.Properties;

import static sse.storage.etc.Toolkit.*;

/**
 * Class Database
 * 
 * @version 2014.3.10
 * @author Chris X.
 */
public class Database {

    private String id;
    private String name;
    private String driver;
    private String user;
    private String password;
    private String url;
    private String maxconn;

    public boolean isValid() {
        return !isEmpty(id, name, driver, user, password, url, maxconn);
    }

    public Properties getDbProps() {
        Properties props = new Properties();
        props.setProperty("driverclassname", driver);
        props.setProperty(id + ".url", url);
        props.setProperty(id + ".user", user);
        props.setProperty(id + ".password", password);
        props.setProperty(id + ".maxconn", maxconn);
        return props;
    }

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

    @Override
    public String toString() {
        return "Database [id=" + id + ", name=" + name + ", driver=" + driver
                + ", user=" + user + ", password=" + password + ", url=" + url
                + ", maxconn=" + maxconn + "]";
    }

}
