package itwise.coreasset.test;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
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
public class ProjectGroupHasProjectApiTest {

	@Autowired
	private WebApplicationContext context;
	
	private UnitTest4RestAPIClient<ProjectGroup> groupClient;
	private UnitTest4RestAPIClient<Project> projectClient;
	
	private ProjectGroup group;
	private Project project;
	
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
	}
	
	@After
	public void tearDown(){
		
	}
	
	@Test
	public void testHasProject() throws Exception{
		String methodName = new Object(){}.getClass().getEnclosingMethod().getName();
		
//		init Project
		Project project01 = (Project) SerializationUtils.clone(this.project);
		project01.setName(methodName + "-project01");
		project01 = projectClient.requestCreate(project01);

		Project project02 = (Project) SerializationUtils.clone(this.project);
		project02.setName(methodName + "-project02");
		project02 = projectClient.requestCreate(project02);
		
//		init Group
		ProjectGroup group = (ProjectGroup) SerializationUtils.clone(this.group);
		group.setName(methodName + "-group");
		group = groupClient.requestCreate(group);
		
		String uri = String.format("%s/%s/hasProject", groupClient.getDefaultURI(), group.getIdx());
		ArrayList<Project> projects01;
		ArrayList<Project> projects02;
		HashMap<String, String> params = new HashMap<String, String>();
		Class<? extends ArrayList> clazz = new ArrayList<Project>().getClass();

		//has project01
		params.put("prj_idx", project01.getIdx().toString());
		projects01 = groupClient.requestPost(clazz, uri, params);
		assertThat(projects01.size(), is(1));

//		has project02
		params.clear();
		params.put("prj_idx", project02.getIdx().toString());
		projects01 = groupClient.requestPost(clazz, uri, params);
		assertThat(projects01.size(), is(2));

//		get hasProject
		projects02 = groupClient.requestGet(clazz, uri);
		
		//compare project01 and project02
		ObjectMapper mapper = new ObjectMapper();
		assertThat(mapper.writeValueAsString(projects02), is(mapper.writeValueAsString(projects01)));
		
//		first del hasProject
		params.clear();
		params.put("prj_idx", project01.getIdx().toString());
		groupClient.requestDelete(clazz, uri, params);

		projects01 = groupClient.requestGet(clazz, uri);
		assertThat(projects01.size(), is(1));
		
//		second del hasProject
		params.clear();
		params.put("prj_idx", project02.getIdx().toString());
		projects01 = groupClient.requestDelete(clazz, uri, params);

		groupClient.requestGet(clazz, uri, status().isNoContent());
	}
	
	@Test
	public void testHasProjectReset() throws Exception {
		String methodName = new Object(){}.getClass().getEnclosingMethod().getName();
//		init Project
		Project project01 = (Project) SerializationUtils.clone(this.project);
		project01.setName(methodName + "-project01");
		project01 = projectClient.requestCreate(project01);

		Project project02 = (Project) SerializationUtils.clone(this.project);
		project02.setName(methodName + "-project02");
		project02 = projectClient.requestCreate(project02);
		
//		init Group
		ProjectGroup group = (ProjectGroup) SerializationUtils.clone(this.group);
		group.setName(methodName + "-group");
		ArrayList<Project> projects = new ArrayList<Project>();
		projects.add(project01);
		projects.add(project02);
		group.setProjects(projects);
		
//		create new Group
		group = groupClient.requestCreate(group);
		
		String uri = String.format("%s/%s/hasProject", groupClient.getDefaultURI(), group.getIdx());
		HashMap<String, String> params = new HashMap<String, String>();
		Class<? extends ArrayList> clazz = new ArrayList<Project>().getClass();

//		get hasProject
		projects = groupClient.requestGet(clazz, uri);
		assertThat(projects.size(), is(2));
		
//		remove one & update
		projects.remove(0);
		group.setProjects(projects);
		groupClient.requestUpdate(group, group.getIdx().toString());
		projects = groupClient.requestGet(clazz, uri);
		assertThat(projects.size(), is(1));
		
		
//		add one & update
		projects.add(project01);
		group.setProjects(projects);
		groupClient.requestUpdate(group, group.getIdx().toString());
		projects = groupClient.requestGet(clazz, uri);
		assertThat(projects.size(), is(2));

//		remove all
		projects.clear();
		group.setProjects(projects);
		groupClient.requestUpdate(group, group.getIdx().toString());
		projects = groupClient.requestGet(clazz, uri, status().isNoContent());
	}
}
