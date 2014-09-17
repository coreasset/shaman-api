package itwise.coreasset.shaman.api.sample;

import itwise.coreasset.shaman.api.exception.ResourceNotFoundException;
import itwise.coreasset.shaman.api.mapper.BuildProfileMapper;
import itwise.coreasset.shaman.api.model.BuildProfile;
import itwise.coreasset.shaman.api.model.ObjectList;

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
@RequestMapping("/BuildProfile")
public class BuildProfileApi {

	@Autowired
	private BuildProfileMapper profileMapper;
	
	
	/**
	 * @return
	 */
//	@RequestMapping(value = "/**", method = RequestMethod.GET, params="!type")
	public ResponseEntity<BuildProfile> except() {
		BuildProfile msg = new BuildProfile("Not Found BuildProfile API");
		return new ResponseEntity<BuildProfile>(msg, HttpStatus.NOT_FOUND);
	}

	
	/**
	 * echo api
	 * 
	 * @return
	 */
	@RequestMapping(value = "/hello", method = RequestMethod.GET, params="!type")
	public ResponseEntity<BuildProfile> hello() {
		BuildProfile msg = new BuildProfile("Hello BuildProfileAPI");
		return new ResponseEntity<BuildProfile>(msg, HttpStatus.OK);
	}

	/**
	 * Create BuildProfile
	 * 
	 * @param msg
	 * @return
	 */
	@RequestMapping(value = "", method = RequestMethod.POST)
	public ResponseEntity<BuildProfile> create(@RequestBody BuildProfile profile) {
		profileMapper.insert(profile);
		
		profile = profileMapper.findOne(profile.getIdx());
		
		return new ResponseEntity<BuildProfile>(profile, HttpStatus.CREATED);
	}
	
	/**
	 * Update BuildProfile
	 * 
	 * @param msg
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/{idx:^[\\d]+$}", method = RequestMethod.PUT)
	public ResponseEntity<BuildProfile> update(@PathVariable int idx, @RequestBody BuildProfile profile) throws Exception {
		if (profileMapper.isExist(idx) > 0 ){
			
			profileMapper.update(idx, profile);
			
			return new ResponseEntity<BuildProfile>(profile, HttpStatus.CREATED);
		} else {
			return new ResponseEntity<BuildProfile>(HttpStatus.NO_CONTENT);
		}
	}


	/**
	 * @param idx
	 * @param profile
	private void resetHasGroups(int idx, BuildProfile profile) {
		ArrayList<BuildProfileGroup> oldGroups = profileMapper.findOne(idx).getGroups();

		//gen add, del, keep group idx set
		Set<Integer> removeGroupIdxs = new HashSet<Integer>();
		for (BuildProfileGroup oldGroup: oldGroups) {
			removeGroupIdxs.add(oldGroup.getIdx());
		}
		
		Set<Integer> addGroupIdxs = new HashSet<Integer>();
		for (BuildProfileGroup group: profile.getGroups()) {
			addGroupIdxs.add(group.getIdx());
		}
		
		Set<Integer> keepIdxs = new HashSet<Integer>(removeGroupIdxs);
		keepIdxs.retainAll(addGroupIdxs);
		removeGroupIdxs.removeAll(keepIdxs);
		addGroupIdxs.removeAll(keepIdxs);
		
		//del
		for (Integer grpIdx: removeGroupIdxs) {
			profileMapper.delHasGroup(idx, grpIdx);
		}
		//add
		for (Integer grpIdx: addGroupIdxs) {
			profileMapper.addHasGroup(idx, grpIdx);
		}
	}
	 */


	/**
	 * Delete BuildProfile by idx
	 * 
	 * @param msg
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/{idx:^[\\d]+$}", method = RequestMethod.DELETE)
	public ResponseEntity<BuildProfile> delete(@PathVariable int idx) throws Exception {
		
		if (profileMapper.isExist(idx) > 0 ){
			profileMapper.delete(idx);
			return new ResponseEntity<BuildProfile>(HttpStatus.ACCEPTED);
		} else {
			return new ResponseEntity<BuildProfile>(HttpStatus.NO_CONTENT);
		}
		
	}

	/**
	 * Delete BuildProfile by name
	 * 
	 * @param msg
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/{name:^.*[^\\d].*$}", method = RequestMethod.DELETE)
	public ResponseEntity<BuildProfile> delete(@PathVariable String name) throws Exception {
		
		int idx = profileMapper.isExist(name);
		if (idx > 0){
			profileMapper.delete(idx);
			return new ResponseEntity<BuildProfile>(HttpStatus.ACCEPTED);
		} else {
			return new ResponseEntity<BuildProfile>(HttpStatus.NO_CONTENT);
		}
		
	}



	/**
	 * Get Only One, BuildProfile by int
	 * 
	 * @param int idx
	 * @return
	 * @throws ResourceNotFoundException
	 */
	@RequestMapping(value = "/{idx:^[\\d]+$}", method = RequestMethod.GET)
	public ResponseEntity<BuildProfile> findOne(@PathVariable int idx) throws ResourceNotFoundException {
		
		BuildProfile profile = profileMapper.findOne(idx);
		
		if (profile == null){
			return new ResponseEntity<BuildProfile>(HttpStatus.NO_CONTENT);
		}
		
		return new ResponseEntity<BuildProfile>(profile, HttpStatus.OK);
	}
	
