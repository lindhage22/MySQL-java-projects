package projects.service;


import projects.dao.ProjectDao;
import projects.entity.entity.Project;


import java.util.List;
import java.util.NoSuchElementException;

public class ProjectService {
    private ProjectDao projectDao = new ProjectDao();

    public Project addProject(Project project) {
        return projectDao.insertProject(project);
    }

  
    }

