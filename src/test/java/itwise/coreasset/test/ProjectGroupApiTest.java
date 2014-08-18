package itwise.coreasset.test;

import itwise.coreasset.shaman.api.config.AppContextConfig;
import itwise.coreasset.shaman.api.config.InitEnvironmentConfig;
import itwise.coreasset.shaman.api.config.WebContextConfig;
import itwise.coreasset.shaman.api.model.ProjectGroup;

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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {WebContextConfig.class, AppContextConfig.class}, initializers = InitEnvironmentConfig.class)
@WebAppConfiguration
@TransactionConfiguration(transactionManager="transactionManager", defaultRollback = true)
@Transactional
public class ProjectGroupApiTest {

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
		this.mockMvc.perform(get("/ProjectGroup/hello"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.name", is(equalTo("Hello ProjectGroupAPI"))))
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
		this.mockMvc.perform(get("/ProjectGroup/helloxxxx"))
			.andExpect(status().is4xxClientError())
			;
	}

	
	/**
	 * ProjectGroup Create
	 * 
	 * @throws Exception
	 */
	@Test
	public void createOk() throws Exception {
		ProjectGroup projectGroup = new ProjectGroup();
		projectGroup.setName("test");
		projectGroup.setDescription("test description");

		requestCreate(projectGroup);
	}
	
	/**
	 * Duplicate key, 같은 insert 두번 연속
	 * 
	 * @throws Exception
	 */
	@Test
	public void createFail() throws Exception {
		ProjectGroup projectGroup = new ProjectGroup();
		projectGroup.setName("createFail");
		projectGroup.setDescription("createFail test description");
		
//		first request
		MvcResult response = requestCreate(projectGroup);
		
		String responseBody = response.getResponse().getContentAsString();
		projectGroup = new ObjectMapper().readValue(responseBody, ProjectGroup.class);
		
		
//		second request
		String requestMessage = new ObjectMapper().writeValueAsString(projectGroup);
		response = this.mockMvc.perform(post("/ProjectGroup")
				.content(requestMessage)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().is4xxClientError())
			.andDo(print())
			.andReturn();
	}
	
	/**
	 * ProjectGroup Update
	 * 
	 * @throws Exception
	 */
	@Test
	public void updateOk() throws Exception {
		ProjectGroup projectGroup = new ProjectGroup();
		projectGroup.setName("updateTest");
		projectGroup.setDescription("update test description");
		
		MvcResult response = requestCreate(projectGroup);
		
		String responseBody = response.getResponse().getContentAsString();
		projectGroup = new ObjectMapper().readValue(responseBody, ProjectGroup.class);
		
		projectGroup.setName("updateTest2");
		String requestMessage = new ObjectMapper().writeValueAsString(projectGroup);
		
		this.mockMvc.perform(put("/ProjectGroup/" + projectGroup.getIdx())
				.content(requestMessage)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.name", is("updateTest2")))
			.andDo(print())
			.andReturn();
	}
	
	/**
	 * ProjectGroup Update Fail
	 * @throws Exception
	 */
	@Test
	public void updateFail() throws Exception {
		ProjectGroup projectGroup = new ProjectGroup();
		projectGroup.setName("updateTest");
		projectGroup.setDescription("update test description");
		
		String requestMessage = new ObjectMapper().writeValueAsString(projectGroup);
		
		this.mockMvc.perform(put("/ProjectGroup/0")
				.content(requestMessage)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().is4xxClientError())
			.andDo(print())
			.andReturn();
	}
	
	/**
	 * ProjectGroup Delete
	 * 
	 * @throws Exception
	 */
	@Test
	public void deleteOk() throws Exception {
		ProjectGroup projectGroup = new ProjectGroup();
		projectGroup.setName("deleteTest");
		projectGroup.setDescription("delete test description");
		
		MvcResult response = requestCreate(projectGroup);

		String responseBody = response.getResponse().getContentAsString();
		projectGroup = new ObjectMapper().readValue(responseBody, ProjectGroup.class);
		
		this.mockMvc.perform(delete("/ProjectGroup/" + projectGroup.getIdx())
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
//			.andDo(print())
			.andReturn();
	}
	

	/**
	 * ProjectGroup Delete Fail
	 * Response Http Status 4xx
	 * 
	 * @throws Exception
	 */
	@Test
	public void deleteFail() throws Exception {
		this.mockMvc.perform(delete("/ProjectGroup/0")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().is4xxClientError())
			.andDo(print())
			.andReturn();
	}

	private MvcResult requestCreate(ProjectGroup projectGroup) throws Exception {
		String requestMessage = new ObjectMapper().writeValueAsString(projectGroup);
		
		MvcResult response = this.mockMvc.perform(post("/ProjectGroup")
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
