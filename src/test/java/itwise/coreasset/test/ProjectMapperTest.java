package itwise.coreasset.test;


import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import itwise.coreasset.shaman.api.config.DataSourceConfig;
import itwise.coreasset.shaman.api.config.InitEnvironmentConfig;
import itwise.coreasset.shaman.api.config.MyBatisConfig;
import itwise.coreasset.shaman.api.mapper.ProjectGroupMapper;
import itwise.coreasset.shaman.api.mapper.ProjectMapper;
import itwise.coreasset.shaman.api.model.Project;
import itwise.coreasset.shaman.api.model.ProjectGroup;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;


@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
@TransactionConfiguration(defaultRollback = true)
@MapperScan(basePackages = "itwise.coreasset.shaman.api.mapper")
@ContextConfiguration(classes = {DataSourceConfig.class, MyBatisConfig.class}, initializers = InitEnvironmentConfig.class)
public class ProjectMapperTest {

	@Autowired
	private ProjectMapper projectMapper;
	
	@Autowired
	private ProjectGroupMapper groupMapper;
	
	@Before
	public void setUp(){
	}
	
	/**
	 * Project CRUD Test
	 */
	@Test
	public void testProjectCRUD(){
		Project project = new Project();
		project.setName("projects CRUD test - insert");
		project.setDescription("projects mapper 테스트");
		
		// insert
		projectMapper.insert(project);
		
		// findone
		Project project2 = projectMapper.findOne(project.getIdx());
		assertEquals("compare name field", project.getName(), project2.getName());
		assertEquals("compare description field", project.getDescription(), project2.getDescription());
		
		// update
		project2.setName("project CRUD test - update");
		projectMapper.update(project2.getIdx(), project2);
		
		// findone
		Project project3 = projectMapper.findOne(project2.getIdx());
		assertEquals("compare name field", project2.getName(), project3.getName());
		assertEquals("compare description field", project2.getDescription(), project3.getDescription());
		
		// delete
		projectMapper.delete(project3.getIdx());
		Project project4 = projectMapper.findOne(project3.getIdx());
		assertEquals("compare description field", project4, null);
		
	}
	
	
	/**
	 * field match and like search count
	 * 
	 * @throws Exception
	 */
	@Test
	public void testProjectCount() throws Exception {
		int idx, allCount;
		
		// idx field search number
		idx = projectMapper.isExist(-1);
		assertEquals("is 0", idx, 0);
		
		//All Count
		allCount = projectMapper.count();
		
		Project project = new Project();
		project.setDescription("테스트");
		
		project.setName("count test 1");
		projectMapper.insert(project);

		// name match
		idx= projectMapper.isExist(project.getName());
		assertThat("is 1", idx, greaterThan(0));
		
		
		// idx match
		idx = projectMapper.isExist(project.getIdx());
		assertThat("is 1", idx,  greaterThan(0));
		
		
		// name field search result is no match
		idx = projectMapper.isExist("count test");
		assertEquals("is 0", idx, 0);

		// one more insert
		project.setName("count test 2");
		projectMapper.insert(project);
		
		//like match
		idx = projectMapper.count("count test");
		assertEquals("like query result is 2", idx, 2);
		assertEquals("all count + 2", projectMapper.count(), allCount + 2);
	}
	
	/**
	 * Project findList and search test and paging
	 */
	@Test
	public void testProjectFindList(){
		
		//page 1, limit 5
		if(projectMapper.count() > 5){
			ArrayList<Project> projects = projectMapper.findList(1, 5);
			assertEquals(5, projects.size());
		}
		
		Project project = new Project();
		project.setDescription("project findlist 테스트");
		// 1 ~ 20
		for (int i = 1; i <= 20; i++) {
			project.setName("findlist" + i + " test - insert");
			System.out.println(project.getName());
			projectMapper.insert(project);
		}

		//검색
		String keyword = "findlist1";
		ArrayList<Project> projects;
		projects = projectMapper.findList(0, 100, keyword);
		int count = projectMapper.count(keyword);
		assertEquals("fist findlist count", projects.size(), count);

		keyword = "findlist2";
		count = projectMapper.count(keyword);
		projects = projectMapper.findList(0, 100, keyword);
		assertEquals("second findlist count", projects.size(), count);
		
		
	}
	
	
	/**
	 * add & delete has ProjectGruop
	 */
	@Test
	public void testHasProjectGroup(){
		ProjectGroup group01 = new ProjectGroup();
		ProjectGroup group02 = new ProjectGroup();
		group01.setName("hasGroupTest01");
		group02.setName("hasGroupTest02");
		groupMapper.insert(group01);
		groupMapper.insert(group02);
		
		Project project = new Project();
		project.setName("hasTest01");
		
		projectMapper.insert(project);
		
		
//		ArrayList<ProjectGroup> groups = new ArrayList<ProjectGroup>();
//		groups.add(group01);
//		project.setGroups(groups);
//		assertThat(project.getGroups().size(), is(1));
//		projectMapper.addHasGroup(project);
//		groups.add(group02);
//		project.setGroups(groups);
//		assertThat(project.getGroups().size(), is(2));
		
//		add has group
		projectMapper.addHasGroup(project.getIdx(), group01.getIdx());
		projectMapper.addHasGroup(project.getIdx(), group02.getIdx());
		
		Project hasProject = projectMapper.findOne(project.getIdx());
		assertThat(hasProject.getGroups().size(), is(2));
		
//		del has group
		projectMapper.delHasGroup(project.getIdx(), group01.getIdx());
		projectMapper.delHasGroup(project.getIdx(), group02.getIdx());
		hasProject = projectMapper.findOne(project.getIdx());
		assertThat(hasProject.getGroups().size(), is(0));
	}

}
