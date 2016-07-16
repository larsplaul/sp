package rest;

import facade.JsonAssembler;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

@Path("user")
@RolesAllowed("User")
public class Student {

  @Context
  SecurityContext securityContext;

  private final JsonAssembler jsonAssembler;

  public Student() {
    jsonAssembler = new JsonAssembler();
  }

  @Path("myClasses")
  @GET
  @Produces("application/json")
  public Response getClassesForCurrentUser() {
    String user = securityContext.getUserPrincipal().getName();
     return Response
            .status(200)
            .header("Access-Control-Allow-Origin", "*")
            .entity(jsonAssembler.getClassesForCurrentUser(user))
            .build();
  }
//  @Path("myClasses")
//  @GET
//  @Produces("application/json")
//  public String getClassesForCurrentUser() {
//    String user = securityContext.getUserPrincipal().getName();
//    return jsonAssembler.getClassesForCurrentUser(user);
//  }
  
  @Path("myStudyPoints/{classId}")
  @GET
  @Produces("application/json")
  public Response studypointsForStudentClass(@PathParam("classId") String classId){
    String user = securityContext.getUserPrincipal().getName();
    return Response
            .status(200)
            .header("Access-Control-Allow-Origin", "*")
            .entity(jsonAssembler.getStudyPointsForCurrentUser(classId, user))
            .build();
  }

}
