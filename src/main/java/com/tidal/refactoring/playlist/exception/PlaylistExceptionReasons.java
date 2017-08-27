package com.tidal.refactoring.playlist.exception;

public enum PlaylistExceptionReasons
{

    UNKNOWN,
    PLAYLIST_MAX_SIZE_EXCEEDED,
    INDEX_INVALID,
    NO_PLAYLIST_FOUND_WITH_UUID,
    INVALID_TRACKS_TO_ADD,
    INDEX_TO_REMOVE_OUT_OF_RANGE,
    INVALID_INDEXES_TO_REMOVE,
}
