/* 
* Created by dan-geabunea on 4/11/2016.
* This code is the property of JLG Consulting. Please
* check the license terms for this product to see under what
* conditions you can use or modify this source code.
*/
package jlg.finalframe.test;

import jlg.finalframe.FinalFrameConstants;
import jlg.finalframe.FinalFrameEncoder;
import org.junit.Test;

import static org.junit.Assert.*;

public class FinalFrameEncoderTest {

    @Test
    public void when_asterix_payload_is_null_should_return_null() {
        //arrange
        byte[] payload = null;
        FinalFrameEncoder ffEncoder = new FinalFrameEncoder();

        //act
        byte[] finalFrameMsg = ffEncoder.encode(payload);

        //assert
        assertNull(finalFrameMsg);
    }

    @Test
    public void when_asterix_payload_is_empty_should_return_null(){
        //arrange
        byte[] payload = new byte[0];
        FinalFrameEncoder ffEncoder = new FinalFrameEncoder();

        //act
        byte[] finalFrameMsg = ffEncoder.encode(payload);

        //assert
        assertNull(finalFrameMsg);
    }

    @Test
    public void when_asterix_payload_exists_should_wrap_it_in_final_frame_format() {
        //arrange
        //we generate a random payload of 3 elements 1,2,3. It is not a valid Asterix message,
        //but this is not the purpose of the test.
        byte[] payload = {11, 21, 31};
        FinalFrameEncoder ffEncoder = new FinalFrameEncoder();

        //act
        byte[] finalFrameMsg = ffEncoder.encode(payload);

        //assert
        assertNotNull(finalFrameMsg);

        int expectedLength = FinalFrameConstants.FINAL_FRAME_WRAPPING_LENGTH + payload.length;
        assertEquals(expectedLength, finalFrameMsg.length);

        //check every byte except the datetime which is dynamic and can not be tested
        assertEquals(0, finalFrameMsg[0]);
        assertEquals(payload.length + FinalFrameConstants.FINAL_FRAME_WRAPPING_LENGTH, finalFrameMsg[1]);
        assertEquals(0, finalFrameMsg[2]);
        assertEquals(0, finalFrameMsg[3]);
        assertEquals(0, finalFrameMsg[4]);
        assertEquals(payload[0], finalFrameMsg[8]);
        assertEquals(payload[1], finalFrameMsg[9]);
        assertEquals(payload[2], finalFrameMsg[10]);
        assertEquals((byte) FinalFrameConstants.FINAL_FRAME_FOOTER_VALUE_BASE10, finalFrameMsg[11]);
        assertEquals((byte) FinalFrameConstants.FINAL_FRAME_FOOTER_VALUE_BASE10, finalFrameMsg[12]);
        assertEquals((byte) FinalFrameConstants.FINAL_FRAME_FOOTER_VALUE_BASE10, finalFrameMsg[13]);
        assertEquals((byte) FinalFrameConstants.FINAL_FRAME_FOOTER_VALUE_BASE10, finalFrameMsg[14]);
    }
}
