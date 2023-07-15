package projects.dao;

import java.math.BigDecimal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import projects.entity.entity.Category;
import projects.entity.entity.Material;
import projects.entity.entity.Project;
import projects.entity.entity.Step;
import projects.exception.DbException;
import provided.util.DaoBase;
import java.util.*;





public class ProjectDao extends DaoBase {
    private static final String CATEGORY_TABLE = "category";
    private static final String MATERIAL_TABLE = "material";
    private static final String PROJECT_TABLE = "project";
    private static final String PROJECT_CATEGORY_TABLE = "project_category";
    private static final String STEP_TABLE = "step";
    
    
    
    public Project insertProject(Project project) {
        //@formatter:off
      
    	String sql=""
                + "INSERT INTO " + PROJECT_TABLE + " "
                + "(project_name, estimated_hours, actual_hours, difficulty, notes)"
                + "VALUES "
                + "(?,?,?,?,?)";
        //@formatter: on
        
    	
    	try(Connection conn = DbConnection.getConnection()){
            startTransaction(conn);
            try(PreparedStatement smt = conn.prepareStatement(sql)){
                setParameter(smt, 1, project.getProjectName(),String.class);
                setParameter(smt, 2, project.getEstimatedHours(), BigDecimal.class);
                setParameter(smt, 3, project.getActualHours(),BigDecimal.class);
                setParameter(smt,4, project.getDifficulty(),Integer.class);
                setParameter(smt, 5,project.getNotes(),String.class);

                smt.executeUpdate();

                Integer projectId = getLastInsertId(conn, PROJECT_TABLE);
                commitTransaction(conn);

                project.setProjectId(projectId);
                return project;
            }
            catch (Exception e){
                rollbackTransaction(conn);
                throw new DbException(e);
            }
        } 
    	catch (SQLException e) {
            throw new DbException(e);
        }
    }



	public List<Project> fetchAllProjects() {
		String sql= "SELECT * FROM " + PROJECT_TABLE + " ORDER BY project_name";

		/*SQL Select Statement, want fields available in the table Select* From*/
		
        try(Connection conn = DbConnection.getConnection()) {
            startTransaction(conn);

            /* Not sure I get the second line with the instructions */
            
            try(PreparedStatement stmt = conn.prepareStatement(sql)){
                try(ResultSet rs = stmt.executeQuery()){
                    List<Project> projects = new LinkedList<>();

                    while(rs.next()){
                      // projects.add(extract(rs,Project.class)); (This is one way)
                        
                    	Project project = new Project();
                        
                    	project.setActualHours(rs.getBigDecimal("actual_hours"));
                        project.setDifficulty(rs.getObject("difficulty",Integer.class));
                        project.setEstimatedHours(rs.getBigDecimal("estimated_hours"));
                        project.setNotes(rs.getString("notes"));
                        project.setProjectId(rs.getObject("project_id",Integer.class));
                        project.setProjectName(rs.getString("project_name"));

                        projects.add(project);
                    }
                    return projects;
                }
            }catch (Exception e){
                rollbackTransaction(conn);
                throw new DbException(e);
            }
        }catch(SQLException e ){
            throw new DbException(e);
        }

	}



	public Optional <Project> fetchProjectById(Integer projectId) {
		  String sql = "SELECT * FROM " + PROJECT_TABLE + " WHERE project_id = ?";
	        try (Connection conn = DbConnection.getConnection()){
	            startTransaction(conn);

	            try {
	                Project project = null;

	                try(PreparedStatement stmt = conn.prepareStatement(sql)){
	                    setParameter(stmt,1,projectId, Integer.class);

	                    try(ResultSet rs = stmt.executeQuery()){
	                        if(rs.next()){
	                            project = extract(rs,Project.class);
	                        }
	                    }
	                }
	                if (Objects.nonNull(project)){
	                    project.getMaterials().addAll(fetchMaterialsForProject(conn,projectId));
	                    project.getSteps().addAll(fetchStepsForProject(conn,projectId));
	                    project.getCategories().addAll(fetchCategoriesForProject(conn,projectId));
	                }
	                commitTransaction(conn);
	                return Optional.ofNullable(project);
	            }
	            catch(Exception e){
	                rollbackTransaction(conn);
	                throw new DbException(e);
	            }
	        }
	        catch (SQLException e){
	            throw new DbException(e);
	}

    
}



	private Collection<? extends Category> fetchCategoriesForProject(Connection conn, Integer projectId) {
		 
		//@formatter:off
        
		String sql = "SELECT c.* FROM " + CATEGORY_TABLE + " c "
        
        + "JOIN " + PROJECT_CATEGORY_TABLE + " pc USING (category_id) "
        + "WHERE project_id = ?";
        
        //@formatter: on

        try(PreparedStatement stmt = conn.prepareStatement(sql)){
            setParameter(stmt,1, projectId, Integer.class);

            try(ResultSet rs = stmt.executeQuery()){
                List<Category> categories = new LinkedList<>();

                while(rs.next()){
                    categories.add(extract(rs,Category.class));
                }
                return categories;
            }
        } catch (SQLException e) {
            throw new DbException(e);
        }
	}



