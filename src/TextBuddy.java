import java.util.Scanner;
import java.io.*;

public class TextBuddy {
	// Message Strings
	private static final String MESSAGE_ADDED = "Added to %1$s: \"%2$s\"";
	private static final String MESSAGE_WELCOME = "Welcome to TextBuddy. %1$s is ready for use.";
	private static final String MESSAGE_SORTED = "All content in %1$s has been sorted.";
	private static final String MESSAGE_SEARCH_SUCCESS = "\"%1$s\" was found at line %2$d.";
	private static final String MESSAGE_SEARCH_FAIL = "\"%1$s\" was not found!";
	private static final String MESSAGE_CLEAR = "All content has been deleted from %1$s.";
	private static final String MESSAGE_DELETE = "Deleted from %1$s: \"%2$s\"";
	private static final String MESSAGE_INVALID_FORMAT = "Invalid command format: %1$s";
	private static final String MESSAGE_INVALID_LINE = "Invalid line number: %1$d";

	// These are the possible command types
	enum COMMAND_TYPE {
		ADD_PHRASE, DISPLAY_LIST, CLEAR_LIST, DELETE_PHRASE, SORT, SEARCH, INVALID, EXIT
	};
	
	// These are a few variables required by some methods later on in the program
	private static String fileName;
	private static boolean isAppendToFile = true;
	private static Scanner scanner = new Scanner(System.in);

	
	public static void main(String[] args) throws IOException{
		fileName = args[0];
		System.out.println(String.format(MESSAGE_WELCOME, fileName));
		while (true) {
			System.out.print("command: ");
			String command = scanner.nextLine();
			String userCommand = command;
			String feedback = executeCommand(userCommand, fileName);
			if (!feedback.equals("")){
				showToUser(feedback);
			}
		}
	}
	
	private static void showToUser(String text) {
		System.out.println(text);
	}
	
	public static String executeCommand(String userCommand, String fileName) throws IOException {
		if (userCommand.trim().equals(""))
			return String.format(MESSAGE_INVALID_FORMAT, userCommand);

		String commandTypeString = getFirstWord(userCommand);

		COMMAND_TYPE commandType = determineCommandType(commandTypeString);

		switch (commandType) {
		case ADD_PHRASE:
			return addPhrase(userCommand, fileName);
		case SORT:
			return sortFile(fileName);
		case SEARCH:
			return searchPhrase(userCommand, fileName);
		case DISPLAY_LIST:
			displayList(fileName);
			return "";
		case CLEAR_LIST:
			return clearList(fileName);
		case DELETE_PHRASE:
			return deletePhrase(userCommand, fileName);
		case INVALID:
			return String.format(MESSAGE_INVALID_FORMAT, userCommand);
		case EXIT:
			System.exit(0);
		default:
			//throw an error if the command is not recognized
			throw new Error("Unrecognized command type");
		}
	
	}
	
	// Determines the command type to be performed.
	
	private static COMMAND_TYPE determineCommandType(String commandTypeString) {
		if (commandTypeString == null)
			throw new Error("command type string cannot be null!");

		if (commandTypeString.equalsIgnoreCase("add")) {
			return COMMAND_TYPE.ADD_PHRASE;
		} else if (commandTypeString.equalsIgnoreCase("sort")) {
		 	return COMMAND_TYPE.SORT;
		} else if (commandTypeString.equals("search")){
			return COMMAND_TYPE.SEARCH;
		} else if (commandTypeString.equalsIgnoreCase("delete")) {
			return COMMAND_TYPE.DELETE_PHRASE;
		} else if (commandTypeString.equalsIgnoreCase("display")) {
			return COMMAND_TYPE.DISPLAY_LIST;
		} else if (commandTypeString.equalsIgnoreCase("clear")) {
			return COMMAND_TYPE.CLEAR_LIST;
		} else if (commandTypeString.equalsIgnoreCase("exit")) {
		 	return COMMAND_TYPE.EXIT;
		} else {
			return COMMAND_TYPE.INVALID;
		}
	}

	// Adds a phrase to the list
	private static String addPhrase(String userCommand, String fileName) throws IOException{
		String phrase = removeFirstWord(userCommand);
		
		FileWriter write = new FileWriter(fileName, isAppendToFile);
		PrintWriter print_line = new PrintWriter(write);
		
		print_line.printf("%s" + "%n", phrase);
		print_line.close();
		
		return String.format(MESSAGE_ADDED, fileName, phrase);
		
	}
	
