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

import org.apache.commons.lang.SerializationUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {WebContextConfig.class, AppContextConfig.class}, initializers = InitEnvironmentConfig.class)
@WebAppConfiguration
@TransactionConfiguration(transactionManager="transactionManager", defaultRollback = true)
@Transactional
public class ProjectHasGroupApiTest {

	@Autowired
	private WebApplicationContext context;
	
	private UnitTest4RestAPIClient<ProjectGroup> groupClient;
	private UnitTest4RestAPIClient<Project> projectClient;
	
	private ProjectGroup group;
	private Project project;
	
	private MockMvc mockMvc;
	
	@Before
	public void setup(){
//		TODO: eclipse에서만 되는건지 jenkins 같은 다른 플랫폼에서도 되는지 확인 해야 함
		Boolean isDebug = (java.lang.management.ManagementFactory.getRuntimeMXBean().
				getInputArguments().toString().indexOf("-agentlib:jdwp") > 0);
		
		groupClient = new UnitTest4RestAPIClient<ProjectGroup>(context);
		groupClient.setIsPrint(isDebug);
		groupClient.setDefaultURI("/ProjectGroup");
		groupClient.setDefaultClass(ProjectGroup.class);
		
		projectClient = new UnitTest4RestAPIClient<Project>(context);
		projectClient.setIsPrint(isDebug);
		projectClient.setDefaultURI("/Project");
		projectClient.setDefaultClass(Project.class);
		
		group = new ProjectGroup();
		group.setDescription("test description");
		
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
		String methodName = new Object(){}.getClass().getEnclosingMethod().getName();
		Project project = (Project) SerializationUtils.clone(this.project);
		project.setName(methodName + "-project");
		projectClient.requestCreate(project);
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
		String methodName = new Object(){}.getClass().getEnclosingMethod().getName();
		
//		init Project
		Project project = (Project) SerializationUtils.clone(this.project);
		project.setName(methodName + "-project01");
		project = projectClient.requestCreate(project);
		
//		init Group
		ProjectGroup group01 = (ProjectGroup) SerializationUtils.clone(this.group);
		group01.setName(methodName + "-group01");
		group01 = groupClient.requestCreate(group01);
		
		ProjectGroup group02 = (ProjectGroup) SerializationUtils.clone(this.group);
		group02.setName(methodName + "-group02");
		group02 = groupClient.requestCreate(group02);
		
		String uri = String.format("%s/%s/hasGroup", projectClient.getDefaultURI(), project.getIdx());
		ArrayList<ProjectGroup> groups01;
		ArrayList<ProjectGroup> groups02;
		HashMap<String, String> params = new HashMap<String, String>();
		Class<? extends ArrayList> clazz = new ArrayList<ProjectGroup>().getClass();
		
		//has group01
		params.put("grp_idx", group01.getIdx().toString());
		groups01 = projectClient.requestPost(clazz, uri, params);
		assertThat(groups01.size(), is(1));

		//has group02
		params.clear();
		params.put("grp_idx", group02.getIdx().toString());
		groups01 = projectClient.requestPost(clazz, uri, params);
		assertThat(groups01.size(), is(2));

		//get hasProjectGroup
		groups02 = projectClient.requestGet(clazz, uri);
		
		//compare group01 and group02
		ObjectMapper mapper = new ObjectMapper();
		assertThat(mapper.writeValueAsString(groups02), is(mapper.writeValueAsString(groups01)));
		
//		first del hasProjectGroup
		params.clear();
		params.put("grp_idx", group01.getIdx().toString());
		projectClient.requestDelete(clazz, uri, params);

		groups01 = projectClient.requestGet(clazz, uri);
		assertThat(groups01.size(), is(1));
		
//		second del hasProjectGroup
		params.clear();
		params.put("grp_idx", group02.getIdx().toString());
		groups01 = projectClient.requestDelete(clazz, uri, params);

		projectClient.requestGet(clazz, uri, status().isNoContent());
	}
	
	@Test
	public void testHasProjectGroupReset() throws Exception {
		String methodName = new Object(){}.getClass().getEnclosingMethod().getName();
//		init ProjectGroup
		ProjectGroup group01 = (ProjectGroup) SerializationUtils.clone(this.group);
		group01.setName(methodName + "-group01");
		group01 = groupClient.requestCreate(group01);

		ProjectGroup group02 = (ProjectGroup) SerializationUtils.clone(this.group);
		group02.setName(methodName + "-group02");
		group02 = groupClient.requestCreate(group02);
		
//		init Project
		Project project = (Project) SerializationUtils.clone(this.project);
		project.setName(methodName + "-group");
		ArrayList<ProjectGroup> groups = new ArrayList<ProjectGroup>();
		groups.add(group01);
		groups.add(group02);
		project.setGroups(groups);
		
//		create new Group
		project = projectClient.requestCreate(project);
		
		String uri = String.format("%s/%s/hasGroup", projectClient.getDefaultURI(), project.getIdx());
		HashMap<String, String> params = new HashMap<String, String>();
		Class<? extends ArrayList> clazz = new ArrayList<ProjectGroup>().getClass();

//		get hasGroup
		groups = projectClient.requestGet(clazz, uri);
		assertThat(groups.size(), is(2));
		
//		remove one & update
		groups.remove(0);
		project.setGroups(groups);
		projectClient.requestUpdate(project, project.getIdx().toString());
		groups = projectClient.requestGet(clazz, uri);
		assertThat(groups.size(), is(1));
		
		
//		add one & update
		groups.add(group01);
		project.setGroups(groups);
		projectClient.requestUpdate(project, project.getIdx().toString());
		groups = projectClient.requestGet(clazz, uri);
		assertThat(groups.size(), is(2));

//		remove all
		groups.clear();
		project.setGroups(groups);
		projectClient.requestUpdate(project, project.getIdx().toString());
		groups = projectClient.requestGet(clazz, uri, status().isNoContent());
	}
}
