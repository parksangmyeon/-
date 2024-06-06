package kr.ac.cu.moai.dcumusicplayer;

import android.net.Uri;  // Uri 클래스를 import
import java.io.Serializable;

public class Music implements Serializable {
    private String title;
    private String artist;
    private int resourceId;

    public Music(String title, String artist, int resourceId) {
        this.title = title;
        this.artist = artist;
        this.resourceId = resourceId;
    }

    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }

    public int getResourceId() {
        return resourceId;
    }

    public Uri getUri() {
        return Uri.parse("android.resource://" + R.class.getPackage().getName() + "/" + resourceId);
    }
}
