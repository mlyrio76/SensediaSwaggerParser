package com.repository.sensedia;

import java.util.ArrayList;
import java.util.List;

public class Resource {

	private String resource;
	private String httpVerb;
	private List<ResourceOperation> operations = new ArrayList<ResourceOperation>();
	
	Resource(String r) {
		this.resource = r;
	}

	public String getResource() {
		return resource;
	}
	
	public void setResource( String r) {
		this.resource = r;
	}

	public String getHttpVerb() {
		return httpVerb;
	}

	public void setHttpVerb(String httpVerb) {
		this.httpVerb = httpVerb;
	}

	public List<ResourceOperation> getOperation() {
		return operations;
	}

	public void setOperation(ResourceOperation operation) {
		this.operations.add(operation);
	}


	
}
