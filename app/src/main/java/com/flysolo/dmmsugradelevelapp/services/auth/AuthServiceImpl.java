package com.flysolo.dmmsugradelevelapp.services.auth;

import android.content.Context;
import android.net.Uri;

import androidx.annotation.NonNull;

import com.flysolo.dmmsugradelevelapp.model.Accounts;
import com.flysolo.dmmsugradelevelapp.model.UserType;
import com.flysolo.dmmsugradelevelapp.utils.Constants;
import com.flysolo.dmmsugradelevelapp.utils.UiState;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.auth.CredentialsProvider;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.security.AuthProvider;
import java.util.ArrayList;


public class AuthServiceImpl implements AuthService {

    FirebaseAuth auth;
    FirebaseFirestore firestore;
    FirebaseStorage storage;
    public AuthServiceImpl(FirebaseAuth auth, FirebaseFirestore firestore, FirebaseStorage storage) {
        this.auth = auth;
        this.firestore = firestore;
        this.storage = storage;
    }

    @Override
    public void login(String email, String password, UiState<FirebaseUser> result) {
        result.Loading();
        auth.signInWithEmailAndPassword(email,password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                FirebaseUser user = task.getResult().getUser();
                result.Successful(user);
            } else {
                result.Failed("Failed signing in!");
            }
        }).addOnFailureListener(e -> {
            result.Failed(e.getMessage());
        });
    }

    @Override
    public void signup(String email, String password, Accounts accounts, UiState<Accounts> result) {
        result.Loading();
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                FirebaseUser data = task.getResult().getUser();
                if (data != null) {
                    accounts.id = data.getUid();
                    result.Successful(accounts);
                }
            } else  {
                result.Failed("Failed creating account");
            }
        }).addOnFailureListener(e -> result.Failed(e.getMessage()));
    }

    @Override
    public void createAccount(Accounts accounts, UiState<String> result) {
        result.Loading();
        firestore.collection(Constants.ACCOUNTS_TABLE)
                .document(accounts.getId())
                .set(accounts)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        result.Successful("Successfully created!");
                    } else {
                        result.Failed("Failed creating account!");
                    }
                }).addOnFailureListener(e -> result.Failed(e.getMessage()));
    }

    @Override
    public void getAccount(String uid, UiState<Accounts> result) {
            result.Loading();
            firestore.collection(Constants.ACCOUNTS_TABLE)
                    .document(uid)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Accounts account = task.getResult().toObject(Accounts.class);
                            result.Successful(account);
                        } else {
                            result.Failed("Failed getting account!");
                        }
                    }).addOnFailureListener(e -> result.Failed(e.getMessage()));
    }

    @Override
    public void reAuthenticateAccount(FirebaseUser user, String email, String password, UiState<FirebaseUser> result) {
        AuthCredential credential = EmailAuthProvider.getCredential(email, password);
        result.Loading();
        user.reauthenticate(credential)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        result.Successful(user);
                    } else {
                        result.Failed("Wrong password");
                    }
                }).addOnFailureListener(e -> result.Failed(e.getMessage()));
    }

    @Override
    public void resetPassword(String email, UiState<String> result) {
        result.Loading();
        auth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful())  {
                        result.Successful("We sent a password reset link to your email!");
                    } else  {
                        result.Failed("Failed to send password reset link to $email");
                    }
                }).addOnFailureListener(e -> {
                    result.Failed(e.getMessage());
                });
    }

    @Override
    public void changePassword(FirebaseUser user, String password, UiState<String> result) {
        result.Loading();
        user.updatePassword(password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                result.Successful("Password change Successfully");
            } else {
                result.Failed("Wrong password!");
            }
        }).addOnFailureListener(e -> result.Failed(e.getMessage()));

    }

    @Override
    public void uploadProfile(String uid, Uri uri, UiState<String> result) {
        StorageReference reference = storage.getReference(Constants.ACCOUNTS_TABLE)
                .child(uid)
                .child(String.valueOf(System.currentTimeMillis()));
        result.Loading();
        reference.putFile(uri).addOnSuccessListener(taskSnapshot -> reference.getDownloadUrl().addOnSuccessListener(uri1 -> result.Successful(uri1.toString()))).addOnFailureListener(e -> {
            result.Failed(e.getMessage());
        });
    }

    @Override
    public void updateProfile(String uid,String name, String profile, UiState<String> result) {
        result.Loading();
        firestore.collection(Constants.ACCOUNTS_TABLE)
                .document(uid)
                .update("name",name,"profile",profile)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        result.Successful("Account updated Successfully");
                    } else {
                        result.Failed("Failed to update account!");
                    }
                }).addOnFailureListener(e -> result.Failed(e.getMessage()));
    }
}
