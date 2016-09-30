package nb.cblink.youtube.viewmodel;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.widget.Toast;

/**
 * Created by nguyenbinh on 17/09/2016.
 */

public class TrainModelView {

    private Context context;
    String[] url = {"https://www.youtube.com/watch?v=2miZUmwjB_8",
            "https://www.youtube.com/watch?v=T8qRJJgxXGE",
            "https://www.youtube.com/watch?v=46MD5EkVK1c"};
    String[] message = {"Go to app Youtube, choose video then share to app Video Download YT",
    "Open web youtube.com, then copy url. A notification show, you just need click in it",
    "Copy a url of youtube, go to app Video Download YT, paste it to edit text then click button next to edit text"};

    public TrainModelView(Context context) {
        this.context = context;
    }

    public void trainAppYoutube() {
        showDialogNotConnectInternet("Download from app", 0);
    }

    public void trainWebYoutube() {
        showDialogNotConnectInternet("Download from web", 1);
    }

    public void trainOther() {
        showDialogNotConnectInternet("Other way", 2);
    }


    void showDialogNotConnectInternet(String title, final int idTrain) {
        AlertDialog.Builder builder = new
                AlertDialog.Builder(context);
        builder.setTitle(title)
                .setMessage(message[idTrain])
                .setPositiveButton("Got it",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int id) {
                            }
                        })
                .setNegativeButton("Watch Video",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int id) {
                                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url[idTrain]));
                                context.startActivity(browserIntent);
                            }
                        });
        builder.create().show();
    }

}
