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
//	public void delete(@Param("name") String name);
	
	public int isExist(@Param("idx") int idx);
	public int isExist(@Param("keyword") String keyword);

	public int count();
	public int count(@Param("idx") int idx);
	public int count(@Param("keyword") String keyword);

	public ProjectGroup findOne(@Param("idx") int idx);
	public ProjectGroup findOne(@Param("name") String name);
	
	public ArrayList<ProjectGroup> findList(@Param("offset") int offset, @Param("limit") int limit);
	public ArrayList<ProjectGroup> findList(@Param("offset") int offset, @Param("limit") int limit, @Param("keyword") String keyword);
	public ArrayList<ProjectGroup> findList(@Param("offset") int offset, @Param("limit") int limit, @Param("keyword") String keyword, @Param("order_column") String order_column, @Param("order_dir") String order_dir);
	public ArrayList<ProjectGroup> findList(@Param("offset") int offset, @Param("limit") int limit, @Param("order_column") String order_column, @Param("order_dir") String order_dir);

}
