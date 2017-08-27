package com.tidal.refactoring.playlist.dao;

import com.tidal.refactoring.playlist.data.PlayList;

/**
 * Class faking the data layer, and returning fake playlists
 */
public class PlaylistDaoBean implements IPlaylistDaoBean  {

    @Override
    public PlayList getPlaylistByUUID(String uuid) {
      //TODO this is the real DAO (and we would have to fetch data here...)
      return null;
    }

}
