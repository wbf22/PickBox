package pick.box;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;

/**
 * <pre>
 * This interface is what you need to implement in order for PickPoint to work it's magic.
 * 
 * If the request specifies that it needs the object this resolver provides, then the
 * resolve method will be invoked on your implementation of this interface.
 * 
 * PickPoint let's you split apart your object into pieces, and only do the work
 * to get the pieces when the requests asks for it. Implementing this class allows you
 * to provide the piece when PickPoint requests it. 
 * 
 * </pre>
 */
public abstract class Resolver<T, P, E> {

    
    /**
     * 
     * This method is called internally by PickPoint to get an object if the request speicifes
     * that it needs it.
     * 
     * The parent is provided for all nested objects by PickPoint. eg, a University object
     * that your Student object belongs too, will be provided in the Student resolver. For most
     * object (any that aren't nested) the parent will just be null.
     * 
     * @param parent the parent of this object if applicable
     * @param extraData some object you provide to PickPoint, given to all resolvers
     * @return
     */
    public abstract T resolve(P parent, E extraData);




    @SuppressWarnings("unchecked")
    public Class<E> getArgType() {
        ParameterizedType superclassType = (ParameterizedType) this.getClass().getGenericSuperclass();
        return (Class<E>) superclassType.getActualTypeArguments()[2];
    }


    @SuppressWarnings("unchecked")
    public Class<P> getParentType() {
        ParameterizedType superclassType = (ParameterizedType) this.getClass().getGenericSuperclass();
        return (Class<P>) superclassType.getActualTypeArguments()[1];
    }



    public String getReturnTypeName() {
        ParameterizedType superclassType = (ParameterizedType) this.getClass().getGenericSuperclass();
        Type returnType = superclassType.getActualTypeArguments()[0];

        return buildReturnTypeNameFromType(returnType);
    }


    public static String buildResolverReturnTypeName(Class<?> type) {
        return buildReturnTypeNameFromType(type);
    }


    public static String buildResolverReturnTypeName(Field field) {
        Type type = field.getGenericType();
        return buildReturnTypeNameFromType(type);
    }

    

    private static String buildReturnTypeNameFromType(Type type) {

        if (type instanceof ParameterizedType paramType) {
            StringBuilder stringBuilder = new StringBuilder();

            stringBuilder.append(paramType.getRawType());
            for (Type t : paramType.getActualTypeArguments()) 
                stringBuilder.append(" ").append(t.getTypeName());

            return stringBuilder.toString();
        }
        else {
            return type.getTypeName();
        }
        
    }


    
}
