package com.example.asstask4;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {
	private static int SAMPLE_RATE = 44100;
	//private static double TARGET_FREQ = 941.0;   //941 Hz
	private static int N = 1000;
	int bufferSizeInBytes;
    private int audioSource = MediaRecorder.AudioSource.MIC; 
    private static int channelConfig = AudioFormat.CHANNEL_IN_MONO;  
    private static int audioFormat = AudioFormat.ENCODING_PCM_16BIT;
	
	private AudioRecord recorder;
	private GoertzelAlgorithm gA;
	
	private TextView resultView;
	private Button btn;
	private List<Integer> freqBands;
	
	int count;
	String binaryBit;
	boolean hasStarted;
	boolean hasEnded;
	String str = "";
	private static int CHECK_INTERVAL = 1;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		freqBands = new ArrayList<Integer>();
		freqBands.add(500);
        freqBands.add(600);
        freqBands.add(3600);
        freqBands.add(1300);
        freqBands.add(2400);
        resultView = (TextView)findViewById(R.id.textView1);
		btn = (Button)findViewById(R.id.button1);
		btn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				hasStarted = false;
				hasEnded = false;
				binaryBit = "";
				str = "";
				count = 0;
				
				final Timer timer = new Timer("interval");
		        TimerTask timerTask = null;
		        timerTask = new TimerTask(){
		            public void run(){
		                try{
		                	if(!hasStarted && !hasEnded){
		                		bufferSizeInBytes = AudioRecord.getMinBufferSize(SAMPLE_RATE,  
		                                channelConfig, audioFormat)*2;  
		                		recorder = new AudioRecord(audioSource, SAMPLE_RATE,
		                			channelConfig, audioFormat, bufferSizeInBytes);

		                    	//定义缓冲    
		                        short[] buffer = new short[bufferSizeInBytes];    
		                        //开始录制    
		                        recorder.startRecording();  
		                        
		                        double max = 0.0;
		                        int presentFreqMax = 0;
		                        //从bufferSize中读取字节，返回读取的short个数      
		                        int bufferReadResult = recorder.read(buffer, 0, buffer.length);   
		                        
		                        for(int freqBand:freqBands){
		                        	gA = new GoertzelAlgorithm(SAMPLE_RATE, freqBand, N);
		                        	gA.initGoertzel();
		                        	//process samples in recorder buffer
		                        	for(int i=0; i<bufferReadResult; i++){    
		                        		gA.processSample(buffer[i]);	
		                        	}  
		                        	if(max < gA.getMagnitudeSquared()){
		                        		max = gA.getMagnitudeSquared();
		                        		presentFreqMax = freqBand;
		                        	}
		                        }
		                        
		                        if(presentFreqMax == 1300){
		                        	hasStarted = true;
			                		System.out.println("HAHAHAHAHHAHAHAH");	
		                        }
		                        
		                        recorder.stop();
		                	}else if(hasStarted && !hasEnded){

			                	bufferSizeInBytes = AudioRecord.getMinBufferSize(SAMPLE_RATE,  
			                            channelConfig, audioFormat)*2;  
			            		recorder = new AudioRecord(audioSource, SAMPLE_RATE,
			            			channelConfig, audioFormat, bufferSizeInBytes);

			                	//定义缓冲    
			                    short[] buffer = new short[bufferSizeInBytes];    
			                    //开始录制    
			                    recorder.startRecording();  
			                    
			                    double max = 0.0;
			                    int presentFreqMax = 0;
			                    //从bufferSize中读取字节，返回读取的short个数      
			                    int bufferReadResult = recorder.read(buffer, 0, buffer.length);   
			                    
			                    for(int freqBand:freqBands){
			                    	gA = new GoertzelAlgorithm(SAMPLE_RATE, freqBand, N);
			                    	gA.initGoertzel();
			                    	//process samples in recorder buffer
			                    	for(int i=0; i<bufferReadResult; i++){    
			                    		gA.processSample(buffer[i]);	
			                    	}  
			                    	if(max < gA.getMagnitudeSquared()){
			                    		max = gA.getMagnitudeSquared();
			                    		presentFreqMax = freqBand;
			                    	}
			                    }
			                    
			                    if(presentFreqMax == 600){
			                    	count++;
			                    	binaryBit += "0";
			                    	hasStarted = false;
				                	System.out.println("========"+count+"========");
			                    	System.out.println(binaryBit);
			                    }else if(presentFreqMax == 3600){
			                    	binaryBit += "1";
			                    	count++;
			                    	hasStarted = false;
				                	System.out.println("========"+count+"========");
			                    	System.out.println(binaryBit);
			                    }else if(presentFreqMax == 2400){
			                    	System.out.println("DONE!!!!!");
			                    	System.out.println(binaryBit);
			                    	
			                    	hasEnded = true;
			                    	hasStarted = true;
			                    	timer.cancel();
			                    }
			                    
			                    recorder.stop();
			                }
		                }catch(Exception e){
		                    e.printStackTrace();
		                }                                        
		            }
		        };
		        timer.scheduleAtFixedRate(timerTask, CHECK_INTERVAL*115, CHECK_INTERVAL*115); 
		        //resultView.setText(binaryBit);
			}
		});   
		
		final Button btn2 = (Button)findViewById(R.id.button2);
		btn2.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				str = "";
            	System.out.println(binaryBit);
				String[] arr = binaryBit.split("");
				String temp = "";
				System.out.println(arr[0]+"  "+arr.length);
				int numOfChar = (arr.length-1)/7;
				for(int n=0;n<numOfChar;n++){
					if(n==0){
						for(int i=1;i<8;i++){
							temp += arr[i];
							System.out.println("=="+temp+"==");
						}
						int a = Integer.valueOf(temp,2);
						char c = (char)a;
						str += c;
						temp = "";
					}else{
						for(int i=0;i<7;i++){
							temp += arr[7*n+i+1];
							System.out.println("=="+temp+"==");
						}
						int a = Integer.valueOf(temp,2);
						char c = (char)a;
						str += c;
						temp = "";
					}
					
				}
				
				resultView.setText(str);
			}
		});
	}

	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}