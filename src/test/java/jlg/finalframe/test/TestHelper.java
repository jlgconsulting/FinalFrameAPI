package jlg.finalframe.test;

import java.io.File;
import java.io.InputStream;

class TestHelper {
    public static File getFileFromResource(String fileName){
        String path = TestHelper.class.getClassLoader().getResource(fileName).getPath();
        File file = new File(path);

        return file;
    }

    public static InputStream getFileInputStreamFromResource(String fileName){
        InputStream fis = TestHelper.class.getClassLoader().getResourceAsStream(fileName);
        return fis;
    }
}
