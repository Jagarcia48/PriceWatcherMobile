package cs4330.cs.utep.pricewatcher;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;

public class EnableWifi extends AlertDialog.Builder {

    protected EnableWifi(Context context) {
        super(context);
        setTitle("ENABLE WIFI");
        setMessage("Wifi must be enable for this application to work");
        setCancelable(false);
        setPositiveButton("Enable", (dialog, which) -> {
            Intent intent = new Intent(WifiManager.ACTION_PICK_WIFI_NETWORK);
            context.startActivity(intent);
        });
    }
}