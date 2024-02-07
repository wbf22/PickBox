package pick.box.domain.doubleNestedExample.resolvers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pick.box.Resolver;
import pick.box.domain.doubleNestedExample.Clazz;
import pick.box.domain.doubleNestedExample.University;



public class ClazzResolver extends Resolver<List<Clazz>, University, String> {


    private Map<String, List<Clazz>> classes = new HashMap<>();


    public ClazzResolver() {
        
        Clazz cs142 = new Clazz();
        cs142.code = "CS_142";
        cs142.name = "CS 142";
        cs142.semester = "Fall";
        cs142.year = "2024";


        Clazz cs256 = new Clazz();
        cs256.code = "CS_256";
        cs256.name = "CS 256";
        cs256.semester = "Fall";
        cs256.year = "2024";


        classes.put("univ_1", List.of(cs142, cs256));
    }


    @Override
    public List<Clazz> resolve(University parent, String extraData) {
        
        return classes.get(parent.id);
    }
    
}
