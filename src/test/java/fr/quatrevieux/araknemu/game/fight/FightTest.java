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

package fr.quatrevieux.araknemu.game.fight;

import fr.quatrevieux.araknemu.core.event.Listener;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMapService;
import fr.quatrevieux.araknemu.game.fight.castable.effect.EffectsHandler;
import fr.quatrevieux.araknemu.game.fight.event.FightCancelled;
import fr.quatrevieux.araknemu.game.fight.event.FightStarted;
import fr.quatrevieux.araknemu.game.fight.event.FightStopped;
import fr.quatrevieux.araknemu.game.fight.exception.InvalidFightStateException;
import fr.quatrevieux.araknemu.game.fight.fighter.player.PlayerFighter;
import fr.quatrevieux.araknemu.game.fight.map.FightMap;
import fr.quatrevieux.araknemu.game.fight.module.FightModule;
import fr.quatrevieux.araknemu.game.fight.state.*;
import fr.quatrevieux.araknemu.game.fight.team.FightTeam;
import fr.quatrevieux.araknemu.game.fight.team.SimpleTeam;
import fr.quatrevieux.araknemu.game.fight.turn.order.AlternateTeamFighterOrder;
import fr.quatrevieux.araknemu.game.fight.type.ChallengeType;
import io.github.artsok.RepeatedIfExceptionsTest;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

class FightTest extends GameBaseCase {
    private Fight fight;
    private FightMap map;
    private List<FightTeam> teams;
    private Logger logger;

    private PlayerFighter fighter1, fighter2;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet.pushMaps().pushSubAreas().pushAreas();

