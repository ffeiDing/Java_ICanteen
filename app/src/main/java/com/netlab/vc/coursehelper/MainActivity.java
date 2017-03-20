package com.netlab.vc.coursehelper;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.netlab.vc.coursehelper.util.Constants;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        RadioGroup.OnCheckedChangeListener,
        ViewPager.OnPageChangeListener{
    private ViewPager vpager;
    private MainPagerAdapter pagerAdapter;
    private RadioGroup rg_tab_bar;
    private RadioButton rb_course;
    private RadioButton rb_announcement;
    private RadioButton rb_group;
    private TextView drawerRealName;
    private TextView drawerUserId;
    private Toolbar toolbar;
    private DrawerLayout drawer;
    private LinearLayout navHeader;
    private NavigationView navigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViews();
        vpager.setAdapter(pagerAdapter);
        vpager.setCurrentItem(0);
        vpager.addOnPageChangeListener(this);

        setSupportActionBar(toolbar);


        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();


        navigationView.setNavigationItemSelectedListener(this);
        //Log.e("1",Constants.realname);
        drawerRealName.setText(Constants.realname);
        drawerUserId.setText(Constants.username);
    }
    @Override
    public void onResume(){
        super.onResume();
        Log.e("realname",Constants.realname);
        drawerRealName.setText(Constants.realname);
        drawerUserId.setText(Constants.username);
    }
    public void findViews(){
        pagerAdapter=new MainPagerAdapter(getSupportFragmentManager());
        vpager=(ViewPager)findViewById(R.id.vpager);
        rg_tab_bar=(RadioGroup)findViewById(R.id.tab_group);
        rg_tab_bar.setVisibility(View.GONE);
        rg_tab_bar.setOnCheckedChangeListener(this);
        rb_course=(RadioButton)findViewById(R.id.rb_course);
        rb_announcement=(RadioButton)findViewById(R.id.rb_message);
        rb_group=(RadioButton)findViewById(R.id.rb_group);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        navHeader=(LinearLayout)navigationView.getHeaderView(0);
        drawerRealName=(TextView)navHeader.findViewById(R.id.drawer_realName);
        drawerUserId=(TextView)navHeader.findViewById(R.id.drawer_userid);
    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_elective) {
            Intent intent=new Intent(MainActivity.this,ElectiveActivity.class);
            startActivity(intent);
            return true;

        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {
            Intent intent=new Intent(MainActivity.this,IAAALogin.class);
            startActivity(intent);
            return true;

        }  else if (id == R.id.nav_exit) {
            MainActivity.this.finish();
            //Intent intent=new Intent(MainActivity.this,LoginActivity.class);
            //startActivity(intent);
            return true;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {
        if(state==2){
            switch(vpager.getCurrentItem()){
                case 0:
                    rb_course.setChecked(true);
                    break;
                case 1:
                    rb_announcement.setChecked(true);
                    break;
                case 2:
                    rb_group.setChecked(true);
                    break;
            }
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch(checkedId){
            case R.id.rb_course:
                vpager.setCurrentItem(0);
                break;
            case R.id.rb_message:
                vpager.setCurrentItem(1);
                break;
            case R.id.rb_group:
                vpager.setCurrentItem(2);
                break;
        }
    }
}
