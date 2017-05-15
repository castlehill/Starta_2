package com.chs.starta;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.speech.tts.TextToSpeech;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Locale;

public class MainActivity extends AppCompatActivity implements TextToSpeech.OnInitListener{

    private Button b;
    private Button bStop;
    private Context c;
    private TextView textview;
    private TextToSpeech  engine=null;
    boolean bVoiceEnabled=true;
    private Vibrator myVib;

//NumberPicker np =null;
  //  NumberPicker numberPicker = null;
    com.shawnlin.numberpicker.NumberPicker numberPicker=null;
    CountDownTimer countdowntimer;

@Override
    protected void onResume()
{

    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
    bVoiceEnabled = sharedPreferences.getBoolean("voice_enabled", true);

    super.onResume();

}
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        bVoiceEnabled = sharedPreferences.getBoolean("voice_enabled", true);
        myVib = (Vibrator) this.getSystemService(VIBRATOR_SERVICE);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        c = getApplicationContext();
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });





        numberPicker = (com.shawnlin.numberpicker.NumberPicker)  findViewById(R.id.number_picker);
// set selected text color
        numberPicker.setSelectedTextColor(ContextCompat.getColor(this, R.color.colorPrimary));
        numberPicker.setSelectedTextColorResource(R.color.colorPrimary);

        numberPicker.setMinValue(0);
        numberPicker.setMaxValue(20);
        numberPicker.setWrapSelectorWheel(true);

        engine = new TextToSpeech(this, this);
        engine.setLanguage(Locale.UK);

        textview = (TextView) findViewById(R.id.tvUpdate);
        b = (Button) findViewById(R.id.mybutton);

        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bStop.setAlpha(1f);
                bStop.setClickable(true);

                if (countdowntimer!=null) {
                    countdowntimer.cancel();
                }
                myVib.vibrate(50);

                int iMills = 10000;
                //iMills = Integer.parseInt( textview.getText().toString()) * 1000;

                int iSelected = numberPicker.getValue();
                //convert to minutes
                    iMills=  iSelected  * 60 * 1000;

                countdowntimer = new CountDownTimerClass(iMills , 1000);

                if (bVoiceEnabled)
                    engine.speak("Countdown set for " +  (iSelected ) + " minutes" , TextToSpeech.QUEUE_FLUSH,null,null);

                countdowntimer.start();


            }
        });


        bStop = (Button) findViewById(R.id.mybutton_stop);
        bStop.setAlpha(.5f);
        bStop.setClickable(false);
        bStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //cancel the timer
                if (countdowntimer !=null) {
                    countdowntimer.cancel();
                }
                if (bVoiceEnabled)
                    engine.speak("Countdown cancelled!" , TextToSpeech.QUEUE_FLUSH,null,null);


                bStop.setAlpha(1);
                bStop.setClickable(false);
            }
        });



    }


    private void startRecording() {
        //call out to app
        if (bVoiceEnabled) {
            if (engine != null) {
                engine.speak("Recording started!", TextToSpeech.QUEUE_FLUSH, null, null);
            }
        }
        Intent intent = new Intent(Intent.ACTION_RUN);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        intent.setData(Uri.parse("http://strava.com/nfc/record"));
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent settingsActivity = new Intent( getApplicationContext(), SettingsActivity.class);
            startActivity(settingsActivity);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onInit(int status) {


    }

    private void checkForAnnouncement(int progress)
    {

        //Minute Check
        if ((progress % 60) == 0) {
            int iMinutes = 0;
            String sMin = "minutes";

            iMinutes   = progress /60;
            if (iMinutes  ==1)
                sMin="minute";

            if ((engine!=null) && (bVoiceEnabled)) {
                engine.speak("Starting in " +  iMinutes   + " " + sMin, TextToSpeech.QUEUE_FLUSH,null,null);
            }
        }
        //30 seconds left Check
        if ((progress==30) && (bVoiceEnabled)) {
            if (engine!=null) {
                engine.speak("Thirty seconds until we start", TextToSpeech.QUEUE_FLUSH,null,null);
            }
        }
        //10 seconds left Check
        if ((progress==10) && (bVoiceEnabled)){
            if (engine!=null) {
                engine.speak("Ten seconds!", TextToSpeech.QUEUE_FLUSH,null,null);
            }
        }


        if (( (progress==3) || (progress==2)  || (progress==1)) && (bVoiceEnabled)) {
            if (engine!=null) {
                engine.speak(progress + "", TextToSpeech.QUEUE_FLUSH,null,null);
            }
        }

    }

    public class CountDownTimerClass extends CountDownTimer {

        public CountDownTimerClass(long millisInFuture, long countDownInterval) {

            super(millisInFuture, countDownInterval);

        }

        @Override
        public void onTick(long millisUntilFinished) {

            int progress = (int) (millisUntilFinished / 1000);
            checkForAnnouncement(progress);
            //textview.setText(Integer.toString(progress));
            textview.setText("Counting down..");
            if ( (progress % 2) ==0) {
                textview.setText("");
            }

        }

        @Override
        public void onFinish() {
            startRecording();
           // textview.setText(" Count Down Finish ");

        }
    }
}