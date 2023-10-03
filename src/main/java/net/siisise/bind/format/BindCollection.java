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

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Set, List, Map をまとめてどうにかする.
 * Array, Object は対象外.
 * @param <T> 出力型
 */
public interface BindCollection<T> {
    // List / Array / Object / Map は個別に定義する?

    /**
     * 
     * @param map 中身は初期状態かも
     * @return 
     */
    T mapFormat(Map map);

    /**
     * listの処理.
     * collection としてまとめておく.
     * @param list 中身は初期状態かも
     * @return 形成済み
     */
    default T listFormat(List list) {
        return collectionFormat(list);
    }

    /**
     * Setの処理.
     * 標準ではCollection として処理.
     * @param set 中身は初期状態かも
     * @return 
     */
    default T setFormat(Set set) {
        return collectionFormat(set);
    }

    /**
     * Set / List は Collection としてまとめても分けてもいい.
     * 
     * @param col ListでもSetでもないことがある.
     * @return 
     */
    T collectionFormat(Collection col);
}
