package com.netlab.vc.coursehelper;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseSettings;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings.Secure;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.gson.Gson;
import com.netlab.vc.coursehelper.util.Constants;
import com.netlab.vc.coursehelper.util.Parameters;
import com.netlab.vc.coursehelper.util.WebConnection;
import com.netlab.vc.coursehelper.util.jsonResults.Course;
import com.netlab.vc.coursehelper.util.jsonResults.SignInInfo;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.BeaconTransmitter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 * Created by dingfeifei on 16/11/20.
 */


/*
 * 课程信息展示界面
 * 可以跳转到课程的所有功能
 * 目前的功能有签到、通知、测试、内容、论坛和小组
 */
public class CourseActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    //分别进入通知、签到、测试、内容、论坛和小组界面的按钮
    private LinearLayout announcementList, testList, contentList, forumList, groupList;
    private TextView courseName, courseTeacher, courseDate, signUpedInfo, absenceInfo;//各种数据的Textview
    private Course course;//当前course的信息
    private Button signUp;//签到按钮
    private SwipeRefreshLayout refreshLayout;
    private ProgressBar progressBar;
    private ImageView courseImg;

    protected static final String TAG = "CourseActivity";//LOG用到的标记
    private String course_id;
    private Set<String> UIDs;//学生扫描得到的uuids
    private String student_uuid;
    private SignInInfo signInInfo;
    private BluetoothAdapter.LeScanCallback mLeScanCallback;
    private BluetoothAdapter mBluetoothAdapter;
    private String deviceId;
    //teacher's tool to start signin
    private Boolean signUpStats = false;//老师修改signUpStats
    private String teacher_uuid;
    public static final String IBEACON_FORMAT = "m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24";
    private String signinId;
    BeaconTransmitter beaconTransmitter;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.activity_course);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//显示返回箭头
        Intent intent = getIntent();
        course_id = intent.getStringExtra("course_id");
        Log.e("course_id", course_id);
        deviceId = Secure.getString(getContentResolver(),
                Secure.ANDROID_ID);
        Log.e("Device id",deviceId);
        findViews();
        //getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.titlebar);
        setListeners();
        getData();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
        //new RegisterTask().execute();
    }

    public class RegisterTask extends AsyncTask<Void,Void,Boolean>{

        @Override
        protected Boolean doInBackground(Void... params) {
            ArrayList<Parameters> arrayList = new ArrayList<Parameters>();
            arrayList.add(new Parameters("_id", Constants._id));
            arrayList.add(new Parameters("course_id", course_id));
            arrayList.add(new Parameters("uuid", teacher_uuid));
            Parameters parameters = WebConnection.connect(Constants.privateBaseUrl + Constants.AddUrls.get("SIGN_REGISTER"),
                    arrayList, WebConnection.CONNECT_POST);
            return parameters.name.equals("200");
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO Auto-generated method stub
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void findViews() {
        announcementList = (LinearLayout) findViewById(R.id.announcement_list);
        testList = (LinearLayout) findViewById(R.id.test_list);
        contentList = (LinearLayout) findViewById(R.id.content_list);
        forumList = (LinearLayout) findViewById(R.id.forum_list);
        groupList = (LinearLayout) findViewById(R.id.group_list);
        courseName = (TextView) findViewById(R.id.course_name);
        courseTeacher = (TextView) findViewById(R.id.course_teacher);
        courseDate = (TextView) findViewById(R.id.course_date);
        signUpedInfo = (TextView) findViewById(R.id.sign_uped_info);
        absenceInfo = (TextView) findViewById(R.id.absence_info);
        signUp = (Button) findViewById(R.id.sign_up);
        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.refreshLayout);
        progressBar = (ProgressBar) findViewById(R.id.load_progress);
        courseImg=(ImageView)findViewById(R.id.course_img);
        courseImg.setImageResource(R.drawable.mysql);
        UIDs=new HashSet<>();
    }

    private void setListeners() {
        announcementList.setOnClickListener(new LinearLayout.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(CourseActivity.this, AnnouncementActivity.class);
                intent.putExtra("course_id",course_id);
                startActivity(intent);
            }
        });
        testList.setOnClickListener(new LinearLayout.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(CourseActivity.this, TestListActivity.class);
                intent.putExtra("course_id", course_id);
                startActivity(intent);
            }
        });
        contentList.setOnClickListener(new LinearLayout.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(CourseActivity.this, CourseContentActivity.class);
                intent.putExtra("course_id", course_id);
                startActivity(intent);
            }
        });
        forumList.setOnClickListener(new LinearLayout.OnClickListener() {
            public void onClick(View v) {

                Intent intent = new Intent(CourseActivity.this, ForumActivity.class);
                intent.putExtra("course_id", course_id);
                startActivity(intent);
            }
        });
        groupList.setOnClickListener(new LinearLayout.OnClickListener() {
            public void onClick(View v) {
                if (Constants.groupStage.equals("1")) {
                    Intent intent = new Intent(CourseActivity.this, GroupActivity.class);
                    intent.putExtra("course_id", course_id);
                    startActivity(intent);
                }
                else{
                    Intent intent = new Intent(CourseActivity.this, GroupSecondActivity.class);
                    intent.putExtra("course_id", course_id);
                    startActivity(intent);
                }
            }
        });
        refreshLayout.setOnRefreshListener(this);
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Constants.admin) {
                    attemptSignUp();
                } else if (!signUpStats) {
                    startSignUp();
                }
                else
                    finishSignUp();
            }
        });
        //TODO 签到
    }

    private void refresh() {

        getData();
    }

    @Override
    public void onRefresh() {
        if (refreshLayout.isRefreshing()) {
//            ArrayList<NameValuePair> params = new ArrayList<>();
//            BasicNameValuePair valuesPair = new BasicNameValuePair("course_id", courseId);
//            params.add(valuesPair);
            refresh();
        }
    }
    public void getData() {
        new GetCourseInfoTask().execute();

    }

    private void attemptSignUp() {

        if(signInInfo==null||!signInInfo.isEnable()){
            Toast.makeText(getApplicationContext(),"当前时间段不可签到，请尝试刷新",Toast.LENGTH_LONG).show();
            return;
        }

        UIDs.clear();
        student_uuid=signInInfo.getUuid().replaceAll("-","");
        if(!verifyBluetooth()){
            signUp.setClickable(true);
            signUp.setText(R.string.signup);
            return ;
        }

        startScan();

        new Handler().postDelayed(new Runnable() {
            public void run() {
                boolean flag=false;
                if(UIDs.size()>0){
                    for(String uid:UIDs){
                        if(uid.contains(student_uuid)) {
                            flag=true;
                            Toast.makeText(CourseActivity.this, "已找到课程iBeacon", Toast.LENGTH_SHORT).show();
                            new SignUpTask().execute();
                        }
                    }
                }
                if(!flag)
                    Toast.makeText(CourseActivity.this, "未扫描到课程IBeacon", Toast.LENGTH_SHORT).show();
                mBluetoothAdapter.stopLeScan(mLeScanCallback);

            }
        },3000);
    }

    private void startSignUp() {
        teacher_uuid=generateRandom();

        signUp.setClickable(false);
        signUpStats=true;
        new StartSignUpTask().execute();
    }
    private void finishSignUp(){
        signUpStats=false;
        signUp.setClickable(true);
        signUp.setText("开启签到");
        new FinishSignUpTask().execute();
    }


    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Course Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }


    @Override
    public void onStop() {
        super.onStop();
        if(signUpStats)
            finishSignUp();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }


    public class GetCourseInfoTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                ArrayList<Parameters> arrayList = new ArrayList<Parameters>();
                arrayList.add(new Parameters("_id", Constants._id));
                arrayList.add(new Parameters("course_id", course_id));
                Log.e("course_id", course_id);
                Parameters parameters = WebConnection.connect(Constants.baseUrl + Constants.AddUrls.get("COURSE_INFO"),
                        arrayList, WebConnection.CONNECT_GET);
                if(!parameters.name.equals("200"))
                    return false;
                course=new Gson().fromJson(parameters.value,Course.class);
                parameters = WebConnection.connect(Constants.baseUrl + Constants.AddUrls.get("SIGN_INFO"),
                        arrayList, WebConnection.CONNECT_GET);
                if(parameters.name.equals("503"))
                    return true;
                signInInfo = new Gson().fromJson(parameters.value, SignInInfo.class);
                signinId=signInInfo.getSignin_id();
                return true;
            } catch (Exception e) {
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (!success) {
                //TODO
                return;
            }
            courseName.setText(course.getName());
            courseTeacher.setText(course.getTerm());
            courseDate.setText(course.getIntroduction());
            progressBar.setVisibility(View.GONE);
            if(signInInfo!=null){
                signUpedInfo.setText(String.valueOf(signInInfo.getUser()));
                absenceInfo.setText(String.valueOf(signInInfo.getTotal()-signInInfo.getUser()));
            }

            if (refreshLayout.isRefreshing())
                refreshLayout.setRefreshing(false);
            if (Constants.admin) {
                signUpStats=false;
                if(signInInfo.isEnable())
                    new FinishSignUpTask().execute();
                signUp.setText("开启签到");
            }
        }
    }
    public class StartSignUpTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                ArrayList<Parameters> arrayList = new ArrayList<Parameters>();
                arrayList.add(new Parameters("_id", Constants._id));
                arrayList.add(new Parameters("course_id", course_id));
                arrayList.add(new Parameters("uuid", teacher_uuid));
                Parameters parameters = WebConnection.connect(Constants.privateBaseUrl + Constants.AddUrls.get("SIGN_UUID"),
                        arrayList, WebConnection.CONNECT_POST);
                if (!parameters.name.equals("200"))
                    return false;
                SignInInfo signInInfo = new Gson().fromJson(parameters.value, SignInInfo.class);
                arrayList = new ArrayList<>();
                arrayList.add(new Parameters("_id", Constants._id));
                arrayList.add(new Parameters("course_id", course_id));
                parameters = WebConnection.connect(Constants.privateBaseUrl + Constants.AddUrls.get("SIGN_ENABLE"),
                        arrayList, WebConnection.CONNECT_POST);
                signinId=new Gson().fromJson(parameters.value,SignInInfo.class).getSignin_id();
                Log.e(parameters.name, parameters.value);
                return parameters.name.equals("200");
            } catch (Exception e) {
                return false;
            }
        }

        @Override
        public void onPostExecute(Boolean success) {
            Toast.makeText(getApplicationContext(), "开启签到！", Toast.LENGTH_SHORT).show();
            if(startTransmission()){
                Toast.makeText(getApplicationContext(), "开启签到成功！", Toast.LENGTH_SHORT).show();
                signUp.setText("停止签到!");
                signUp.setClickable(true);
            }


            else
                Toast.makeText(getApplicationContext(), "开启签到失败！", Toast.LENGTH_SHORT).show();

        }
    }
    public class FinishSignUpTask extends AsyncTask<Void,Void,Boolean>{

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                ArrayList<Parameters> arrayList = new ArrayList<Parameters>();
                arrayList.add(new Parameters("_id", Constants._id));
                arrayList.add(new Parameters("course_id", course_id));
                arrayList.add(new Parameters("signin_id", signinId));
                Parameters parameters = WebConnection.connect(Constants.privateBaseUrl + Constants.AddUrls.get("SIGN_DISABLE"),
                        arrayList, WebConnection.CONNECT_POST);
                return parameters.name.equals("200");
            }
            catch(Exception e){
                return false;
            }
        }
        @Override
        public void onPostExecute(Boolean success){

            if(beaconTransmitter != null)
                beaconTransmitter.stopAdvertising();
            if(!success){
                Log.e(TAG,"停止签到失败！");
                return;
            }
            Log.e(TAG,"停止签到成功！");


        }
    }
    public class SignUpTask extends AsyncTask<Void,Void,Boolean>{

        @Override
        protected Boolean doInBackground(Void... params) {
            try{
                ArrayList<Parameters> arrayList = new ArrayList<Parameters>();
                arrayList.add(new Parameters("_id", Constants._id));
                arrayList.add(new Parameters("course_id", course_id));
                arrayList.add(new Parameters("device_id",deviceId));
                arrayList.add(new Parameters("signin_id",signInInfo.getSignin_id()));
                arrayList.add(new Parameters("uuid",signInInfo.getUuid()));
                Parameters parameters = WebConnection.connect(Constants.baseUrl + Constants.AddUrls.get("SIGN_SUBMIT"),
                        arrayList, WebConnection.CONNECT_POST);
                return parameters.name.equals("200");

            }
            catch (Exception e){
                return false;
            }
        }
        @Override
        public void onPostExecute(Boolean success){
            if(!success){
                Toast.makeText(CourseActivity.this, "签到失败!", Toast.LENGTH_SHORT).show();
                return;
            }
            Toast.makeText(CourseActivity.this, "签到成功！", Toast.LENGTH_SHORT).show();
            signUp.setClickable(true);
            signUp.setText(R.string.signup);
        }
    }
    private boolean checkSupport() {
        BluetoothManager btManager = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
        BluetoothAdapter btAdapter = btManager.getAdapter();
        boolean isSupported = false;
        if (btAdapter.isEnabled()) {
            int sysVersion = Integer.parseInt(Build.VERSION.SDK);
            if (sysVersion >= 21)
                isSupported = btAdapter.isMultipleAdvertisementSupported();
        }
        return isSupported;
    }

    public boolean startTransmission(){
        if(!checkSupport()){
            Log.e(TAG,"Do not support!");
            return false;
        }
        Beacon beacon = new Beacon.Builder()
                .setId1(teacher_uuid)
                .setId2("1")
                .setId3("2")
                .setManufacturer(0x0000) // Radius Networks.  Change this for other beacon layouts
                .setTxPower(-59)
                .setDataFields(Arrays.asList(new Long[]{0l})) // Remove this for beacon layouts without d: fields
                .build();
        BeaconParser beaconParser = new BeaconParser()
                .setBeaconLayout(IBEACON_FORMAT);
        if(beacon == null || beaconParser == null){
            Toast.makeText(CourseActivity.this, "Ibeacon初始化失败", Toast.LENGTH_SHORT).show();
            return false;
        }
        beaconTransmitter = new BeaconTransmitter(getApplicationContext(), beaconParser);
        beaconTransmitter.startAdvertising(beacon, new AdvertiseCallback() {
            @Override
            public void onStartFailure(int errorCode) {
                Log.e(TAG, "Advertisement start failed with code: " + errorCode);

            }

            @Override
            public void onStartSuccess(AdvertiseSettings settingsInEffect) {
                Log.i(TAG, "Advertisement start succeeded.");
            }
        });
        return true;
    }
    //确认蓝牙已经打开
    private boolean verifyBluetooth() {
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            Toast.makeText(this, "mBluetoothAdapter not started", Toast.LENGTH_SHORT).show();
            this.startActivity(enableBtIntent);
            return false;
        }
        return true;
    }

    //开始扫描周围的蓝牙
    private void startScan() {
        mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
            @Override
            public void onLeScan(final BluetoothDevice device, int rssi,
                                 byte[] scanRecord) {
                Log.e(TAG, "Receive Scanning Result");
                String rawHex = bytes2HexString(scanRecord);
                Log.e("scanRecord", rawHex);
                //String UUID = info.UUID;
                //Log.e("UUID", UUID);
                UIDs.add(rawHex);    //将扫描到的id加入集合
            }
        };
        Log.e(TAG, "startScanning");
        mBluetoothAdapter.startLeScan(mLeScanCallback);
    }
    public static String bytes2HexString(byte[] b) {
        String ret = "";
        for (int i = 0; i < b.length; i++) {
            String hex = Integer.toHexString(b[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            ret += hex.toUpperCase();
        }
        return ret;
    }
    /*
     * generate a random uuid.
     */
    private String generateRandom() {
        char[] s = new char[36];
        Random random = new Random();
        String str = "ABCDEF0123456789";
        for (int i = 0; i < 36; i++) {
            if (i == 8 || i == 13 || i == 18 || i == 23) {
                s[i] = '-';
                continue;
            }
            int k = random.nextInt(16);
            s[i] = str.charAt(k);
        }
        return String.valueOf(s);
    }
}