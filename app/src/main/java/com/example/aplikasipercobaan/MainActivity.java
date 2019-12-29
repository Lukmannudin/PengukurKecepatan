package com.example.aplikasipercobaan;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.Locale;

import de.siegmar.fastcsv.writer.CsvAppender;
import de.siegmar.fastcsv.writer.CsvWriter;


public class MainActivity extends Activity implements LocationListener, SensorEventListener {
    TextView title, tv, tv1, tv2, tvSecond, tv_speed;
    int CurrentTimeMills = 0;
    RelativeLayout layout;
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private int detik = 0;
    private Storage mStorage;
    Button saveToCSV;

    float x, y, z = 0f;
    Boolean onStartIteration = false;
    LocationManager locationManager;
    String strCurrentSpeed = "null";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        //mendapatkan tampilan pada layar
        layout = findViewById(R.id.relative);
        //tulisan pada layar
        tv = findViewById(R.id.xval);
        tv1 = findViewById(R.id.yval);
        tv2 = findViewById(R.id.zval);
        tvSecond = findViewById(R.id.tv_second);
        saveToCSV = findViewById(R.id.saveToCSV);
        tv_speed = findViewById(R.id.tv_speed);

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);


        mStorage = new Storage(getApplicationContext());

//        createCSVFile();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            //
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

            } else {

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        0);
            }
        }
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1000);



        final ArrayList<String> d = new ArrayList<>();
        final ArrayList<String> xs = new ArrayList<>();
        final ArrayList<String> ys = new ArrayList<>();
        final ArrayList<String> zs = new ArrayList<>();
        final ArrayList<String> speed = new ArrayList<>();

        final Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    while (!this.isInterrupted()) {
                        Thread.sleep(1);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // update TextView here!
                                ++detik;
                                d.add(String.valueOf(detik));
                                xs.add(String.valueOf(x));
                                ys.add(String.valueOf(y));
                                zs.add(String.valueOf(z));
                                tvSecond.setText(String.valueOf(detik));
                                speed.add(strCurrentSpeed);
                            }
                        });
                    }
                } catch (InterruptedException e) {
                }
            }
        };
        thread.start();

        this.updateSpeed( null );


        saveToCSV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createCSVFile(d, xs, ys, zs, speed);
                detik = 0;
                onStartIteration = !onStartIteration;
                Toast.makeText(MainActivity.this, "Tersimpan di pengukur.csv", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void stopGetLocation() {
        locationManager = null;
    }


    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    public final void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public final void onSensorChanged(SensorEvent event) {
        // 3 Sensor pada 3 axis
        x = event.values[0];
        y = event.values[1];
        z = event.values[2];

        CurrentTimeMills++;
//        Log.d("waktu", "Waktu putaran" + CurrentTimeMills + " detik");
        // nilai akselerometer pada layar
        tv.setText(String.valueOf(x));
        tv1.setText(String.valueOf(y));
        tv2.setText(String.valueOf(z));

    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    void createCSVFile(ArrayList<String> detik, ArrayList<String> x, ArrayList<String> y, ArrayList<String> z, ArrayList<String> spe) {
        String folderPath = mStorage.getExternalStorageDirectory() + File.separator + "pengukur.csv";

        File file = new File(folderPath);
        CsvWriter csvWriter = new CsvWriter();

        try (CsvAppender csvAppender = csvWriter.append(file, StandardCharsets.UTF_8)) {
            // header
            csvAppender.appendLine("waktu", "X", "Y", "Z", "Kecepatan");

            // 1st line in one operation

//            // 2nd line in split operations
//            csvAppender.appendField("value3");
//            csvAppender.appendField("value4");


            for (int i = 0; i < detik.size(); i++) {
                csvAppender.appendLine(
                        String.valueOf(detik.get(i)),
                        String.valueOf(x.get(i)),
                        String.valueOf(y.get(i)),
                        String.valueOf(z.get(i)),
                        String.valueOf(spe.get(i)));

            }

            csvAppender.endLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updateSpeed(Clocation location) {
        float nCurrentSpeed = 0;
        if (location != null) {
            nCurrentSpeed = location.getSpeed();
        }
        Formatter fmt = new Formatter(new StringBuilder());
        fmt.format(Locale.US, "%5.1f", nCurrentSpeed);
        strCurrentSpeed = fmt.toString();

        strCurrentSpeed = strCurrentSpeed.replace("", "0");

        tv_speed.setText(strCurrentSpeed + "km/jam");

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1000) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                doStuff();
            } else {
                finish();
            }
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            Clocation myLocation = new Clocation(location, true);
            this.updateSpeed(myLocation);
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @SuppressLint("MissingPermission")
    private void doStuff() {
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (locationManager != null) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        }
        Toast.makeText(this, "Menunggu Koneksi GPS!", Toast.LENGTH_SHORT).show();
    }
}

