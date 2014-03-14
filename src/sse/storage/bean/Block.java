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
 * Class Block
 * 
 * @version 2014.3.12
 * @author Chris X.
 */
public class Block {

    private Integer id;
    private String vdisk_id;
    private String type;
    private String status;
    private Integer left_space;
    private Timestamp created;
    private Timestamp modified;

    @Override
    public Block clone() {
	Block block = new Block();
	block.id = this.id;
	block.vdisk_id = this.vdisk_id;
	block.type = this.type;
	block.status = this.status;
	block.left_space = this.left_space;
	block.created = this.created;
	block.modified = this.modified;
	return block;
    }

    public Integer getId() {
	return id;
    }

    public void setId(Integer id) {
	this.id = id;
    }

    public String getVdisk_id() {
	return vdisk_id;
    }

    public void setVdisk_id(String vdisk_id) {
	this.vdisk_id = vdisk_id;
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

    public Integer getLeft_space() {
	return left_space;
    }

    public void setLeft_space(Integer left_space) {
	this.left_space = left_space;
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
	return "Block [id=" + id + ", vdisk_id=" + vdisk_id + ", type=" + type
		+ ", status=" + status + ", left_space=" + left_space
		+ ", created=" + created + ", modified=" + modified + "]";
    }

}
