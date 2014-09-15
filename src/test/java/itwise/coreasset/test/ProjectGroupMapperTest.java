package itwise.coreasset.test;


import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import itwise.coreasset.shaman.api.config.AppContextConfig;
import itwise.coreasset.shaman.api.config.InitEnvironmentConfig;
import itwise.coreasset.shaman.api.mapper.ProjectGroupMapper;
import itwise.coreasset.shaman.api.mapper.ProjectMapper;
import itwise.coreasset.shaman.api.model.Project;
import itwise.coreasset.shaman.api.model.ProjectGroup;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;


@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
@TransactionConfiguration(defaultRollback = true)
@ContextConfiguration(classes = {AppContextConfig.class}, initializers = InitEnvironmentConfig.class)
public class ProjectGroupMapperTest {

	@Autowired
	private ProjectGroupMapper groupMapper;
	
	@Autowired
	private ProjectMapper projectMapper;
	
	@Before
	public void setUp(){
	}
	
	/**
	 * ProjectGroup CRUD Test
	 */
	@Test
	public void testProjectGroupCRUD(){
		ProjectGroup projectGroup = new ProjectGroup();
		projectGroup.setName("project groups CRUD test - insert");
		projectGroup.setDescription("project groups mapper 테스트");
		
		// insert
		groupMapper.insert(projectGroup);
		
		// findone
		ProjectGroup projectGroup2 = groupMapper.findOne(projectGroup.getIdx());
		assertEquals("compare name field", projectGroup.getName(), projectGroup2.getName());
		assertEquals("compare description field", projectGroup.getDescription(), projectGroup2.getDescription());
		
		// update
		projectGroup2.setName("project groups CRUD test - update");
		groupMapper.update(projectGroup2.getIdx(), projectGroup2);
		
		// findone
		ProjectGroup projectGroup3 = groupMapper.findOne(projectGroup2.getIdx());
		assertEquals("compare name field", projectGroup2.getName(), projectGroup3.getName());
		assertEquals("compare description field", projectGroup2.getDescription(), projectGroup3.getDescription());
		
		// delete
		groupMapper.delete(projectGroup3.getIdx());
		ProjectGroup projectGroup4 = groupMapper.findOne(projectGroup3.getIdx());
		assertEquals("compare description field", projectGroup4, null);
		
	}
	
	
	/**
	 * field match and like search count
	 * 
	 * @throws Exception
	 */
	@Test
	public void testProjectGroupCount() throws Exception {
		int idx, allCount;
		
		// idx field search number
		idx = groupMapper.isExist(-1);
		assertEquals("is 0", idx, 0);
		
		//All Count
		allCount = groupMapper.count();
		
		ProjectGroup projectGroup = new ProjectGroup();
		projectGroup.setDescription("테스트");
		
		projectGroup.setName("count test 1");
		groupMapper.insert(projectGroup);

		// name match
		idx= groupMapper.isExist(projectGroup.getName());
		assertThat("is 1", idx, greaterThan(0));
		
		
		// idx match
		idx = groupMapper.isExist(projectGroup.getIdx());
		assertThat("is 1", idx,  greaterThan(0));
		
		
		// name field search result is no match
		idx = groupMapper.isExist("count test");
		assertEquals("is 0", idx, 0);

		// one more insert
		projectGroup.setName("count test 2");
		groupMapper.insert(projectGroup);
		
		//like match
		idx = groupMapper.count("count test");
		assertEquals("like query result is 2", idx, 2);
		assertEquals("all count + 2", groupMapper.count(), allCount + 2);
	}
	
	/**
	 * ProjectGroup findList and search test and paging
	 */
	@Test
	public void testProjectGroupFindList(){
		
		//page 1, limit 5
		if(groupMapper.count() > 5){
			ArrayList<ProjectGroup> projectGroups = groupMapper.findList(1, 5);
			assertEquals(5, projectGroups.size());
		}
		
		ProjectGroup projectGroup = new ProjectGroup();
		projectGroup.setDescription("project groups findlist 테스트");
		// 1 ~ 20
		for (int i = 1; i <= 20; i++) {
			projectGroup.setName("findlist" + i + " test - insert");
			System.out.println(projectGroup.getName());
			groupMapper.insert(projectGroup);
		}

		String keyword = "findlist1";
		ArrayList<ProjectGroup> projectGroups;
		projectGroups = groupMapper.findList(0, 100, keyword);
		int count = groupMapper.count(keyword);
		assertEquals("fist findlist count", projectGroups.size(), count);

		keyword = "findlist2";
		count = groupMapper.count(keyword);
		projectGroups = groupMapper.findList(0, 100, keyword);
		assertEquals("second findlist count", projectGroups.size(), count);
	}

	/**
	 * add & delete has ProjectGruop
	 */
	@Test
	public void testHasProject(){
		//init project
		Project project01 = new Project();
		Project project02 = new Project();
		project01.setName("testHasProject-project01");
		project02.setName("testHasProject-project02");
		projectMapper.insert(project01);
		projectMapper.insert(project02);

		//init group
		ProjectGroup group = new ProjectGroup();
		group.setName("testHasProject-group");
		groupMapper.insert(group);
		
//		add has project
		groupMapper.addHasProject(group.getIdx(), project01.getIdx());
		groupMapper.addHasProject(group.getIdx(), project02.getIdx());
		
		ProjectGroup findGroup = groupMapper.findOne(group.getIdx());
		assertThat(findGroup.getProjects().size(), is(2));
		
//		del has project
		groupMapper.delHasProject(group.getIdx(), project01.getIdx());
		groupMapper.delHasProject(group.getIdx(), project02.getIdx());
		findGroup = groupMapper.findOne(group.getIdx());
		assertThat(findGroup.getProjects().size(), is(0));

//		add has project
		groupMapper.addHasProject(group.getIdx(), project01.getIdx());
		groupMapper.addHasProject(group.getIdx(), project02.getIdx());
		
		findGroup = groupMapper.findOne(group.getIdx());
		assertThat(findGroup.getProjects().size(), is(2));
		
	}
	
	@Test
	public void testTmp() throws Exception{
		
	}
}
