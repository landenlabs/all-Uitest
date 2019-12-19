package com.landenlabs.all_uiTest;

import java.util.Locale;

public class WxHourlyData {

    //  3PM | 2° | icon | drop none
    static final int COL_TIME = 0;
    static final int COL_TEMP = 1;
    static final int COL_WxICON = 2;
    static final int COL_RAIN = 3;

    static class WxData {
        String time;
        String temp;
        int wxicon;
        float rainPercent;

        WxData(
                String time, String temp, int wxicon, float rainPercent) {
            this.time = time;
            this.temp = temp;
            this.wxicon = wxicon;
            this.rainPercent = rainPercent;
        }
        String getDetails(int col) {
            return String.format(Locale.US, "Details for column %d\nWeather\nStuff\nLast Line", col);
        }
        static int columns() { return 4; }
    }

    static WxData[] WXDATA = new WxData[]{
            new WxData("4PM", " 2°", R.drawable.wx_sun_30d, 0),
            new WxData("5PM", "12°", R.drawable.wx_sun_31d, 10),
            new WxData("6PM", "18°", R.drawable.wx_sun_32d, 20),
            new WxData("7PM", "22°", R.drawable.wx_sun_34d, 70),
            new WxData("8PM", "23°", R.drawable.wx_sun_30d,0),
    };

    static int size() {
        return WXDATA.length * WxData.columns();
    }
}
