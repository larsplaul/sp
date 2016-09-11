/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rest;

import entity.exceptions.NonexistentEntityException;
import entity.exceptions.PreexistingEntityException;
import entity.exceptions.ScriptException;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import scripthandling.ScriptBuilder;

/**
 * REST Web Service
 *
 * @author plaul1
 */
@Path("script")
@RolesAllowed("Super") 
public class ScriptService {
static String msg = "Hello";
    @Context
    private UriInfo context;

    /**
     * Creates a new instance of ScriptService
     */
    public ScriptService() {
    }

    /**
     * Retrieves representation of an instance of rest.ScriptService
     * @return an instance of java.lang.String
     */
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String getText() {
        //TODO return proper representation object
        return msg;
    }

      @POST
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public String scriptService(String content) throws NonexistentEntityException, ScriptException, PreexistingEntityException {
        msg = content;
        ScriptBuilder.createFromScript(content);
        return "{\"status\": \"Script executed\"}";
    }
    
    
}
