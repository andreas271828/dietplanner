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

public class Pair<A, B> {
    private final A a;
    private final B b;

    public static <A, B> Pair<A, B> pair(final A a, final B b) {
        return new Pair<A, B>(a, b);
    }

    private Pair(final A a, final B b) {
        this.a = a;
        this.b = b;
    }

    public A a() {
        return a;
    }

    public B b() {
        return b;
    }

    @Override
    public int hashCode() {
        // TODO: Faster hash algorithm
        return toString().hashCode();
    }

    @Override
    public String toString() {
        return "<" + a.toString() + ", " + b.toString() + ">";
    }
}
