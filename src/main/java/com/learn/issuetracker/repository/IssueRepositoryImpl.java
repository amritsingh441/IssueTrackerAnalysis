package com.learn.issuetracker.repository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import com.learn.issuetracker.model.Issue;

/*
 * This class is used to read the issues data from the file and store it in a collection. Java8 NIO 
 * should be used to read the file in to streams
*/
public class IssueRepositoryImpl implements IssueRepository {

	/*
	 * This List will store the issue details read from the file
	 */
	private List<Issue> issues;

	/*
	 * issuesFilePath variable is used to store the path of issues.csv file
	 */
	private Path issuesFilePath;

	/*
	 * Initialize the member variables in the parameterized constructor
	 * initializeIssuesFromFile() method should be used in the constructor to
	 * initialize the 'issues' instance variable
	 *
	 */
	public IssueRepositoryImpl(Path issuesFilePath) {
		super();
		this.issuesFilePath = issuesFilePath;
		initializeIssuesFromFile();
	}

	/*
	 * This method should read the file from the path stored in variable
	 * 'issuesFilePath'. It should store the records read from the file in a List
	 * and initialize the 'issues' variable with this list. It should use
	 * 'parseIssue' method of Utility class for converting the line read from the
	 * file in to an Issue Object. Any issue with ISSUE ID, not starting with "IS",
	 * should not be stored in the "issues" List.
	 */

	public void initializeIssuesFromFile() {
		try(Stream<String> issuesFileData = Files.newBufferedReader(issuesFilePath).lines()){
			issues = issuesFileData.map(Utility::parseIssue).collect(Collectors.toList());
			issues = issues.stream().filter(issue -> issue.getIssueId().startsWith("IS")).collect(Collectors.toList());
		} catch (IOException e) {
			e.getMessage();		
		}
	}

	/*
	 * Getter Method
	 */
	public List<Issue> getIssues() {
		return issues;
	}
}
