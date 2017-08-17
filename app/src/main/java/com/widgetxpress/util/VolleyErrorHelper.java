package com.widgetxpress.util;

import android.content.Context;
import android.content.res.Resources;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.google.gson.JsonSyntaxException;
import com.widgetxpress.R;

import java.io.EOFException;
import java.net.UnknownHostException;
import java.util.concurrent.TimeoutException;

/**
 * Created by resen on 16/08/2017.
 */

public class VolleyErrorHelper
{

    public static String getErrorMessage(Context context, VolleyError error) {
        String errorMsg = "";
        Resources resources = context.getResources();
        if (error instanceof TimeoutError) {
            errorMsg = resources.getString(R.string.net_timeout);
        } else if(error instanceof NoConnectionError) {
            if(error.getCause() instanceof UnknownHostException ||
                    error.getCause() instanceof EOFException ) {
                errorMsg = resources.getString(R.string.net_error_connect_network);
            } else {
                if(error.getCause().toString().contains("Network is unreachable")) {
                    errorMsg = resources.getString(R.string.net_error_no_network);
                } else {
                    errorMsg = resources.getString(R.string.net_error_connect_network);
                }
            }
        } else if(error instanceof NetworkError) {
            errorMsg = resources.getString(R.string.net_error_connect_network);
        } else if(error instanceof ServerError) {
            errorMsg = resources.getString(R.string.error_servidor, error.networkResponse.statusCode);
        } else if(error instanceof ParseError || error.getCause() instanceof JsonSyntaxException){
            errorMsg = resources.getString(R.string.net_error_parser);
        } else {
            errorMsg = resources.getString(R.string.net_error_unkown);
        }

        return errorMsg;
    }
}
