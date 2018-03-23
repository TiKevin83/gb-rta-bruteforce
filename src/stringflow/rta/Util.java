package stringflow.rta;

import java.io.*;

public class Util {

    public static byte[] readBytesFromFile(String filename) throws IOException {
        File fh = new File(filename);
        if(!fh.exists() || !fh.isFile() || !fh.canRead()) {
            throw new FileNotFoundException(filename);
        }
        long fileSize = fh.length();
        if(fileSize > Integer.MAX_VALUE) {
            throw new IOException(filename + " is too long to read in as a byte-array.");
        }
        FileInputStream fis = new FileInputStream(filename);
        byte[] result = new byte[fis.available()];
        fis.read(result);
        fis.close();
        return result;
    }

    public static void writeBytesToFile(String fileName, byte[] data) throws IOException {
        FileOutputStream fos = new FileOutputStream(fileName);
        fos.write(data);
        fos.close();
    }

    public static String readTextFile(String fileName) throws IOException {
        StringBuilder builder = new StringBuilder();
        String currentLine;
        BufferedReader reader;
        reader = new BufferedReader(new FileReader(new File(fileName)));
        while((currentLine = reader.readLine()) != null) {
            builder.append(currentLine).append("\n");
        }
        reader.close();
        return builder.toString();
    }

    public static String toHexString(int number) {
        String result = Integer.toHexString(number);
        if(result.length() == 1) {
            result = "0" + result;
        }
        return result.toUpperCase();
    }
}