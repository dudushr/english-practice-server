package com.dudu.english.utils;

import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class DataValidationUtils {
	
	public static boolean isAlphaNumeric(String str) {
	    return str.matches("^[ א-תa-zA-Z0-9]*$");
	}
	
	public static boolean isInteger(String str) {
		try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
	}
	
	public static boolean validateWord(JSONObject word) {
	   String englishWord = word.get("englishWord").toString();
	   if(!isAlphaNumeric(englishWord)) {
		   return false;
	   }
	   
	   ArrayList hebrewWords = (ArrayList) word.get("hebrewWord");
	   for(int i=0; i<hebrewWords.size(); i++) {
		   String hebrewWord = hebrewWords.get(i).toString();
		   if(!isAlphaNumeric(hebrewWord)) {
			   return false;
		   }
	   }
	   
	   String level = word.get("level").toString();
	   if(level != null && !isInteger(level)) {
		   return false;
	   }
	   
	   return true;
	}

}
