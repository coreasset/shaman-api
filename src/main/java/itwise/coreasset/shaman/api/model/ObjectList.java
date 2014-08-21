package itwise.coreasset.shaman.api.model;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * List template
 * 
 * @author kkuru
 * @param <T>
 *
 * @param <T>
 */
public class ObjectList<T> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6570643959301155432L;
	private int totalCount;
	private int filterCount;
	private List<T> data;
	
	/**
	 * constructor
	 */
	public int getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}

	public int getFilterCount() {
		return filterCount;
	}

	public void setFilterCount(int filterCount) {
		this.filterCount = filterCount;
	}
	
	public void setList(List<T> data){
		this.data = data;
	}

	@JsonProperty("data")
	public List<T> getList(){
		return this.data;
	}

}
