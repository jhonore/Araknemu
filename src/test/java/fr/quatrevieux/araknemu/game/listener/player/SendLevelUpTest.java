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

package fr.quatrevieux.araknemu.game.listener.player;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.player.experience.event.PlayerLevelUp;
import fr.quatrevieux.araknemu.network.game.out.account.NewPlayerLevel;
import fr.quatrevieux.araknemu.network.game.out.account.Stats;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

class SendLevelUpTest extends GameBaseCase {
    private SendLevelUp listener;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        listener = new SendLevelUp(gamePlayer(true));

        requestStack.clear();
    }

    @Test
    void onLevelUp() throws SQLException, ContainerException {
        listener.on(new PlayerLevelUp(50));

        requestStack.assertAll(
            new NewPlayerLevel(50),
            new Stats(gamePlayer().properties())
        );
    }
}