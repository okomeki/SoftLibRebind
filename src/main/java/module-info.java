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

module net.siisise.rebind {
    requires java.logging;
    requires java.sql;
    requires net.siisise;
    exports net.siisise.bind;
    exports net.siisise.bind.format;
    exports net.siisise.bind.unbind;
    exports net.siisise.bind.unbind.java;
    uses net.siisise.bind.UnbindList;
    uses net.siisise.bind.format.TypeFormat;
    uses net.siisise.bind.TypeUnbind;
    provides net.siisise.bind.UnbindList with net.siisise.bind.unbind.JavaUnbind;
    provides net.siisise.bind.format.TypeFormat with net.siisise.bind.format.JavaFormat;
}
