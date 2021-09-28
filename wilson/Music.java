/*
  © Paul Wilson 2021
    Published under the Simple Public License:
    https://opensource.org/licenses/Simple-2.0
  For controlling BirdBrain Technologies Finch 2.0
    with Java and Python in online IDE like ReplIt
    see https://www.birdbraintechnologies.com/
*/
package wilson;

import java.util.ArrayList;

public class Music
{
  private ArrayList<Note> list;

  public Music() {
    list = new ArrayList<Note>();
  }
  
  public void addNote(int midiNote, int duration) {
    Note note = new Note(midiNote, duration);
    list.add(note);
  }

  public ArrayList<Note> getList() {
    return list;
  }

  public class Note
  {
    public int midiNote;
    public int duration;

    Note(int midiNote, int duration) {
      this.midiNote = midiNote;
      this.duration = duration;
    }
  }
}
