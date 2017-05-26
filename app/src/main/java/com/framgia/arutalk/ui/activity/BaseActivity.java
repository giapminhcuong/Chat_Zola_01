package com.framgia.arutalk.ui.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;

/**
 * Created by Admin on 22/5/2017.
 */
public class BaseActivity extends AppCompatActivity {
    private ProgressDialog mProgressDialog;

    protected void showDialog(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        AlertDialog dialog = builder.setTitle(title)
            .setMessage(message)
            .setPositiveButton(android.R.string.ok, null)
            .create();
        dialog.show();
    }

    protected void showProgressDialog(String title, String msg) {
        mProgressDialog = ProgressDialog.show(this, title, msg);
    }

    protected void dismissKeyboard(View view) {
        InputMethodManager imm =
            (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    protected void dismissProgressDialog() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
    }
}