	/**
	 * Get Only One, BuildProfile by String
	 * 
	 * @param String name
	 * @return
	 */
	@RequestMapping(value = "/{name:^.*[^\\d].*$}", method = RequestMethod.GET)
	public ResponseEntity<BuildProfile> findOne(@PathVariable String name) {
		
		BuildProfile profile = profileMapper.findOne(name);

		if (profile == null){
			return new ResponseEntity<BuildProfile>(HttpStatus.NO_CONTENT);
		}
		
		return new ResponseEntity<BuildProfile>(profile, HttpStatus.OK);
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
		
		int count = profileMapper.count();
		int lastPage = (int)Math.ceil((double)count / limit);
		
		if(page < 1) page = 1;
		else if(page > lastPage) page = lastPage;
		
		int offset = (page - 1) * limit;
		
		ArrayList<BuildProfile> profiles = profileMapper.findList(offset, limit, keyword, order_column, order_dir);
		ObjectList<BuildProfile> response = new ObjectList<BuildProfile>();
		
		response.setList(profiles);
		response.setTotalCount(count);
		response.setFilterCount(keyword == null ? count : profileMapper.count(keyword));
		return new ResponseEntity<ObjectList>(response, HttpStatus.OK);
	}

	/**
	 * add BuildProfileGroup
	 * 
	 * @param String name
	 * @return
	 */
//	@RequestMapping(value = "/hasGroup", method = RequestMethod.GET)
//	public ResponseEntity<BuildProfile> hasGroup(@PathVariable String name
//			, @RequestParam(value = "group_name", required = true) String groupName
//			) {
//
//		BuildProfile profile = profileMapper.findOne(name);
//
//		if (profile == null){
//			return new ResponseEntity<BuildProfile>(HttpStatus.NO_CONTENT);
//		}
//
//		return new ResponseEntity<BuildProfile>(profile, HttpStatus.OK);
//	}

	/**
	 * add Has BuildProfileGroup
	 *
	 * @param String name
	 * @return
	 *
	@RequestMapping(value = "/{idx:^[\\d]+$}/hasGroup", method = RequestMethod.POST)
	public ResponseEntity<ArrayList<BuildProfileGroup>> addHasGroup(@PathVariable int idx
			, @RequestParam(value = "grp_idx", required = true) int groupIdx
			) {

		BuildProfile profile = profileMapper.findOne(idx);
		BuildProfileGroup group = groupMapper.findOne(groupIdx);

		if (profile == null || group == null){
			return new ResponseEntity<ArrayList<BuildProfileGroup>>(HttpStatus.NO_CONTENT);
		}

		profile = profileMapper.findOne(idx);
		
		if (profile.getGroups().contains(group)){
			return new ResponseEntity<ArrayList<BuildProfileGroup>>(HttpStatus.CONFLICT);
		}
		
		profileMapper.addHasGroup(idx, groupIdx);
		profile = profileMapper.findOne(idx);
		
		return new ResponseEntity<ArrayList<BuildProfileGroup>>(profile.getGroups(), HttpStatus.OK);
	}

	
	/**
	 * get Has BuildProfileGroups
	 * 
	 * @param idx
	 * @return
	 */
//	@RequestMapping(value = "/{idx:^[\\d]+$}/hasGroup", method = RequestMethod.GET)
//	public ResponseEntity<ArrayList<BuildProfileGroup>> getHasGroup(@PathVariable int idx) {
//
//		BuildProfile profile = profileMapper.findOne(idx);
//
//		if (profile == null || profile.getGroups().size() == 0){
//			return new ResponseEntity<ArrayList<BuildProfileGroup>>(HttpStatus.NO_CONTENT);
//		}
//
//		return new ResponseEntity<ArrayList<BuildProfileGroup>>(profile.getGroups(), HttpStatus.OK);
//	}
	
	/**
	 * delete BuildProfileGroup
	 * 
	 * @param String name
	 * @return
	 */
//	@RequestMapping(value = "/{idx:^[\\d]+$}/hasGroup", method = RequestMethod.DELETE)
//	public ResponseEntity<ArrayList<BuildProfileGroup>> delHasGroup(@PathVariable int idx
//			, @RequestParam(value = "grp_idx", required = true) int groupIdx
//			) {
//
//		BuildProfile profile = profileMapper.findOne(idx);
//
//		if (profile == null){
//			return new ResponseEntity<ArrayList<BuildProfileGroup>>(HttpStatus.NO_CONTENT);
//		}
//
//		profileMapper.delHasGroup(idx, groupIdx);
//
//		profile = profileMapper.findOne(idx);
//
//		return new ResponseEntity<ArrayList<BuildProfileGroup>>(profile.getGroups(), HttpStatus.ACCEPTED);
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
