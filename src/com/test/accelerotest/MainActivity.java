package com.test.accelerotest;

import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends Activity implements SensorEventListener {

	static final String TAG = "SENSOR";
	
	SensorManager sensorManager;
	Sensor accelerometer;
	private XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
    private XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
    private XYSeries currentSeries;
    private XYSeriesRenderer currentRenderer;
    private double time=0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, accelerometer , SensorManager.SENSOR_DELAY_NORMAL);
	}
	@Override
	public void onSensorChanged(SensorEvent event) {
		time+=1;
		Log.d(TAG, "X: "+event.values[0]);
		addData(time, (double)event.values[0]);
		Log.d(TAG, "Y: "+event.values[1]);
		Log.d(TAG, "Z: "+event.values[2]);
	}
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
		
	}
	
	private void addData(double x, double y) {
		currentSeries.add(x, y);
	}
	
	private void initChart() {
        currentSeries = new XYSeries("X direction");
        dataset.addSeries(currentSeries);
        currentRenderer = new XYSeriesRenderer();
        renderer.addSeriesRenderer(currentRenderer);
    }
	

}
