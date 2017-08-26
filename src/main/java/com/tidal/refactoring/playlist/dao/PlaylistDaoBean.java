package com.tidal.refactoring.playlist.dao;

import com.tidal.refactoring.playlist.data.PlayList;
import com.tidal.refactoring.playlist.data.PlayListTrack;
import com.tidal.refactoring.playlist.data.Track;

import java.util.*;

/**
 * Class faking the data layer, and returning fake playlists
 */
public class PlaylistDaoBean {

    private final Map<String, PlayList> playlists = new HashMap<String, PlayList>();

    public PlayList getPlaylistByUUID(String uuid) {

        PlayList playList = playlists.get(uuid);

        if (playList != null) {
            return playList;
        }

        //return default playlist
        return createPlayList(uuid);
    }

    private PlayList createPlayList(String uuid) {
        PlayList trackPlayList = new PlayList();

        trackPlayList.setDeleted(false);
        trackPlayList.setDuration((float) (60 * 60 * 2));
        trackPlayList.setLastUpdated(new Date());
        trackPlayList.setNrOfTracks(376);
        trackPlayList.setPlayListName("Collection of great songs");
        trackPlayList.setPlayListTracks(getPlaylistTracks());
        trackPlayList.setUuid(uuid);

        return trackPlayList;
    }

    private static List<PlayListTrack> getPlaylistTracks() {

        List<PlayListTrack> playListTracks = new ArrayList<>();
        for (int i = 0; i < 376; i++) {
            PlayListTrack playListTrack = new PlayListTrack();
            playListTrack.setDateAdded(new Date());
            playListTrack.setId(i + 1);
            playListTrack.setTrack(getTrack());

        }

        return playListTracks;
    }

    public static Track getTrack() {
        Random randomGenerator = new Random();

        Track track = new Track();
        track.setArtistId(randomGenerator.nextInt(10000));
        track.setDuration(60 * 3);

        int trackNumber = randomGenerator.nextInt(15);
        track.setTitle("Track no: " + trackNumber);

        return track;
    }
}
