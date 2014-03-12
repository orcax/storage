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
import sse.storage.fs.bean.ResourceFile;

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

    public ResourceFile readRawFile(String path, boolean local) {
	ResourceFile res = new ResourceFile();
	if (local) {
	    FileInputStream fis = null;
	    FileChannel channel = null;
	    File file = new File(path);
	    int index = file.getName().lastIndexOf(".");
	    String name = file.getName().substring(0, index);
	    String format = file.getName().substring(index + 1);
	    res.setName(name);
	    res.setFormat(format);
	    try {
		fis = new FileInputStream(file);
		channel = fis.getChannel();
		ByteBuffer byteBuffer = ByteBuffer.allocate((int) channel
			.size());
		while ((channel.read(byteBuffer)) > 0) {
		}
		res.setContent(byteBuffer.array());

	    } catch (IOException e) {
		e.printStackTrace();
	    } finally {
		try {
		    if (fis != null)
			fis.close();
		    if (channel != null)
			channel.close();
		} catch (IOException e) {
		    e.printStackTrace();
		}
	    }
	} else {
	    System.setProperty("jcifs.smb.client.dfs.disabled", "true");
	    BufferedInputStream in = null;
	    try {
		SmbFile file = new SmbFile(path);
		int index = file.getName().lastIndexOf(".");
		String name = file.getName().substring(0, index);
		String format = file.getName().substring(index + 1);
		res.setName(name);
		res.setFormat(format);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		in = new BufferedInputStream(new SmbFileInputStream(file));
		byte buffer[] = new byte[1024];
		int count = -1;
		while ((count = in.read(buffer, 0, 1024)) != -1) {
		    out.write(buffer, 0, count);
		}
		res.setContent(out.toByteArray());
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
		    if (in != null)
			in.close();
		} catch (IOException e) {
		    e.printStackTrace();
		}
	    }
	}
	return res;
    }
}
