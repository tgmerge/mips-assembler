import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Assembler {

	public static void main(String[] args) {

		String path;
		if(args.length < 1) {
			System.out.println("[Info]Using default filename: test.asm");
			path = "test.asm";
		} else {
			System.out.println("[Info]Filename: "+args[0]);
			path = args[0];
		}
		
		String str;
		try {
			str = new String(Files.readAllBytes(Paths.get(path)));
		} catch (IOException e) {
			System.out.println("[IOException]Failed to open file.");
			e.printStackTrace();
			return;
		}			

		Preprocessor   pre;
		PseudoReplacer pseudo;
		SymbolScanner  symbol;
		OpTranslator   op; 
		
		try {
			pre    = new Preprocessor("pre.txt");
			pseudo = new PseudoReplacer("pseudo.txt");
			symbol = new SymbolScanner(35840);								// baseAddr, TODO
			op     = new OpTranslator("op.txt", symbol);
		} catch (AssembleException e) {
			System.out.println("[AssembleException]Failed to assemble:");
			e.printInfo();
			return;
		} catch (IOException e) {
			System.out.println("[IOException]Failed to assemble:");
			e.printStackTrace();
			return;
		}
		
		try {
			str = pre.replaceText(str);
			str = pseudo.replaceText(str);
			str = symbol.replaceText(str);
			str = op.replaceText(str);
		} catch(AssembleException e) {
			System.out.println("[AssembleException]Failed to assemble:");
			e.printInfo();
			return;
		}

		System.out.println("[Assembler]Success.");
		System.out.println(toHex(str));
		return;
	}
	
	static public String toHex(String str) {
		String result = "";
		String[] arr = str.split("\n");
		for(int i = 0; i < arr.length; i ++) {
			for(int j = 0; j < 32; j += 4)
				result += Integer.toHexString(Integer.parseInt(arr[i].substring(j, j+4), 2));
			result += "\n";
		}
		return result;
	}
}
