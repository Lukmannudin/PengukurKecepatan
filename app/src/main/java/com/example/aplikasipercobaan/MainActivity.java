package com.example.aplikasipercobaan;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Chronometer;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.nio.file.Files;

import static androidx.constraintlayout.widget.Constraints.TAG;


public class MainActivity extends Activity implements SensorEventListener {
    TextView title, tv, tv1, tv2, tvSecond;
    int  CurrentTimeMills = 0;
    RelativeLayout layout;
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView( R.layout.activity_main );
        mSensorManager = (SensorManager) getSystemService( Context.SENSOR_SERVICE );
        mAccelerometer = mSensorManager.getDefaultSensor( Sensor.TYPE_ACCELEROMETER );
        //mendapatkan tampilan pada layar
        layout = findViewById( R.id.relative );
        //tulisan pada layar
        title = findViewById( R.id.name );
        tv = findViewById( R.id.xval );
        tv1 = findViewById( R.id.yval );
        tv2 = findViewById( R.id.zval );
        tvSecond = findViewById( R.id.tv_second );
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED) {
            //
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

            } else {

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        0);
            }
        }


    }

    @Override
    public final void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public final void onSensorChanged(SensorEvent event) {
        // 3 Sensor pada 3 axis
        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];

        CurrentTimeMills++;
        Log.d( "waktu", "Waktu putaran" + CurrentTimeMills + " detik" );
        // nilai akselerometer pada layar
        title.setText( R.string.app_name );
        tv.setText( "X " + "\t\t" + x );
        tv1.setText( "Y " + "\t\t" + y );
        tv2.setText( "Z " + "\t\t" + z );
        tvSecond.setText( "Waktu : " + CurrentTimeMills );

            }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener( this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL );
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener( this );
    }


}

