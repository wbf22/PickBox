package pick.box;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.Temporal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import pick.box.exception.PickerException;
import pick.box.types.TypeUtil;

/**
 * <pre>
 * This class is an object that handles the request object, and extracts 
 * only the provided fields for the client from your provided object. 
 * 
 * This allows you to have graphql esk functionality without all the magic
 * and baggage. 
 * 
 * </pre>
 */
public class PickerBox {
    
    private Map<String, Resolver<?, ?, ?>> resolvers;


    /**
     * <pre>
     * To make a functional PickerBox, provide a list of 
     * Resolver implementations to provide the objects
     * in a request. 
     * 
     * 
     * </pre>
     * @param resolvers
     */
    public PickerBox(List<Resolver<?,?,?>> resolvers) {
        this.resolvers = new HashMap<>();

        for (Resolver<?,?,?> resolver : resolvers) {
            this.resolvers.put(resolver.getReturnTypeName(), resolver);
        }
    }



    /**
     * <pre>
     * Goes through the request object looking for any fields that are not null. 
     * If a field is not null, (has any arbitrary value) then that field will be requested
     * from the resolvers and created in the result. 
     * 
     * The type T must have a no args constructor for this to work. 
     * 
     * The argList is a list of arguments needed for each 
     * resolver to fulfill their piece of the request. Say
     * to get a Person object you need an Id for the row in 
     * the table. You would provide this in the args list, and 
     * set the corresponding index in your Resolver. Then when
     * PickerBox tries to fulfill the request it would look up
     * the Id in the arg list by the index and provide that to 
     * your resolver.
     * 
     * </pre>
     * @param <T>
     * @param request
     * @param argList
     * @return
     */
    @SuppressWarnings("unchecked")
    public <T, E> T resolveRequest(T request, E extraData){
        T response = (T) getResponseFromResolver(
            Resolver.buildResolverReturnTypeName(request.getClass()), 
            null,
            extraData
        );

        return getRequestedFields(request, response, extraData);
    }

    private <T, E> T getRequestedFields(T request, T response, E extraData){

        Class<?> objectType = request.getClass();
        T mappedResponse = TypeUtil.makeWithNoArgsConstructor(objectType);

        Field[] fields = objectType.getFields();

        List<Field> nonBlockingFields = new ArrayList<>();
        List<Field> resolverFields = new ArrayList<>();
        
        // determine which fields are done with resolvers and do those last
        Arrays.stream(fields)
            .forEach(field -> {
                String resolverTypeName = Resolver.buildResolverReturnTypeName(field);
                if (this.resolvers.containsKey(resolverTypeName)) 
                    resolverFields.add(field);
                else
                    nonBlockingFields.add(field);
            });

        Field currentField = null;
        try {
            // do the non blocking (ie fields with defined resolvers) first
            for (Field field : nonBlockingFields) {
                boolean originalAccessibility = field.canAccess(request);
                field.setAccessible(true);

                currentField = field; // for errors below

                field.set(
                    mappedResponse, 
                    resolveField(field, request, response, mappedResponse, extraData)
                );
                
                field.setAccessible(originalAccessibility);
            }
            // then do fields using resolvers so most complete parent object can be provided
            for (Field field : resolverFields) {
                boolean originalAccessibility = field.canAccess(request);
                field.setAccessible(true);

                currentField = field; // for errors below

                field.set(
                    mappedResponse, 
                    resolveField(field, request, response, mappedResponse, extraData)
                );
                
                field.setAccessible(originalAccessibility);
            }
            
        } catch (IllegalArgumentException e) {
            throw new PickerException("Tried to map bad value to field '" + currentField.getName() + "' in class '" + objectType.getName() + "'", e);
        }  catch (IllegalAccessException e) {
            throw new PickerException("Couldn't access field '" + currentField.getName() + "' in class '" + objectType.getName() + "'", e);
        } 
        
        return mappedResponse;
    }

