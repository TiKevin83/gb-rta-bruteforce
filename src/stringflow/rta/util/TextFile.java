package stringflow.rta.util;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class TextFile implements Iterable<String> {

	private LinkedList<String> lines;
	private FileOutputStream writeContext;

	public TextFile(String filePath, String attributes) {
		try {
			lines = new LinkedList<String>();
			if(attributes.toLowerCase().contains("r")) {
				readFile(filePath);
			}
			if(attributes.toLowerCase().contains("w")) {
				lines.clear();
				writeContext = new FileOutputStream(filePath);
			} else if(attributes.toLowerCase().contains("a")) {
				readFile(filePath);
				writeContext = new FileOutputStream(filePath);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void readFile(String filePath) {
		try {
			lines.clear();
			BufferedReader reader = new BufferedReader(new FileReader(filePath));
			String line;
			while((line = reader.readLine()) != null) {
				lines.add(line);
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void write(String... lines) {
		for(String line : lines) {
			writeInternal(line, false);
		}
	}

	public void write(Collection<String> lines) {
		for(String line : lines) {
			writeInternal(line, false);
		}
	}

	public void writeln(String... lines) {
		for(String line : lines) {
			writeInternal(line, true);
		}
	}

	public void writeln(Collection<String> lines) {
		for(String line : lines) {
			writeInternal(line, true);
		}
	}

	public void save() {
		try {
			if(writeContext == null) {
				throw new IllegalStateException("No write context created.");
			}
			writeContext.write(getContent().getBytes());
			writeContext.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void writeInternal(String line, boolean newLine) {
		if(writeContext == null) {
			throw new IllegalStateException("No write context created.");
		}
		try {
			if(newLine) {
				lines.add(line + "\n");
				lines.add("");
			} else {
				if(lines.size() > 0) {
					String currentLine = lines.getLast();
					lines.removeLast();
					currentLine += line;
					lines.add(currentLine);
				} else {
					lines.add(line);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String getContent() {
		String result = "";
		for(String line : lines) {
			result += line;
		}
		return result;
	}

	public List<String> getContentAsList() {
		return lines;
	}

	public String[] getContentAsArray() {
		String result[] = new String[lines.size()];
		lines.toArray(result);
		return result;
	}

	public String getLineAt(int lineNumber) {
		return lines.get(lineNumber);
	}

	public int getNumLines() {
		return lines.size();
	}

	public void visitAll(ITextLineVisitor visitor) {
		visitInRage(0, lines.size(), visitor);
	}

	public void visitInRage(int min, int max, ITextLineVisitor visitor) {
		for(int lineNumber = min; lineNumber < max; lineNumber++) {
			visitor.onVisit(lines.get(lineNumber), lineNumber);
		}
	}

	public Iterator<String> iterator() {
		return lines.iterator();
	}
}