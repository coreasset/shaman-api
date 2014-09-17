package itwise.coreasset.shaman.api.sample;

import itwise.coreasset.shaman.api.exception.ResourceNotFoundException;
import itwise.coreasset.shaman.api.mapper.ProjectGroupMapper;
import itwise.coreasset.shaman.api.mapper.ProjectMapper;
import itwise.coreasset.shaman.api.model.ObjectList;
import itwise.coreasset.shaman.api.model.Project;
import itwise.coreasset.shaman.api.model.ProjectGroup;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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
	
	@Autowired
	private ProjectGroupMapper groupMapper;
	

	
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
		resetHasGroups(project.getIdx(), project);
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
			
			resetHasGroups(idx, project);
			
			projectMapper.update(idx, project);
			
			return new ResponseEntity<Project>(project, HttpStatus.CREATED);
		} else {
			return new ResponseEntity<Project>(HttpStatus.NO_CONTENT);
		}
	}


	private void resetHasGroups(int idx, Project project) {
		ArrayList<ProjectGroup> oldGroups = projectMapper.findOne(idx).getGroups();

		//gen add, del, keep group idx set
		Set<Integer> removeGroupIdxs = new HashSet<Integer>();
		for (ProjectGroup oldGroup: oldGroups) {
			removeGroupIdxs.add(oldGroup.getIdx());
		}
		
		Set<Integer> addGroupIdxs = new HashSet<Integer>();
		for (ProjectGroup group: project.getGroups()) {
			addGroupIdxs.add(group.getIdx());
		}
		
		Set<Integer> keepIdxs = new HashSet<Integer>(removeGroupIdxs);
		keepIdxs.retainAll(addGroupIdxs);
		removeGroupIdxs.removeAll(keepIdxs);
		addGroupIdxs.removeAll(keepIdxs);
		
		//del
		for (Integer grpIdx: removeGroupIdxs) {
			projectMapper.delHasGroup(idx, grpIdx);
		}
		//add
		for (Integer grpIdx: addGroupIdxs) {
			projectMapper.addHasGroup(idx, grpIdx);
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
		int lastPage = (int)Math.ceil((double)count / limit);
		
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

	/**
	 * add ProjectGroup
	 * 
	 * @param String name
	 * @return
	 */
//	@RequestMapping(value = "/hasGroup", method = RequestMethod.GET)
//	public ResponseEntity<Project> hasGroup(@PathVariable String name
//			, @RequestParam(value = "group_name", required = true) String groupName
//			) {
//
//		Project project = projectMapper.findOne(name);
//
//		if (project == null){
//			return new ResponseEntity<Project>(HttpStatus.NO_CONTENT);
//		}
//
//		return new ResponseEntity<Project>(project, HttpStatus.OK);
//	}

	/**
	 * add Has ProjectGroup
	 *
	 * @param String name
	 * @return
	 */
	@RequestMapping(value = "/{idx:^[\\d]+$}/hasGroup", method = RequestMethod.POST)
	public ResponseEntity<ArrayList<ProjectGroup>> addHasGroup(@PathVariable int idx
			, @RequestParam(value = "grp_idx", required = true) int groupIdx
			) {

		Project project = projectMapper.findOne(idx);
		ProjectGroup group = groupMapper.findOne(groupIdx);

		if (project == null || group == null){
			return new ResponseEntity<ArrayList<ProjectGroup>>(HttpStatus.NO_CONTENT);
		}

		project = projectMapper.findOne(idx);
		
		if (project.getGroups().contains(group)){
			return new ResponseEntity<ArrayList<ProjectGroup>>(HttpStatus.CONFLICT);
		}
		
		projectMapper.addHasGroup(idx, groupIdx);
		project = projectMapper.findOne(idx);
		
		return new ResponseEntity<ArrayList<ProjectGroup>>(project.getGroups(), HttpStatus.OK);
	}

	
	/**
	 * get Has ProjectGroups
	 * 
	 * @param idx
	 * @return
	 */
	@RequestMapping(value = "/{idx:^[\\d]+$}/hasGroup", method = RequestMethod.GET)
	public ResponseEntity<ArrayList<ProjectGroup>> getHasGroup(@PathVariable int idx) {

		Project project = projectMapper.findOne(idx);

		if (project == null || project.getGroups().size() == 0){
			return new ResponseEntity<ArrayList<ProjectGroup>>(HttpStatus.NO_CONTENT);
		}

		return new ResponseEntity<ArrayList<ProjectGroup>>(project.getGroups(), HttpStatus.OK);
	}
	
	/**
	 * delete ProjectGroup
	 * 
	 * @param String name
	 * @return
	 */
	@RequestMapping(value = "/{idx:^[\\d]+$}/hasGroup", method = RequestMethod.DELETE)
	public ResponseEntity<ArrayList<ProjectGroup>> delHasGroup(@PathVariable int idx
			, @RequestParam(value = "grp_idx", required = true) int groupIdx
			) {

		Project project = projectMapper.findOne(idx);

		if (project == null){
			return new ResponseEntity<ArrayList<ProjectGroup>>(HttpStatus.NO_CONTENT);
		}
		
		projectMapper.delHasGroup(idx, groupIdx);
		
		project = projectMapper.findOne(idx);
		
		return new ResponseEntity<ArrayList<ProjectGroup>>(project.getGroups(), HttpStatus.ACCEPTED);
	}
//
//	private void sleep() {
//		try {
//			Thread.sleep(1 * 1000);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
	
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
