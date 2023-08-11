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

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Optional;

/**
 * 出力の判定方法2つくらい
 * @param <T>
 */
public interface TypeBind<T> extends TypeFormat<T> {

    /**
     * T で型指定して継承する場合に省略可能にしておく.
     * @return 対象型
     */
    default Type targetClass() {
        return target(this);
    }

    public static Type target(TypeFormat bind) {
        Type[] ifs = bind.getClass().getGenericInterfaces();
        Optional<Type> an = Arrays.asList(ifs).stream().filter(v -> v instanceof ParameterizedType)
                .filter(t -> ((ParameterizedType)t).getRawType() == TypeBind.class)
                .findFirst();

        if ( an.isPresent() ) {
            ParameterizedType type = (ParameterizedType) an.get();
            return type.getActualTypeArguments()[0];
        }
        return null;
    }
    
}
