# FinalFrameAPI
A concise API that simplifies your work when using the Final Frame format. This format is normally used to persist Asterix information in a
file. Tools like SAASC or RAPS can extract Asterix data in the final frame format.

### What is final frame?

The final frame format is used to wrap an Asterix payload within a header and a footer. The overall structure of a final frame message is
the following:

|Header|Payload|Footer|

The header is made up of 8 bytes where:
- bytes 0,1 give the length of the final frame message (final frame wrapping and payload)
- bytes 2,3,4 are empty and not used
- bytes 5,6,7 give the time of the final frame expressed in hundredths of a second (100 units make up a second)

The payload is the actual Asterix message

The footer is made up of 4 bytes, each with the value of a5 in hex, or 165 in base 10 encoding. So, a header must always be a5 a5 a5 a5

### Reading final frame data

In order to read final frame data, you can use the FinalFrameReader class. It is sufficient to instantiate a single instance and perform
reads until the input stream is finished. It is up to you to receive the input stream. The example bellow illustrates this

```java
try(InputStream is = ...){
     FinalFrameReader ffReader = new FinalFrameReader();
     //read to end of stream
     while (is.available() > 0){
         byte[] ffPayload = ffReader.read(is);
         if(ffPayload != null){
             //do stuff with payload
         }
     } catch (IOException e) {
         e.printStackTrace();
     }
}
```

You can choose to drop final frame that contain Asterix records with undesired categories. For example, if you want to decode only
the Asterix messages with category 62, you can use the following override of the read method:

```java
byte[] ffPayload = ffReader.read(is, 62);
if(ffPayload != null){
    //do stuff with payload
    //if final frame is not valid or if Asterix record is not cat 62, then the payload will be null
}
```

Using the reader, you can also check how many final frame packets have been decoded with success and how many have been dropped. For each
message that is dropped, a warn message is written to the console by the logger, which is called "jlg.final-frame-api".

```java
ffReader.getNbOfReadFinalFramePackets()
ffReader.getNbOfDroppedFinalFramePackets()
```

### Encoding data to final frame format

If you have the Asterix data represented as a byte array, then you can use the FinalFrameEncoder class to wrap into final frame format.
As for the reader, it is sufficient to instantiate a single instance and perform encodings by calling the encode method multiple times.

```java
byte[] payload = {11, 21, 31};
FinalFrameEncoder ffEncoder = new FinalFrameEncoder();
byte[] finalFrameMsg = ffEncoder.encode(payload);
```

### Installation

You can get the latest version of this by using Maven.

* Add a repositories section to your pom.xml file.

````xml
...
<repositories>
    <repository>
        <id>jlg-maven-repository</id>
        <url>http://maven.jlg.ro</url>
    </repository>
</repositories>
...
````

* Add the maven dependency

````xml
...
<dependency>
    <groupId>jlg-consulting</groupId>
    <artifactId>final-frame-api</artifactId>
    <version>1.0</version>
</dependency>
...
````

If you want a specific version, please insert the version value in the <version> tag.


