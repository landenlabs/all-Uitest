/*
 * Copyright (c) 2020 Dennis Lang (LanDen Labs) landenlabs@gmail.com
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the
 * following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial
 * portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT
 * LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN
 * NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 * @author Dennis Lang
 * @see https://LanDenLabs.com/
 */

package com.landenlabs.all_uiTest;

import java.util.Locale;


/**
 * Test data for GridView
 */
class TestData {

    //  3PM | 2° | icon | drop none
    static final int COL_TIME = 0;
    static final int COL_TEMP = 1;
    static final int COL_WxICON = 2;
    static final int COL_RAIN = 3;

    static class WxData {
        final String time;
        final String temp;
        final int wxicon;
        final float rainPercent;

        WxData(
                String time, String temp, int wxicon, float rainPercent) {
            this.time = time;
            this.temp = temp;
            this.wxicon = wxicon;
            this.rainPercent = rainPercent;
        }
        String getDetails(int col) {
            return String.format(Locale.US, "Details for col %d\nSome\ninformation\nabout cell", col);
        }
        static int columns() { return 4; }
    }

    static final WxData[] WXDATA = new WxData[]{
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
