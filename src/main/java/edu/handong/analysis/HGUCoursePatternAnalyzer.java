package edu.handong.analysis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import edu.handong.analysis.datamodel.Course;
import edu.handong.analysis.datamodel.Student;
import edu.handong.analysise.utils.NotEnoughArgumentException;
import edu.handong.analysise.utils.Utils;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

public class HGUCoursePatternAnalyzer {

	private HashMap<String,Student> students;
	
	String input;
	String output;
	String analysis;
	String coursecode;
	String startyear;
	String endyear;
	boolean help;
	
	/**
	 * This method runs our analysis logic to save the number courses taken by each student per semester in a result file.
	 * Run method must not be changed!!
	 * @param args
	 */
	
	public void run(String[] args) {
		
		Options options = createOptions();
		
		if(parseOptions(options, args)){
			
			if (help){
				printHelp(options);
				return;
			}
		/*
		try {
			// when there are not enough arguments from CLI, it throws the NotEnoughArgmentException which must be defined by you.
			if(args.length<2)
				throw new NotEnoughArgumentException();
		} catch (NotEnoughArgumentException e) {
			System.out.println(e.getMessage());
			System.exit(0);
		}
		*/
		
		//String dataPath = args[0]; // csv file to be analyzed
		//String resultPath = args[1]; // the file path where the results are saved.
			
		ArrayList<String> lines = Utils.getLines(input, true);
		
		
		students = loadStudentCourseRecords(lines);
				
		// To sort HashMap entries by key values so that we can save the results by student ids in ascending order.
		Map<String, Student> sortedStudents = new TreeMap<String,Student>(students); 
		
		
		if(Integer.parseInt(analysis)==1) {
			// Generate result lines to be saved.
			ArrayList<String> linesToBeSaved = countNumberOfCoursesTakenInEachSemester(sortedStudents);
			Utils.writeAFile(linesToBeSaved, output);
		}else {
			if(coursecode == null) {
				printHelp(options);
				return;
			}
			ArrayList<String> linesToBeSaved = calculateTheRate(sortedStudents);
			Utils.writeAFile(linesToBeSaved, output);
		}
		
		
		// Write a file (named like the value of resultPath) with linesTobeSaved.
		//Utils.writeAFile(linesToBeSaved, output);
		
		}
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
		Student[] student = new Student[lines.size()];
		
		/*newRecord[0] = new Course(lines.get(0));
		student[0] =new Student(newRecord[0].getStudentId());
		student[0].addCourse(newRecord[0]);
		*/
		int j=0, k=0;
		int startNum = Integer.parseInt(startyear);
		int endNum = Integer.parseInt(endyear);
		
		Course compa = new Course(lines.get(lines.size()-1));
		String comp = compa.getStudentId();
		
		Course ignore;
		
		for(int i=0; i<lines.size(); i++) {
			ignore = new Course(lines.get(i));
			int igNum = ignore.getYearTaken();
			
			//if(igNum < startNum || igNum > endNum) { continue;}
			
			newRecord[k] = new Course(lines.get(i));
			
			if(!comp.equals(newRecord[k].getStudentId())) {
				
				if(j!=0){
					records.put(comp,student[j-1]);
				}
				
				student[j] = new Student(newRecord[k].getStudentId());
				j++;
			}
			if(i==lines.size()-1) {
				records.put(newRecord[k].getStudentId(), student[j-1]);
			}
			
			student[j-1].addCourse(newRecord[k]);
			comp = newRecord[k].getStudentId();
			k++;
			
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
		countNumber.add(0,"StudentID, TotalNumberOfSemestersRegistered, Semester, NumCoursesTakenInTheSemester");
		
		return countNumber; // do not forget to return a proper variable.
	}
	
	
	/**
	 * This method calculates the rate of students who took the class and shows it on a yearly basis.
	 * @param sortedStudents
	 * @return
	 */
	private ArrayList<String> calculateTheRate(Map<String, Student> sortedStudents){
		ArrayList<String> results = new ArrayList<String>();
		String courseName = "a";
		
		for(int year = Integer.parseInt(startyear); year <= Integer.parseInt(endyear); year++ ) {
			
			for(int semester = 1 ; semester <=4 ; semester++) {
				int totalStudents = 0, studentsTaken = 0;
				
				for(int k = 1 ; k<= sortedStudents.size() ; k++) {
					String id = String.format("%04d", k);
					
					if(sortedStudents.get(id).getSemestersByYearAndSemester().containsKey(Integer.toString(year)+"-"+Integer.toString(semester))) {
						
						totalStudents ++;
						
						for(Course array:sortedStudents.get(id).getCoursesTaken()) {
							if(array.getCourseCode().equals(coursecode)&&array.getYearTaken() == year &&array.getSemesterCourseTaken() == semester) {
								
								studentsTaken++ ;
							
								if(courseName.equals("a")) {
									courseName = array.getCourseName();
								}
							}
						}
						
					}
				}
				float rate = (float)studentsTaken/(float)totalStudents*100 ;
				String theRate = String.format("%.1f", rate);
				results.add(year+","+semester+","+coursecode+","+courseName+","+Integer.toString(totalStudents)+","+Integer.toString(studentsTaken)+","+theRate+"%");
			}
		}
		results.add(0, "Year,Semester,CouseCode,CourseName,TotalStudents,StudentsTaken,Rate");
		return results;
	}
	
	/**
	 * This method implements a logic that parse options typed by users in CLI.
	 * @param options
	 * @param args
	 * @return
	 */
	private boolean parseOptions(Options options, String[] args) {
		CommandLineParser parser = new DefaultParser();

		try {

			CommandLine cmd = parser.parse(options, args);
			
			input = cmd.getOptionValue("i");
			output = cmd.getOptionValue("o");
			analysis = cmd.getOptionValue("a");
			coursecode = cmd.getOptionValue("c");
			startyear = cmd.getOptionValue("s");
			endyear = cmd.getOptionValue("e");
			help = cmd.hasOption("h");

		} catch (Exception e) {
			printHelp(options);
			return false;
		}

		return true;
	}

	/**
	 * This method creates an instance of Options class.
	 * This method adds each option in the options instance.
	 * @return
	 */
	private Options createOptions() {
		Options options = new Options();

		// add options by using OptionBuilder
		options.addOption(Option.builder("i").longOpt("input")
				.desc("Set an input file path")
				.hasArg()
				.argName("Input path")
				.required()
				.build());
		
		
		options.addOption(Option.builder("o").longOpt("output")
				.desc("Set an output file path")
				.hasArg()
				.argName("Output path")
				.required()
				.build());
		

		options.addOption(Option.builder("a").longOpt("analysis")
				.desc("1: Count courses per semester, 2: Count per course name and year")
				.hasArg()
				.argName("Analysis option")
				.required()
				.build());
		
		
		options.addOption(Option.builder("c").longOpt("coursecode")
				.desc("Course code for '-a 2' option")
				.hasArg()
				.argName("course code")
				//.required() // this is an optional option. So disabled required().
				.build());
		
		
		options.addOption(Option.builder("s").longOpt("startyear")
				.desc("Set the start year for analysis e.g., -s 2002")
				.hasArg()
				.argName("Start year for analysis")
				.required()
				.build());
		
		
		options.addOption(Option.builder("e").longOpt("endyear")
				.desc("Set the end year for analysis e.g., -e 2005")
				.hasArg()
				.argName("End year for analysis")
				.required()
				.build());
		
		
		options.addOption(Option.builder("h").longOpt("help")
		        .desc("Help")
		        .build());

		return options;
	}
	
	/**
	 * This method automatically generate the help statement.
	 * @param options
	 */
	private void printHelp(Options options) {
		HelpFormatter formatter = new HelpFormatter();
		String header = "HGU Course Analyzer";
		String footer ="";
		formatter.printHelp("HGUCourseCounter", header, options, footer, true);
	}

}
