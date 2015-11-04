package org.javers.common.reflection;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author bartosz walacik
 */
class JaversMethodFactory {
    private final Class methodSource;

    public JaversMethodFactory(Class methodSource) {
        this.methodSource = methodSource;
    }

    /**
     * List all class methods, including inherited and private.
     * Inheritance duplicates cleared
     */
    public List<JaversMethod> getAllMethods(){
        List<JaversMethod> methods = new ArrayList<>();
        Set<Integer> added = new HashSet<>();
        TypeResolvingContext context = new TypeResolvingContext();

        Class clazz = methodSource;
        while (clazz != null) {
            context.addTypeSubstitutions(clazz);
            for (Method m : clazz.getDeclaredMethods()) {
                int methodKey = methodKey(m);
                if (added.contains(methodKey)) {
                    // System.out.println("filtered inheritance duplicate" +m);
                    continue;
                }
                methods.add(createJMethod(m, context));
                added.add(methodKey);
            }
            clazz = clazz.getSuperclass();
        }

        return methods;
    }

    private JaversMethod createJMethod(Method rawMethod, TypeResolvingContext context){
        Type actualReturnType = context.getSubstitution(rawMethod.getGenericReturnType());
        return new JaversMethod(rawMethod, actualReturnType);
    }

    public static int methodKey(Method m){
        int key = shaDigest(m.getName());
        for (Class c : m.getParameterTypes()) {
            key += c.hashCode();
        }
        return key;
    }

    private static int shaDigest(String text){
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            digest.update(text.getBytes("UTF-8"));
            byte[] hashBytes = digest.digest();

            int result = 0;
            for (int i=0; i<hashBytes.length; i++){
                result += Math.abs(hashBytes[i]) * (i+1);
            }
            return result;
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
}
