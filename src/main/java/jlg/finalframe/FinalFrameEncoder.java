/* 
* Created by dan-geabunea on 4/11/2016.
* This code is the property of JLG Consulting. Please
* check the license terms for this product to see under what
* conditions you can use or modify this source code.
*/
package jlg.finalframe;

import java.nio.ByteBuffer;
import java.util.Date;

public class FinalFrameEncoder {

    private ByteBuffer lengthBuffer;
    private ByteBuffer timeBuffer;

    public FinalFrameEncoder() {
        lengthBuffer = ByteBuffer.allocate(4);
        timeBuffer = ByteBuffer.allocate(4);
    }

    /*
    Given an Asterix message, add the appropriate header and footer to it in order to wrap it up
    in final frame format
     */
    public byte[] encode(byte[] message) {
        //if the message is null, can not wrap it, so return null
        if (message == null) {
            return null;
        }

        int finalFrameMsgLength = FinalFrameConstants.FINAL_FRAME_WRAPPING_LENGTH + message.length;
        byte[] finalFrameMessage = new byte[finalFrameMsgLength];

        //populate header with length
        lengthBuffer.putInt(message.length);
        finalFrameMessage[0] = lengthBuffer.get(2);
        finalFrameMessage[1] = lengthBuffer.get(3);

        finalFrameMessage[2] = 0;
        finalFrameMessage[3] = 0;
        finalFrameMessage[4] = 0;

        int hundredthsOfSecondsFromMidnight = (int) (new Date().getTime() / 10);
        timeBuffer.putInt(hundredthsOfSecondsFromMidnight);
        finalFrameMessage[5] = timeBuffer.get(1);
        finalFrameMessage[6] = timeBuffer.get(2);
        finalFrameMessage[7] = timeBuffer.get(3);

        //populate with payload
        for (int i = 0; i < message.length; i++) {
            finalFrameMessage[8 + i] = message[i];
        }

        //populate footer with 165 (a5 in hex)
        for (int i = 0; i < 4; i++) {
            finalFrameMessage[finalFrameMsgLength - i - 1] = (byte) 165;
        }

        //clear the byte buffers
        lengthBuffer.clear();
        timeBuffer.clear();

        return finalFrameMessage;
    }
}
