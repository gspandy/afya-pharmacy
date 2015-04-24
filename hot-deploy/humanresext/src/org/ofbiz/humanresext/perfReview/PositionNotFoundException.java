package org.ofbiz.humanresext.perfReview;

import org.ofbiz.service.GenericServiceException;

public final class PositionNotFoundException extends GenericServiceException {
	
	public PositionNotFoundException(String msg){
		super(msg);
	}
}
