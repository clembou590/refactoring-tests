package com.tidal.refactoring.playlist.exception;

import lombok.Getter;

public class PlaylistException extends RuntimeException {

	private static final long serialVersionUID = 759495431208011733L;
	
	private @Getter PlaylistExceptionReasons playlistExceptionReason;

	public PlaylistException(PlaylistExceptionReasons reason) {
		super();
		this.playlistExceptionReason = reason;
	}


	
}
