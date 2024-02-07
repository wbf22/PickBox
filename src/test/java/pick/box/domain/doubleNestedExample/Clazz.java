package pick.box.domain.doubleNestedExample;

import java.sql.Time;
import java.util.List;

public class Clazz {


    public String code;

    public String name;
    public String semester;
    public String year;

    public List<ClassDays> classDays;

    public Clazz() {
    }

    public Clazz(String code, String name, String semester, String year, List<ClassDays> classDays) {
        this.code = code;
        this.name = name;
        this.semester = semester;
        this.year = year;
        this.classDays = classDays;
    }

    
}
