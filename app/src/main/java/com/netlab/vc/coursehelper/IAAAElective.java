package com.netlab.vc.coursehelper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.filters.AndFilter;
import org.htmlparser.filters.HasAttributeFilter;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class IAAAElective extends AppCompatActivity {

    class Course{
        String name;
        int full;   //0 for unknown, 1 for FULL, 2 for SELECT.
        boolean wanted;
        Course(){
            full=0;
            name="";
            wanted=false;

        }
    }
    ImageView imageView;
    ListView listView;
    TextView textView;
    String cookie;
    Button buttonCheck,buttonSelect;
    EditText editText;
    RefreshCourseTask refreshCourseTask;
    Timer timer = new Timer();
    SoundPool soundPool;
    Course[] courses=new Course[50];
    String selectHref;
    String courseNameRegex="(?<=refreshLimit\\(')\\w+";
    String courseNameRegexSelect="(?<=confirmSelect\\(')\\w+";
    String courseIdRegex="(?<='',')\\w+";
    String selectRegex="/elective2008/edu/pku/stu/elective/controller/supplement/electSupplement" +
            ".do\\?index=\\w+&amp;seq=\\w+";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_iaaaelective);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        imageView=(ImageView)findViewById(R.id.validcode_img);
        textView=(TextView)findViewById(R.id.web_content);
        buttonCheck=(Button)findViewById(R.id.button_check);
        editText=(EditText)findViewById(R.id.user_validcode);
        listView=(ListView)findViewById(R.id.elective_list);
        textView.setMovementMethod(ScrollingMovementMethod.getInstance());
        setSupportActionBar(toolbar);
        cookie=getIntent().getStringExtra("cookie");
        refreshCourseTask=new RefreshCourseTask();
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        new RefreshCourseTask().execute(";0");
        new RefreshCourseTask().execute(";20");
        soundPool= new SoundPool(10,AudioManager.STREAM_SYSTEM,5);
        soundPool.load(this,R.raw.beep,1);
        buttonCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new CheckValidTask().execute();
            }
        });
        new GetValidateTask().execute();        //获取验证码
        timer.schedule(task, 5000, 10000);       // timeTask 定时扫描


    }
    TimerTask task = new TimerTask() {
        int a=0;
        String page[]={";0",";20"};
        @Override
        public void run() {

            runOnUiThread(new Runnable() {      // UI thread
                @Override
                public void run() {
                    new RefreshCourseTask().execute(page[a]);
                    a=1-a;
                }
            });
        }
    };
    private class RefreshCourseTask extends AsyncTask<String,Void,Boolean>{

        String content=null;
        int p=0;
        @Override
        protected Boolean doInBackground(String... params) {
            /*
            HttpParams httpParams = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParams, 4000);
            HttpConnectionParams.setSoTimeout(httpParams, 13000);
            DefaultHttpClient httpClient = new DefaultHttpClient(httpParams);
            HttpGet httpGet = new HttpGet("http://elective.pku.edu.cn/elective2008/edu/pku/stu/elective/controller/supplement/SupplyCancel.do?netui_row=electableListGrid"+params[p]);
            httpGet.setHeader("Referer", "http://elective.pku.edu.cn/elective2008/edu/pku/stu/elective/controller/supplement/SupplyCancel.do");
            httpGet.setHeader("Cookie", cookie);
            Log.e("123",params[0]);
            try {
                HttpResponse httpResponse=httpClient.execute(httpGet);
                BufferedReader bf;

                bf = new BufferedReader(
                        new InputStreamReader(httpResponse.getEntity().getContent()));

                String line = bf.readLine();
                while (line != null) {
                    content = content + line + "\n";
                    line = bf.readLine();
                }
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
            return true;
            */
            content=webConnect("http://elective.pku.edu.cn/elective2008/edu/pku/stu/elective/controller/supplement/SupplyCancel.do?netui_row=electableListGrid"+params[p]);
            return(content!=null);
        }
        @Override
        public void onPostExecute(Boolean success){
            if(!success){
                Vibrator vibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);

                vibrator.vibrate(new long[]{300,500},0);
                return;
            }

            try {
                Parser parser=new Parser();
                parser.setInputHTML(content);

                NodeFilter filter1 = new HasAttributeFilter("href");
                NodeFilter filter2=new HasAttributeFilter("style","width: 30");
                NodeFilter filter = new AndFilter(filter1,filter2);
                NodeList nodeList = parser.extractAllNodesThatMatch(filter);
                for(int i = 0; i<nodeList.size();i++) {
                    Node node = nodeList.elementAt(i);
                    //Log.e("node",node.getText());
                    String line=node.getText();

                    Pattern courseNamePattern= Pattern.compile(courseNameRegex);
                    Pattern courseNamePatternSelect=Pattern.compile(courseNameRegexSelect);
                    Pattern courseIdPattern=Pattern.compile(courseIdRegex);
                    Pattern selectHrefPattern=Pattern.compile(selectRegex);
                    Matcher courseNameMat = courseNamePattern.matcher(line);
                    Matcher courseNameSelectMat=courseNamePatternSelect.matcher(line);
                    Matcher courseIdMat=courseIdPattern.matcher(line);
                    Matcher selectHrefMat=selectHrefPattern.matcher(line);
                    //courseNameMat.find();
                    //courseIdMat.find();
                    //courseNameSelectMat.find();
                    if(courseNameMat.find()&&courseIdMat.find()){
                        String courseName=courseNameMat.group();
                        int courseId=Integer.parseInt(courseIdMat.group());
                        courses[courseId]=new Course();
                        courses[courseId].name=courseName;
                        courses[courseId].full=1;
                    }
                    else if(courseNameSelectMat.find()&&courseIdMat.find()&&selectHrefMat.find()){
                        String courseName=courseNameSelectMat.group();
                        int courseId=Integer.parseInt(courseIdMat.group());
                        Log.e(String.valueOf(courseId),courseName);
                        courses[courseId]=new Course();
                        courses[courseId].name= courseName;
                        if(editText.getText().length()==4&&(courses[courseId].full==1&&courseName.contains("英语词汇学"))){    //验证码格式正确，且该课程原本处于满员状态
                            Log.e("Select course!",courseName);
                            /*
                            selectHref=selectHrefMat.group();
                            if(selectHref.contains("index=1")||selectHref.contains("index=2")){
                                webConnect("http://elective.pku.edu.cn/elective2008/edu/pku/stu/elective/controller/supplement/cancelCourse.do?index=0&seq=BKC03835230AT0008067");

                            }
                            */
                            //get class!!!
                            Vibrator vibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
                            //textView.setText(courseId+","+courseName+"\n");
                            vibrator.vibrate(new long[]{300,500},0);
                            soundPool.play(1,1, 1, 0, -1, 1);
                            new ConfirmSelectTask().execute(editText.getText().toString(),selectHref);
                        }
                        courses[courseId].full=2;
                    }

                }
            } catch (ParserException e) {
                e.printStackTrace();
            }

            //textView.setText(content);
            if(content.contains("28 / 27")||content.contains("22 / 21")||content.contains("30 / 29")
                    ||content.contains("480 / 479")||content.contains("480 / 478")||content.contains("刷课机")
                    ||content.contains("重新登录")||content.contains("480 / 477")
                    ||content.contains("480 / 476")||content.contains("30 / 28")){
                Vibrator vibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);

                vibrator.vibrate(new long[]{300,500},0);
                soundPool.play(1,1, 1, 0, -1, 1);
            }

        }
    }
    private class GetValidateTask extends AsyncTask<Void,Void,Boolean>{

        Bitmap bitmap;
        @Override
        protected Boolean doInBackground(Void... params) {
            HttpParams httpParams = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParams, 4000);
            HttpConnectionParams.setSoTimeout(httpParams, 13000);
            DefaultHttpClient httpClient = new DefaultHttpClient(httpParams);
            String validCodeUrl="http://elective.pku.edu.cn/elective2008/DrawServlet?Rand="+String.valueOf(Math.random()*10000);
            Log.e("url",validCodeUrl);
            HttpGet httpGet = new HttpGet(validCodeUrl);
            httpGet.setHeader("Referer", "http://elective.pku.edu.cn/elective2008/edu/pku/stu/elective/controller/supplement/SupplyCancel.do");
            httpGet.setHeader("Cookie", cookie);
            try {
                HttpResponse httpResponse = httpClient.execute(httpGet);
                InputStream is=httpResponse.getEntity().getContent();
                bitmap = BitmapFactory.decodeStream(is);
                return true;
            }
            catch(Exception e){
                return false;
            }
        }
        @Override
        public void onPostExecute(Boolean success){
            if(!success)return;
            imageView.setImageBitmap(bitmap);
        }
    }
    boolean checkValidateCode(String str){
        if(str.length()!=4)
            return false;
        HttpParams httpParams = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParams, 4000);
        HttpConnectionParams.setSoTimeout(httpParams, 13000);
        DefaultHttpClient httpClient = new DefaultHttpClient(httpParams);
        HttpGet httpGet=new HttpGet("http://elective.pku.edu.cn/elective2008/edu/pku/stu/elective/controller/supplement/validate.do?validCode="+str);
        httpGet.setHeader("Referer", "http://elective.pku.edu.cn/elective2008/edu/pku/stu/elective/controller/supplement/SupplyCancel.do");
        httpGet.setHeader("Cookie", cookie);
        String content="";
        try {
            HttpResponse httpResponse=httpClient.execute(httpGet);
            BufferedReader bf;

            bf = new BufferedReader(
                    new InputStreamReader(httpResponse.getEntity().getContent()));
            String line = bf.readLine();
            while (line != null) {
                content = content + line + "\n";
                line = bf.readLine();
            }
            return content.contains("<valid>2</valid>");
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
    private class ConfirmSelectTask extends AsyncTask<String,Void,Boolean>{

        String content=null;
        @Override
        protected Boolean doInBackground(String... params) {
            Log.e(params[0],"Start to select course!");
            if(!checkValidateCode(params[0])){
                Toast.makeText(getApplicationContext(),"验证码错误！",Toast.LENGTH_SHORT).show();
                return false;
            }
            String url="http://elective.pku.edu.cn"+params[1].replace("&amp;","&");
            content=webConnect(url);
            return (content!=null);
            /*
            HttpParams httpParams = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParams, 4000);
            HttpConnectionParams.setSoTimeout(httpParams, 13000);
            DefaultHttpClient httpClient = new DefaultHttpClient(httpParams);

            HttpGet httpGet=new HttpGet("http://elective.pku.edu.cn"+electSuffix);
            Log.e("select url","http://elective.pku.edu.cn"+electSuffix);
            httpGet.setHeader("Referer", "http://elective.pku.edu.cn/elective2008/edu/pku/stu/elective/controller/supplement/SupplyCancel.do");
            httpGet.setHeader("Cookie", cookie);
            try {
                HttpResponse httpResponse=httpClient.execute(httpGet);
                BufferedReader bf;

                bf = new BufferedReader(
                        new InputStreamReader(httpResponse.getEntity().getContent()));

                String line = bf.readLine();
                while (line != null) {
                    content = content + line + "\n";
                    line = bf.readLine();
                }


            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
            return true;
            */

        }
        @Override
        public void onPostExecute(Boolean success){
            timer.cancel();
            soundPool.play(1,1, 1, 0, -1, 1);
            if(!success){
                textView.setText("验证码错误！");
                return;
            }
            if(content.contains("message_success")){
                textView.setText("补选成功！");
            }
            else if(content.contains("message_error")){
                textView.setText("补选失败！");
            }
            else{
                textView.setText("未知错误！");
            }
        }
    }
    private class CheckValidTask extends AsyncTask<Void,Void,Boolean>{

        String validCode;
        @Override
        public void onPreExecute(){
            validCode=editText.getText().toString();
        }
        @Override
        protected Boolean doInBackground(Void... params) {
            return checkValidateCode(validCode);
        }
    }
    String webConnect(String url){
        String content=null;
        HttpParams httpParams = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParams, 4000);
        HttpConnectionParams.setSoTimeout(httpParams, 13000);
        DefaultHttpClient httpClient = new DefaultHttpClient(httpParams);
        //String electSuffix=params[1].replace("&amp;","&");
        HttpGet httpGet=new HttpGet(url);
        Log.e("visit_url",url);
        httpGet.setHeader("Referer", "http://elective.pku.edu.cn/elective2008/edu/pku/stu/elective/controller/supplement/SupplyCancel.do");
        httpGet.setHeader("Cookie", cookie);
        try {
            HttpResponse httpResponse=httpClient.execute(httpGet);
            BufferedReader bf;

            bf = new BufferedReader(
                    new InputStreamReader(httpResponse.getEntity().getContent()));

            String line = bf.readLine();
            while (line != null) {
                content = content + line + "\n";
                line = bf.readLine();
            }


        } catch (IOException e) {
            e.printStackTrace();

        }
        return content;
    }
}
