package com.csv;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStream;
import java.io.Reader;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.swing.text.Document;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.ResourceUtils;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.WebRequest;

import com.csv.service.CSVResourceService;
import com.csv.service.impl.CSVResourceServiceImpl;
import com.csv.pojo.CsvFile;
import com.csv.pojo.XmlRecords;
import com.csv.resources.CSVResource;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CsvApplicationTests {

	@Test
	public void contextLoads() {
	}

	private MockMvc mockMvc;

	@Mock
	private CSVResource csvResource;
	@Autowired
	private WebApplicationContext webApplicationContext;

	@Spy
	@InjectMocks
	private CSVResourceService CSVResourceService = new CSVResourceServiceImpl();
	@Autowired
	private HttpServletRequest request;

	@Before
	public void setup() {
		this.mockMvc = webAppContextSetup(webApplicationContext).build();
		this.mockMvc = MockMvcBuilders.standaloneSetup(csvResource).build();

	}
/*
 * read a xml file from the classpath resource folder and convert as a String */
	
	private static String convertXMLFileToXMLDocument(File xmlFile) throws FileNotFoundException {
		Reader fileReader = new FileReader(xmlFile);
		BufferedReader bufReader = new BufferedReader(fileReader);
		String xml2String = null;
		try {
			StringBuilder sb = new StringBuilder();
			String line = bufReader.readLine();
			while (line != null) {
				sb.append(line).append("\n");
				line = bufReader.readLine();
			}
			xml2String = sb.toString();
			bufReader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return xml2String;
	}
	
	/*
	 * testing the Resource with sending  two parameter and success validation  */
	
	@Test
	public void testCsvAndXmlReadResource() throws Exception {
		File file = ResourceUtils.getFile("classpath:records.csv");
		File xmlfile = ResourceUtils.getFile("classpath:records.xml");
		String strxmlfile = convertXMLFileToXMLDocument(xmlfile);
		byte[] bytesArray = new byte[(int) file.length()];

		FileInputStream fis = new FileInputStream(file);
		fis.read(bytesArray); // read file into bytes[]
		fis.close();

		HashMap<String, Object> hmObj = null;
		MockMultipartFile jsonFile = new MockMultipartFile("file", "records.csv", "multipart/form-data", bytesArray);
		when(CSVResourceService.csvFileObjListService(request, jsonFile, strxmlfile)).thenReturn(hmObj);

		mockMvc.perform(MockMvcRequestBuilders.multipart("/readcsv").file(jsonFile)
				.contentType(MediaType.MULTIPART_FORM_DATA_VALUE)).andExpect(status().isOk());
		verify(CSVResourceService, times(1)).csvFileObjListService(request, jsonFile, strxmlfile);
	}

	/*
	 * testing the Resource with sending  two parameter and error validation  */
	
	@Test
	public void testCsvAndXmlReadResource_isInternalServerError() throws Exception {
		File file = ResourceUtils.getFile("classpath:records.csv");
		File xmlfile = ResourceUtils.getFile("classpath:records.xml");
		String strxmlfile = convertXMLFileToXMLDocument(xmlfile);
		byte[] bytesArray = new byte[(int) file.length()];

		FileInputStream fis = new FileInputStream(file);
		fis.read(bytesArray); 
		fis.close();

		HashMap<String, Object> hmObj = null;
		MockMultipartFile jsonFile = new MockMultipartFile("file", "records.csv", "multipart/form-data", bytesArray);
		when(CSVResourceService.csvFileObjListService(request, jsonFile, strxmlfile)).thenReturn(hmObj);

		mockMvc.perform(MockMvcRequestBuilders.multipart("/readcsv").file(jsonFile)
				.contentType(MediaType.MULTIPART_FORM_DATA_VALUE)).andExpect(status().isInternalServerError());
		verify(CSVResourceService, times(1)).csvFileObjListService(request, jsonFile, strxmlfile);
	}
	/*
	 * testing the Resource with sending  only csvfile as a parameter and success validation  */
	
	@Test
	public void testCsvReadResource() throws Exception {
		File file = ResourceUtils.getFile("classpath:records.csv");
		File xmlfile = ResourceUtils.getFile("classpath:records.xml");
		String strxmlfile = convertXMLFileToXMLDocument(xmlfile);
		byte[] bytesArray = new byte[(int) file.length()];

		FileInputStream fis = new FileInputStream(file);
		fis.read(bytesArray); 
		fis.close();

		HashMap<String, Object> hmObj = null;
		MockMultipartFile jsonFile = new MockMultipartFile("file", "records.csv", "multipart/form-data", bytesArray);
		when(CSVResourceService.csvFileObjListService(request, jsonFile, null)).thenReturn(hmObj);

		mockMvc.perform(MockMvcRequestBuilders.multipart("/readcsv").file(jsonFile)
				.contentType(MediaType.MULTIPART_FORM_DATA_VALUE)).andExpect(status().isOk());
		verify(CSVResourceService, times(1)).csvFileObjListService(request, jsonFile, null);
	}
	/*
	 * testing the Resource with sending  only csvfile as a parameter and error validation  */
	
	@Test
	public void testCsvReadResource_isInternalServerError() throws Exception {
		File file = ResourceUtils.getFile("classpath:records.csv");
		File xmlfile = ResourceUtils.getFile("classpath:records.xml");
		String strxmlfile = convertXMLFileToXMLDocument(xmlfile);
		byte[] bytesArray = new byte[(int) file.length()];

		FileInputStream fis = new FileInputStream(file);
		fis.read(bytesArray);
		fis.close();

		HashMap<String, Object> hmObj = null;
		MockMultipartFile jsonFile = new MockMultipartFile("file", "records.csv", "multipart/form-data", bytesArray);
		when(CSVResourceService.csvFileObjListService(request, jsonFile, null)).thenReturn(hmObj);

		mockMvc.perform(MockMvcRequestBuilders.multipart("/readcsv").file(jsonFile)
				.contentType(MediaType.MULTIPART_FORM_DATA_VALUE)).andExpect(status().isInternalServerError());
		verify(CSVResourceService, times(1)).csvFileObjListService(request, jsonFile, null);
	}
	
	/*
	 * testing the Resource with sending  only xmlfile as a parameter and success validation  */
		
	@Test
	public void tesxmlReadResource() throws Exception {
		File file = ResourceUtils.getFile("classpath:records.csv");
		File xmlfile = ResourceUtils.getFile("classpath:records.xml");
		String strxmlfile = convertXMLFileToXMLDocument(xmlfile);
		byte[] bytesArray = new byte[(int) file.length()];

		FileInputStream fis = new FileInputStream(file);
		fis.read(bytesArray); 
		fis.close();

		HashMap<String, Object> hmObj = null;
		MockMultipartFile jsonFile = new MockMultipartFile("file", "records.csv", "multipart/form-data", bytesArray);
		when(CSVResourceService.csvFileObjListService(request, null, strxmlfile)).thenReturn(hmObj);

		mockMvc.perform(MockMvcRequestBuilders.multipart("/readcsv").file(jsonFile)
				.contentType(MediaType.MULTIPART_FORM_DATA_VALUE)).andExpect(status().isOk());
		verify(CSVResourceService, times(1)).csvFileObjListService(request, null, strxmlfile);
	}

	/*
	 * testing the Resource with sending  only xmlfile as a parameter and error validation  */
	
	@Test
	public void testxmlReadResource_isInternalServerError() throws Exception {
		File file = ResourceUtils.getFile("classpath:records.csv");
		File xmlfile = ResourceUtils.getFile("classpath:records.xml");
		String strxmlfile = convertXMLFileToXMLDocument(xmlfile);
		byte[] bytesArray = new byte[(int) file.length()];

		FileInputStream fis = new FileInputStream(file);
		fis.read(bytesArray); 
		fis.close();

		HashMap<String, Object> hmObj = null;
		MockMultipartFile jsonFile = new MockMultipartFile("file", "records.csv", "multipart/form-data", bytesArray);
		when(CSVResourceService.csvFileObjListService(request, null, strxmlfile)).thenReturn(hmObj);

		mockMvc.perform(MockMvcRequestBuilders.multipart("/readcsv").file(jsonFile)
				.contentType(MediaType.MULTIPART_FORM_DATA_VALUE)).andExpect(status().isInternalServerError());
		verify(CSVResourceService, times(1)).csvFileObjListService(request,null, strxmlfile);
	}
}
