package net.siisise.bind.unbind.java;

import java.lang.reflect.Type;
import java.util.UUID;
import net.siisise.bind.TypeUnbind;
import net.siisise.bind.format.TypeFormat;
import net.siisise.bind.format.BindObject;

/**
 * UUIDを扱う拡張例.
 */
public class UnbindUUID implements TypeUnbind {

    @Override
    public Type[] getSrcTypes() {
        return new Type[] { UUID.class };
    }

    @Override
    public <T> T valueOf(Object uuid, TypeFormat<T> format) {
        if (uuid instanceof UUID) {
            if ( format instanceof BindObject ) {
                return ((BindObject<T>) format).objectFormat(uuid);
            }
            return format.stringFormat(uuid.toString());
        }
        return (T)this;
    }

}
