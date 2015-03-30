package com.nzion.tally;

public interface Visitable {
	
	public void accept(Visitor visitor) throws Exception;
}
