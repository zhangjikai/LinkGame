package nec.soft.java.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class FileHelper {
	public static String folder = "config";

	private static void checkFolder() {
		File file = new File(folder);
		if (!file.exists()) {
			file.mkdir();
		}
	}

	public static void writeToFile(String fileName, String message) {
		writeToFile(fileName, message, true);
	}

	public static void writeToFile(String fileName, String message, boolean append) {
		try {
            //ClassLoader classLoader = FileHelper.class.getClassLoader();
            //File file = new File(classLoader.getResource(fileName).getFile());
			checkFolder();
			File file = new File(folder + File.separator + fileName);
			System.out.println(fileName);
			if (!file.exists())
				file.createNewFile();
			FileWriter writer = new FileWriter(file, append);
			BufferedWriter bWriter = new BufferedWriter(writer);
			bWriter.write(message);
			bWriter.close();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static String readFromFile(String fileName) {
		try {

			//ClassLoader classLoader = FileHelper.class.getClassLoader();
			//File file = new File(classLoader.getResource(fileName).getFile());
			checkFolder();
			File file = new File(folder + File.separator + fileName);
			if (!file.exists())
				file.createNewFile();
			BufferedReader br = new BufferedReader(new FileReader(file));
			StringBuffer sb = new StringBuffer();
			String line = br.readLine();
			while (line != null) {
				sb.append(line);
				line = br.readLine();
			}
			br.close();
			return sb.toString();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
}
