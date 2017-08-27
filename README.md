# My solution to this test

###Setup notes
this is a standard maven project so to execute it:
 - git clone https://github.com/clembou590/refactoring-tests
 - mvn clean package

### My detailed solution step by step

1) remove all getters and setters from data classes and use lombok!
if you do not know this library: it enables you to create the getter and the setter with just one annotation in the java file.
--> it makes the file much more "readable" 
(for exemple in original class "PlayList.java" someone could be tempted to modify the setPlayListTracks method to also change the "number of tracks" and that code would be all "hidden" in among all other getters and setters).



2) refactoring the data classes:
--> I am going to write most of the relevant questions I asked myself to justify the changes that I would do if I were part of the team:

	A) in Playlist.java we have "id" and "uuid"... that's not necessary either use id and let the database layer autogenerate the id or use uuid and then generate it.
	--> remove id attribute

	B) why Playlist.java contains a set of "PlaylistTrack" and not a set of "Track"? (ie why not delete the class PlaylistTrack.java?)
	--> the only reason I see to keep the class "PlaylistTrack" and not completely delete it, is the "PlaylistTrack.dateAdded" field:
		when a user add a track to his playlist, if it is a requirement that we must save the information that he added this track at this time, then we cannot save it in "Track" (and obviously not in Playlist).
		So we need an additionnal class: PlaylistTrack.
		
	C) "PlaylistTrack.index" / Playlist contains a SET of PlaylistTrack and not a LIST ?
	--> a requirement for playlist fonctionnality: "it should be possible to create a playlist that contains several times the same track." (that is one of the question I asked you by email)
		index seems to be used to be able to sort the list of tracks: using an LIST instead of a SET enables the business logic to not have to deal with this problematic.
	    So I get rid of "PlaylistTrack.index" and I remove "implement Comparable" from PlayListTrack.
	    I also remove the attribute PlayList.nrOfTracks but I create a getNrOfTracks that returns the size of the list.
	    
	D) ¨PlayListTrack" contains attribute: Track / TrackId / Playlist But not PlaylistId?
	--> add playListUUID as an attribute in PlayListTrack.java (we need this information because two playlistTrack are not equals if they do not belong to the same Playlist).
	note: we could have used playlist.getUUID() (and also track.getId()) but in this case it would mean that the data "playlist and track objects" would have to be fetched from DB.
	
	 
3) PlaylistBusinessBean:
because of previous steps, the Business class becomes very small. (small means easier to maintain, easier to understand too...).
only two public methods (one for add, one for remove tracks). All other methods are private AND static because they do not use class members.
when adding or removing tracks from a playlist, we must also update the "playlist duration" and I see 2 ways to do it:
+ A): you just recompute all the playlist duration using all the tracks the playlist contains.
+ B): you just add/remove the time of the sum of the duration of the elements that were added/removed.

- I chose option B) because this way we do not have to "fetch" all the "track" objects to update the playlist (it's possible when calling DAO that track object is lazy loaded into playlistTrack).



4) TESTING:
+ create java interface "IPlaylistDaoBean" for DAO so that just by configuring dependency injection we can switch to another DAO.
+ PlaylistDaoBean is just an implementation and will not be used by our unit tests : the DAO must be "stubbed" when testing business logics --> let's use mockito.
+ I created PlaylistTestUtil.java to be able to create some input Data for the tests.



+ I created A parameterized unit test for "ADD_TRACK_NOMINAL".
+ I created a parameterized unit test for "ADD_TRACK_EXCEPTION."
+ both test case use the same method (less code to maintain...)
+ All Corner cases are tested.
+ All "exception" cases are tested.

+ same things for the "remove" functionnality.

conclusion:
there are 45 test cases and all of them runs successfully.
Please do not hesitate to contact me if you have any questions about my code.
thank you.


