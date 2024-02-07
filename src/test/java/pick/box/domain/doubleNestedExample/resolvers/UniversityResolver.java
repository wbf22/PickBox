package pick.box.domain.doubleNestedExample.resolvers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pick.box.Resolver;
import pick.box.domain.doubleNestedExample.University;

public class UniversityResolver extends Resolver<University, Void, String> {

    
    private Map<String, University> universities = new HashMap<>();


    public UniversityResolver() {
        University request = new University();
        request.id = "univ_1";
        request.name = "Teaching Focused University";
        request.city = "Spanish Fork";
        request.state = "Utah";
        request.country = "USA";


        universities.put("univ_1", request);
    }




    @Override
    public University resolve(Void parent, String extraData) {
        return universities.get(extraData);
    }

    
    
}
