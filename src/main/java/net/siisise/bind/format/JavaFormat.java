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

import java.util.Calendar;
import java.util.Collection;
import java.util.Map;

/**
 * なにもしないさんぷる.
 * Map/List系出力をするJavaFormat
 * Object に割り当てるのは危ないかも.
 * Class/Object系で対象の型を特定する場合はJavaTypeConvert
 */
public class JavaFormat implements TypeFormat<Object> {

    @Override
    public Object mapFormat(Map map) {
        return map;
    }

    @Override
    public Object nullFormat() {
        return null;
    }

    @Override
    public Object booleanFormat(boolean bool) {
        return bool;
    }

    /**
     * Number同士の型変換ができないよ.
     * @param num
     * @return 
     */
    @Override
    public Object numberFormat(Number num) {
        return num;
    }

    @Override
    public Object stringFormat(String str) {
        return str;
    }

    @Override
    public Object collectionFormat(Collection col) {
        return col;
    }

    @Override
    public Object datetimeFormat(Calendar cal) {
        return cal;
//        return new Date(cal.getTimeInMillis());
    }

    @Override
    public Object arrayFormat(Object array) {
        return array;
    }
}
