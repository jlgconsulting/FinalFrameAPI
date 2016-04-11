/* 
* Created by dan-geabunea on 4/11/2016.
* This code is the property of JLG Consulting. Please
* check the license terms for this product to see under what
* conditions you can use or modify this source code.
*/
package jlg.finalframe.test;

import jlg.finalframe.FinalFrameEncoder;
import org.junit.Test;

import static org.junit.Assert.assertNull;

public class FinalFrameEncoderTest {



    @Test
    public void when_asterix_payload_is_null_should_return_null(){
        //arrange
        byte[] payload = null;
        FinalFrameEncoder ffEncoder = new FinalFrameEncoder();

        //act
        byte[] finalFrameMsg = ffEncoder.encode(payload);

        //assert
        assertNull(finalFrameMsg);
    }

}
