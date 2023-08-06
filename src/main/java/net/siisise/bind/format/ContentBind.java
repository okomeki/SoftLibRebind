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
 * contentType / MIME Type で判別できる出力形態.
 * 
 * byte[], String の他 SoftLib の Packet, Blockっぽいものも標準で扱う
 * @param <T> byte[], String, Packet, Block など
 */
public interface ContentBind<T> extends TypeFormat<T> {
    
    /**
     * ファイルやストリーム化可能な場合に media type を指定できる。
     * ASN.1 の OID , XMLなどのURLでもいいのかも (重複しなければ)
     * @return 未定義バイナリは nullあり、 "application/octet-stream" かもしれない
     */
    String contentType();
}
