package nb.cblink.youtube.data;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import nb.cblink.youtube.viewmodel.DetectCopyURLService;
import nb.cblink.youtube.viewmodel.MainActivityModelView;

/**
 * Created by nguyenbinh on 28/09/2016.
 */

public class BroadcastConectChange extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent) {
        if(!MainActivityModelView.isMyServiceRunning(DetectCopyURLService.class, context)){
            MainActivityModelView.startServiceDetectClipboard(context);
        }
    }
}
