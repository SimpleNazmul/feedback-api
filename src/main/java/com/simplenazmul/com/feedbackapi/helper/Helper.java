package com.simplenazmul.com.feedbackapi.helper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.simplenazmul.com.feedbackapi.exception.BadGatewayException;
import com.simplenazmul.com.feedbackapi.exception.BadRequestException;
import com.simplenazmul.com.feedbackapi.exception.InternalServerErrorException;

import org.springframework.core.io.FileSystemResource;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpEntity;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.ClientResponse;
import reactor.core.publisher.Mono;

import java.io.File;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.text.Normalizer;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.springframework.web.util.UriComponentsBuilder.fromUriString;

public class Helper {

    // method to generate a random integer of 6 digits
    public static Integer getSixDigitRandomInteger() {

        Random rand = new Random();
        int low = 100000; // this value is inclusive
        int high = 1000000; // this value is exclusive
        return rand.nextInt(high-low) + low;

    }
	
	// method to generate a random complex string of length n
    public static String getComplexString(int n) {
    	
        // chose a Character random from this String 
        String AlphaNumericString = "!@#$%^&*?"
                					+ "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                                    + "0123456789"
                                    + "abcdefghijklmnopqrstuvxyz"; 
  
        // create StringBuffer size of AlphaNumericString
        StringBuilder sb = new StringBuilder(n); 
  
        for (int i = 0; i < n; i++) { 
  
            // generate a random number between 
            // 0 to AlphaNumericString variable length 
            int index 
                = (int)(AlphaNumericString.length() 
                        * Math.random()); 
  
            // add Character one by one in end of sb 
            sb.append(AlphaNumericString 
                          .charAt(index)); 
        } 
  
        return sb.toString(); 
    }
	
	// method to generate a random alpha numeric string of length n
    public static String getAlphaNumericString(int n) {
    	
        // chose a Character random from this String 
        String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                                    + "0123456789"
                                    + "abcdefghijklmnopqrstuvxyz"; 
  
        // create StringBuffer size of AlphaNumericString 
        StringBuilder sb = new StringBuilder(n); 
  
        for (int i = 0; i < n; i++) { 
  
            // generate a random number between 
            // 0 to AlphaNumericString variable length 
            int index 
                = (int)(AlphaNumericString.length() 
                        * Math.random()); 
  
            // add Character one by one in end of sb 
            sb.append(AlphaNumericString 
                          .charAt(index)); 
        } 
  
        return sb.toString(); 
    }

    // method to generate a random string of length n
    public static String getSimpleString(int n) {

        // chose a Character random from this String
        String AlphaNumericString = "abcdefghijklmnopqrstuvxyz";

        // create StringBuffer size of AlphaNumericString
        StringBuilder sb = new StringBuilder(n);

        for (int i = 0; i < n; i++) {

            // generate a random number between
            // 0 to AlphaNumericString variable length
            int index
                    = (int)(AlphaNumericString.length()
                    * Math.random());

            // add Character one by one in end of sb
            sb.append(AlphaNumericString
                    .charAt(index));
        }

        return sb.toString();
    }
	  
    // method to generate a unique string
    public static String getUniqueString() { 
    	
    	// set string length
    	int n = 19;
    	
        // chose a Character random from this String 
        String AlphaNumericString = "abcdefghijklmnopqrstuvxyz"
                                    + "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                                    + "0123456789";
  
        // create StringBuffer size of AlphaNumericString 
        StringBuilder sb = new StringBuilder(n); 
  
        for (int i = 0; i < n; i++) { 
  
            // generate a random number between 
            // 0 to AlphaNumericString variable length 
            int index 
                = (int)(AlphaNumericString.length() 
                        * Math.random()); 
  
            // add Character one by one in end of sb 
            sb.append(AlphaNumericString 
                          .charAt(index));
        } 
  
        return sb.toString() + getCurrentTimestamp().getTime(); 
    }
    
    // method to get current timestamp
    public static Timestamp getCurrentTimestamp() {
		Calendar calendar = Calendar.getInstance();
        return new Timestamp(calendar.getTimeInMillis());
	}

    public static Map<String, String> splitQuery(String query) {
        Map<String, String> query_pairs = new LinkedHashMap<>();
        String[] pairs = query.split("&");
        for (String pair : pairs) {
            int idx = pair.indexOf("=");
            query_pairs.put(URLDecoder.decode(pair.substring(0, idx), StandardCharsets.UTF_8), URLDecoder.decode(pair.substring(idx + 1), StandardCharsets.UTF_8));
        }
        return query_pairs;
    }

