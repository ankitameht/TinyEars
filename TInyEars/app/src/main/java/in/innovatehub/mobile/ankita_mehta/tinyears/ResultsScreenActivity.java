package in.innovatehub.mobile.ankita_mehta.tinyears;

import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class ResultsScreenActivity extends AppCompatActivity {

    static final String TAG = "ResultsScreenActivity";

    static final String STATE_AC = "STATE_AC";
    static final String STATE_FAN = "STATE_FAN";
    static final String STATE_MW = "STATE_MW";
    static final String STATE_HMN = "STATE_HMN";


    Boolean AC = false;
    Boolean FAN = false;
    Boolean MW = false;
    Boolean HMN = false;

    private TextView mResultText;
    private ImageView mAC;
    private ImageView mFan;
    private ImageView mMW;
    private ImageView mHmn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results_screen);
        //getActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        String message = intent.getStringExtra(RecordActivity.Result_MESSAGE);
        mResultText = (TextView) findViewById(R.id.ResultsMessage);
        mResultText.setText("Devices on .."+message);

        Log.d(TAG, message);

        mAC = (ImageView) findViewById(R.id.imageViewACRS);
        mFan = (ImageView) findViewById(R.id.imageViewFanRS);
        mMW = (ImageView) findViewById(R.id.imageViewMWRS);
        mHmn = (ImageView) findViewById(R.id.imageViewHumanRS);

      //  FAN = true;

        // Check whether we're recreating a previously destroyed instance
        if (savedInstanceState != null) {
            // Restore value of members from saved state
            AC = savedInstanceState.getBoolean(STATE_AC);
            FAN = savedInstanceState.getBoolean(STATE_FAN);
            MW = savedInstanceState.getBoolean(STATE_MW);
            HMN = savedInstanceState.getBoolean(STATE_HMN);
        } else {
            // Probably initialize members with default values for a new instance
            if(message.contains("AC")){
                AC = true;
            }
            if(message.contains("FAN")){
                FAN = true;
            }
            if(message.contains("MW")){
                MW = true;
            }
            if(message.contains("Human")){
                HMN = true;
            }
        }

        if(!(AC)&&!(MW)&&!(FAN)&&!(HMN)){
            mResultText.setText(message);
        }
        if(AC){
            mAC.setVisibility(View.VISIBLE);
        }
        if(FAN){
            mFan.setVisibility(View.VISIBLE);
        }
        if(MW){
            mMW.setVisibility(View.VISIBLE);
        }
        if(HMN){
            mHmn.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save the user's current game state
        savedInstanceState.putBoolean(STATE_AC, AC);
        savedInstanceState.putBoolean(STATE_FAN, FAN);
        savedInstanceState.putBoolean(STATE_MW, MW);
        savedInstanceState.putBoolean(STATE_HMN, HMN);

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        // Always call the superclass so it can restore the view hierarchy
        super.onRestoreInstanceState(savedInstanceState);

        AC = savedInstanceState.getBoolean(STATE_AC);
        FAN = savedInstanceState.getBoolean(STATE_FAN);
        MW = savedInstanceState.getBoolean(STATE_MW);
        HMN = savedInstanceState.getBoolean(STATE_HMN);
    }
}
