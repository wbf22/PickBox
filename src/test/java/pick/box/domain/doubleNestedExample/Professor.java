package pick.box.domain.doubleNestedExample;

import java.util.List;

public class Professor {

    public String id;

    public String name;
    public String areaOfStudy;

    public List<String> classDayIds;
    public List<String> studentIds;



    public Professor() {

    }
    
    public Professor(String id, String name, String areaOfStudy, List<String> classDayIds, List<String> studentIds) {
        this.id = id;
        this.name = name;
        this.areaOfStudy = areaOfStudy;
        this.classDayIds = classDayIds;
        this.studentIds = studentIds;
    }


    

}
