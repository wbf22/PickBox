package pick.box.domain.nestedExample;

import java.util.List;

import pick.box.Resolver;


public class ChildResolver extends Resolver<List<Child>, Parent, Integer> {

    @Override
    public List<Child> resolve(Parent parent, Integer args) {
        
        // normally you would use the key to look up the parent or something
        // but this is just fake

        Child child1Res = new Child();
        child1Res.age = 11;
        child1Res.name = "Dan";
        child1Res.gender = Gender.MALE;

        Child child2Res = new Child();
        child2Res.age = 4;
        child2Res.name = "Julie";
        child2Res.gender = Gender.FEMALE;

        return List.of(child1Res, child2Res);
    }
    
}
