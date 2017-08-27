package com.tidal.refactoring.playlist;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.tidal.refactoring.playlist.PlaylistBusinessBean;
import com.tidal.refactoring.playlist.dao.IPlaylistDaoBean;
import com.tidal.refactoring.playlist.dao.PlaylistDaoBean;

public class TestBusinessModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(IPlaylistDaoBean.class).to(PlaylistDaoBean.class);
        bind(PlaylistDaoBean.class).in(Singleton.class);
        bind(PlaylistBusinessBean.class).in(Singleton.class);
    }
}
