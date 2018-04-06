package com.repository.sensedia;

import java.util.ArrayList;
import java.util.List;

public class ResourceOperation {
	private String name;
	private List<Parameter> requests = new ArrayList<Parameter>();
	private List<Parameter> responses = new ArrayList<Parameter>();
	private List<Parameter> exceptions = new ArrayList<Parameter>();

	public ResourceOperation (String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Parameter> getRequest() {
		return requests;
	}

	public void setRequests(Parameter request) {
		this.requests.add(request);
	}

	public List<Parameter> getResponses() {
		return responses;
	}

	public void setResponses(Parameter response) {
		this.responses.add(response);
	}

	public List<Parameter> getExceptions() {
		return exceptions;
	}

	public void setExceptions(Parameter exceptions) {
		this.exceptions.add(exceptions);
	}	
	
}
