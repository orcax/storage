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

import java.io.UnsupportedEncodingException;

import static sse.storage.etc.Const.*;

/**
 * Classs ResourceFile
 * 
 * @version 2014.3.10
 * @author Chris X.
 */
public class ResourceEntity extends Resource {

    private byte[] content;

    public Resource toResource() {
	Resource res = new Resource();
	res.setId(this.getId());
        res.setBlock_id(this.getBlock_id());
        res.setName(this.getName());
        res.setFormat(this.getFormat());
        res.setType(this.getType());
        res.setStatus(this.getStatus());
        res.setSize(this.getSize());
        res.setCreated(this.getCreated());
        res.setModified(this.getModified());
        res.setDescription(this.getDescription());
        return res;
    }

    public void fromResource(Resource res) {
	this.setId(res.getId());
	this.setBlock_id(res.getBlock_id());
	this.setName(res.getName());
	this.setFormat(res.getFormat());
	this.setType(res.getType());
	this.setStatus(res.getStatus());
	this.setSize(res.getSize());
	this.setCreated(res.getCreated());
	this.setModified(res.getModified());
	this.setDescription(res.getDescription());
    }

    public byte[] getContent() {
	return content;
    }

    public String getContentString() {
	return new String(content);
    }
    
    public String getContentString(String encode) {
	try {
	    return new String(content, ENCODING);
	} catch (UnsupportedEncodingException e) {
	    e.printStackTrace();
	}
	return null;
    }

    public void setContent(byte[] content) {
	this.content = content;
    }

    public void setContent(String content) {
	this.content = content.getBytes();
    }

}
