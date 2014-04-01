package com.test.accelerotest;

import java.text.DecimalFormat;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import android.R.layout;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Chronometer;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends Activity implements SensorEventListener {

	static final String TAG = "SENSOR";
	static private final double NOISE = 0.5;
	static private final int max = 100;
	
	private GraphicalView mChart;
	private SensorManager sensorManager;
	private Sensor accelerometer;
	private XYMultipleSeriesDataset dataset;
    private XYMultipleSeriesRenderer renderer;
    private XYSeries xSeries;
    private XYSeries ySeries;
    private XYSeries zSeries;
    private XYSeriesRenderer xRenderer;
    private XYSeriesRenderer yRenderer;
    private XYSeriesRenderer zRenderer;
    private long startTime;
    private boolean isInitiated = false;
    
    private LinearLayout layout;
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
        
        dataset = new XYMultipleSeriesDataset();
        renderer = new XYMultipleSeriesRenderer();
        
		xAxis = (TextView) findViewById(R.id.x_axis);
		yAxis = (TextView) findViewById(R.id.y_axis);
		zAxis = (TextView) findViewById(R.id.z_axis);
		
		f = new DecimalFormat("##.####");
		layout = (LinearLayout) findViewById(R.id.chart);
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
	public void onSensorChanged(SensorEvent event) {
		if (!isInitiated) {
			xOld = event.values[0];
            yOld = event.values[1];
            zOld = event.values[2];
            isInitiated = true;
            return;
		}
		
		xyz[0] = event.values[0]-xOld; //xyz[] is the change in the reading
		xyz[1] = event.values[1]-yOld; //so that we record the acceleration
		xyz[2] = event.values[2]-zOld;
		
		xOld = event.values[0];
        yOld = event.values[1];
        zOld = event.values[2];
		 
		if (Math.abs(xyz[0])<NOISE) xyz[0] = 0.0; //Filter out some of the noise to get a
		if (Math.abs(xyz[1])<NOISE) xyz[1] = 0.0; //cleaner reading.
		if (Math.abs(xyz[2])<NOISE) xyz[2] = 0.0; 
		
		xAxis.setText("X Axis: "+f.format(xyz[0]));
		yAxis.setText("Y Axis: "+f.format(xyz[1]));
		zAxis.setText("Z Axis: "+f.format(xyz[2]));
		
		double time = (double)(System.currentTimeMillis()-startTime); //get the since since the app is started
        addData(xSeries,time, xyz[0]);
        addData(ySeries,time, xyz[1]);
        addData(zSeries,time, xyz[2]);
        mChart.repaint();
	}
	
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {}
	
	private void addData(XYSeries series, double x, double y) {
		if (series.getItemCount()>max) series.remove(0); //Limit the backlog of data elements
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
        sensorManager.registerListener(this, accelerometer , SensorManager.SENSOR_DELAY_UI); //Listener is registered onResume and unregisted onPasue to save energy
    }
	
	@Override
	protected void onPause() {
		super.onPause();
        sensorManager.unregisterListener(this);
	}

}
