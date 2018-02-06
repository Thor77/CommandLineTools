/*
 *     Copyright 2017 Jean-Pierre Hotz
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

package de.hotzjeanpierre.commandlinetools.command.exceptions;

/**
 * This exception may be thrown whenever there is an parameter given twice or more often.
 */
public class DuplicateParameterException extends RuntimeException {

    public DuplicateParameterException() {
    }

    public DuplicateParameterException(String message) {
        super(message);
    }

    public DuplicateParameterException(String message, Throwable cause) {
        super(message, cause);
    }

    public DuplicateParameterException(Throwable cause) {
        super(cause);
    }

    public DuplicateParameterException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}