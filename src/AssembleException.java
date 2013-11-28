
@SuppressWarnings("serial")
public class AssembleException extends Exception {
	
	String information;

	public AssembleException(String info) {
		information = info;
	}
	
	public void AddInfo(String info) {
		information = info + "\n" + information;
	}
	
	public void printInfo() {
		System.out.println(information);
	}
}
