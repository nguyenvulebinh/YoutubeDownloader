package nb.cblink.youtube.view.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.ConnectivityManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import nb.cblink.youtube.R;
import nb.cblink.youtube.databinding.GetYTContentBinding;
import nb.cblink.youtube.viewmodel.GetYTContentModelView;

public class GetYTContentActivity extends AppCompatActivity {
    GetYTContentModelView modelView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GetYTContentBinding mainActivityBinding = DataBindingUtil.setContentView(this, R.layout.get_youtube_content_layout);
        View layout = mainActivityBinding.getRoot();
        modelView = new GetYTContentModelView(this, (RecyclerView) layout.findViewById(R.id.recycleview_list_url));
        mainActivityBinding.setYtContentMV(modelView);
        if (!isNetworkConnected()) {
            showDialogNotConnectInternet();
        }else {
            try {
                getSupportActionBar().hide();
            } catch (NullPointerException e) {
            }
            Intent intent = getIntent();
            if (intent != null) handleIntent(intent);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        if (isNetworkConnected()) {
            super.onNewIntent(intent);
            handleIntent(intent);
        } else {
            showDialogNotConnectInternet();
        }
    }

    private void handleIntent(Intent intent) {
        String action = intent.getAction();
        String type = intent.getType();

        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if ("text/plain".equals(type)) {
                modelView.handleIntentShare(intent);
            }
        }
    }

    void showDialogNotConnectInternet() {
        AlertDialog.Builder builder = new
                AlertDialog.Builder(this);
        builder.setTitle("There is no internet connection")
                .setMessage("Turn on internet connection?")
                .setPositiveButton("Setting",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int id) {
                                startActivity(new Intent(android.provider.Settings.ACTION_SETTINGS));
                                finish();
                            }
                        })
                .setNegativeButton("Exit",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int id) {
                                finish();
                            }
                        });
        builder.create().show();
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }
}
