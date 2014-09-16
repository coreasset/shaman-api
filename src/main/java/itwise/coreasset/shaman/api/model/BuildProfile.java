package itwise.coreasset.shaman.api.model;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * Created by gwkoo on 2014. 09. 16
 * 
 * BuildProfile DTO
 */
public class BuildProfile implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 593181629181614333L;

	private Integer idx = 0;
	private String name;
	private String description;
	private String flavor;
	private String goal;
	private String param;
	@JsonFormat(pattern="yyyy-MM-dd hh:mm")
	private Date regDate;
	
	public BuildProfile() {
	}
	
	public BuildProfile(Integer idx) {
		this.idx = idx;
	}

	public BuildProfile(String name) {
		this.name = name;
	}

	public String getGoal() {
		return goal;
	}

	public String getDescription() {
		return description;
	}

	public Integer getIdx() {
		return idx;
	}

	public String getName() {
		return name;
	}
	
	public String getParam() {
		return param;
	}

	public Date getRegDate() {
		return regDate;
	}

	public void setGoal(String goal) {
		this.goal = goal;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setIdx(Integer idx) {
		this.idx = idx;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public void setParam(String param) {
		this.param = param;
	}

	public void setRegDate(Date regDate) {
		this.regDate = regDate;
	}

	/**
	 * @return the flavor
	 */
	public String getFlavor() {
		return flavor;
	}

	/**
	 * @param flavor the flavor to set
	 */
	public void setFlavor(String flavor) {
		this.flavor = flavor;
	}
}
