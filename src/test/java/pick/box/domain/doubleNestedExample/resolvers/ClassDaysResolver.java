package pick.box.domain.doubleNestedExample.resolvers;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pick.box.Resolver;
import pick.box.domain.doubleNestedExample.ClassDays;
import pick.box.domain.doubleNestedExample.Clazz;
import pick.box.domain.doubleNestedExample.Student;

public class ClassDaysResolver extends Resolver<List<ClassDays>, Clazz, String>{



    private Map<String, List<ClassDays>> codeToClassDays = new HashMap<>();


    public ClassDaysResolver() {
        
        ClassDays classDayCS142MW = new ClassDays();
        classDayCS142MW.classCode = "CS_142";
        classDayCS142MW.id = "class_142MW";
        classDayCS142MW.day = List.of(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY);
        classDayCS142MW.startTime = LocalTime.of(10, 30).toString();
        classDayCS142MW.endTime = LocalTime.of(11, 30).toString();
        classDayCS142MW.room = "101";
        classDayCS142MW.building = "Taylor";
        classDayCS142MW.campus = "Main";
        classDayCS142MW.studentIds = List.of("std_1", "std_2");
        


        ClassDays classDaysCS256MW = new ClassDays();
        classDayCS142MW.classCode = "CS_256";
        classDaysCS256MW.id = "class_256MW";
        classDaysCS256MW.day = List.of(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY);
        classDaysCS256MW.startTime = LocalTime.of(10, 30).toString();
        classDaysCS256MW.endTime = LocalTime.of(11, 30).toString();
        classDaysCS256MW.room = "204";
        classDaysCS256MW.building = "Taylor";
        classDaysCS256MW.campus = "Main";
        classDaysCS256MW.studentIds = List.of("std_1", "std_2");


        codeToClassDays.put("CS_142", List.of(classDayCS142MW));
        codeToClassDays.put("CS_256", List.of(classDaysCS256MW));
    }


    @Override
    public List<ClassDays> resolve(Clazz parent, String extraData) {
        return codeToClassDays.get(parent.code);
    }
    



}
