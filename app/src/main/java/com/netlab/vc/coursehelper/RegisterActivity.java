package com.netlab.vc.coursehelper;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.google.gson.Gson;
import com.netlab.vc.coursehelper.util.Constants;
import com.netlab.vc.coursehelper.util.Editor;
import com.netlab.vc.coursehelper.util.Parameters;
import com.netlab.vc.coursehelper.util.RegexValidater;
import com.netlab.vc.coursehelper.util.WebConnection;
import com.netlab.vc.coursehelper.util.jsonResults.LoginResult;
import com.netlab.vc.coursehelper.util.jsonResults.UserInfo;

import java.util.ArrayList;

/**
 * Created by Vc on 2016/11/23.
 */

public class RegisterActivity extends AppCompatActivity {

    private RegisterTask mAuthTask = null;
    //UI interfaces.
    private AutoCompleteTextView mUsernameView;
    private EditText mPasswordView;
    private EditText mConfirmPassView;
    private EditText mRealnameView;
    private EditText mPhoneNumberView;
    private EditText mEmailView;
    private View mProgressView;
    private View mRegisterFormView;
    private CheckBox cbSavePass;
    private CheckBox cbAutoLogin;
    private Button registerButton;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        //Link UI interface
        setContentView(R.layout.activity_register);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mRegisterFormView=findViewById(R.id.s_register_form);
        mProgressView=findViewById(R.id.register_progress);
        mUsernameView=(AutoCompleteTextView)findViewById(R.id.register_username);
        mPasswordView=(EditText)findViewById(R.id.register_password);
        mConfirmPassView=(EditText)findViewById(R.id.register_confirm_password);
        mRealnameView=(EditText)findViewById(R.id.register_realname);
        mPhoneNumberView=(EditText)findViewById(R.id.register_phone_number);
        mEmailView=(EditText)findViewById(R.id.register_email);
        cbSavePass=(CheckBox)findViewById(R.id.checkBox_remember);
        cbAutoLogin=(CheckBox)findViewById(R.id.checkBox_autologin);
        registerButton=(Button)findViewById(R.id.register_button);
        Intent intent=getIntent();
        mUsernameView.setText(intent.getStringExtra("username"));
        mPasswordView.setText(intent.getStringExtra("password"));
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptRegister();
            }
        });
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
    private void attemptRegister() {
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
        /*
        if(stuID<1000000000||stuID>=2000000000){
            isCorrect=false;
            mUsernameView.setError("请将用户名设为你的学号");
            focusView=mUsernameView;
        }

        else*/ if(password.length()<6||password.length()>=18){
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
            showProgress(true);
            mAuthTask = new RegisterTask(username, password, email, phoneNumber, realName);
            mAuthTask.execute((Void) null);
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
    public class RegisterTask extends AsyncTask<Void,Void,Boolean> {
        private String mUsername,mPassword,mEmail,mPhoneNumber,mRealName;
        RegisterTask(String username,String password,String email,String phoneNumber,String realName){
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
                arrayList.add(new Parameters("name",mUsername));
                arrayList.add(new Parameters("password",mPassword));
                arrayList.add(new Parameters("realName",mRealName));
                arrayList.add(new Parameters("phone",mPhoneNumber));
                arrayList.add(new Parameters("email",mEmail));
                Parameters parameters = WebConnection.connect(Constants.baseUrl+Constants.AddUrls.get("REGISTER"),
                        arrayList,WebConnection.CONNECT_POST);
                LoginResult loginResult = new Gson().fromJson(parameters.value,LoginResult.class);
                if(loginResult.getSuccess()) {
                    Constants.password = mPassword;
                    Constants._id = loginResult.get_id();
                    Constants.token = loginResult.getToken();
                    arrayList = new ArrayList<Parameters>();
                    arrayList.add(new Parameters("_id",Constants._id));
                    parameters = WebConnection.connect(Constants.baseUrl+Constants.AddUrls.get("INFO"),
                            arrayList,WebConnection.CONNECT_GET);
                    UserInfo userInfo=new Gson().fromJson(parameters.value,UserInfo.class);
                    if(userInfo.getSuccess()){
                        Constants.realname=userInfo.getRealName();
                        Constants.phone=userInfo.getPhone();
                        Constants.email=userInfo.getEmail();
                        Constants.type=userInfo.getType();
                        Constants.username=userInfo.getName();
                        Constants.admin=userInfo.isAdmin();
                        //Constants.avatars=userInfo.getAvatars();
                    }
                    return true;
                }
                else
                    return false;
            } catch (Exception e) {
                return false;
            }
        }

        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if (success) {
                //save the password?
                Editor.putString(getApplicationContext(),"password",mPassword);
                Editor.putString(getApplicationContext(),"username",mUsername);
                Intent jumpToMain = new Intent(RegisterActivity.this,MainActivity.class);
                RegisterActivity.this.startActivity(jumpToMain);
                RegisterActivity.this.finish();
            } else {
                Log.e("123","注册失败!");
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }
}
