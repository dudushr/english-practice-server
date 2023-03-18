package com.dudu.english;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Scanner;

public class IOUtils {

	public static String readFile(String fileName) {
		StringBuffer fileContent = new StringBuffer();

		try {
			File myObj = new File(fileName);
			Scanner myReader = new Scanner(myObj);
			while (myReader.hasNextLine()) {
				fileContent.append(myReader.nextLine()).append("\n");

			}
			myReader.close();
		} catch (FileNotFoundException e) {
			System.out.println("An error occurred.");
			e.printStackTrace();
		}

		return fileContent.toString();
	}

	public static void writeToFile(String text, String fileName) throws IOException {
		FileOutputStream outputStream = new FileOutputStream(fileName);
		byte[] strToBytes = text.getBytes();
		outputStream.write(strToBytes);

		outputStream.close();
	}

}
