package com.flysolo.myapplication.services.auth

import android.net.Uri

import com.flysolo.myapplication.utils.UiState
import com.flysolo.myapplication.models.Accounts
import com.google.firebase.auth.FirebaseUser

interface AuthService {
    fun login(email : String,password : String,result  : (UiState<FirebaseUser>) -> Unit)
    fun signup(email: String,password: String,user : Accounts,result  : (UiState<Accounts>) -> Unit)
    fun createAccount(user : Accounts,result  : (UiState<Boolean>) -> Unit)
    fun getUserInfo(id : String ,result  : (UiState<Accounts>) -> Unit)
    fun reAuthenticateAccount( user : FirebaseUser,email: String,password: String,result: (UiState<FirebaseUser>) -> Unit)
    fun resetPassword(email : String,result: (UiState<String>) -> Unit)
    fun changePassword(user: FirebaseUser,password: String,result: (UiState<Boolean>) -> Unit)
    fun updateUserName(uid : String , name : String,result: (UiState<String>) -> Unit)
    fun logout()
    fun getAllStudent(result: (UiState<List<Accounts>>) -> Unit)
    fun updateAvatar(uid : String,avatar : String ,result: (UiState<String>) -> Unit)
    fun updateInfo(uid: String,name : String,gender : String,result: (UiState<String>) -> Unit)
    fun uploadProfile(uid: String,uri: Uri,result: (UiState<String>) -> Unit)
    fun updateTeacherInfo(uid: String,name : String,avatar: String, result: (UiState<String>) -> Unit)
}