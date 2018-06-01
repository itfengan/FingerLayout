package ui.fengan.com.fingerlayout;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

public class Demo1Aty extends AppCompatActivity {
    private int fadeIn = R.anim.fade_in;
    private int fadeOut = R.anim.fade_out;
    private final String url = "http://fenganblogimgs.oss-cn-beijing.aliyuncs.com/blog/bwnwb.jpg";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo1_aty);
        FingerViewGroup adFingerViewGroup = findViewById(R.id.ad_finger_vg);
        final ImageView imageView = findViewById(R.id.iv);
        imageView.post(new Runnable() {
            @Override
            public void run() {
                Glide.with(Demo1Aty.this).load(url).asBitmap().into(imageView);
            }
        });
        adFingerViewGroup.setOnAlphaChangeListener(new FingerViewGroup.onAlphaChangedListener() {
            @Override
            public void onAlphaChanged(float alpha) {
                Log.e("fengan","[onAlphaChanged]:alpha="+alpha);
            }

            @Override
            public void onTranslationYChanged(float translationY) {
                Log.e("fengan","[onTranslationYChanged]:translationY="+translationY);
            }

            @Override
            public void onFinishAction() {
                finish();
                overridePendingTransition(fadeIn, fadeOut);
            }
        });
    }
}
