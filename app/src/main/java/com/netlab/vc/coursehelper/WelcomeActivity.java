package com.netlab.vc.coursehelper;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.netlab.vc.coursehelper.util.DeanDecode;

public class WelcomeActivity extends AppCompatActivity {

    public static WelcomeActivity welcomeActivity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        //new GetPictureTask().execute();
        Intent intent=new Intent(this,LoginActivity.class);
        startActivity(intent);
    }
    @Override
    public void onResume(){
        super.onResume();
        Intent intent=new Intent(this,LoginActivity.class);
        startActivity(intent);
    }
    private class GetPictureTask extends AsyncTask<Void,Void,Boolean>{

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                //InputStream is= WebConnection.connect("http://222.29.98.104:8000/ct2.jpg");
                //Bitmap bm= BitmapFactory.decodeStream(is);
                //BitmapDrawable bd = new BitmapDrawable(bm);
                Drawable d = getResources().getDrawable(R.drawable.ct2);
                Log.e("result", DeanDecode.decode(d));
                return true;
            } catch (Exception e) {
                return false;
            }
        }
    }
}
