package com.dudu.english;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.commons.io.IOUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.dudu.english.utils.DataValidationUtils;
import com.dudu.english.utils.DateUtils;
import com.dudu.english.utils.PropertiesUtils;

@CrossOrigin(origins = {"http://localhost:4200", "http://141.136.36.155:4200/", "http://141.136.36.155:4210/"}, maxAge = 3600)
@RestController
public class DictionaryController {
	private final static String DICTIONARY_FIOLDER_NAME = PropertiesUtils.getInstance().get("filesResourceLocation") + PropertiesUtils.getInstance().get("dictionaryFolder");
	private final static String DICTIONARY_FILE_NAME = "_dictionary.json";
	
	private final static String CONFIG_FIOLDER_NAME = PropertiesUtils.getInstance().get("filesResourceLocation") + PropertiesUtils.getInstance().get("configFolder");
	private final static String CONFIG_FILE_NAME = "_config.json";
	
	private final static String HISTORY_FIOLDER_NAME = PropertiesUtils.getInstance().get("filesResourceLocation") + PropertiesUtils.getInstance().get("historyFolder");
	
	private final static String CLUE_FIOLDER_NAME = PropertiesUtils.getInstance().get("filesResourceLocation") + PropertiesUtils.getInstance().get("clueFolder");
	
	private final static String PATH_DELIM = PropertiesUtils.getInstance().getPathDelim();
	

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
			JSONObject wordToAdd = toEnglishWord(params);
			if(wordToAdd.get("level") == null) {
				wordToAdd.put("level", 0);
			}
			
			if(wordToAdd.get("lastDictationDate") == null) {
				wordToAdd.put("lastDictationDate", DateUtils.convertToAngularDate(new Date()));
			}
			
			if(!DataValidationUtils.validateWord(wordToAdd)) {
				return "Invalid data";
			}
			
