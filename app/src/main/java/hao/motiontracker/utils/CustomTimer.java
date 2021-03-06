/**************************************************************************************************
  Filename:       CustomTimer.java
  Revised:        $Date: 2013-08-30 12:02:37 +0200 (fr, 30 aug 2013) $
  Revision:       $Revision: 27470 $

  Copyright 2013 Texas Instruments Incorporated. All rights reserved.
 
  IMPORTANT: Your use of this Software is limited to those specific rights
  granted under the terms of a software license agreement between the user
  who downloaded the software, his/her employer (which must be your employer)
  and Texas Instruments Incorporated (the "License").  You may not use this
  Software unless you agree to abide by the terms of the License. 
  The License limits your use, and you acknowledge, that the Software may not be 
  modified, copied or distributed unless used solely and exclusively in conjunction 
  with a Texas Instruments Bluetooth device. Other than for the foregoing purpose, 
  you may not use, reproduce, copy, prepare derivative works of, modify, distribute, 
  perform, display or sell this Software and/or its documentation for any purpose.
 
  YOU FURTHER ACKNOWLEDGE AND AGREE THAT THE SOFTWARE AND DOCUMENTATION ARE
  PROVIDED �AS IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESS OR IMPLIED,
  INCLUDING WITHOUT LIMITATION, ANY WARRANTY OF MERCHANTABILITY, TITLE,
  NON-INFRINGEMENT AND FITNESS FOR A PARTICULAR PURPOSE. IN NO EVENT SHALL
  TEXAS INSTRUMENTS OR ITS LICENSORS BE LIABLE OR OBLIGATED UNDER CONTRACT,
  NEGLIGENCE, STRICT LIABILITY, CONTRIBUTION, BREACH OF WARRANTY, OR OTHER
  LEGAL EQUITABLE THEORY ANY DIRECT OR INDIRECT DAMAGES OR EXPENSES
  INCLUDING BUT NOT LIMITED TO ANY INCIDENTAL, SPECIAL, INDIRECT, PUNITIVE
  OR CONSEQUENTIAL DAMAGES, LOST PROFITS OR LOST DATA, COST OF PROCUREMENT
  OF SUBSTITUTE GOODS, TECHNOLOGY, SERVICES, OR ANY CLAIMS BY THIRD PARTIES
  (INCLUDING BUT NOT LIMITED TO ANY DEFENSE THEREOF), OR OTHER SIMILAR COSTS.
 
  Should you have any questions regarding your right to use this Software,
  contact Texas Instruments Incorporated at www.TI.com

 **************************************************************************************************/
package hao.motiontracker.utils;

import java.util.Timer;
import java.util.TimerTask;

import android.widget.ProgressBar;

public class CustomTimer {
  private Timer mTimer;
  private CustomTimerCallback mCb = null;
  private ProgressBar mProgressBar;
  private int mTimeout;

  public CustomTimer(ProgressBar progressBar, int timeout, CustomTimerCallback cb) {
    mTimeout = timeout;
    mProgressBar = progressBar;
    mTimer = new Timer();
    ProgressTask t = new ProgressTask();
    mTimer.schedule(t, 0, 1000); // One second tick
    mCb = cb;
  }

  public void stop() {
    if (mTimer != null) {
      mTimer.cancel();
      mTimer = null;
    }
  }

  private class ProgressTask extends TimerTask {
    int i = 0;

    @Override
    public void run() {
      i++;
      if (mProgressBar != null)
        mProgressBar.setProgress(i);
      if (i >= mTimeout) {
        mTimer.cancel();
        mTimer = null;
        if (mCb != null)
          mCb.onTimeout();
      } else {
        if (mCb != null)
          mCb.onTick(i);
      }
    }
  }
}
