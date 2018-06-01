package ui.fengan.com.fingerlayout;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class Demo1Aty extends AppCompatActivity {
    private int fadeIn = R.anim.fade_in;
    private int fadeOut = R.anim.fade_out;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo1_aty);
        FingerViewGroup adFingerViewGroup = findViewById(R.id.ad_finger_vg);
        TextView textView = findViewById(R.id.tv);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(Demo1Aty.this,"click",Toast.LENGTH_SHORT).show();
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
