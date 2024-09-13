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

import java.net.URI;

/**
 * 文字列対応.
 * 基本的にString で処理することにする。
 * char配列、int配列に対応する? とりあえず型変換でなんとかなるものはあとまわし。
 * ASN.1のように複数型がある場合はCharSequenceの拡張として処理できる道も作ってみる。
 * 
 * @param <T> 出力型
 */
public interface BindString<T> {

    /**
     * 一般的な文字列をなんとかする.
     *
     * @param str 文字列
     * @return 符号化
     */
    T stringFormat(String str);

    /**
     * 文字列として処理する.
     *
     * @param chars 文字列
     * @return 符号化
     */
    default T charArrayFormat(char[] chars) {
        return stringFormat(String.valueOf(chars));
    }

    /**
     * 多様な文字列型を処理したい場合の抜け道.
     *
     * @param seq 文字列
     * @return 符号化
     */
    default T stringFormat(CharSequence seq) {
        return stringFormat(seq.toString());
    }

    
    default T uriFormat(URI uri) {
        return stringFormat(uri.toString());
    }
}
