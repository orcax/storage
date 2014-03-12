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

/**
 * Class Resource
 * 
 * @version 2014.3.10
 * @author Chris X.
 */
public class Resource {
    private Integer id;
    private String block_id;
    private String vdisk_id;
    private String name;
    private String type;
    private String format;
    private Integer size;
    private String description;

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

    public String getType() {
	return type;
    }

    public void setType(String type) {
	this.type = type;
    }

    public String getFormat() {
	return format;
    }

    public void setFormat(String format) {
	this.format = format;
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

    @Override
    public String toString() {
	return "Resource [id=" + id + ", block_id=" + block_id + ", vdisk_id="
		+ vdisk_id + ", name=" + name + ", type=" + type + ", format="
		+ format + ", size=" + size + ", description=" + description
		+ "]";
    }

}
