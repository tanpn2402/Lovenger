package com.tanpn.messenger.message;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.tanpn.messenger.R;
import com.tanpn.messenger.utils.utils;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Random;

/**
 * A simple {@link Fragment} subclass.
 */
public class VoiceRecorder extends DialogFragment {


    public VoiceRecorder() {
        // Required empty public constructor
    }

    private TextView tvVoiceStatus;
    private ImageButton ibtPlay;
    private ImageButton ibtSent;
    private ImageView ibtVoice;
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        View dialogView = getActivity().getLayoutInflater().inflate(R.layout.fragment_voice_recorder, null);

        tvVoiceStatus = (TextView) dialogView.findViewById(R.id.tvVoiceStatus);
        ibtSent = (ImageButton) dialogView.findViewById(R.id.ibtSend);
        ibtSent.setVisibility(View.INVISIBLE);
        ibtSent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // gửi tin nhắn ghi âm
                send();
            }
        });

        ibtPlay = (ImageButton) dialogView.findViewById(R.id.ibtPlay);
        ibtPlay.setVisibility(View.INVISIBLE);
        ibtPlay.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        ibtPlay.setImageResource(R.drawable.ic_play_gray);
                        playVoice();

                        break;
                    case MotionEvent.ACTION_UP:
                        ibtPlay.setImageResource(R.drawable.ic_play_pink);
                        stopVoice();

                        break;
                    case MotionEvent.ACTION_HOVER_EXIT:
                        stopVoice();

                        break;
                }
                return true;
            }
        });
        ibtVoice = (ImageView) dialogView.findViewById(R.id.ibtVoice);


        ibtVoice.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                switch (motionEvent.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        ibtVoice.setImageResource(R.drawable.ic_record_gray);
                        startVoiceRecorder();
                        // set text
                        tvVoiceStatus.setText("00:00");

                        ibtSent.setVisibility(View.INVISIBLE);
                        ibtPlay.setVisibility(View.INVISIBLE);

                        break;
                    case MotionEvent.ACTION_UP:
                        ibtVoice.setImageResource(R.drawable.ic_record_pink);
                        stopVoiceRecorder();
                        // set text
                        tvVoiceStatus.setText("Hãy gửi");

                        ibtSent.setVisibility(View.VISIBLE);
                        ibtPlay.setVisibility(View.VISIBLE);

                        break;
                    case MotionEvent.ACTION_HOVER_EXIT:
                        stopVoiceRecorder();
                        // set text
                        tvVoiceStatus.setText("Hãy gửi");

                        ibtSent.setVisibility(View.VISIBLE);
                        ibtPlay.setVisibility(View.VISIBLE);

                        break;
                }
                return true;
            }
        });

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(dialogView);

        return builder.create();
    }
    private String AudioSavePathInDevice = null;
    private MediaRecorder mediaRecorder ;
    private String voiceName = null;

    private voiceTimer timer = new voiceTimer();



    private void MediaRecorderReady(){
        mediaRecorder=new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        mediaRecorder.setOutputFile(AudioSavePathInDevice);
    }

    private String generateVoiceName(){

        Calendar calendar = Calendar.getInstance();
        return "v-" + calendar.get(Calendar.DATE) + "" + calendar.get(Calendar.MONTH) + calendar.get(Calendar.YEAR) + "-" + calendar.get(Calendar.HOUR_OF_DAY) + "" + calendar.get(Calendar.MINUTE) + "" + calendar.get(Calendar.SECOND);
    }


    private void startVoiceRecorder(){
        voiceName = generateVoiceName() + ".3gp";
        AudioSavePathInDevice = utils.getFolderPath("voice") + voiceName;

        MediaRecorderReady();




        try {

            mediaRecorder.prepare();
            mediaRecorder.start();
            timer.start();
            errorWhileRecord = false;


        } catch (IllegalStateException e) {
            Log.i("LOI", "loi 1");
            errorWhileRecord = true;

            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
        catch (IOException e) {
            Log.i("LOI", "loi 2");
            errorWhileRecord = true;
            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }

    }
    private boolean errorWhileRecord = false;
    private void stopVoiceRecorder(){


        timer.stop();

        if(!errorWhileRecord)
            mediaRecorder.stop();

    }

    private MediaPlayer mediaPlayer;

    private void playVoice(){
        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(AudioSavePathInDevice);
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }

        mediaPlayer.start();
    }

    private void stopVoice(){
        if(mediaPlayer != null){
            mediaPlayer.stop();
            mediaPlayer.release();
            //MediaRecorderReady();
        }
    }

    private void send(){
        Intent i = new Intent();
        i.putExtra("data", AudioSavePathInDevice);
        getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, i);
        /*
        * see at: http://stackoverflow.com/questions/10905312/receive-result-from-dialogfragment
        * */
        this.dismiss();
    }



    private class voiceTimer extends AsyncTask<Void, Integer, Void>{

        private int i = 0;
        private boolean voiceStarted = false;
        boolean isStart = false;

        public void start(){
            i = 0;
            voiceStarted = true;
            if(!isStart){
                this.execute();
                isStart = true;
            }
        }

        public void stop(){
            //this.cancel(true);
            i = 0;
            voiceStarted = false;

        }

        @Override
        protected Void doInBackground(Void... voids) {

            while(isStart){ // vong while luon chay background
                if(voiceStarted){
                    publishProgress(i++);
                    SystemClock.sleep(1000); // sleep 1s
                }



            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);

            int value = values[0];
            int m = value / 60;
            int s = value % 60;

            String ms = m < 10 ? "0" + m : m +"";
            String ss = s < 10 ? "0" + s : s + "";
            tvVoiceStatus.setText(ms +":" +ss);
        }

    }


}
