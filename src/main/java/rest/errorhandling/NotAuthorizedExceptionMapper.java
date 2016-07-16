package rest.errorhandling;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import javax.servlet.ServletContext;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class NotAuthorizedExceptionMapper implements ExceptionMapper<NotAuthorizedException> {

  private static Gson gson = new GsonBuilder().setPrettyPrinting().create();

  @Context
  ServletContext context;

  @Override
  public Response toResponse(NotAuthorizedException ex) {

    boolean isDebug = context.getInitParameter("debug").toLowerCase().equals("true");
    ErrorMessage err = new ErrorMessage(ex, ex.getResponse().getStatus(), isDebug);
    err.setDescription("You could either not be authenticated, or are not authorized to perform this request");
    
    return Response.status(ex.getResponse().getStatus())
            .entity("{\"error\":"+gson.toJson(err)+"}")
            .type(MediaType.APPLICATION_JSON).
            build();
  }
}
