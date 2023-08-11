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
package net.siisise.bind.format;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.siisise.bind.Rebind;
import net.siisise.io.BASE64;

/**
 * プリミティブ形など変換機能つよめのTypeFormat
 * OMAPConvert だったもの
 * @param <T> 出力型
 */
public class JavaTypeConvert<T> implements TypeBind<T> {

    private final Type type;
    
    public JavaTypeConvert(Type type) {
        this.type = type;
    }
    
    @Override
    public Type targetClass() {
        return type;
    }

    @Override
    public T nullFormat() {
        return null;
    }

    @Override
    public T booleanFormat(boolean bool) {
        if ( type instanceof Class ) {
            Class cls = (Class)type;
            if ( cls.isAssignableFrom(Boolean.class) || cls == Boolean.TYPE ) {
                return (T)(Boolean)bool;
            } else if ( cls == String.class || cls == CharSequence.class ) {
                return (T)Boolean.toString(bool);
            } else if ( Number.class.isAssignableFrom(cls)) { // Number系
                return (T)numberFormat(bool ? 1 : 0);
            }
        }
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public T numberFormat(Number number) {
        if ( !(type instanceof Class) ) {
            throw new UnsupportedOperationException("まだ");
        }
        Class cls = (Class)type;
        
//        if ( cls.isInstance(number)) { // cls instanceof  プリミティブは false
//            return number;
//        }
        if ( cls.isAssignableFrom(number.getClass())) {
            return (T)number;
        }
        // 整数型
        if (cls == Integer.TYPE || cls == Integer.class) {
            return (T)(Integer)number.intValue();
        } else if (cls == Long.TYPE || cls == Long.class) {
            return (T)(Long)number.longValue();
        } else if (cls == Short.TYPE || cls == Short.class) {
            return (T)(Short)number.shortValue();
        } else if (cls == Character.TYPE || cls == Character.class) {
            return (T)(Character)(char)number.intValue();
        } else if (cls == Byte.TYPE || cls == Byte.class) {
            return (T)(Byte)number.byteValue();
        // 浮動小数点型
        } else if (cls == Float.TYPE || cls == Float.class) {
            return (T)(Float)number.floatValue();
        } else if (cls == Double.TYPE || cls == Double.class) {
            return (T)(Double)number.doubleValue();
        // 大きい型
        } else if (cls == BigInteger.class) {
            return (T)new BigInteger(number.toString());
        } else if (cls == BigDecimal.class) {
            if ( number instanceof BigInteger ) {
                return (T)new BigDecimal((BigInteger)number);
            }
            if ( number instanceof Long || number instanceof Integer || number instanceof Short || number instanceof Byte ) {
                return (T)BigDecimal.valueOf(number.longValue());
            } else if ( number instanceof Double || number instanceof Float ) {
                return (T)BigDecimal.valueOf(number.doubleValue());
            }
            return (T)new BigDecimal(number.toString());
        } else if ( cls == String.class || cls == CharSequence.class ) {
            return (T)number.toString();
        }
//        if ( cls.isAssignableFrom(Boolean.class) || cls == Boolean.TYPE ) { // 仮
//            return number.longValue() != 0;
//        }

        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * CharSequence に対応するかもしれない仮
     * @param value
     * @return 
     */
    @Override
    public T stringFormat(String value) {
        if (value == null) {
            return nullFormat();
        }
        Class ocls = value.getClass(); // CharSequence だったときの残り
        String val;// = value;
//        if (ocls != String.class ) {
//            val = value.toString();
//        }
        if (type == StringBuilder.class ) {
            return (T)new StringBuilder(value);
        } else if (type == StringBuffer.class ) {
            return (T)new StringBuffer(value);
        } else {
            val = (String)value;
        }
        if ( type instanceof Class ) {
            if ( type == ocls || type == CharSequence.class ) {
                return (T)value;
            }
            if ( type == String.class ) {
                return (T)val;
            }
            Class cls = (Class)type;
            if ( cls.isArray() ) {
                if ( cls.getComponentType() == Character.TYPE ) {
                    return (T)val.toCharArray();
                } else if ( cls.getComponentType() == Byte.TYPE ) {
                    BASE64 b64 = new BASE64(BASE64.URL,0);
                    return (T)b64.decode(val);
                }
                throw new UnsupportedOperationException("謎の配列");
            }
//            if ( cls.isAssignableFrom(JSONString.class) ) {
//                return new JSONString(val);
//            }
            if ( type == UUID.class ) {
                return (T)UUID.fromString(val);
            }
//            if ( type == Date.class ) {
//                return new JSONDateM().replace(new JSON2String(val), null);
//            }
            // 任意の型になるかもしれない注意
            try {
                Constructor c = ((Class)type).getConstructor(value.getClass());
                return (T)c.newInstance(value);
            } catch (NoSuchMethodException | SecurityException | InstantiationException
                    | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                throw new UnsupportedOperationException(ex);
//                Logger.getLogger(OMAPConvert.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return (T)value;
    }

    /**
     * Object に変換可能.
     * @param map
     * @return 
     */
    @Override
    public T mapFormat(Map map) {
        if (type instanceof Class) {
            return (T)mapClassCast(map, (Class)type);
        } else if ( type instanceof ParameterizedType ) {
            return (T)mapParameterizedCast(map, (ParameterizedType)type);
        }
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Mapを指定の型に収まるよう加工する
     * @param type
     * @return
     */
    private <K,V> Map mapParameterizedCast(Map<K,V> src, ParameterizedType type) {
        Type raw = type.getRawType();
        if ((raw instanceof Class) && (Map.class.isAssignableFrom(((Class) raw)))) {
            Type[] args = type.getActualTypeArguments();
            Map map = typeToMap((Class) raw);
            src.forEach((k,v) -> {
                Object tkey = Rebind.valueOf(k, args[0]);
                Object tval = Rebind.valueOf(v, args[1]);
                map.put(tkey, tval);
            });
            return map;
        }
        throw new UnsupportedOperationException("Not supported yet.");
    }

    private <K,V> Object mapClassCast(Map<K,V> obj, Class cls) {
        if (cls == String.class || cls == CharSequence.class) {
            return obj.toString();
        } else if (cls.isAssignableFrom(this.getClass())) {
            return obj;
        } else if (Map.class.isAssignableFrom(cls)) { // 表面だけ軽い複製 ToDO: 全部複製?
            Map<K,V> map = typeToMap(cls);
            obj.forEach((k,v) -> {
                map.put(k, v);
            });
            return map;
        }
//        if (cls.isAssignableFrom(JsonObject.class)) { // なし
//            return Rebind.typeMap(obj, JsonValue.class);
//        }
        return mapLc(obj, cls);
    }

    private <K,V,T> T mapLc(Map<K,V> src, Class<T> cls) {
        try {
            T obj = cls.getConstructor().newInstance();
            for (Map.Entry<K,V> es : src.entrySet()) {
                Field field = null;

                Class c = cls;
                while (c != null && field == null) {
                    try {
                        field = c.getDeclaredField(es.getKey().toString());
                        // field.setAccessible(true);
                    } catch (NoSuchFieldException e) {
                        c = c.getSuperclass();
                    }
                }
                field.set(obj, Rebind.valueOf((Object)es.getValue(), field.getGenericType()));
            }
            return obj;
        } catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            throw new java.lang.UnsupportedOperationException(ex);
        }
    }

    //JSONObjectもある
    static Class[] MAPS = {HashMap.class, LinkedHashMap.class, EnumMap.class, Hashtable.class, TreeMap.class};

    /**
     * Mapに使える実装を適当に決める
     *
     * @param type
     * @return
     */
    static Map typeToMap(Type type) {
        Class tcls;
        if (type instanceof ParameterizedType) {
            tcls = (Class) ((ParameterizedType) type).getRawType();
        } else {
            tcls = (Class) type;
        }

        for (Class cls : MAPS) {
            if (tcls.isAssignableFrom(cls)) {
                try {
                    return (Map) cls.getConstructor().newInstance();
                } catch (NoSuchMethodException | SecurityException | InstantiationException
                        | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                    throw new UnsupportedOperationException("Not supported yet.");
                }
            }
        }

        try {
            return (Map) tcls.getConstructor().newInstance();
        } catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException
                | IllegalArgumentException | InvocationTargetException ex) {
            Logger.getLogger(JavaTypeConvert.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /**
     * いろいろ変換.
     * Object に変換する場合コンストラクタの薔薇メータとして扱う.
     * @param list
     * @return 
     */
    @Override
    public T collectionFormat(Collection list) {
        if (type instanceof Class) {
            return (T)listClassCast(list, (Class)type);
        } else if ( type instanceof ParameterizedType ) { // List<Generic>
            ParameterizedType pt = (ParameterizedType) type;
            Type raw = pt.getRawType();
            Collection col = typeToList((Class)raw);
            if ( col != null ) {
                return (T)listCollectionTypeMap(list, pt, col);
            }

        } else if ( type instanceof GenericArrayType ) { // XXX<String>[]
            GenericArrayType gat = (GenericArrayType)type;
            Type component = gat.getGenericComponentType();
            return (T)listToArray(list, component);
        }
        
        throw new UnsupportedOperationException("未サポートな型:" + type.getTypeName());
    }

    @Override
    public T arrayFormat(Object array) {
        if ( type instanceof Class ) {
            if ( array.getClass() == type ) {
                return (T)array;
            }
        }
        throw new UnsupportedOperationException("Not supported yet." + array.getClass().getName() + " target:" + type.getTypeName());
    }

    /**
     * なんとなく変換する.
     * @param <I>
     * @param <L> 出力形
     * @param src
     * @param cls 出力class
     * @return 
     */
    public static <I,L> L listClassCast(Collection<I> src, Class<L> cls) {
        if (cls == String.class || cls == CharSequence.class) {
            return (L) src.toString();
        } else if (cls.isAssignableFrom(src.getClass())) {
            return (L) src; // ToDo: 複製?
//        } else if (!cls.isAssignableFrom(List.class) && cls.isAssignableFrom(JsonArray.class)) { // List を除く
//            if ( src.isEmpty() ) {
//                return (T)JsonValue.EMPTY_JSON_ARRAY;
//            }
//            return (T) src.stream().collect(JSONPArray.collector());
        } else if (cls.isArray()) { // 配列 要素の型も指定可能, Memberの型ではParameterizedTypeに振り分けられそう?
            return (L) listToArray(src, cls.getComponentType());
        }
        
        // Collection 要素の型は?
        Collection col = typeToList(cls);
        if ( col != null ) {
            src.forEach(col::add);
            return (L)col;
        }
        List list;
        if ( src instanceof List ) {
            list = (List)src;
        } else {
            list = new ArrayList(src);
        }
        return (L) listLcCast(list, cls);
    }

    /**
     *
     * @param <T>
     * @param type Generic List&lt;A&gt; のA が取れる
     * @param colCls List,Setの実装class
     * @return
     */
    private <T,M> T listCollectionTypeMap(Collection<M> list, ParameterizedType type, Collection col) {
        // 要素(単体)の型
        Type[] argTypes = type.getActualTypeArguments();
        if (argTypes.length == 0) { // 未検証
            list.forEach(col::add);
        } else {
            list.stream().map(m -> Rebind.valueOf(m,argTypes[0])).forEach(col::add);
        }
        return (T) col;
    }

    /**
     * Genericを外した形で作ればいいのかどうか
     * @param <I>
     * @param src
     * @param componentType
     * @return 
     */
    private static <I> Object listToArray(Collection<I> src, Type componentType) {
        Class raw = unGene(componentType);
        Object array = Array.newInstance(raw, src.size());
        
        int i = 0;
        for ( I val : src ) {
//            if ( val instanceof JSONValue ) { // 中身はGeneric対応で変換
//                Array.set(array, i++, OMAP.valueOf( val, componentType));
//            } else {
                Array.set(array, i++, Rebind.valueOf(val,componentType));
//            }
        }
        return array;
    }

    /**
     * Genericを外す仮
     * @param type
     * @return 
     */
    private static Class unGene( Type type ) {
        if ( type instanceof ParameterizedType ) {
            return (Class) ((ParameterizedType) type).getRawType();
        } else if ( type instanceof GenericArrayType ) {
            Type gct = ((GenericArrayType) type).getGenericComponentType();
            Class c = unGene(gct);
            return Array.newInstance(c, 0).getClass(); // 配列の型を作る方法は? arrayType() JDK12以降
        } else if ( type instanceof Class ) {
            return (Class) type;
        }
        throw new UnsupportedOperationException();
    }

    private static <T> T listLcCast(List array, Class<T> cls) {

        // ToDo: コンストラクタに突っ込む.
        int len = array.size();
        Constructor[] cnss = cls.getConstructors();

        for (Constructor c : cnss) {
            if (c.getParameterCount() != len) {
                continue;
            }

            Type[] pt = c.getGenericParameterTypes();
            Object[] params = new Object[pt.length];
//            cons.add(c);
//        }
//        if ( cons.size() == 1 ) { // 対象っぽいのがあれば
//            Constructor c = cons.get(0);
            try {
                for (int i = 0; i < pt.length; i++) {
                    Object o = array.get(i);
//                    if (o instanceof JSONValue) {
//                        params[i] = ((JSONValue) o).typeMap(pt[i]);
//                    } else {
                        params[i] = Rebind.valueOf(o, pt[i]);
//                    }
                }
                return (T) c.newInstance(params);
            } catch (UnsupportedOperationException | InstantiationException
                    | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                Logger.getLogger(JavaTypeConvert.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        throw new UnsupportedOperationException();
    }

    // JSONArrayもあり
    static Class<? extends Collection>[] COLL = new Class[]{ArrayList.class, HashSet.class, LinkedList.class};

    static Collection typeToList(Class cls) {
        for (Class<? extends Collection> colCls : COLL) {
            if (cls.isAssignableFrom(colCls)) {
                try {
                    return colCls.getConstructor().newInstance();
                } catch (NoSuchMethodException | SecurityException | InstantiationException
                        | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                    Logger.getLogger(JavaTypeConvert.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

        try {
            return (Collection) cls.getConstructor().newInstance();
        } catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException
                | IllegalArgumentException | InvocationTargetException ex) {
            Logger.getLogger(JavaTypeConvert.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
//        throw new UnsupportedOperationException("未サポートな型:" + cls.getTypeName());
    }

    @Override
    public T datetimeFormat(Calendar datetime) {
        if ( type instanceof Class ) {
            Class cls = (Class) type;
            if ( cls.isAssignableFrom( Calendar.class )) {
                return (T)datetime;
            } else if ( cls == java.util.Date.class) {
                return (T)new java.util.Date(datetime.getTimeInMillis());
            } else if ( cls == java.sql.Timestamp.class ) {
                return (T)new java.sql.Timestamp(datetime.getTimeInMillis());
            }
            
        }
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
