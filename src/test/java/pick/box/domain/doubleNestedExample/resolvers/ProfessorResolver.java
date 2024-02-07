package pick.box.domain.doubleNestedExample.resolvers;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pick.box.Resolver;
import pick.box.domain.doubleNestedExample.ClassDays;
import pick.box.domain.doubleNestedExample.Professor;
import pick.box.domain.doubleNestedExample.University;

public class ProfessorResolver extends Resolver<List<Professor>, University, String>{



    private Map<String, List<Professor>> professors = new HashMap<>();


    public ProfessorResolver() {
        Professor professor = new Professor();
        professor.id = "pro_1";
        professor.areaOfStudy = "Computer Science";
        professor.classDayIds = List.of("class_142MW", "class_256MW");
        professor.name = "Dr. Nelson";
        professor.studentIds = List.of("std_1", "std_2");

        professors.put("univ_1", List.of(professor));
    }



    @Override
    public List<Professor> resolve(University parent, String extraData) {
        return professors.get(parent.id);
    }


    
}
