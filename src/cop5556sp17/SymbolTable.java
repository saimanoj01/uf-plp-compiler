package cop5556sp17;



import cop5556sp17.AST.Dec;

import java.util.HashMap;
import java.util.ListIterator;
import java.util.Stack;


public class SymbolTable {

	HashMap<String, HashMap<Integer, Dec>> symbolTable = new HashMap<String, HashMap<Integer, Dec>>();
	Stack<Integer> scopeStack = new Stack<Integer>();
	int scopeCounter = 0;

	/** 
	 * to be called when block entered
	 */
	public void enterScope(){
		scopeStack.push(scopeCounter++);
	}
	
	
	/**
	 * leaves scope
	 */
	public void leaveScope(){
		scopeStack.pop();
	}


	public boolean insert(String ident, Dec dec){
		int currentScope = scopeStack.peek();
		if(symbolTable.containsKey(ident)) {
			HashMap<Integer, Dec> map = symbolTable.get(ident);
			if(map.containsKey(currentScope))
				return false;
			map.put(currentScope, dec);
		}
		else {
			HashMap<Integer, Dec> map = new HashMap<Integer, Dec>();
			map.put(currentScope, dec);
			symbolTable.put(ident, map);
		}
		return true;
	}
	
	public Dec lookup(String ident){
		if(!symbolTable.containsKey(ident))
			return null;
		HashMap<Integer, Dec> map = symbolTable.get(ident);
		ListIterator<Integer> listIterator = scopeStack.listIterator(scopeStack.size());
		while(listIterator.hasPrevious()) {
			int key = listIterator.previous();
			if(map.containsKey(key)) {
				return map.get(key);
			}
		}
		return null;
	}
		
	public SymbolTable() {
		scopeStack.push(scopeCounter++);
	}


	@Override
	public String toString() {
		//TODO:  IMPLEMENT THIS
		return "";
	}
	
	


}
