package itwise.coreasset.test;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import itwise.coreasset.shaman.api.config.AppContextConfig;
import itwise.coreasset.shaman.api.config.InitEnvironmentConfig;
import itwise.coreasset.shaman.api.config.WebContextConfig;
import itwise.coreasset.shaman.api.model.Project;
import itwise.coreasset.shaman.api.model.ProjectGroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {WebContextConfig.class, AppContextConfig.class}, initializers = InitEnvironmentConfig.class)
@WebAppConfiguration
@TransactionConfiguration(transactionManager="transactionManager", defaultRollback = true)
@Transactional
public class ProjectGroupHasProjectApiTest {

	@Autowired
	private WebApplicationContext context;
	
	private MockMvc mockMvc;
	private ObjectMapper mapper;
	
	@Before
	public void setup(){
		MockitoAnnotations.initMocks(this);
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.context).build();
		
		mapper = new ObjectMapper();
		assertThat(0,is(0));
	}
	
	@After
	public void tearDown(){
		
	}
	
	/**
	 * static test
	 * 
	 * @throws Exception
	 */
//	@Test
	public void testTmp() throws Exception{
		MvcResult response;
		response = this.mockMvc.perform(
				get("/Project/4")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
			)
			.andExpect(status().isOk())
			.andDo(print())
			.andReturn();
		
		response = this.mockMvc.perform(
				get("/Project/4/hasGroup")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
			)
			.andExpect(status().isOk())
			.andDo(print())
			.andReturn();
	}
	
	@Test
	public void testHasProject() throws Exception{
		
		HashMap<String, String> params = new HashMap<String, String>();
		
//		init Project
		Project project1 = new Project();
		project1.setName("testHasProject-project1");
		project1 = requestCreate(project1, "/Project");

		Project project2 = new Project();
		project2.setName("testHasProject-project2");
		project2 = requestCreate(project2, "/Project");
		
//		init Group
		ProjectGroup group = new ProjectGroup();
		group.setName("testHasProject-group");
		group.setDescription("test description");
		group = requestCreate(group, "/ProjectGroup");
		
//		case 1
//		TODO: refactoring
//		first add has Project
		MvcResult response;
		response = this.mockMvc.perform(
				post("/ProjectGroup/" + group.getIdx() + "/hasProject")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.param("prj_idx", project1.getIdx().toString())
			)
			.andExpect(status().isOk())
			.andDo(print())
			.andReturn();

//		second add has Project
		response = this.mockMvc.perform(
				post("/ProjectGroup/" + group.getIdx() + "/hasProject")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.param("prj_idx", project2.getIdx().toString())
			)
			.andExpect(status().isOk())
			.andDo(print())
			.andReturn();
		
		ArrayList<Project> projects = mapper.readValue(response.getResponse().getContentAsString()
				, new TypeReference<ArrayList<Project>>(){});
		assertThat(projects.size(),is(2));
		assertThat(mapper.writeValueAsString(project1), is(mapper.writeValueAsString(projects.get(0))));
		assertThat(mapper.writeValueAsString(project2), is(mapper.writeValueAsString(projects.get(1))));
		
		
//		get has project
		ArrayList<Project> projects2 = requestGetList(Project.class, "/ProjectGroup/" + group.getIdx() + "/hasProject");
		assertThat(mapper.writeValueAsString(projects2), is(mapper.writeValueAsString(projects)));

//		first del hasProject
		params.clear();
		params.put("prj_idx", project1.getIdx().toString());
		requestDelete(Project.class, "/ProjectGroup/" + group.getIdx() + "/hasProject", params);
		ArrayList<Project> projects3 = requestGetList(Project.class, "/ProjectGroup/" + group.getIdx() + "/hasProject");
		assertThat(projects3.size(), is(1));
		
//		second del hasGroup
		params.clear();
		params.put("prj_idx", project2.getIdx().toString());
		requestDelete(Project.class, "/ProjectGroup/" + group.getIdx() + "/hasProject", params);
		ArrayList<Project> projects4 = requestGetList(Project.class, "/ProjectGroup/" + group.getIdx() + "/hasProject");
		assertThat(projects4.size(), is(0));
		
//		case 2
//		ArrayList<ProjectGroup> groups = new ArrayList<ProjectGroup>();
//		groups.add(group);
//		groups.add(group2);
//		project.setGroups(groups);
	}
	
	@Test
	public void testHasProjectReset() throws Exception {

//		init Project
		Project project01 = new Project();
		project01.setName("testHasProjectReset-project01");
		project01.setDescription("test description");
		project01 = requestCreate(project01, "/Project");
		
		Project project02 = new Project();
		project02.setName("testHasProjectReset-project02");
		project02.setDescription("test description");
		project02 = requestCreate(project02, "/Project");

//		init Group
		ProjectGroup group = new ProjectGroup();
		group.setName("testHasProjectGroupReset-project");
		ArrayList<Project> projects = new ArrayList<Project>();
		projects.add(project01);
		projects.add(project02);
		group.setProjects(projects);
		
//		create new Group
		group = requestCreate(group, "/ProjectGroup");
		
//		get hasProject
		projects = requestGetList(Project.class, "/ProjectGroup/" + group.getIdx() + "/hasProject");
		assertThat(group.getProjects().size(), is(2));

//		remove one & update
		projects.remove(0);
		group.setProjects(projects);
		group = requestUpdate(group, "/ProjectGroup/" + group.getIdx());

		projects = requestGetList(Project.class, "/ProjectGroup/" + group.getIdx() + "/hasProject");
		assertThat(group.getProjects().size(), is(1));
		
//		add one & update
		projects.add(project01);
		group.setProjects(projects);
		group = requestUpdate(group, "/ProjectGroup/" + group.getIdx());
		
		projects = requestGetList(Project.class, "/ProjectGroup/" + group.getIdx() + "/hasProject");
		assertThat(group.getProjects().size(), is(2));
	}
	
	
	private <T> T requestCreate(T obj, String uri) throws Exception {
		String requestMessage = new ObjectMapper().writeValueAsString(obj);
		
		MvcResult response = this.mockMvc.perform(post(uri)
				.content(requestMessage)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.idx", greaterThan(0)))
			.andDo(print())
			.andReturn();
		
		String responseBody = response.getResponse().getContentAsString();
		obj = (T) new ObjectMapper().readValue(responseBody, obj.getClass());
		
		return obj;
	}

	private <T> T requestUpdate(T obj, String uri) throws Exception {
		String requestMessage = new ObjectMapper().writeValueAsString(obj);
		
		MvcResult response = this.mockMvc.perform(put(uri)
				.content(requestMessage)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isCreated())
			.andDo(print())
			.andReturn();
		
		String responseBody = response.getResponse().getContentAsString();
		obj = (T) new ObjectMapper().readValue(responseBody, obj.getClass());
		
		return obj;
	}

	private <T> void requestDelete(Class<T> cls, String uri) throws Exception {
		requestDelete(cls, uri, new HashMap<String, String>());
	}
	
	private <T> void requestDelete(Class<T> cls, String uri, HashMap<String, String> params) throws Exception {

		MockHttpServletRequestBuilder requestBuilder = delete(uri)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON);
		
		
		for (Entry<String, String> element : params.entrySet()){
			requestBuilder.param(element.getKey(), element.getValue());
		}
		
		this.mockMvc.perform(requestBuilder)
			.andExpect(status().isAccepted())
			.andDo(print())
			.andReturn();
	}
	
	private <T> T requestGet(Class<T> cls, String uri) throws Exception {
		
		MvcResult response = this.mockMvc.perform(get(uri)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andDo(print())
			.andReturn();
		
		String responseBody = response.getResponse().getContentAsString();
		
		return new ObjectMapper().readValue(responseBody, cls);
	}
	
	private <T> ArrayList<T> requestGetList(Class<T> cls, String uri) throws Exception {
		
		MvcResult response = this.mockMvc.perform(get(uri)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().is2xxSuccessful())
			.andDo(print())
			.andReturn();
		
		String responseBody = response.getResponse().getContentAsString();
		
		if(response.getResponse().getStatus() == HttpStatus.NO_CONTENT.value()) {
			return new ArrayList<T>();
		} else {
			return new ObjectMapper().readValue(responseBody,  mapper.getTypeFactory().constructCollectionType(ArrayList.class, cls));
		}
	}
}
