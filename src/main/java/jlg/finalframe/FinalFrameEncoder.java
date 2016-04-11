/* 
* Created by dan-geabunea on 4/11/2016.
* This code is the property of JLG Consulting. Please
* check the license terms for this product to see under what
* conditions you can use or modify this source code.
*/
package jlg.finalframe;

public class FinalFrameEncoder {

    /*
    Given an Asterix message, add the appropriate header and footer to it in order to wrap it up
    in final frame format
     */
    public byte[] encode(byte[] message) {
        //if the message is null, can not wrap it, so return null
        if(message == null){
            return null;
        }

        return new byte[0];
    }
}
