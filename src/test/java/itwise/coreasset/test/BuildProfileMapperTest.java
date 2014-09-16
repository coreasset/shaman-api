package itwise.coreasset.test;


import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import itwise.coreasset.shaman.api.config.AppContextConfig;
import itwise.coreasset.shaman.api.config.InitEnvironmentConfig;
import itwise.coreasset.shaman.api.mapper.BuildProfileMapper;
import itwise.coreasset.shaman.api.model.BuildProfile;

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
public class BuildProfileMapperTest {

	@Autowired
	private BuildProfileMapper profileMapper;
	
	@Before
	public void setUp(){
	}
	
	/**
	 * BuildProfile CRUD Test
	 */
	@Test
	public void testBuildProfileCRUD(){
		BuildProfile profile = new BuildProfile();
		profile.setName("testBuildProfileCRUD - insert");
		profile.setDescription("BuildProfileMapperTest 테스트");
		profile.setFlavor("ant");
		profile.setGoal("install");
		profile.setParam("-DskipTest");
		
		// insert
		profileMapper.insert(profile);
		
		// findone & check assert
		BuildProfile profile02 = profileMapper.findOne(profile.getIdx());
		assertEquals("compare name field", profile.getName(), profile02.getName());
		assertEquals("compare description field", profile.getDescription(), profile02.getDescription());
		
		// update
		profile02.setName("testBuildProfileCRUD - update");
		profileMapper.update(profile02.getIdx(), profile02);
		
		// findone & check assert
		BuildProfile profile03 = profileMapper.findOne(profile02.getIdx());
		assertEquals("compare name field", profile02.getName(), profile03.getName());
		assertEquals("compare description field", profile02.getDescription(), profile03.getDescription());
		
		// delete
		profileMapper.delete(profile03.getIdx());
		BuildProfile profile04 = profileMapper.findOne(profile03.getIdx());
		assertEquals("compare description field", profile04, null);
	}
	
	
	/**
	 * field match and like search count
	 * 
	 * @throws Exception
	 */
	@Test
	public void testBuildProfileCount() throws Exception {
		int idx, allCount;

		String namePrefix = "testBuildProfileCount";
		
		// idx field search number
		idx = profileMapper.isExist(-1);
		assertEquals("is 0", idx, 0);
		
		//All Count
		allCount = profileMapper.count();
		BuildProfile profile = new BuildProfile();
		profile.setName(namePrefix + "-test 1");
		profile.setDescription("testBuildProfileCount-테스트");
		profile.setFlavor("ant");
		profile.setGoal("install");
		profile.setParam("-DskipTest");
		
		profileMapper.insert(profile);

		// name match
		idx= profileMapper.isExist(profile.getName());
		assertThat("is 1", idx, greaterThan(0));
		
		
		// idx match
		idx = profileMapper.isExist(profile.getIdx());
		assertThat("is 1", idx,  greaterThan(0));
		
		
		// name field search result is no match
		idx = profileMapper.isExist("not" + profile.getName());
		assertEquals("is 0", idx, 0);

		// one more insert
		profile.setName(namePrefix + "-테스트2");
		profile.setFlavor("ant");
		profile.setGoal("install");
		profile.setParam("-DskipTest");
		
		profileMapper.insert(profile);
		
		//like match
		idx = profileMapper.count(namePrefix);
		assertEquals("like query result is 2", idx, 2);
		assertEquals("all count + 2", profileMapper.count(), allCount + 2);
	}
	
	/**
	 * BuildProfile findList and search test and paging
	 */
	@Test
	public void testBuildProfileFindList(){
		
		String namePrefix = "testBuildProfileFindList";
		
		//page 1, limit 5
		if(profileMapper.count() > 5){
			ArrayList<BuildProfile> profiles = profileMapper.findList(1, 5);
			assertEquals(5, profiles.size());
		}
		
		BuildProfile profile = new BuildProfile();
		profile.setDescription(namePrefix + "-desc");
		
		// 1 ~ 20
		for (int i = 1; i <= 20; i++) {
			profile.setName(namePrefix + " findlist" + i + " test - insert");
			profile.setFlavor("ant");
			profile.setGoal("install");
			profileMapper.insert(profile);
		}

		//검색
		String keyword = "findlist1";
		ArrayList<BuildProfile> profiles;
		profiles = profileMapper.findList(0, 100, keyword);
		int count = profileMapper.count(keyword);
		assertEquals("fist findlist count", profiles.size(), count);

		keyword = "findlist2";
		count = profileMapper.count(keyword);
		profiles = profileMapper.findList(0, 100, keyword);
		assertEquals("second findlist count", profiles.size(), count);
	}
	
	/**
	 * 실제 DB에 있는 값으로 임시 테스트
	 * 
	 * @throws Exception
	 */
	@Test
	public void testTmp() throws Exception {
	}
}
