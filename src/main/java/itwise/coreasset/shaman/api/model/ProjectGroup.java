package itwise.coreasset.shaman.api.model;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by gwkoo on 2014. 7. 29
 * ProjectGroup DTO
 */
public class ProjectGroup implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 593181629181614333L;
	private Integer idx = 0;
	
	private String name;
	
	private String description;
	
	private Date regDate;

	public ProjectGroup() {
	}

	public ProjectGroup(String name) {
		this.name = name;
	}
	
	public ProjectGroup(Integer idx) {
		this.idx = idx;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Integer getIdx() {
		return idx;
	}

	public void setIdx(Integer idx) {
		this.idx = idx;
	}

	public Date getRegDate() {
		return regDate;
	}

	public void setRegDate(Date regDate) {
		this.regDate = regDate;
	}
}