    public static String buildPageUri(Pageable page) {
        return fromUriString("").query("page={page}&size={size}")
                .buildAndExpand(page.getPageNumber(), page.getPageSize()).toUriString();
    }

    public static Boolean isValidKeyword(String str) {
        String s = "^[a-zA-Z ]*$";  // Letters and space. Ex: "Zakir Hussain"
        Pattern p = Pattern.compile(s);
        assert str != null;
        Matcher m = p.matcher(str);
        return m.matches();
    }

    public static Boolean isCorrectDateFormat(String str) {
        String s = "^\\d{4}-(0[1-9]|1[012])-(0[1-9]|[12][0-9]|3[01])$"; // yyyy-MM-dd
        Pattern p = Pattern.compile(s);

        if (str == null) {
            return false;
        }

        Matcher m = p.matcher(str);
        return m.matches();
    }

    public static Boolean isAlphaNumeric(String str) {
        String s = "^[a-z0-9]+$";
        Pattern p = Pattern.compile(s);

        if (str == null) {
            return false;
        }

        Matcher m = p.matcher(str);
        return m.matches();
    }

    public static Boolean doesNotContainSpecialCharacter(String str) {
        String s = "^[a-zA-Z0-9 ]+$";
        Pattern p = Pattern.compile(s);

        if (str == null) {
            return false;
        }

        Matcher m = p.matcher(str);
        return m.matches();
    }

    public static Boolean isValidEmail(String email) {
        String s = "^[a-zA-Z0-9_+&*-]+(?:\\."+
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$";
        Pattern p = Pattern.compile(s);

        if (email == null) {
            return false;
        }

        return p.matcher(email).matches();
    }

    // These are the methods to handle communicating with external API
    public static Mono<? extends Throwable> handle4xxAnd5xxError(ClientResponse clientResponse) {
        Mono<String> errorMessage = clientResponse.bodyToMono(String.class);

        return errorMessage.flatMap((message) -> {
            if (clientResponse.statusCode().is4xxClientError()) {
                try {
                    Error errorHandler = new ObjectMapper().readValue(message, Error.class);
                    throw new BadRequestException(errorHandler.getMessage());
                } catch (JsonProcessingException e) {
                    throw new InternalServerErrorException("Could not parse error response for Status Code 400!");
                }
            } else {
                try {
                    Error errorHandler = new ObjectMapper().readValue(message, Error.class);
                    throw new BadGatewayException(errorHandler.getMessage());
                } catch (JsonProcessingException e) {
                    throw new InternalServerErrorException("Could not parse error response for Status Code 500!");
                }
            }
        });
    }

    public static String encodeURLComponent(String component) {
        return URLEncoder.encode(component, StandardCharsets.UTF_8);
    }

    public static String queryStringFromPageable(Pageable pageable) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("page=");
        stringBuilder.append(encodeURLComponent(pageable.getPageNumber() + ""));
        stringBuilder.append("&size=");
        stringBuilder.append(encodeURLComponent(pageable.getPageSize() + ""));

        // No sorting
        pageable.getSort();

        // Sorting is specified
        for (Sort.Order o : pageable.getSort()) {
            stringBuilder.append("&sort=");
            stringBuilder.append(encodeURLComponent(o.getProperty()));
            stringBuilder.append(",");
            stringBuilder.append(encodeURLComponent(o.getDirection().name()));
        }

        return stringBuilder.toString();
    }
    // The End

    private static final Pattern NON_LATIN = Pattern.compile("[^\\w-]");
    private static final Pattern WHITESPACE = Pattern.compile("[\\s]");

    public static String toSlugCase(String input) {
        String noWhiteSpace = WHITESPACE.matcher(input).replaceAll("-");
        String normalized = Normalizer.normalize(noWhiteSpace, Normalizer.Form.NFD);
        String slug = NON_LATIN.matcher(normalized).replaceAll("");
        return slug.toLowerCase(Locale.ENGLISH);
    }

    public static MultiValueMap<String, HttpEntity<?>> multiValueMap(Long accountRdbmsId, File file) {
        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        builder.part("accountRdbmsId", accountRdbmsId);
        builder.part("file", new FileSystemResource(file));
        return builder.build();
    }

    public static MultiValueMap<String, HttpEntity<?>> multiValueMap(Map<String, Object> objects) {
        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        for (Map.Entry<String, Object> object : objects.entrySet()) {
            builder.part(object.getKey(), object.getValue());
        }
        return builder.build();
    }

}