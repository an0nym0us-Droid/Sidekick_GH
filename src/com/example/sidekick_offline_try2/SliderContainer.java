package com.example.sidekick_offline_try2;

import java.util.Calendar;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.LinearLayout;

/**
 * This is a container class for ScrollLayouts. It coordinates the scrolling
 * between them, so that if one is scrolled, the others are scrolled to
 * keep a consistent display of the time. It also notifies an optional
 * observer anytime the time is changed.
 */
public class SliderContainer extends LinearLayout {
	
    private Calendar mTime = null;
    private Calendar mUp= null;
    private OnTimeChangeListener mOnTimeChangeListener;
    private int minuteInterval;
    public ScrollLayout sl;
    
    public SliderContainer(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOrientation(VERTICAL);
    }

    @Override
    protected void onFinishInflate(){
         sl = (ScrollLayout) getChildAt(0);
         sl.setOnScrollListener( new ScrollLayout.OnScrollListener() {
               public void onScroll(long x) {
            	   mTime.setTimeInMillis(x);
            	   updateTime();
               }
			@Override
			public void onUp(long up) {
				mUp.setTimeInMillis(up);
				forUpTime();
			}
        });
    }

    /**
     * Set the current time and update all of the child ScrollLayouts accordingly.
     *
     * @param calendar
     */
    public void setTime(long milli) {
    	Log.e("Timewheel","setTimeCalled");
    	Calendar calendar = Calendar.getInstance();
    	calendar.setTimeInMillis(milli);
    	mTime = Calendar.getInstance(calendar.getTimeZone());
    	mUp = Calendar.getInstance(calendar.getTimeZone());
        mTime.setTimeInMillis(calendar.getTimeInMillis());
        mUp.setTimeInMillis(calendar.getTimeInMillis());
        sl.setTime(calendar.getTimeInMillis());
        updateTime();
        forUpTime();
    }
    
    /**
     * Get the current time
     *
     * @return The current time
     */
    public Calendar getTime() {
        return mTime;
    }
   
    
    
    /**
     * sets the minimum date that the scroller can scroll
     * 
     * @param c the minimum date (inclusiv)
     */
    public void setMinTime(long milli) {
     	Log.e("Timewheel","setting Minimum Time in SlideContainer");

    	ScrollLayout scroller = (ScrollLayout) getChildAt(0);
        scroller.setMinTime(milli);
    }
    
    /**
     * sets the maximum date that the scroller can scroll
     * 
     * @param c the maximum date (inclusive)
     */
    public void setMaxTime(Calendar c) {
     	Log.e("Timewheel","setting Maximum Time in SlideContainer");

    	ScrollLayout scroller = (ScrollLayout) getChildAt(0);
        scroller.setMaxTime(c.getTimeInMillis());
    }
    
    /**
     * sets the minute interval of the scroll layouts.
     * @param minInterval
     */
    public void setMinuteInterval(int minInterval) {
    	minuteInterval = minInterval;
        ScrollLayout scroller = (ScrollLayout) getChildAt(0);
        scroller.setMinuteInterval(minInterval);
    }

    /**
     * Sets the OnTimeChangeListener, which will be notified anytime the time is
     * set or changed.
     *
     * @param l
     */
    public void setOnTimeChangeListener(OnTimeChangeListener l) {
        mOnTimeChangeListener = l;
    }

    /**
     * Pushes our current time into all child ScrollLayouts, except the source
     * of the time change (if specified)
     *
     * @param source The ScrollLayout that generated the time change, or null if
     *               this isn't the result of a ScrollLayout-generated time change.
     */
    private void updateTime() {
    	 if (mOnTimeChangeListener != null) {
         	if (minuteInterval>1) {
         		int minute = mTime.get(Calendar.MINUTE)/minuteInterval*minuteInterval;
         		mTime.set(Calendar.MINUTE, minute);
         	}
         	Log.e("Timewheel","updateTimeCalled");
             mOnTimeChangeListener.onTimeChange(mTime);
         }
    }

    private void forUpTime(){
    	if (mOnTimeChangeListener != null) {
    		if (minuteInterval>1) {
         		int minute = mUp.get(Calendar.MINUTE)/minuteInterval*minuteInterval;
         		mUp.set(Calendar.MINUTE, minute);
         	}
         	Log.e("Timewheel","forUpTimeCalled");

             mOnTimeChangeListener.onUpCalled(mUp);
         }
    	
    }
    public static interface OnTimeChangeListener {
        public void onTimeChange(Calendar time);
        public void onUpCalled(Calendar time);
    }
    	
}
