package stringflow.rta;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;

public class Util {

    public static byte[] readBytesFromFile(String fileName) throws IOException {
        File fh = new File(fileName);
        if(!fh.exists() || !fh.isFile() || !fh.canRead()) {
            throw new FileNotFoundException(fileName);
        }
        long fileSize = fh.length();
        if(fileSize > Integer.MAX_VALUE) {
            throw new IOException(fileName + " is too long to read in as a byte-array.");
        }
        FileInputStream fis = new FileInputStream(fileName);
        byte[] result = new byte[fis.available()];
        fis.read(result);
        fis.close();
        return result;
    }

    public static void writeBytesToFile(String fileName, ByteBuffer buffer) throws IOException {
        FileChannel channel = new FileOutputStream(fileName).getChannel();
        channel.write(buffer);
        channel.close();
    }

    public static void writeBytesToFile(String fileName, byte data[]) throws IOException {
        FileOutputStream fos = new FileOutputStream(fileName);
        fos.write(data);
        fos.close();
    }

    public static String readTextFile(String fileName) throws IOException {
        File file = new File(fileName);
        if(!file.exists()) {
            throw new IOException(fileName + " does not exist.");
        }
        StringBuilder builder = new StringBuilder();
        String currentLine;
        BufferedReader reader = new BufferedReader(new FileReader(file));
        while((currentLine = reader.readLine()) != null) {
            builder.append(currentLine).append("\n");
        }
        reader.close();
        return builder.toString();
    }

    public static ByteBuffer loadByteBufferFromFile(String filename) throws IOException {
        byte[] byteArray = readBytesFromFile(filename);
        ByteBuffer res = ByteBuffer.allocateDirect(byteArray.length).order(ByteOrder.nativeOrder());
        for(int i = 0; i < byteArray.length; i++) {
            res.put(byteArray[i]);
        }
        return res;
    }

    public static String toHexString(int number) {
        String result = Integer.toHexString(number);
        if(result.length() == 1) {
            result = "0" + result;
        }
        return result.toUpperCase();
    }

    public static String getSpriteAddressIndexString(int addressIndex) {
        String result = addressIndex == 0 ? "Player" : String.valueOf(addressIndex);
        return result.length() == 1 ? "0" + result : result;
    }
}