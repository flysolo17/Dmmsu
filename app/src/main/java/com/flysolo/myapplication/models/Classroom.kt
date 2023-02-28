package com.flysolo.myapplication.models

class Classroom(
    val id : String? = null,
    val teacherID : String ? = null,
    var background : String ?= null,
    val name : String? = null,
    val status : Boolean ? = null,
    val code : String ? = null,
    val students : List<String> ? = null,
    val createdAt : Long ? = null
)