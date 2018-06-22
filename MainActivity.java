package com.oh.stopwatch;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    ListView lv;
    TextView time;
    Button start;
    Button reset;
    Button save;

    ArrayList<String> savetime=new ArrayList<>();
    ArrayAdapter adapter;

    final static int IDLE=0;
    final static int run=1;
    final static int pause=2;

    int status=IDLE;
    long basetime;
    long pausetime;
    int count;

    AdView adview;
    AdRequest adr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        time=findViewById(R.id.tv);
        start=findViewById(R.id.start);
        reset=findViewById(R.id.reset);
        save=findViewById(R.id.save);
        lv=findViewById(R.id.lv);
        adapter=new ArrayAdapter(this,android.R.layout.simple_list_item_1,savetime);
        lv.setAdapter(adapter);

    }
    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
          time.setText(gettime());
          handler.sendEmptyMessage(0);
        }
    };
    public void click(View v) {
        switch (v.getId()) {
            case R.id.start:
                switch (status) {
                    case IDLE:
                        basetime = SystemClock.elapsedRealtime();
                        handler.sendEmptyMessage(0);
                        start.setText("pause");
                        save.setEnabled(true);
                        status = run;
                        break;
                    case run:
                        handler.removeMessages(0);
                        pausetime = SystemClock.elapsedRealtime();
                        start.setText("START");
                        save.setText("save");
                        status = pause;
                        break;
                    case pause:
                        long now = SystemClock.elapsedRealtime();
                        handler.sendEmptyMessage(0);
                        basetime += (now - pausetime);
                        start.setText("pause");
                        save.setText("save");
                        status = run;
                        break;
                }
                break;
            case R.id.reset:
                switch (status) {
                    case run:

                        break;
                    case pause:
                        handler.removeMessages(0);
                        start.setText("start");
                        save.setText("save");
                        time.setText("");
                        status = IDLE;
                        count = 0;
                        save.setEnabled(false);
                        savetime.clear();
                        adapter.notifyDataSetChanged();
                        time.setText("00:00:00");
                        break;

                }
                break;
            case R.id.save:
                switch (status){
                    case run:
                        String s = save.getText().toString();
                        s += String.format("%d.%s\n", count, gettime());
                        savetime.add(s);
                        adapter.notifyDataSetChanged();
                        count++;


                        break;
                    case  IDLE:
                        break;
                }
        }
        //////////////////////////////////
        MobileAds.initialize(this,"ca-app-pub-8861946097010677~5554457435");
        adview=findViewById(R.id.adview);
        adr=new AdRequest.Builder().build();
        adview.loadAd(adr);
        ////////////////////////////////

    }//on

    String gettime(){
        long now= SystemClock.elapsedRealtime();//앱이 실행되고 나서 실제로 경과된 시간
        long outtime=now-basetime;
        String easy_outTime=String.format("%02d:%02d:%02d",outtime/1000/60,(outtime/1000)%60,(outtime%1000)/10);
        return easy_outTime;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(adview != null ){
            adview.pause();
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        if(adview != null){
            adview.resume();
        }
    }

    @Override
    protected void onDestroy() {
        handler.removeMessages(0);
        super.onDestroy();
        if(adview != null){
            adview.destroy();
        }
    }
}
