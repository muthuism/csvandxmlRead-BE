package com.csv.pojo;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlAccessType;

@XmlRootElement(name = "records")
@XmlAccessorType(XmlAccessType.FIELD)
public class XmlRecords {
	@XmlElement(name = "record")
	private List<XmlRecord> xmlRecord = null;

	public List<XmlRecord> getXmlRecord() {
		return xmlRecord;
	}

	public void setXmlRecord(List<XmlRecord> xmlRecord) {
		this.xmlRecord = xmlRecord;
	}

}
