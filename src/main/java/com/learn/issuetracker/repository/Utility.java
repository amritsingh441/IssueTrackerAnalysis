package com.learn.issuetracker.repository;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import com.learn.issuetracker.model.Employee;
import com.learn.issuetracker.model.Issue;

/*
 * This class has methods for parsing the String read from the files in to corresponding Model Objects
*/
public class Utility {
	
	private Utility() {
		//Private Constructor to prevent object creation
	}

	/*
	 * parseEmployee takes a string with employee details as input parameter and parses it in to an Employee Object 
	*/
	public static Employee parseEmployee(String employeeDetail) {
		
		if(employeeDetail != null) {
			String [] empStrArr = employeeDetail.split(",");
			return new Employee(Integer.parseInt(empStrArr[0]),empStrArr[1],empStrArr[2]);
		}
		return null;
	}

	/*
	 * parseIssue takes a string with issue details and parses it in to an Issue Object. The employee id in the 
	 * Issue details is used to search for an an Employee, using EmployeeRepository class. If the employee is found
	 * then it is set in the Issue object. If Employee is not found, employee is set as null in Issue Object  
	*/

	public static Issue parseIssue(String issueDetail) {
		if(issueDetail != null) {
			String [] issueStrArr = issueDetail.split(",");
			DateTimeFormatter format = DateTimeFormatter.ofPattern("[dd/MM/yyyy][dd-MM-yyyy]");
			Optional<Employee> optEmp = EmployeeRepository.getEmployee(Integer.parseInt(issueStrArr[6]));
			Employee emp = optEmp.isPresent()?optEmp.get():null;
			return new Issue(issueStrArr[0],issueStrArr[1],LocalDate.parse(issueStrArr[2],format),LocalDate.parse(issueStrArr[3],format),
					issueStrArr[4],issueStrArr[5],emp);
		}
		return null;
	}
}
