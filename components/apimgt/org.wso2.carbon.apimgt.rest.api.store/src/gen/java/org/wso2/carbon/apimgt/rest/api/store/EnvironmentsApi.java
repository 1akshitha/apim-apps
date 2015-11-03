package org.wso2.carbon.apimgt.rest.api.store;

import org.wso2.carbon.apimgt.rest.api.store.dto.*;
import org.wso2.carbon.apimgt.rest.api.store.EnvironmentsApiService;
import org.wso2.carbon.apimgt.rest.api.store.factories.EnvironmentsApiServiceFactory;

import io.swagger.annotations.ApiParam;

import org.wso2.carbon.apimgt.rest.api.store.dto.ErrorDTO;
import org.wso2.carbon.apimgt.rest.api.store.dto.EnvironmentListDTO;

import java.util.List;

import java.io.InputStream;

import javax.ws.rs.core.Response;
import javax.ws.rs.*;

@Path("/environments")
@Consumes({ "application/json" })
@Produces({ "application/json" })
@io.swagger.annotations.Api(value = "/environments", description = "the environments API")
public class EnvironmentsApi  {

   private final EnvironmentsApiService delegate = EnvironmentsApiServiceFactory.getEnvironmentsApi();

    @GET
    
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    @io.swagger.annotations.ApiOperation(value = "", notes = "Get a list of gateway environments configured previously.", response = EnvironmentListDTO.class)
    @io.swagger.annotations.ApiResponses(value = { 
        @io.swagger.annotations.ApiResponse(code = 200, message = "OK. \nEnvironment list is returned."),
        
        @io.swagger.annotations.ApiResponse(code = 304, message = "Not Modified. \nEmpty body because the client has already the latest version of the requested resource."),
        
        @io.swagger.annotations.ApiResponse(code = 404, message = "Not Found. \nRequested API does not exist.") })

    public Response environmentsGet(@ApiParam(value = "Will return environment list for the provided API.") @QueryParam("apiId") String apiId)
    {
    return delegate.environmentsGet(apiId);
    }
}

