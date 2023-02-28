package com.flysolo.myapplication.services.classroom

import android.net.Uri
import com.flysolo.myapplication.models.Classroom
import com.flysolo.myapplication.utils.UiState
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

interface ClassroomService {
    fun createClassroom(classroom : Classroom,result: (UiState<String>) -> Unit)
    fun uploadClassroomBackground(uid : String,uri : Uri, result: (UiState<String>) -> Unit)
    fun getAllClassroom(uid: String,result: (UiState<List<Classroom>>) -> Unit)
}