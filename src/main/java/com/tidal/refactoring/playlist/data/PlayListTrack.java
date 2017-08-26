package com.tidal.refactoring.playlist.data;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

public @Data class PlayListTrack implements Serializable {

    private static final long serialVersionUID = 5464240796158432162L;

    private Integer id;
    
    private String playlistUUID;
    private PlayList playlist;
 

    private Date dateAdded;
    
    private int trackId;
    private Track track;

    public PlayListTrack() {
        dateAdded = new Date();
    }






    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PlayListTrack that = (PlayListTrack) o; 
  			
  			if(playlistUUID !=null ? !playlistUUID.equals(that.playlistUUID): that.playlistUUID!=null) return false;
        if (trackId != that.trackId) return false;
        if (dateAdded != null ? !dateAdded.equals(that.dateAdded) : that.dateAdded != null) return false;
        return !(id != null ? !id.equals(that.id) : that.id != null);

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (dateAdded != null ? dateAdded.hashCode() : 0);
        result = 31 * result + trackId;
        result = 31 * result + ((playlistUUID == null) ? 0 : playlistUUID.hashCode());
        return result;
    }

    public String toString() {
        return "PlayListTrack id[" + getId() + "], trackId[" + getTrackId() + "]";
    }
}
