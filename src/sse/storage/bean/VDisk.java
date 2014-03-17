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

import static sse.storage.etc.Toolkit.*;

import java.io.File;
import java.io.FilenameFilter;
import java.net.MalformedURLException;

import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFilenameFilter;

/**
 * Class VDisk
 * 
 * @version 2014.3.10
 * @author Chris X.
 */
public class VDisk {

    private static final String IP_LOCAL = "localhost";

    private String id;
    private String ip;
    private String user;
    private String password;
    private String rootPath; // Only support SMB protocal for remote access

    private String mkpath(String subDir) {
        if (isEmpty(subDir)) {
            return rootPath + "/";
        }
        if (!subDir.startsWith("/")) {
            subDir = "/" + subDir;
        }
        return subDir.endsWith("/") ? rootPath + subDir : rootPath + subDir
                + "/";
    }

    public boolean isLocal() {
        return IP_LOCAL.equals(ip);
    }

    public boolean isValid() {
        if (isEmpty(id, ip)) {
            return false;
        }
        if (isEmpty(rootPath)) {
            rootPath = "/";
        } else {
            int i = rootPath.length() - 1;
            while (i >= 0 && rootPath.charAt(i) == '/') {
                --i;
            }
            rootPath = rootPath.substring(0, i + 1);
            if (!rootPath.startsWith("/")) {
                rootPath = "/" + rootPath;
            }
        }
        if (!isLocal()) {
            rootPath = "smb://" + user + ":" + password + "@" + ip + rootPath;
        }
        return true;
    }

    public boolean isdir(String subDir) throws MalformedURLException,
            SmbException {
        String path = mkpath(subDir);
        if (isLocal()) {
            File dir = new File(path);
            return dir.exists() && dir.isDirectory();
        } else {
            SmbFile dir = new SmbFile(path);
            return dir.exists() && dir.isDirectory();
        }
    }

    public String[] ls(String subDir) throws MalformedURLException,
            SmbException {
        String path = mkpath(subDir);
        if (isLocal()) {
            return new File(path).list();
        } else {
            return new SmbFile(path).list();
        }
    }

    public String[] lsdirs(String subDir) throws MalformedURLException,
            SmbException {
        String path = mkpath(subDir);
        String[] names = null;
        if (isLocal()) {
            File[] dirs = new File(path).listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return dir.isDirectory() && name.startsWith("b");
                }
            });
            names = new String[dirs.length];
            for (int i = 0; i < dirs.length; i++) {
                names[i] = dirs[i].getName();
            }
        } else {
            SmbFile[] dirs = new SmbFile(path)
                    .listFiles(new SmbFilenameFilter() {
                        @Override
                        public boolean accept(SmbFile dir, String name) {
                            try {
                                return dir.isDirectory()
                                        && name.startsWith("b");
                            } catch (SmbException e) {
                                e.printStackTrace();
                                return false;
                            }
                        }
                    });
            names = new String[dirs.length];
            for (int i = 0; i < dirs.length; i++) {
                names[i] = dirs[i].getName();
            }
        }
        return names;
    }

    public void mkdirs(String subDir) throws MalformedURLException,
            SmbException {
        String path = mkpath(subDir);
        if (isLocal()) {
            File dir = new File(path);
            if (!dir.exists() || !dir.isDirectory()) {
                dir.mkdirs();
            }
        } else {
            SmbFile dir = new SmbFile(path);
            if (!dir.exists() || !dir.isDirectory()) {
                dir.mkdirs();
            }
        }
    }

    public void rm(String subDir) throws MalformedURLException, SmbException {
        String path = mkpath(subDir);
        if (isLocal()) {
            new File(path).delete();
        } else {
            SmbFile f = new SmbFile(path);
            if (f.exists()) {
                f.delete();
            }
        }
    }

    /* Getters, setters and toString */

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
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

    public String getRootPath() {
        return rootPath;
    }

    public void setRootPath(String rootPath) {
        this.rootPath = rootPath;
    }

    @Override
    public String toString() {
        return "VDisk [id=" + id + ", ip=" + ip + ", user=" + user
                + ", password=" + password + ", rootPath=" + rootPath + "]";
    }

}
