/*
 * This file is part of Araknemu.
 *
 * Araknemu is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Araknemu is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Araknemu.  If not, see <https://www.gnu.org/licenses/>.
 *
 * Copyright (c) 2017-2019 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.data.value;

/**
 * Position object into the world
 */
final public class Position {
    final private int map;
    final private int cell;

    public Position(int map, int cell) {
        this.map = map;
        this.cell = cell;
    }

    public int map() {
        return map;
    }

    public int cell() {
        return cell;
    }

    public boolean isNull() {
        return map == 0 && cell == 0;
    }

    /**
     * Change the cell position
     */
    public Position newCell(int cell) {
        return new Position(map, cell);
    }

    @Override
    public boolean equals(Object o) {
        return
            this == o
            || (o instanceof Position && equals((Position) o))
        ;
    }

    public boolean equals(Position other) {
        return other != null && other.cell == cell && other.map == map;
    }

    @Override
    public int hashCode() {
        int result = map;
        result = 31 * result + cell;
        return result;
    }

    @Override
    public String toString() {
        return "(" + map + ", " + cell + ")";
    }
}
