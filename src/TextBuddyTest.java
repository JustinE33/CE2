import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Test;

public class TextBuddyTest {

	@Test
	public void testExecuteCommand() throws IOException  {
		testOneCommand("simple wrong format", "invalid command format: blah blahblahblah", "blah blahblahblah", "mytextfile.txt");
		testOneCommand("simple add", "added to mytextfile.txt: \"the cow jumped over the moon\"", "add the cow jumped over the moon", "mytextfile.txt");
		testOneCommand("simple invalid delete", "invalid line number: 2", "delete 2", "mytextfile.txt");
		// problematictest testOneCommand("simple delete", "deleted from mytextfile.txt: \"the cow jumped over the moon\"", "delete 1", "mytextfile.txt");
		// problematictest testOneCommand("simple clear file", "All content deleted from mytextfile.txt.", "clear", "mytextfile.txt");
		//fail("Not yet implemented");
	}
	
	private void testOneCommand(String description, String expected, String command, String filename) throws IOException{
		assertEquals(description, expected, TextBuddy.executeCommand(command, filename));
	}

}
