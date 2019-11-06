Due to the difficult nature of installing the application on a smartphone that is supported and the lack of user interface to find the behaviour results.
I have logged all the data output within the application.
Two log files are provided for establishing that the locking and password behaviours are possible within the runtime of the application
Two video files are provided which were recorded at the same time as the logs. 
Therefore Using the timestamps, the logs can show the application capture my interactions in the videos.

Locking Behaviour-

Shows the unlocks and locks.

Shows the change in security

Shows the timer that the phone is left unlocked.

Password Behaviour-

Shows the capture of an actual password - to a hashed password

Shows that making mistakes in the text field e.g backspaces does not effect the capture ability

Shows that wrong passwords are not captured.

Notifications -

Strictly the methods are limited to actual updates so at the time of recording there were none, So this could not be tested I'm Afraid ;) 
So outputs remain 0 throughout the videos.

_____________________________________________________________________________________________________________________________________________________________

The Data IO was not completed in time to show all the values but was implemented to show the proof of concept for storing data.

Proof of Writing to a JSON
rawdata.json shows the output of the collection of global settings. - Created in Utils/DATAIO class

Proof of Reading a JSON
userfeedback shows the raw values that were outputted to the user interface - Created in Fragments/FragmentAssessment