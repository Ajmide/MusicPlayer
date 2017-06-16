package cn.edu.bjtu.android.mymusicplayer.data;

/**
 * Created by Administrator on 2017/6/13 0013.
 */

public class Song {
    private long id;
    private String title;
    private String artist;
    private String data;
    private String album;

    public Song(long id, String title, String artist, String data, String album) {
        this.id = id;
        this.title = title;
        this.artist = artist;
        this.data = data;
        this.album = album;
    }

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }

    public String getData() {
        return data;
    }

    public String getAlbum() {
        return album;
    }

    //

    @Override
    public String toString() {
        return "Song{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", artist='" + artist + '\'' +
                ", data='" + data + '\'' +
                ", album='" + album + '\'' +
                '}';
    }
}
