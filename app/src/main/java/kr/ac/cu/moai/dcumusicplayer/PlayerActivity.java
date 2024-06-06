package kr.ac.cu.moai.dcumusicplayer;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class PlayerActivity extends AppCompatActivity {

    private MediaPlayer mediaPlayer;
    private AudioManager audioManager;
    private ImageButton buttonPlayPause;
    private ImageButton buttonBack;
    private TextView tvTitle;
    private TextView tvArtist;
    private TextView tvDuration;
    private SeekBar seekBar;
    private Handler handler;
    private Runnable updateSeekBar;
    private static final String TAG = "PlayerActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        // Toolbar 설정
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);  // 기본 제목을 비활성화

        // 뒤로 가기 버튼 설정
        buttonBack = findViewById(R.id.buttonBack);
        buttonBack.setOnClickListener(view -> {
            if (mediaPlayer != null) {
                mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer = null;  // Ensure mediaPlayer is set to null
            }
            Intent intent = new Intent(PlayerActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        });

        // View 요소 초기화
        buttonPlayPause = findViewById(R.id.buttonPlayPause);
        tvTitle = findViewById(R.id.tvTitle);
        tvArtist = findViewById(R.id.tvArtist);
        tvDuration = findViewById(R.id.tvDuration);
        seekBar = findViewById(R.id.seekBar);

        // Intent에서 Music 객체 가져오기
        Intent intent = getIntent();
        Music selectedMusic = (Music) intent.getSerializableExtra("music");

        if (selectedMusic == null) {
            throw new NullPointerException("Music object is null");
        }

        // 음악 정보 설정
        tvTitle.setText(selectedMusic.getTitle());
        tvArtist.setText(selectedMusic.getArtist());

        // MediaPlayer 초기화
        mediaPlayer = MediaPlayer.create(this, selectedMusic.getResourceId());
        if (mediaPlayer == null) {
            Log.e(TAG, "MediaPlayer creation failed.");
            return;
        }
        mediaPlayer.setOnCompletionListener(mp -> buttonPlayPause.setImageResource(R.drawable.ic_play_arrow));

        // 재생/일시정지 버튼 클릭 리스너
        buttonPlayPause.setOnClickListener(v -> {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                buttonPlayPause.setImageResource(R.drawable.ic_play_arrow);
            } else {
                mediaPlayer.start();
                buttonPlayPause.setImageResource(R.drawable.ic_pause);
                handler.post(updateSeekBar); // 음악 재생을 시작할 때 SeekBar 업데이트 시작
            }
        });

        // SeekBar 설정
        seekBar.setMax(mediaPlayer.getDuration());

        handler = new Handler();
        updateSeekBar = new Runnable() {
            @Override
            public void run() {
                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    try {
                        int currentPosition = mediaPlayer.getCurrentPosition();
                        seekBar.setProgress(currentPosition);
                        tvDuration.setText(formatDuration(currentPosition));
                        handler.postDelayed(this, 1000); // 1초마다 업데이트
                    } catch (IllegalStateException e) {
                        Log.e(TAG, "MediaPlayer is in illegal state", e);
                    }
                }
            }
        };

        // 처음 업데이트 시작
        handler.post(updateSeekBar);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser && mediaPlayer != null) {
                    mediaPlayer.seekTo(progress);
                    tvDuration.setText(formatDuration(progress));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        // AudioManager 초기화 및 오디오 포커스 요청
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        requestAudioFocus();
    }

    private void requestAudioFocus() {
        AudioManager.OnAudioFocusChangeListener focusChangeListener = focusChange -> {
            switch (focusChange) {
                case AudioManager.AUDIOFOCUS_LOSS:
                    if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                        mediaPlayer.pause();
                        buttonPlayPause.setImageResource(R.drawable.ic_play_arrow);
                    }
                    break;
                case AudioManager.AUDIOFOCUS_GAIN:
                    // Audio focus gained
                    break;
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                    if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                        mediaPlayer.pause();
                        buttonPlayPause.setImageResource(R.drawable.ic_play_arrow);
                    }
                    break;
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                    if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                        mediaPlayer.setVolume(0.1f, 0.1f);
                    }
                    break;
            }
        };

        int result = audioManager.requestAudioFocus(focusChangeListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        if (result != AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            Log.e(TAG, "Audio focus request failed");
        }
    }

    private String formatDuration(int duration) {
        int minutes = (duration / 1000) / 60;
        int seconds = (duration / 1000) % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        handler.removeCallbacks(updateSeekBar);
    }
}
