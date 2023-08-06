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

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import net.siisise.io.BASE64;

/**
 * よくある基本型をまとめたもの.
 * 他の型が必要な場合は別途つけてもよい.
 * @param <T> 出力型
 */
public interface TypeFormat<T> extends BindNull<T>, BindBoolean<T>, BindNumber<T>, BindString<T>, BindArray<T>, BindCollection<T> {

    /**
     * 配列をbyte[] ,char[], list/set系に振り分ける
     * @param array
     * @return 
     */
    @Override
    default T arrayFormat(Object array) {
        Class cls = array.getClass();
        if ( !cls.isArray() ) {
            throw new IllegalStateException();
        }
        Class cpType = cls.getComponentType();
        if (cpType.isPrimitive()) {
            if ( cpType == Byte.TYPE ) {
                return byteArrayFormat((byte[])array);
            } else if ( cpType == Character.TYPE ) {
                return charArrayFormat((char[])array);
            }
            List list = new ArrayList();
            int size = Array.getLength(array);
            for ( int i = 0; i < size; i++ ) {
                list.add(Array.get(array, i));
            }
            return listFormat(list);
        }
        // Arrays.asList が難しい
        Object[] oa = (Object[])array;
        return listFormat(Arrays.asList(oa));
    }

    /**
     * とりあえず文字列に変換する.
     * @param bytes
     * @return 
     */
    default T byteArrayFormat(byte[] bytes) {
        BASE64 b64 = new BASE64(BASE64.URL, 0);
        return stringFormat(b64.encode(bytes));
    }

    /**
     * 文字列として処理する.
     * @param chars
     * @return 形成済み 
     */
    default T charArrayFormat(char[] chars) {
        return stringFormat(String.valueOf(chars));
    }

    /**
     * 日時変換 Date型、Calendar型
     * 指定のない場合はRFC 3339 形式で文字列に. (TimeZone Zの場合のみ)
     * 
     * TimeZone は Zにしたい
     * @param cal ミリ秒 TZつき
     * @return RFC 3339 形式 文字列
     */
    default T datetimeFormat(Calendar cal) {
//        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        SimpleDateFormat rfc3339format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
        rfc3339format.setCalendar(cal);
        String dfstr = rfc3339format.format(new Date(cal.getTimeInMillis()));
        return stringFormat(dfstr);

    }
}
