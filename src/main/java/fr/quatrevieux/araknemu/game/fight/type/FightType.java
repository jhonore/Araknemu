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

package fr.quatrevieux.araknemu.game.fight.type;

import fr.quatrevieux.araknemu.game.fight.ending.reward.RewardsGenerator;

import java.time.Duration;

/**
 * Fight type parameters
 */
public interface FightType {
    /**
     * The type id
     *
     * https://github.com/Emudofus/Dofus/blob/1.29/dofus/managers/GameManager.as#L1255
     */
    public int id();

    /**
     * Can cancel the fight without penalties
     */
    public boolean canCancel();

    /**
     * Does the fight type has a placement time limit ?
     *
     * @see FightType#placementTime() For get the placement time limit
     */
    public boolean hasPlacementTimeLimit();

    /**
     * Get the fight placement time in seconds
     * This value must be used only, and only if hasPlacementTimeLimit is set to true
     *
     * @see FightType#hasPlacementTimeLimit()
     *
     * @todo Return duration instead of int
     */
    public int placementTime();

    /**
     * Get the maximum duration of a turn
     */
    public Duration turnDuration();

    /**
     * Get the rewards generator
     */
    public RewardsGenerator rewards();
}
