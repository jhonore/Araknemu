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

package fr.quatrevieux.araknemu.game.admin.debug;

import fr.quatrevieux.araknemu.game.admin.context.Context;
import fr.quatrevieux.araknemu.game.admin.context.ContextConfigurator;
import fr.quatrevieux.araknemu.game.admin.context.ContextResolver;

import java.util.ArrayList;
import java.util.List;

/**
 * Resolver for debug context
 */
final public class DebugContextResolver implements ContextResolver {
    final private List<ContextConfigurator<DebugContext>> configurators = new ArrayList<>();

    @Override
    public Context resolve(Context globalContext, Object argument) {
        return new DebugContext(globalContext, configurators);
    }

    @Override
    public String type() {
        return "debug";
    }

    /**
     * Register a configurator for the debug context
     */
    public DebugContextResolver register(ContextConfigurator<DebugContext> configurator) {
        configurators.add(configurator);

        return this;
    }
}
