package scripthandling;

import deploy.DeploymentConfiguration;
import entity.exceptions.NonexistentEntityException;
import entity.exceptions.PreexistingEntityException;
import entity.exceptions.ScriptException;
import java.util.Scanner;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 *
 * @author plaul1
 */
public abstract class ScriptHandler {

  //START TOKENS
  public static final String ASSIGN_POINTS = "_AssignPoints_";
  public static final String PERIOD_INFO = "_PeriodInfo_";
  public static final String CLASS_AND_STUDENTS = "_class_and_students_";

  static EntityManagerFactory emf = Persistence.createEntityManagerFactory(DeploymentConfiguration.PU_NAME);
  protected int currentLineNo = 0;

  protected String getNextLine(Scanner scan) {
    currentLineNo++;
    return scan.nextLine().trim();
  }

  protected boolean isLineToSkip(String line) {
    if (line.equals("")) {
      return true;
    }
    if (line.startsWith("#")) {
      return true;
    }
    return false;
  }

  static String makeError(int lineNo, String txt) {
    return String.format("Error (line: %1$d): %2$s", lineNo, txt);
  }

  protected String makeError(String txt) {
    return String.format("Error (line: %1$d): %2$s", currentLineNo, txt);
  }

  protected String script;

  protected ScriptHandler(String script) {
    this.script = script;
  }

  protected abstract void handleScriptTemplate(Scanner scan) throws ScriptException;

  public void executeScript() throws ScriptException {
    try (Scanner scan = new Scanner(script)) {
      getNextLine(scan); //First line, with script type is read by MakeScriptHandler
      handleScriptTemplate(scan);
    }
  }
  
  public static ScriptHandler MakeScriptHandler(String script) throws ScriptException, PreexistingEntityException, NonexistentEntityException {

    try (Scanner scan = new Scanner(script)) {
      String scriptType = scan.nextLine().trim();
      switch (scriptType) {
        case PERIOD_INFO:
          return new PeriodInfoStrategy(script);
        case CLASS_AND_STUDENTS:
          return new ClassAndStudentsStrategy(script);
        case ASSIGN_POINTS:
          return new AssignPointsStrategy(script);
      }
      throw new ScriptException(makeError(1, "Script does not include a known script-type in first line"));
    }
  }

}
