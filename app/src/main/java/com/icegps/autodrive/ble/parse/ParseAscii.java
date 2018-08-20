package com.icegps.autodrive.ble.parse;


/**
 * Created by 111 on 2018/1/9.
 */

public class ParseAscii implements Parse {
    private String asciiStr = "";
    private boolean isWorking;
    private OnAsciiCallback onAsciiCallback;

    public void parseData(byte bt) {
        if (bt == '$') {
            asciiStr = "";
            isWorking = true;
        }
        if (isWorking && asciiStr.length() >= 7 && bt == '\n') {
            asciiStr = asciiStr.trim();
            if (checksum(asciiStr)) {
                if (onAsciiCallback != null) {
                    onAsciiCallback.onAscii(asciiStr);
                }
            }
            asciiStr = "";
            isWorking = false;
        }
        if (isWorking) {
            asciiStr += new String(new byte[]{bt});
        }
        if (asciiStr.length() == 7) {
            if (!asciiStr.equals("$ICEGPS")) {
                asciiStr = "";
                isWorking = false;
            }
        }
        if (asciiStr.length() > 256 * 2) {
            asciiStr = "";
            isWorking = false;
        }
    }

    /**
     * 检查指令结尾校验和
     */
    private boolean checksum(String buff) {
        int sum = 0;
        int cs = 0;
        int pos = buff.indexOf("*");
        if (pos == 0)
            return false;
        for (int i = 1; i < pos; i++)
            sum ^= buff.charAt(i);
        try {
            cs = Integer.parseInt(buff.substring(pos + 1, buff.length()), 16);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            isWorking = false;
            asciiStr = "";
        }
        return cs == sum;
    }

    public void setOnAsciiCallback(OnAsciiCallback onAsciiCallback) {
        this.onAsciiCallback = onAsciiCallback;
    }

    public interface OnAsciiCallback {
        void onAscii(String data);
    }


    @Override
    public boolean isWorking() {
        return false;
    }


}
