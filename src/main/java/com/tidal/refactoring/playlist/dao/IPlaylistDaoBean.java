package com.tidal.refactoring.playlist.dao;

import com.tidal.refactoring.playlist.data.PlayList;

public interface IPlaylistDaoBean {

  PlayList getPlaylistByUUID(String uuid);

}