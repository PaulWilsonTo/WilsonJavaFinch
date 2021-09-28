/*
  © Paul Wilson 2021
    Published under the Simple Public License:
    https://opensource.org/licenses/Simple-2.0
  For controlling BirdBrain Technologies Finch 2.0
    with Java and Python in online IDE like ReplIt
    see https://www.birdbraintechnologies.com/
*/
import wilson.Finch;
import wilson.Music;

public class TestSound
{
  public static void main(String[] args) {
    Music music = new Music();
    music.addNote(60, 1000);
    music.addNote(62, 1000);
    music.addNote(64, 1000);
    music.addNote(65, 1000);
    music.addNote(67, 1000);
    music.addNote(69, 1000);
    music.addNote(71, 1000);
    music.addNote(72, 1000);
    music.addNote(0, 2000);
    music.addNote(72, 1000);
    music.addNote(74, 1000);
    music.addNote(76, 1000);
    music.addNote(77, 1000);
    music.addNote(79, 1000);
    music.addNote(81, 1000);
    music.addNote(83, 1000);
    music.addNote(84, 1000);

    Finch finch = new Finch();
    finch.playSound(48, 3000);
    Finch.sleep(3000);
    finch.playMusic(music);
    Finch.sleep(20000);
  }
}
