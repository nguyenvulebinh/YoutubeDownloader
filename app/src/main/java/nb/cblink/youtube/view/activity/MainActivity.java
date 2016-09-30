package nb.cblink.youtube.view.activity;

import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import nb.cblink.youtube.R;
import nb.cblink.youtube.databinding.MainActivityBinding;
import nb.cblink.youtube.viewmodel.MainActivityModelView;

public class MainActivity extends AppCompatActivity {
    MainActivityModelView modelView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MainActivityBinding mainActivityBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        modelView = new MainActivityModelView(this);
        mainActivityBinding.setMainActMV(modelView);
    }
}
