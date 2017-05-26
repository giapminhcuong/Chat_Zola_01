package com.framgia.arutalk.ui.activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.framgia.arutalk.R;
import com.framgia.arutalk.model.Constant;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignInActivity extends BaseActivity implements View.OnClickListener {
    private EditText mEditEmail;
    private EditText mEditPassword;
    private Button mButtonSignIn;
    private TextView mTextRegister;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        initViews();
        addEvents();
        mFirebaseAuth = FirebaseAuth.getInstance();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        if (mFirebaseUser != null) {
            jumpToMainActivity();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.text_register:
                jumpToSignUpActivity();
                break;
            case R.id.button_sign_in:
                onPressButtonSignIn();
                break;
            default:
                break;
        }
    }

    private void jumpToMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void jumpToSignUpActivity() {
        Intent intent = new Intent(this, SignUpActivity.class);
        startActivity(intent);
    }

    private void initViews() {
        mEditEmail = (EditText) findViewById(R.id.edit_email);
        mEditPassword = (EditText) findViewById(R.id.edit_password);
        mButtonSignIn = (Button) findViewById(R.id.button_sign_in);
        mTextRegister = (TextView) findViewById(R.id.text_register);
    }

    private void addEvents() {
        mTextRegister.setOnClickListener(this);
        mButtonSignIn.setOnClickListener(this);
    }

    private void onPressButtonSignIn() {
        String titleSigningIn = getString(R.string.title_sign_in);
        String msgSigningIn = getString(R.string.msg_signing_in);
        if (!checkInputSignIn()) {
            String title = getString(R.string.title_invalid_input_sign_in);
            String msg = getString(R.string.msg_invalid_input_sign_in);
            showDialog(title, msg);
            return;
        }
        showProgressDialog(titleSigningIn, msgSigningIn);
        String email = mEditEmail.getText().toString();
        String password = mEditPassword.getText().toString();
        mFirebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    onCompleteSignIn(task);
                    dismissProgressDialog();
                }
            });
    }

    private void onCompleteSignIn(Task<AuthResult> task) {
        // if sign in successful
        if (task.isSuccessful()) {
            mFirebaseUser = mFirebaseAuth.getCurrentUser();
            jumpToMainActivity();
        }
        // if sign in faild
        else {
            String title = getString(R.string.title_sign_in_faild);
            String msg = getErrorMessage(task);
            showDialog(title, msg);
        }
    }

    private String getErrorMessage(Task<AuthResult> task) {
        String msg = task.getException().toString();
        return msg.split(Constant.REGEX_SPLIT_ERROR_SIGN_IN)[1].trim();
    }

    private boolean checkInputSignIn() {
        if (mEditEmail.getText().toString().isEmpty()
            || mEditPassword.getText().toString().isEmpty()) {
            return false;
        }
        return true;
    }
}
