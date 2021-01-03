/*
 * Author: Danny H Duong
 * NetID: dhd170001
 * 
 * */

package Tickets;

import Tickets.*;
import java.util.*;
import java.io.*;
import java.util.HashMap; 
import java.util.Map; 
import java.util.ArrayList;

public class Main {
	
    static HashMap<String, ArrayList<Integer>> map = new HashMap<>(); 
	
	public static void main(String[] args) throws FileNotFoundException
	{
		PrintWriter error = new PrintWriter(new File("error.log")); // is created to be used to print to error.log file errors with invalid lines
		
		Scanner inventory = new Scanner(new File("inventory.dat"));
		
		//this while loop is meant to read in the file from inventory.dat to store it into a string array as a Node while also separating each key
		while (inventory.hasNextLine())
		{
			String[] keys = inventory.nextLine().split(",");
			map.put(keys[0].replaceAll("\"", ""), new ArrayList<Integer>(Arrays.asList(Integer.parseInt(keys[1]), Integer.parseInt(keys[2]))));
		}
		inventory.close();
				
		// the following is used to read file transaction.log and read in each line with its respective command that it provides to do the following instructions
		Scanner transaction = new Scanner(new File("transaction.log"));
		while (transaction.hasNextLine())
		{
			String line = transaction.nextLine();
			
			//The following acts as a buffer space in order to replace these with actual values later on
			String blanks = line.replaceFirst(" ", "_");
			blanks = blanks.replaceAll(",", "_");

			//allows there to be no real empty strings in array and adds the size of the array based on blanks
			String[] newKeys = blanks.split("_"); 
			
			//the following are instructions that are performed based on key words being read in
			if(newKeys[0].equals("add"))// word[0] = 'add'
			{
				if (!check(newKeys, 3)) // check condition
				{
					error.println(line); // if line in file does not hold appropriate amount of words for the function, line with error will be written to file
				}
				else // condition is used to replace initial blanks in array with the title and use its respective number to sort in binary tree, to add title
				{
					String title = newKeys[1].replaceAll("\"", "");
					boolean checkTitle = map.containsKey(title);
					// if checkTitle = true then movie exists
					if (checkTitle) // If the movie exists
					{
						map.get(title).set(0, map.get(title).get(0) + Integer.parseInt(newKeys[2])); // Increase its number of copies
					}
					else
					{
						map.put(title, new ArrayList<Integer>(Arrays.asList(Integer.parseInt(newKeys[2]), 0)));// Create a new movie with that title
					}
				}
			}
			else if(newKeys[0].equals("remove")) // word[0] = 'remove'
			{
				String check1 = newKeys[1].replaceAll("\"", "");

					if (!check(newKeys, 3) || (Integer.parseInt(newKeys[2]) > map.get(check1).get(0))) // check condition
					{
						error.println(line); // if line in file does not hold appropriate amount of words for the function, line with error will be written to file
					}
					else// condition is used to replace initial blanks in array with the title and use its respective number to sort in binary tree, to remove title from tree
					{
						String title1 = newKeys[1].replaceAll("\"", "");
						//map.get(title1).get(1);
						map.get(title1).set(0, map.get(title1).get(0) - Integer.parseInt(newKeys[2]));
						if (map.get(title1).get(0) == 0 && map.get(title1).get(1) == 0) 
						{
							map.remove(title1);
						}
					}
			}
			else if(newKeys[0].equals("rent"))// word[0] = 'rent' 
			{
				String check3 = newKeys[1].replaceAll("\"", "");

					if (!check(newKeys, 2) || map.get(check3).get(0) == 0) // check condition
					{
						error.println(line); // if line in file does not hold appropriate amount of words for the function, line with error will be written to file
					}
					else// condition is used to replace initial blanks in array with the title and use its respective number to sort in binary tree, to replace title and sort tree
					{
						String title = newKeys[1].replaceAll("\"", "");
						map.get(title).set(0, map.get(title).get(0) - 1); // Decrease its availability by one
						map.get(title).set(1, map.get(title).get(1) + 1); // Increase its number of rentals by one
					}
			}
			else if(newKeys[0].equals("return"))// word[0] = 'return'
			{
					if (!check(newKeys, 2)) // check condition
					{
						error.println(line); // if line in file does not hold appropriate amount of words for the function, line with error will be written to file
					}
					else// condition is used to replace initial blanks in array with the title and use its respective number to sort in binary tree, to return a title
						// and increase amount available and decrease amount rented
					{
						String title = newKeys[1].replaceAll("\"", "");
						map.get(title).set(0, map.get(title).get(0) + 1);
						map.get(title).set(1, map.get(title).get(1) - 1);
					}
			}
			else // word[0] does not equal a key word
			{ 
				error.println(line); // if line in file does not hold appropriate amount of words for the function, line with error will be written to file
			}
		}
		transaction.close();
		
		error.close();
		
		// outputs content to respective file through using java library of PrintWriter
		PrintWriter report = new PrintWriter(new File("redbox_kiosk.txt"));
		report.println("1st is title\t2nd is number of copies available\t3rd is number of copies rented"); 
		sortTitles(report);
		report.close();
	}
	
	
	/** 
	 * Method: sortTitles(reports)
	 * Description: sorts the titles alphabetically using a treemap while also writing them along with
	 * the number available and rented to the file
	 * @param reports contains the file to be written to which is redbox_kiosk.txt in this case
	 * 
	 * **/
	public static void sortTitles(PrintWriter reports) 
    { 
        // TreeMap to store values of HashMap 
        TreeMap<String, ArrayList<Integer>> sortingMap = new TreeMap<>(); 
  
        // Copy all data from hashMap into TreeMap 
        sortingMap.putAll(map); 
  
        // Display the TreeMap which is naturally sorted 
        for (Map.Entry<String, ArrayList<Integer>> iteration : sortingMap.entrySet()) 
        {
            reports.println(iteration.getKey() + "\t\t" + iteration.getValue().get(0) + "\t\t" + iteration.getValue().get(1));  
        }
    } 
	
	/** 
	 * Method: check(words, numExpected)
	 * Description: Checks to see in the array that has stored words in it matches the number of words that is expected for each case
	 * @param words is an array of string values that are the keys for titles
	 * @param numExpected is the number of words that are required to be a specific action in main
	 * @return is a boolean value that returns true if the number expected matches the length of words in array
	 * 
	 * **/
	public static boolean check(String[] numOfWords, int numOfExpected)
	{
		if (numOfWords.length != numOfExpected) // in the case that there is not correct number of words
		{
			return false;
		}
		else // if there are at least potential for correct number of words
		{
			if (numOfWords.length >= 2)
				if (numOfWords[1].charAt(0) == '\"' && numOfWords[1].charAt(numOfWords[1].length() - 1) == '\"' && numOfWords[1].length() - numOfWords[1].replaceAll("\"", "").length() == 2)
				{
					if (numOfWords.length == 3) 
					{
						try
						{
							Integer checkIfInt = Integer.parseInt(numOfWords[2]);
							checkIfInt+=1;
							return true; 
						} catch(Exception e)
						{
							return false;
						}
					}
					else
					{
						return true; 
					}
				}
				else
				{
					return false; 
				}
		}
		return true;
	}
}
