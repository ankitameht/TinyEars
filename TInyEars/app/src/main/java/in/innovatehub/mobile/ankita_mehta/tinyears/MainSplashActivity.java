package in.innovatehub.mobile.ankita_mehta.tinyears;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.graphics.drawable.AnimationDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;

public class MainSplashActivity extends AppCompatActivity {

    ImageView mGyroView;
    AnimationDrawable gyroAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_splash);
        mGyroView = (ImageView) findViewById(R.id.gyro);

      // mGyroView.setBackgroundResource(R.drawable.gyro_animation);
      //  gyroAnimation = (AnimationDrawable) mGyroView.getBackground();
      //  gyroAnimation.start();

        Thread myThread = new Thread(){
            @Override
            public void run(){
                try {
                    sleep(3000);
                    Intent newIntent = new Intent(getApplicationContext(),MainActivity.class);
                    startActivity(newIntent);
                    finish();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

        myThread.start();
    }

    @Override
    protected void onStop(){
        super.onStop();
     //   gyroAnimation.stop();
    }

    @Override
    protected void onPause()
    {
        super.onPause();

        unbindDrawables(findViewById(R.id.activity_main_splash));
        System.gc();
    }


    @Override
    protected void onDestroy()
    {
        super.onDestroy();

        unbindDrawables(findViewById(R.id.activity_main_splash));
        System.gc();
    }

    private void unbindDrawables(View view)
    {
        if (view.getBackground() != null)
        {
            view.getBackground().setCallback(null);
        }
        if (view instanceof ViewGroup && !(view instanceof AdapterView))
        {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++)
            {
                unbindDrawables(((ViewGroup) view).getChildAt(i));
            }
            ((ViewGroup) view).removeAllViews();
        }
    }
}
