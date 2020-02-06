package com.learn.issuetracker.service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalUnit;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import com.learn.issuetracker.exceptions.IssueNotFoundException;
import com.learn.issuetracker.model.Employee;
import com.learn.issuetracker.model.Issue;
import com.learn.issuetracker.repository.IssueRepository;

/*
 * This class contains functionalities for searching and analyzing Issues data Which is stored in a collection
 * Use JAVA8 STREAMS API to do the analysis
 * 
*/
public class IssueTrackerServiceImpl implements IssueTrackerService {

	/*
	 * CURRENT_DATE contains the date which is considered as todays date for this
	 * application Any logic which uses current date in this application, should
	 * consider this date as current date
	 */
	private static final String CURRENT_DATE = "2019-05-01";

	/*
	 * The issueDao should be used to get the List of Issues, populated from the
	 * file
	 */
	private IssueRepository issueDao;
	private LocalDate today;

	/*
	 * Initialize the member variables Variable today should be initialized with the
	 * value in CURRENT_DATE variable
	 */
	public IssueTrackerServiceImpl(IssueRepository issueDao) {
		this.issueDao = issueDao;
		this.today = LocalDate.parse(CURRENT_DATE);

	}

	/*
	 * In all the below methods, the list of issues should be obtained by used
	 * appropriate getter method of issueDao.
	 */
	/*
	 * The below method should return the count of issues which are closed.
	 */
	@Override
	public long getClosedIssueCount() {
		return issueDao.getIssues().stream().filter(issue -> issue.getStatus().equalsIgnoreCase("Closed")).count();
	}

	/*
	 * The below method should return the Issue details given a issueId. If the
	 * issue is not found, method should throw IssueNotFoundException
	 */

	@Override
	public Issue getIssueById(String issueId) throws IssueNotFoundException {
	Optional<Issue> issueObj = 	issueDao.getIssues().stream().filter(issue -> issue.getIssueId().equalsIgnoreCase(issueId)).findAny();
	return issueObj.orElseThrow(IssueNotFoundException::new);
	}

	/*
	 * The below method should return the Employee Assigned to the issue given a
	 * issueId. It should return the employee in an Optional. If the issue is not
	 * assigned to any employee or the issue Id is incorrect the method should
	 * return empty optional
	 */
	@Override
	public Optional<Employee> getIssueAssignedTo(String issueId) {
		Optional<Employee> empOtional = issueDao.getIssues().stream().filter(issue -> issue.getIssueId().equalsIgnoreCase(issueId))
				.map(Issue::getAssignedTo).findAny();
		return empOtional.isPresent()?empOtional:empOtional.empty();
	}

	/*
	 * The below method should return the list of Issues given the status. The
	 * status can contain values OPEN / CLOSED
	 */
	@Override
	public List<Issue> getIssuesByStatus(String status) {
		return issueDao.getIssues().stream().filter(issue -> issue.getStatus().equalsIgnoreCase(status)).collect(Collectors.toList());
	}

	/*
	 * The below method should return a LinkedHashSet containing issueid's of open
	 * issues in the ascending order of expected resolution date
	 */
	@Override
	public Set<String> getOpenIssuesInExpectedResolutionOrder() {
		
		return issueDao.getIssues().stream().filter(issue -> issue.getStatus().equalsIgnoreCase("Open"))
				.sorted(Comparator.comparing(Issue :: getExpectedResolutionOn))
				.map(Issue::getIssueId).collect(Collectors.toCollection(LinkedHashSet::new));
	}

	/*
	 * The below method should return a List of open Issues in the descending order
	 * of Priority and ascending order of expected resolution date within a priority
	 */
	@Override
	public List<Issue> getOpenIssuesOrderedByPriorityAndResolutionDate() {
		return issueDao.getIssues().stream().filter(issue -> issue.getStatus().equalsIgnoreCase("Open"))
				.sorted(Comparator.comparing(Issue :: getPriority).reversed().thenComparing(Comparator.comparing(Issue :: getExpectedResolutionOn)))
				.collect(Collectors.toList());
	}

	/*
	 * The below method should return a List of 'unique' employee names who have
	 * issues not closed even after 7 days of Expected Resolution date. Consider the
	 * current date as 2019-05-01
	 */
	@Override
	public List<String> getOpenIssuesDelayedbyEmployees() {
		return issueDao.getIssues().stream().filter(issue -> issue.getStatus().equalsIgnoreCase("Open") 
				&& ChronoUnit.DAYS.between(issue.getExpectedResolutionOn(), today)>7)
				.map(i -> i.getAssignedTo().getName()).distinct()
				.collect(Collectors.toList());
	}

	/*
	 * The below method should return a map with key as issueId and value as
	 * assigned employee Id. THe Map should contain details of open issues having
	 * HIGH priority
	 */
	@Override
	public Map<String, Integer> getHighPriorityOpenIssueAssignedTo() {
	return issueDao.getIssues().stream().filter(issue -> issue.getStatus().equalsIgnoreCase("Open") && issue.getPriority().equalsIgnoreCase("High"))
			.collect(Collectors.toMap(Issue::getIssueId, issue -> issue.getAssignedTo().getEmplId()));
	}

	/*
	 * The below method should return open issues grouped by priority in a map. The
	 * map should have key as issue priority and value as list of open Issues
	 */
	@Override
	public Map<String, List<Issue>> getOpenIssuesGroupedbyPriority() {
		return issueDao.getIssues().stream().filter(issue -> issue.getStatus().equalsIgnoreCase("Open")).collect(Collectors.groupingBy(Issue::getPriority));
	}

	/*
	 * The below method should return count of open issues grouped by priority in a map. 
	 * The map should have key as issue priority and value as count of open issues 
	 */
	@Override
	public Map<String, Long> getOpenIssuesCountGroupedbyPriority() {
		return issueDao.getIssues().stream().filter(issue -> issue.getStatus().equalsIgnoreCase("Open"))
				.collect(Collectors.groupingBy(Issue :: getPriority,Collectors.counting()));
	}
	
	/*
	 * The below method should provide List of issue id's(open), grouped by location
	 * of the assigned employee. It should return a map with key as location and
	 * value as List of issue Id's of open issues
	 */
	@Override
	public Map<String, List<String>> getOpenIssueIdGroupedbyLocation() {
		
		return issueDao.getIssues().stream().filter(issue -> issue.getStatus().equalsIgnoreCase("Open"))
				.collect(Collectors.groupingBy(issue -> issue.getAssignedTo().getLocation(),
						Collectors.mapping(Issue :: getIssueId, Collectors.toList())));
	}
	
	/*
	 * The below method should provide the number of days, since the issue has been
	 * created, for all high/medium priority open issues. It should return a map
	 * with issueId as key and number of days as value. Consider the current date as
	 * 2019-05-01
	 */
	@Override
	public Map<String, Long> getHighMediumOpenIssueDuration() {
		return issueDao.getIssues().stream()
				.filter(issue -> issue.getStatus().equalsIgnoreCase("Open") && 
						(issue.getPriority().equalsIgnoreCase("High") || issue.getPriority().equalsIgnoreCase("Medium")))
				.collect(Collectors.toMap(Issue::getIssueId,i -> ChronoUnit.DAYS.between(i.getCreatedOn(), today)));
	}
}