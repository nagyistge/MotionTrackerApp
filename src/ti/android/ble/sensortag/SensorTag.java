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
  
      UUID_ACC_SERV = fromString("0667c144-6bbf-4a6b-96d7-40e2233a5962"),
      UUID_ACC_DATA = fromString("3bcea24f-99d5-44bd-b795-3cfdf6544c72"),
      UUID_ACC_CONF = fromString("2c242d78-42e3-4d6a-9062-4e4643313e74"), // 0: disable, 1: enable
      UUID_ACC_PERI = fromString("bfd7fcc8-6067-47bf-bfb1-47277b2f1e09"), // Period in milliseconds

      UUID_MAG_SERV = fromString("44da6c78-dec0-4e2d-b55c-0197ec007dfa"),
      UUID_MAG_DATA = fromString("a1c0be45-9d95-4512-ab3a-6b304b8e4bab"),
      UUID_MAG_CONF = fromString("bb96d47f-2b43-4c8a-93c4-7e87def53763"), // 0: disable, 1: enable
      UUID_MAG_PERI = fromString("a9a2add5-11a5-4978-a0c4-37106f9ce89f"), // Period in milliseconds

      UUID_GYR_SERV = fromString("35950f04-747c-47d9-a5f4-14405e57885f"), 
      UUID_GYR_DATA = fromString("4f9284c5-639f-4a07-b4e0-0c516989028c"),
      UUID_GYR_CONF = fromString("5adef8ca-fc1a-42b0-a7b8-428825d2ac8f"), // 0: disable, bit 0: enable x, bit 1: enable y, bit 2: enable z
      UUID_GYR_PERI = fromString("7027e6c5-dd93-4990-a090-e74a7a4ab021"), // Period in milliseconds

      UUID_EUL_SERV = fromString("4297b65e-1218-4d07-a203-9c1413b26430"), 
      UUID_EUL_DATA = fromString("41c590ce-154b-46b8-9892-db4edd6a8ca6"),
      UUID_EUL_CONF = fromString("6cd98885-babe-4733-bfaf-0fe5da52d9ed"), // 0: disable, bit 0: enable x, bit 1: enable y, bit 2: enable z
      UUID_EUL_PERI = fromString("5ff9210a-0ec0-4c8b-aa88-7418a740bf6b"); // Period in milliseconds;
  
}
