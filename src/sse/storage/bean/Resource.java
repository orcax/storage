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

import java.sql.Timestamp;

/**
 * Class Resource
 * 
 * @version 2014.3.10
 * @author Chris X.
 */
public class Resource {

    private Integer id;
    private Integer block_id;
    private String name;
    private String format;
    private String type;
    private String status;
    private Integer size;
    private Timestamp created;
    private Timestamp modified;
    private String description;

    public Integer getId() {
	return id;
    }

    public void setId(Integer id) {
	this.id = id;
    }

    public Integer getBlock_id() {
	return block_id;
    }

    public void setBlock_id(Integer block_id) {
	this.block_id = block_id;
    }

    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    public String getFormat() {
	return format;
    }

    public void setFormat(String format) {
	this.format = format;
    }

    public String getType() {
	return type;
    }

    public void setType(String type) {
	this.type = type;
    }

    public String getStatus() {
	return status;
    }

    public void setStatus(String status) {
	this.status = status;
    }

    public Integer getSize() {
	return size;
    }

    public void setSize(Integer size) {
	this.size = size;
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

    public String getDescription() {
	return description;
    }

    public void setDescription(String description) {
	this.description = description;
    }

    @Override
    public String toString() {
	return "Resource [id=" + id + ", block_id=" + block_id + ", name="
		+ name + ", format=" + format + ", type=" + type + ", status="
		+ status + ", size=" + size + ", created=" + created
		+ ", modified=" + modified + ", description=" + description
		+ "]";
    }

}
