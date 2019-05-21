package edu.handong.analysis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import edu.handong.analysis.datamodel.Course;
import edu.handong.analysis.datamodel.Student;
import edu.handong.analysise.utils.NotEnoughArgumentException;
import edu.handong.analysise.utils.Utils;

public class HGUCoursePatternAnalyzer {

	private HashMap<String,Student> students;
	
	/**
	 * This method runs our analysis logic to save the number courses taken by each student per semester in a result file.
	 * Run method must not be changed!!
	 * @param args
	 */
	public void run(String[] args) {
		
		try {
			// when there are not enough arguments from CLI, it throws the NotEnoughArgmentException which must be defined by you.
			if(args.length<2)
				throw new NotEnoughArgumentException();
		} catch (NotEnoughArgumentException e) {
			System.out.println(e.getMessage());
			System.exit(0);
		}
		
		String dataPath = args[0]; // csv file to be analyzed
		String resultPath = args[1]; // the file path where the results are saved.
		ArrayList<String> lines = Utils.getLines(dataPath, true);
		
		students = loadStudentCourseRecords(lines);
				
		// To sort HashMap entries by key values so that we can save the results by student ids in ascending order.
		Map<String, Student> sortedStudents = new TreeMap<String,Student>(students); 
		
		// Generate result lines to be saved.
		ArrayList<String> linesToBeSaved = countNumberOfCoursesTakenInEachSemester(sortedStudents);
		
		
		// Write a file (named like the value of resultPath) with linesTobeSaved.
		Utils.writeAFile(linesToBeSaved, resultPath);
	
	}
	
	/**
	 * This method create HashMap<String,Student> from the data csv file. Key is a student id and the corresponding object is an instance of Student.
	 * The Student instance have all the Course instances taken by the student.
	 * @param lines
	 * @return
	 */
	private HashMap<String,Student> loadStudentCourseRecords(ArrayList<String> lines) {
		
		// TODO: Implement this method
		HashMap<String,Student> records = new HashMap<String,Student>();
		
		Course[] newRecord = new Course[lines.size()];
		newRecord[0] = new Course(lines.get(0));
		Student[] student = new Student[lines.size()];
		student[0] =new Student(newRecord[0].getStudentId());
		student[0].addCourse(newRecord[0]);
		String comp = newRecord[0].getStudentId();
		int j=1;
		
		for(int i=1; i<lines.size(); i++) {
			newRecord[i] = new Course(lines.get(i));
			
			if(!comp.equals(newRecord[i].getStudentId())) {
				records.put(comp,student[j-1]);
				
				student[j] = new Student(newRecord[i].getStudentId());
				j++;
			}
			if(i==lines.size()-1) {
				records.put(newRecord[i].getStudentId(), student[j-1]);
			}
			
			
			student[j-1].addCourse(newRecord[i]);
			comp = newRecord[i].getStudentId();
		}
		
		
		
		return records; // do not forget to return a proper variable.
	}

	/**
	 * This method generate the number of courses taken by a student in each semester. The result file look like this:
	 * StudentID, TotalNumberOfSemestersRegistered, Semester, NumCoursesTakenInTheSemester
	 * 0001,14,1,9
     * 0001,14,2,8
	 * ....
	 * 
	 * 0001,14,1,9 => this means, 0001 student registered 14 semeters in total. In the first semeter (1), the student took 9 courses.
	 * 
	 * 
	 * @param sortedStudents
	 * @return
	 */
	private ArrayList<String> countNumberOfCoursesTakenInEachSemester(Map<String, Student> sortedStudents) {
		
		// TODO: Implement this method
		ArrayList<String> countNumber = new ArrayList<String>();
		
		for(int i = 1; i<=sortedStudents.size() ; i++) {
			String id = String.format("%04d", i);
			
			for(int j = 1; j<=sortedStudents.get(id).getSemestersByYearAndSemester().size() ; j++) {
				countNumber.add(i + ", " + sortedStudents.get(id).getSemestersByYearAndSemester().size() + ", " + j + ", " + sortedStudents.get(id).getNumCourseInNthSementer(j));
			} 
			
		}
		
		return countNumber; // do not forget to return a proper variable.
	}
}
