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
package sse.storage.db.bean;

import java.sql.Timestamp;

/**
 * Clas Post
 * 
 * @version 2014.3.10
 * @author Chris X.
 */
public class Post {

    private Integer id;
    private String block_id;
    private String vdisk_id;
    private String name;
    private Integer size;
    private String description;
    private Timestamp created;
    private Timestamp modified;

    public Integer getId() {
	return id;
    }

    public void setId(Integer id) {
	this.id = id;
    }

    public String getBlock_id() {
	return block_id;
    }

    public void setBlock_id(String block_id) {
	this.block_id = block_id;
    }

    public String getVdisk_id() {
	return vdisk_id;
    }

    public void setVdisk_id(String vdisk_id) {
	this.vdisk_id = vdisk_id;
    }

    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    public Integer getSize() {
	return size;
    }

    public void setSize(Integer size) {
	this.size = size;
    }

    public String getDescription() {
	return description;
    }

    public void setDescription(String description) {
	this.description = description;
    }

    public Timestamp getCreated() {
	return created;
    }

    public void setCreated(Timestamp created) {
	this.created = created;
    }

    public Timestamp getModified() {
	return modified;
    }

    public void setModified(Timestamp modified) {
	this.modified = modified;
    }

    @Override
    public String toString() {
	return "Post [id=" + id + ", block_id=" + block_id + ", vdisk_id="
		+ vdisk_id + ", name=" + name + ", size=" + size
		+ ", description=" + description + ", created=" + created
		+ ", modified=" + modified + "]";
    }

}
