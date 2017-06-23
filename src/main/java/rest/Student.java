package rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import entity.exceptions.StudyPointException;
import facade.JsonAssembler;
import facade.LogFacade;
import facade.LogMessage;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
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
  private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

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

  @Path("myStudyPoints/{classId}")
  @GET
  @Produces("application/json")
  public Response studypointsForStudentClass(@PathParam("classId") String classId){
    String user = securityContext.getUserPrincipal().getName();
    LogFacade.addLogEntry(user, LogMessage.userGotPoints);
    return Response
            .status(200)
            .header("Access-Control-Allow-Origin", "*")
            .entity(jsonAssembler.getStudyPointsForCurrentUser(classId, user))
            .build();
  }
  
  @Path("registerAttendance")
  @PUT
  @Produces("application/json")
  public Response registerAttendence(String json) throws StudyPointException{
    jsonmappers.StudyPointTask data = gson.fromJson(json, jsonmappers.StudyPointTask.class);
    String user = securityContext.getUserPrincipal().getName();
    jsonAssembler.registerAttendance(user, data.taskId, data.spId,data.code);
    try {
    LogFacade.addLogEntry(user, LogMessage.registeredAttendance);
    } catch(Exception e){
      LogFacade.addLogEntry(user, LogMessage.registerAttendanceFailded, e.getMessage());
      throw e;
    }
    String returnJson = "{\"status\":\"OK\"}";
     return Response
            .status(200)
            .header("Access-Control-Allow-Origin", "*")
            .entity(returnJson)
            .build();
  }

}
