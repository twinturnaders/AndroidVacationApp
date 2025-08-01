package wgu.bright.d308.alerts;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class AlertReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String title = intent.getStringExtra("title");
        String type = intent.getStringExtra("type");
        Toast.makeText(context, title + " is " + type + " today, have fun for the rest of us, who have to work!", Toast.LENGTH_LONG).show();
    }
}
