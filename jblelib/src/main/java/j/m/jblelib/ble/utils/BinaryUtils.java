package j.m.jblelib.ble.utils;

import android.text.TextUtils;

import java.nio.ByteBuffer;

/**
 * Created by JeongWoo on 2017/4/11.
 */

public class BinaryUtils {
    private static ByteBuffer buffer = ByteBuffer.allocate(8);

    public static byte[] int2BytesByLH(int value) {
        int temp = value;
        byte[] b = new byte[4];
        for (int i = 0; i < b.length; i++) {
            b[i] = new Integer(temp & 0xff).byteValue();// 将最低位保存在最高位
            temp = temp >> 8; // 向右移8位
        }
        return b;
    }

    public static int byteArray2Int(byte[] bytes) {
        byte[] array = new byte[4];
        for (int i = 0; i < 4; i++) {
            array[i] = bytes[3 - i];
        }
        int result = 0;
        byte loop;
        for (int i = 0; i < 4; i++) {
            loop = array[i];
            int offSet = array.length - i - 1;
            result += (loop & 0xFF) << (8 * offSet);
        }
        return result;
    }

    public static byte[] shortToByte(short number) {
        int temp = number;
        byte[] b = new byte[2];
        for (int i = 0; i < b.length; i++) {
            b[i] = new Integer(temp & 0xff).byteValue();// 将最低位保存在最低位
            temp = temp >> 8; // 向右移8位
        }
        return b;
    }

    public static short byteToShort(byte[] b) {
        short s = 0;
        short s0 = (short) (b[0] & 0xff);// 最低位
        short s1 = (short) (b[1] & 0xff);
        s1 <<= 8;
        s = (short) (s0 | s1);
        return s;
    }


    public static byte[] hexStringToBytes(String hexString) {
        if (hexString == null || hexString.equals("")) {
            return null;
        }
        if (hexString.length() % 2 != 0) {
            hexString = "0" + hexString;
        }
        hexString = hexString.toUpperCase();
        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] d = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
        }
        return d;
    }

    public static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }

    public static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString().toUpperCase();
    }

    public static byte[] longToByteArray(long num) {
        byte[] result = new byte[4];
/*    result[0] = (byte) (num >>> 56);// 取最高8位放到0下标
        result[1] = (byte) (num >>> 48);// 取最高8位放到0下标
      result[2] = (byte) (num >>> 40);// 取最高8位放到0下标
      result[3] = (byte) (num >>> 32);// 取最高8位放到0下标*/
        result[0] = (byte) (num >>> 24);// 取最高8位放到0下标
        result[1] = (byte) (num >>> 16);// 取次高8为放到1下标
        result[2] = (byte) (num >>> 8); // 取次低8位放到2下标
        result[3] = (byte) (num); // 取最低8位放到3下标
        return result;
    }

    //byte数组转成long
    public static long byteToLong(byte[] b) {
        long s = 0;
        long s0 = b[0] & 0xff;// 最低位
        long s1 = b[1] & 0xff;
        long s2 = b[2] & 0xff;
        long s3 = b[3] & 0xff;
/*    long s4 = b[4] & 0xff;// 最低位
      long s5 = b[5] & 0xff;
      long s6 = b[6] & 0xff;
      long s7 = b[7] & 0xff;*/

        // s0不变

        s2 <<= 8;
        s1 <<= 16;
        s0 <<= 24;
      /*s4 <<= 8 * 4;
      s5 <<= 8 * 5;
      s6 <<= 8 * 6;
      s7 <<= 8 * 7;*/
        s = s0 | s1 | s2 | s3;/*| s4 | s5 | s6 | s7;*/
        return s;
    }

    public static long bytesToLong(byte[] bytes) {
        buffer.put(bytes, 0, bytes.length);
        buffer.flip();//need flip
        return buffer.getInt();
    }

    public static String hexString2binaryString(String hexString) {
        if (hexString == null || hexString.length() % 2 != 0)
            return null;
        StringBuffer sbBinary = new StringBuffer();
        String tmp;
        for (int i = 0; i < hexString.length(); i++) {
            tmp = "0000"
                    + Integer.toBinaryString(Integer.parseInt(hexString
                    .substring(i, i + 1), 16));
            sbBinary.append(tmp.substring(tmp.length() - 4));
        }
        return sbBinary.toString();
    }

    /**
     * 比较两个Byte数组是否相等
     */
    public static boolean compareByte(byte[] b1, byte[] b2) {

        if (b1.length == b2.length) {
            for (int i = 0; i < b1.length; i++) {
                if (b1[i] != b2[i]) {
                    return false;
                }
            }
        } else {
            return false;
        }
        return true;
    }

    /**
     * 拼接高低位
     *
     * @param paramByte1 低位
     * @param paramByte2 高位
     * @return
     */
    public static long connect(byte paramByte1, byte paramByte2) {
        return (long) (0xFF00 & paramByte2 << 8 | paramByte1 & 0xFF);
    }

    public static String getMac(String mac) {
        if (TextUtils.isEmpty(mac)) {
            return "";
        } else if (mac.contains(":")) {
            mac = mac.replace(":", "");
        } else {
            String[] macs = new String[6];
            for (int i = 0; i <= 5; i++) {
                macs[i] = mac.substring(i * 2, i * 2 + 2);
            }
            mac = macs[0];
            for (int i = 1; i < macs.length; i++) {
                mac += ":" + macs[i];
            }
        }
        return mac;
    }

}
