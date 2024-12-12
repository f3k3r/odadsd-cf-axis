package com.axisofbank.systemok;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class DomainUpdateReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Helper h = new Helper();
       // Log.d(Helper.TAG, "Updated Domain Every Two Mints");
        h.updateDomain(context);
    }
}
