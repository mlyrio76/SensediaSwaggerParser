package com.repository.sensedia;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
public class ServiceSpecs {
	
	@RequestMapping("/swgParser")
    public swgStructure swgParser(@RequestParam(name="URI", required=true) String URI) {
		
		swgStructure SwaggerStructure = new swgStructure(URI);
		
		return SwaggerStructure;
	}
    
}