        fight = new Fight(
            5,
            new ChallengeType(),
            map = container.get(FightService.class).map(container.get(ExplorationMapService.class).load(10340)),
            teams = new ArrayList<>(Arrays.asList(
                new SimpleTeam(fighter1 = new PlayerFighter(gamePlayer(true)), Arrays.asList(123), 0),
                new SimpleTeam(fighter2 = new PlayerFighter(makeOtherPlayer()), Arrays.asList(321), 1)
            )),
            new StatesFlow(
                new NullState(),
                new InitialiseState(),
                new PlacementState(),
                new ActiveState(),
                new FinishState()
            ),
            logger = Mockito.mock(Logger.class)
        );
    }

    @Test
    void getters() {
        assertEquals(5, fight.id());
        assertEquals(Arrays.asList(fighter1, fighter2), fight.fighters(false));
        assertSame(map, fight.map());
        assertInstanceOf(NullState.class, fight.state());
        assertEquals(teams, fight.teams());
        assertInstanceOf(ChallengeType.class, fight.type());
        assertInstanceOf(EffectsHandler.class, fight.effects());
        assertFalse(fight.active());
    }

    @Test
    void fighters() {
        assertEquals(Arrays.asList(fighter1, fighter2), fight.fighters(false));
        assertCount(0, fight.fighters(true));

        new PlacementState().start(fight);

        assertEquals(Arrays.asList(fighter1, fighter2), fight.fighters(true));
    }

    @Test
    void stateBadState() {
        assertThrows(InvalidFightStateException.class, () -> fight.state(PlacementState.class));
    }

    @Test
    void stateWithType() {
        assertInstanceOf(NullState.class, fight.state(NullState.class));

        fight.nextState();

        assertInstanceOf(PlacementState.class, fight.state(PlacementState.class));
    }

    @Test
    void teamByNumber() {
        assertSame(teams.get(0), fight.team(0));
        assertSame(teams.get(1), fight.team(1));
    }

    @Test
    void send() {
        fight.send("test");

        requestStack.assertLast("test");
    }

    @RepeatedIfExceptionsTest
    void schedule() throws InterruptedException {
        AtomicBoolean ab = new AtomicBoolean(false);

        fight.schedule(() -> ab.set(true), Duration.ofMillis(10));

        assertFalse(ab.get());

        Thread.sleep(15);
        assertTrue(ab.get());
    }

    @RepeatedIfExceptionsTest
    void execute() throws InterruptedException {
        AtomicBoolean ab = new AtomicBoolean(false);

        fight.execute(() -> {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            ab.set(true);
        });

        assertFalse(ab.get());

        Thread.sleep(15);
        assertTrue(ab.get());
    }

    @RepeatedIfExceptionsTest
    void executeWithExceptionShouldBeLogged() throws InterruptedException {
        RuntimeException raisedException = new RuntimeException("my error");

        fight.execute(() -> { throw raisedException; });

        Thread.sleep(10);

        Mockito.verify(logger).error("Error on fight executor : my error", raisedException);
    }

    @RepeatedIfExceptionsTest
    void scheduleWithExceptionShouldBeLogged() throws InterruptedException {
        RuntimeException raisedException = new RuntimeException("my error");

        fight.schedule(() -> { throw raisedException; }, Duration.ZERO);

        Thread.sleep(10);

        Mockito.verify(logger).error("Error on fight executor : my error", raisedException);
    }

    @Test
    void destroy() {
        fight.destroy();

        assertCount(0, fight.teams());
        assertEquals(0, fight.map().size());
    }

    @RepeatedIfExceptionsTest
    void startStop() throws InterruptedException {
        AtomicReference<FightStarted> ref = new AtomicReference<>();
        AtomicReference<FightStopped> ref2 = new AtomicReference<>();

        fight.dispatcher().add(new Listener<FightStarted>() {
            @Override
            public void on(FightStarted event) {
                ref.set(event);
            }

            @Override
            public Class<FightStarted> event() {
                return FightStarted.class;
            }
        });

        fight.dispatcher().add(new Listener<FightStopped>() {
            @Override
            public void on(FightStopped event) {
                ref2.set(event);
            }

            @Override
            public Class<FightStopped> event() {
                return FightStopped.class;
            }
        });

        fight.turnList().init(new AlternateTeamFighterOrder());

        fight.start();
        assertTrue(fight.active());
        assertNotNull(ref.get());
        assertSame(fight, ref.get().fight());

        Thread.sleep(205);

        assertTrue(fight.turnList().current().isPresent());

        fight.stop();
        assertSame(fight, ref2.get().fight());
        assertFalse(fight.active());
        assertFalse(fight.turnList().current().isPresent());

        assertBetween(205, 220, (int) fight.duration());
    }

    @Test
    void cancelActive() {
        fight.start();

        assertThrows(IllegalStateException.class, () -> fight.cancel());
    }

    @Test
    void cancel() {
        AtomicReference<FightCancelled> ref = new AtomicReference<>();
        fight.dispatcher().add(FightCancelled.class, ref::set);

        fight.cancel();

        assertSame(fight, ref.get().fight());
        assertCount(0, fight.teams());
        assertCount(0, fight.fighters());
    }

    @Test
    void cancelActiveForce() {
        AtomicReference<FightCancelled> ref = new AtomicReference<>();
        fight.dispatcher().add(FightCancelled.class, ref::set);

        fight.start();

        fight.cancel(true);

        assertSame(fight, ref.get().fight());
        assertCount(0, fight.teams());
        assertCount(0, fight.fighters());
    }

    @Test
    void register() {
        FightModule module = Mockito.mock(FightModule.class);

        Mockito.when(module.listeners()).thenReturn(new Listener[0]);

        fight.register(module);

        Mockito.verify(module).effects(fight.effects());
        Mockito.verify(module).listeners();
    }

    @Test
    void nextStateWillNotifyModules() {
        fight.nextState();

        FightModule module = Mockito.mock(FightModule.class);
        Mockito.when(module.listeners()).thenReturn(new Listener[0]);
        fight.register(module);

        fight.nextState();

        Mockito.verify(module).stateChanged(fight.state());
    }

    @Test
    void attach() {
        Object attachment = new Object();
        fight.attach(attachment);

        assertSame(attachment, fight.attachment(Object.class));
    }
}
