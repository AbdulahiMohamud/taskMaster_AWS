package com.abdul.taskmaster.model;



import java.util.Date;


public class TaskModel {


    private Long id;
    private String name;
    private String description;
    private Date dateCreated;
    private StateEnum state;


    public TaskModel(String name, String description, Date dateCreated, StateEnum state) {
        this.name = name;
        this.description = description;
        this.dateCreated = dateCreated;
        this.state = state;
    }

    public TaskModel(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public StateEnum getState() {
        return state;
    }

    public void setState(StateEnum state) {
        this.state = state;
    }
}