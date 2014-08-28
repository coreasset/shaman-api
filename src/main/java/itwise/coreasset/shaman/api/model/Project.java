package itwise.coreasset.shaman.api.model;

import java.io.Serializable;
import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

/**
 * Created by gwkoo on 2014. 7. 29
 * Project DTO
 */
public class Project implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 593181629181614333L;
	private Integer idx = 0;
	
	private Integer build_profile_idx;
	
	private String name;
	
	private String description;
	
	private String group_id;
	
	public String getGroup_id() {
		return group_id;
	}

	public void setGroup_id(String group_id) {
		this.group_id = group_id;
	}

	public String getArtifact_id() {
		return artifact_id;
	}

	public void setArtifact_id(String artifact_id) {
		this.artifact_id = artifact_id;
	}

	public String getPackage_type() {
		return package_type;
	}

	public void setPackage_type(String package_type) {
		this.package_type = package_type;
	}

	private String artifact_id;
	private String package_type;
	
	
	private Date regDate;

	public Project() {
	}

	public Project(String name) {
		this.name = name;
	}
	
	public Project(Integer idx) {
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
}
