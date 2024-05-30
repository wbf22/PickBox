package pick.box;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import pick.box.exception.PickerException;
import pick.box.types.TypeUtil;

public class PickerUtil {
    


    /**
     * Converts an object to a map ommiting null fields
     * 
     * @param object
     * @return
     */
    public static <T> Map<String, Object> mapify(T object) {

        Map<String, Object> mappedResponse = new HashMap<>();
        
        Class<?> objectType = object.getClass();

        Field[] fields = objectType.getFields();

        for (Field field : fields) {
            
            try {
                boolean originalAccessibility = field.canAccess(object);
                field.setAccessible(true);


                if (field.get(object) != null) {

                    Object value = null;
    
                    // check if the field is a list
                    if (List.class.isAssignableFrom(field.getType())) {
                        List<Object> list = new ArrayList<>();

                        Object fieldResponse = field.get(object);

                        List<?> resList = TypeUtil.safeCast(fieldResponse, List.class);
                        for (Object o : resList) {
                            Object val = (TypeUtil.isBasicJavaType(o.getClass()) || o.getClass().isEnum())? o : mapify(o);
                            list.add(val);
                        }
                        value = list;
                    }
                    // check if the field is a map
                    else if (Map.class.isAssignableFrom(field.getType())) {
                        Map<Object, Object> map = new HashMap<>();

                        Map<?, ?> resMap = TypeUtil.safeCast(field.get(object), Map.class);
                        for (Entry<?, ?> entry : resMap.entrySet()) {
                            Object key = entry.getKey();
                            Object val = entry.getValue();
                            val = (TypeUtil.isBasicJavaType(val.getClass()) || val.getClass().isEnum())? val : mapify(val);
                            map.put(key, val);
                        }

                        value = map;
                    }
                    // check if the field is a basic java type or enum
                    else if (TypeUtil.isBasicJavaType(field.getType()) || field.getType().isEnum()) {
                        value = field.get(object);
                    }
                    // otherwise we need to fulfill it as well
                    else {
                        Object fieldResponse = field.get(object);
                        value = mapify(fieldResponse);
                    }


                    mappedResponse.put(field.getName(), value);
    
                }
                field.setAccessible(originalAccessibility);
                
            } catch (IllegalArgumentException e) {
                throw new PickerException("Tried to map bad value to field '" + field.getName() + "' in class '" + objectType.getName() + "'", e);
            }  catch (IllegalAccessException e) {
                throw new PickerException("Couldn't access field '" + field.getName() + "' in class '" + objectType.getName() + "'", e);
            } 
        }

        return mappedResponse;
    }


    /**
     * Converts a map to a pretty json string
     */
    public static String jsonMap(Map<String, Object> map) {
        StringBuilder builder = new StringBuilder();
        builder.append("{\n");

        for (Entry<String, Object> entry : map.entrySet()) {

            if (entry.getValue() instanceof Map) {
                builder.append("\t\"").append(entry.getKey()).append("\": ");
                String mapString = jsonMap((Map<String, Object>) entry.getValue());
                String[] lines = mapString.split("\n");

                builder.append("\t").append(lines[0]).append("\n");
                String[] otherLines = Arrays.copyOfRange(lines, 1, lines.length);
                for (String line : otherLines) {
                    builder.append("\t").append(line).append("\n");
                }

            } 
            else if (entry.getValue() instanceof List) {
                builder.append("\t\"").append(entry.getKey()).append("\": ");
                List<?> list = (List<?>) entry.getValue();
                builder.append("[\n");
                for (Object o : list) {
                    if (o instanceof Map) {
                        String mapString = jsonMap((Map<String, Object>) o);
                        String[] lines = mapString.split("\n");

                        builder.append("\t\t").append(lines[0]).append("\n");
                        String[] otherLines = Arrays.copyOfRange(lines, 1, lines.length);
                        for (String line : otherLines) {
                            builder.append("\t\t").append(line).append("\n");
                        }
                        builder.deleteCharAt(builder.length() - 1);
                        builder.append(",\n");
                    } 
                    else if (o instanceof List) {
                        throw new PickerException("Double nested lists aren't supported for jsonMap conversion. Map key: " + entry.getKey(), null);
                    }
                    else if (o instanceof String || o.getClass().isEnum()) {
                        builder.append("\"").append(o).append("\"").append(",\n");
                    }
                    else {
                        builder.append(entry.getValue()).append(",\n");
                    }
                }
                builder.deleteCharAt(builder.length() - 2);
                builder.append("\t],\n");	
            }
            else {
                builder.append("\t\"").append(entry.getKey()).append("\": ");
                if (entry.getValue() instanceof String || entry.getValue().getClass().isEnum()) {
                    builder.append("\"").append(entry.getValue()).append("\"").append(",\n");
                }
                else {
                    builder.append(entry.getValue()).append(",\n");
                }
            }

        }

        builder.deleteCharAt(builder.length() - 2);
        builder.append("}");

        return builder.toString();
    }
	

}
