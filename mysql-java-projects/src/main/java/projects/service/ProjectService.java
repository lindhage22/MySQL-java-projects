package projects.service;


import projects.dao.ProjectDao;
import projects.exception.DbException;
import projects.entity.entity.Project;
import java.util.List;
import java.util.NoSuchElementException;


public class ProjectService {
    private ProjectDao projectDao = new ProjectDao();

    public Project addProject(Project project) {
        return projectDao.insertProject(project);
    }

	public List<Project> fetchAllProjects() {
		return projectDao.fetchAllProjects();
	}

	public Project fetchProjectById(Integer projectId) {
	    return projectDao.fetchProjectById(projectId).orElseThrow(() -> 
	    new NoSuchElementException("Project with project ID = " + projectId 
	    + " does not exist."));
	}

	public void modifyProjectDetails(Project project) {
		  if(!projectDao.modifyProjectDetails(project)){
	         throw  new DbException("ProjectId with ID = "
	          + project.getProjectId() + " does not exist.");
	        }
		
	}

	public void deleteProject(Integer projectId) {
	     if(!projectDao.deleteProject(projectId)){
	            throw new DbException("Project with ID= " + projectId 
	            	+ " does not exist.");
	        }
		
	}


    }

