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

package fr.quatrevieux.araknemu.game.fight.ai.action;

import fr.arakne.utils.maps.CoordinateCell;
import fr.quatrevieux.araknemu.game.fight.ai.AI;
import fr.quatrevieux.araknemu.game.fight.ai.util.SpellCaster;
import fr.quatrevieux.araknemu.game.fight.castable.Castable;
import fr.quatrevieux.araknemu.game.fight.fighter.PassiveFighter;
import fr.quatrevieux.araknemu.game.fight.map.FightCell;
import fr.quatrevieux.araknemu.game.fight.turn.action.Action;
import fr.quatrevieux.araknemu.game.spell.Spell;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Try to teleport near enemy
 */
final public class TeleportNearEnemy implements ActionGenerator {
    private SpellCaster caster;
    private List<Spell> teleportSpells;

    /**
     * Select the best spell and cell couple for teleport
     */
    private class Selector {
        final private CoordinateCell<FightCell> enemyCell;
        private int distance;
        private FightCell cell;
        private Spell spell;

        public Selector(FightCell enemyCell, FightCell currentCell) {
            this.enemyCell = new CoordinateCell<>(enemyCell);
            this.distance = this.enemyCell.distance(new CoordinateCell<>(currentCell));
        }

        /**
         * Check if the current cell is adjacent to the enemy cell
         */
        public boolean adjacent() {
            return distance == 1;
        }

        /**
         * Push the teleport parameters and check if there are better than the previous
         */
        public void push(Spell spell, FightCell cell) {
            int currentDistance = new CoordinateCell<>(cell).distance(enemyCell);

            if (currentDistance < distance) {
                this.spell = spell;
                this.cell = cell;
                this.distance = currentDistance;
            }
        }

        /**
         * Get the best cast action
         * May return an empty optional if no teleport spell can be found, or if the fighter is already on the best cell
         */
        public Optional<Action> action() {
            if (spell == null) {
                return Optional.empty();
            }

            return Optional.of(caster.create(spell, cell));
        }
    }

    @Override
    public void initialize(AI ai) {
        caster = new SpellCaster(ai);
        teleportSpells = new ArrayList<>();

        for (Spell spell : ai.fighter().spells()) {
            if (spell.effects().stream().anyMatch(spellEffect -> spellEffect.effect() == 4)) {
                teleportSpells.add(spell);
            }
        }

        teleportSpells.sort(Comparator.comparingInt(Castable::apCost));
    }

    @Override
    public Optional<Action> generate(AI ai) {
        if (teleportSpells.isEmpty()) {
            return Optional.empty();
        }

        final int actionPoints = ai.turn().points().actionPoints();

        if (actionPoints < 1) {
            return Optional.empty();
        }

        Optional<? extends PassiveFighter> enemy = ai.enemy();

        if (!enemy.isPresent()) {
            return Optional.empty();
        }

        final Selector selector = new Selector(enemy.get().cell(), ai.fighter().cell());

        // Already at adjacent cell of the enemy
        if (selector.adjacent()) {
            return Optional.empty();
        }

        for (Spell spell : teleportSpells) {
            if (spell.apCost() > actionPoints) {
                continue;
            }

            for (FightCell cell : ai.map()) {
                // Target or launch is not valid
                if (!cell.walkable() || !caster.validate(spell, cell)) {
                    continue;
                }

                selector.push(spell, cell);

                // Adjacent cell found : no need to continue
                if (selector.adjacent()) {
                    return selector.action();
                }
            }
        }

        return selector.action();
    }
}
