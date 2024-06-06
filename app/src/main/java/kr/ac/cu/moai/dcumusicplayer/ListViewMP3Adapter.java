package kr.ac.cu.moai.dcumusicplayer;

import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class ListViewMP3Adapter extends ArrayAdapter<Music> {

    private final Context context;
    private final List<Music> musicList;

    public ListViewMP3Adapter(Context context, List<Music> musicList) {
        super(context, 0, musicList);
        this.context = context;
        this.musicList = musicList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_music, parent, false);
        }

        Music music = getItem(position);

        TextView textViewTitle = convertView.findViewById(R.id.textViewTitle);
        TextView textViewArtist = convertView.findViewById(R.id.textViewArtist);
        TextView textViewDuration = convertView.findViewById(R.id.textViewDuration);
        ImageView musicIcon = convertView.findViewById(R.id.musicIcon);

        if (music != null) {
            textViewTitle.setText(music.getTitle());
            textViewArtist.setText(music.getArtist());

            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource(context, music.getUri());
            String duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            textViewDuration.setText(formatDuration(Long.parseLong(duration)));

            // 음악 아이콘 설정
            musicIcon.setImageResource(R.drawable.ic_music_icon);  // 아이콘을 ic_music_icon으로 변경
        }

        return convertView;
    }

    private String formatDuration(long duration) {
        long minutes = (duration / 1000) / 60;
        long seconds = (duration / 1000) % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }
}
