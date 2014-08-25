/**************************************************************************************************
  Filename:       SensorTag.java
  Revised:        $Date: 2013-08-30 11:44:31 +0200 (fr, 30 aug 2013) $
  Revision:       $Revision: 27454 $

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
  PROVIDED “AS IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESS OR IMPLIED,
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
package ti.android.ble.sensortag;

import static java.util.UUID.fromString;

import java.util.UUID;

public class SensorTag {

  public final static UUID 
  
      UUID_ACC_SERV = fromString("f000aa10-0451-4000-b000-000000000000"),
      UUID_ACC_DATA = fromString("f000aa11-0451-4000-b000-000000000000"),
      UUID_ACC_CONF = fromString("f000aa12-0451-4000-b000-000000000000"), // 0: disable, 1: enable
      UUID_ACC_PERI = fromString("f000aa13-0451-4000-b000-000000000000"), // Period in tens of milliseconds

      UUID_MAG_SERV = fromString("f000aa30-0451-4000-b000-000000000000"),
      UUID_MAG_DATA = fromString("f000aa31-0451-4000-b000-000000000000"),
      UUID_MAG_CONF = fromString("f000aa32-0451-4000-b000-000000000000"), // 0: disable, 1: enable
      UUID_MAG_PERI = fromString("f000aa33-0451-4000-b000-000000000000"), // Period in tens of milliseconds

      UUID_GYR_SERV = fromString("f000aa50-0451-4000-b000-000000000000"), 
      UUID_GYR_DATA = fromString("f000aa51-0451-4000-b000-000000000000"),
      UUID_GYR_CONF = fromString("f000aa52-0451-4000-b000-000000000000"), // 0: disable, bit 0: enable x, bit 1: enable y, bit 2: enable z
      UUID_GYR_PERI = fromString("f000aa53-0451-4000-b000-000000000000"), // Period in tens of milliseconds

      UUID_KEY_SERV = fromString("0000ffe0-0000-1000-8000-00805f9b34fb"), 
      UUID_KEY_DATA = fromString("0000ffe1-0000-1000-8000-00805f9b34fb"),

      UUID_EUL_SERV = fromString("4297b65e-1218-4d07-a203-9c1413b26430"), 
      UUID_EUL_DATA = fromString("41c590ce-154b-46b8-9892-db4edd6a8ca6"),
      UUID_EUL_CONF = fromString("6cd98885-babe-4733-bfaf-0fe5da52d9ed"), // 0: disable, bit 0: enable x, bit 1: enable y, bit 2: enable z
      UUID_EUL_PERI = fromString("5ff9210a-0ec0-4c8b-aa88-7418a740bf6b"); // Period in tens of milliseconds;
  
}
