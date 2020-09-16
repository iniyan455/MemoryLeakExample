package com.iniyan.memoryleakexample;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.lang.ref.WeakReference;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private static final String TAG = "MainActivity";

    //private static MainActivity activity;

    private static WeakReference<MainActivity> activity;

    // other possibilities
    private static TextView textView ;


    //private static  Object innerObject;
    private   Object innerObject;


    private static Thread thread;

    private SensorManager sensorManager ;

    private Sensor sensor ;

    SomeRandomSampleClass someRandomSampleClass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //activity = this // leads to memory leaks to avoid use WeakReference
        activity = new  WeakReference<MainActivity>(this);

        textView = (TextView) findViewById(R.id.textview); // this lead to memory leaks whenever
        // activity rotation to avoid this we need to do dereference the variable


      //  someRandomSampleClass = SomeRandomSampleClass.getSomeRandomSampleClass(this); leads memory leak because of this context
        someRandomSampleClass = SomeRandomSampleClass.getSomeRandomSampleClass(getApplicationContext()); // to avoid leaks because singleton application class subclasses in context

        //avoiding the usage of static reference in the class

        class SampleInnerClass {

        }


        innerObject = new SampleInnerClass(); // This will lead to memory leak  to solve this dont
        // use static reference to remove static from top


        // Anonymous inner class asyncTask this is holding the reference of activity to avoid this
       // static async seperate class

//        new AsyncTask<Void,Void,Void>(){
//
//            @Override
//            protected Void doInBackground(Void... voids) {
//                return null;
//            }
//        }.execute();

        new MyAsyncTask().execute(); // to solve this problem to avoid memory leak



        new Thread().start(); // leak memory thread might outlive activity

        thread = new Thread(){

            @Override
            public void run() {
                super.run();

                if(!isInterrupted()){

                }
            }
        };
        thread.start(); // to avoid memory leaks



        // Multiple anonymous class here leads to memory leak -> handler , Runnable both of there
        // references of activity reference to avoid this need to make seperate class
//        new Handler(){
//            @Override
//            public void handleMessage(@NonNull Message msg) {
//                super.handleMessage(msg);
//            }
//        }.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//
//            }
//        },Long.MAX_VALUE >> 1);



       new CustomHandler().postDelayed(new RunnableForHandler(),Long.MAX_VALUE >> 1);


//       // Developer may leads in Timer this will leads to outer activity to solve create custom
//        //  class
//        new Timer().schedule(new TimerTask() {
//            @Override
//            public void run() {
//
//            }
//        },Long.MAX_VALUE >> 1);

       // To avoid use this used for schedule some task after some times
        new Timer().schedule(new CustomTimer(),Long.MAX_VALUE >> 1);

        registerSensor();

    }



    private void registerSensor() {
        sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ALL);
        sensorManager.registerListener(this,sensor,SensorManager.SENSOR_DELAY_FASTEST);
    }

    private void unregisterSensor() {
        if(sensorManager != null && sensor != null) {
            sensorManager.unregisterListener(this,sensor);
        }

    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    private static class CustomTimer extends TimerTask{
        @Override
        public void run() {

        }
    }

    private static class MyAsyncTask extends AsyncTask<Void,Void,Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            return null;
        }
    }

    private static class RunnableForHandler implements Runnable {
        @Override
        public void run() {

        }
    }


    private static class CustomHandler extends Handler {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        textView = null;
        unregisterSensor();
        thread.interrupt(); // to apply to activity lifecycle
    }
}