package com.flysolo.dmmsugradelevelapp.services.auth;

import android.accounts.Account;
import android.net.Uri;

import com.flysolo.dmmsugradelevelapp.model.Accounts;
import com.flysolo.dmmsugradelevelapp.utils.UiState;
import com.google.firebase.auth.FirebaseUser;

import java.net.URI;

public interface AuthService {
        void login(String email , String password, UiState<FirebaseUser> result);
        void signup(String email, String password, Accounts accounts, UiState<Accounts> result);
        void createAccount(Accounts accounts,UiState<String> result);
        void getAccount(String uid,UiState<Accounts> result);
        void reAuthenticateAccount( FirebaseUser user,String email ,String password,UiState<FirebaseUser> result);
        void resetPassword(String email,UiState<String> result);
        void changePassword(FirebaseUser user,String password,UiState<String> result);
        void uploadProfile(String uid , Uri uri, UiState<String> result);
        void updateProfile(String uid,String name ,String profile,UiState<String> result);
}
