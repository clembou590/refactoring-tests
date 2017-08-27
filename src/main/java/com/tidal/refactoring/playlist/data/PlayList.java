package com.tidal.refactoring.playlist.data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import lombok.Data;


/**
 * A very simplified version of TrackPlaylist
 */
public @Data class PlayList {

   
    private String playListName;
    private List<PlayListTrack> playListTracks = new ArrayList<>();
    private Date registeredDate;
    private Date lastUpdated;
    private String uuid;
  
    private boolean deleted;
    private Float duration;

    public PlayList() {
        this.uuid = UUID.randomUUID().toString();
        Date d = new Date();
        this.registeredDate = d;
        this.lastUpdated = d;
    }

    
    public int getNrOfTracks(){
    	return this.playListTracks.size();
    }

}