package com.dudu.english;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.dudu.english.utils.DateUtils;

@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600)
@RestController
public class DictionaryController {
	private final static String DICTIONARY_FIOLDER_NAME = "C:\\MyFiles\\programs\\java\\EnglishPractice\\src\\resources\\dictionary\\"; 
	private final static String DICTIONARY_FILE_NAME = "_dictionary.json";

	@GetMapping("/dictionary/get/{uid}")	
	public String getDictionary(@PathVariable String uid) {
		String dictionary = "";
		if(!"".equals(uid.trim())) {
			dictionary = loadDictionary(uid);
			dictionary = sortDictionary(dictionary);
		}
		
		return dictionary;
	}
	
	@GetMapping("/dictionary/{uid}/getWord/{englishWord}")
	public String getWord(@PathVariable("uid") String uid, @PathVariable("englishWord") String englishWord) {
		String dictionary = loadDictionary(uid);
		JSONParser jsonParser = new JSONParser();
		try {
			JSONObject dict = (JSONObject) jsonParser.parse(dictionary);
			JSONArray wordsList = (JSONArray) dict.get("dictionary");
						
			for(int i=0; i<wordsList.size(); i++) {
				String currentEnglishWord = ((JSONObject)wordsList.get(i)).get("englishWord").toString();
				if(englishWord.equals(currentEnglishWord)) {
					return ((JSONObject)wordsList.get(i)).toJSONString();
				}
			}
			
			saveDictionary(dict.toJSONString(), uid);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return dictionary;
	}
	
	@PostMapping("/dictionary/update")
	public String updateWord(@RequestBody String params) {
		JSONParser jsonParser = new JSONParser();
			
		try {
			String uid = getUid(params);
			String wordToSave = getBodyParam(params, "wordToSave");
			wordToSave = java.net.URLDecoder.decode(wordToSave, StandardCharsets.UTF_8.name());
			wordToSave = wordToSave.substring(wordToSave.indexOf("{"));
			JSONObject newWord = (JSONObject) jsonParser.parse(wordToSave);
	
			String newEnglishWord = newWord.get("englishWord").toString();
			
			String dictionary = loadDictionary(uid);	
			
			JSONObject dict = (JSONObject) jsonParser.parse(dictionary);
			JSONArray wordsList = (JSONArray) dict.get("dictionary");
			for(int i=0; i<wordsList.size(); i++) {
				JSONObject word= (JSONObject) wordsList.get(i);
				String currentWord = ((JSONObject)word).get("englishWord").toString();
				if(newEnglishWord.equals(currentWord)) {
					word.put("englishWord", newWord.get("englishWord"));
					word.put("hebrewWord", newWord.get("hebrewWord"));
					word.put("level", newWord.get("level"));
					word.put("lastDictationDate", newWord.get("lastDictationDate"));
					
					saveDictionary(dict.toJSONString(), uid);
				}
			}
			
			saveDictionary(dict.toJSONString(), uid);
		} catch (ParseException | UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		return "";
		
		
	}
	
	@PostMapping("/dictionary/add")
	public String addWord(@RequestBody String params) {
		String uid = getUid(params);
		String dictionary = loadDictionary(uid);
		JSONParser jsonParser = new JSONParser();
		try {			
			JSONObject wordToadd = toEnglishWord(params);
			
			JSONObject dict = new JSONObject(); 
			if(dictionary != null && !"".equals(dictionary)) {
				dict = (JSONObject) jsonParser.parse(dictionary);
			}else {
				dict.put("dictionary", new JSONArray());
			}
			JSONArray wordsList = (JSONArray) dict.get("dictionary");
			wordsList.add(wordToadd);
			
			saveDictionary(dict.toJSONString(), uid);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		return "";
	}
	
	@PostMapping("/dictionary/remove")
	public String removeWord(@RequestBody String params) {
		String uid = getUid(params);
		String englishWord = getBodyParam(params, "englishWord");
		String dictionary = loadDictionary(uid);
		JSONParser jsonParser = new JSONParser();
		try {
			JSONObject dict = (JSONObject) jsonParser.parse(dictionary);
			JSONArray wordsList = (JSONArray) dict.get("dictionary");
			for(int i=0; i<wordsList.size(); i++) {
				Object word= wordsList.get(i);
				String currentWord = ((JSONObject)word).get("englishWord").toString();
				if(englishWord.equals(currentWord)) {
					wordsList.remove(word);
					i--;
				}
			}
			
			saveDictionary(dict.toJSONString(), uid);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		return "";
	}
	
	
	@PostMapping("/dictionary/updateLevel")
	public String updateLevel(@RequestBody String params) {
		String uid = getUid(params);
		String dictionary = getBodyParam(params, "dictionary");
		
		try {			
			dictionary =  java.net.URLDecoder.decode(dictionary, StandardCharsets.UTF_8.name());						
			saveDictionary(dictionary, uid);
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
		
		ArrayList<String> hebrewWordArr = new ArrayList<String>();
		hebrewWordArr.add(paramsMap.get("hebrewWord"));
		word.put("hebrewWord", hebrewWordArr);
		
		return word;
	}
	
	private String getUid(String params) {
		try {
			HashMap<String, String> paramsMap = bodyParamsToMap(params);
			String uid = paramsMap.get("uid").toLowerCase();
							
			return uid;
		}catch(Exception ex) {
			ex.printStackTrace();
			return "";
		}
	}
	
	private String getBodyParam(String params, String key) {
		try {
			HashMap<String, String> paramsMap = bodyParamsToMap(params);
			String value = paramsMap.get(key);
							
			return value;
		}catch(Exception ex) {
			ex.printStackTrace();
			return "";
		}
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
	
	
	private String loadDictionary(String uid) { 
		String dictionary = "";
		try {
			dictionary = IOUtils.readFile(getDictionaryFileName(uid));
		}catch(Exception ex) {
			System.out.println("Dictionary no exist for user uid");
		}
		
		return dictionary;
	}

	private void saveDictionary(String dictionary, String uid) { 
		try {
			IOUtils.writeToFile(dictionary, getDictionaryFileName(uid));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private String sortDictionary(String dictionary) {
		JSONParser jsonParser = new JSONParser();
		try {
			JSONObject dict = (JSONObject) jsonParser.parse(dictionary);
			//JSONObject dictionaryNode = (JSONObject) dict.get("dictionary");
			JSONArray wordsList = (JSONArray) dict.get("dictionary");
			
			List<JSONObject> jsonValues = new ArrayList<JSONObject>();
		    for (int i = 0; i < wordsList.size(); i++) {
		        jsonValues.add((JSONObject) wordsList.get(i));
		    }
		    Collections.sort( jsonValues, new Comparator<JSONObject>() {
		        //You can change "Name" with "ID" if you want to sort by ID
		        private static final String KEY_NAME = "Name";

		        @Override
		        public int compare(JSONObject a, JSONObject b) {		            
		            Calendar calA = null;
		            Calendar calB = null;

		            try {
		                String valA = (String) a.get("lastDictationDate");
		                String valB = (String) b.get("lastDictationDate");
		                int levelA = Integer.parseInt(a.get("level").toString());
		                int levelB = Integer.parseInt(b.get("level").toString());
		                
		                calA = DateUtils.stringToCal(valA);
		                calA.add(Calendar.DAY_OF_MONTH, levelA);
		                calB = DateUtils.stringToCal(valB);
		                calB.add(Calendar.DAY_OF_MONTH, levelB);
		            } 
		            catch (Exception ex) {
		                ex.printStackTrace();
		            }

		            if(calA == null) {
		            	return 1;
		            }
		            
		            return calA.compareTo(calB);		            
		        }
		    });

		    wordsList = new JSONArray();
		    for (int i = 0; i < jsonValues.size(); i++) {
		    	wordsList.add(jsonValues.get(i));
		    }
			
		    dict.put("dictionary", wordsList);
		    
		    dictionary = dict.toJSONString();
		    
		}catch(Exception ex) {
			ex.printStackTrace();
		}
		
		return dictionary;
	}
	
	private String getDictionaryFileName(String uid) {
		return new StringBuffer(DICTIONARY_FIOLDER_NAME).append(uid).append(DICTIONARY_FILE_NAME).toString();
	}
	
}