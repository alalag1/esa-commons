/*
 * Copyright 2020 OPPO ESA Stack Project
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
package esa.commons.function;

import esa.commons.Checks;
import esa.commons.ExceptionUtils;

import java.util.function.DoubleFunction;
import java.util.function.Function;

/**
 * Represents a function that accepts an double-valued argument and produces a result, which allows user to throw a
 * checked exception.
 *
 * @param <R> the type of the result of the function
 * @see DoubleFunction
 */
@FunctionalInterface
public interface ThrowingDoubleFunction<R> {

    /**
     * Wraps a {@link ThrowingDoubleFunction} to {@link DoubleFunction} which will rethrow the error thrown by the given
     * {@link ThrowingDoubleFunction}
     *
     * @param f   target to wrap
     * @param <R> the type of the result of the function
     *
     * @return transferred
     */
    static <R> DoubleFunction<R> rethrow(ThrowingDoubleFunction<R> f) {
        Checks.checkNotNull(f);
        return (t) -> {
            try {
                return f.apply(t);
            } catch (Throwable e) {
                ExceptionUtils.throwException(e);
                // never reach this statement
                return null;
            }
        };
    }

    /**
     * Wraps a {@link ThrowingDoubleFunction} to {@link DoubleFunction} which will use the given value of {@code
     * onFailure} as the result of the function when an error is thrown by the given {@link ThrowingDoubleFunction}
     *
     * @param f         target to wrap
     * @param onFailure onFailure
     * @param <R>       the type of the result of the function
     *
     * @return transferred
     */
    static <R> DoubleFunction<R> onFailure(ThrowingDoubleFunction<R> f,
                                           R onFailure) {
        Checks.checkNotNull(f);
        return (t) -> {
            try {
                return f.apply(t);
            } catch (Throwable e) {
                return onFailure;
            }
        };
    }

    /**
     * @see #failover(ThrowingDoubleFunction, ObjDoubleFunction)
     */
    static <R> DoubleFunction<R> failover(ThrowingDoubleFunction<R> f,
                                          Function<Throwable, R> failover) {
        return failover(f, (e, t) -> failover.apply(e));
    }

    /**
     * Wraps a {@link ThrowingDoubleFunction} to {@link DoubleFunction} which will handle the error thrown by the given
     * {@link ThrowingDoubleFunction} with the given {@code failover} operation of {@link ObjDoubleFunction}
     *
     * @param f        target to wrap
     * @param failover failover
     * @param <R>      the type of the result of the function
     *
     * @return transferred
     */
    static <R> DoubleFunction<R> failover(ThrowingDoubleFunction<R> f,
                                          ObjDoubleFunction<Throwable, R> failover) {
        Checks.checkNotNull(f);
        Checks.checkNotNull(failover);
        return (t) -> {
            try {
                return f.apply(t);
            } catch (Throwable e) {
                return failover.apply(e, t);
            }
        };
    }

    /**
     * @see DoubleFunction#apply(double)
     */
    R apply(double t) throws Throwable;

}
