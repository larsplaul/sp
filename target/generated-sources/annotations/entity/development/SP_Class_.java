package entity.development;

import entity.SP_Class;
import entity.SemesterPeriod;
import entity.StudyPointUser;
import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.5.2.v20140319-rNA", date="2017-06-23T14:35:30")
@StaticMetamodel(SP_Class.class)
public class SP_Class_ { 

    public static volatile SingularAttribute<SP_Class, String> id;
    public static volatile ListAttribute<SP_Class, SemesterPeriod> periods;
    public static volatile ListAttribute<SP_Class, StudyPointUser> users;
    public static volatile SingularAttribute<SP_Class, Integer> requiredPoints;
    public static volatile SingularAttribute<SP_Class, String> semesterDescription;
    public static volatile SingularAttribute<SP_Class, Integer> maxPointForSemester;
    public static volatile SingularAttribute<SP_Class, String> nameShort;

}