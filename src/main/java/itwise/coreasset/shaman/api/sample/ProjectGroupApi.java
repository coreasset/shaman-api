package itwise.coreasset.shaman.api.sample;

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
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
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
	@RequestMapping(method = RequestMethod.POST, headers = {"Content-type=application/json"})
	public HttpEntity<ProjectGroup> create(@RequestBody ProjectGroup projectGroup) {
		projectGroupMapper.insert(projectGroup);
		return new ResponseEntity<ProjectGroup>(projectGroup, HttpStatus.CREATED);
	}
	
	/**
	 * Update ProjectGroup
	 * 
	 * @param msg
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/{idx}", method = RequestMethod.PUT)
	public ResponseEntity<ProjectGroup> update(@PathVariable int idx, @RequestBody ProjectGroup projectGroup) throws Exception {
		if (projectGroupMapper.isExist(idx) == 1 ){
			projectGroupMapper.update(idx, projectGroup);
			return new ResponseEntity<ProjectGroup>(projectGroup, HttpStatus.OK);
		} else {
			throw new RuntimeException("Update Fail, cannot find ProjectGroup");
		}
	}


	/**
	 * Delete ProjectGroup
	 * 
	 * @param msg
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/{idx:[\\d]+}", method = RequestMethod.DELETE)
	public ResponseEntity<ProjectGroup> delete(@PathVariable int idx) throws Exception {
		
		if (projectGroupMapper.isExist(idx) == 1 ){
			projectGroupMapper.delete(idx);
			return new ResponseEntity<ProjectGroup>(new ProjectGroup(), HttpStatus.OK);
		} else {
			throw new RuntimeException("Delete Fail, cannot find ProjectGroup");
		}
		
	}
	
	/**
	 * @param msg
	 * @return
	 */
	@RequestMapping(value = "/{name}", method = RequestMethod.GET)
	public ResponseEntity<ProjectGroup> findOne(@PathVariable String name) {
		
		ProjectGroup projectGroup = projectGroupMapper.findOne(name);
		
		return new ResponseEntity<ProjectGroup>(projectGroup, HttpStatus.OK);
	}

	/**
	 * Get Only One, ProjectGroup
	 * 
	 * @param msg
	 * @return
	 */
	@RequestMapping(value = "/{idx:[\\d]+}", method = RequestMethod.GET)
	public ResponseEntity<ProjectGroup> findOne(@PathVariable int idx) {
		
		ProjectGroup projectGroup = projectGroupMapper.findOne(idx);
		
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
			, @RequestParam(value = "sort", required = false) String sort
		) {
		
		if(page < 1) {
			page = 1;
		}
		
		int offset = (page - 1) * limit;
		
		ArrayList<ProjectGroup> projectGroups = projectGroupMapper.findList(offset, limit, keyword);
		ObjectList<ProjectGroup> response = new ObjectList<ProjectGroup>();
		
		response.setList(projectGroups);
		response.setTotalCount(projectGroupMapper.count());
		//TODO: datatables에서 필요로 함, 나중에 구현
		response.setFilterCount(projectGroupMapper.count());
		return new ResponseEntity<ObjectList>(response, HttpStatus.OK);
	}
	
	@RequestMapping(value = "", method = RequestMethod.POST)
	@ResponseBody
	public void example() throws Exception{
		System.out.println("in the example function");
		throw new Exception("a new Exception");
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
