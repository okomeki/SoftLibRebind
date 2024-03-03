
package net.siisise.bind;

import java.lang.reflect.ParameterizedType;
import net.siisise.bind.format.TypeFormat;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.stream.Collectors;
import net.siisise.bind.format.ContentBind;
import net.siisise.bind.format.JavaTypeConvert;
import net.siisise.bind.format.TypeBind;
import net.siisise.bind.unbind.JavaUnbind;
import net.siisise.bind.unbind.java.UnbindObject;

/**
 * JSON Binding から他の型も使えそうな汎用版にする予定は未定.
 * net.siisise.json.omap として作っていたもののリメイク.
 */
public class Rebind {

    /**
     * 分解装置
     */
    private List<TypeUnbind> unbindList;
    
    final Map<Type, List<TypeUnbind>> unbindMap = new HashMap<>();
    Map<Type, TypeFormat> formats = new HashMap<>();
    Map<String, TypeFormat> mimes = new HashMap<>();
    
    // 全部 static か部分的にか
    static Rebind bind = new Rebind();
    
    /**
     * 微妙な全部入り.
     */
    public Rebind() {
        this( loadUnbindLists(), loadTypeFormats());
    }

    /**
     * 
     * @param srcUnbindList
     * @param bindList 
     */
    public Rebind(List<TypeUnbind> srcUnbindList, List<TypeFormat> bindList) {
        this.unbindList = new ArrayList<>();
        unbindList.addAll(srcUnbindList);
        
        for ( TypeUnbind un : this.unbindList ) {
            Type[] srcs = un.getSrcTypes();
            for ( Type src : srcs ) {
                putUnbind(src, un);
            }
        }

        // ContentBind のついているものはContentTypeで、ついていないものはTypeで振り分ける?
        for ( TypeFormat form : bindList ) {
            if ( form instanceof ContentBind ) {
                String contentType = ((ContentBind)form).contentType();
                if ( contentType != null ) {
                    mimes.put(contentType, form);
                }
            } else { // TypeBind<>
                if ( form instanceof TypeBind ) {
                    Type target = ((TypeBind)form).targetClass();
                    if ( target != null ) {
                        formats.put(target, form);
                    }
                }
            }
        }
    }
    
    /**
     * とりあえずList でまとめて取得してみる.
     * @return 
     */
    static List<TypeUnbind> loadUnbindLists() {
        ServiceLoader<UnbindList> unbindLoader = ServiceLoader.load(UnbindList.class);
        List<TypeUnbind> ubs = new ArrayList<>();
        UnbindList javaUnbind = null;
        
        for ( UnbindList unbindList : unbindLoader ) {
            if ( unbindList.getClass() == JavaUnbind.class ) {
                javaUnbind = unbindList;
            } else {
//                System.out.println("Load UnbindList : " + unbindList.getClass().getName());
                ubs.addAll(Arrays.asList(unbindList.getList()));
            }
        }
        // Javaは最後
        ubs.addAll(Arrays.asList(javaUnbind.getList()));
        
        return ubs;
    }
/*
    static List<TypeUnbind> loadUnbinds() {
        ServiceLoader<TypeUnbind> unbindLoader = ServiceLoader.load(TypeUnbind.class);
        List<TypeUnbind> ubs = new ArrayList<>();
        for ( TypeUnbind unbind : unbindLoader ) {
            ubs.add(unbind);
        }
        return ubs;
    }
*/
    
    /**
     * サービスとして登録された TypeFormat の一覧を取得する.
     * @return 使える TypeFormat 一覧.
     */
    static List<TypeFormat> loadTypeFormats() {
        ServiceLoader<TypeFormat> formatLoader = ServiceLoader.load(TypeFormat.class);
        List<TypeFormat> formats = new ArrayList<>();
        for ( TypeFormat format : formatLoader ) {
//            System.out.println("Load TypeFormat : " + format.getClass().getName());
            formats.add(format);
        }
        
        return formats;
    }
    
    void putUnbind(Type cls, TypeUnbind unbind) {
        synchronized(unbindMap) {
            List<TypeUnbind> uns = unbindMap.get(cls);
            if ( uns == null) {
                uns = new ArrayList<>();
                unbindMap.put(cls, uns);
            }
            if (!uns.contains(unbind)) {
                uns.add(unbind);
            }
        }
    }

    /**
     * バイナリ変換用.
     * @param <T> 出力型
     * @param src source data
     * @param target output Class or Type
     * @return sourceからtarget型に変換されたもの
     */
    public static <T> T valueOf(Object src, Type target) {
        TypeFormat<T> format = bind.convert(target);
        return valueOf(src, format);
    }

    /**
     * とりあえずContentTypeで指定可能にしたもの.
     * 出力方法は変えるかも.
     * @param <T> byte[], Strinig, Packet など
     * @param src 元データ
     * @param contentType 出力形式
     * @return 符号化データ.
     */
    public static <T> T valueOf(Object src, String contentType) {
        TypeFormat<T> format = bind.mimes.get(contentType);
        if ( format == null ) {
            throw new IllegalStateException();
        } 
        return valueOf(src, format);
    }

