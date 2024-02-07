package pick.box.domain.doubleNestedExample;

import java.util.List;
import java.util.Map;

public class Student {

    public String id;
    public String name;
    public List<String> classDayIds;
    public Map<String, String> classIdToGrades;

    
    public Student() {
    }


    public Student(String id, String name, List<String> classDayIds, Map<String, String> classIdToGrades) {
        this.id = id;
        this.name = name;
        this.classDayIds = classDayIds;
        this.classIdToGrades = classIdToGrades;
    }
   
    

    

}
