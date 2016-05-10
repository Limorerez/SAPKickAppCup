package com.sap.als.persistence;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

@Entity
@NamedQueries({
	@NamedQuery(name = "SMSTestMessageById", query = "select m from SMSTestMessage m where m.id = :id")
})
public class SMSTestMessage implements Serializable {

	private static final long serialVersionUID = 1L;

	public SMSTestMessage() {
	}

	@Id
	@GeneratedValue
	private long id;
	private String smsType;
	private Timestamp smsDate;	
	private Integer length;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
	public String getSmsType() {
		return smsType;
	}

	public void setSmsType(String smsType) {
		this.smsType = smsType;
	}

	public Timestamp getSmsDate() {
		return smsDate;
	}

	public void setSmsDate(Timestamp smsDate) {
		this.smsDate = smsDate;
	}

	public Integer getLength() {
		return length;
	}

	public void setLength(Integer length) {
		this.length = length;
	}

}