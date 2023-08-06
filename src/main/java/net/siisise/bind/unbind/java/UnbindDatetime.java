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
package net.siisise.bind.unbind.java;

import java.lang.reflect.Type;
import java.util.Calendar;
import net.siisise.bind.TypeUnbind;
import net.siisise.bind.format.TypeFormat;

/**
 * 日時のいろいろ
 * SQL で扱えそうな範囲くらい?
 */
public class UnbindDatetime implements TypeUnbind {

    @Override
    public Type[] getSrcTypes() {
        return new Type[] { java.util.Date.class, Calendar.class };
    }

    @Override
    public Object valueOf(Object src, TypeFormat format) {
        // 1970/01/01T00:00:00Z からのミリ秒
        long datetime;
        Calendar cal = Calendar.getInstance();
        if ( src instanceof java.sql.Date ) {
            java.sql.Date sql = (java.sql.Date)src;
            datetime = sql.getTime();
            cal.setTimeInMillis(datetime);
        } else if ( src instanceof java.sql.Time ) {
            java.sql.Time time = (java.sql.Time)src;
            datetime = time.getTime();
            cal.setTimeInMillis(datetime);
        } else if ( src instanceof java.sql.Timestamp ) {
            java.sql.Timestamp ts = (java.sql.Timestamp)src;
            datetime = ts.getTime();
            cal.setTimeInMillis(datetime);
        } else if ( src instanceof java.util.Date ) { // long ms 国際化されていない日付時刻 UTC想定
            datetime = ((java.util.Date)src).getTime();
            cal.setTimeInMillis(datetime);
        } else if ( src instanceof java.util.Calendar ) { // long ms
            cal = (Calendar)src;
        } else {
            return this;
        }
        return format.datetimeFormat(cal);
    }
    
    
    
}
