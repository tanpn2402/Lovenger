package com.tanpn.messenger.message;


import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.SeekBar;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.tanpn.messenger.R;

import java.io.IOException;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class VoicePlayer extends DialogFragment implements MediaPlayer.OnCompletionListener,  MediaPlayer.OnBufferingUpdateListener {


    public VoicePlayer() {
        // Required empty public constructor
    }

    private String voicePath;
    private String voiceId;

    public void setVoicePath(String path){
        voicePath = path;
    }

    public void setVoiceDetail(Map<String, String> voice){
        for(Map.Entry<String, String> m : voice.entrySet()){
            voiceId = m.getKey();
            voicePath = m.getValue();
        }
    }

    private boolean isPlaying = false;
    private SeekBar progressBar;
    private MediaPlayer mediaPlayer;

    private int voiceTimeLong;
    private Handler handler = new Handler();
    private updateSeekbar updateSeekbar = new updateSeekbar();

    private ImageButton ibtPlay, ibtDownload;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        View dialogView = getActivity().getLayoutInflater().inflate(R.layout.fragment_voice_player, null);
        progressBar = (SeekBar) dialogView.findViewById(R.id.progressBar);

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.setOnBufferingUpdateListener(this);


        ibtPlay = (ImageButton) dialogView.findViewById(R.id.ibtPlay);
        ibtPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    mediaPlayer.setDataSource(voicePath);
                    mediaPlayer.prepare();

                } catch (IOException e) {
                    e.printStackTrace();
                }

                voiceTimeLong = mediaPlayer.getDuration();

                if(!mediaPlayer.isPlaying()){
                    mediaPlayer.start();
                    ibtPlay.setImageResource(R.drawable.ic_stop_pink);
                }
                else{
                    mediaPlayer.stop();
                    mediaPlayer.reset();
                    ibtPlay.setImageResource(R.drawable.ic_play_pink);
                }

                //updateProgress();
                handler.post(updateSeekbar);
            }
        });

        final ImageButton ibtStop = (ImageButton) dialogView.findViewById(R.id.ibtStop);
        //ibtStop.setVisibility(View.INVISIBLE);
        ibtStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isPlaying)
                    mediaPlayer.stop();
            }
        });

        ibtDownload = (ImageButton) dialogView.findViewById(R.id.ibtDownload);
        ibtDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                downloadFile();
            }
        });


        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(dialogView);


        return builder.create();
    }
    private void downloadFile(){
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference httpsReference = storage.getReferenceFromUrl(voicePath);

        //httpsReference.getFile()

    }



    private class updateSeekbar implements Runnable{


        @Override
        public void run() {
            progressBar.setProgress((int)(((float)mediaPlayer.getCurrentPosition()/voiceTimeLong)*100)); // This math construction give a percentage of "was playing"/"song length"

            if(mediaPlayer.isPlaying())
                handler.postDelayed(this, 1000);
        }
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mediaPlayer, int i) {
        progressBar.setSecondaryProgress(i);

    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        progressBar.setProgress(0);
        progressBar.setSecondaryProgress(0);

        //mediaPlayer.reset();


        ibtPlay.setImageResource(R.drawable.ic_play_pink);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(mediaPlayer!=null) {
            try {
                mediaPlayer.release();
                handler.removeCallbacks(updateSeekbar);
            }
            catch(Exception e) {
                e.printStackTrace();
            }
        }
    }
}
