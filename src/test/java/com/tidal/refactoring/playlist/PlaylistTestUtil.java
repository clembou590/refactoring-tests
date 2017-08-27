package com.tidal.refactoring.playlist;

import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.tidal.refactoring.playlist.data.PlayList;
import com.tidal.refactoring.playlist.data.PlayListTrack;
import com.tidal.refactoring.playlist.data.Track;

public class PlaylistTestUtil {

  private static Integer[] artistIdRange = { 0, 10 };
  private static Integer[] trackIdRanges = { 100, 200 }; //I decide that We only have 101 track in the DAO...

  private static float minTrackDuration = 0;
  private static float maxTrackDuration = 7.0f * 24.0f * 3600.0f; //one week is a LONG TRACK... this is max duration but we could change it...

  public PlayList generatePlaylist(String uuid, int numberOfTracks) {
    PlayList result = null;
    if (numberOfTracks >= 0) {
      final PlayList playlist = new PlayList(); 
      result = playlist; 
      
      playlist.setUuid(uuid);    
      List<PlayListTrack> listOfPlaylistTrack = IntStream.range(0, numberOfTracks).mapToObj((i) -> generateTrack()).map((track) -> generatePlayListTrackFromTrack(track, playlist)).collect(Collectors.toList());
      playlist.setDeleted(false);
      playlist.setDuration((float) listOfPlaylistTrack.stream().mapToDouble(track -> track.getTrack().getDuration()).sum());
      playlist.setPlayListName("nameOfPlayList" + playlist.getUuid());
      playlist.setPlayListTracks(listOfPlaylistTrack);
      //playlist.setLastUpdated(new Date()); NO need because already done in constructor
      //playlist.setRegisteredDate(new Date());  NO need because already done in constructor
      // playlist.setUuid(uuid);NO need because already done in constructor 
      
    }
      
    return result;
  }

  public PlayListTrack generatePlayListTrackFromTrack(Track track, PlayList playlist) {
    PlayListTrack playListTrack = new PlayListTrack();

    playListTrack.setDateAdded(new Date());
    playListTrack.setId(0);
    playListTrack.setPlaylist(playlist);
    playListTrack.setPlaylistUUID(playlist.getUuid());
    playListTrack.setTrack(track);
    playListTrack.setTrackId(track.getId());

    return playListTrack;
  }
  
  public List<Track> generateListOfTrack(int numberOfTrack){
    List<Track> result = null;
    if(numberOfTrack>=0) {
      result = IntStream.range(0, numberOfTrack).mapToObj((i) -> generateTrack()).collect(Collectors.toList());
    }
    return result;
  }

  private Track generateTrack() {
    Track track = new Track();

    track.setArtistId(getRandomIntegerInRange(artistIdRange[0], artistIdRange[1])); // set artistId (not really usefull because we do not use artistId...)
    track.setDuration(getRandomFloatInRange(minTrackDuration, maxTrackDuration));
    track.setId(getRandomIntegerInRange(trackIdRanges[0], trackIdRanges[1]));
    track.setTitle("title_" + track.getId());

    return track;
  }

  private static int getRandomIntegerInRange(int min, int max) {
    if (min >= max) {
      throw new IllegalArgumentException("max must be greater than min");
    }

    Random r = new Random();
    int result = r.ints(min, (max + 1)).limit(1).findFirst().getAsInt();

    if (result < min || result > max) {
      throw new AssertionError("getRandomIntegerInRange does not work as expected");
    }
    return result;
  }

  private static float getRandomFloatInRange(float min, float max) {
    if (min >= max) {
      throw new IllegalArgumentException("max must be greater than min");
    }

    Random rand = new Random();
    float result = rand.nextFloat() * (max - min) + min;

    if (result < min || result > max) {
      throw new AssertionError("getRandomFloatInRanges does not work as expected");
    }
    return result;

  }
}
