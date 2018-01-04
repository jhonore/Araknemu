package fr.quatrevieux.araknemu.game.handler.game;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMapService;
import fr.quatrevieux.araknemu.network.exception.CloseImmediately;
import fr.quatrevieux.araknemu.network.game.in.game.AskExtraInfo;
import fr.quatrevieux.araknemu.network.game.out.game.AddSprites;
import fr.quatrevieux.araknemu.network.game.out.game.MapReady;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class LoadExtraInfoTest extends GameBaseCase {
    private LoadExtraInfo handler;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        handler = new LoadExtraInfo();
        dataSet.pushMaps();
        login();
        gamePlayer();
    }

    @Test
    void handleWithoutLoadMap() throws Exception {
        assertThrows(CloseImmediately.class, () -> handler.handle(session, new AskExtraInfo()));
    }

    @Test
    void handleSuccess() throws Exception {
        gamePlayer().join(
            container.get(ExplorationMapService.class).load(10300)
        );

        handler.handle(session, new AskExtraInfo());

        requestStack.assertAll(
            new AddSprites(
                gamePlayer().map().sprites()
            ),
            new MapReady()
        );
    }
}