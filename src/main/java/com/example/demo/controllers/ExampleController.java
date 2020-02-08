package com.example.demo.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.http.HttpServletRequest;

/**
 * ExampleController
 * 
 * @author danielpadua
 *
 */
@Controller
@RequestMapping("/")
public class ExampleController {
	
	@ResponseBody
	@RequestMapping(method = GET, produces = "application/json")
	public double assignmentAPI(HttpServletRequest req) throws ParseException, IOException {
		HashMap<String, Integer> data = new HashMap<String,Integer>();
		String apireq = "https://api.exchangeratesapi.io/";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String start = req.getParameter("start");
		String end = req.getParameter("end");
		String currency = req.getParameter("currency");
		String curr = start;
		int k = 0;
		double total = 0;
		while(curr.compareTo(end) != 0) {
			URL url = new URL(apireq+curr);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("GET");
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer content = new StringBuffer();
			while ((inputLine = in.readLine()) != null) {
			    content.append(inputLine);
			}
			HashMap<String, Object> map = new HashMap<String, Object>();
	        System.out.println(content);
	        ObjectMapper mapper = new ObjectMapper();
	        try
	        {
	            //Convert Map to JSON
	            map = (HashMap<String, Object>) mapper.readValue(content.toString(), new TypeReference<Map<String, Object>>(){});
	            total += ((HashMap<String, Double>)map.get("rates")).get(currency);
//	            System.out.println(((HashMap<String, Double>)map.get("rates")).get(currency));
	        } 
	        catch (JsonGenerationException e) {
	            e.printStackTrace();
	        } catch (JsonMappingException e) {
	            e.printStackTrace();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
			in.close();
			con.disconnect();
			curr = LocalDate.parse(curr).plusDays(1).toString();
			System.out.println(curr);
			k++;
		}
		double avg = total/(k*1.0);
		return avg;
	}
}