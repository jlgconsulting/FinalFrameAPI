/*
* Created by dan-geabunea on 4/11/2016.
* This code is the property of JLG Consulting. Please
* check the license terms for this product to see under what
* conditions you can use or modify this source code.
*/
package jlg.finalframe.test;

import jlg.finalframe.FinalFrameConstants;
import jlg.finalframe.FinalFrameReader;
import org.junit.Test;

import java.io.InputStream;

import static org.junit.Assert.*;

public class FinalFrameReaderTest {

    /*
    The goal of this test suite is to verify the correct reading of final frame messages/packets from a given file.
    The tests to be executed:
    - one with a correct final frame format, that should return the message payload
    - one where the length from the final frame message header exceeds the file length, that should be discarded
    - one where the footer of the final frame is not valid (a5 a5 a5 a5), that should be discarded.
    - two tests that check if the asterix category filtering works
    The input data is read from the /resources folder. All the test files contain a single final frame packet.
     */

    @Test
    public void when_final_frame_dimension_is_wrong_should_return_null() {
        //arrange
        InputStream is = TestHelper.getFileInputStreamFromResource("final_frame_wrong_dimension_sample_one_packet.ff");
        FinalFrameReader ffReader = new FinalFrameReader();

        //act
        byte[] ffPayload = ffReader.read(is);

        //assert
        assertNull(ffPayload);
        assertEquals(0, ffReader.getNbOfReadFinalFramePackets());
        assertEquals(1, ffReader.getNbOfDroppedFinalFramePackets());
    }

    @Test
    public void when_final_frame_has_wrong_footer_should_return_null() {
        //arrange
        InputStream is = TestHelper.getFileInputStreamFromResource("final_frame_wrong_footer_sample_one_packet.ff");
        FinalFrameReader ffReader = new FinalFrameReader();

        //act
        byte[] ffPayload = ffReader.read(is);

        //assert
        assertNull(ffPayload);
        assertEquals(0, ffReader.getNbOfReadFinalFramePackets());
        assertEquals(1, ffReader.getNbOfDroppedFinalFramePackets());
    }

    @Test
    public void when_message_is_correct_should_return_byte_array_with_payload(){
        //arrange
        InputStream is = TestHelper.getFileInputStreamFromResource("final_frame_correct_sample_one_packet.ff");
        FinalFrameReader ffReader = new FinalFrameReader();

        //act
        byte[] ffPayload = ffReader.read(is);

        //assert
        assertNotNull(ffPayload);
        assertEquals(70- FinalFrameConstants.FINAL_FRAME_WRAPPING_LENGTH, ffPayload.length);
        assertEquals(1, ffReader.getNbOfReadFinalFramePackets());
        assertEquals(0, ffReader.getNbOfDroppedFinalFramePackets());
    }

    @Test
    public void when_message_is_in_allowed_categories_should_return_payload(){
        //arrange
        InputStream isCat62 = TestHelper.getFileInputStreamFromResource("final_frame_correct_sample_one_packet.ff");
        FinalFrameReader ffReader = new FinalFrameReader();

        //act
        byte[] ffPayload = ffReader.read(isCat62,62);

        //assert
        assertNotNull(ffPayload);
        assertEquals(70- FinalFrameConstants.FINAL_FRAME_WRAPPING_LENGTH, ffPayload.length);
        assertEquals(1, ffReader.getNbOfReadFinalFramePackets());
        assertEquals(0, ffReader.getNbOfDroppedFinalFramePackets());
    }

    @Test
    public void when_message_is_in_not_allowed_categories_should_return_payload(){
        //arrange
        InputStream isCat62 = TestHelper.getFileInputStreamFromResource("final_frame_correct_sample_one_packet.ff");
        FinalFrameReader ffReader = new FinalFrameReader();

        //act
        byte[] ffPayload = ffReader.read(isCat62,65);

        //assert
        assertNull(ffPayload);
        assertEquals(1, ffReader.getNbOfReadFinalFramePackets());
        assertEquals(0, ffReader.getNbOfDroppedFinalFramePackets());
    }
}