			JSONObject dict = new JSONObject(); 
			if(dictionary != null && !"".equals(dictionary)) {
				dict = (JSONObject) jsonParser.parse(dictionary);
			}else {
				dict.put("dictionary", new JSONArray());
			}
			JSONArray wordsList = (JSONArray) dict.get("dictionary");
			wordsList.add(wordToAdd);
			
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
			saveDictionaryBackup(dictionary, uid);
		} catch (Exception e) {			
			e.printStackTrace();
		}
		System.out.println(dictionary);
		return "";
	}
	
	
	@GetMapping("/english/config/get/{uid}")	
	private String loadConfig(@PathVariable String uid) { 
		String config = "";
		try {
			config = EnIOUtils.readFile(getConfigFileName(uid));
		}catch(Exception ex) {
			System.out.println("Config no exist for user uid");
		}
		
		return config;
	}
	
	
	@PostMapping("/english/config/save")
	public String saveConfig(@RequestBody String params) {
		String uid = getUid(params);
		String numOfWordsInDictation = getBodyParam(params, "numOfWordsInDictation").replaceAll("\"", "");
		String highLevelWords = getBodyParam(params, "highLevelWords").replaceAll("\"", "");
		String mediumLevelWords = getBodyParam(params, "mediumLevelWords").replaceAll("\"", "");
		String lowLevelWords = getBodyParam(params, "lowLevelWords").replaceAll("\"", "");
		
		try {
			JSONObject json = new JSONObject();
			json.put("numOfWordsInDictation", numOfWordsInDictation);
			json.put("highLevelWords", highLevelWords);
			json.put("mediumLevelWords", mediumLevelWords);
			json.put("lowLevelWords", lowLevelWords);
			EnIOUtils.writeToFile(json.toJSONString(), getConfigFileName(uid));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return "";
	}
	
	
	@PostMapping("/remove/clue/{uid}/{englishWord}")
	public String removeClue(@PathVariable String uid, @PathVariable String englishWord) {
		try {				
	        String clueFileName = getClueFileName(uid, englishWord);
	        File file = new File(clueFileName);
	        if (file.delete()) {	            
	            updateWordParam(uid, englishWord, "clueFileName", "");
	        } else {
	            if(!file.exists()) {
	            	updateWordParam(uid, englishWord, "clueFileName", "");
	            }
	        }
			
			
			
			return getJsonStatus("OK");
		} catch (Exception e) {
			e.printStackTrace();
			return getJsonStatus("FAILED");
		}
	}
	
	@PostMapping("/upload/{uid}/{englishWord}")
	public String handleFileUpload(@PathVariable String uid, @PathVariable String englishWord, @RequestParam("file") MultipartFile file) {
		try {	
			String directory = getClueFilePath(uid);
	        File dir = new File(directory);
	        if (!dir.exists()) {
	            dir.mkdirs();
	        }
			
	        String clueFileName = getClueFileName(uid, englishWord);
			File convertedFile = new File(clueFileName);
			file.transferTo(convertedFile);
			
			updateWordParam(uid, englishWord, "clueFileName", clueFileName);
			
			return "File uploaded successfully!";
		} catch (IOException e) {
			e.printStackTrace();
			return "Error uploading file!";
		}
	}
	
	@GetMapping("/clue/{uid}/{englishWord}")
	public ResponseEntity<byte[]> getImage(@PathVariable String uid, @PathVariable("englishWord") String englishWord) {
		try {
			String clueFileName = getClueFileName(uid, englishWord);		
			FileInputStream imageStream = new FileInputStream(clueFileName);
			byte[] imageBytes = IOUtils.toByteArray(imageStream);
			
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.IMAGE_JPEG);

			ResponseEntity<byte[]> responseEntity = new ResponseEntity<byte[]>(imageBytes, headers, HttpStatus.OK);
			return responseEntity;

		}catch(Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

	
	
	public String updateWordParam(String uid, String englishWord, String key, String value) {		
		JSONParser jsonParser = new JSONParser();
			
		try {
			String dictionary = loadDictionary(uid);	
			
			JSONObject dict = (JSONObject) jsonParser.parse(dictionary);
			JSONArray wordsList = (JSONArray) dict.get("dictionary");
			for(int i=0; i<wordsList.size(); i++) {
				JSONObject word= (JSONObject) wordsList.get(i);
				String currentWord = ((JSONObject)word).get("englishWord").toString();
				if(englishWord.equals(currentWord)) {
					word.put(key, value);										
				}
			}
			
			saveDictionary(dict.toJSONString(), uid);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
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
			System.out.println("---> Dictionary filename = " + getDictionaryFileName(uid));
			dictionary = EnIOUtils.readFile(getDictionaryFileName(uid));
			System.out.println("---> dictionary = " + dictionary);
		}catch(Exception ex) {
			System.out.println("Dictionary no exist for user uid");
		}
		
		return dictionary;
	}

	private void saveDictionary(String dictionary, String uid) { 
		try {
			EnIOUtils.writeToFile(dictionary, getDictionaryFileName(uid));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void saveDictionaryBackup(String dictionary, String uid) { 
		try {
			String backupFileName = new StringBuffer("back_").append(getCurrentDateTime()).append(".json") .toString();
			String absolutFileName = new StringBuffer(HISTORY_FIOLDER_NAME).append(uid.toLowerCase()).append(PATH_DELIM).append(backupFileName).toString();
			EnIOUtils.writeToFile(EnIOUtils.formatJson(dictionary), absolutFileName);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private String getCurrentDateTime() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HHmmss");
        return now.format(formatter);
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
		                if(a.get("level") == null) {
		                	a.put("level", "0");
		                }		                
		                if(b.get("level") == null) {
		                	b.put("level", "0");
		                }
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
		            
		            if(calA.compareTo(calB) == 0) {
		            	int levelA = Integer.parseInt(a.get("level").toString());
		            	int levelB = Integer.parseInt(b.get("level").toString());
		            	if(levelA > levelB) {
		            		return 1;
		            	}else if(levelA < levelB) {
		            		return -1;
		            	}else {
		            		return 0;
		            	}
		            }else{
		            	return calA.compareTo(calB);
		            }	            
		        }
		    });

		    wordsList = new JSONArray();
		    for (int i = 0; i < jsonValues.size(); i++) {
		    	wordsList.add(jsonValues.get(i));
		    }
			
		    dict.put("dictionary", wordsList);
		    
		    dictionary = dict.toJSONString();
		    
		}catch(Exception ex) {
			System.out.println(dictionary);
			ex.printStackTrace();
		}
		
		return dictionary;
	}
	
	private String getDictionaryFileName(String uid) {
		return new StringBuffer(DICTIONARY_FIOLDER_NAME).append(uid.toLowerCase()).append(DICTIONARY_FILE_NAME).toString();
	}
	
	private String getConfigFileName(String uid) {
		return new StringBuffer(CONFIG_FIOLDER_NAME).append(uid.toLowerCase()).append(CONFIG_FILE_NAME).toString();
	}
	
	private String getClueFilePath(String uid) {
		return new StringBuffer(CLUE_FIOLDER_NAME).append(uid.toLowerCase()).toString();
	}
	
	private String getClueFileName(String uid, String englishWord) {
		return new StringBuffer(getClueFilePath(uid)).append(PATH_DELIM).append(englishWord.toLowerCase()).append(".jpg").toString();
	}
	
	private String getJsonStatus(String status) {
		StringBuffer statusAsJson = new StringBuffer("{");
		statusAsJson.append("\"status\": \"").append(status).append("\"");
		statusAsJson.append("}");
		
		return statusAsJson.toString();
	}
}