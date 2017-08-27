package com.tidal.refactoring.playlist;

import static org.mockito.Mockito.when;
import static org.testng.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Guice;
import org.testng.annotations.Test;

import com.tidal.refactoring.playlist.dao.IPlaylistDaoBean;
import com.tidal.refactoring.playlist.data.PlayList;
import com.tidal.refactoring.playlist.data.PlayListTrack;
import com.tidal.refactoring.playlist.data.Track;
import com.tidal.refactoring.playlist.exception.PlaylistException;
import com.tidal.refactoring.playlist.exception.PlaylistExceptionReasons;

@Guice(modules = TestBusinessModule.class)
public class PlaylistBusinessBeanTest {

  @Mock
  IPlaylistDaoBean dao;

  @InjectMocks
  PlaylistBusinessBean business;
  
  
  PlaylistTestUtil util = new PlaylistTestUtil();

  private String uuid;

  @BeforeClass
  public void setUpClass() throws Exception {
    MockitoAnnotations.initMocks(this);
  }

  @BeforeMethod
  public void setUpOneTest() throws Exception {
    uuid = UUID.randomUUID().toString();
  }

  @AfterMethod
  public void tearDownOneTest() throws Exception {

  }


  @DataProvider(name = "ADD_TRACK_NOMINAL")
  public static Object[][] createNominalInputsADD() {
    return new Object[][] { 
      // playlistInitialSize  / numberOfTrackToAddToPlaylist / requestedIndex 
      { 0, 1, 0},
      { 0, 2, 0},
      { 0, 3, 0},
      
      { 0, 1, 10}, //requestedIndex is bigger than initial playList size (which is zero...)
      { 0, 2, 10},
      { 0, 3, 10},
      
      { 5, 1, 10}, //requestedIndex is bigger than initial playList size
      { 5, 2, 10},
      { 5, 3, 10},
      
      { PlaylistBusinessBean.MaxNumberOfTrackInPlaylist-1, 1, 0}, //500 trakcs total and insertion at begining.
      { PlaylistBusinessBean.MaxNumberOfTrackInPlaylist-2, 2, 0}, 
      { PlaylistBusinessBean.MaxNumberOfTrackInPlaylist-3, 3, 0}, 
      
      { 1, PlaylistBusinessBean.MaxNumberOfTrackInPlaylist-1, 0}, //500 trakcs total and insertion at begining.
      { 2, PlaylistBusinessBean.MaxNumberOfTrackInPlaylist-2, 0}, 
      { 3, PlaylistBusinessBean.MaxNumberOfTrackInPlaylist-3, 0}, 
      

      { PlaylistBusinessBean.MaxNumberOfTrackInPlaylist-1, 1, PlaylistBusinessBean.MaxNumberOfTrackInPlaylist}, //500 tracks total and insertion at end 
      { PlaylistBusinessBean.MaxNumberOfTrackInPlaylist-2, 2, PlaylistBusinessBean.MaxNumberOfTrackInPlaylist}, 
      { PlaylistBusinessBean.MaxNumberOfTrackInPlaylist-3, 3, PlaylistBusinessBean.MaxNumberOfTrackInPlaylist}, 
      
      { 1, PlaylistBusinessBean.MaxNumberOfTrackInPlaylist-1, PlaylistBusinessBean.MaxNumberOfTrackInPlaylist}, //500 trakcs total and insertion at end.
      { 2, PlaylistBusinessBean.MaxNumberOfTrackInPlaylist-2, PlaylistBusinessBean.MaxNumberOfTrackInPlaylist}, 
      { 3, PlaylistBusinessBean.MaxNumberOfTrackInPlaylist-3, PlaylistBusinessBean.MaxNumberOfTrackInPlaylist}, 
      
      
    };
  }
  
