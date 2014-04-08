package com.example.sidekick_offline_try2;

import java.util.ArrayList;
import java.util.UUID;

import javax.sql.DataSource;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.htc.circontrol.CIRControl;
import com.htc.htcircontrol.HtcIrData;

public class ExistingRemote  {

	StackItem st;
	static String  TAG = "MyRemote";
	CIRControl mControl;
	Context mContext;
	Handler mHandler = new Handler() 
	{ 
		@Override
		public void handleMessage(Message msg)
		{
			UUID rid;
			switch (msg.what) {
			case CIRControl.MSG_RET_LEARN_IR:
				//CIR APP developer can check UUID to see if the reply message is what he/she is interesting. 
				rid = (UUID) msg.getData().getSerializable(CIRControl.KEY_RESULT_ID);
				Log.e(TAG, "Receive IR Returned UUID: "+rid);
				
				//CIR APP developer must check learning IR data.
				//The learning IR data is in HtcIrData object.
				
					switch(msg.arg1) {
					case CIRControl.ERR_LEARNING_TIMEOUT:
						//TODO: timeout error because of CIR do not receive IR data from plastic remote. 
						break;
					case CIRControl.ERR_PULSE_ERROR:
						//TODO:
						//CIR receives IR data from plastic remote, but data is inappropriate.
						//The common error is caused by user he/she does not align the phone's CIR receiver
						// with CIR transmitter of plastic remote.		
						break;
					case CIRControl.ERR_OUT_OF_FREQ:
						//TODO:
						//This error is to warn user that the plastic remote is not supported or
						// the phone's CIR receiver does not align with CIR transmitter of plastic remote.
						break;
					case CIRControl.ERR_IO_ERROR:
						//TODO:
						//CIR hardware component is busy in doing early CIR activity.
						break;
					default:
						//TODO: other errors
						break;
					
				}
				break;
			case CIRControl.MSG_RET_TRANSMIT_IR:
				rid = (UUID) msg.getData().getSerializable(CIRControl.KEY_RESULT_ID);
				Log.e(TAG, "Send IR Returned UUID: "+rid);
				switch(msg.arg1) {
				case CIRControl.ERR_IO_ERROR:
					//TODO:
					//CIR hardware component is busy in doing early CIR command.
					Log.e("MyRemote", "HARDWARE BUSY");
					break;
				case CIRControl.ERR_INVALID_VALUE:
					//TODO:
					//The developer might use wrong arguments.
					Log.e("MyRemote", "INVALID VALUE");

					break;
				case CIRControl.ERR_CMD_DROPPED:
					//TODO:
					//SDK might be too busy to send IR key, developer can try later, or send IR key with non-droppable setting  
					Log.e("MyRemote", "COMMAND DROPPED");

					break;
				default:
					//TODO: other errors
					break;
				}
				break;
			case CIRControl.MSG_RET_CANCEL:
				switch(msg.arg1) {
				case CIRControl.ERR_IO_ERROR:
					//TODO:
					//CIR hardware component is busy in doing early CIR command.
					Log.e("MyRemote", "HARDWARE BUSY OTHER");

					break;
				case CIRControl.ERR_CANCEL_FAIL:
					//TODO:
					//CIR hardware component is busy in doing early CIR command.
					Log.e("MyRemote", "HARDWARE BUSY OTHER");

					break;
				default:
					//TODO: other errors
					break;
				} 
				break;
			default:
				super.handleMessage(msg);
			}
	    }
	};
	
	
	public int run =0;

	public ExistingRemote(Context c){
		st = new StackItem();
		mContext = c;
		mControl = new CIRControl (mContext, mHandler);
		
		mControl.start();
	}
	
	public void test(int dth_code,String dth_chancode){
		if(dth_code==-1 || dth_chancode==null){
			Log.e("Mtremote",""+dth_code +" OR dth_chancode is null");
			return;
		}
		StringBuilder build = new StringBuilder(dth_chancode);
		build.reverse();
		Log.e("MyRemoteExistingRemote ",dth_code + " | "+ dth_chancode);
		final ArrayList<StackItem> st = MainActivity.datasource.retIRItem(dth_code,dth_chancode);
		if(st.size()==0)
			return;
		final int t = Integer.parseInt(build.toString());
//		final int t = Integer.parseInt(build.toString());
		if(run==0){
			run = 1;
//			Log.e("MyRemote","st is :"+st.rc+" | "+st.freq+" | "+st.data.toString());
			new Thread(new Runnable() {
				public void run() {
					int temp =t;
					while(temp>0){
						int dig = temp%10;
						temp=temp/10;
						for(StackItem item : st){
							if(dig!=((item.retfuncid()-5)%10))
								 continue;
							 else
								 Log.e("MyRemote","Sending digit "+((item.retfuncid()-5)%10));
								UUID rid;
							try {
								int[] data =  new int[item.data.size()];
								for(int m=0;m<data.length;m++)
									data[m] = item.data.get(m);
								HtcIrData ird = new HtcIrData (item.rc, item.freq, data);
								rid = mControl.transmitIRCmd (ird, false); //no drop command
								int interval = ird.getPeriod();//msec
								Log.e("interval",""+interval);
		        				Thread.sleep(interval);
							}
							catch(IllegalArgumentException e) {
										//TODO: developer will get exception if any arguemnt of HtcIrData is wrong!  
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
					run=0;
				}}).start();
		}
	}
	
	
//	private class sendIR extends AsyncTask<Integer,Void,Integer>{
//
//		@Override
//		protected Integer doInBackground(Integer... params) {
//			// TODO Auto-generated method stub
//			return null;
//		}
//		
//	}
	
	
}
