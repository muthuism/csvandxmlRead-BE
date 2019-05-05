package com.csv.service;

import java.util.HashMap;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.springframework.web.multipart.MultipartFile;
import com.csv.pojo.CsvFile;

public interface CSVResourceService {

	public HashMap<String, Object>csvFileObjListService(HttpServletRequest req, MultipartFile file,String xmlrecords)
			throws Exception;

}
