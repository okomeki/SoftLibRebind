/*
 * Copyright 2024 okome.
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
 * YAML, CBOR などで使える
 * NULLと分けておく.
 * @param <T> 出力形
 */
public interface BindUndefined<T> extends BindNull<T> {
    /**
     * undefined型があるときの対応.
     * @return 該当する情報, ないときはNULL的なものとする
     */
    default T undefinedFormat() {
        return nullFormat();
    }
}
