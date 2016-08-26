package com.powerfin.exception;

import org.openxava.util.*;

public class InternalException extends XavaException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	public InternalException()
	{
		super();
	}
	
	public InternalException(String idOrMessage) { 
		super(idOrMessage);
	}
	
	public InternalException(String idOrMessage, Object argv0) { 
		super(idOrMessage, argv0);
	}
	
	public InternalException(String idOrMessage, Object argv0, Object argv1) { 
		super(idOrMessage, argv0, argv1);
	}
	
	public InternalException(String idOrMessage, Object argv0, Object argv1, Object argv2) { 
		super(idOrMessage, argv0, argv1, argv2);
	}
	
	public InternalException(String idOrMessage, Object argv0, Object argv1, Object argv2, Object argv3) { 
		super(idOrMessage, argv0, argv1, argv2, argv3);
	}
	
	public InternalException(String idOrMessage, Object argv0, Object argv1, Object argv2, Object argv3, Object argv4) { 
		super(idOrMessage, argv0, argv1, argv2, argv3, argv4);
	}
}
