package com.netlab.vc.coursehelper;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.netlab.vc.coursehelper.util.Constants;
import com.netlab.vc.coursehelper.util.Parameters;
import com.netlab.vc.coursehelper.util.WebConnection;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;

public class NewPostActivity extends AppCompatActivity {

    private EditText title;
    private EditText content;
    private Button submitPost;
    private String courseId,type,imagePath;
    private TextInputLayout title_layout;
    private TextInputLayout content_layout;
    private ImageButton addImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        courseId=getIntent().getStringExtra("course_id");
        type=getIntent().getStringExtra("type");
        title=(EditText)findViewById(R.id.edit_title);
        title_layout = (TextInputLayout)findViewById(R.id.edit_title_layout);
        content_layout = (TextInputLayout)findViewById(R.id.edit_content_layout);
        content=(EditText)findViewById(R.id.edit_content);
        submitPost=(Button)findViewById(R.id.submit_post);
        addImage=(ImageButton)findViewById(R.id.imageButton);
        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, RESULT_CANCELED);
            }
        });

        //输入文本框的焦点事件
        content.setOnFocusChangeListener(new View.OnFocusChangeListener(){
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                EditText edit=(EditText)v;
                if(hasFocus){
                    edit.setHint("");
                }
            }
        });
        //开启计数
        title_layout.setCounterEnabled(true);
        title_layout.setCounterMaxLength(20);//最大输入限制数
        content_layout.setCounterEnabled(true);
        content_layout.setCounterMaxLength(300);//最大输入限制数
        submitPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new CreatePostTask().execute();
            }
        });
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO Auto-generated method stub
        if(item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            Uri uri = data.getData();
            Log.e("uri", uri.getPath() + "");
            //new UploadImageTask().execute(uri.toString());

            String[] pojo = {MediaStore.Images.Media.DATA};

            Cursor cursor = getContentResolver().query(uri, pojo, null, null, null);

            if (cursor != null) {
                cursor.moveToFirst();
                int colunm_index = cursor.getColumnIndex(MediaStore.Images.Media.DATA);

                String path = cursor.getString(colunm_index);
                Log.e("path", path);
                try {
                    imagePath=encode(uri);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            else{
                Log.e("Cursor","null");
            }

        }
        else{
            Log.e("Result","123");
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    private String encode(Uri uri) throws Exception {
        InputStream inputStream=getContentResolver().openInputStream(uri);
        //decode to bitmap

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds=true;
        Bitmap bitmap = BitmapFactory.decodeStream(inputStream,null,options);
        int scale = (int)( options.outWidth / (float)300);
        if(scale <= 0)
            scale = 1;
        options.inSampleSize = scale;
        Log.e("scale",String.valueOf(scale));
        options.inJustDecodeBounds = false;
        inputStream=getContentResolver().openInputStream(uri);
        bitmap = BitmapFactory.decodeStream(inputStream,null,options);
        Log.d("bitmap", "bitmap width: " + bitmap.getWidth() + " height: " + bitmap.getHeight());
        //convert to byte array
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        bitmap.recycle();
        System.gc();
        byte[] bytes = baos.toByteArray();

        //base64 encode
        byte[] encode = Base64.encode(bytes,Base64.DEFAULT);
        return new String(encode);
    }
    public class CreatePostTask extends AsyncTask<Void,Void,Boolean> {

        private Editable mTitle;
        private Editable mContent;

        @Override
        public void onPreExecute() {
            mTitle = title.getText();
            mContent = content.getText();
            submitPost.setEnabled(false);
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            if(mTitle.length()>20)
                return false;
            try {
                ArrayList<Parameters> arrayList = new ArrayList<Parameters>();
                arrayList.add(new Parameters("_id", Constants._id));
                arrayList.add(new Parameters("user_id", Constants._id));
                arrayList.add(new Parameters("name",Constants.realname));
                arrayList.add(new Parameters("course_id", courseId));
                arrayList.add(new Parameters("title", mTitle.toString()));
                arrayList.add(new Parameters("content", mContent.toString()));
                if(imagePath!=null)
                    arrayList.add(new Parameters("img",imagePath));
                arrayList.add(new Parameters("post_type",type));
                Parameters parameters = WebConnection.connect(Constants.baseUrl + Constants.AddUrls.get("FORUM_POST"),
                        arrayList, WebConnection.CONNECT_POST);
                return parameters.name.equals("200");
            }
            catch(Exception e){
                return  false;
            }
        }
        @Override
        public void onPostExecute(Boolean success){
            if(!success){
                Toast.makeText(getApplicationContext(),"发帖失败！",Toast.LENGTH_SHORT).show();
                submitPost.setEnabled(true);
                return;
            }
            Toast.makeText(getApplicationContext(),"发帖成功！",Toast.LENGTH_SHORT).show();

            NewPostActivity.this.finish();

        }
    }

    public static View.OnFocusChangeListener onFocusAutoClearHintListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            EditText textView = (EditText) v;
            String hint;
            if (hasFocus) {
                hint = textView.getHint().toString();
                textView.setTag(hint);
                textView.setHint("");
            } else {
                hint = textView.getTag().toString();
                textView.setHint(hint);
            }
        }
    };
}
