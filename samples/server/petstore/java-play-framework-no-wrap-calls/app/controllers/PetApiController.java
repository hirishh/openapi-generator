package controllers;

import java.io.InputStream;
import apimodels.ModelApiResponse;
import apimodels.Pet;

import com.typesafe.config.Config;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Http;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.inject.Inject;
import java.io.File;
import play.libs.Files.TemporaryFile;
import openapitools.OpenAPIUtils;
import com.fasterxml.jackson.core.type.TypeReference;

import javax.validation.constraints.*;
import javax.validation.Valid;
import com.typesafe.config.Config;


@javax.annotation.Generated(value = "org.openapitools.codegen.languages.JavaPlayFrameworkCodegen", comments = "Generator version: 7.15.0-SNAPSHOT")
public class PetApiController extends Controller {
    private final PetApiControllerImpInterface imp;
    private final ObjectMapper mapper;
    private final Config configuration;

    @Inject
    private PetApiController(Config configuration, PetApiControllerImpInterface imp) {
        this.imp = imp;
        mapper = new ObjectMapper();
        this.configuration = configuration;
    }

    
    public Result addPet(Http.Request request) throws Exception {
        JsonNode nodebody = request.body().asJson();
        Pet body;
        if (nodebody != null) {
            body = mapper.readValue(nodebody.toString(), Pet.class);
            if (configuration.getBoolean("useInputBeanValidation")) {
                OpenAPIUtils.validate(body);
            }
        } else {
            throw new IllegalArgumentException("'body' parameter is required");
        }
        return imp.addPetHttp(request, body);
    }

    
    public Result deletePet(Http.Request request, Long petId) throws Exception {
        String valueapiKey = request.header("api_key").orElse(null);
        String apiKey;
        if (valueapiKey != null) {
            apiKey = valueapiKey;
        } else {
            apiKey = null;
        }
        return imp.deletePetHttp(request, petId, apiKey);
    }

    
    public Result findPetsByStatus(Http.Request request) throws Exception {
        String[] statusArray = request.queryString().get("status");
        if (statusArray == null) {
            throw new IllegalArgumentException("'status' parameter is required");
        }
        List<String> statusList = OpenAPIUtils.parametersToList("csv", statusArray);
        List<String> status = new ArrayList<>();
        for (String curParam : statusList) {
            if (!curParam.isEmpty()) {
                //noinspection UseBulkOperation
                status.add(curParam);
            }
        }
        return imp.findPetsByStatusHttp(request, status);
    }

    
    public Result findPetsByTags(Http.Request request) throws Exception {
        String[] tagsArray = request.queryString().get("tags");
        if (tagsArray == null) {
            throw new IllegalArgumentException("'tags' parameter is required");
        }
        List<String> tagsList = OpenAPIUtils.parametersToList("csv", tagsArray);
        List<String> tags = new ArrayList<>();
        for (String curParam : tagsList) {
            if (!curParam.isEmpty()) {
                //noinspection UseBulkOperation
                tags.add(curParam);
            }
        }
        return imp.findPetsByTagsHttp(request, tags);
    }

    
    public Result getPetById(Http.Request request, Long petId) throws Exception {
        return imp.getPetByIdHttp(request, petId);
    }

    
    public Result updatePet(Http.Request request) throws Exception {
        JsonNode nodebody = request.body().asJson();
        Pet body;
        if (nodebody != null) {
            body = mapper.readValue(nodebody.toString(), Pet.class);
            if (configuration.getBoolean("useInputBeanValidation")) {
                OpenAPIUtils.validate(body);
            }
        } else {
            throw new IllegalArgumentException("'body' parameter is required");
        }
        return imp.updatePetHttp(request, body);
    }

    
    public Result updatePetWithForm(Http.Request request, Long petId) throws Exception {
        String[] valuename = request.body().asMultipartFormData().asFormUrlEncoded().get("name");
        String name;
        if (valuename != null) {
            name = valuename[0];
        } else {
            name = null;
        }
        String[] valuestatus = request.body().asMultipartFormData().asFormUrlEncoded().get("status");
        String status;
        if (valuestatus != null) {
            status = valuestatus[0];
        } else {
            status = null;
        }
        return imp.updatePetWithFormHttp(request, petId, name, status);
    }

    
    public Result uploadFile(Http.Request request, Long petId) throws Exception {
        String[] valueadditionalMetadata = request.body().asMultipartFormData().asFormUrlEncoded().get("additionalMetadata");
        String additionalMetadata;
        if (valueadditionalMetadata != null) {
            additionalMetadata = valueadditionalMetadata[0];
        } else {
            additionalMetadata = null;
        }
        Http.MultipartFormData<TemporaryFile> body_file = request.body().asMultipartFormData();
        Http.MultipartFormData.FilePart<TemporaryFile> _file = body_file.getFile("file");
        return imp.uploadFileHttp(request, petId, additionalMetadata, _file);
    }

}
