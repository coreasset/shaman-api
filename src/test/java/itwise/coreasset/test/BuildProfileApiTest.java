package itwise.coreasset.test;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import itwise.coreasset.shaman.api.config.AppContextConfig;
import itwise.coreasset.shaman.api.config.InitEnvironmentConfig;
import itwise.coreasset.shaman.api.config.WebContextConfig;
import itwise.coreasset.shaman.api.model.BuildProfile;
import itwise.coreasset.shaman.api.model.ObjectList;

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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {WebContextConfig.class, AppContextConfig.class}, initializers = InitEnvironmentConfig.class)
@WebAppConfiguration
@TransactionConfiguration(transactionManager="transactionManager", defaultRollback = true)
@Transactional
public class BuildProfileApiTest {

	@Autowired
	private WebApplicationContext context;
	
	private UnitTest4RestAPIClient<BuildProfile> client;

	private BuildProfile profile;
	
	@Before
	public void setup(){
		client = new UnitTest4RestAPIClient<BuildProfile>(context);
		
//		TODO: eclipse에서만 되는건지 jenkins 같은 다른 플랫폼에서도 되는지 확인 해야 함
		client.setIsPrint(java.lang.management.ManagementFactory.getRuntimeMXBean().
				getInputArguments().toString().indexOf("-agentlib:jdwp") > 0);
		
		client.setDefaultURI("/BuildProfile");
		client.setDefaultClass(BuildProfile.class);

		profile = new BuildProfile();
		profile.setDescription("test description");
		profile.setFlavor("ant");
		profile.setGoal("install");
		profile.setParam("-DskipTest");
	}
	
	@After
	public void tearDown(){
		
	}
	
	
	/**
	 * BuildProfile Create
	 * 
	 * @throws Exception
	 */
	@Test
	public void createOk() throws Exception {
		BuildProfile profile = (BuildProfile) SerializationUtils.clone(this.profile);
		profile.setName(this.getClass() + "createOk-test");
		
		client.requestCreate(profile);
	}
	
	/**
	 * Duplicate key, 같은 insert 두번 연속
	 * 
	 * @throws Exception
	 */
	@Test
	public void createFail() throws Exception {
		BuildProfile profile = (BuildProfile) SerializationUtils.clone(this.profile);
		profile.setName(this.getClass() + "-createFail");
		
//		first request
		client.requestCreate(profile);
		
//		second same request
		client.requestCreate(profile, status().is4xxClientError());
	}
	
	/**
	 * BuildProfile Update
	 * 
	 * @throws Exception
	 */
	@Test
	public void updateOk() throws Exception {
		BuildProfile profile = (BuildProfile) SerializationUtils.clone(this.profile);
		profile.setName(this.getClass() + "-updateTest");
		
//		Create
		profile = client.requestCreate(profile);
		
//		Update
		profile.setName(this.getClass() + "-updateTest2");
		client.requestUpdate(profile, profile.getIdx().toString());
	}
	
	/**
	 * BuildProfile Update Fail
	 * @throws Exception
	 */
	@Test
	public void updateFail() throws Exception {
		BuildProfile profile = (BuildProfile) SerializationUtils.clone(this.profile);
		profile.setName(this.getClass() + "-updateFail");
		
		client.requestUpdate(profile, "0", status().isNoContent());
	}
	
	/**
	 * BuildProfile Delete
	 * 
	 * @throws Exception
	 */
	@Test
	public void deleteOk() throws Exception {
		BuildProfile profile = (BuildProfile) SerializationUtils.clone(this.profile);
		profile.setName(this.getClass() + "-deleteTest");

		profile = client.requestCreate(profile);
		
		// delete by idx
		client.requestDelete(profile.getIdx().toString());

		// delete by name
		client.requestCreate(profile);
		client.requestDelete(profile.getName());
	}
	

	/**
	 * BuildProfile Delete Fail
	 * Response Http Status expect No Content
	 * 
	 * @throws Exception
	 */
	@Test
	public void deleteFail() throws Exception {
		client.requestDelete("0", status().isNoContent());
	}

	
	/**
	 * BuildProfile list
	 * 
	 * @throws Exception
	 */
	@Test
	public void findList() throws Exception{
//		init data
		BuildProfile profile = (BuildProfile) SerializationUtils.clone(this.profile);
		profile.setDescription(this.getClass() + "-list test description");

//		20개 insert
		for (int i = 0; i < 20; i++) {
			profile.setName(this.getClass() + "-listTest-" + i);
			client.requestCreate(profile);
		}

		//no param fisrt page
		ObjectList<BuildProfile> list = client.requestGetList();
		assertThat("list count", list.getList().size(), is(10));
		
		
		//2 page, limit 20
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("page", "2");
		params.put("limit", "20");
		list = client.requestGetList(params);
		
		assertThat("list count", list.getList().size(), is(20));
	}
	
	@Test
	public void testTmp() throws Exception {
	}
}
