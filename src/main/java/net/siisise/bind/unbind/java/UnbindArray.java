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

import java.lang.reflect.Type;
import java.util.ArrayList;
import net.siisise.bind.format.TypeFormat;
import net.siisise.bind.TypeUnbind;

/**
 * 配列全般
 */
public class UnbindArray implements TypeUnbind {

    @Override
    public Type[] getSrcTypes() {
        return new Type[] { ArrayList.class };
    }

    @Override
    public Object valueOf(Object src, TypeFormat format) {
        Class cls = src.getClass();
        if ( cls.isArray() ) {
            return format.arrayFormat(src);
        }
        return this;
    }
    
}
