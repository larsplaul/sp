package entity.development;

import entity.StudyPointUser;
import entity.UserRole;
import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.5.2.v20140319-rNA", date="2016-09-11T12:11:34")
@StaticMetamodel(UserRole.class)
public class UserRole_ { 

    public static volatile ListAttribute<UserRole, StudyPointUser> studyPointUsers;
    public static volatile SingularAttribute<UserRole, String> roleName;

}