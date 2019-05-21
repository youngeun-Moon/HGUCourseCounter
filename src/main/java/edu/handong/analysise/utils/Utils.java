package edu.handong.analysise.utils;

import java.util.ArrayList;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.io.File;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.DataOutputStream;

public class Utils{
	
	public static ArrayList<String> getLines(String file, boolean removeHeader){
		Scanner inputStream = null;
		
		try {
			inputStream = new Scanner(new File(file));
		}catch(FileNotFoundException e){
			System.out.println("The file path does not exist. Please check your CLI argument!");
			System.exit(0);
		}
		
		ArrayList<String> lines = new ArrayList<String>();
		
		while (inputStream.hasNextLine()) {
            
            lines.add(inputStream.nextLine ());
        }
		inputStream.close();
		
		if(removeHeader) {
			lines.remove(0);
		}
		
		return lines;
	}
	
	public static void writeAFile(ArrayList<String> lines, String targetFileName){
		
		
		try {
			File file= new File(targetFileName);
			FileOutputStream fos = new FileOutputStream(file);
			DataOutputStream dos=new DataOutputStream(fos);
			
			for(String line:lines){
				dos.write((line+"\n").getBytes());
			}
			
			dos.close();
			fos.close();
		} catch (IOException e) {
			e.printStackTrace();
		} 
		
	}

}