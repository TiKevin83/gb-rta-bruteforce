package stringflow.rta.util;

import java.io.*;
import java.lang.reflect.Array;
import java.util.*;

public class TextFile extends ArrayList<String> {

	public TextFile(String filePath) {
		try {
			BufferedReader reader = new BufferedReader(new FileReader(new File(filePath)));
			String line;
			while((line = reader.readLine()) != null) {
				add(line);
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public TextFile(Collection<String> collection) {
		super(collection);
	}
	
	public TextFile subList(int fromIndex, int toIndex) {
		return new TextFile(super.subList(fromIndex, toIndex));
	}
	
	public String getContent() {
		String result = "";
		for(String line : this) {
			result += line + "\n";
		}
		return result;
	}

	public int getNumLines() {
		return size();
	}
}