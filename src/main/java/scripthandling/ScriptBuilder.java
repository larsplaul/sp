package scripthandling;

import deploy.DeploymentConfiguration;
import entity.SP_Class;
import entity.SemesterPeriod;
import entity.StudyPoint;
import entity.StudyPointUser;
import entity.Task;
import entity.UserRole;
import entity.exceptions.NonexistentEntityException;
import entity.exceptions.PreexistingEntityException;
import entity.exceptions.ScriptException;
import facade.ClassFacade;
import java.util.Scanner;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.Persistence;
import javax.persistence.Query;
import security.PasswordStorage;

public class ScriptBuilder {

    static EntityManagerFactory emf = Persistence.createEntityManagerFactory(DeploymentConfiguration.PU_NAME);

    public static SP_Class makeClass(String classId, int maxPointsForSemester, String nameShort, int requredPoints, String description) throws PreexistingEntityException {

        ClassFacade classFacade = new ClassFacade(emf);
        SP_Class newClass = classFacade.findSP_Class(classId);
        if (newClass != null) {
            throw new PreexistingEntityException(String.format("The class '%s' already exist",newClass.getId()));
        }

        newClass = new SP_Class();
        newClass.setId(classId);
        newClass.setMaxPointForSemester(maxPointsForSemester);
        newClass.setNameShort(nameShort);
        newClass.setRequiredPoints(requredPoints);
        newClass.setSemesterDescription(description);
        try {
            newClass = classFacade.create(newClass);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return newClass;

    }

    public static StudyPointUser makeStudent(String classId, String userName, String fName, String lName, String email, String phone, String password, String passwordInitial, SP_Class theClass) throws NonexistentEntityException, ScriptException {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        StudyPointUser user = null;
        try {
            try {
                Query q = em.createNamedQuery("StudyPointUser.findByUsername", StudyPointUser.class);
                q.setParameter("username", userName);
                user = (StudyPointUser) q.getSingleResult();
            } catch (NoResultException ex) {
                //User does not already exist (from a previous class), so create him
                user = new StudyPointUser(userName, fName, lName, email, phone);
                user.setPasswordInitial(passwordInitial);
                try{
                user.setPassword(password);
                }
                catch(PasswordStorage.CannotPerformOperationException cpoe){
                    throw new ScriptException(cpoe.getMessage(),cpoe);
                }
                //em.getTransaction().begin();
                em.persist(user);
                UserRole role = em.find(UserRole.class, "User");
                if (role == null) {
                    throw new NonexistentEntityException("Role 'User' not found");
                }
                role.addStudyPointUser(user);
                user.addRole(role);
                em.merge(role);
                // em.getTransaction().commit();
            }
            //em.getTransaction().begin();
            theClass.addStudyPointUser(user);
            user.addClass(theClass);
            em.merge(theClass);
            em.merge(user);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
        return user;
    }

    public static SemesterPeriod makePeriod(SP_Class theClass, String name, String description, EntityManager em) throws ScriptException {
       
        for(SemesterPeriod period : theClass.getPeriods()){
          if(period.getPeriodName().equals(name)){
              throw new ScriptException(String.format("The class '%1s' already contains a period with the name %2s ",theClass.getId(),name));
          }
        }
        
        SemesterPeriod period = new SemesterPeriod();
        period.setInClass(theClass);
        period.setPeriodDescription(description);
        period.setPeriodName(name);
        theClass.addPeriod(period);
        em.merge(theClass);
        em.persist(period);
        return period;
    }

    public static Task makeTask(int maxScore, String name, SP_Class theClass, SemesterPeriod period, EntityManager em) {
        Task task = new Task(maxScore, name, period);
        period.addTask(task);
        for (StudyPointUser user : theClass.getUsers()) {
            StudyPoint sp = new StudyPoint(task, user);
            em.persist(sp);
            user.addStudyPoint(sp);
            em.merge(user);

            task.addStudyPoint(sp);

        }
        em.persist(task);
        return task;
    }

    private static void makeClassAndStudents(Scanner scan, String friendlyName, int max, int required) throws PreexistingEntityException, NonexistentEntityException {

        boolean classLineExpected = true;
        boolean classCreated = false;
        String className = null;
        int studentCount = 0;
        SP_Class theClass = null;
        while (scan.hasNext()) {
            if (classLineExpected) {
                String headerLine = scan.nextLine();
                String[] headers = headerLine.split(";");
                //System.out.println(makeClassRow(headers[0], 240, "sem3-COS",70, "fjlsafjlska"));
                classLineExpected = false;
            } else {
                String studentLine = scan.nextLine();
                String[] sd = studentLine.split(";");

                if (!classCreated) {
                    theClass = makeClass(sd[0], max, friendlyName, required, "NOT_USED");
                    classCreated = true;
                    className = sd[0];
                }
                try {
                    makeStudent(sd[0], sd[1], sd[6], sd[7], sd[3], sd[8], sd[2], sd[2], theClass);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
        }
    }

    public static void createFromScript(String script) throws ScriptException, PreexistingEntityException, NonexistentEntityException {

        try (Scanner scan = new Scanner(script)) {
            String scriptType = scan.nextLine();
            if (scriptType.equals("#PeriodInfo#")) {
                makeTaskAndStudyPoints(scan);
            } else if (scriptType.equals("#class_and_students#")) {
                String[] secondLine = scan.nextLine().split("#");
                if (!secondLine[0].equals("friendlyname")) {
                    System.out.println("ERROR: Second line in a #class_and_students# file must include a friendly class name");
                    throw new ScriptException("ERROR: Second line in a #class_and_students# file must include a friendly class name");
                }
                String friendlyName = secondLine[1];
                String[] thirdLine = scan.nextLine().split("#");
                if (!thirdLine[0].equals("maxpoints_required")) {
                    System.out.println("ERROR: Third line in a #class_and_students# file must include 'maxpoints_required#MAX0#REQUIRED'");
                    throw new ScriptException("ERROR: Third line in a #class_and_students# file must include 'maxpoints_required#MAX0#REQUIRED'");
                }
                int max = Integer.parseInt(thirdLine[1]);
                int required = Integer.parseInt(thirdLine[2]);

                makeClassAndStudents(scan, friendlyName, max, required);
            }
            else{
                throw new ScriptException("Script does not include a known script-type in first line");
            }
        }
    }

    private static void makeTaskAndStudyPoints(Scanner scan) throws NonexistentEntityException, ScriptException  {

        boolean taskTagFound = false;
        boolean classCreated = false;
        boolean periodCreated = false;

        EntityManager em = emf.createEntityManager();
        try {

            int lineCount = 0;
            SP_Class theClass = null;
            SemesterPeriod period = null;
            em.getTransaction().begin();
            while (scan.hasNext()) {
                lineCount++;
                String line = scan.nextLine();
                if (line.equals("")) {
                    continue;
                }
                if (period == null) {
                    String[] periodInfo = line.split(";");
                    String classID = periodInfo[0];
                    theClass = em.find(SP_Class.class, classID);
                    if (periodInfo.length != 3) {
                        throw new ScriptException("Second line (following a '#PeriodInfo#' must contain a valid class-id,, a header and a description");
                    }
                    if (theClass == null) {
                        throw new NonexistentEntityException(String.format("Class '%s' not found", classID));
                    }
                    
                    String periodName = periodInfo[1];
                    String periodDes = periodInfo[2];
                    period = makePeriod(theClass, periodName, periodDes, em);

                    continue;
                }

                if (!taskTagFound && !line.equals("") && !line.equals("#Tasks#")) {
                    throw new ScriptException("Expected a line with: \"#Tasks#\"");
                } else if (taskTagFound && line.equals("#Tasks#")) {
                    throw new ScriptException("Only one tag: \"#Tasks#\" allowed");
                } else if (line.equals("#Tasks#")) {
                    taskTagFound = true;
                } else {

                    String[] td = line.split(";");
                    makeTask(Integer.parseInt(td[1]), td[0], theClass, period, em);

                }
            }
            em.merge(period);
            //em.flush();
            em.getTransaction().commit();
            em.getEntityManagerFactory().getCache().evictAll();
        } 
        catch(Exception e){
       
          if(em != null){
            em.getTransaction().rollback();
          }
          throw e;
        }
        finally {
            em.close();
        }
    }

}
