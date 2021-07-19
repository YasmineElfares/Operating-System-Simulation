import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;
import java.util.Vector;

public class OS {
	static Vector variables = new Vector();

	static String[] memory = new String[70];
	static int lastMemSpace = -1;
	static int id = 1;
	static Queue<String> readyQueue = new LinkedList();

	public static void main(String[] args) throws IOException {
		createProcess("Program 1.txt");
		createProcess("Program 2.txt");
		createProcess("Program 3.txt");
		while (!(readyQueue.isEmpty())) {
			scheduler();
		}
		System.out.println("Done!");

//		codeParser("Program 1.txt");
//	    codeParser("Program 2.txt");
//	    codeParser("Program 3.txt");
//		for(int i=0; i<=8;i++)
//			 {
//			
//			System.out.println(memory[i]);
//				
//			}

	}

	private static void scheduler() throws IOException {
		// TODO Auto-generated method stub
		String runningProcessId = readyQueue.remove();

		System.out.println("Now in process " + runningProcessId + "\n");
		System.out.println("Looking for process " + runningProcessId + " in memory");
		boolean found = false;
		int i = -1;
		while (!found && i <= lastMemSpace) {
			i++;
			if (memory[i].equals(runningProcessId)) {
				System.out.println("Found process " + runningProcessId + " in position: " + i);
				System.out.println("Process state switched to Running");
				// System.out.println("Memory Boundaries ");
//				 System.out.println("PCB: \nProcess ID: "+processID +", Memory Bounds: "+ (lastMemSpace +1)+"-"+(lastMemSpace+4+codeLines.size()+variables.size()) + ", Process state: " + processState + ", PC: " + pc);
//			     System.out.println("Lines of code: ");
				memory[i + 2] = "Running";
				found = true;

			} else {
				String[] bounds = memory[i + 1].split(",");
				String[] last = bounds[2].split("-");
				int jump = Integer.parseInt(last[1]);
				i = jump;
			}

		}

		if (found == true) {
			int pc = Integer.parseInt(memory[i + 3]);
			// System.out.println("PCB: ");
			System.out.println("PC: " + pc + " found in index " + (i + 3));

			String[] bounds = memory[i + 1].split(",");

			String[] b1 = bounds[0].split("-");
			String[] b2 = bounds[2].split("-");

			System.out.println("Memory boundaries: " + b1[0] + "-" + b2[1] + " in index " + (i + 1));
			String[] lines = bounds[1].split("-");
			int count = 1;
			codeParser(memory[pc], bounds[2]);
			int lastLine = Integer.parseInt(lines[1]);
			if (pc + 1 <= lastLine) {
				codeParser(memory[pc + 1], bounds[2]);
				pc += 1;
				count++;
			}
			pc += 1;
			if (pc - 1 == lastLine) {
				memory[i + 2] = "Finished";
			} else {
				readyQueue.add(runningProcessId);
				memory[i + 2] = "Ready";

			}
			memory[i + 3] = pc + "";

			System.out.println(
					"Process: " + runningProcessId + " is done after running for " + count + " quanta" + "!" + "\n");
		}
	}

	private static void createProcess(String string) throws IOException {
		// TODO Auto-generated method stub

		BufferedReader Reader = new BufferedReader(new FileReader(string));
		String row = "";
		Vector variables = new Vector();

		Vector codeLines = new Vector();
		while ((row = Reader.readLine()) != null) {
			String[] data = row.split(" ");
			codeLines.add(row);
			if (data[0].equals("assign")) {
				if (!(variables.contains(data[1]))) {
					variables.add(data[1]);
				}
			}

		}

		if (lastMemSpace + variables.size() + codeLines.size() + 5 > memory.length) {
			System.out.println("Sorry! Not enough memory");
			return;
		}
		String processID = id + "";
		id++;
		String processState = "Ready";
		// int pc=lastMemSpace+1;
		memory[lastMemSpace + 1] = processID;
		readyQueue.add(processID);
		String MemBound = (lastMemSpace + 1) + "-" + (lastMemSpace + 4) + "," + (lastMemSpace + 5) + "-"
				+ (lastMemSpace + 5 + codeLines.size() - 1) + "," + (lastMemSpace + 5 + codeLines.size()) + "-"
				+ (lastMemSpace + 4 + codeLines.size() + variables.size());
		// PCB, CodeLines, Variables
        //PCB: id, Memory bounds, state, pc
		String pc = (lastMemSpace + 5) + "";

		System.out.println("-------------------------------------");
		System.out.println("Creation of process: " + processID);
		System.out.println("PCB: \nProcess ID: " + processID + ", Memory Bounds: " + (lastMemSpace + 1) + "-"
				+ (lastMemSpace + 4 + codeLines.size() + variables.size()) + ", Process state: " + processState
				+ ", PC: " + pc);
		System.out.println("Lines of code: ");

		lastMemSpace++;
		memory[lastMemSpace + 1] = MemBound;
		lastMemSpace++;
		memory[lastMemSpace + 1] = processState;
		lastMemSpace++;
		memory[lastMemSpace + 1] = pc;
		lastMemSpace++;

		for (int i = 0; i < codeLines.size(); i++) {
			memory[lastMemSpace + 1] = (String) codeLines.get(i);
			lastMemSpace++;
			System.out.println(codeLines.get(i));
		}
		System.out.println("End of lines of code");
		System.out.println("-------------------------------------");
		for (int i = 0; i < variables.size(); i++) {
			memory[lastMemSpace + 1] = (String) variables.get(i);
			lastMemSpace++;
		}

//		System.out.println(MemBound);
//		System.out.println("code Lines "+ codeLines.size()+" Variables "+variables.size());

	}

