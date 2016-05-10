package com.sap.als.persistence;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

@Entity
@NamedQueries({
	@NamedQuery(name = "BreathTestRecordingById", query = "select r from BreathTestRecording r where r.id = :id")
})
public class BreathTestRecording implements Serializable {

	private static final long serialVersionUID = 1L;

	public BreathTestRecording() {
	}

	@Id
	@GeneratedValue
	private long id;
	private int testId;
	private long length;

	@Column(columnDefinition = "BLOB")
	private byte[] recording;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public byte[] getRecording() {
		return recording;
	}

	public void setRecording(byte[] recording) {
		this.recording = recording;
	}

	
	public int getTestId() {
		return testId;
	}

	public void setTestId(int answerId) {
		this.testId = answerId;
	}

	public long getLength() {
		return length;
	}

	public void setLength(long length) {
		this.length = length;
	}

}