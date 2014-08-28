package itwise.coreasset.shaman.api.sample;

import itwise.coreasset.shaman.api.exception.ResourceNotFoundException;
import itwise.coreasset.shaman.api.mapper.ProjectMapper;
import itwise.coreasset.shaman.api.model.ObjectList;
import itwise.coreasset.shaman.api.model.Project;

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
 * Created by gwkoo on 2014. 08 .28
 */
/**
 * @author kkuru
 *
 */
@RestController
@RequestMapping("/Project")
public class ProjectApi {

	@Autowired
	private ProjectMapper projectMapper;
	

	
	/**
	 * @return
	 */
//	@RequestMapping(value = "/**", method = RequestMethod.GET, params="!type")
	public ResponseEntity<Project> except() {
		Project msg = new Project("Not Found Project API");
		return new ResponseEntity<Project>(msg, HttpStatus.NOT_FOUND);
	}

	
	/**
	 * echo api
	 * 
	 * @return
	 */
	@RequestMapping(value = "/hello", method = RequestMethod.GET, params="!type")
	public ResponseEntity<Project> hello() {
		Project msg = new Project("Hello ProjectAPI");
		return new ResponseEntity<Project>(msg, HttpStatus.OK);
	}

	/**
	 * Create Project
	 * 
	 * @param msg
	 * @return
	 */
	@RequestMapping(value = "", method = RequestMethod.POST)
	public ResponseEntity<Project> create(@RequestBody Project project) {
		projectMapper.insert(project);
		project = projectMapper.findOne(project.getIdx());
		return new ResponseEntity<Project>(project, HttpStatus.CREATED);
	}
	
	/**
	 * Update Project
	 * 
	 * @param msg
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/{idx:^[\\d]+$}", method = RequestMethod.PUT, headers = {"Content-type=application/json"})
	public ResponseEntity<Project> update(@PathVariable int idx, @RequestBody Project project) throws Exception {
		if (projectMapper.isExist(idx) > 0 ){
			projectMapper.update(idx, project);
			return new ResponseEntity<Project>(project, HttpStatus.CREATED);
		} else {
			return new ResponseEntity<Project>(HttpStatus.NO_CONTENT);
		}
	}


	/**
	 * Delete Project by idx
	 * 
	 * @param msg
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/{idx:^[\\d]+$}", method = RequestMethod.DELETE)
	public ResponseEntity<Project> delete(@PathVariable int idx) throws Exception {
		
		if (projectMapper.isExist(idx) > 0 ){
			projectMapper.delete(idx);
			return new ResponseEntity<Project>(HttpStatus.ACCEPTED);
		} else {
			return new ResponseEntity<Project>(HttpStatus.NO_CONTENT);
		}
		
	}

	/**
	 * Delete Project by name
	 * 
	 * @param msg
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/{name:^.*[^\\d].*$}", method = RequestMethod.DELETE)
	public ResponseEntity<Project> delete(@PathVariable String name) throws Exception {
		
		int idx = projectMapper.isExist(name);
		if (idx > 0){
			projectMapper.delete(idx);
			return new ResponseEntity<Project>(HttpStatus.ACCEPTED);
		} else {
			return new ResponseEntity<Project>(HttpStatus.NO_CONTENT);
		}
		
	}



	/**
	 * Get Only One, Project by int
	 * 
	 * @param int idx
	 * @return
	 * @throws ResourceNotFoundException
	 */
	@RequestMapping(value = "/{idx:^[\\d]+$}", method = RequestMethod.GET)
	public ResponseEntity<Project> findOne(@PathVariable int idx) throws ResourceNotFoundException {
		
		Project project = projectMapper.findOne(idx);
		
		if (project == null){
			return new ResponseEntity<Project>(HttpStatus.NO_CONTENT);
		}
		
		return new ResponseEntity<Project>(project, HttpStatus.OK);
	}
	
	/**
	 * Get Only One, Project by String
	 * 
	 * @param String name
	 * @return
	 */
	@RequestMapping(value = "/{name:^.*[^\\d].*$}", method = RequestMethod.GET)
	public ResponseEntity<Project> findOne(@PathVariable String name) {
		
		Project project = projectMapper.findOne(name);

		if (project == null){
			return new ResponseEntity<Project>(HttpStatus.NO_CONTENT);
		}
		
		return new ResponseEntity<Project>(project, HttpStatus.OK);
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
		
		int count = projectMapper.count();
		int lastPage = (int)Math.ceil(count / limit);
		
		if(page < 1) page = 1;
		else if(page > lastPage) page = lastPage;
		
		int offset = (page - 1) * limit;
		
		ArrayList<Project> projects = projectMapper.findList(offset, limit, keyword, order_column, order_dir);
		ObjectList<Project> response = new ObjectList<Project>();
		
		response.setList(projects);
		response.setTotalCount(count);
		response.setFilterCount(keyword == null ? count : projectMapper.count(keyword));
		return new ResponseEntity<ObjectList>(response, HttpStatus.OK);
	}


	private void sleep() {
		try {
			Thread.sleep(1 * 1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
