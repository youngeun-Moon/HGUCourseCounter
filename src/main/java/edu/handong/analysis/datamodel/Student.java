package edu.handong.analysis.datamodel;

import java.util.ArrayList;
import java.util.HashMap;

public class Student{
	private String studentId;
	private ArrayList<Course> coursesTaken ;
	private HashMap<String,Integer> semestersByYearAndSemester; 
	
	
	public Student(String studentId) {
		coursesTaken = new ArrayList<Course>();
		this.studentId =studentId;
		semestersByYearAndSemester = getSemestersByYearAndSemester();
	}
	
	public void addCourse(Course newRecord) {
		
		coursesTaken.add(newRecord);
	}
	
	public HashMap<String,Integer> getSemestersByYearAndSemester(){
		semestersByYearAndSemester = new HashMap<String,Integer>();
		int semester = 0;
		
		String comp = "a";
		for(Course lists:coursesTaken) {
			String date = lists.getYearTaken()+ "-" + lists.getSemesterCourseTaken();
			
			if(!date.equals(comp)) {
				semester ++;
				semestersByYearAndSemester.put(date,semester);
			}
			
			comp = date;
		}		
		
		return semestersByYearAndSemester;
	}
	
		
	
	public int getNumCourseInNthSementer(int semester) {
		
		int count = 0;
		for(Course lists:coursesTaken) {
			String key = lists.getYearTaken()+ "-" + lists.getSemesterCourseTaken();
			if(semester == semestersByYearAndSemester.get(key)) {
				count ++ ;
			}
		}
		
		return count;
	}
	

	public ArrayList<Course> getCoursesTaken() {
		return coursesTaken;
	}

}