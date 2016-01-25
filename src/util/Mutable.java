/**********************************************************************
 DietPlanner

 Copyright (C) 2015-2016 Andreas Huemer

 This file is part of DietPlanner.

 DietPlanner is free software: you can redistribute it and/or modify it
 under the terms of the GNU General Public License as published by the
 Free Software Foundation, either version 3 of the License, or (at
 your option) any later version.

 DietPlanner is distributed in the hope that it will be useful, but
 WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program. If not, see <http://www.gnu.org/licenses/>.
 **********************************************************************/
package util;

public class Mutable<T> {
    private T t;

    public static <T> Mutable<T> mutable() {
        return new Mutable<T>();
    }

    public static <T> Mutable<T> mutable(final T t) {
        return new Mutable<T>(t);
    }

    private Mutable() {
    }

    private Mutable(final T t) {
        this.t = t;
    }

    public T get() {
        return t;
    }

    public void set(final T t) {
        this.t = t;
    }

    @Override
    public String toString() {
        return t.toString();
    }
}