	private List<Step> fetchStepsForProject(Connection conn, Integer projectId) {
		 String sql = "SELECT * FROM " + STEP_TABLE + " WHERE project_id = ?";

	        try(PreparedStatement stmt = conn.prepareStatement(sql)){
	            setParameter(stmt,1, projectId, Integer.class);

	            try(ResultSet rs = stmt.executeQuery()){
	                List<Step> steps = new LinkedList<>();

	                while(rs.next()){
	                    steps.add(extract(rs,Step.class));
	                }
	                return steps;
	            }
	        } catch (SQLException e) {
	            throw new DbException(e);
	        }
	}



	private List<Material> fetchMaterialsForProject(Connection conn, Integer projectId) {
		 String sql = "SELECT * FROM " + MATERIAL_TABLE + " WHERE project_id = ?";

	        try(PreparedStatement stmt = conn.prepareStatement(sql)){
	            setParameter(stmt,1, projectId, Integer.class);

	            try(ResultSet rs = stmt.executeQuery()){
	                List<Material> materials = new LinkedList<>();

	                while(rs.next()){
	                    materials.add(extract(rs, Material.class));
	                }
	                return materials;
	            }
	        } catch (SQLException e) {
	            throw new DbException(e);
	}
	}

/*
 * Now, complete the code in the project DAO to update the project details. 
 * The method structure is similar to the insertProject() method. 
 * You will write the SQL UPDATE statement with the parameter placeholders. 
 * Then, obtain a Connection and start a transaction. 
 * Next, you will obtain a PreparedStatement object and set the six parameter values. 
 * Finally, you will call executeUpdate() on the PreparedStatement and commit the 
 * transaction.The difference in this method and the  * insert method is that you 
 * will examine the return * value from executeUpdate(). The executeUpdate()  * method 
 * returns the number of rows affected by the * UPDATE operation. Since a single row 
 * is being acted on * (comparing to the primary key in the WHERE clause guarantees this), the return value should be 1. If it is 0 it means that no rows were acted on and the primary key value (project ID) is not found. So, the method returns true if executeUpdate() returns 1 and false if it returns 0.
 */

	public boolean modifyProjectDetails(Project project) {
		 // formatter: off
        String sql = "UPDATE " + PROJECT_TABLE + " SET "
                + "project_name = ?, "
                +"estimated_hours = ?, "
                +"actual_hours = ?, "
                +"difficulty = ?, "
                +"notes = ? "
                +"WHERE project_id = ?";
        //formatter: on

        try (Connection conn = DbConnection.getConnection()){
            startTransaction(conn);

            try(PreparedStatement stmt = conn.prepareStatement(sql)) {
                setParameter(stmt, 1, project.getProjectName(), String.class);
                setParameter(stmt, 2, project.getEstimatedHours(), BigDecimal.class);
                setParameter(stmt, 3, project.getActualHours(), BigDecimal.class);
                setParameter(stmt, 4, project.getDifficulty(), Integer.class);
                setParameter(stmt, 5, project.getNotes(), String.class);
                setParameter(stmt, 6, project.getProjectId(), Integer.class);

                boolean modified = stmt.executeUpdate() == 1;
                commitTransaction(conn);

                return modified;
            }
            catch (Exception e) {
                rollbackTransaction(conn);
                throw new DbException(e);
            }
         }
         catch (SQLException e){
            throw new DbException(e);
        }
	}

/*
 * The deleteProject() method in the DAO is very similar to the modifyProjectDetails() 
 * method. You will first create the SQL DELETE statement. Then, you will obtain the 
 * Connection and PreparedStatement, and set the project ID parameter on the 
 * PreparedStatement. Then, you will call executeUpdate() and verify that the return 
 * value is 1, indicating a successful deletion. Finally, you will commit the 
 * transaction and return success or failure.
 */

	public boolean deleteProject(Integer projectId) {
		 String sql = "DELETE FROM " + PROJECT_TABLE + " WHERE project_id = ?";
	        
		 try(Connection conn = DbConnection.getConnection()){
	            startTransaction(conn);

	     try(PreparedStatement stmt = conn.prepareStatement(sql)){
	                setParameter(stmt,1, projectId, Integer.class);

	     boolean deleted = stmt.executeUpdate()==1;
	                commitTransaction(conn);
	                return deleted;
	      }
	     catch (Exception e){
	          rollbackTransaction(conn);
	           throw new DbException(e);
	            }
	      }
	      catch (SQLException e){
	            throw new DbException(e);
	        }
	}
	}     