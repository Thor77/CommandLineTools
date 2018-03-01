/*
 *     Copyright 2018 Jean-Pierre Hotz
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.hotzjeanpierre.commandlinetools.command.testutilities;

public class ByteArrayProcessor {

    public static byte[] bringToLength(byte[] toProcess, int length) {
        if(toProcess == null) {
            toProcess = new byte[0];
        }

        byte[] result = new byte[length];

        int max = (length > toProcess.length)? toProcess.length : length;

        for(int i = 0; i < max; i++) {
            result[i] = toProcess[i];
        }

        return result;
    }

    public static byte[] parseFromHexString(String str) {
        // bring the string to an even length without affecting its 'content'
        if(str.length() % 2 == 1) {
            str = "0" + str;
        }

        // create an array of the needed length
        byte[] result = new byte[str.length() / 2];

        // parse every byte of the given string
        for(int i = 0; i < result.length; i++) {
            result[i] = (byte) Integer.parseInt(str.substring(i * 2, (i + 1) * 2), 16);
        }

        return result;
    }

}