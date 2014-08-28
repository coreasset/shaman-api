package itwise.coreasset.test;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import itwise.coreasset.shaman.api.config.AppContextConfig;
import itwise.coreasset.shaman.api.config.InitEnvironmentConfig;
import itwise.coreasset.shaman.api.config.WebContextConfig;
import itwise.coreasset.shaman.api.model.ObjectList;
import itwise.coreasset.shaman.api.model.Project;

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

import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {WebContextConfig.class, AppContextConfig.class}, initializers = InitEnvironmentConfig.class)
@WebAppConfiguration
@TransactionConfiguration(transactionManager="transactionManager", defaultRollback = true)
@Transactional
public class ProjectApiTest {

	@Autowired
	private WebApplicationContext context;
	
	private MockMvc mockMvc;
	
	@Before
	public void setup(){
		MockitoAnnotations.initMocks(this);
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.context).build();
	}
	
	@After
	public void tearDown(){
		
	}
	
	
//	@Test
	public void helloOk() throws Exception {
		this.mockMvc.perform(get("/Project/hello"))
			.andExpect(status().isOk())
//			.andExpect(jsonPath("$.name", is(equalTo("Hello ProjectAPI"))))
			;
	}

	
	/**
	 * TODO : URL match가 안될 경우 404 not found 떨어지는 테스트 케이스
	 * 제어권이 controller 밖에 있는 것으로 보여지며 나중에 다시 확인
	 * 
	 * @throws Exception
	 */
//	@Test(expected=IllegalStateException.class)
	public void helloFail() throws Exception {
		this.mockMvc.perform(get("/Project/helloxxxx"))
			.andExpect(status().is4xxClientError())
			;
	}

	
	/**
	 * Project Create
	 * 
	 * @throws Exception
	 */
	@Test
	public void createOk() throws Exception {
		Project project = new Project();
		project.setName("test");
		project.setDescription("test description");

		requestCreate(project);
	}
	
	/**
	 * Duplicate key, 같은 insert 두번 연속
	 * 
	 * @throws Exception
	 */
	@Test
	public void createFail() throws Exception {
		Project project = new Project();
		project.setName("createFail");
		project.setDescription("createFail test description");
		
//		first request
		MvcResult response = requestCreate(project);
		
		String responseBody = response.getResponse().getContentAsString();
		project = new ObjectMapper().readValue(responseBody, Project.class);
		
		
//		second request
		String requestMessage = new ObjectMapper().writeValueAsString(project);
		response = this.mockMvc.perform(post("/Project")
				.content(requestMessage)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().is4xxClientError())
			.andDo(print())
			.andReturn();
	}
	
	/**
	 * Project Update
	 * 
	 * @throws Exception
	 */
	@Test
	public void updateOk() throws Exception {
		Project project = new Project();
		project.setName("updateTest");
		project.setDescription("update test description");
		
		MvcResult response = requestCreate(project);
		
		String responseBody = response.getResponse().getContentAsString();
		project = new ObjectMapper().readValue(responseBody, Project.class);
		
		project.setName("updateTest2");
		String requestMessage = new ObjectMapper().writeValueAsString(project);
		
		this.mockMvc.perform(put("/Project/" + project.getIdx())
				.content(requestMessage)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isCreated())
//			.andExpect(jsonPath("$.name", is("updateTest2")))
			.andDo(print())
			.andReturn();
	}
	
	/**
	 * Project Update Fail
	 * @throws Exception
	 */
	@Test
	public void updateFail() throws Exception {
		Project project = new Project();
		project.setName("updateTest");
		project.setDescription("update test description");
		
		String requestMessage = new ObjectMapper().writeValueAsString(project);
		
		this.mockMvc.perform(put("/Project/0")
				.content(requestMessage)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isNoContent())
			.andDo(print())
			.andReturn();
	}
	
	/**
	 * Project Delete
	 * 
	 * @throws Exception
	 */
	@Test
	public void deleteOk() throws Exception {
		Project project = new Project();
		project.setName("deleteTest");
		project.setDescription("delete test description");
		
		MvcResult response = requestCreate(project);

		String responseBody = response.getResponse().getContentAsString();
		project = new ObjectMapper().readValue(responseBody, Project.class);
		
		// delete by idx
		this.mockMvc.perform(delete("/Project/" + project.getIdx())
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isAccepted())
//			.andDo(print())
			.andReturn();
		
		response = requestCreate(project);
		
		// delete by name
		this.mockMvc.perform(delete("/Project/" + project.getName())
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isAccepted())
//			.andDo(print())
			.andReturn();
	}
	

	/**
	 * Project Delete Fail
	 * Response Http Status 4xx
	 * 
	 * @throws Exception
	 */
	@Test
	public void deleteFail() throws Exception {
		this.mockMvc.perform(delete("/Project/0")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isNoContent())
			.andDo(print())
			.andReturn();
	}

	
	/**
	 * Project list
	 * 
	 * @throws Exception
	 */
	@Test
	public void findList() throws Exception{
		
//		init data
		Project project = new Project();
		project.setDescription("list test description");

		for (int i = 0; i < 20; i++) {
			project.setName("listTest-" + i);
			requestCreate(project);
		}

//		request with no params
		MvcResult response = this.mockMvc.perform(get("/Project")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andDo(print())
			.andReturn();
		String responseBody = response.getResponse().getContentAsString();
		ObjectList<Project> list = new ObjectMapper().readValue(responseBody, ObjectList.class);
		assertThat("list count", list.getList().size(), is(10));
		
		response = this.mockMvc.perform(get("/Project")
				.param("page", "2")
				.param("limit", "20")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andDo(print())
			.andReturn();
		
//		request with params
		responseBody = response.getResponse().getContentAsString();
		list = new ObjectMapper().readValue(responseBody, ObjectList.class);
		assertThat("list count", list.getList().size(), is(20));

	}
	
	
	private MvcResult requestCreate(Project project) throws Exception {
		String requestMessage = new ObjectMapper().writeValueAsString(project);
		
		MvcResult response = this.mockMvc.perform(post("/Project")
				.content(requestMessage)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.idx", greaterThan(0)))
			.andDo(print())
			.andReturn();
		return response;
	}
	
}
