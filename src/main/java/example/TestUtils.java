package example;

/**
 * Created by eranga on 3/8/16.
 */
public class TestUtils {
//    public static void main(String args[]) {
//        try {
//            System.out.println(getHexLength("eranga".getBytes().clone()));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    public static byte[] getMessageWithHeader(byte[] msg) throws Exception {
        return concat(getHexLength(msg), msg);
    }

    private static byte[] getHexLength(byte[] response) throws Exception {
        int requestLen = response.length;
        String len = Integer.toHexString(requestLen);
        len = zeropad(len, 4);

        byte y[] = new byte[2];
        y[0] = (byte) Integer.parseInt(len.substring(0, 2), 16);
        y[1] = (byte) Integer.parseInt(len.substring(2, 4), 16);

        return y;
    }

    public static String zeropad(String s, int len) throws Exception {
        return padleft(s, len, '0');
    }

    public static String padleft(String s, int len, char c) throws Exception {
        s = s.trim();
        if (s.length() > len)
            throw new Exception("invalid len " + s.length() + "/" + len);
        StringBuilder d = new StringBuilder(len);
        int fill = len - s.length();
        while (fill-- > 0)
            d.append(c);
        d.append(s);
        return d.toString();
    }

    public static byte[] concat(byte[] array1, byte[] array2) {
        byte[] concatArray = new byte[array1.length + array2.length];
        System.arraycopy(array1, 0, concatArray, 0, array1.length);
        System.arraycopy(array2, 0, concatArray, array1.length, array2.length);
        return concatArray;
    }
}

