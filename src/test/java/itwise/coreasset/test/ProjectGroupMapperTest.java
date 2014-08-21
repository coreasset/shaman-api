package itwise.coreasset.test;


import static org.junit.Assert.*;
import itwise.coreasset.shaman.api.config.DataSourceConfig;
import itwise.coreasset.shaman.api.config.InitEnvironmentConfig;
import itwise.coreasset.shaman.api.config.MyBatisConfig;
import itwise.coreasset.shaman.api.mapper.ProjectGroupMapper;
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
public class ProjectGroupMapperTest {

	@Autowired
	private ProjectGroupMapper projectGroupMapper;
	
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
		projectGroupMapper.insert(projectGroup);
		
		// findone
		ProjectGroup projectGroup2 = projectGroupMapper.findOne(projectGroup.getIdx());
		assertEquals("compare name field", projectGroup.getName(), projectGroup2.getName());
		assertEquals("compare description field", projectGroup.getDescription(), projectGroup2.getDescription());
		
		// update
		projectGroup2.setName("project groups CRUD test - update");
		projectGroupMapper.update(projectGroup2.getIdx(), projectGroup2);
		
		// findone
		ProjectGroup projectGroup3 = projectGroupMapper.findOne(projectGroup2.getIdx());
		assertEquals("compare name field", projectGroup2.getName(), projectGroup3.getName());
		assertEquals("compare description field", projectGroup2.getDescription(), projectGroup3.getDescription());
		
		// delete
		projectGroupMapper.delete(projectGroup3.getIdx());
		ProjectGroup projectGroup4 = projectGroupMapper.findOne(projectGroup3.getIdx());
		assertEquals("compare description field", projectGroup4, null);
		
	}
	
	
	/**
	 * field match and like search count
	 * 
	 * @throws Exception
	 */
	@Test
	public void testProjectGroupCount() throws Exception {
		int count, allCount;
		
		// idx field search number
		count = projectGroupMapper.isExist(-1);
		assertEquals("is 0", count, 0);
		
		//All Count
		allCount = projectGroupMapper.count();
		
		ProjectGroup projectGroup = new ProjectGroup();
		projectGroup.setDescription("테스트");
		
		projectGroup.setName("count test 1");
		projectGroupMapper.insert(projectGroup);

		// name match
		count = projectGroupMapper.isExist(projectGroup.getName());
		assertEquals("is 1", count, 1);
		
		// idx match
		count = projectGroupMapper.isExist(projectGroup.getIdx());
		assertEquals("is 1", count, 1);
		
		
		// name field search result is no match
		count = projectGroupMapper.isExist("count test");
		assertEquals("is 0", count, 0);

		// one more insert
		projectGroup.setName("count test 2");
		projectGroupMapper.insert(projectGroup);
		
		//like match
		count = projectGroupMapper.count("count test");
		assertEquals("like query result is 2", count, 2);
		assertEquals("all count + 2", projectGroupMapper.count(), allCount + 2);
	}
	
	/**
	 * ProjectGroup findList and search test and paging
	 */
	@Test
	public void testProjectGroupFindList(){
		
		//page 1, limit 5
		if(projectGroupMapper.count() > 5){
			ArrayList<ProjectGroup> projectGroups = projectGroupMapper.findList(1, 5);
			assertEquals(5, projectGroups.size());
		}
		
		ProjectGroup projectGroup = new ProjectGroup();
		projectGroup.setDescription("project groups findlist 테스트");
		// 1 ~ 20
		for (int i = 1; i <= 20; i++) {
			projectGroup.setName("findlist" + i + " test - insert");
			System.out.println(projectGroup.getName());
			projectGroupMapper.insert(projectGroup);
		}

		String keyword = "findlist1";
		ArrayList<ProjectGroup> projectGroups;
		projectGroups = projectGroupMapper.findList(0, 100, keyword);
		int count = projectGroupMapper.count(keyword);
		assertEquals("fist findlist count", projectGroups.size(), count);

		keyword = "findlist2";
		count = projectGroupMapper.count(keyword);
		projectGroups = projectGroupMapper.findList(0, 100, keyword);
		assertEquals("second findlist count", projectGroups.size(), count);
	}

}