  @DataProvider(name = "ADD_TRACK_EXCEPTION")
  public static Object[][] createExceptionInputsADD() {
    return new Object[][] { 
      // playlistInitialSize  / numberOfTrackToAddToPlaylist / requestedIndex / expected exception
      { 0, 1, -1,PlaylistExceptionReasons.INDEX_INVALID},
      { 0, 2, -1,PlaylistExceptionReasons.INDEX_INVALID},
      { 0, 3, -1,PlaylistExceptionReasons.INDEX_INVALID},
      
      { 1, -1, 0 ,PlaylistExceptionReasons.INVALID_TRACKS_TO_ADD}, //tracks to add will be null
      { 2, -2, 0 ,PlaylistExceptionReasons.INVALID_TRACKS_TO_ADD}, 
      { 3, -3, 0 ,PlaylistExceptionReasons.INVALID_TRACKS_TO_ADD},      
      
      { -1, 1, 0,PlaylistExceptionReasons.NO_PLAYLIST_FOUND_WITH_UUID},
      { -1, 2, 0,PlaylistExceptionReasons.NO_PLAYLIST_FOUND_WITH_UUID},
      { -1, 3, 0,PlaylistExceptionReasons.NO_PLAYLIST_FOUND_WITH_UUID},
      
      { 1, 0, 0,PlaylistExceptionReasons.INVALID_TRACKS_TO_ADD}, //tracks to add will be empty
      { 2, 0, 0,PlaylistExceptionReasons.INVALID_TRACKS_TO_ADD},
      { 3, 0, 0,PlaylistExceptionReasons.INVALID_TRACKS_TO_ADD},
      
      { 1, PlaylistBusinessBean.MaxNumberOfTrackInPlaylist, 0,PlaylistExceptionReasons.PLAYLIST_MAX_SIZE_EXCEEDED}, //501 tracks total and insertion at begining.
      { 2, PlaylistBusinessBean.MaxNumberOfTrackInPlaylist, 0,PlaylistExceptionReasons.PLAYLIST_MAX_SIZE_EXCEEDED}, //502 tracks total and insertion at begining.
      { 3, PlaylistBusinessBean.MaxNumberOfTrackInPlaylist, 0,PlaylistExceptionReasons.PLAYLIST_MAX_SIZE_EXCEEDED}, //503 tracks total and insertion at begining.
    };
  }
  
  

  @Test(dataProvider = "ADD_TRACK_NOMINAL")
  public void testAddTracksNominalCase(int playlistInitialSize, int numberOfTrackToAddToPlaylist, int requestedIndex) {
    this.addTracks(playlistInitialSize, numberOfTrackToAddToPlaylist, requestedIndex);
  }

  /**
   * 
   * when adding tracks:
   * we must always check the following:
   * that tracks have been added to playlist AND they have been added at the right index.
   * that the playlist duration has updated.
   * 
   * @param playlistInitialSize the initial size of the playlist
   * @param numberOfTrackToAddToPlaylist
   * @param requestedIndex
   */
  private void addTracks(int playlistInitialSize, int numberOfTrackToAddToPlaylist, int requestedIndex) {

    PlayList generatedPlaylist = util.generatePlaylist(uuid, playlistInitialSize);
    when(dao.getPlaylistByUUID(uuid)).thenReturn(generatedPlaylist);

    int expectedInsertionIndex = requestedIndex <= playlistInitialSize ? requestedIndex : playlistInitialSize; // this is the index where the first track should be inserted

    Float initialDuration = (generatedPlaylist == null ? new Float(0) : generatedPlaylist.getDuration());

    List<Track> listOfTrackTOAdd = util.generateListOfTrack(numberOfTrackToAddToPlaylist);

    List<PlayListTrack> added = business.addTracks(uuid, listOfTrackTOAdd, requestedIndex);
    assertTrue(added.size() == numberOfTrackToAddToPlaylist); //check same number of playlistTrack than numberOfTrackToAdd.

    //HERE WE CHECK that all playlistTracks have been added at the right index in the playlist
    for (int i = 0; i < added.size(); i++) {
      PlayListTrack ptToCheck = added.get(i);
      assertTrue(ptToCheck.equals(ptToCheck.getPlaylist().getPlayListTracks().get(expectedInsertionIndex + i)));
      assertTrue(ptToCheck.getPlaylist().getNrOfTracks() == numberOfTrackToAddToPlaylist + playlistInitialSize);
    }

    //this is the duration of the track that were requested to be added.
    float durationOfInsertedTrack = (float) listOfTrackTOAdd.stream().mapToDouble(track -> track.getDuration()).sum();

    //here we check the duration of the playlist.
    Float finalDuration = generatedPlaylist.getDuration();
    assertTrue(finalDuration.floatValue() == initialDuration.floatValue() + durationOfInsertedTrack);
  }

  @Test(dataProvider = "ADD_TRACK_EXCEPTION")
  public void testAddTracksExceptionCases(int playlistInitialSize, int numberOfTrackToAddToPlaylist, int requestedIndex, PlaylistExceptionReasons failureReason) {
    boolean exceptionReceieved = false;
    try {
      this.addTracks(playlistInitialSize, numberOfTrackToAddToPlaylist, requestedIndex);
    } catch (PlaylistException e) {
      exceptionReceieved = true;
      assertTrue(e.getPlaylistExceptionReason() == failureReason);
    }
    assertTrue(exceptionReceieved);

  }

  
  
  ////////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////////
  //////////////REMOVE  FUNCTION /////////////////////////////
  ////////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////////
  ////////////////////////////////////////////////////////////

  
  
  
  @DataProvider(name = "REMOVE_TRACK_NOMINAL")
  public static Object[][] createNominalInputsREMOVE() {
    return new Object[][] { 
      // playlistInitialSize  / indexesToRemove 
      { 10, Arrays.asList(0, 2)},
      { 10, Arrays.asList(1, 2)},
      { 10, Arrays.asList(8, 9)},
      { 10, Arrays.asList(7, 8, 9)},
      { 10, Arrays.asList(7, 8, 9,7, 8, 9)},

    };
  }
  
