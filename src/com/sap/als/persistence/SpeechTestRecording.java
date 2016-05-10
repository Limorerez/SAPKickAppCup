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
	@NamedQuery(name = "SpeechTestRecordingById", query = "select r from SpeechTestRecording r where r.id = :id")
})
public class SpeechTestRecording implements Serializable {

	private static final long serialVersionUID = 1L;

	public SpeechTestRecording() {
	}

	@Id
	@GeneratedValue
	private long id;
	private int testId;

	@Column(columnDefinition = "BLOB")
	private byte[] recording;
	
	private long length;

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

	/*
	 * public void setRecording(byte [] recording) { try { this.recording = new
	 * SerialBlob(recording); } catch (SQLException e) { throw new
	 * ExceptionInInitializerError(e); } }
	 */

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