    private <T, E> Object resolveField(Field field, T request, T response, T parent, E extraData) {

        try {
            if (field.get(request) == null) 
                return null;

                
            Object value = null;

            // check if response already provided the field
            if (field.get(response) != null) {
                value = field.get(response);
            }
            // check if the field is a list
            else if (List.class.isAssignableFrom(field.getType())) {
                List<Object> list = new ArrayList<>();

                List<?> reqList = TypeUtil.safeCast(field.get(request), List.class);
                if (reqList.isEmpty())
                        throw new PickerException("List for field " + field.getName() + " was empty so fulfillment couldn't be completed.", null);

                Object reqO = reqList.get(0);

                Object fieldResponse = getResponseFromResolver(
                    Resolver.buildResolverReturnTypeName(field), 
                    parent,
                    extraData
                );

                if (fieldResponse != null) {
                    List<?> resList = TypeUtil.safeCast(fieldResponse, List.class);
                    for (Object o : resList) {
                        list.add(
                            getRequestedFields(reqO, o, extraData)
                        );
                    }
                    value = list;
                }
                else {
                    value = new ArrayList<>();
                }
                
            }
            // check if the field is a map
            else if (Map.class.isAssignableFrom(field.getType())) {
                Map<Object, Object> map = new HashMap<>();

                value = field.get(response);
                if (value != null) {
                    Map<?, ?> reqMap = TypeUtil.safeCast(field.get(response), Map.class);
                    if (reqMap.isEmpty())
                        throw new PickerException("Map for field " + field.getName() + " was empty so fulfillment couldn't be completed.", null);

                    Object reqOVal = reqMap.values().iterator().next();

                    Map<?, ?> resMap = TypeUtil.safeCast(field.get(response), Map.class);
                    for (Entry<?, ?> entry : resMap.entrySet()) {
                        Object key = entry.getKey();
                        Object val = entry.getValue();
                        val = getRequestedFields(reqOVal, val, extraData);
                        map.put(key, val);
                    }

                    value = map;
                }
                else {
                    value = new HashMap<>();
                }
            }
            // check if the field is a basic java type or enum
            else if (TypeUtil.isBasicJavaType(field.getType()) || field.getType().isEnum()) {
                value = field.get(response);
            }
            // otherwise we need to fulfill it with a resolver
            else {
                Object fieldResponse = getResponseFromResolver(
                        Resolver.buildResolverReturnTypeName(field.getType()), 
                        parent,
                        extraData
                );
                value = getRequestedFields(field.get(request), fieldResponse, extraData);
            }

            return value;
        } catch (IllegalArgumentException e) {
            throw new PickerException("Tried to map bad value to field '" + field.getName() + "' in class '" + parent.getClass().getName() + "'", e);
        }  catch (IllegalAccessException e) {
            throw new PickerException("Couldn't access field '" + field.getName() + "' in class '" + parent.getClass().getName() + "'", e);
        } 
    }

    private Object getResponseFromResolver(String resolverTypeName, Object parent, Object extraData) {
        if (!this.resolvers.containsKey(resolverTypeName))
            return null;

        Resolver<?,?,?> resolver = this.resolvers.get(resolverTypeName);

        Class<?> parentType = resolver.getParentType();
        if (parent != null && !parentType.isAssignableFrom(parent.getClass())) 
            throw new PickerException(
                "Parent for resolver " + resolver.getClass().getName() + 
                " was expected to be of type " + parentType.getName()
                + " but was of type " + extraData.getClass().getName(), 
                null
            );

        Class<?> argType = resolver.getArgType();
        if (extraData != null && !argType.isAssignableFrom(extraData.getClass())) 
            throw new PickerException(
                "Argument for resolver " + resolver.getClass().getName() + 
                " was expected to be of type " + argType.getName()
                + " but was of type " + extraData.getClass().getName(), 
                null
            );

        return invokeResolver(resolver, parent, extraData);
    }

    private <T, U, E> T invokeResolver(Resolver<T, U, E> resolver, Object parent, Object extraData) {
        return resolver.resolve(
            resolver.getParentType().cast(parent),
            resolver.getArgType().cast(extraData)
        );
    }




}
