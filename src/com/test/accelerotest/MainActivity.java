package com.test.accelerotest;

import java.text.DecimalFormat;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
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
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends Activity implements SensorEventListener {

	static final String TAG = "SENSOR";
	static final double THRESHOLD = 1;
	static final int max = 50;
	
	private GraphicalView mChart;
	private SensorManager sensorManager;
	private Sensor accelerometer;
	private XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
    private XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
    private XYSeries currentSeries;
    private XYSeriesRenderer currentRenderer;
    private long startTime;
    private boolean isInitiated = false;
    
    private TextView xAxis;
    private TextView yAxis;
    private TextView zAxis;
    
    private double xOld;
    private double yOld;
    private double zOld;
    
    private double[] xyz = new double[3];
    
    private DecimalFormat f; 
    
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		xAxis = (TextView) findViewById(R.id.x_axis);
		yAxis = (TextView) findViewById(R.id.y_axis);
		zAxis = (TextView) findViewById(R.id.z_axis);
		f = new DecimalFormat("##.####");
	}
	@Override
	public void onSensorChanged(SensorEvent event) {
		if (!isInitiated) {
			xOld = event.values[0];
            yOld = event.values[1];
            zOld = event.values[2];
            isInitiated = true;
            return;
		}
		
		xyz[0] = event.values[0]-xOld;
		xyz[1] = event.values[1]-yOld;
		xyz[2] = event.values[2]-zOld;
		
		xOld = event.values[0];
        yOld = event.values[1];
        zOld = event.values[2];
		 
		if (Math.abs(xyz[0])<THRESHOLD) xyz[0] = 0.0;
		if (Math.abs(xyz[1])<THRESHOLD) xyz[1] = 0.0;
		if (Math.abs(xyz[2])<THRESHOLD) xyz[2] = 0.0; 
		
		xAxis.setText("X Axis: "+f.format(xyz[0]));
		yAxis.setText("Y Axis: "+f.format(xyz[1]));
		zAxis.setText("Z Axis: "+f.format(xyz[2]));
		
		long time = System.currentTimeMillis()-startTime;
        addData(currentSeries,(double)(time), xyz[0]);
        mChart.repaint();
	}
	
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
		
	}
	
	private void addData(XYSeries series, double x, double y) {
		if (series.getItemCount()>max) series.remove(0);
		series.add(x, y);
	}
	
	private void initChart() {
        currentSeries = new XYSeries("X direction");
        dataset.addSeries(currentSeries);
        currentRenderer = new XYSeriesRenderer();
        renderer.addSeriesRenderer(currentRenderer);
    }
	
	@Override
	protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, accelerometer , SensorManager.SENSOR_DELAY_UI);
        LinearLayout layout = (LinearLayout) findViewById(R.id.chart);
        if (mChart == null) {
            initChart();
            mChart = ChartFactory.getLineChartView(this, dataset, renderer);
            layout.addView(mChart);
            startTime = java.lang.System.currentTimeMillis();
        } else {
            mChart.repaint();
        }
    }
	@Override
	protected void onPause() {
		super.onPause();
        sensorManager.unregisterListener(this);
	}

}
