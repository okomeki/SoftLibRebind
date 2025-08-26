/*
 * Copyright 2025 okome.
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
 * ASN.1 BER/CER/DER用タグ拡張
 */
public interface BindASN1<T> {
    
    public static final int APPLICATION = 1;
    public static final int CONTEXT_SPECIFIC = 2;
    public static final int PRIVATE = 3;

    /**
     * 名前の代わりにタグを付ける.
     * 型情報をタグ番号で上書きする
     * 名前のある構造ではタグは不要.
     * @param cls APPLICATION 1, CONTEXT_SPECIFIC 2, PRIVATE 3
     * @param tag 付け替えるタグ番号
     * @param v 元の値
     * @return 
     */
    default T implicit(int cls, int tag, T v) {
        return v;
    }

    /**
     * 名前の代わりにタグを付ける.
     * 型情報も保存される。
     * 名前のある構造ではタグは不要.
     * @param cls APPLICATION, CONTEXT_SPECIFIC, PRIVATE
     * @param tag タグ
     * @param v 元の値
     * @return 
     */
    default T explicit(int cls, int tag, T v) {
        return v;
    }

    default T contextSpecific(int tag, T v) {
        return explicit(CONTEXT_SPECIFIC,tag,v);
    }
            
    default T contextSpecificImplicit(int tag, T v) {
        return implicit(CONTEXT_SPECIFIC,tag,v);
    }

    default T application(int tag, T v) {
        return explicit(APPLICATION,tag,v);
    }
            
    default T applicationImplicit(int tag, T v) {
        return implicit(APPLICATION,tag,v);
    }

    default T privateExplicit(int tag, T v) {
        return explicit(PRIVATE,tag,v);
    }
            
    default T privateImplicit(int tag, T v) {
        return implicit(PRIVATE,tag,v);
    }
}
