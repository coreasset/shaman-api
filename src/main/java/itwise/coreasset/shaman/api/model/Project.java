package itwise.coreasset.shaman.api.model;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

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
	private Integer buildProfileIdx;
	private String name;
	private String description;
	private String groupId;
	private String artifactId;
	private String packageType;
	@JsonFormat(pattern="yyyy-MM-dd hh:mm")
	private Date regDate;

	public Project() {
	}
	
	public Project(Integer idx) {
		this.idx = idx;
	}

	public Project(String name) {
		this.name = name;
	}

	public String getArtifactId() {
		return artifactId;
	}

	public String getDescription() {
		return description;
	}

	public String getGroupId() {
		return groupId;
	}

	public Integer getIdx() {
		return idx;
	}

	public String getName() {
		return name;
	}
	
	public String getPackageType() {
		return packageType;
	}

	public Date getRegDate() {
		return regDate;
	}

	public void setArtifactId(String artifactId) {
		this.artifactId = artifactId;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public void setIdx(Integer idx) {
		this.idx = idx;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public void setPackageType(String packageType) {
		this.packageType = packageType;
	}

	public void setRegDate(Date regDate) {
		this.regDate = regDate;
	}
}
