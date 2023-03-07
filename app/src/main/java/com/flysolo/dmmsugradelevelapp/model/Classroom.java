package com.flysolo.dmmsugradelevelapp.model;


import java.util.List;

//    val id : String? = null,
//            val teacherID : String ? = null,
//            var background : String ?= null,
//            val name : String? = null,
//            val status : Boolean ? = null,
//            val code : String ? = null,
//            val students : List<String> ? = null,
//        val createdAt : Long ? = null
public class Classroom {
    String id;
    String teacherID;
    String background;
    String name;
    Boolean status;
    String code;
    List<String> students;
    Long createdAt;
    public Classroom(){}
    public Classroom(String id, String teacherID, String background, String name, Boolean status, String code, List<String> students, Long createdAt) {
        this.id = id;
        this.teacherID = teacherID;
        this.background = background;
        this.name = name;
        this.status = status;
        this.code = code;
        this.students = students;
        this.createdAt = createdAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTeacherID() {
        return teacherID;
    }

    public void setTeacherID(String teacherID) {
        this.teacherID = teacherID;
    }

    public String getBackground() {
        return background;
    }

    public void setBackground(String background) {
        this.background = background;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public List<String> getStudents() {
        return students;
    }

    public void setStudents(List<String> students) {
        this.students = students;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Long createdAt) {
        this.createdAt = createdAt;
    }
}
