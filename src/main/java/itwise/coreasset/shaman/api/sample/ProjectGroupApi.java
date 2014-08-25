package itwise.coreasset.shaman.api.sample;

import itwise.coreasset.shaman.api.exception.ResourceNotFoundException;
import itwise.coreasset.shaman.api.mapper.ProjectGroupMapper;
import itwise.coreasset.shaman.api.model.ObjectList;
import itwise.coreasset.shaman.api.model.ProjectGroup;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by gwkoo on 2014. 08 .05
 */
/**
 * @author kkuru
 *
 */
@RestController
@RequestMapping("/ProjectGroup")
public class ProjectGroupApi {

	@Autowired
	private ProjectGroupMapper projectGroupMapper;
	

	
	/**
	 * @return
	 */
//	@RequestMapping(value = "/**", method = RequestMethod.GET, params="!type")
	public ResponseEntity<ProjectGroup> except() {
		ProjectGroup msg = new ProjectGroup("Not Found ProjectGroup API");
		return new ResponseEntity<ProjectGroup>(msg, HttpStatus.NOT_FOUND);
	}

	
	/**
	 * echo api
	 * 
	 * @return
	 */
	@RequestMapping(value = "/hello", method = RequestMethod.GET, params="!type")
	public ResponseEntity<ProjectGroup> hello() {
		ProjectGroup msg = new ProjectGroup("Hello ProjectGroupAPI");
		return new ResponseEntity<ProjectGroup>(msg, HttpStatus.OK);
	}

	/**
	 * Create ProjectGroup
	 * 
	 * @param msg
	 * @return
	 */
	@RequestMapping(value = "", method = RequestMethod.POST)
	public ResponseEntity<ProjectGroup> create(@RequestBody ProjectGroup projectGroup) {
		projectGroupMapper.insert(projectGroup);
		projectGroup = projectGroupMapper.findOne(projectGroup.getIdx());
		return new ResponseEntity<ProjectGroup>(projectGroup, HttpStatus.CREATED);
	}
	
	/**
	 * Update ProjectGroup
	 * 
	 * @param msg
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/{idx:^[\\d]+$}", method = RequestMethod.PUT, headers = {"Content-type=application/json"})
	public ResponseEntity<ProjectGroup> update(@PathVariable int idx, @RequestBody ProjectGroup projectGroup) throws Exception {
		if (projectGroupMapper.isExist(idx) > 0 ){
			projectGroupMapper.update(idx, projectGroup);
			return new ResponseEntity<ProjectGroup>(projectGroup, HttpStatus.CREATED);
		} else {
			return new ResponseEntity<ProjectGroup>(HttpStatus.NO_CONTENT);
		}
	}


	/**
	 * Delete ProjectGroup by idx
	 * 
	 * @param msg
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/{idx:^[\\d]+$}", method = RequestMethod.DELETE)
	public ResponseEntity<ProjectGroup> delete(@PathVariable int idx) throws Exception {
		
		if (projectGroupMapper.isExist(idx) > 0 ){
			projectGroupMapper.delete(idx);
			return new ResponseEntity<ProjectGroup>(HttpStatus.ACCEPTED);
		} else {
			return new ResponseEntity<ProjectGroup>(HttpStatus.NO_CONTENT);
		}
		
	}

	/**
	 * Delete ProjectGroup by name
	 * 
	 * @param msg
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/{name:^.*[^\\d].*$}", method = RequestMethod.DELETE)
	public ResponseEntity<ProjectGroup> delete(@PathVariable String name) throws Exception {
		
		int idx = projectGroupMapper.isExist(name);
		if (idx > 0){
			projectGroupMapper.delete(idx);
			return new ResponseEntity<ProjectGroup>(HttpStatus.ACCEPTED);
		} else {
			return new ResponseEntity<ProjectGroup>(HttpStatus.NO_CONTENT);
		}
		
	}



	/**
	 * Get Only One, ProjectGroup by int
	 * 
	 * @param int idx
	 * @return
	 * @throws ResourceNotFoundException
	 */
	@RequestMapping(value = "/{idx:^[\\d]+$}", method = RequestMethod.GET)
	public ResponseEntity<ProjectGroup> findOne(@PathVariable int idx) throws ResourceNotFoundException {
		
		ProjectGroup projectGroup = projectGroupMapper.findOne(idx);
		
		if (projectGroup == null){
			return new ResponseEntity<ProjectGroup>(HttpStatus.NO_CONTENT);
		}
		
		return new ResponseEntity<ProjectGroup>(projectGroup, HttpStatus.OK);
	}
	
	/**
	 * Get Only One, ProjectGroup by String
	 * 
	 * @param String name
	 * @return
	 */
	@RequestMapping(value = "/{name:^.*[^\\d].*$}", method = RequestMethod.GET)
	public ResponseEntity<ProjectGroup> findOne(@PathVariable String name) {
		
		ProjectGroup projectGroup = projectGroupMapper.findOne(name);

		if (projectGroup == null){
			return new ResponseEntity<ProjectGroup>(HttpStatus.NO_CONTENT);
		}
		
		return new ResponseEntity<ProjectGroup>(projectGroup, HttpStatus.OK);
	}


	/**
	 * Get List
	 * 
	 * @param list
	 * @return
	 */
	@RequestMapping(value = "", method = RequestMethod.GET)
	public ResponseEntity<ObjectList> findList(
			  @RequestParam(value = "page", required = false, defaultValue = "1") int page
			, @RequestParam(value = "limit", required = false, defaultValue = "10") int limit
			, @RequestParam(value = "keyword", required = false) String keyword
			, @RequestParam(value = "order_column", required = false, defaultValue = "idx") String order_column
			, @RequestParam(value = "order_dir", required = false, defaultValue = "DESC") String order_dir
		) {
		
		int count = projectGroupMapper.count();
		int lastPage = (int)Math.ceil(count / limit) + 1;
		
		if(page < 1) page = 1;
		else if(page > lastPage) page = lastPage;
		
		int offset = (page - 1) * limit;
		
		ArrayList<ProjectGroup> projectGroups = projectGroupMapper.findList(offset, limit, keyword, order_column, order_dir);
		ObjectList<ProjectGroup> response = new ObjectList<ProjectGroup>();
		
		response.setList(projectGroups);
		response.setTotalCount(count);
		response.setFilterCount(keyword == null ? count : projectGroupMapper.count(keyword));
		return new ResponseEntity<ObjectList>(response, HttpStatus.OK);
	}
	
	/**
	 * TODO : 디테일한 예외 상황은 나중에 다시 정리
	 * @param ex
	 * @param response
	 * @return
	 * @throws IOException
	 */
	@ExceptionHandler(RuntimeException.class)
	public ResponseEntity<Map<String,String>> errorResponse(Exception ex, HttpServletResponse response) throws IOException{
		Map<String,String> errorMap = new HashMap<String,String>();
		errorMap.put("errorMessage", ex.getMessage());

		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		ex.printStackTrace(pw);
		String stackTrace = sw.toString();

		errorMap.put("errorStackTrace", stackTrace);
		response.sendError(HttpServletResponse.SC_BAD_REQUEST, "message goes here");
		response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());

		return new ResponseEntity<Map<String, String>>(errorMap, HttpStatus.NOT_FOUND);

	}
}