    /**
     * Stream 変換可能.
     * 出力TypeFormatを知っているとき.
     * @param <T> 出力型
     * @param src source data
     * @param format 出力整形用
     * @return 出力
     */
    public static <T> T valueOf(Object src, TypeFormat<T> format) {
        return bind.valueOf(src, format, bind.unbindList);
    }

    /**
     * 
     * @param <T>
     * @param src source data
     * @param format 出力ォーマット
     * @param ublist not null 分解用機能群
     * @return 
     */
    public <T> T valueOf(Object src, TypeFormat<T> format, List<TypeUnbind> ublist) {
        // Unbind
        List<TypeUnbind> unbinds;
        synchronized (unbindMap) {
            unbinds = unbindMap.get(src == null ? null : src.getClass());
        }
        if (unbinds != null) {
            for ( TypeUnbind unbind : unbinds ) {
                Object val = unbind.valueOf(src, format);
                if (val != unbind) { // 成功
                    return (T)val;
                }
            }
        }

        for ( TypeUnbind ub : ublist ) {
            Object val = ub.valueOf(src, format);
            if (val != ub) {
                putUnbind(src == null ? null : src.getClass(), ub);
                return (T)val;
            }
        }
        throw new UnsupportedOperationException();
    }
    
    Type toClass(Type type) {
        if ( type instanceof ParameterizedType ) {
            return ((ParameterizedType)type).getRawType();
        }
        // いろいろあるけど略
        return type;
    }

    /**
     * stream の逆っぽい構築ができないかな
     * @param <T>
     * @param target
     * @return 
     */
    public <T> TypeFormat<T> convert(Type target) {
        TypeFormat<T> format = formats.get(target);
        if ( format == null) {
            Type rawClass = toClass(target);
            
            if ( rawClass instanceof Class ) {
                List<Map.Entry<Type, TypeFormat>> list = formats.entrySet().parallelStream()
                        .filter(e -> ((e.getKey() instanceof Class) ))
                        .filter(e -> ((Class)e.getKey()).isAssignableFrom((Class)rawClass)).collect(Collectors.toList());
                if ( !list.isEmpty() ) {
//                    System.out.println("Target match : " + list.size());
                    while ( list.size() > 1 ) {
                        Map.Entry<Type, TypeFormat> a = list.get(0);
                        Map.Entry<Type, TypeFormat> b = list.get(1);
                        if ( ((Class)a.getKey()).isAssignableFrom((Class)b.getKey())) {
                            list.remove(0);
                        } else if ( ((Class)a.getKey()).isAssignableFrom((Class)b.getKey())) {
                            list.remove(1);
                        } else { // ?
                            break;
                        }
                    }
                    format = list.get(0).getValue();
                    formats.put(target, format);
                    return format;
                }
            }
            format = new JavaTypeConvert(target);
        }
//        System.out.println("Target match : " + format.getClass().getName());
        return format;
    }
    
    public static <T> TypeFormat<T> s_convert(Type target) {
        return bind.convert(target);
    }

    // 分解工程を省いた各型の変換.
    
    public static <T> T typeNull(Type type) {
        TypeFormat<T> format = s_convert(type);
        return format.nullFormat();
    }
    
    public static <T> T typeBoolean(boolean bool, Type type) {
        TypeFormat<T> format = s_convert(type);
        return format.booleanFormat(bool);
    }
    
    public static <T> T typeNumber(Number num, Type type) {
        TypeFormat<T> format = s_convert(type);
        return format.numberFormat(num);
    }
    
    public static <T> T typeString(String str, Type type) {
        TypeFormat<T> format = s_convert(type);
        return format.stringFormat(str);
    }

    public static <T> T typeList(List list, Type type) {
        TypeFormat<T> format = s_convert(type);
        return format.listFormat(list);
    }

    public static <T> T typeSet(Set set, Type type) {
        TypeFormat<T> format = s_convert(type);
        return format.setFormat(set);
    }

    public static <T> T typeCollection(Collection col, Type type) {
        TypeFormat<T> format = s_convert(type);
        return format.collectionFormat(col);
    }

    public static <T> T typeMap(Map map, Type type) {
        TypeFormat<T> format = s_convert(type);
        return format.mapFormat(map);
    }

    /**
     * 該当クラスのpublic fieldのみ抽出する
     * @param <T>
     * @param object
     * @param type
     * @return 
     */
    public static <T> T typeObjectField(Object object, Type type) {
        UnbindObject uo = new UnbindObject(UnbindObject.MapType.FIELD);
        TypeFormat<T> format = s_convert(type);
        return (T)uo.valueOf(object, format);
    }

    /**
     * 継承もとクラスのFieldまで抽出する.
     * @param <T>
     * @param object
     * @param type
     * @return 
     */
    public static <T> T typeObjectDeclaredField(Object object, Type type) {
        UnbindObject uo = new UnbindObject(UnbindObject.MapType.DECLARED_FIELD);
        TypeFormat<T> format = s_convert(type);
        return (T)uo.valueOf(object, format);
    }

    public static <T> T typeObjectBean(Object object, Type type) {
        UnbindObject uo = new UnbindObject(UnbindObject.MapType.BEAN);
        TypeFormat<T> format = s_convert(type);
        return (T)uo.valueOf(object, format);
    }
    
}
