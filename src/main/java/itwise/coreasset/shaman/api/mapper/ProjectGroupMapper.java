package itwise.coreasset.shaman.api.mapper;

import java.util.ArrayList;
import java.util.List;

import itwise.coreasset.shaman.api.model.ProjectGroup;

/**
 * @author kkuru
 *
 */
public interface ProjectGroupMapper {
	public void insert(ProjectGroup projectGroup);
	
	public void update(ProjectGroup projectGroup);
	
	public void delete(ProjectGroup projectGroup);

	public ProjectGroup findOne(ProjectGroup projectGroup);
	
	public ArrayList<ProjectGroup> findList();
}
