package jlg.finalframe.test;

import jlg.finalframe.FinalFrameReader;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.context.annotation.Description;

import java.io.File;
import java.io.InputStream;

import static org.junit.Assert.assertNull;

public class FinalFrameReaderTest {

    @Test
    @Description(
    "We read a final frame file with a single packet." +
    "If thr dimension of the final frame (2 first bytes) is larger than the available" +
    "length of the file, then the reader should return null.")
    public void when_final_frame_dimension_is_wrong_should_return_null() {
        //arrange
        InputStream is = TestHelper.getFileInputStreamFromResource("final_frame_wrong_dimension_sample_one_packet.ff");
        FinalFrameReader ffReader = new FinalFrameReader();

        //act
        byte[] ffPayload = ffReader.read(is);

        //assert
        assertNull(ffPayload);
    }

}
