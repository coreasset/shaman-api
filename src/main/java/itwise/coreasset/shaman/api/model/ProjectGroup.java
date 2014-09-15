package itwise.coreasset.shaman.api.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;

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
	
	@JsonFormat(pattern="yyyy-MM-dd hh:mm")
	private Date regDate;
	
	private ArrayList<Project> projects = new ArrayList<Project>();

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
	
	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
	public Date getRegDate() {
		return regDate;
	}

	public void setRegDate(Date regDate) {
		this.regDate = regDate;
	}

	/**
	 * @return the projects
	 */
	public ArrayList<Project> getProjects() {
		return projects;
	}

	/**
	 * @param projects the projects to set
	 */
	public void setProjects(ArrayList<Project> projects) {
		this.projects = projects;
	}
}
