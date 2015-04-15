package com.tidal.playlist;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.tidal.playlist.dao.PlaylistDaoBean;

public class TestBusinessModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(PlaylistDaoBean.class).in(Singleton.class);
        bind(PlaylistBusinessBean.class).in(Singleton.class);
    }
}
