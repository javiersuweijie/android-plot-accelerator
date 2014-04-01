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
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends Activity implements SensorEventListener {

	static final String TAG = "SENSOR";
	static final double THRESHOLD = 1;
	static final int max = 200;
	
	private GraphicalView mChart;
	private SensorManager sensorManager;
	private Sensor accelerometer;
	private XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
    private XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
    private XYSeries xSeries;
    private XYSeries ySeries;
    private XYSeries zSeries;
    private XYSeriesRenderer xRenderer;
    private XYSeriesRenderer yRenderer;
    private XYSeriesRenderer zRenderer;
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
		
		double time = (double)(System.currentTimeMillis()-startTime);
        addData(xSeries,time, xyz[0]);
        addData(ySeries,time, xyz[1]);
        addData(zSeries,time, xyz[2]);
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
        xSeries = new XYSeries("X direction");
        ySeries = new XYSeries("Y direction");
        zSeries = new XYSeries("Z direction");
        
        dataset.addSeries(xSeries);
        dataset.addSeries(ySeries);
        dataset.addSeries(zSeries);

        xRenderer = new XYSeriesRenderer();
        xRenderer.setColor(Color.argb(255, 231, 76, 60));
        yRenderer = new XYSeriesRenderer();
        yRenderer.setColor(Color.argb(255, 52, 152, 219));
        zRenderer = new XYSeriesRenderer();
        zRenderer.setColor(Color.argb(255, 230, 126, 34));
        
        renderer.addSeriesRenderer(xRenderer);
        renderer.addSeriesRenderer(yRenderer);
        renderer.addSeriesRenderer(zRenderer);
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
