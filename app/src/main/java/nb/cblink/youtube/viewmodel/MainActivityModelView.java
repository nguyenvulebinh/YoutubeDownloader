package nb.cblink.youtube.viewmodel;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.databinding.BaseObservable;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Toast;

import nb.cblink.youtube.R;
import nb.cblink.youtube.model.YoutubeResource;
import nb.cblink.youtube.view.activity.GetYTContentActivity;
import nb.cblink.youtube.view.activity.MainActivity;
import nb.cblink.youtube.view.fragment.TrainFragment;

/**
 * Created by nguyenbinh on 27/09/2016.
 */

public class MainActivityModelView extends BaseObservable {
    private MainActivity context;
    private String textSearch;

    public MainActivityModelView(MainActivity context) {
        this.context = context;
        init();
    }

    private void init() {
        //Khoi tao fragment train
        loadTrain();

        //Chay service tu dong copy neu can thiet
        if(!isMyServiceRunning(DetectCopyURLService.class, context)){
            startServiceDetectClipboard(context);
        }

    }

    private void loadTrain(){
        FragmentTransaction transaction = context.getSupportFragmentManager().beginTransaction();
        TrainFragment fragmentTrain = new TrainFragment();
        transaction.replace(R.id.main_content_view, fragmentTrain);
        transaction.commit();
    }


    public static void startServiceDetectClipboard(Context context){
        Intent myIntent = new Intent(context, DetectCopyURLService.class);
        context.startService(myIntent);
    }

    public static boolean isMyServiceRunning(Class<?> serviceClass,Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public void onClickGetLink() {
        YoutubeResource resource = new YoutubeResource(textSearch);
        if (resource.isMatch()) {
            Intent activityIntent = new Intent(context, GetYTContentActivity.class);
            activityIntent.setAction(Intent.ACTION_SEND);
            activityIntent.setType("text/plain");
            activityIntent.putExtra(Intent.EXTRA_TEXT, textSearch);
            context.startActivity(activityIntent);
        }else {
            Toast.makeText(context, "Url not match!", Toast.LENGTH_SHORT).show();
        }
    }

    public String getTextSearch() {
        return textSearch;
    }

    public void setTextSearch(String textSearch) {
        this.textSearch = textSearch;
        notifyChange();
    }


    public TextWatcher onBasicChanged = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            setTextSearch(charSequence.toString());
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };
}
