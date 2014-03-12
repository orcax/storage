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
package sse.storage.fs.io;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileInputStream;
import sse.storage.constant.Config;
import sse.storage.fs.Coordinator;
import sse.storage.fs.bean.ResourceFile;
import sse.storage.fs.bean.VDisk;

/**
 * Class Reader
 * 
 * @version 2014.3.10
 * @author Chris X.
 */
public class Reader {

    public static final Reader INSTNANCE = new Reader();

    private Reader() {
    }

    private byte[] readLocalFile(File file) {
	FileInputStream fis = null;
	FileChannel channel = null;
	byte[] content = null;
	try {
	    fis = new FileInputStream(file);
	    channel = fis.getChannel();
	    ByteBuffer byteBuffer = ByteBuffer.allocate((int) channel.size());
	    while ((channel.read(byteBuffer)) > 0) {
	    }
	    content = byteBuffer.array();
	} catch (IOException e) {
	    e.printStackTrace();
	} finally {
	    try {
		if (fis != null) {
		    fis.close();
		}
		if (channel != null) {
		    channel.close();
		}
	    } catch (IOException e) {
		e.printStackTrace();
	    }
	}
	return content;
    }

    private byte[] readLocalFile(String path) {
	return readLocalFile(new File(path));
    }

    private byte[] readSmbFile(SmbFile file) {
	System.setProperty("jcifs.smb.client.dfs.disabled", "true");
	SmbFileInputStream fis = null;
	BufferedInputStream in = null;
	ByteArrayOutputStream out = null;
	byte[] content = null;
	try {
	    fis = new SmbFileInputStream(file);
	    in = new BufferedInputStream(fis);
	    out = new ByteArrayOutputStream();
	    byte buffer[] = new byte[1024];
	    for (int count = -1; (count = in.read(buffer, 0, 1024)) != -1;) {
		out.write(buffer, 0, count);
	    }
	    content = out.toByteArray();
	} catch (MalformedURLException e) {
	    e.printStackTrace();
	} catch (SmbException e) {
	    e.printStackTrace();
	} catch (UnknownHostException e) {
	    e.printStackTrace();
	} catch (IOException e) {
	    e.printStackTrace();
	} finally {
	    try {
		if (in != null) {
		    in.close();
		}
		if (fis != null) {
		    fis.close();
		}
		if (out != null) {
		    out.close();
		}
	    } catch (IOException e) {
		e.printStackTrace();
	    }
	}
	return content;
    }

    private byte[] readSmbFile(String url) {
	try {
	    SmbFile file = new SmbFile(url);
	    return readSmbFile(file);
	} catch (MalformedURLException e) {
	    e.printStackTrace();
	    return null;
	}
    }

    public byte[] readFile(ResourceFile res) {
	if (res == null || res.getBlock_id() == null
		|| res.getVdisk_id() == null) {
	    return null;
	}
	VDisk vdisk = Config.INSTANCE.getVdisk(res.getVdisk_id());
	if (vdisk == null) {
	    return null;
	}
	if (vdisk.isLocal()) {
	    return readLocalFile(Coordinator.mkurl(res));
	} else {
	    return readSmbFile(Coordinator.mkurl(res));
	}
    }

    public ResourceFile readRawFile(String path, boolean local) {
	ResourceFile res = new ResourceFile();
	if (local) {
	    File file = new File(path);
	    if (!file.exists()) {
		return null;
	    }
	    int index = file.getName().lastIndexOf(".");
	    if (index < 0) {
		res.setName(file.getName());
		res.setFormat(null);
	    } else {
		res.setName(file.getName().substring(0, index));
		res.setFormat(file.getName().substring(index + 1));
	    }
	    res.setContent(readLocalFile(file));
	} else {
	    try {
		SmbFile file = new SmbFile(path);
		if (!file.exists()) {
		    return null;
		}
		int index = file.getName().lastIndexOf(".");
		if (index < 0) {
		    res.setName(file.getName());
		    res.setFormat(null);
		} else {
		    res.setName(file.getName().substring(0, index));
		    res.setFormat(file.getName().substring(index + 1));
		}
		res.setContent(readSmbFile(file));
	    } catch (MalformedURLException e) {
		e.printStackTrace();
		return null;
	    } catch (SmbException e) {
		e.printStackTrace();
		return null;
	    }
	}
	return res;
    }
}
