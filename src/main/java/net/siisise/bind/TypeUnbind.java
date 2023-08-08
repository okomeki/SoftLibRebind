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
package net.siisise.bind;

import net.siisise.bind.format.TypeFormat;
import java.lang.reflect.Type;

/**
 *
 */
public interface TypeUnbind {
    
    /**
     * 解析対象 Class
     * 重複可能、順番は保証しない.
     * 含まなくても解析は可能.
     * 
     * @return unbind target Class
     */
    Type[] getSrcTypes();
    
    /**
     * 分解する機能.
     * ありとあらゆるObjectを返したいので該当しない場合は null や unknown ではなく自身を指す。
     * @param <T>
     * @param src
     * @param format
     * @return 自由形 該当しない場合はnullではなくTypeUnbind自身を返す
     */
    <T> T valueOf(Object src, TypeFormat<T> format);
}
