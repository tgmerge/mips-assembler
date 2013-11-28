import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PseudoReplacer {
	LinkedHashMap<Pattern, String> dict = new LinkedHashMap<Pattern, String>();

	public PseudoReplacer(String fileName) throws IOException, AssembleException  {
		readFile(fileName);
	}
	
	private void readFile(String name) throws IOException, AssembleException {
		BufferedReader file = new BufferedReader(new FileReader(name));
		String p1 = "", p2 = "", line;
		while((line = file.readLine()) != null) {
			p1 = line;
			p2 = file.readLine();
			try {
				while( (line = file.readLine()).charAt(0) != '#' )
					p2 += '\n' + line;
			} catch(Exception e) {
				file.close();
				throw new AssembleException("[PseudoReplacer.readFile]Seems like no # at the end of dict data."); 
			}
			dict.put(Pattern.compile(p1), p2);
		}
		file.close();
		return;
	}

	// 一次处理整个文本
	public String replaceText(String str) {
		
		String[] strArr = str.split("\n");
		String   result = "";
		
		for(int i = 0; i < strArr.length; i ++) {
			if(result.length() > 0)
				result += "\n";
			result += replaceLine(strArr[i]);
		}
		
		return result;
	}
	
	// 处理一行
	private String replaceLine(String str) {
		Iterator<Entry<Pattern, String>> iter = dict.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry<Pattern, String> pair = iter.next();
			Matcher matcher = pair.getKey().matcher(str);
			if(matcher.matches()) {
				str = matcher.replaceFirst(pair.getValue());
			}
		}
		return str;
	}
	
}
