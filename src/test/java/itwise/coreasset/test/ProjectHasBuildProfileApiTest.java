package itwise.coreasset.test;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import itwise.coreasset.shaman.api.config.AppContextConfig;
import itwise.coreasset.shaman.api.config.InitEnvironmentConfig;
import itwise.coreasset.shaman.api.config.WebContextConfig;
import itwise.coreasset.shaman.api.model.BuildProfile;
import itwise.coreasset.shaman.api.model.Project;

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

import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {WebContextConfig.class, AppContextConfig.class}, initializers = InitEnvironmentConfig.class)
@WebAppConfiguration
@TransactionConfiguration(transactionManager="transactionManager", defaultRollback = true)
@Transactional
public class ProjectHasBuildProfileApiTest {

	@Autowired
	private WebApplicationContext context;

	private UnitTest4RestAPIClient<BuildProfile> profileClient;
	private UnitTest4RestAPIClient<Project> projectClient;
	
	private BuildProfile profile;
	private Project project;
	
	@Before
	public void setup(){
//		TODO: eclipse에서만 되는건지 jenkins 같은 다른 플랫폼에서도 되는지 확인 해야 함
		Boolean isDebug = (java.lang.management.ManagementFactory.getRuntimeMXBean().
				getInputArguments().toString().indexOf("-agentlib:jdwp") > 0);
		
		profileClient = new UnitTest4RestAPIClient<BuildProfile>(context);
		profileClient.setIsPrint(isDebug);
		profileClient.setDefaultURI("/BuildProfile");
		profileClient.setDefaultClass(BuildProfile.class);
		
		projectClient = new UnitTest4RestAPIClient<Project>(context);
		projectClient.setIsPrint(isDebug);
		projectClient.setDefaultURI("/Project");
		projectClient.setDefaultClass(Project.class);
		
		profile = new BuildProfile();
		profile.setDescription("test description");
		profile.setFlavor("ant");
		profile.setGoal("install");
		
		project = new Project();
		project.setDescription("test description");
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

	}
	
	/**
	 * static test
	 * 
	 * @throws Exception
	 */
//	@Test
	public void testTmp() throws Exception{
	}
	
	@Test
	public void testHasBuildProfile() throws Exception{
		String methodName = new Object(){}.getClass().getEnclosingMethod().getName();
		ObjectMapper mapper = new ObjectMapper();
		
//		init BuildProfile
		BuildProfile profile = (BuildProfile) SerializationUtils.clone(this.profile);
		profile.setName(methodName + "-profile");
		profile = profileClient.requestCreate(profile);
		
//		init Project
		Project project = (Project) SerializationUtils.clone(this.project);
		project.setName(methodName + "-project");

//		first has set BuildProfile
		project.setBuildProfile(profile);
		project = projectClient.requestCreate(project);
		
		assertThat(mapper.writeValueAsString(project.getBuildProfile()), is(mapper.writeValueAsString(profile)));
		
//		has unset BuildProfile
		project.setBuildProfile(null);
		project = projectClient.requestUpdate(project, project.getIdx().toString());
		assertTrue(project.getBuildProfile() == null);

//		second has set BuildProfile
		project.setBuildProfile(profile);
		project = projectClient.requestUpdate(project, project.getIdx().toString());
		assertThat(mapper.writeValueAsString(project.getBuildProfile()), is(mapper.writeValueAsString(profile)));
	}
}
