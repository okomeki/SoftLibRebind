/*
 * Copyright 2023 okome.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.siisise.bind.unbind.java;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import net.siisise.bind.TypeUnbind;
import net.siisise.bind.format.TypeFormat;
import net.siisise.bind.format.BindObject;

/**
 * 一般的なデータclass の場合と、Dateなど他に当てはまらない場合の2パターン
 * Mapに変換する場合
 */
public class UnbindObject implements TypeUnbind {
    
    public static enum MapType {
        FIELD,
        DECLARED_FIELD,
        BEAN
    }
    
    /**
     * 仮
     */
    final MapType type;
    
    public UnbindObject() {
        type = MapType.FIELD;
    }
    
    public UnbindObject(MapType type) {
        this.type = type;
    }
    
    static final Type[] def = { Object.class };

    @Override
    public Type[] getSrcTypes() {
        return def;
    }

    @Override
    public Object valueOf(Object src, TypeFormat format) {
        if ( format instanceof BindObject ) {
            return ((BindObject)format).objectFormat(src);
        }
        Map<String, Object> objmap = objectToMap(src);
        return format.mapFormat(objmap);
    }
    
    Map<String, Object> objectToMap(Object src) {
        Map<String, Object> objmap;
        switch (type) {
            case FIELD:
                objmap = fieldsToMap(src);
                break;
            case DECLARED_FIELD:
                objmap = declaredFieldsToMap(src);
                break;
            case BEAN:
                objmap = beanMap(src);
                break;
            default:
                throw new UnsupportedOperationException();
//                objmap = null;
        }
        return objmap;
    }
    
    public static Map<String, Object> fieldsToMap(Object obj) {
        Class<? extends Object> cls = obj.getClass();
        HashMap objmap = new LinkedHashMap();

        Field[] fields = cls.getFields();

        for (Field field : fields) {
            try {
                objmap.put(field.getName(), field.get(obj));
            } catch (IllegalArgumentException | IllegalAccessException ex) {
                throw new IllegalStateException(ex);
//                Logger.getLogger(UnbindObject.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return objmap;
    }

    public static Map<String, Object> declaredFieldsToMap(Object obj) {
        Class<? extends Object> cls = obj.getClass();
        HashMap objmap = new LinkedHashMap();

        while (cls != null) {
            Field[] fields = cls.getDeclaredFields();

            for (Field field : fields) {
                try {
//                    field.setAccessible(true);
                    objmap.put(field.getName(), field.get(obj));
                } catch (IllegalArgumentException | IllegalAccessException ex) {
                    throw new IllegalStateException(ex);
                }

            }
            cls = cls.getSuperclass();
        }

        return objmap;
    }

    public static Map<String, Object> beanMap(Object obj) {
        Class<? extends Object> cls = obj.getClass();
        HashMap objmap = new LinkedHashMap();

        while (cls != null) {
            Method[] methods = cls.getMethods();

            for (Method method : methods) {
                try {
                    if (method.getParameterCount() != 0 || method.getReturnType() != Void.TYPE) {
                        continue;
                    }
                    String name = method.getName();
                    String keyName;
                    if (name.startsWith("get")) {
                        keyName = name.substring(3, 4).toLowerCase();
                        if (name.length() > 4) {
                            keyName = keyName + name.substring(4);
                        }
                        objmap.put(keyName, method.invoke(obj));
                    } else if (name.startsWith("is")) {
                        keyName = name.substring(2, 3).toLowerCase();
                        if (name.length() > 3) {
                            keyName = keyName + name.substring(3);
                        }
                        objmap.put(keyName, method.invoke(obj));
                    }
                } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                    throw new IllegalStateException(ex);
                }

            }
            cls = cls.getSuperclass();
        }

        return objmap;
    }
}
