package com.landenlabs.all_uiTest;

/*
 * Copyright (C) 2019 Dennis Lang (landenlabs@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
