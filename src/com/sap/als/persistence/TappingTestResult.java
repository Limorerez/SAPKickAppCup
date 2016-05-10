package com.sap.als.persistence;

import java.io.Serializable;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;

@Entity
@NamedQueries({
	@NamedQuery(name = "TappingTestResultsById", query = "select d from TappingTestResult d where d.id = :id"),
	@NamedQuery(name = "TappingTestResultByName", query = "select d from TappingTestResult d where d.tappingName = :tappingName")
})
public class TappingTestResult implements Serializable {

	private static final long serialVersionUID = 1L;

	public TappingTestResult() {
	}

	@Id
	@GeneratedValue
	private long id;
	private String tappingName;
	
	
	public String getTappingName() {
		return tappingName;
	}

	public void setTappingName(String tappingName) {
		this.tappingName = tappingName;
	}

	private long tappingCount;	

	@OneToMany(cascade = CascadeType.ALL)
	private List<TappingDuration> tappingDurations;

	public List<TappingDuration> getTappingDurations() {
		return tappingDurations;
	}

	public void setTappingDurations(List<TappingDuration> tappingDuration) {
		this.tappingDurations = tappingDuration;
	}

	public long getId() {
		return id;
	}
	
	public void setId(long id) {
		this.id = id;
	}

	public long getTappingCount() {
		return tappingCount;
	}

	public void setTappingCount(long tappingCount) {
		this.tappingCount = tappingCount;
	}

}