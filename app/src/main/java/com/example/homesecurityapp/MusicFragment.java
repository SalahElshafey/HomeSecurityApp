package com.example.homesecurityapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import java.io.IOException;
import android.media.MediaPlayer;

public class MusicFragment extends Fragment {

    private static final int PICK_AUDIO_REQUEST = 1;
    private TextView selectedMusicText;
    private MediaPlayer mediaPlayer;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_music, container, false);

        Button chooseMusicButton = view.findViewById(R.id.choose_music_button);
        Button playMusicButton = view.findViewById(R.id.play_music_button);
        Button stopMusicButton = view.findViewById(R.id.stop_music_button);
        selectedMusicText = view.findViewById(R.id.selected_music_text);

        mediaPlayer = new MediaPlayer();

        chooseMusicButton.setOnClickListener(v -> openAudioFilePicker());
        playMusicButton.setOnClickListener(v -> playMusic());
        stopMusicButton.setOnClickListener(v -> stopMusic());

        return view;
    }

    private void openAudioFilePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("audio/*");
        startActivityForResult(Intent.createChooser(intent, "Select MP3 File"), PICK_AUDIO_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_AUDIO_REQUEST && resultCode == getActivity().RESULT_OK && data != null) {
            Uri audioUri = data.getData();
            selectedMusicText.setText(audioUri.getLastPathSegment());

            try {
                mediaPlayer.reset();
                mediaPlayer.setDataSource(getContext(), audioUri);
                mediaPlayer.prepare();
                Toast.makeText(getContext(), "Music Selected", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(getContext(), "Error loading music", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void playMusic() {
        if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
            mediaPlayer.start();
            Toast.makeText(getContext(), "Playing Music", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), "No Music Selected", Toast.LENGTH_SHORT).show();
        }
    }

    private void stopMusic() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            try {
                mediaPlayer.prepare(); // Prepare the player again for playback
            } catch (IOException e) {
                e.printStackTrace();
            }
            Toast.makeText(getContext(), "Music Stopped", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
