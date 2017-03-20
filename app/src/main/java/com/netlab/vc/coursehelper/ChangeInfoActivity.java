package com.netlab.vc.coursehelper;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.netlab.vc.coursehelper.util.Constants;
import com.netlab.vc.coursehelper.util.Editor;
import com.netlab.vc.coursehelper.util.Parameters;
import com.netlab.vc.coursehelper.util.RegexValidater;
import com.netlab.vc.coursehelper.util.WebConnection;
import com.netlab.vc.coursehelper.util.jsonResults.UserInfo;

import java.util.ArrayList;

/**
 * Created by dingfeifei on 16/12/7.
 */

public class ChangeInfoActivity extends AppCompatActivity {

    private ChangeInfoTask mAuthTask = null;
    //UI interfaces.
    private AutoCompleteTextView mUsernameView;
    private EditText mPasswordView;
    private EditText mConfirmPassView;
    private EditText mRealnameView;
    private EditText mPhoneNumberView;
    private EditText mEmailView;
    private View mProgressView;
    private View mRegisterFormView;
    private Button changeinfoButton;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        //Link UI interface
        setContentView(R.layout.activity_change_info);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mRegisterFormView=findViewById(R.id.s_change_info_form);
        mProgressView=findViewById(R.id.change_info_progress);
        mUsernameView=(AutoCompleteTextView)findViewById(R.id.change_info_username);
        Log.e("username",Constants.username);
        mUsernameView.setTextColor(Color.rgb(0, 0, 0));
        mUsernameView.setText(Constants.username);
        mUsernameView.setEnabled(false);
        mPasswordView=(EditText)findViewById(R.id.change_info_password);
        mPasswordView.setText(Constants.password);
        mConfirmPassView=(EditText)findViewById(R.id.change_info_confirm_password);
        mConfirmPassView.setText(Constants.password);
        mRealnameView=(EditText)findViewById(R.id.change_info_realname);
        mRealnameView.setText(Constants.realname);
        mPhoneNumberView=(EditText)findViewById(R.id.change_info_phone_number);
        mPhoneNumberView.setText(Constants.phone);
        mEmailView=(EditText)findViewById(R.id.change_info_email);
        mEmailView.setText(Constants.email);
        changeinfoButton=(Button)findViewById(R.id.change_info_button);
        changeinfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptChangeInfo();
                Toast.makeText(getApplicationContext(),"修改成功！",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void attemptChangeInfo() {
        View focusView=null;
        Boolean isCorrect=true;
        String username=mUsernameView.getText().toString();
        String password=mPasswordView.getText().toString();
        String confirmPass=mConfirmPassView.getText().toString();
        String email=mEmailView.getText().toString();
        String phoneNumber=mPhoneNumberView.getText().toString();
        String realName=mRealnameView.getText().toString();
        int stuID;
        try{
            stuID=Integer.parseInt(username);
        }
        catch (NumberFormatException e){
            stuID=0;
        }
        if(stuID<1000000000||stuID>=2000000000){
            isCorrect=false;
            mUsernameView.setError("请将用户名设为你的学号");
            focusView=mUsernameView;
        }
        else if(password.length()<6||password.length()>=18){
            isCorrect=false;
            mPasswordView.setError("密码限制在6~18位");
            focusView=mPasswordView;

        }
        else if(!password.equals(confirmPass)){
            isCorrect=false;
            mConfirmPassView.setError("两次输入的密码不一致");
            focusView=mConfirmPassView;
        }
        else if(!RegexValidater.checkEmail(email)){
            isCorrect=false;
            mEmailView.setError("邮箱格式错误");
            focusView=mEmailView;

        }
        else if(!RegexValidater.checkMobileNumber(phoneNumber)){
            isCorrect=false;
            mPhoneNumberView.setError("手机号错误");
            focusView=mPhoneNumberView;
        }
        if(!isCorrect){
            focusView.requestFocus();
        }
        else{
            mAuthTask = new ChangeInfoTask(username, password, email, phoneNumber, realName);
            mAuthTask.execute((Void) null);
        }
    }

    @Override
    public void onBackPressed(){
        Log.e("BackPressed","1");
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // TODO Auto-generated method stub
        if(item.getItemId() == android.R.id.home)
        {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public class ChangeInfoTask extends AsyncTask<Void,Void,Boolean> {
        private String mUsername,mPassword,mEmail,mPhoneNumber,mRealName;
        ChangeInfoTask(String username,String password,String email,String phoneNumber,String realName){
            mUsername=username;
            mPassword=password;
            mEmail=email;
            mPhoneNumber=phoneNumber;
            mRealName=realName;
        }
        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                ArrayList<Parameters> arrayList = new ArrayList<Parameters>();
                arrayList.add(new Parameters("_id",Constants._id));
                arrayList.add(new Parameters("name",mUsername));
                arrayList.add(new Parameters("password",mPassword));
                arrayList.add(new Parameters("realName",mRealName));
                arrayList.add(new Parameters("phone",mPhoneNumber));
                arrayList.add(new Parameters("email",mEmail));
                Parameters parameters = WebConnection.connect(Constants.baseUrl+Constants.AddUrls.get("INFO"),
                        arrayList,WebConnection.CONNECT_POST);
                UserInfo changeInfoResult = new Gson().fromJson(parameters.value,UserInfo.class);
                if(changeInfoResult.getSuccess()) {
                    Constants.password = mPassword;
                    Constants.realname = mRealName;
                    Constants.email = mEmail;
                    Constants.phone = mPhoneNumber;
                    return true;
                }
                else
                    return false;
            } catch (Exception e) {
                return false;
            }
        }
        /**
         * Shows the progress UI and hides the login form.
         */
        @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
        private void showProgress(final boolean show) {
            // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
            // for very easy animations. If available, use these APIs to fade-in
            // the progress spinner.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
                int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

                mRegisterFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                mRegisterFormView.animate().setDuration(shortAnimTime).alpha(
                        show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mRegisterFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                    }
                });

                mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                mProgressView.animate().setDuration(shortAnimTime).alpha(
                        show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                    }
                });
            } else {
                // The ViewPropertyAnimator APIs are not available, so simply show
                // and hide the relevant UI components.
                mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                mRegisterFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            }
        }

        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if (success) {
                //save the password?
                Editor.putString(getApplicationContext(),"password",mPassword);
                Editor.putString(getApplicationContext(),"username",mUsername);
                ChangeInfoActivity.this.finish();
            } else {
                Log.e("123","修改失败!");
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }

}