package deploy;

import entity.StudyPointUser;
import entity.UserRole;
import static entity.deploy.StudyPointUser_.password;
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
import security.MailSender;
import security.PasswordStorage;

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
        MailSender.initConstants(context);
       StudyPointUser.tempPasswordTimeoutMinutes =  Integer.parseInt(context.getInitParameter("tempPasswordTimeoutMinutes"));
       

        boolean makeTestUser = context.getInitParameter("makeTestUser").toLowerCase().equals("true");
        if (makeTestUser) {
            EntityManagerFactory emf = Persistence.createEntityManagerFactory(DeploymentConfiguration.PU_NAME);
            EntityManager em = emf.createEntityManager();
            try {

                StudyPointUser user = new StudyPointUser("lam", "lars", "mortensen", "lam@cphbusiness.dk", "");
                user.setPasswordInitial("");
                try {
                    user.setPassword("test");
                    UserRole role = em.find(UserRole.class, "Admin");
                    role.addStudyPointUser(user);
                    user.addRole(role);
                    em.getTransaction().begin();
                    em.persist(user);
                    em.getTransaction().commit();
                } catch (Exception ex) {
                    Logger.getLogger(DeploymentConfiguration.class.getName()).log(Level.SEVERE, null, ex);
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