	private static void codeParser(String codeLine, String bounds) throws IOException {
		// TODO Auto-generated method stub
		String res = "";
		String row = "";
//		BufferedReader Reader = new BufferedReader(new FileReader(string));
//		while ((row = Reader.readLine()) != null) {
		String[] data = codeLine.split(" ");
		for (int i = data.length - 1; i >= 0; i--) {
			switch (data[i]) {
			case ("print"):
				print(data[i + 1], bounds);

				break;

			case ("input"):
				Scanner sc = new Scanner(System.in);
				System.out.println("Please enter your input");
				String input = sc.nextLine();
				data[i] = input;
				break;

			case ("assign"):
				assign(data[i + 1], data[i + 2], bounds);
				break;

			case ("add"):
				res = add(data[i + 1], data[i + 2], bounds);
				data[i] = res;
				data[i + 1] = "";
				data[i + 2] = "";

				break;

			case ("readFile"):
				res = readFile(data[i + 1], bounds);
				data[i] = res;
				data[i + 1] = "";

				break;

			case ("writeFile"):
				writeFile(data[i + 1], data[i + 2], bounds);

				break;

			}
		}		
	}

	private static void writeFile(String fileName, String data, String bounds) {
		System.out.println("Writing in File: ...");
		String[] arr = bounds.split("-");
		int start = Integer.parseInt(arr[0]);
		int end = Integer.parseInt(arr[1]);
		// OUR NAMING CONVENTION: variable name, data

		String text = "";
		for (int j = start; j <= end; j++) {
			String[] var = memory[j].split(",");
			if (fileName.equals(var[0])) {
				System.out.println("Variable " + fileName + " found in index " + j);
				fileName = var[1]; // omar el7a2i2y (Mark.txt)
				break;
				//((Variable) variables.get(j)).set(data);
			}
		}

		String y = null;
		for (int i = start; i <= end; i++) {
			String[] var = memory[i].split(",");
			if (data.equals(var[0])) {
				y = var[1]; //omar el7a2i2y
				System.out.println("Variable " + var[0] + " found in index " + i);
				break;
				// ((Variable) variables.get(i)).set(x+y);
			}
		}

		if (y == null) {
			y = data;
		}

		// FileOutputStream fileOut;
		Path file = Path.of(fileName); // omar el7a2i2y
		// String actual = "";
		try {
			// System.out.println(data);
			Files.writeString(file, (String) y);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private static String readFile(String fileName, String bounds) throws IOException {
		System.out.println("Reading file: ...");
		String text = "";
		// System.out.println(fileName);
		String[] arr = bounds.split("-");
		int start = Integer.parseInt(arr[0]);
		int end = Integer.parseInt(arr[1]);

		for (int j = start; j <= end; j++) {
			String[] var = memory[j].split(",");
			if (fileName.equals(var[0])) {
				System.out.println("Variable " + fileName + " found in index " + j);
				fileName = var[1];
				break;
			}
		}

		try {
			File myObj = new File(fileName);
			Scanner myReader = new Scanner(myObj);
			while (myReader.hasNextLine()) {

				text += myReader.nextLine() + "\n";
			}
			myReader.close();
		} catch (FileNotFoundException e) {
			System.out.println("An error occurred.");
			e.printStackTrace();
		}

		return text;

	}

	private static String add(String number1, String number2, String bounds) {
		System.out.println("Adding: ...");
		int x;
		int y;
		String res = "";
		String[] arr = bounds.split("-");
		int start = Integer.parseInt(arr[0]);
		int end = Integer.parseInt(arr[1]);

		for (int i = start; i <= end; i++) {
			String[] var = memory[i].split(",");

			if (number1.equals(var[0])) {
				System.out.println("Variable " + number1 + " found in index " + i);
				x = Integer.parseInt(var[1]);
				if (number2.charAt(0) >= '0' && number2.charAt(0) <= '9') {
					memory[i] = var[0] + "," + (x + Integer.parseInt(number2));
				} else {
					for (int j = start; j <= end; j++) {
						String[] var2 = memory[j].split(",");
						if (number2.equals(var2[0])) {
							System.out.println("Variable " + number2 + " found in index " + j);
							y = Integer.parseInt(var2[1]);
							memory[i] = var[0] + "," + (x + y);
							break;
						}
					}
				}
				System.out.println("Writing in memory index " + i + " variable " + memory[i]);
				res = var[0];
				break;
			}
		}
		return number1;
		// TODO Auto-generated method stub

	}

	private static void assign(String varName, String data, String bounds) {
		System.out.println("Assigning: ...");
		String[] arr = bounds.split("-");
		int start = Integer.parseInt(arr[0]);
		int end = Integer.parseInt(arr[1]);

		for (int j = start; j <= end; j++) {
			String[] var = memory[j].split(",");
			if (varName.equals(var[0])) {

				memory[j] = varName + "," + data;
				System.out.println("Writing in memory index " + j + " variable " + memory[j]);
				break;
			}
		}
	}

	private static void print(String string, String bounds) {
		System.out.println("Printing: ...");
		// System.out.println("da5al");
		Object y = "";
		String[] arr = bounds.split("-");
		int start = Integer.parseInt(arr[0]);
		int end = Integer.parseInt(arr[1]);

		for (int j = start; j <= end; j++) {
			String[] var = memory[j].split(",");
			// System.out.println("hena" + y);
			if (string.equals(var[0])) {
				// System.out.println(y);
				// System.out.println("Writing in memory index " + j + " variable " +
				// memory[j]);
				System.out.println("Variable " + var[0] + " found in index " + j);
				y = var[1];
				System.out.println(var[1]);
			}

		}
		if (y.equals("")) {
			// System.out.println("da5alt");
			System.out.println(string);
		}
	}
}
