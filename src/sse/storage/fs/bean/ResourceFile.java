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
package sse.storage.fs.bean;

import static sse.storage.constant.Const.*;

import java.io.UnsupportedEncodingException;

import sse.storage.constant.ResourceType;
import sse.storage.db.bean.Picture;
import sse.storage.db.bean.Post;
import sse.storage.db.bean.Resource;

/**
 * Classs ResourceFile
 * 
 * @version 2014.3.10
 * @author Chris X.
 */
public class ResourceFile extends Resource {

    private byte[] content;
    private String text;

    public Picture toPicture() {
	Picture pic = new Picture();
	pic.setId(this.getId());
	pic.setVdisk_id(this.getVdisk_id());
	pic.setBlock_id(this.getBlock_id());
	pic.setName(this.getName());
	pic.setFormat(this.getFormat());
	pic.setSize(this.getSize());
	pic.setDescription(this.getDescription());
	return pic;
    }

    public void fromPicture(Picture pic) {
	this.setId(pic.getId());
	this.setVdisk_id(pic.getVdisk_id());
	this.setBlock_id(pic.getBlock_id());
	this.setName(pic.getName());
	this.setType(ResourceType.PICTURE.toString());
	this.setFormat(pic.getFormat());
	this.setSize(pic.getSize());
	this.setDescription(pic.getDescription());
    }

    public Post toPost() {
	Post post = new Post();
	post.setId(this.getId());
	post.setVdisk_id(this.getVdisk_id());
	post.setBlock_id(this.getBlock_id());
	post.setName(this.getName());
	post.setSize(this.getSize());
	post.setDescription(this.getDescription());
	return post;
    }

    public void fromPost(Post post) {
	this.setId(post.getId());
	this.setVdisk_id(post.getVdisk_id());
	this.setBlock_id(post.getBlock_id());
	this.setName(post.getName());
	this.setType(ResourceType.POST.toString());
	this.setFormat(POST_FORMAT);
	this.setSize(post.getSize());
	this.setDescription(post.getDescription());
    }

    public byte[] getContent() {
	return content;
    }

    public void setContent(byte[] content) {
	this.content = content;
    }

    public String getText() {
	return text;
    }
    
    public void setText(String text) {
	this.text = text;
	this.content = text.getBytes();
    }

    public void setText(byte[] text) {
	this.content = text;
	try {
	    this.text = new String(content, POST_ENCODING);
	} catch (UnsupportedEncodingException e) {
	    this.text = new String(content);
	}	
    }

}
