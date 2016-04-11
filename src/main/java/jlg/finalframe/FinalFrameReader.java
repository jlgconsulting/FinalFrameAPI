/* 
* Created by dan-geabunea on 4/7/2016.
* This code is the property of JLG Consulting. Please
* check the license terms for this product to see under what
* conditions you can use or modify this source code.
*/
package jlg.finalframe;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;

/**
 * Class that provides methods for reading final frame packets. A final frame packet has the following structure:
 * header => 8 bytes
 *   bytes 0,1 give the length of the frame
 *   bytes 2,3,4 are unused
 *   bytes 5,6,7 give the time
 * payload => length of the frame - 12 (header + footer). The payload has Asterix messages
 * footer => 4 bytes
 *   each byte has a hex value of A5 (165 in decimal)
 */
public class FinalFrameReader {
    private int nbOfReadFinalFramePackets;
    private int nbOfDroppedFinalFramePackets;
    private int nbOfDroppedFinalFramePacketsBecauseOfInvalidSize;
    private int nbOfDroppedFinalFramePacketsBecauseOfInvalidFooter;
    private Logger logger;

    private byte[] finalFrameHeader;
    private byte[] finalFrameFooter;

    public FinalFrameReader() {
        nbOfReadFinalFramePackets = 0;
        nbOfDroppedFinalFramePackets = 0;
        finalFrameHeader = new byte[FinalFrameConstants.FINAL_FRAME_HEADER_LENGTH];
        finalFrameFooter = new byte[FinalFrameConstants.FINAL_FRAME_FOOTER_LENGTH];
        logger = LoggerFactory.getLogger("jlg.final-frame-api");
    }

    /**
     * Read the input stream and extract payload from final frame
     *
     * @param is the input stream containing the final frame packet
     * @return the payload of the final frame packet or NULL if the data is not valid final frame
     */
    public byte[] read(InputStream is) {
        try {
            //read header
            is.read(finalFrameHeader, 0, finalFrameHeader.length);
            int frameLength = getFrameLength();
            int availableBytes = is.available();
            //we need to add the 8 bytes from the header for the comparison
            if (!isHeaderValid(frameLength, availableBytes + finalFrameHeader.length)) {
                return null;
            }
            int finalFrameTime = getFrameTime();

            //read payload
            byte[] finalFramePayload = new byte[frameLength - finalFrameHeader.length - finalFrameFooter.length];
            is.read(finalFramePayload, 0, finalFramePayload.length);

            //read footer
            is.read(finalFrameFooter, 0, finalFrameFooter.length);
            if (!isFooterValid()) {
                return null;
            }
            nbOfReadFinalFramePackets++;
            return finalFramePayload;
        } catch (IOException e) {
            logger.error("(FinalFrameReader::read) Error while reading data from stream. Final frame packet will be discarded.");
            return null;
        }
    }

    /**
     * Returns the number of final frame packets that were correct. The counters are incremented each time the
     * read method is used, therefor it is recommended to use one reader for multiple reads.
     * @return the number of final frame packets that were correct
     */
    public int getNbOfReadFinalFramePackets() {
        return nbOfReadFinalFramePackets;
    }

    /**
     * Returns the number of dropped final frame packets in the stream. Dropping of packets happens when the FF length exceeds
     * the stream length, when the footer is invalid or by an unexpected IOException. The counters are incremented each time the
     * read method is used, therefor it is recommended to use one reader for multiple reads.
     * @return the number of final frame packets that were dropped
     */
    public int getNbOfDroppedFinalFramePackets() {
        return nbOfDroppedFinalFramePackets;
    }

    /**
     * Get the time of the final frame packet. This is given by reading 3 bytes in the header (5,6,7)
     * @return the time of the final frame packet
     */
    private int getFrameTime() {
        int currentFrameTime =
                Byte.toUnsignedInt(finalFrameHeader[5]) * 256 * 256 +
                        Byte.toUnsignedInt(finalFrameHeader[6]) * 256 +
                        Byte.toUnsignedInt(finalFrameHeader[7]);
        return currentFrameTime;
    }

    /**
     * Parse the header of the final frame and read the length value. This is given by the first 2 bytes
     * in the final frame header.
     * @return the total length of the final frame packet (header + payload + footer)
     */
    private int getFrameLength() {
        int finalFramePacketSize =
                Byte.toUnsignedInt(finalFrameHeader[0]) * 256 +
                Byte.toUnsignedInt(finalFrameHeader[1]);
        return finalFramePacketSize;
    }

    /**
     * In final frame format, the footer always has 4 bytes with the A5 value, or 165 in decimal.
     * @return true if the footer is valid FF footer, false otherwise
     */
    private boolean isFooterValid() {
        for (int i = 0; i < finalFrameFooter.length; i++) {
            ///165 = A5 in hexadecimal. In final frame format, the footer is always A5A5A5A5 (A5 x 4 times)
            int unsignedValue = Byte.toUnsignedInt(finalFrameFooter[i]);
            if (unsignedValue != 165) {
                nbOfDroppedFinalFramePackets++;
                nbOfDroppedFinalFramePacketsBecauseOfInvalidFooter++;
                logger.warn("(FinalFrameReader::read) Dropped final frame packet because an invalid " +
                        "character was found in the footer: " + unsignedValue + " (dec), " +
                        Integer.toHexString(unsignedValue) + " (hex)");

                return false;
            }
        }
        return true;
    }

    /**
     * In final frame format, the header has 8 bytes. The first two bytes are combined to give the length of the
     * entire final frame. If the frame length exceeds the remaining input stream length, then it can not be
     * processed
     * @param frameLength the length of the final frame packet (header + payload + footer)
     * @param availableBytes the remaining bytes on the stream
     * @return true if the header is valid, false otherwise
     */
    private boolean isHeaderValid(int frameLength, int availableBytes) {
        if (availableBytes < frameLength) {
            //the packet size is larger than the available stream
            nbOfDroppedFinalFramePackets++;
            nbOfDroppedFinalFramePacketsBecauseOfInvalidSize++;
            logger.warn("(FinalFrameReader::read) Dropped final frame packet because its size exceeds the remaining available stream. " +
                    "Size of final frame packet is: " + frameLength + " bytes. " +
                    "Remaining size on stream is: " + availableBytes + " bytes");
            return false;
        }
        return true;
    }
}
