FaWatcher
=========

Mass-watch FA users

This program and all elements are released under the GNU General Public License V3. Use it and modify it if you want. 
However, derivative works must also permit all rights granted by this license. This includes the bit
that says distribution of binaries must include the source code. 
At least, that was my understanding, I'm not a legalist so forgive me.

If you wish to simply use the watcher program, download the zip of this repository from the link on the right
and run "watcher.jar". Please note, this is a Java program so you must have Java installed for it to work.
I implore you to exercise extreme caution in using this application if downloaded from any other source, as 
it would be a trivial task for someone of ill intent to modify this program to steal your password.

If you're interested in where I got the list of usernames, I also have a postgres database backup available for download of the
results of a page scraping application I used to traverse the "watch graph" of furaffinity. It contains records
of usernames, a snapshot of their userpage, and who they are watching / watched by. It can be downloaded
as a 7z archive from [MediaFire](https://www.mediafire.com/?fucsuxu613c5m0l) (sorry but it was all I could get for now)

# ABOUT USING THE PROGRAM
it is very simple. You put in your FurAffinity login info, then hit start. After a few seconds, you will be
logged in and an Absol face will appear at the bottom of the screen. This will show you each new user that you've watched.
The name will appear green if a new watch was detected, and red if it couldn't watch them (perhaps they were already
on your watch list).
As well, a progress bar at the bottom of the window will keep track of how many you've got left.
You can stop and resume at any time, and even close the program and begin again later. The program does NOT store your
password anywhere so you will need to re-input it every time.
