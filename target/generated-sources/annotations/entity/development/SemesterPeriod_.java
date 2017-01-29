package entity.development;

import entity.SP_Class;
import entity.SemesterPeriod;
import entity.Task;
import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.5.2.v20140319-rNA", date="2017-01-29T22:13:27")
@StaticMetamodel(SemesterPeriod.class)
public class SemesterPeriod_ { 

    public static volatile SingularAttribute<SemesterPeriod, Integer> id;
    public static volatile SingularAttribute<SemesterPeriod, SP_Class> inClass;
    public static volatile ListAttribute<SemesterPeriod, Task> tasks;
    public static volatile SingularAttribute<SemesterPeriod, String> periodDescription;
    public static volatile SingularAttribute<SemesterPeriod, String> periodName;

}