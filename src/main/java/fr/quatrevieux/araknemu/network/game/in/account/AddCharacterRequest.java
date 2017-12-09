package fr.quatrevieux.araknemu.network.game.in.account;

import fr.quatrevieux.araknemu.data.constant.Race;
import fr.quatrevieux.araknemu.data.constant.Sex;
import fr.quatrevieux.araknemu.data.value.Colors;
import fr.quatrevieux.araknemu.network.in.Packet;
import fr.quatrevieux.araknemu.network.in.ParsePacketException;
import fr.quatrevieux.araknemu.network.in.SinglePacketParser;
import org.apache.commons.lang3.StringUtils;

/**
 * Request for create a new character
 *
 * https://github.com/Emudofus/Dofus/blob/1.29/dofus/aks/Account.as#L90
 */
final public class AddCharacterRequest implements Packet {
    final static public class Parser implements SinglePacketParser<AddCharacterRequest> {
        @Override
        public AddCharacterRequest parse(String input) throws ParsePacketException {
            String[] data = StringUtils.split(input, "|", 6);

            if (data.length != 6) {
                throw new ParsePacketException(code() + input, "Invalid data : required 6 parts");
            }

            return new AddCharacterRequest(
                data[0],
                Race.byId(Integer.parseInt(data[1])),
                Sex.parse(data[2]),
                new Colors(
                    Integer.parseInt(data[3]),
                    Integer.parseInt(data[4]),
                    Integer.parseInt(data[5])
                )
            );
        }

        @Override
        public String code() {
            return "AA";
        }
    }

    final private String name;
    final private Race race;
    final private Sex sex;
    final private Colors colors;

    public AddCharacterRequest(String name, Race race, Sex sex, Colors colors) {
        this.name = name;
        this.race = race;
        this.sex = sex;
        this.colors = colors;
    }

    public String name() {
        return name;
    }

    public Race race() {
        return race;
    }

    public Sex sex() {
        return sex;
    }

    public Colors colors() {
        return colors;
    }
}
