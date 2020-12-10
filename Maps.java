import java.util.*;
import java.util.stream.*;

public class Maps {

	public static Boolean isEven(int i){
		System.out.println("hello");
		return i % 2 == 0;
	}


	public static List<Boolean> isBoolean2(List<Integer> list) {
		//return list.stream().map(num -> num % 2 == 0).collect(Collectors.toList());
		return list.stream().map(Maps::isEven).collect(Collectors.toList());
	}

	public static void main(String[] args) {
		ArrayList<Integer> currentMatches = new ArrayList<Integer>();
		for (String arg : args) {
			currentMatches.add(Integer.valueOf(arg));

		}
		System.out.println(isBoolean2(currentMatches));
	}
}