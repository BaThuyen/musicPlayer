package model_song;

public class Song {
	private long id;
	private String name;
	private String title;
	private String artist;
	private String album;

	public Song(long songID, String songName, String songTitle, String songArtist, String songAlbum) {
		id = songID;
		name = songName;
		title = songTitle;
		artist = songArtist;
		album = songAlbum;
	}

	public long getId() {
		return id;
	}

	public String getName() {
		return name;
	}
	
	public String getTitle() {
		return title;
	}

	public String getArtist() {
		return artist;
	}
	
	public String getAlbum()
	{
		return album;
	}
}
