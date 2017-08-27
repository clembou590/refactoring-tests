package com.tidal.refactoring.playlist;

import com.google.inject.Inject;
import com.tidal.refactoring.playlist.dao.PlaylistDaoBean;
import com.tidal.refactoring.playlist.data.PlayListTrack;
import com.tidal.refactoring.playlist.data.Track;
import com.tidal.refactoring.playlist.data.PlayList;
import com.tidal.refactoring.playlist.exception.PlaylistException;
import com.tidal.refactoring.playlist.exception.PlaylistExceptionReasons;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class PlaylistBusinessBean {

  private static final int MaxNumberOfTrackInPlaylist = 500;

  private PlaylistDaoBean playlistDaoBean;

  @Inject
  public PlaylistBusinessBean(PlaylistDaoBean playlistDaoBean) {
    this.playlistDaoBean = playlistDaoBean;
  }

  /**
   * Add tracks to the index
   * there must be not more than 500 track in the playlist (500 is OK)
   * if the given index is bigger than the current size of the playlist then the tracks will be added at the end of the playlist.
   * the given index must be superior or equal to zero.
   * 
   * note that this function will ONLY throw a PlaylistException. (any other kind of exception would get caught and throw a PlaylistException instead)
   * 
   * @param playlistUUID of the playlist in which we want to add tracks
   * @param tracksToAdd the tracks to add to the playlist.
   * @param toIndex the index on which we want to add tracksToAdd
   * @return the playListTrack added that have been added to the playlist.
   * @throws PlaylistException  if
   * - the playlist size exeeds 500. (500 is OK but 501 then exception)
   * - the playlist with the given uuid could not be found in dao.
   * - tracksToAdd is null or empty
   * 	
   * 
   */
  public List<PlayListTrack> addTracks(String playlistUUID, List<Track> tracksToAdd, int toIndex) throws PlaylistException {
    return wrapPlayListException((Void t) -> addTrackToPlayList(playlistDaoBean.getPlaylistByUUID(playlistUUID), tracksToAdd, toIndex));
  }

  private static List<PlayListTrack> addTrackToPlayList(PlayList playlist, List<Track> tracksToAdd, int toIndex) {
    //HANDLE NON NOMINAL CASES.
    if (playlist == null) {
      throw new PlaylistException(PlaylistExceptionReasons.NO_PLAYLIST_FOUND_WITH_UUID);
    }
    else if (toIndex < 0) {
      throw new PlaylistException(PlaylistExceptionReasons.INDEX_INVALID);
    }
    else if (tracksToAdd == null || tracksToAdd.isEmpty()) {
      throw new PlaylistException(PlaylistExceptionReasons.INVALID_TRACKS_TO_ADD);
    }
    else if (playlist.getNrOfTracks() + tracksToAdd.size() > MaxNumberOfTrackInPlaylist) {
      throw new PlaylistException(PlaylistExceptionReasons.PLAYLIST_MAX_SIZE_EXCEEDED);
    }
    else {
      //NOMINAL CASE
      int initialNbOfTracksInPlaylist = playlist.getNrOfTracks();

      //we insert the tracks at then end of the playList if the "toIndex" is bigger than initialNbOfTracksInPlaylist.
      int newComputedToIndex = Math.min(initialNbOfTracksInPlaylist, toIndex);

      //here we compute the "total duration of the new tracks that we are going to add".  
      float durationOfTrackToAdd = 0;
      durationOfTrackToAdd = computeTotalDuration(tracksToAdd);

      //now we create list of "playlistTrack" and insert it into the playlist at computedIndex
      List<PlayListTrack> listOfPlayListTrack2Add = tracksToAdd.stream().map(track -> createPlayListTrackUsingTrackAndPlaylist(track, playlist)).collect(Collectors.toList());
      playlist.getPlayListTracks().addAll(newComputedToIndex, listOfPlayListTrack2Add);

      //THIS IS JUST FOR CODE MORE READABLE. (listOfPlayListTrack2Add becomes addedTrack because it has been added).
      List<PlayListTrack> addedTrack = listOfPlayListTrack2Add;

      //we update the playList duration
      playlist.setDuration(playlist.getDuration() == null ? new Float(0 + durationOfTrackToAdd) : new Float(playlist.getDuration().floatValue() + durationOfTrackToAdd));

      //we update the lastUpdated time
      playlist.setLastUpdated(new Date());

      return addedTrack;
    }

  }

  private static PlayListTrack createPlayListTrackUsingTrackAndPlaylist(Track track, PlayList playlist) {
    PlayListTrack playlistTrack = new PlayListTrack();

    playlistTrack.setTrack(track);
    playlistTrack.setPlaylist(playlist);
    playlistTrack.setDateAdded(new Date());
    playlistTrack.setTrackId(track.getId());
    playlistTrack.setPlaylistUUID(playlist.getUuid());

    return playlistTrack;
  }

  /**
   * Remove the tracks from the playlist located at the sent indexes
   *  
   * 
   * @param playlistUUID the uuid of the playlist in which we want to remove tracks
   * @param indexes the indexes of the tracks that needs to be removed. if indexes contains two times the same index then the second occurence will be ignored: [5,6,6,7,3] will have same result as [5,6,7,3]
   * @return the  PlaylistTracks that have been removed (either all the requested indexes are removed or an exception will be thrown)
   * @throws PlaylistException
   * -playlist is not found in dao
   * -indexes is null or empty
   * -if one index is negative
   * -if one index is bigger than (playlistSize -1) . (for exemple: [track0,track1] trying to remove [2] will throw exception: so trying to remove an index from an empty list will throw an exception)
   */
  public List<PlayListTrack> removeTracks(String playlistUUID, List<Integer> indexes) throws PlaylistException {
    return wrapPlayListException((Void t) -> removeTrackToPlayList(playlistDaoBean.getPlaylistByUUID(playlistUUID), indexes));
  }

  private static List<PlayListTrack> removeTrackToPlayList(PlayList playlist, List<Integer> indexes) {
    //HANDLE NON NOMINAL CASES.
    if (playlist == null) {
      throw new PlaylistException(PlaylistExceptionReasons.NO_PLAYLIST_FOUND_WITH_UUID);
    }
    else if (indexes == null || indexes.isEmpty()) {
      throw new PlaylistException(PlaylistExceptionReasons.INVALID_INDEXES_TO_REMOVE);
    }
    else {
      //here we remove "dublicates" and sort indexes: (sorting is just to find min max values...)
      List<Integer> sortedIndexesWithNoDublicates = indexes.stream().sorted().distinct().collect(Collectors.toList());

      Integer minIndex = sortedIndexesWithNoDublicates.get(0);
      Integer maxIndex = sortedIndexesWithNoDublicates.get(sortedIndexesWithNoDublicates.size() - 1);

      List<PlayListTrack> playListTracks = playlist.getPlayListTracks();

      if (minIndex < 0 || maxIndex >= playListTracks.size()) {
        throw new PlaylistException(PlaylistExceptionReasons.INDEX_TO_REMOVE_OUT_OF_RANGE);
      }
      else {
        //NOMINAL CASE
        ArrayList<PlayListTrack> removedTracks = new ArrayList<>();

        //TODO HERE CAN AND SHOULD MODIFY THE FUNCTION TO REMOVE THE ELSE IF BLOCK AND AUTO add the removed track to the list 
        for (Integer indexToRemove : sortedIndexesWithNoDublicates) {
          PlayListTrack removed = playListTracks.remove(indexToRemove.intValue() - removedTracks.size());
          if (removed == null) {//THIS SHOULD NEVER HAPPEND
            throw new PlaylistException(PlaylistExceptionReasons.UNKNOWN);
          }
          else {
            removedTracks.add(removed);
          }
        }

        float durationToremove = computeTotalDuration(removedTracks.stream().map(pt -> pt.getTrack()).collect(Collectors.toList()));
        playlist.setDuration(playlist.getDuration() == null || playlist.getDuration().floatValue() <= durationToremove ? new Float(0) : new Float(playlist.getDuration().floatValue() - durationToremove));

        playlist.setLastUpdated(new Date());

        return removedTracks;
      }

    }

  }

  private static final float computeTotalDuration(List<Track> tracks) {
    float totalDuration = 0;
    //(for some reason mapToFloat does not exist, so we have to use mapToDouble...)
    totalDuration = (float) tracks.stream().mapToDouble(track -> track.getDuration()).sum();
    return totalDuration;
  }

  /**
   * this is just a function that will execute "func" given in parameter but it transforms any exeption that would be thrown while executing "func" into a PlaylistException
   * 
   * @param func the function that should be called
   * @return the result of the function passed as param.
   */
  private static <R> R wrapPlayListException(Function<Void, R> func) throws PlaylistException {
    try {
      return func.apply(null);
    } catch (PlaylistException e) {
      throw e;
    } catch (Exception e) {
      throw new PlaylistException(PlaylistExceptionReasons.UNKNOWN);
    }
  }

}
