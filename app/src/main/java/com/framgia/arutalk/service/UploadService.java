package com.framgia.arutalk.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.icu.text.LocaleDisplayNames;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.framgia.arutalk.R;
import com.framgia.arutalk.model.Constant;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

/**
 * Created by Admin on 22/5/2017.
 */

public class UploadService extends Service {
    public final static String ACTION_UPLOAD_AVATAR = "com.framgia.arutalk.ACTION_UPLOAD_AVATAR";
    public final static String EXTRA_URI = "EXTRA_URI";

    private FirebaseStorage mFirebaseStorage;
    private StorageReference mStorageReference;
    private FirebaseUser mFirebaseUser;
    private DatabaseReference mDatabaseReference;

    @Override
    public void onCreate() {
        super.onCreate();
        mFirebaseStorage = FirebaseStorage.getInstance();
        mStorageReference = mFirebaseStorage.getReference();
        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null || intent.getAction() == null) {
            return START_STICKY;
        }
        switch (intent.getAction()) {
            case ACTION_UPLOAD_AVATAR:
                upload(intent);
                break;
            default:
                break;
        }
        return START_STICKY;
    }

    private void upload(Intent intent) {
        String uri_ = intent.getStringExtra(UploadService.EXTRA_URI);
        if (uri_ == null || uri_.isEmpty()) {
            return;
        }
        Uri uri = Uri.parse(uri_);
        StorageReference refAvatars = mStorageReference.child(Constant.Storage.AVATARS)
                .child(mFirebaseUser.getUid());
        refAvatars.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Uri uri = taskSnapshot.getDownloadUrl();
                updateUriPhotoFirebaseUser(uri);
                updateUriPhotoFirebaseStorage(uri);
                sendBroadcastUploadAvatarSuccessful(uri);
            }
        });
    }

    private void sendBroadcastUploadAvatarSuccessful(Uri uri) {
        Intent intent = new Intent();
        intent.setAction(Constant.BroadcastIntent.UploadAvatar.ACTION_UPLOAD_AVATAR_SUCCESSFUL);
        intent.putExtra(Constant.BroadcastIntent.UploadAvatar.EXTRA_UPLOAD_AVATAR_SUCCESSFUL, uri
            .toString());
        sendBroadcast(intent);
    }

    private void updateUriPhotoFirebaseUser(Uri uri) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                 .setPhotoUri(uri)
                .build();
        user.updateProfile(profileUpdates);
    }

    private void updateUriPhotoFirebaseStorage(Uri uri) {
        DatabaseReference refUriPhoto = mDatabaseReference.child(Constant.Database.USERS)
                .child(mFirebaseUser.getUid())
                .child(Constant.Database.User.URI_PHOTO);
        refUriPhoto.setValue(uri.toString());
    }
}
