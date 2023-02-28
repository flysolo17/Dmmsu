package com.flysolo.myapplication.views.teacher.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.flysolo.myapplication.R
import com.flysolo.myapplication.models.Classroom

class ClassroomAdapter(val context: Context,private val classroomList : List<Classroom>) : RecyclerView.Adapter<ClassroomAdapter.ClassroomViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClassroomViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.row_classroom,parent,false)
        return ClassroomViewHolder(view)
    }

    override fun onBindViewHolder(holder: ClassroomViewHolder, position: Int) {
        val classroom = classroomList[position]
        if (!classroom.background.isNullOrEmpty()) {
            Glide.with(context).load(classroom.background).into(holder.classroomBackground)
        }
        holder.textClassroomName.text = classroom.name
    }

    override fun getItemCount(): Int {
        return classroomList.size
    }
    class ClassroomViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val textClassroomName = itemView.findViewById<TextView>(R.id.textClassroomName)
        val classroomBackground = itemView.findViewById<ImageView>(R.id.classroomBackground)
    }

}