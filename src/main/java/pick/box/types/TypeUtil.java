package pick.box.types;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.Temporal;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import pick.box.exception.PickerException;

public class TypeUtil {
    
    @SuppressWarnings("unchecked")
    public static <T> T makeWithNoArgsConstructor(Class<?> type) {

        T instance;
        try {
            instance = (T) type.getDeclaredConstructor().newInstance();
        } catch (ReflectiveOperationException e) {
            throw new PickerException("Missing default constructor in class " + type.getName(), e);
        }catch (ClassCastException e) {
            throw new PickerException("Unexpected error when trying to create class " + type.getName(), e);
        }

        return instance;
    }



    /**
     * Tries to cast the object to the desired type. Throwing an exception if not possible.
     *
     * @param obj object to cast
     * @param desiredType type to cast too
     * @return cast object of type T
     * @param <T> type
     */
    public static <T> T safeCast(Object obj, Class<T> desiredType) {
        if (desiredType.isInstance(obj)) {
            return desiredType.cast(obj);
        }
        String message = "Failed trying to cast class " + obj.getClass().getName() + " to " + desiredType.getName();
        throw new PickerException(message, null);
    }


    public static Object buildDefaultForNonBasicClass(Class<?> type) {

        Object instance;
        try {
            instance = type.getDeclaredConstructor().newInstance();
        } catch (ReflectiveOperationException e) {
            throw new PickerException("Missing default constructor in class " + type.getName(), e);
        }

        for (Field field : type.getDeclaredFields()) {
            try {
                boolean isPrivate = !Modifier.isPublic(field.getModifiers());
                if (isPrivate)
                    field.setAccessible(true);
                Object value = (isBasicType(field.getType()))? 
                    buildDefaultForJavaClass(field.getGenericType()) : 
                    buildDefaultForNonBasicClass(field.getType());
                field.set(instance, value);
                if (isPrivate)
                    field.setAccessible(false);
            } catch (IllegalArgumentException e) {
                throw new PickerException("Tried to map bad value to field " + field.getName() + "", e);
            } catch (IllegalAccessException e) {
                throw new PickerException("Couldn't access field " + field.getName() + "", e);
            }
        }

        return instance;
    }

    public static Object buildDefaultForJavaClass(Type generic) {
        Type screened = (generic instanceof ParameterizedType parameterizedType)? parameterizedType.getRawType() : generic;
        Class<?> type = (Class<?>) screened;
        Object defaultObject;
        if (type == String.class) {
            defaultObject = "";
        }
        else if (type == Integer.class || type == int.class) {
            defaultObject = Integer.parseInt("0");
        }
        else if (type == Long.class || type == long.class) {
            defaultObject = Long.parseLong("0");
        }
        else if (type == Double.class || type == double.class) {
            defaultObject = Double.parseDouble("0.0");
        }
        else if (type == Float.class || type == float.class) {
            defaultObject = Float.parseFloat("0.0");
        }
        else if (type == Short.class || type == short.class) {
            defaultObject = Short.parseShort("0");
        }
        else if (type == Byte.class || type == byte.class) {
            defaultObject = Byte.parseByte("0");
        }
        else if (type == Character.class || type == char.class) {
            defaultObject = "a";
        }
        else if (type == BigDecimal.class) {
            defaultObject = BigDecimal.valueOf( Long.parseLong("0") );
        }
        else if (type == Boolean.class || type == boolean.class) {
            defaultObject = false;
        }
        else if (type.isEnum()) {
            defaultObject = type.getEnumConstants()[0];
        }
        else if (Map.class.isAssignableFrom(type)) {
            Class<?> keyType = null;
            if (generic instanceof ParameterizedType) {
                keyType = (Class<?>) ((ParameterizedType) generic).getActualTypeArguments()[0];
            }
            else {
                keyType = Object.class;
            }

            Class<?> valueType = null;
            if (generic instanceof ParameterizedType) {
                valueType = (Class<?>) ((ParameterizedType) generic).getActualTypeArguments()[1];
            }
            else {
                valueType = Object.class;
            }

            Object key = (isBasicType(keyType))? buildDefaultForJavaClass(keyType) : buildDefaultForNonBasicClass(keyType);
            Object value = (isBasicType(valueType))? buildDefaultForJavaClass(valueType) : buildDefaultForNonBasicClass(valueType);

            defaultObject = Map.of(key, value);
        }
        else if (Collection.class.isAssignableFrom(type)) {
            Class<?> listType = null;
            if (generic instanceof ParameterizedType) {
                listType = (Class<?>) ((ParameterizedType) generic).getActualTypeArguments()[0];
            }
            else {
                listType = Object.class;
            }
            Object instance = (isBasicType(listType))? buildDefaultForJavaClass(listType) : buildDefaultForNonBasicClass(listType);
            defaultObject = List.of(instance);
        }
        else if (Temporal.class.isAssignableFrom(type)) {
            try {
                defaultObject = type.getMethod("now").invoke(null);
            } catch (ReflectiveOperationException e) {
                defaultObject = LocalDate.now();
            }
        }
        else if (type == Object.class) {
            defaultObject = new Object();
        }
        else {
            throw new PickerException("Unsupported java type: " + type.getName(), null);
        }

        return defaultObject;
    }



    public static boolean isBasicType(Class<?> type) {
        return isBasicJavaType(type) || isSupportedCollectionType(type) || type.isEnum();
    }

    /**
     * Determines if a type is one of the following:
     * - String
     * - Date types
     * - Numeric Types
     * @param type
     * @return
     */
    public static boolean isBasicJavaType(Class<?> type) {
        return isNumericClass(type) || Temporal.class.isAssignableFrom(type) || type == String.class;
    }

    public static boolean isNumericClass(Class<?> type) {
        return Number.class.isAssignableFrom(type) || isPrimitiveNumericClass(type);
    }

    public static boolean isPrimitiveNumericClass(Class<?> type) {
        return type == int.class || type == long.class || type == double.class
                || type == float.class || type == short.class || type == byte.class;
    }

    public static boolean isSupportedCollectionType(Class<?> type) {
        return Map.class.isAssignableFrom(type) || Collection.class.isAssignableFrom(type);
    }


}
