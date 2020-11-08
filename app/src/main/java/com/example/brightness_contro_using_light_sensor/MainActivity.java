package com.example.brightness_contro_using_light_sensor;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Type;
import java.util.Optional;

public class MainActivity extends AppCompatActivity implements SensorEventListener{
TextView lightValue;
Sensor sensor;
SensorManager sensorManager;
Camera camera;
android.hardware.Camera.Parameters parameters;
boolean isTourchOn=false;

float lv=0.0f;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lightValue=findViewById(R.id.light);
        permission();

        camera= Camera.open();
        parameters=camera.getParameters();
        sensorManager= (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensor=sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
if(sensor!=null){
    sensorManager.registerListener((SensorEventListener) MainActivity.this,sensor,SensorManager.SENSOR_DELAY_NORMAL);
}
else{
    lightValue.setText("Light Sensor is not present in your device");
}
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onSensorChanged(SensorEvent event) {
        Sensor sensor=event.sensor;
        if(sensor.getType()==sensor.TYPE_LIGHT) {

            lightValue.setText("Light: " + event.values[0]);
            lv=event.values[0];
            Context context = getApplicationContext();
            boolean canWrite = Settings.System.canWrite(context);
            if (canWrite) {
                if (event.values[0] < 255) {
                    lightValue.setText("Light: " + event.values[0]);


                    Settings.System.putInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, (int) (event.values[0]));

                } else {
                    Settings.System.putInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, 100);
                    }
            }
            else {
                Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                context.startActivity(intent);

            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void Manual_onOff(View view) {
      if(parameters.getFlashMode().equals(Camera.Parameters.FLASH_MODE_OFF))
      {
          isTourchOn=true;
      }else{
          isTourchOn=false;
      }
      if(isTourchOn==true){
          parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
          camera.setParameters(parameters);
          camera.startPreview();
          parameters.setFlashMode(Camera.Parameters.FLASH_MODE_ON);
      } else if(isTourchOn==false){

          camera.stopPreview();

          parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);

      }
      else {
          Toast.makeText(getApplicationContext(),"errr",Toast.LENGTH_LONG).show();
      }

    }
    private void permission() {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CAMERA},1);
        }


    }
}