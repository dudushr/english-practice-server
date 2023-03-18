package com.dudu.english;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.StringTokenizer;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600)
@RestController
public class DictionaryController {
	private final static String DICTIONARY_FILE_NAME = "C:\\MyFiles\\programs\\java\\EnglishPractice\\src\\resources\\dictionary\\dictionary.json"; 

	@GetMapping("/dictionary/get")
	public String getDictionary() {
		String dictionary = loadDictionary();
		return dictionary;
	}
	
	@PostMapping("/dictionary/add")
	public String addWord(@RequestBody String newWord) {
		String dictionary = loadDictionary();
		JSONParser jsonParser = new JSONParser();
		try {
			JSONObject dict = (JSONObject) jsonParser.parse(dictionary);
			JSONObject dictionaryNode = (JSONObject) dict.get("dictionary");
			JSONArray wordsList = (JSONArray) dictionaryNode.get("wordsList");
			wordsList.add(toEnglishWord(newWord));
			
			saveDictionary(dict.toJSONString());
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		return "Greetings from Spring Boot!";
	}
	
	@PostMapping("/dictionary/remove")
	public String removeWord(@RequestBody String englishWord) {
		englishWord = englishWord.substring(englishWord.indexOf("=") + 1);
		String dictionary = loadDictionary();
		JSONParser jsonParser = new JSONParser();
		try {
			JSONObject dict = (JSONObject) jsonParser.parse(dictionary);
			JSONObject dictionaryNode = (JSONObject) dict.get("dictionary");
			JSONArray wordsList = (JSONArray) dictionaryNode.get("wordsList");
			for(int i=0; i<wordsList.size(); i++) {
				Object word= wordsList.get(i);
				String currentWord = ((JSONObject)word).get("englishWord").toString();
				if(englishWord.equals(currentWord)) {
					wordsList.remove(word);
					i--;
				}
			}
			
			saveDictionary(dict.toJSONString());
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		return "";
	}
	
	
	@PostMapping("/dictionary/updateLevel")
	public String updateLevel(@RequestBody String dictionary) {
		try {
			dictionary =  java.net.URLDecoder.decode(dictionary, StandardCharsets.UTF_8.name());
			dictionary= dictionary.substring("dictionary=".length());
			
			
			
			saveDictionary(dictionary);
		} catch (Exception e) {			
			e.printStackTrace();
		}
		System.out.println(dictionary);
		return "";
	}
	
	private JSONObject toEnglishWord(String params) throws ParseException {
		HashMap<String, String> paramsMap = bodyParamsToMap(params);
		JSONObject word = new JSONObject();
		word.put("englishWord", paramsMap.get("englishWord"));
		word.put("hebrewWord", paramsMap.get("hebrewWord"));
		
		return word;
	}
	
	private HashMap<String, String> bodyParamsToMap(String params){
		HashMap<String, String> paramsMap = new HashMap<String, String>();
		StringTokenizer st = new StringTokenizer(params, "&");
		while(st.hasMoreElements()) {
			String paramAndValue = st.nextToken();
			String paramName = paramAndValue.substring(0, paramAndValue.indexOf("="));
			String value = paramAndValue.substring(paramAndValue.indexOf("=") + 1);
			
			try {
			    value = java.net.URLDecoder.decode(value, StandardCharsets.UTF_8.name());
			} catch (UnsupportedEncodingException e) {
			    // not going to happen - value came from JDK's own StandardCharsets
			}
			
			paramsMap.put(paramName,  value);
		}
		
		return paramsMap;
	}
	
	
	private String loadDictionary() { 
		String dictionary = IOUtils.readFile(DICTIONARY_FILE_NAME);
		
		return dictionary;
	}

	private void saveDictionary(String dictionary) { 
		try {
			IOUtils.writeToFile(dictionary, DICTIONARY_FILE_NAME);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}