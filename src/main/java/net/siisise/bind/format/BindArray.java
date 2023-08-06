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

/**
 * 配列全般.
 * primitive 型と Object系な型の2種類
 * 主にbyte列, char列を扱いたいときに
 * その他はListなどとして扱う場合が多い
 * @param <T> 出力型
 */
public interface BindArray<T> {
    /**
     * 配列を出力する
     * @param array 配列
     * @return 
     */
    T arrayFormat(Object array);
}
