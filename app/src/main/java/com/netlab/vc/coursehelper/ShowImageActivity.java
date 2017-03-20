package com.netlab.vc.coursehelper;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;


public class ShowImageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_image);
        String base64Img=getIntent().getStringExtra("image");
        ImageView imageView=(ImageView)findViewById(R.id.show_image);
        imageView.setImageBitmap(PostDetailActivity.decode(base64Img));
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        /*
        Bitmap bm=getIntent().getParcelableExtra("image");

        imageView.setImageBitmap(bm);

        */
    }
}
