import java.util.LinkedHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class SymbolScanner {
	
	private LinkedHashMap<String, Integer> map;
	private boolean scanned;
	private int base;
	
	public SymbolScanner(int baseAddr) {
		
		scanned = false;
		base = baseAddr;
		map = new LinkedHashMap<String, Integer>();
	}
	
	public int getBaseAddr() {
		return base;
	}
	
	/** 返回指定标号所在的位置相对开始的offset(需要>>2) **/
	public int findSymbolOffset(String symbol) throws AssembleException {
		
		if(!scanned)
			throw new AssembleException("[SymbolScanner.findSymbolOffset]The SymbolScanner hasn't scanned any code yet.");
		
		Integer result = map.get(symbol);
		
		if(result == null)
			throw new AssembleException("[SymbolScanner.findSymbolOffset]Symbol: " + symbol + " not found.");
		
		return result;
	}
	
	/** 返回指定标号的绝对位置(需要>>2) **/
	public int findSymbolTarget(String symbol) throws AssembleException {
		return getBaseAddr()/4 + findSymbolOffset(symbol);
	}
	
	/** 扫描代码，构建符号表 **/
	public String replaceText(String str) throws AssembleException {
		
		if(scanned)
			throw new AssembleException("[SymbolScanner.replaceText]This scanner is already scanned some code and cannot rescan.");
		scanned = true;
		
		String[] strArr  = str.split("\n");
		Pattern  pattern = Pattern.compile("^(.+):$");
		Matcher  matcher;
		String   result = "";
		
		for(int pc = 0, i = 0; i < strArr.length; i ++) {
			
			matcher = pattern.matcher(strArr[i]);
			
			if(matcher.matches()) {
				
				String symbol = matcher.group(1);
				if(map.get(symbol) != null)
					throw new AssembleException("[SymbolScanner.replaceText]Duplicate symbol: " + symbol + " at line" + i + ".");
				map.put(symbol, pc);
			} else {
				
				if(result.length() > 0)
					result += "\n";
				result += strArr[i];
				pc ++;
			}
		}
		
		return result;
	}
}
