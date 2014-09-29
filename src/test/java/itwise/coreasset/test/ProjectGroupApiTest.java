package itwise.coreasset.test;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import itwise.coreasset.shaman.api.config.AppContextConfig;
import itwise.coreasset.shaman.api.config.InitEnvironmentConfig;
import itwise.coreasset.shaman.api.config.WebContextConfig;
import itwise.coreasset.shaman.api.model.ObjectList;
import itwise.coreasset.shaman.api.model.ProjectGroup;

import java.util.HashMap;

import org.apache.commons.lang.SerializationUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {WebContextConfig.class, AppContextConfig.class}, initializers = InitEnvironmentConfig.class)
@WebAppConfiguration
@TransactionConfiguration(transactionManager="transactionManager", defaultRollback = true)
@Transactional
public class ProjectGroupApiTest {

	@Autowired
	private WebApplicationContext context;
	
	private MockMvc mockMvc;
	
	private UnitTest4RestAPIClient<ProjectGroup> client;
	
	private ProjectGroup group;
	
	@Before
	public void setup(){
		client = new UnitTest4RestAPIClient<ProjectGroup>(context);
		
//		TODO: eclipse에서만 되는건지 jenkins 같은 다른 플랫폼에서도 되는지 확인 해야 함
		client.setIsPrint(java.lang.management.ManagementFactory.getRuntimeMXBean().
				getInputArguments().toString().indexOf("-agentlib:jdwp") > 0);
		
		client.setDefaultURI("/ProjectGroup");
		client.setDefaultClass(ProjectGroup.class);

		group = new ProjectGroup();
		group.setDescription("test description");
	}
	
	@After
	public void tearDown(){
		
	}
	
	
//	@Test
	public void helloOk() throws Exception {
		this.mockMvc.perform(get("/ProjectGroup/hello"))
			.andExpect(status().isOk())
//			.andExpect(jsonPath("$.name", is(equalTo("Hello ProjectGroupAPI"))))
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
		ProjectGroup group = (ProjectGroup) SerializationUtils.clone(this.group);
		group.setName(this.getClass() + "createOk-test");
		
		client.requestCreate(group);
	}
	
	/**
	 * Duplicate key, 같은 insert 두번 연속
	 * 
	 * @throws Exception
	 */
	@Test
	public void createFail() throws Exception {
		ProjectGroup group = (ProjectGroup) SerializationUtils.clone(this.group);
		group.setName(this.getClass() + "-createFail");
		
//		first request
		client.requestCreate(group);
		
//		second same request
		client.requestCreate(group, status().is4xxClientError());
	}
	
	/**
	 * ProjectGroup Update
	 * 
	 * @throws Exception
	 */
	@Test
	public void updateOk() throws Exception {
		ProjectGroup group = (ProjectGroup) SerializationUtils.clone(this.group);
		group.setName(this.getClass() + "-updateTest");
		
//		Create
		group = client.requestCreate(group);
		
//		Update
		group.setName(this.getClass() + "-updateTest2");
		client.requestUpdate(group, group.getIdx().toString());
	}
	
	/**
	 * ProjectGroup Update Fail
	 * @throws Exception
	 */
	@Test
	public void updateFail() throws Exception {
		ProjectGroup group = (ProjectGroup) SerializationUtils.clone(this.group);
		group.setName(this.getClass() + "-updateFail");
		
		client.requestUpdate(group, "0", status().isNoContent());
	}
	
	/**
	 * ProjectGroup Delete
	 * 
	 * @throws Exception
	 */
	@Test
	public void deleteOk() throws Exception {
		ProjectGroup group = (ProjectGroup) SerializationUtils.clone(this.group);
		group.setName(this.getClass() + "-deleteTest");

		group = client.requestCreate(group);
		
		// delete by idx
		client.requestDelete(group.getIdx().toString());

		// delete by name
		client.requestCreate(group);
		client.requestDelete(group.getName());
	}
	

	/**
	 * ProjectGroup Delete Fail
	 * Response Http Status 4xx
	 * 
	 * @throws Exception
	 */
	@Test
	public void deleteFail() throws Exception {
		client.requestDelete("0", status().isNoContent());
	}

	
	/**
	 * ProjectGroup list
	 * 
	 * @throws Exception
	 */
	@Test
	public void findList() throws Exception{
//		init data
		ProjectGroup group = (ProjectGroup) SerializationUtils.clone(this.group);
		group.setDescription(this.getClass() + "-list test description");

//		20개 insert
		for (int i = 0; i < 20; i++) {
			group.setName(this.getClass() + "-listTest-" + i);
			client.requestCreate(group);
		}

		//no param fisrt page
		ObjectList<ProjectGroup> list = client.requestGetList();
		assertThat("list count", list.getList().size(), is(10));
		
		
		//2 page, limit 20
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("page", "2");
		params.put("limit", "20");
		list = client.requestGetList(params);
		
		assertThat("list count", list.getList().size(), is(20));
	}
}
