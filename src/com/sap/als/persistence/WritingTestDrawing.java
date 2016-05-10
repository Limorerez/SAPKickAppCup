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
	@NamedQuery(name = "WritingTestDrawingById", query = "select d from WritingTestDrawing d where d.id = :id")
})
public class WritingTestDrawing implements Serializable {

	private static final long serialVersionUID = 1L;

	public WritingTestDrawing() {
	}

	@Id
	@GeneratedValue
	private long id;
	private String drawingName;
	@Column(columnDefinition = "BLOB")
	private byte[] drawingImage;
	private String drawTime;
	private String drawingTitle; //for compatibility purpose add this field and not using the drawingName 
	
	public String getDrawTime() {
		return drawTime;
	}

	public void setDrawTime(String drawTime) {
		this.drawTime = drawTime;
	}
	
	public long getId() {
		return id;
	}
	
	public void setId(long id) {
		this.id = id;
	}

	public String getDrawingName() {
		return drawingName;
	}

	public void setDrawingName(String drawingName) {
		this.drawingName = drawingName;
	}

	public byte[] getDrawingImage() {
		return drawingImage;
	}

	public void setDrawingImage(byte[] value) {
		this.drawingImage = value;
	}
	public String getDrawingTitle() {
		return drawingTitle;
	}

	public void setDrawingTitle(String drawingTitle) {
		this.drawingTitle = drawingTitle;
	}
}