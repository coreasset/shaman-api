package itwise.coreasset.shaman.api.mapper;

import itwise.coreasset.shaman.api.model.ProjectGroup;

import java.util.ArrayList;

import org.apache.ibatis.annotations.Param;

/**
 * @author kkuru
 *
 */
public interface ProjectGroupMapper {
	public void insert(ProjectGroup projectGroup);
	
	public void update(@Param("idx") int idx, @Param("projectGroup") ProjectGroup projectGroup);
	
	public void delete(@Param("idx") int idx);

//	public int count(@Param("idx") int idx);
	public int isExists(@Param("idx") int idx);

	public ProjectGroup findOne(ProjectGroup projectGroup);
	
	public ArrayList<ProjectGroup> findList();
}
