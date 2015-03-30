package com.nzion.tally;


public interface Visitor {
	public void visit(Ledger node) throws Exception;
}
