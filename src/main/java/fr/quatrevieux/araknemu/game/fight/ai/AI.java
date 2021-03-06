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
 * Copyright (c) 2017-2020 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.game.fight.ai;

import fr.quatrevieux.araknemu.game.fight.fighter.ActiveFighter;
import fr.quatrevieux.araknemu.game.fight.fighter.PassiveFighter;
import fr.quatrevieux.araknemu.game.fight.map.BattlefieldMap;
import fr.quatrevieux.araknemu.game.fight.turn.Turn;

import java.util.Optional;
import java.util.stream.Stream;

/**
 * Base type for AI
 * Use by action generators as data accessor
 */
public interface AI {
    /**
     * Start the AI
     * The AI will be pushed into the fight to be executed
     *
     * @param turn The current turn
     */
    public void start(Turn turn);

    /**
     * Get the fighter controlled by the AI
     */
    public ActiveFighter fighter();

    /**
     * Get the current fight map
     */
    public BattlefieldMap map();

    /**
     * Get the current turn
     */
    public Turn turn();

    /**
     * Get all fighter of the current fight
     */
    public Stream<? extends PassiveFighter> fighters();

    /**
     * Get all alive enemies of the fighter
     * This method behavior can change, depending of the AI resolution strategy
     */
    public Stream<? extends PassiveFighter> enemies();

    /**
     * Get the best enemy
     * This method behavior can change, depending of the AI resolution strategy
     *
     * An empty optional can be returned, if there is no enemy which match
     */
    public Optional<? extends PassiveFighter> enemy();
}
