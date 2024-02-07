package pick.box.domain.doubleNestedExample.resolvers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pick.box.Resolver;
import pick.box.domain.doubleNestedExample.Student;
import pick.box.domain.doubleNestedExample.University;

public class StudentResolver extends Resolver<List<Student>, University, String>{



    private Map<String, List<Student>> students = new HashMap<>();


    public StudentResolver() {
        Student pamela = new Student();
        pamela.id = "std_1";
        pamela.classDayIds = List.of("class_142MW", "class_256MW");
        pamela.name = "Pamela de la Cruz";
        pamela.classIdToGrades = Map.of("class_142MW", "A", "class_256MW", "B");

        Student james = new Student();
        james.id = "std_2";
        james.classDayIds = List.of("class_142MW", "class_256MW");
        james.name = "James Francis";
        james.classIdToGrades = Map.of("class_142MW", "A", "class_256MW", "B");
        


        students.put("univ_1", List.of(pamela, james));
    }


    @Override
    public List<Student> resolve(University parent, String extraData) {
        return students.get(parent.id);
    }
    
}
