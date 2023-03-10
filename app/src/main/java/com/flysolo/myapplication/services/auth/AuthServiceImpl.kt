package com.flysolo.myapplication.services.auth

import android.net.Uri

import com.danica.ikidsv2.utils.Constants
import com.flysolo.myapplication.utils.UiState
import com.flysolo.myapplication.models.Accounts
import com.flysolo.myapplication.models.UserType
import com.google.android.gms.tasks.Task
import com.google.common.io.Files
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage


class AuthServiceImpl(private val auth : FirebaseAuth,private val firestore : FirebaseFirestore,private val firebaseStorage: FirebaseStorage) :
    AuthService {
    override fun login(email: String, password: String, result: (UiState<FirebaseUser>) -> Unit) {
        result.invoke(UiState.Loading)
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
            if (it.isSuccessful) {
                val user = it.result.user
                if (user != null) {
                    result.invoke(UiState.Successful(user))
                }
            } else {
                result.invoke(UiState.Failed("Failed to login!"))
            }
        }.addOnFailureListener {
            result.invoke(UiState.Failed(it.message!!))
        }

    }
    override fun signup(
        email: String,
        password: String,
        user: Accounts,
        result: (UiState<Accounts>) -> Unit
    ) {
        result.invoke(UiState.Loading)
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task: Task<AuthResult> ->
                if (task.isSuccessful) {
                    val data = task.result.user
                    if (data != null) {
                        user.id = data.uid
                        result.invoke(UiState.Successful(user))
                    }
                }
            }.addOnFailureListener { e: Exception? ->
                e?.let {
                    result.invoke(UiState.Failed(it.message!!))
                }
            }
    }

    override fun createAccount(user: Accounts, result: (UiState<Boolean>) -> Unit) {
        result.invoke(UiState.Loading)
        firestore.collection(Constants.USER_TABLE)
            .document(user.id!!)
            .set(user)
            .addOnCompleteListener {
                if (it.isSuccessful)  {
                    result.invoke(UiState.Successful(true))
                } else {
                    result.invoke(UiState.Failed("Failed to Create Account"))
                }
            }
    }

    override  fun getUserInfo(id: String, result: (UiState<Accounts>) -> Unit) {
        firestore.collection(Constants.USER_TABLE)
                .document(id)
                .get()
                .addOnSuccessListener {
                    if (it.exists()) {
                        val user : Accounts? = it.toObject(Accounts::class.java)
                        result.invoke(UiState.Successful(user!!))
                    } else {
                        result.invoke(UiState.Failed("Failed Getting Account"))
                    }
                }.addOnFailureListener {
                    result.invoke(UiState.Failed(it.message!!))
                }

    }

    override fun reAuthenticateAccount(
        user : FirebaseUser,
        email: String,
        password: String,
        result: (UiState<FirebaseUser>) -> Unit
    ) {
        result.invoke(UiState.Loading)
        val credential = EmailAuthProvider.getCredential(email, password)
        user.reauthenticate(credential)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    result.invoke(UiState.Successful(user))
                } else  {
                    result.invoke(UiState.Failed("Wrong Password!"))
                }
            }.addOnFailureListener {
                result.invoke(UiState.Failed(it.message!!))
            }
    }

    override fun resetPassword(
        email: String,

        result: (UiState<String>) -> Unit
    ) {
        result.invoke(UiState.Loading)
        auth.sendPasswordResetEmail(email).addOnCompleteListener {
            if (it.isSuccessful) {
                result.invoke(UiState.Successful("We sent a password reset link to your email!"))
            } else {
                result.invoke(UiState.Failed("Failed to send password reset link to $email"))
            }
        }.addOnFailureListener {
            result.invoke(UiState.Failed(it.message!!))
        }
    }

    override fun changePassword(
        user: FirebaseUser,
        password: String,
        result: (UiState<Boolean>) -> Unit
    ) {
        result.invoke(UiState.Loading)
        user.updatePassword(password)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    result.invoke(UiState.Successful(true))
                } else  {
                    result.invoke(UiState.Failed("Wrong Password!"))
                }
            }.addOnFailureListener {
                result.invoke(UiState.Failed(it.message!!))
            }
    }

    override fun updateUserName(uid : String,name: String, result: (UiState<String>) -> Unit) {
        result.invoke(UiState.Loading)
        firestore.collection(Constants.USER_TABLE)
            .document(uid)
            .update("name",name)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    result.invoke(UiState.Successful("Successfully Updated"))
                } else  {
                    result.invoke(UiState.Failed("Failed to update name"))
                }
            }.addOnFailureListener {
                result.invoke(UiState.Failed(it.message!!))
            }
    }

    override fun logout() {

    }

    override fun getAllStudent(result: (UiState<List<Accounts>>) -> Unit) {
        val users = mutableListOf<Accounts>()
        result.invoke(UiState.Loading)
        firestore.collection(Constants.USER_TABLE)
            .whereEqualTo("type", UserType.STUDENTS)
            .get()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    for (snapshot in it.result) {
                        val user = snapshot.toObject(Accounts::class.java)
                        users.add(user)
                    }
                    result.invoke(UiState.Successful(users))
                } else {
                    result.invoke(UiState.Failed("Failed getting students"))
                }
            }.addOnFailureListener {
                result.invoke(UiState.Failed(it.message!!))
            }
    }

    override fun updateAvatar(uid: String,profile: String, result: (UiState<String>) -> Unit) {
        result.invoke(UiState.Loading)
        firestore.collection(Constants.USER_TABLE)
            .document(uid)
            .update("profile",profile)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    result.invoke(UiState.Successful("Profile changed!"))
                } else {
                    result.invoke(UiState.Failed("Failed to update profile"))
                }
            }.addOnFailureListener {
                result.invoke(UiState.Failed(it.message!!))
            }
    }

    override fun updateInfo(
        uid: String,
        name: String,
        gender: String,
        result: (UiState<String>) -> Unit
    ) {
        result.invoke(UiState.Loading)
        firestore.collection(Constants.USER_TABLE)
            .document(uid)
            .update("name",name,"gender",gender)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    result.invoke(UiState.Successful("Profile changed!"))
                } else {
                    result.invoke(UiState.Failed("Failed to update profile"))
                }
            }.addOnFailureListener {
                result.invoke(UiState.Failed(it.message!!))
            }
    }

    override fun uploadProfile(uid: String, uri: Uri, result: (UiState<String>) -> Unit) {
        val storage  = firebaseStorage.getReference(Constants.USER_TABLE)
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

    override fun updateTeacherInfo(
        uid: String,
        name: String,
        avatar: String,
        result: (UiState<String>) -> Unit
    ) {
        result.invoke(UiState.Loading)
        firestore.collection(Constants.USER_TABLE)
            .document(uid)
            .update("name",name,"avatar",avatar)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    result.invoke(UiState.Successful("Profile Updated Successfully!"))
                } else {
                    result.invoke(UiState.Failed("Failed to update profile"))
                }
            }.addOnFailureListener {
                result.invoke(UiState.Failed(it.message!!))
            }
    }

}