  @DataProvider(name = "REMOVE_TRACK_EXCEPTION")
  public static Object[][] createExceptionInputsREMOVE() {
    return new Object[][] { 
      // playlistInitialSize  / indexesToRemove / exception expected
      { 10, Arrays.asList(-1,0, 2),PlaylistExceptionReasons.INDEX_TO_REMOVE_OUT_OF_RANGE},
      { 10, Arrays.asList(0, 2, 10),PlaylistExceptionReasons.INDEX_TO_REMOVE_OUT_OF_RANGE},
      { -1, Arrays.asList(0, 2, 10),PlaylistExceptionReasons.NO_PLAYLIST_FOUND_WITH_UUID},
      { 10, Arrays.asList(),PlaylistExceptionReasons.INVALID_INDEXES_TO_REMOVE},

    };
  }
  
  

  @Test(dataProvider = "REMOVE_TRACK_NOMINAL")
  public void testRemoveTracksNominalCase(int playlistInitialSize,  List<Integer> indexesToRemove) {
    this.removeTrack(playlistInitialSize, indexesToRemove);
  }


  /**
   * when we remove tracks, we check 
   * that the tracks that should not have been removed are still in the list.
   * that the playlist duration is updated.
   * that the playlist size is the one expected. (we handle duplicates)
   * 
   * @param playlistInitialSize
   * @param indexesToRemove
   */
  private void removeTrack(int playlistInitialSize, List<Integer> indexesToRemove) {
    List<Integer> sortedIndexesWithNoDublicates = indexesToRemove==null? new ArrayList<>():indexesToRemove.stream().sorted().distinct().collect(Collectors.toList());
    float durationThatMustBeRemovedFromPlaylist = 0; 
    
    final PlayList generatedPlaylist = util.generatePlaylist(uuid, playlistInitialSize);
    if(generatedPlaylist!=null) {
      durationThatMustBeRemovedFromPlaylist = (float) sortedIndexesWithNoDublicates.stream().mapToDouble(index->{
        return index<generatedPlaylist.getNrOfTracks() & index>=0? generatedPlaylist.getPlayListTracks().get(index).getTrack().getDuration():0;
        }).sum();
    }
    
   
    List<PlayListTrack> ptThatMustRemainInList =  new ArrayList<>();
    if(generatedPlaylist!=null && indexesToRemove !=null) {
      for(int i = 0; i< generatedPlaylist.getPlayListTracks().size(); i ++) {
        if(!indexesToRemove.contains(new Integer(i))) {
          ptThatMustRemainInList.add(generatedPlaylist.getPlayListTracks().get(i));
        }
      }
    }
    
    when(dao.getPlaylistByUUID(uuid)).thenReturn(generatedPlaylist);
    float initialDurationOfPlaylist = generatedPlaylist!=null ? generatedPlaylist.getDuration():0f;

    List<PlayListTrack> removedTracks = business.removeTracks(uuid, indexesToRemove);
    float removedTrackTotalDuration = (float) removedTracks.stream().mapToDouble(track -> track.getTrack().getDuration()).sum();
    
    assertTrue(removedTrackTotalDuration == durationThatMustBeRemovedFromPlaylist);
    
    //here we check that the playlist duration has been updated successfully.
    //and that the right number of tracks remains in the playlist.
    for(PlayListTrack ptRemoved:removedTracks) {
      assertTrue(ptRemoved.getPlaylist().getDuration().floatValue() == initialDurationOfPlaylist - durationThatMustBeRemovedFromPlaylist);
      assertTrue(ptRemoved.getPlaylist().getNrOfTracks() == playlistInitialSize - sortedIndexesWithNoDublicates.size());
    }
    
    // HERE WE REALLY check that everything that must remain in the list is still in the list
    ptThatMustRemainInList.stream().forEach(pt->{
      assertTrue(generatedPlaylist.getPlayListTracks().contains(pt));
    });
  
    
    
    

  }

  @Test(dataProvider = "REMOVE_TRACK_EXCEPTION")
  public void testRemoveTracksExceptionCases(int playlistInitialSize, List<Integer> indexesToRemove, PlaylistExceptionReasons failureReason) {
    boolean exceptionReceieved = false;
    try {
      this.removeTrack(playlistInitialSize, indexesToRemove);
    } catch (PlaylistException e) {
      exceptionReceieved = true;
      assertTrue(e.getPlaylistExceptionReason() == failureReason);
    }
    assertTrue(exceptionReceieved);

  }

  
  
  
  
  
  
  
  
  
  
}