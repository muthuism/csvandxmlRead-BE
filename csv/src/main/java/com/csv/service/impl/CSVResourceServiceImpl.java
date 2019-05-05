package com.csv.service.impl;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeSet;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.csv.pojo.CsvFile;
import com.csv.pojo.XmlRecord;
import com.csv.pojo.XmlRecords;
import com.csv.service.CSVResourceService;

import static java.util.Comparator.comparingInt;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toCollection;

@Service
public class CSVResourceServiceImpl implements CSVResourceService {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Override
	public HashMap<String, Object> csvFileObjListService(HttpServletRequest req, MultipartFile file,
			String strxmlrecords) throws Exception {
		logger.info("> readcsv Service ");
		HashMap<String, Object> hmReturn = new HashMap<>();

		if ((file!=null&&!file.isEmpty())&& strxmlrecords != null) {
			hmReturn.put("csvFileRead", csvFileRead(file));
			hmReturn.put("xmlFileRead", xmlFileRead(strxmlrecords));
		} else if ((file!=null&&!file.isEmpty())) {
			hmReturn.put("csvFileRead", csvFileRead(file));
		} else if (strxmlrecords != null) {
			hmReturn.put("xmlFileRead", xmlFileRead(strxmlrecords));
		}
		return hmReturn;
	}

	public HashMap<String, List<XmlRecord>> xmlFileRead(String strxmlrecords) throws Exception {
		logger.info(">xmlFileRead method called");
		XmlRecords xmlUnMarshalling = xmlUnMarshalling(strxmlrecords);
		HashMap<String, List<XmlRecord>> hmXmlRecords = new HashMap<String, List<XmlRecord>>();
		List<XmlRecord> uniqueByReferenceAndEndBalance = uniqueByReference(xmlUnMarshalling);
		uniqueByReferenceAndEndBalance.addAll(endBalanceValidation(xmlUnMarshalling));
		hmXmlRecords.put("xmlvalidRecords", uniqueByReferenceAndEndBalance);
		hmXmlRecords.put("xmlinvalidrecordsbyrefernce", uniqueByReferenceFailed(xmlUnMarshalling));
		hmXmlRecords.put("xmlinvalidrecordsbyendbalance", endBalanceValidationFailed(uniqueByReferenceAndEndBalance));
		return hmXmlRecords;
	}

	public List<XmlRecord> uniqueByReference(XmlRecords xmlrecords) throws Exception {
		logger.info(">uniqueByReference method called");
		return xmlrecords.getXmlRecord().stream().collect(collectingAndThen(
				toCollection(() -> new TreeSet<>(comparingInt(XmlRecord::getReference))), ArrayList::new));
	}

	public List<XmlRecord> uniqueByReferenceFailed(XmlRecords xmlrecords) throws Exception {
		logger.info(">duplicateByReference method called");
		return xmlrecords.getXmlRecord().stream().collect(Collectors.groupingBy(XmlRecord::getReference)).entrySet()
				.stream().filter(e -> e.getValue().size() > 1).flatMap(e -> e.getValue().stream())
				.collect(Collectors.toList());

	}

	public List<XmlRecord> endBalanceValidation(XmlRecords xmlrecords) throws Exception {
		logger.info(">endBalanceValidation method called");
		return xmlrecords.getXmlRecord().stream()
				.filter(f -> f.getStartBalance() + f.getMutation() == f.getEndBalance()).collect(Collectors.toList());
	}

	public List<XmlRecord> endBalanceValidationFailed(List<XmlRecord> endBalanceValidation) throws Exception {
		logger.info(">endBalanceValidationFailed method called");
		return endBalanceValidation.stream()
				.filter(f -> f.getStartBalance() + f.getMutation() != f.getEndBalance()).collect(Collectors.toList());
	}

	public XmlRecords xmlUnMarshalling(String strxmlrecords) throws Exception {
		logger.info(">xmlUnMarshalling method called");
		return (XmlRecords) JAXBContext.newInstance(XmlRecords.class).createUnmarshaller()
				.unmarshal(new StringReader(strxmlrecords));
	}

	public HashMap<String, List<CsvFile>> csvFileRead(MultipartFile file) throws Exception {
		logger.info("> readcsv Service ");
		HashMap<String, List<CsvFile>> hmObj = new HashMap<>();
		List<CsvFile> csvcvalidRecords = new ArrayList<>();
		List<CsvFile> csvinvalidrecordsbyrefernce = new ArrayList<>();
		List<CsvFile> csvinvalidrecordsbyendbalance = new ArrayList<>();
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream()));
			String line = br.readLine(); // Reading header, Ignoring
			int val = 1;
			List<Integer> liOBj = new ArrayList<>();
			while ((line = br.readLine()) != null && !line.isEmpty()) {
				String[] fields = line.split(",");
				if (val++ == 1) {
					liOBj.add(Integer.parseInt(fields[0]));
					csvcvalidRecords.add(csvFileObj(fields));
				} else {
					if (liOBj.contains(Integer.parseInt(fields[0]))) {
						csvinvalidrecordsbyrefernce.add(csvFileObj(fields));
					} else {

						liOBj.add(Integer.parseInt(fields[0]));
						Double newEndBalance = Double.parseDouble(fields[4]) + Double.parseDouble(fields[5]);
						int retval = Double.compare(newEndBalance, Double.parseDouble(fields[5]));
						if (!(retval > 0 && retval < 0)) {
							csvcvalidRecords.add(csvFileObj(fields));
						} else {
							csvinvalidrecordsbyendbalance.add(csvFileObj(fields));
						}
					}
				}
			}

			hmObj = putHash(csvcvalidRecords, csvinvalidrecordsbyrefernce, csvinvalidrecordsbyendbalance);
			logger.info("< readcsv Service ");
		} catch (Exception e) {
			logger.isTraceEnabled();
			throw e;
		}

		return hmObj;

	}

	public CsvFile csvFileObj(String[] fields) {
		logger.info(">csvFileObj method called");
		return new CsvFile(Integer.parseInt(fields[0]), fields[1], fields[2], Double.parseDouble(fields[3]),
				Double.parseDouble(fields[4]), Double.parseDouble(fields[5]));

	}

	public HashMap<String, List<CsvFile>> putHash(List<CsvFile> csvcvalidRecords,
			List<CsvFile> csvinvalidrecordsbyrefernce, List<CsvFile> csvinvalidrecordsbyendbalance) {
		logger.info(">csvFileObj method called");
		HashMap<String, List<CsvFile>> hmObj = new HashMap<>();
		hmObj.put("csvcvalidRecords", csvcvalidRecords);
		hmObj.put("csvinvalidrecordsbyrefernce", csvinvalidrecordsbyrefernce);
		hmObj.put("csvinvalidrecordsbyendbalance", csvinvalidrecordsbyendbalance);
		return hmObj;
	}
}
