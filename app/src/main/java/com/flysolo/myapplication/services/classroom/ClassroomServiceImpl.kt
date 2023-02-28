package com.flysolo.myapplication.services.classroom

import android.net.Uri
import com.danica.ikidsv2.utils.Constants
import com.flysolo.myapplication.models.Classroom
import com.flysolo.myapplication.utils.UiState
import com.google.common.io.Files
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage

class ClassroomServiceImpl(private val firestore : FirebaseFirestore,private val storage: FirebaseStorage) : ClassroomService {
    override fun createClassroom(classroom: Classroom, result: (UiState<String>) -> Unit) {
        val id = firestore.collection(Constants.CLASSROOM_TABLE).document().id
        result.invoke(UiState.Loading)
        firestore.collection(Constants.CLASSROOM_TABLE)
            .document(id)
            .set(classroom)
            .addOnCompleteListener {
                if (it.isSuccessful)  {
                    result.invoke(UiState.Successful("Classroom Created!"))
                } else {
                    result.invoke(UiState.Failed("Failed to Create Classroom"))
                }
            }.addOnFailureListener {
                result.invoke(UiState.Failed(it.message!!))
            }
    }

    override fun uploadClassroomBackground(
        uid: String,
        uri: Uri,
        result: (UiState<String>) -> Unit
    ) {
        val storage  = storage.getReference(Constants.CLASSROOM_TABLE)
            .child(uid)
            .child(System.currentTimeMillis().toString() + "." + Files.getFileExtension(uri.toString()))
        result.invoke(UiState.Loading)
        storage.putFile(uri)
            .addOnSuccessListener {
                storage.downloadUrl.addOnSuccessListener { uri1: Uri ->
                    result.invoke(UiState.Successful(uri1.toString()))
                }
            }
            .addOnFailureListener{
                result.invoke(UiState.Failed(it.message!!))
            }
    }

    override fun getAllClassroom(uid: String, result: (UiState<List<Classroom>>) -> Unit) {
        val classList  = mutableListOf<Classroom>()
        firestore.collection(Constants.CLASSROOM_TABLE)
            .whereEqualTo("teacherID",uid)
            .orderBy("createdAt",Query.Direction.DESCENDING)
            .addSnapshotListener { value, error ->
                error?.let {
                    result.invoke(UiState.Failed(it.message!!))
                }
                value?.let {
                    classList.clear()
                    for (snapshot in value.documents) {
                        val classes = snapshot.toObject(Classroom::class.java)
                        if (classes != null) {
                            classList.add(classes)
                        }
                    }
                    result.invoke(UiState.Successful(classList))
                }
            }
    }

}