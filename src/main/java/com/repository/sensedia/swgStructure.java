package com.repository.sensedia;

import io.swagger.parser.SwaggerParser;
import io.swagger.models.Operation;
import io.swagger.models.Path;
import io.swagger.models.Response;
import io.swagger.models.Swagger;
import io.swagger.models.ArrayModel;
import io.swagger.models.Model;
import io.swagger.models.parameters.BodyParameter;
import io.swagger.models.parameters.FormParameter;
import io.swagger.models.parameters.HeaderParameter;
import io.swagger.models.parameters.QueryParameter;
import io.swagger.models.parameters.PathParameter;
import io.swagger.models.properties.Property;
import io.swagger.models.properties.ArrayProperty;

import io.swagger.models.properties.RefProperty;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;


public class swgStructure {

	private String URI;
	private Swagger SWG;
	private List<Resource> OperationResource = new ArrayList<Resource>();
	

	//Constructor
	public swgStructure(String URI) {
		this.URI = URI;
		this.SWG = new SwaggerParser().read(URI);

		for (Map.Entry<String, Path> path : SWG.getPaths().entrySet()) {
			addOperationStruct(path.getKey(), SWG.getPath(path.getKey()));
		}
	}

	//Class GETs and SETs
	public String getURI() {
		return URI;
	}
	
	public List<Resource> getOperationResource(){
		return OperationResource;
	}

	//Class Methods
	private void addOperationStruct (String resource, Path path) {
		addOperationItemStruct("GET", resource, path.getGet());
		addOperationItemStruct("POST", resource, path.getPost());
		addOperationItemStruct("PUT", resource, path.getPut());
		addOperationItemStruct("PATCH", resource, path.getPatch());
		addOperationItemStruct("DELETE", resource, path.getDelete());
	}
	
	private String parseSwaggerDefinition(String msgType, String ref, int level, ResourceOperation resourceOperation) {
		String result="";
		String ident ="";
		
		for (int i=0; i<level; i++)
			ident += "&nbsp;&nbsp;&nbsp;";
		
		if (ref != null) {
			String[] refArray = ref.split("/");
			Map<String, Model> map = SWG.getDefinitions();

			for (Map.Entry<String, Model> entry: map.entrySet()) {
				String def = entry.getKey();
				Model s = entry.getValue();
				
				if (def.equals(refArray[refArray.length-1])) {
					Iterator<Entry<String, Property>> prop = s.getProperties().entrySet().iterator();
					
					while (prop.hasNext()) {
						Entry<String, Property> pair = prop.next();
						String reference = null;
						String type = pair.getValue().getType();
						String required = pair.getValue().getRequired() ? "Obrigatório": "Opcional";
						
						if (type.equals("ref")) {
							RefProperty rp = (RefProperty) pair.getValue();
							reference = rp.get$ref();
							type = rp.getType();
							
						}else if (pair.getValue().getType().equals("array")) {
							Property rp = (Property) ((ArrayProperty) pair.getValue()).getItems();

							if (rp.getType().equals("ref")) {
								RefProperty z = (RefProperty) rp;
								reference = z.get$ref();
								type = rp.getType();
							}else{
								type = rp.getType();
							}
						}
						
						if (msgType == "request")
							resourceOperation.setRequests(new Parameter(ident + pair.getKey() + " | " + type + " | " + required));
						else
							resourceOperation.setResponses(new Parameter(ident + pair.getKey() + " | " + type + " | " + required));
							
						
						result += ident + pair.getKey() 
								+ "|" + type //pair.getValue().getType()
								+ "|" + required;
						
						if (reference != null) {
								String a = parseSwaggerDefinition(msgType, reference, level+1, resourceOperation);
								result = a;
						}
						
					}
				}
			}
		}
		
		return result;
	}
	
	private String parseQueryDefinition(String msgType, QueryParameter q, ResourceOperation resourceOperation) {
		String type = q.getType();
		String subType = "";
		
		if (q.getItems() != null) {
			Property rp = (Property) q.getItems();
			subType = rp.getType();
		}

		if (type.equals("array")) {
			resourceOperation.setRequests(new Parameter(type + " (" + subType + ")"));
			return type + " (" + subType + ")";
		}
		
		if (msgType == "request")
			resourceOperation.setRequests(new Parameter(type));
		else 
			resourceOperation.setResponses(new Parameter(type));

		return type;
	}

    private String parsePathDefinition(String msgType, PathParameter item, ResourceOperation resourceOperation) {
		String type = item.getType()
				+ "|" + ((item).getRequired() ? "Obrigatório": "Opcional");

		if (msgType == "request")
			resourceOperation.setRequests(new Parameter(type));
		else 
			resourceOperation.setResponses(new Parameter(type));

		return type;
	}

