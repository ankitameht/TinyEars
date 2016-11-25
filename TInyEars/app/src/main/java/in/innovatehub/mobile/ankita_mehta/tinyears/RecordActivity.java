package in.innovatehub.mobile.ankita_mehta.tinyears;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.io.FileUtils;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class RecordActivity extends AppCompatActivity {
    private static final String LOG_TAG = "AudioRecordTest";

    private static String msg = "Oops, We have no results, Try recording again!";

    public final static String Result_MESSAGE = "in.innovatehub.ankita_mehta.tinyears.ResultMESSAGE";

    private static final int REQUESTCODE_RECORDING = 109201;
    private Button mRecorderApp = null;

    private static String mFileName = "music.mp3";
    private static String mFilePath = String.valueOf(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS + "/TinyEars/"));

    private MediaRecorder mRecorder = null;
    private MediaPlayer mPlayer = null;

    private ImageButton mRecordImageButton = null;
    private ImageButton mPlayImageButton = null;

    boolean mStartRecording = true;
    boolean mStartPlaying = true;

    private Button mShowStatsButton = null;

    private TextView mStopCountTimer = null;

   // private static final String TAG = "RecordActivity";
    Thread thread = null;

    CountDownTimer t = new CountDownTimer( Long.MAX_VALUE , 1000) {
            Integer cnt = -1;
            @Override
            public void onTick(long millisUntilFinished) {
                Log.d(LOG_TAG,"Inside CountDownTimer onTick");
                cnt++;
                long millis = cnt;
                int seconds = (int) (millis / 60);
                int minutes = seconds / 60;
                seconds     = seconds % 60;
                mStopCountTimer.setText(String.format("%d:%02d:%02d", minutes, seconds,millis));
            }

            @Override
            public void onFinish() {
                Log.d(LOG_TAG,"Inside CountDownTimer finish");
                cnt = 0;
                long millis = cnt;
                int seconds = (int) (millis / 60);
                int minutes = seconds / 60;
                seconds     = seconds % 60;
                //mStopCountTimer.setText(String.format("%d:%02d:%02d", minutes, seconds,millis));
                }
        };



    private Handler handler = new Handler();
    final Runnable updater = new Runnable() {
        public void run() {
            handler.postDelayed(this, 1);
            if (mRecorder != null) {
                int maxAmplitude = mRecorder.getMaxAmplitude();
                if (maxAmplitude != 0) {
                    // visualizerView.addAmplitude(maxAmplitude);
                }
            } else {

            }
        }
    };

    private void onRecord(boolean start) {
        if (start) {
            startRecording();
        } else {
            stopRecording();
        }
    }

    private void onPlay(boolean start) {
        if (start) {
            startPlaying();
        } else {
            stopPlaying();
        }
    }

    private void startPlaying() {
        mPlayer = new MediaPlayer();
        try {
            mPlayer.setDataSource(mFilePath + "/" + mFileName);
            mPlayer.prepare();
            mPlayer.start();
            mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                public void onCompletion(MediaPlayer mp) {
                    Log.i("Completion Listener", "Song Complete");
                    stopPlaying();
                    mRecordImageButton.setEnabled(true);
                }
            });

        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }
    }

    private void stopPlaying() {
        if (mPlayer != null) {
            mPlayer.reset();
            mPlayer.release();
            mPlayer = null;
            mPlayImageButton.setImageResource(R.drawable.playicon);
            //  mStartPlaying = true;
        } else {
            mPlayImageButton.setImageResource(R.drawable.pauseicon);
            //   mStartPlaying = false;
        }
    }

    private void startRecording() {
        AudioRecordTest(String.valueOf(System.currentTimeMillis()));
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mRecorder.setOutputFile(mFilePath + "/" + mFileName);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        try {
            mRecorder.prepare();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }
        try {
            mRecorder.start();
            Toast.makeText(getApplicationContext(), "Recording started", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Log.e(LOG_TAG, "start() failed");
        }
    }

    private void stopRecording() {
        if (mRecorder != null) {
            mRecorder.stop();
            mRecorder.release();
            Toast.makeText(getApplicationContext(), "Audio recorded successfully", Toast.LENGTH_LONG).show();
            mRecorder = null;
            mRecordImageButton.setImageResource(R.drawable.micicon);
            // mStartRecording = true;
        } else {
            mRecordImageButton.setImageResource(R.drawable.stopicon);
            // mStartRecording = false;
        }
    }

    public void AudioRecordTest(String text) {
        boolean exists = (new File(mFilePath + "/" + mFileName)).exists();
        if (!exists) {
            new File(mFileName).mkdirs();
        }
        //  mFileName += "audiorecordtest.mp3";
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);

        //  getActionBar().setDisplayHomeAsUpEnabled(true);

        Log.d(LOG_TAG, "HERE IS FILE PATH" + mFilePath + "/" + mFileName);

        mRecordImageButton = (ImageButton) findViewById(R.id.imageButton2);
        mPlayImageButton = (ImageButton) findViewById(R.id.imageButton3);
        mShowStatsButton = (Button) findViewById(R.id.showMeStats);
        mRecorderApp = (Button) findViewById(R.id.recorderApp);
        mStopCountTimer = (TextView) findViewById(R.id.stopCountTimer);


        AudioRecordTest("00000");

        mRecordImageButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                onRecord(mStartRecording);
                if (mStartRecording) {
                    t.start();
                    mRecordImageButton.setImageResource(R.drawable.stopicon);
                    mPlayImageButton.setEnabled(false);
                    //setText("Stop recording");
                } else {
                    t.cancel();
                    t.onFinish();
                    mRecordImageButton.setImageResource(R.drawable.micicon);
                    mPlayImageButton.setEnabled(true);
                    mShowStatsButton.setEnabled(true);
                    mShowStatsButton.setVisibility(View.VISIBLE);
                    Toast.makeText(getApplicationContext(), "Hold on... we are getting the results!", Toast.LENGTH_SHORT).show();
                    pressedSavBtn();
                    Toast.makeText(getApplicationContext(), "Parsing done ... now you may see the results!", Toast.LENGTH_SHORT).show();
                    //setText("Start recording");
                }
                mStartRecording = !mStartRecording;
            }
        });
        mPlayImageButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                onPlay(mStartPlaying);
                if (mStartPlaying) {
                    mPlayImageButton.setImageResource(R.drawable.pauseicon);
                    mRecordImageButton.setEnabled(false);
                    mShowStatsButton.setEnabled(false);
                    //setText("Stop playing");
                } else {
                    mPlayImageButton.setImageResource(R.drawable.playicon);
                    mRecordImageButton.setEnabled(true);
                    mShowStatsButton.setEnabled(false);
                    //setText("Start playing");
                }
                mStartPlaying = !mStartPlaying;
            }
        });
        //Calling recorder ...
        mRecorderApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION);
                if (isAvailable(getApplicationContext(), intent)) {
                    startActivityForResult(intent, REQUESTCODE_RECORDING);
                }
            }
        });
        mShowStatsButton = (Button) findViewById(R.id.showMeStats);
        mShowStatsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendResults(msg);
            }
        });

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

    public void pressedSavBtn() {
        try {
            if (thread == null || !thread.isAlive()) {
                startThread();
                thread.start();
            } else {
                startThread();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            mShowStatsButton.setVisibility(View.VISIBLE);
            Thread.currentThread().interrupt();
            thread = null;
        }
    }

    public void writeToFile(String data) {
        // Get the directory for the user's public pictures directory.
        final File path = new File(mFilePath + "/");
        // Make sure the path directory exists.
        if (!path.exists()) {
            // Make it, if it doesn't exit
            path.mkdirs();
        }
        final File file = new File(path, "config.txt");
        // Save your stream, don't forget to flush() it before closing it.
        try {
            file.createNewFile();
            FileOutputStream fOut = new FileOutputStream(file);
            OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
            myOutWriter.append(data);

            myOutWriter.close();

            fOut.flush();
            fOut.close();
        } catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    private static String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append((line + "\n"));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

    void startThread() {
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    MediaPlayer mp = MediaPlayer.create(getApplicationContext(), Uri.parse((String) mFilePath + "/" + mFileName));
                    int duration = mp.getDuration();

                    Log.d("~~~~~~~~ DURATION: ", String.valueOf(duration));

                    //THIS IS FILE ENCODING CODE
                    File file = new File(mFilePath + "/" + mFileName);
                    byte[] bytes = FileUtils.readFileToByteArray(file);

                    String encoded = Base64.encodeToString(bytes, 0);
                    Log.d("~~~~~~~~ Encoded: ", encoded);
                    writeToFile(encoded);

                    //THIS IS URL CONN CODE
                    String link = "http://192.168.50.0:9000/divide_result2";
                    URL url = new URL(link);
                    HttpClient httpclient = new DefaultHttpClient();
                    HttpPost httppost = new HttpPost(link);
                    try {
                        // Add your data
                        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
                        nameValuePairs.add(new BasicNameValuePair("Name", "StackOverFlow"));
                        nameValuePairs.add(new BasicNameValuePair("Date", encoded));
                        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                        // Execute HTTP Post Request
                        HttpResponse response = httpclient.execute(httppost);
                        String sb = convertStreamToString(response.getEntity().getContent());
                        Log.d(LOG_TAG, "MESSAGE NOW" + sb);
                        Log.d(LOG_TAG, sb);
                        msg = sb.toString();

                    } catch (ClientProtocolException e) {
                        // TODO Auto-generated catch block
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    Log.d(LOG_TAG, "finished response");
                }
            }
        });
    }

    public void sendResults(String res) {
        Log.d(LOG_TAG, "Inside on create, Navigating to Result Screen Activity!");
        Intent intent = new Intent(getApplicationContext(), ResultsScreenActivity.class);
        intent.putExtra(Result_MESSAGE, res);
        startActivity(intent);
    }

    public static boolean isAvailable(Context ctx, Intent intent) {
        final PackageManager mgr = ctx.getPackageManager();
        List<ResolveInfo> list = mgr.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == REQUESTCODE_RECORDING) {
            if (resultCode == RESULT_OK) {
                Uri audioUri = intent.getData();
                // make use of this MediaStore uri
                // e.g. store it somewhere
            } else {
                // react meaningful to problems
            }
        } else {
            super.onActivityResult(requestCode,
                    resultCode, intent);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mRecorder != null) {
            mRecorder.release();
            mRecorder = null;
        }
        if (mPlayer != null) {
            mPlayer.release();
            mPlayer = null;
        }
        thread.stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(updater);
        if (mRecorder != null) {
            mRecorder.stop();
            mRecorder.reset();
            mRecorder.release();
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        handler.post(updater);
    }
}
/*
public class yourAsyncTask extends AsyncTask<Integer, Integer, Void>{
        ProgressDialog pDialog;

        @Override
        protected Void doInBackground(Integer... integers) {
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(Context);
            pDialog.setMessage("Charging...");
            pDialog.setCancelable(true);
            pDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {

                @Override
                public void onCancel(DialogInterface dialog) {
                    errorMesaje = "Process cancelled";
                    cancel(true);
                }
            });
            pDialog.show();
        }

        @Override
        protected void onPostExecute (Void voidp){
            super.onPostExecute(voidp);
            //Use an interface to call your activity and pass the data, you will have to change the attribute of the method in order to do it.
            pDialog.dismiss();
        }

}*/