package com.tidal.refactoring.playlist.data;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import lombok.Data;


/**
 * A very simplified version of TrackPlaylist
 */
public @Data class PlayList {

    private Integer id;
    private String playListName;
    private Set<PlayListTrack> playListTracks = new HashSet<PlayListTrack>();
    private Date registeredDate;
    private Date lastUpdated;
    private String uuid;
    private int nrOfTracks;
    private boolean deleted;
    private Float duration;

    public PlayList() {
        this.uuid = UUID.randomUUID().toString();
        Date d = new Date();
        this.registeredDate = d;
        this.lastUpdated = d;
        this.playListTracks = new HashSet<PlayListTrack>();
    }


}