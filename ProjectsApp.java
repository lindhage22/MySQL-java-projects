package projects;

import java.util.List;
import java.util.Scanner;
import java.math.BigDecimal;
import java.util.Objects;
import projects.exception.DbException;
import projects.entity.entity.Project;
import projects.service.ProjectService;




public class ProjectsApp {

	private Scanner scanner = new Scanner(System.in);
	private ProjectService projectService = new ProjectService();
	private Project curProject;
	
	
	//@formatter:off
	private List<String> operations =List.of(
			"1) Add a project",
			"2) List projects",
			"3) Select a project"
		);
	//@formatter:off
	public static void main(String[]args) {
		new ProjectsApp().processUserSelections();
	}
	
	private void processUserSelections( ) {
		boolean done = false;
		
		while(!done) {
			try {
		     int selection = getUserSelection();
			
		switch(selection) {
		case -1:
			done = exitMenu();
			break;
		case 1: 
			createProject();
			break;
		case 2:
			listProjects();
			break;
		case 3:
			selectProjects();
			break;
			
	 default:
		 System.out.println("\n" + selection + " is not a valid selection. Try again.");
		 break;
		}
	}
	catch(Exception e) {
		System.out.println("\nError: " + e + " Try again. ");
				
	 }
	}	
   }
	private void selectProjects() {
		 listProjects();
	     Integer projectId = getIntInput("Enter a project ID to select a project");

	     
	     /* Unselect the current project. */
	     
	     curProject = null;
	     
	     /*This will throw an exception if an invalid project ID is entered. */
	     
	     curProject = projectService.fetchProjectById(projectId);
		
	}

	private void listProjects() {
		List<Project> projects = projectService.fetchAllProjects();
		
		System.out.println("\nProjects:");
		
		projects.forEach(project ->System.out.println("  " + project.getProjectId()
		   + ": " + project.getProjectName()));
		
		/*Not sure why in the instructions it ask to do with out " " but in the solution it says too*/
	}

	private void createProject(){
		String projectName = getStringInput("Enter the project name");
		BigDecimal estimatedHours = getDecimalInput ("Enter the estimated hours");
		BigDecimal actualHours = getDecimalInput ("Enter the actual hours");
		Integer difficulty =getIntInput ("Enter the project difficulty (1-5))");
		String notes = getStringInput ("Enter the project notes");	
		
		Project project = new Project();
		
		project.setProjectName(projectName);
		project.setEstimatedHours (estimatedHours);
		project.setActualHours(actualHours);
		project.setDifficulty(difficulty);
		project.setNotes(notes);
		
		Project dbProject = projectService.addProject(project);
		System.out.println("You have successfully created project: " + dbProject);
		
		/*validate code here?? Not quite sure how */
	}

	private BigDecimal getDecimalInput(String prompt) {
		String input =getStringInput(prompt);
		
		if(Objects.isNull(input)) {
		
		return null;
	}
		try {
			return new BigDecimal(input).setScale(2);
		}
		catch(NumberFormatException e) {
			throw new DbException (input + " is not a valid decimal number. ");
		}
		}

	private boolean exitMenu() {
		System.out.println("Exiting the menu.");
		return true;
	
}

	private int getUserSelection() {
		printOperations();
		
		Integer input = getIntInput("Enter a menu selection");
		
		return Objects.isNull(input)? -1 : input;
	}

	private Integer getIntInput(String prompt) {
		String input = getStringInput(prompt);
		
		if(Objects.isNull(input)) {
		return null;
	}
		try {
			return Integer.valueOf(input);
		}
		catch(NumberFormatException e) {
			throw new DbException(input + " is not a valid number.");
		}
	}

	private String getStringInput(String prompt) {
		System.out.print(prompt + ": ");
		String input =scanner.nextLine();
		
		return input.isBlank() ? null : input.trim();
	}
	/* This is the print menu selections, one per line. */

	private void printOperations() {
		System.out.println("\n These are the available selections."
				+ "Press the enter key to quit:");
		
		/* Lambda Expression*/
		operations.forEach(line -> System.out.println(" " + line));
		
		/*With enhanced for loop*/
		//for(Sting line : operations){
		//System.out.println( " " + line);
		//}

	if (Objects.isNull(curProject)){
        System.out.println("\nYou are not working with a project. ");
    }
    else {
        System.out.println("\nYou are working with this project: " + curProject);
    }
}
}

		