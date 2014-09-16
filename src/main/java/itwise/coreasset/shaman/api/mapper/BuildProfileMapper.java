package itwise.coreasset.shaman.api.mapper;

import itwise.coreasset.shaman.api.model.BuildProfile;

import java.util.ArrayList;

import org.apache.ibatis.annotations.Param;

/**
 * Mybatis BuildProfile Mapper
 * 
 * @author kkuru
 *
 */
public interface BuildProfileMapper {
	public void insert(BuildProfile profile);
	
	public void update(@Param("idx") int idx, @Param("profile") BuildProfile profile);
	
	public void delete(@Param("idx") int idx);
//	public void delete(@Param("name") String name);
	
	public int isExist(@Param("idx") int idx);
	public int isExist(@Param("keyword") String keyword);

	public int count();
	public int count(@Param("idx") int idx);
	public int count(@Param("keyword") String keyword);

	public BuildProfile findOne(@Param("idx") int idx);
	public BuildProfile findOne(@Param("name") String name);
	
	public ArrayList<BuildProfile> findList(@Param("offset") int offset, @Param("limit") int limit);
	public ArrayList<BuildProfile> findList(@Param("offset") int offset, @Param("limit") int limit, @Param("keyword") String keyword);
	public ArrayList<BuildProfile> findList(@Param("offset") int offset, @Param("limit") int limit, @Param("keyword") String keyword, @Param("order_column") String order_column, @Param("order_dir") String order_dir);
	public ArrayList<BuildProfile> findList(@Param("offset") int offset, @Param("limit") int limit, @Param("order_column") String order_column, @Param("order_dir") String order_dir);

//	public void addHasGroup(@Param("prj_idx") int prj_idx, @Param("grp_idx") int grp_idx);
//	public void delHasGroup(@Param("prj_idx") int prj_idx, @Param("grp_idx") int grp_idx);
//
//	public void delHasGroupByProject(@Param("prj_idx") int idx);
}
