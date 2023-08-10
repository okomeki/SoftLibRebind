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
 * Number型、中身は精査されていない.
 * @param <T> 出力型
 */
public interface BindNumber<T> {
    /**
     * 整数型 Byte Short Integer BigInteger
     * 浮動小数点型 Float Double BigDecimal
     * を想定したもの.
     * 他の型もあるかもしれないけど事前変換がおすすめ.
     * @param num 数値型の値
     * @return 出力
     */
    T numberFormat(Number num);
}
