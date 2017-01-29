package entity.development;

import entity.SP_Class;
import entity.StudyPoint;
import entity.StudyPointUser;
import entity.UserRole;
import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.5.2.v20140319-rNA", date="2017-01-29T22:13:27")
@StaticMetamodel(StudyPointUser.class)
public class StudyPointUser_ { 

    public static volatile SingularAttribute<StudyPointUser, Integer> id;
    public static volatile ListAttribute<StudyPointUser, SP_Class> classes;
    public static volatile ListAttribute<StudyPointUser, StudyPoint> studyPoints;
    public static volatile SingularAttribute<StudyPointUser, String> lastName;
    public static volatile SingularAttribute<StudyPointUser, Date> tempPasswordCreated;
    public static volatile SingularAttribute<StudyPointUser, String> phone;
    public static volatile SingularAttribute<StudyPointUser, String> email;
    public static volatile SingularAttribute<StudyPointUser, String> passwordInitial;
    public static volatile ListAttribute<StudyPointUser, UserRole> roles;
    public static volatile SingularAttribute<StudyPointUser, String> userName;
    public static volatile SingularAttribute<StudyPointUser, String> firstName;
    public static volatile SingularAttribute<StudyPointUser, String> password;

}