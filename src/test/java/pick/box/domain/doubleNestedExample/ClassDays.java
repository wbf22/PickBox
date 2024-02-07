package pick.box.domain.doubleNestedExample;

import java.sql.Time;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

public class ClassDays {

    public String id;

    public String classCode;
    public List<DayOfWeek> day;
    public String startTime;
    public String endTime;
    public String room;
    public String building;
    public String campus;

    public String professorId;
    public List<String> studentIds;


    public ClassDays() {

    }
    
    public ClassDays(String id, String classCode, List<DayOfWeek> day, String startTime, String endTime,
            String room, String building, String campus, String professorId, List<String> studentIds) {
        this.id = id;
        this.classCode = classCode;
        this.day = day;
        this.startTime = startTime;
        this.endTime = endTime;
        this.room = room;
        this.building = building;
        this.campus = campus;
        this.professorId = professorId;
        this.studentIds = studentIds;
    }


    
}
