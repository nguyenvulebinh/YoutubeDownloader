package nb.cblink.youtube.viewmodel;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import nb.cblink.youtube.R;
import nb.cblink.youtube.model.YoutubeResource;
import nb.cblink.youtube.view.activity.GetYTContentActivity;

/**
 * Created by nguyenbinh on 27/09/2016.
 */

public class DetectCopyURLService extends Service implements ClipboardManager.OnPrimaryClipChangedListener {


    @Override
    public void onCreate() {
        super.onCreate();
        ((ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE)).addPrimaryClipChangedListener(this);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d("DetectClipboardService", "OnBindService");
        return null;
    }

    public void createNotify(String title, String content, String url) {
        Intent activityIntent = new Intent(this,GetYTContentActivity.class);
        activityIntent.setAction(Intent.ACTION_SEND);
        activityIntent.setType("text/plain");
        activityIntent.putExtra(Intent.EXTRA_TEXT, url);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0, activityIntent, PendingIntent.FLAG_ONE_SHOT);
        NotificationCompat.Builder notificationBuilder = new
                NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(content)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);
        NotificationManager notificationManager =
                (NotificationManager) this.getSystemService(
                        Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(0);
        notificationManager.notify(0,
                notificationBuilder.build());
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("DetectClipboardService", "OnDestroyService");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("DetectClipboardService", "OnStartService");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onPrimaryClipChanged() {
        try {
            String contentCopy = ((ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE)).getPrimaryClip().getItemAt(0).getText().toString();
            YoutubeResource resource = new YoutubeResource(contentCopy);
            if (resource.isMatch()) {
                createNotify("Detected Youtube url", contentCopy, contentCopy);
            }
        } catch (NullPointerException ex) {

        }
    }
}
