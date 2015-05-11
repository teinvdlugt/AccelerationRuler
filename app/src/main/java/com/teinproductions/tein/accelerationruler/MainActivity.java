package com.teinproductions.tein.accelerationruler;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity implements SensorEventListener {

    TextView resultTextView, xResultTV, yResultTV, zResultTV;
    Button button;

    SensorManager manager;
    Sensor linearAccSensor;

    boolean running = false;

    long lastMillis;
    double[] a = new double[3];
    double[] v = new double[3];
    double[] s = new double[3];

    long negativeV = 0, positiveV = 0, negativeA = 0, positiveA = 0;

    //int howManyTimes = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);

        resultTextView = (TextView) findViewById(R.id.resultTextView);
        xResultTV = (TextView) findViewById(R.id.x_result);
        yResultTV = (TextView) findViewById(R.id.y_result);
        zResultTV = (TextView) findViewById(R.id.z_result);
        button = (Button) findViewById(R.id.button);

        manager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        linearAccSensor = manager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor != linearAccSensor) return;

        //if (howManyTimes > 500) return;

        Log.d("mkbhd", "ax = " + this.a[0]);

        double[] currA = new double[3];
        currA[0] = event.values[0];
        currA[1] = event.values[1];
        currA[2] = event.values[2];
        for (int i = 0; i < 3; i++) {
            if ((currA[i] < 0 && currA[i] > -0.001) || (currA[i] > 0 && currA[i] < 0.001)) currA[i] = 0;
        }


        double dT = ((double) (System.currentTimeMillis()) - lastMillis) / 1000d;
        lastMillis = System.currentTimeMillis();

        for (int i = 0; i < 3; i++) {
            //Log.d("pasta", " ");
            //Log.d("pasta", "xyz: " + i);
            //Log.d("pasta", "a = " + a[i]);
            //Log.d("pasta", "dT = " + dT);
            //double dA = a[i] - this.a[i];
            //Log.d("pasta", "dA = " + dA);
            //double dV = dT * (this.a[i] + 0.5 * dA);
            double dV = dT * (0.5 * (this.a[i] + currA[i]));
            if ((dV < 0 && dV > -0.01) || (dV > 0 && dV < 0.01)) dV = 0;
            /*if (i == 0) {
                if (dV < 0) negativeV++;
                else positiveV++;

                if (currA[i] < 0) negativeA++;
                else positiveA++;

                Log.d("high tea", "negative v: " + negativeV);
                Log.d("high tea", "positive v: " + positiveV);
                Log.d("high tea", "negative a: " + negativeA);
                Log.d("high tea", "positive a: " + positiveA);
            }*/
            //Log.d("pasta", "dV = " + dV);
            //double dS = dT * (this.v[i] + 0.5 * dV);
            //Log.d("pasta", "dS = " + dS);

            this.v[i] += dV;

            if ((this.v[i] < 0 && this.v[i] > -0.02) || (this.v[i] > 0 && this.v[i] < 0.02)) this.v[i] = 0;

            double dS = dT * (0.5 * (this.v[i] + this.v[i] + dV));
            this.s[i] += dS;
            Log.d("pasta", "v = " + this.v[i]);
        }

        this.a = currA;

        updateResultText();

        //howManyTimes++;
    }

    private void updateResultText() {
        double result = Math.sqrt(s[0] * s[0] + s[1] * s[1] + s[2] * s[2]);

        resultTextView.setText("" + result);
        xResultTV.setText("" + s[0]);
        yResultTV.setText("" + s[1]);
        zResultTV.setText("" + s[2]);

        /*if (result >= 1) {
            resultTextView.setText(new DecimalFormat("0.000").format(result) + " m");
        } else {
            double centimeters = result / 100;
            resultTextView.setText(new DecimalFormat("0.0").format(centimeters) + " cm");
        }

        TextView[] textViews = {xResultTV, yResultTV, zResultTV};
        String[] strings = {"x: ", "y: ", "z: "};
        for (int i = 0; i < 3; i++) {
            if (s[i] >= 1) {
                textViews[i].setText(strings[i] + new DecimalFormat("0.000").format(s[i]) + " m");
            } else {
                double centimeters = s[i] / 100;
                textViews[i].setText(strings[i] + new DecimalFormat("0.0").format(centimeters) + " cm");
            }
        }*/
    }

    private void reset() {
        a[0] = 0;
        a[1] = 0;
        a[2] = 0;
        v[0] = 0;
        v[1] = 0;
        v[2] = 0;
        s[0] = 0;
        s[1] = 0;
        s[2] = 0;
        lastMillis = System.currentTimeMillis();
    }

    public void onClickButton(View view) {
        if (running) {
            running = false;
            manager.unregisterListener(this);
            button.setText(R.string.start);
        } else {
            running = true;
            reset();
            manager.registerListener(this, linearAccSensor, SensorManager.SENSOR_DELAY_FASTEST);
            button.setText(R.string.stop);
        }
    }

    @Override
    protected void onPause() {
        manager.unregisterListener(this);
        super.onPause();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
