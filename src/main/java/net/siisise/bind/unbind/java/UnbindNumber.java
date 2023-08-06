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
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import net.siisise.bind.TypeUnbind;
import net.siisise.bind.format.TypeFormat;
import net.siisise.lang.Binary16;

/**
 * 数値抽出.
 * Java標準型の他にCBORで使う16bit Floatを追加してみた.
 */
public class UnbindNumber implements TypeUnbind {

    @Override
    public Type[] getSrcTypes() {
        return new Type[] { Number.class, Integer.class, Long.class, Float.class, Short.class, BigInteger.class, Double.class, BigDecimal.class, Binary16.class };
    }

    @Override
    public Object valueOf(Object src, TypeFormat format) {
        if ( src instanceof Binary16 ) {
            src = ((Binary16)src).floatValue();
        }
        // ToDo: JSONNumber とか外したい
        if ( src instanceof Number ) {
            if ( Arrays.asList(getSrcTypes()).contains(src.getClass())) {
                return format.numberFormat((Number)src);
            } else {
                throw new UnsupportedOperationException("未定");
            }
        }
        return this;
    }
    
}
