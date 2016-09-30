package nb.cblink.youtube.data;

import android.app.DownloadManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import nb.cblink.youtube.R;

/**
 * Created by nguyenbinh on 15/09/2016.
 */

public class DownloadBroadcastReceiver extends BroadcastReceiver {

    public long startDownload(Context context, DownloadManager downloadManager, String url, String title) {
        try {
            long idDownload = processDownloadSong(downloadManager, url, title);
            Toast.makeText(context, "Start download", Toast.LENGTH_SHORT).show();
            return idDownload;
        } catch (Exception e) {
            showDialogCanntWrite(context);
        }
        return -1;
    }

    void showDialogCanntWrite(Context context) {
        AlertDialog.Builder builder = new
                AlertDialog.Builder(context);
        builder.setTitle("Can not write data")
                .setMessage("The cause may be due:\n" +
                        "\t- Application don't have write permission\n" +
                        "\t- Not enough memory")
                .setPositiveButton("Ok",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int id) {
                            }
                        });
        builder.create().show();
    }

    private long processDownloadSong(DownloadManager downloadManager, String url, String title) throws Exception {
        Uri uri = Uri.parse(url);
        DownloadManager.Request request = new DownloadManager.Request(uri);
        //Setting title of request
        request.setTitle(title);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "");
        return downloadManager.enqueue(request);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {
            long referenceId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);

            DownloadManager.Query myDownloadQuery = new DownloadManager.Query();
            //set the query filter to our previously Enqueued download
            myDownloadQuery.setFilterById(referenceId);

            //Query the download manager about downloads that have been requested.
            Cursor cursor = downloadManager.query(myDownloadQuery);
            cursor.moveToFirst();
            int filenameIndex = cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_FILENAME);
            String fileName = "";
            String location = "";
            Intent intentOpenFile = new Intent(Intent.ACTION_VIEW);
            PendingIntent pendingIntent = null;
            try {
                fileName = cursor.getString(filenameIndex);
                filenameIndex = fileName.lastIndexOf("/");
                location = fileName.substring(0, fileName.lastIndexOf("/"));
                fileName = fileName.substring(filenameIndex + 1, fileName.length());

                intentOpenFile.setDataAndType(downloadManager.getUriForDownloadedFile(referenceId), "video/*");
                Intent.createChooser(intentOpenFile, "Choose app to play");
                pendingIntent = PendingIntent.getActivity(context, 0, intentOpenFile, PendingIntent.FLAG_ONE_SHOT);

            } catch (NullPointerException e) {
                fileName = "Download complete";
                location = "Download";
                pendingIntent = null;
            }
            NotificationCompat.Builder notificationBuilder = new
                    NotificationCompat.Builder(context)
                    .setSmallIcon(R.drawable.play)
                    .setContentTitle(fileName)
                    .setContentText("Location: \n" + location)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true);
            NotificationManager notificationManager =
                    (NotificationManager) context.getSystemService(
                            Context.NOTIFICATION_SERVICE);
            notificationManager.notify(19871,
                    notificationBuilder.build());
        }
    }
}
