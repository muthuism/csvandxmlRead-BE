package com.csv.resources;

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.csv.exception.CustomizedResponseEntityExceptionHandler;
import com.csv.service.CSVResourceService;

@RestController
public class CSVResource {
	private Logger log = LoggerFactory.getLogger(CustomizedResponseEntityExceptionHandler.class);

	@Autowired
	CSVResourceService csvResourceService;
/*
 * Create a Resource Endpoint to accept CSVFIle and XMLFILE as well as it accept Only XMLFILE OR CSVFILE
 * */
	@PostMapping(path = "/readcsv", consumes = "multipart/form-data")
	public ResponseEntity<HashMap<String, Object>> validateCSV(HttpServletRequest req,
			@RequestPart(value = "xmlrecords", required = false) String strxmlrecords,
			@RequestPart(value = "file", required = false) MultipartFile file) throws Exception {
		log.info("> readcsv resource ");
		HashMap<String, Object> csvFileObjList = csvResourceService.csvFileObjListService(req, file, strxmlrecords);
		log.info("< readcsv resource ");
		return new ResponseEntity<>(csvFileObjList, HttpStatus.OK);

	}

}
