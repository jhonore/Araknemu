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

package fr.quatrevieux.araknemu.game.fight.castable.validator;

import fr.quatrevieux.araknemu.game.fight.castable.Castable;
import fr.quatrevieux.araknemu.game.fight.map.FightCell;
import fr.quatrevieux.araknemu.game.fight.turn.Turn;
import fr.quatrevieux.araknemu.network.game.out.info.Error;

/**
 * Aggregates of constraints
 */
final public class ConstraintsAggregateValidator<T extends Castable> implements CastConstraintValidator<T> {
    final private CastConstraintValidator<? super T>[] validators;

    public ConstraintsAggregateValidator(CastConstraintValidator<? super T>[] validators) {
        this.validators = validators;
    }

    @Override
    public Error validate(Turn turn, T action, FightCell target) {
        for (CastConstraintValidator<? super T> validator : validators) {
            Error error = validator.validate(turn, action, target);

            if (error != null) {
                return error;
            }
        }

        return null;
    }
}
