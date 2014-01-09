import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OpTranslator {

	private LinkedHashMap<String, String>  dict;
	private HashMap<String, Integer>       regDict;
	private SymbolScanner                  symbolScanner;
	
	public OpTranslator(String fileName, SymbolScanner s) throws AssembleException, IOException {
		
		dict = new LinkedHashMap<String, String>();
		regDict = new HashMap<String, Integer>();
		symbolScanner = s;
		
		readFile(fileName);
		initRegDict();
	}
	
	// 建立寄存器名字典
	private void initRegDict() {
		
		String[] regs = {"$zero","$at","$v0","$v1","$a0","$a1","$a2","$a3",
						  "$t0","$t1","$t2","$t3","$t4","$t5","$t6","$t7",
						  "$s0","$s1","$s2","$s3","$s4","$s5","$s6","$s7",
						  "$t8","$t9","$k0","$k1","$gp","$sp","$fp","$ra"};
		
		regDict.put("$s8", 30);
		for(int i = 0; i < regs.length; i ++) {
			regDict.put(regs[i], i);
			regDict.put("$"+i, i);
		}
	}
	
	// 读取字典
	private void readFile(String name) throws AssembleException, IOException {
		
		BufferedReader file = new BufferedReader(new FileReader(name));
		String p1 = "", p2 = "", line;
		while((line = file.readLine()) != null) {
			p1 = line;
			p2 = file.readLine();
			line = file.readLine();
			if(line.charAt(0) != '#') {
				file.close();
				throw new AssembleException("[OpTranslator.readFile]No # at the end of dict data."); 
			}
			dict.put(p1, p2);
		}
		file.close();
		return;
	}
	
	// 替换整个string
	public String replaceText(String str) throws AssembleException {
		
		String[] strArr = str.split("\n");
		String result = "";
		for(int pos = 0; pos < strArr.length; pos ++) {
			if(result.length() > 0)
				result += "\n";
			result += replaceLine(strArr[pos], pos);
		}
		return result;
	}
	
	// 替换一行, pos是行数（未×4）
	private String replaceLine(String code, int pos) throws AssembleException {
		String[] codeArr = code.split("[ ,\\(\\)]");
		String[] patternArr = null;
		String   result = null;
		
		Iterator<Entry<String, String>> iter = dict.entrySet().iterator();
		while(iter.hasNext()) {
			Entry<String, String> item = iter.next();
			String[] tmpArr = item.getKey().split("[ ,\\(\\)]");
			if( tmpArr[0].equals(codeArr[0]) ) {
				patternArr = item.getKey().split("[ ,\\(\\)]");
				result = item.getValue();
				break;
			}
		}
		
		if(patternArr == null)
			throw new AssembleException("[OpTranslator.replaceLine]Line "+pos+":["+code+"] has error:\n" + "Opcode " + codeArr[0] + " not found.");
		if(codeArr.length != patternArr.length)
			throw new AssembleException("[OpTranslator.replaceLine]Line "+pos+":["+code+"] has error:\n" + "Wrong number of operation numbers.");
		
		try{
			for(int i = 1; i < patternArr.length; i ++) {
	
				if(patternArr[i].charAt(0) == '$') {
					
					// $x
					// e.g. sssss => s+
					Matcher m = Pattern.compile(patternArr[i].charAt(1)+"+").matcher(result);
					result = m.replaceFirst(regToBin(codeArr[i]));
					
				} else if(patternArr[i].equals("imm")) {
					
					// imm
					Matcher m = Pattern.compile("i").matcher(result);
					int immlen = 0;
					while(m.find())
					    immlen  ++;
					m = Pattern.compile("i+").matcher(result);
					result = m.replaceFirst(toBin(Integer.parseInt(codeArr[i]), immlen));
					
				} else if(patternArr[i].equals("offset")) {
					
					// offset
					Matcher m = Pattern.compile("i").matcher(result);
					int off = ( symbolScanner.findSymbolOffset(codeArr[i]) - pos - 1 );
					int offlen = 0;
					while(m.find())
						offlen ++;
					m = Pattern.compile("i+").matcher(result);
					result = m.replaceFirst(toBin(off, offlen));
					
				} else if(patternArr[i].equals("target")) {
					
					// target
					Matcher m = Pattern.compile("i").matcher(result);
					int tar = ( symbolScanner.findSymbolTarget(codeArr[i]) );
					int tarlen = 0;
					while(m.find())
						tarlen ++;
					m = Pattern.compile("i+").matcher(result);
					result = m.replaceFirst(toBin(tar, tarlen));
					
				}
			}
		} catch(AssembleException e) {
			e.AddInfo("[OpTranslator.replaceLine]Line "+pos+":["+code+"] has error:");
			throw e;
		}

		return result;
	}
	
	// $t0 -> 01000,  $8 -> 01000
	private String regToBin(String reg) throws AssembleException {
		
		Integer regValue = regDict.get(reg);
		if(regValue == null)
			throw new AssembleException("[OpTranslator.regToBin]Registry name: ["+reg+"] not found.");
		return toBin(regValue, 5);
	}
	
	// 8 -> "01000"
	private String toBin(int value, int length) {
		String bin = Integer.toBinaryString(value);
		bin = "00000000000000000000000000000000" + bin;
		bin = bin.substring(bin.length()-length);
		return bin;
	}
	
}
