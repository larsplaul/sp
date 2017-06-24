package deploy;

import entity.StudyPointUser;
import entity.Task;
import entity.UserRole;
import static entity.deploy.StudyPointUser_.password;
import java.security.SecureRandom;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import rest.TestResource;
import security.MailSender;
import security.PasswordStorage;
import security.Secrets;

@WebListener
public class DeploymentConfiguration implements ServletContextListener {

  public static String PU_NAME = "PU-Local";

  @Override
  public void contextInitialized(ServletContextEvent sce) {
    Map<String, String> env = System.getenv();
    //If we are running in the OPENSHIFT environment change the pu-name 
    if (env.keySet().contains("OPENSHIFT_MYSQL_DB_HOST")) {
      PU_NAME = "PU_OPENSHIFT";
    }

    System.out.println("PU_NAME: " + PU_NAME);

    ServletContext context = sce.getServletContext();
    boolean isDebug = context.getInitParameter("debug").toLowerCase().equals("true");

    if (Secrets.SHARED_SECRET == null) {
      if (isDebug) {
        Secrets.SHARED_SECRET = "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA".getBytes();
      } else {
        SecureRandom random = new SecureRandom();
        Secrets.SHARED_SECRET = new byte[32];
        random.nextBytes(Secrets.SHARED_SECRET);
      }
    }
    
    isDebug = isDebug || context.getInitParameter("makeTestUser").toLowerCase().equals("true");
    TestResource.STATUS = isDebug ? "DEBUG" : "PRODUCTION";

    MailSender.initConstants(context);
    StudyPointUser.tempPasswordTimeoutMinutes = Integer.parseInt(context.getInitParameter("tempPasswordTimeoutMinutes"));
    Task.CODE_TIMEOUT_MINUTES = Integer.parseInt(context.getInitParameter("autoAttendaceCodeTimeOutMinutes"));

    boolean makeTestUser = context.getInitParameter("makeTestUser").toLowerCase().equals("true");
    if (makeTestUser) {
      System.out.println("Making Test Usr: lam");
      EntityManagerFactory emf = Persistence.createEntityManagerFactory(DeploymentConfiguration.PU_NAME);
      EntityManager em = emf.createEntityManager();
      try {
        UserRole student = new UserRole("User");
        UserRole admin = new UserRole("Admin");
        UserRole superRole = new UserRole("Super");
        StudyPointUser user = new StudyPointUser("lam", "lars", "mortensen", "lam@cphbusiness.dk", "");
        user.setPasswordInitial("");
        try {
          user.setPassword("test");
          em.persist(admin);
          em.persist(user);
          em.persist(superRole);
          //UserRole role = em.find(UserRole.class, "Admin");
          admin.addStudyPointUser(user);
          user.addRole(superRole);
          user.addRole(admin);
          em.getTransaction().begin();
          em.persist(student);

          em.getTransaction().commit();
        } catch (Exception ex) {
          //Logger.getLogger(DeploymentConfiguration.class.getName()).log(Level.SEVERE, null, ex);
          Logger.getLogger(DeploymentConfiguration.class.getName()).log(Level.SEVERE, "User alredy exist");
        }
      } finally {
        em.close();
        emf.close();
      }

    }
  }

  @Override
  public void contextDestroyed(ServletContextEvent sce) {
  }
}
