package rest;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import deploy.DeploymentConfiguration;
import facade.LogFacade;
import facade.StudyPointUserFacade;
import java.security.SecureRandom;
import java.util.Date;
import java.util.List;
import javax.naming.AuthenticationException;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.ws.rs.Consumes;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import security.PasswordStorage;
import security.Secrets;
import facade.LogMessage;
import security.AuthenticatedUser;

@Path("login")
public class Login {

  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public Response login(String jsonString) throws JOSEException, AuthenticationException {
    JsonObject json = new JsonParser().parse(jsonString).getAsJsonObject();
    String username = json.get("username").getAsString();
    String password = json.get("password").getAsString();
   // boolean useFronter = json.get("useFronter").getAsBoolean();
    JsonObject responseJson = new JsonObject();
    //String role;  
    //List<String> roles;
    AuthenticatedUser userDetails = null;
    try {
      if ((userDetails = authenticate(username, password)) != null) {
        String token = createToken(username, "lam@cphbusiness.dk", userDetails);
        responseJson.addProperty("username", username);
        responseJson.addProperty("token", token);
        LogFacade.addLogEntry(username, LogMessage.okLogin);
        return Response.ok(new Gson().toJson(responseJson)).header("Access-Control-Allow-Origin", "*").build();
      }
    } catch (PasswordStorage.CannotPerformOperationException | PasswordStorage.InvalidHashException ex) {
      AuthenticationException ae = new AuthenticationException("The system could not Authenticate you, with the provided credentials");
      ae.setRootCause(ex);
      throw ae;
    }
    throw new NotAuthorizedException("Ilegal username or password"); 
  }

  //Todo Deep deeper into this
  @OPTIONS
  @Produces("application/json")
  @Consumes("application/json")
  public Response loginOpt(String scoresAsJson) {
    return Response
            .status(200)
            .header("Access-Control-Allow-Origin", "*")
            .header("Access-Control-Allow-Headers", "X-Requested-With, Content-Type, Origin, Authorization, Accept, Client-Security-Token, Accept-Encoding")
            .header("Access-Control-Allow-Credentials", "true")
            .header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD")
            .header("Access-Control-Max-Age", "1209600")
            .build();
  }

  static AuthenticatedUser authenticate(String userName, String password) throws PasswordStorage.CannotPerformOperationException, PasswordStorage.InvalidHashException, AuthenticationException {
    EntityManagerFactory emf = Persistence.createEntityManagerFactory(DeploymentConfiguration.PU_NAME);
    StudyPointUserFacade facade = new StudyPointUserFacade(emf);
    return facade.authenticateUser(userName, password);
  }

  static String createToken(String subject, String issuer, AuthenticatedUser userDetails) throws JOSEException {

    StringBuilder res = new StringBuilder();
    JWSSigner signer = new MACSigner(Secrets.SHARED_SECRET);
    Date date = new Date();
    JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
            .subject(subject)
            .claim("username", subject)
            .claim("roles", userDetails.getRoles())
            .claim("fn", userDetails.getFirstName())
            .claim("ln", userDetails.getLastName())
            .claim("issuer", issuer)
            .issueTime(date)
            .expirationTime(new Date(date.getTime() + 1000 * 60 * 60))
            .build();
    SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claimsSet);
    signedJWT.sign(signer);
    return signedJWT.serialize();

  }
}
