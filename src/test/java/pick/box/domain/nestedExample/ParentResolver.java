package pick.box.domain.nestedExample;

import java.util.List;
import java.util.Map;

import pick.box.Resolver;

public class ParentResolver extends Resolver<Parent, Void, Integer> {



    @Override
    public Parent resolve(Void parent, Integer args) {
        Parent response = new Parent();
        response.name = "Stinky Dad";
        response.gender = Gender.MALE;

        return response;
    }
    
}