    private String parseFormDefinition(String msgType, FormParameter item, ResourceOperation resourceOperation) {
		String type = item.getType()
				+ "|" + ((item).getRequired() ? "Obrigatório": "Opcional");

		if (msgType == "request")
			resourceOperation.setRequests(new Parameter(type));
		else 
			resourceOperation.setResponses(new Parameter(type));

		return type;
	}

    private String parseHeaderDefinition(String msgType, HeaderParameter item, ResourceOperation resourceOperation) {
		String type = item.getType()
				+ "|" + ((item).getRequired() ? "Obrigatório": "Opcional");

		if (msgType == "request")
			resourceOperation.setRequests(new Parameter(type));
		else 
			resourceOperation.setResponses(new Parameter(type));

		return type;
	}

	private void addOperationItemStruct (String httpVerb, String resource, Operation operation) {
		
		if (operation != null) {
			
			Resource newResource = new Resource(resource);
			ResourceOperation newResourceOperation = new ResourceOperation(operation.getOperationId());
			
			
			//Resources e verbos
			System.out.println(resource + ";" + httpVerb);
			
			newResource.setHttpVerb(httpVerb);
	
			//Operações
			System.out.println("\t" + operation.getOperationId() 
									+ ";" + operation.getSummary());
			
			newResource.setOperation(newResourceOperation);

			//Parametros
			operation.getParameters().forEach((item) -> {
				String defStruct ="";
				String msgType = "request";
				if (item != null) {
					if (item.getIn().equals("body")) {
						if (((BodyParameter) item).getSchema().getReference() != null) {
							defStruct = parseSwaggerDefinition(msgType, ((BodyParameter) item).getSchema().getReference(), 0, newResourceOperation);
						}
						else if (((ArrayModel) ((BodyParameter) item).getSchema()).getType().equals("array")) 
						{
							Property rp = ((ArrayModel) ((BodyParameter) item).getSchema()).getItems();
							if (rp.getType().equals("ref")) {
								RefProperty z = (RefProperty) rp;
								defStruct = parseSwaggerDefinition(msgType, z.get$ref(),0, newResourceOperation);
							}
						}
					}
					else if (item.getIn().equals("query")) { 
						defStruct = parseQueryDefinition(msgType, (QueryParameter) item, newResourceOperation);
						//newResourceOperation.setRequests(new Parameter(defStruct));
					}
					else if (item.getIn().equals("path")) {
						defStruct = parsePathDefinition(msgType, (PathParameter) item, newResourceOperation);
						//newResourceOperation.setRequests(new Parameter(defStruct));
					}
					else if (item.getIn().equals("formData")) {
						defStruct = parseFormDefinition(msgType, (FormParameter) item, newResourceOperation);
						//newResourceOperation.setRequests(new Parameter(defStruct));
					}
					else if (item.getIn().equals("header")) {
						defStruct = parseHeaderDefinition(msgType, (HeaderParameter) item, newResourceOperation);
						//newResourceOperation.setRequests(new Parameter(defStruct));
					}
						
					System.out.println("\t\t" + item.getName() 
							+ "|" + item.getDescription()
							+ "|" + defStruct);
				}
			});			

			//Reponses
			Map<String, Response> map = operation.getResponses();

			for (Entry<String, Response> entry: map.entrySet()) {
				String defStruct ="";
				Response r = entry.getValue();
				String msgType = "response";
				
				if (entry.getKey().equals("200") || entry.getKey().equals("default")) {
					if (r.getSchema() != null)
						if (r.getSchema().getType().equals("array"))
							defStruct = r.getSchema().getType() + parseSwaggerDefinition(msgType, ((RefProperty) ((ArrayProperty) r.getSchema()).getItems()).get$ref(), 0, newResourceOperation);
						else if (r.getSchema().getType().equals("ref")) {
							defStruct = parseSwaggerDefinition(msgType, ((RefProperty) r.getSchema()).get$ref(),0, newResourceOperation);
						}else {
							defStruct = r.getSchema().getName();
						};
				}
				else {
					defStruct = "-------->>>>>> EXCEÇÃO ";
					newResourceOperation.setExceptions(new Parameter(entry.getKey() + "|" + entry.getValue().getDescription()));
				}

				System.out.println("\t\t" + "|" + defStruct
									+ "|" + entry.getKey() 
									+ "|" + entry.getValue().getDescription()
									);
			}
		
			OperationResource.add(newResource);
		}
	}

}

