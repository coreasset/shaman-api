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

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
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
public class ProjectHasGroupApiTest {

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
	 * Project Create
	 * 
	 * @throws Exception
	 */
//	@Test
	public void createOk() throws Exception {
		Project project = new Project();
		project.setName("test");
		project.setDescription("test description");
		project = requestCreate(project, "/Project");
		
		ProjectGroup projectGroup = new ProjectGroup();
		projectGroup.setName("test");
		projectGroup.setDescription("test description");

		requestCreate(projectGroup, "/ProjectGroup");
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
	public void testHasProjectGroup() throws Exception{
//		init Project
		Project project = new Project();
		project.setName("hasGroupTest");
		project = requestCreate(project, "/Project");

//		init Group
		ProjectGroup group = new ProjectGroup();
		group.setName("hasGroupTest-01");
		group.setDescription("test description");
		group = requestCreate(group, "/ProjectGroup");
		
		ProjectGroup group2 = new ProjectGroup();
		group2.setName("hasGroupTest-02");
		group2.setDescription("test description");
		group2 = requestCreate(group2, "/ProjectGroup");
		
//		case 1
//		first add has group
		MvcResult response;
		response = this.mockMvc.perform(
				post("/Project/" + project.getIdx() + "/hasGroup")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.param("grp_idx", group.getIdx().toString())
			)
			.andExpect(status().isOk())
			.andDo(print())
			.andReturn();

//		second add has group
		response = this.mockMvc.perform(
				post("/Project/" + project.getIdx() + "/hasGroup")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.param("grp_idx", group2.getIdx().toString())
			)
			.andExpect(status().isOk())
			.andDo(print())
			.andReturn();
		
		ArrayList<ProjectGroup> groups = new ObjectMapper().readValue(response.getResponse().getContentAsString()
				, new TypeReference<ArrayList<ProjectGroup>>(){});
		assertThat(groups.size(),is(2));
		assertThat(mapper.writeValueAsString(group), is(mapper.writeValueAsString(groups.get(0))));
		assertThat(mapper.writeValueAsString(group2), is(mapper.writeValueAsString(groups.get(1))));
		
		
//		get has group
		response = this.mockMvc.perform(get("/Project/" + project.getIdx() + "/hasGroup")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andDo(print())
			.andReturn();
		
		ArrayList<ProjectGroup> groups2 = new ObjectMapper().readValue(response.getResponse().getContentAsString()
				, new TypeReference<ArrayList<ProjectGroup>>(){});
		assertThat(mapper.writeValueAsString(groups2), is(mapper.writeValueAsString(groups)));

//		del has group
		response = this.mockMvc.perform(delete("/Project/" + project.getIdx() + "/hasGroup")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.param("grp_idx", group.getIdx().toString())
			)
			.andExpect(status().isAccepted())
			.andDo(print())
			.andReturn();
		
		ArrayList<ProjectGroup> groups3= mapper.readValue(response.getResponse().getContentAsString()
				, new TypeReference<ArrayList<ProjectGroup>>(){});
		assertThat(groups3.size(), is(1));
		
		response = this.mockMvc.perform(delete("/Project/" + project.getIdx() + "/hasGroup")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.param("grp_idx", group2.getIdx().toString())
			)
			.andExpect(status().isAccepted())
			.andDo(print())
			.andReturn();

		ArrayList<ProjectGroup> groups4 = new ObjectMapper().readValue(response.getResponse().getContentAsString()
				, new TypeReference<ArrayList<ProjectGroup>>(){});
		assertThat(groups4.size(), is(0));
		
//		case 2
//		ArrayList<ProjectGroup> groups = new ArrayList<ProjectGroup>();
//		groups.add(group);
//		groups.add(group2);
//		project.setGroups(groups);
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
}