	// Sorts the list alphabetically
	private static String sortFile(String fileName) throws IOException{
		FileReader file = new FileReader(fileName);
		BufferedReader textReader = new BufferedReader(file);
		int numberOfLines = readLines();
		String[] textData = readFileLines(numberOfLines, textReader);
		textReader.close();
			
		File inputFile = new File(fileName);
		File tempFile = new File("myTempFile.txt");
		
		String[] sortedTextData = sortArray(numberOfLines, textData);
			
		BufferedReader reader = new BufferedReader(new FileReader(inputFile));
		BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));
			
		for(int i=0; i<numberOfLines; i++) {
			writer.write(sortedTextData[i] + System.getProperty("line.separator"));
		}
		writer.close(); 
		reader.close(); 
			
		renameFile(inputFile, tempFile);
			
		return String.format(MESSAGE_SORTED, fileName);
	}
	
	// Searches for a line in the list
	private static String searchPhrase(String userCommand, String fileName) throws IOException{
		String phrase = removeFirstWord(userCommand);
		
		FileReader file = new FileReader(fileName);
		BufferedReader textReader = new BufferedReader(file);
		
		int numberOfLines = readLines();
		String[] textData = readFileLines(numberOfLines, textReader);

		textReader.close();
		
		for(int i=0; i<numberOfLines; i++){
			if (textData[i].equals(phrase)){
				return String.format(MESSAGE_SEARCH_SUCCESS, phrase, (i+1));
			}
		}
		
		return String.format(MESSAGE_SEARCH_FAIL, phrase);
	}
	
	// Deletes a phrase from the list
	private static String deletePhrase(String userCommand, String fileName) throws IOException{
		String phraseNum = removeFirstWord(userCommand);
		int deletePhraseIndex = Integer.parseInt(phraseNum);
		
		int numberOfLines = readLines();
		
		if (deletePhraseIndex > numberOfLines){
			return String.format(MESSAGE_INVALID_LINE, deletePhraseIndex);
		}
		else{
			FileReader file = new FileReader(fileName);
			BufferedReader textReader = new BufferedReader(file);
			String[] textData = readFileLines(numberOfLines, textReader);
		
			String delPhrase = textData[deletePhraseIndex-1];
			textReader.close();

			File inputFile = new File(fileName);
			File tempFile = new File("myTempFile.txt");

			BufferedReader reader = new BufferedReader(new FileReader(inputFile));
			BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));

			String currentLine;

			while((currentLine = reader.readLine()) != null) {
				if(null!=currentLine && !currentLine.equalsIgnoreCase(delPhrase)){
					writer.write(currentLine + System.getProperty("line.separator"));
				}
			}
			writer.close(); 
			reader.close(); 
			
			renameFile(inputFile, tempFile);

			return String.format(MESSAGE_DELETE,  fileName, delPhrase);
		}
	}
	// Displays the list
	private static void displayList(String fileName) throws IOException{
		FileReader file = new FileReader(fileName);
		BufferedReader textReader = new BufferedReader(file);
		
		int numberOfLines = readLines();
		String[] textData = readFileLines(numberOfLines, textReader);
		
		textReader.close();
		
		if (numberOfLines == 0){
			System.out.println(fileName + " is empty");
		}
		else{
			int j=1;
			for (int i=0; i < numberOfLines; i++){
				System.out.println(j + ". " + textData[i]);
				j++;
			}
		}
	}
	
	// Clears the list
	private static String clearList(String fileName) throws IOException{
		PrintWriter pw = new PrintWriter(fileName);
		pw.close();
		return (String.format(MESSAGE_CLEAR, fileName));
	}
	
	private static int readLines() throws IOException{
		FileReader file = new FileReader(fileName);
		BufferedReader bf = new BufferedReader(file);
		
		String aLine;
		int numberOfLines = 0;
		
		while ((aLine = bf.readLine()) != null){
			numberOfLines++;
		}
		bf.close();
		
		return numberOfLines;
	}
	
	private static String removeFirstWord(String userCommand) {
		return userCommand.replace(getFirstWord(userCommand), "").trim();
	}

	private static String getFirstWord(String userCommand) {
		String commandTypeString = userCommand.trim().split("\\s+")[0];
		return commandTypeString;
	}
	
	private static File renameFile(File inputFile, File tempFile){
		//boolean success = 
		inputFile.delete(); 
		//boolean success2 = 
		tempFile.renameTo(inputFile);
		
		return inputFile;
	}
	
	private static String[] readFileLines(int numberOfLines, BufferedReader textReader) throws IOException{
		String[] textData = new String[numberOfLines];
		for (int rowNumber=0; rowNumber < numberOfLines; rowNumber++){
			textData[rowNumber] = textReader.readLine();
		}
		
		return textData;
	}
	
	private static String[] sortArray(int numberOfLines, String[] textData){
		for(int j=0; j<numberOfLines;j++){
			for (int i=j+1 ; i<numberOfLines; i++){
				if(textData[i].compareToIgnoreCase(textData[j])<0){
					String temp = textData[j];
					textData[j]= textData[i]; 
				    textData[i]=temp;
				}
			}
		}
		return textData;
	}
	
}
