package com.example.kevinhuang.spf420client;

import java.text.SimpleDateFormat;
import java.util.TimeZone;

/**
 * Created by Kevin Huang on 4/25/2015.
 */
public class RequestandResponse {

    private String unixToDate(String unix_timestamp) {
        long timestamp = Long.parseLong(unix_timestamp) * 1000;

        TimeZone timeZone = TimeZone.getTimeZone("GMT+7");
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM d, yyyy 'at' h:mm a");
        sdf.setTimeZone(timeZone);
        String date = sdf.format(timestamp);

        return date.toString();
    }
}
