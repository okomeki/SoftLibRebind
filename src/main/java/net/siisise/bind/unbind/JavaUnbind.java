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
package net.siisise.bind.unbind;

import net.siisise.bind.unbind.java.UnbindDatetime;
import net.siisise.bind.unbind.java.UnbindString;
import net.siisise.bind.unbind.java.UnbindObject;
import net.siisise.bind.unbind.java.UnbindNumber;
import net.siisise.bind.unbind.java.UnbindCollection;
import net.siisise.bind.unbind.java.UnbindBoolean;
import net.siisise.bind.unbind.java.UnbindNull;
import net.siisise.bind.unbind.java.UnbindMap;
import net.siisise.bind.unbind.java.UnbindArray;
import net.siisise.bind.TypeUnbind;
import net.siisise.bind.UnbindList;
import net.siisise.bind.unbind.java.UnbindUUID;

/**
 * Javaの一般プリミティブ型、classを抽出する.
 */
public class JavaUnbind implements UnbindList {

    static final TypeUnbind[] unbindList = {
        new UnbindNull(),
        new UnbindBoolean(),
        new UnbindNumber(),
        new UnbindString(),
        new UnbindArray(),
        new UnbindMap(),
        new UnbindCollection(),
        new UnbindDatetime(),
        new UnbindUUID(),
        new UnbindObject()
    };
    
    @Override
    public TypeUnbind[] getList() {
        return unbindList;
    }
    
}
