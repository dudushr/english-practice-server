package com.dudu.english;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Scanner;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;

public class EnIOUtils {

	public static String readFileOld(String fileName) {
		StringBuffer fileContent = new StringBuffer();

		try {
			File myObj = new File(fileName);
			System.out.println("---> Absolute path = " + myObj.getAbsolutePath());
			System.out.println("---> isExist = " + myObj.exists());
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
	
	
	public static String readFile(String fileName) {
		Path filePath = Path.of(fileName);

		String content = "";
		try {
			content = Files.readString(filePath, StandardCharsets.UTF_8);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return content;
	}

	public static void writeToFileOld(String text, String fileName) throws IOException {
		FileOutputStream outputStream = new FileOutputStream(fileName);
		byte[] strToBytes = text.getBytes();
		outputStream.write(strToBytes);

		outputStream.close();
	}
	
	public static void writeToFile(String text, String fileName) throws IOException {
		Path filePath = Path.of(fileName);

		try {
			Files.delete(filePath);			
		} catch (IOException e) {
			e.printStackTrace();
		}	
		
		try {			
			Files.writeString(filePath, text, StandardOpenOption.CREATE_NEW);
		} catch (IOException e) {
			e.printStackTrace();
		}		
		
	}
	
	public static String formatJson(String jsonString) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        Object jsonObject = mapper.readValue(jsonString, Object.class);
        ObjectWriter writer = mapper.writerWithDefaultPrettyPrinter();
        return writer.writeValueAsString(jsonObject);
    }

}
