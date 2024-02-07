package pick.box.domain.doubleNestedExample;

import java.util.List;

public class University {

    public String id;

    public String name;
    public String city;
    public String state;
    public String country;


    public List<Clazz> classes;
    public List<Student> students;
    public List<Professor> professors;


    public University() {
    }


    public University(String id, String name, String city, String state, String country, List<Clazz> classes,
            List<Student> students, List<Professor> professors) {
        this.id = id;
        this.name = name;
        this.city = city;
        this.state = state;
        this.country = country;
        this.classes = classes;
        this.students = students;
        this.professors = professors;
    }

    

    

}
