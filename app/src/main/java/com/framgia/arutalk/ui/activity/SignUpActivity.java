package com.framgia.arutalk.ui.activity;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.framgia.arutalk.R;
import com.framgia.arutalk.model.Constant;
import com.framgia.arutalk.model.User;
import com.framgia.arutalk.service.UploadService;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

public class SignUpActivity extends BaseActivity implements View.OnClickListener {
    private static final int RESULT_LOAD_IMG = 1;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseStorage mFirebaseStorage;
    private FirebaseUser mFirebaseUser;
    private User mUser;
    private Uri mUriPhoto = null;
    private LinearLayout mLinearBackground;
    private ImageView mImageAvatar;
    private EditText mEditEmail;
    private EditText mEditPassword;
    private EditText mEditConfirmPassword;
    private EditText mEditFirstName;
    private EditText mEditLastName;
    private RadioButton mRadioMale;
    private RadioButton mRadioFemale;
    private Button mButtonRegister;
    private ProgressDialog mDialogRegisting;
    private BroadcastReceiver mReceiverUploadAvatarSuccessful = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        setTitle(R.string.title_register);
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseStorage = FirebaseStorage.getInstance();
        initViews();
        addEvents();
        registerReceiverUploadAvatarSuccessful();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMG && resultCode == RESULT_OK
            && data != null) {
            mUriPhoto = data.getData();
            // show selected image
            Glide.with(this)
                .load(mUriPhoto)
                .into(mImageAvatar);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.linear_background:
                dismissKeyboard(view);
                break;
            case R.id.button_register:
                registerUser();
                break;
            case R.id.image_avatar:
                getPicture();
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mReceiverUploadAvatarSuccessful != null) {
            unregisterReceiver(mReceiverUploadAvatarSuccessful);
        }
    }

    private void getPicture() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, RESULT_LOAD_IMG);
    }

    private void addEvents() {
        // dismiss keyboard when press background
        mLinearBackground.setOnClickListener(this);
        // when press register button
        mButtonRegister.setOnClickListener(this);
        // when choose avatar
        mImageAvatar.setOnClickListener(this);
    }

    private void initViews() {
        mLinearBackground = (LinearLayout) findViewById(R.id.linear_background);
        mImageAvatar = (ImageView) findViewById(R.id.image_avatar);
        mEditEmail = (EditText) findViewById(R.id.edit_email);
        mEditPassword = (EditText) findViewById(R.id.edit_password);
        mEditConfirmPassword = (EditText) findViewById(R.id.edit_confirm_password);
        mEditFirstName = (EditText) findViewById(R.id.edit_first_name);
        mEditLastName = (EditText) findViewById(R.id.edit_last_name);
        mRadioMale = (RadioButton) findViewById(R.id.radio_male);
        mRadioFemale = (RadioButton) findViewById(R.id.radio_female);
        mButtonRegister = (Button) findViewById(R.id.button_register);
    }

    private void registerUser() {
        // check empty
        if (!checkValidInput()) {
            return;
        }
        // check confirm password
        if (!checkConfirmPassword()) {
            return;
        }
        // register user with firebase
        String email = mEditEmail.getText().toString();
        String password = mEditPassword.getText().toString();
        // start show dialog loading
        startLoadingDialog();
        mFirebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    handleOnCompleteRegistration(task);
                    dismissLoadingDialog();
                }
            });
    }

    private void startLoadingDialog() {
        String titleLoading = getString(R.string.title_loading_registing);
        String msgLoading = getString(R.string.msg_loading_registing);
        mDialogRegisting = ProgressDialog.show(this, titleLoading, msgLoading);
    }

    private void dismissLoadingDialog() {
        if (mDialogRegisting != null) {
            mDialogRegisting.dismiss();
        }
    }

    private boolean checkValidInput() {
        if (mEditEmail.getText().toString().isEmpty()
            || mEditPassword.getText().toString().isEmpty()
            || mEditConfirmPassword.getText().toString().isEmpty()
            || mEditFirstName.getText().toString().isEmpty()
            || mEditLastName.getText().toString().isEmpty()) {
            String titleEmptyInputRegister = getString(R.string.title_empty_input_register);
            String msgEmptyInputRegister = getString(R.string.msg_empty_input_register);
            showDialog(titleEmptyInputRegister, msgEmptyInputRegister);
            return false;
        }
        return true;
    }

    private boolean checkConfirmPassword() {
        String password = mEditPassword.getText().toString();
        String confirmPassword = mEditConfirmPassword.getText().toString();
        if (!password.equals(confirmPassword)) {
            String msgTitleWrongPassword = getString(R.string.title_wrong_password);
            String msgWrongPassword = getString(R.string.msg_wrong_confirm_password);
            showDialog(msgTitleWrongPassword, msgWrongPassword);
            return false;
        }
        return true;
    }

    private void handleOnCompleteRegistration(Task<AuthResult> task) {
        // if register successful
        if (task.isSuccessful()) {
            mFirebaseUser = mFirebaseAuth.getCurrentUser();
            // upload avatar to storage
            uploadAvatarToStorage();
            // update display name to firebase user
            updateDisplayNameUserProfile();
            // create java object user and store to database
            mUser = makeUser();
            writeUserToDatabase(mUser);
            resetUi();
        }
        // else register faild
        else {
            String title_register_faild = getString(R.string.title_register_faild);
            String msg_register_faild = getString(R.string.msg_register_faild);
            showDialog(title_register_faild, msg_register_faild);
        }
    }

    private void uploadAvatarToStorage() {
        // if user dont choose avatar
        if (mUriPhoto == null) {
            return;
        }
        // if use choose avatar then upload avatar
        Intent intent = new Intent(this, UploadService.class);
        intent.setAction(UploadService.ACTION_UPLOAD_AVATAR);
        intent.putExtra(UploadService.EXTRA_URI, mUriPhoto.toString());
        startService(intent);
    }

    private void updateDisplayNameUserProfile() {
        String firstName = mEditFirstName.getText().toString();
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
            .setDisplayName(firstName)
            .build();
        mFirebaseUser.updateProfile(profileUpdates);
    }

    private User makeUser() {
        String email = mFirebaseUser.getEmail();
        String firstName = mEditFirstName.getText().toString();
        String lastName = mEditLastName.getText().toString();
        User.Gender gender = (mRadioMale.isChecked()) ? User.Gender.MALE : User.Gender.FEMALE;
        User u = new User(mFirebaseUser.getUid(), email, firstName, lastName, gender);
        return u;
    }

    private void writeUserToDatabase(User u) {
        DatabaseReference refUser =
            FirebaseDatabase.getInstance().getReference().child(Constant.Database.USERS);
        refUser.child(u.getUserId()).setValue(u);
    }

    private void resetUi() {
        mEditEmail.setText("");
        mEditPassword.setText("");
        mEditConfirmPassword.setText("");
        mEditFirstName.setText("");
        mEditLastName.setText("");
        mImageAvatar.setImageResource(R.drawable.ic_user);
        mRadioMale.setChecked(true);
    }

    private void registerReceiverUploadAvatarSuccessful() {
        IntentFilter filter = new IntentFilter(Constant.BroadcastIntent.UploadAvatar
            .ACTION_UPLOAD_AVATAR_SUCCESSFUL);
        mReceiverUploadAvatarSuccessful = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                notifyUploadAvatarSuccessful();
            }
        };
        registerReceiver(mReceiverUploadAvatarSuccessful, filter);
    }

    private void notifyUploadAvatarSuccessful() {
        String msg = getString(R.string.msg_upload_avatar_successful);
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}
