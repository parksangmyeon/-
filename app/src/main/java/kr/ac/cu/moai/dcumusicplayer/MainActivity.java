package kr.ac.cu.moai.dcumusicplayer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ArrayList<Music> musicList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView listView = findViewById(R.id.listView);

        musicList = new ArrayList<>();
        musicList.add(new Music("After You", "unknown", R.raw.afteryou));
        musicList.add(new Music("Far Apart", "unknown", R.raw.farapart));
        musicList.add(new Music("Tin Spirit", "unknown", R.raw.tinspirit));
        musicList.add(new Music("Unavailable", "unknown", R.raw.unavailable));
        musicList.add(new Music("Valley of Spies", "unknown", R.raw.valleyofspies));
        musicList.add(new Music("Luxury", "unknown", R.raw.luxery));

        ListViewMP3Adapter adapter = new ListViewMP3Adapter(this, musicList);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Music selectedMusic = musicList.get(position);
                Intent intent = new Intent(MainActivity.this, PlayerActivity.class);
                intent.putExtra("music", selectedMusic);
                startActivity(intent);
            }
        });
    }
}
