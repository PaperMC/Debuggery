/*
 * This file is part of Debuggery.
 *
 * Debuggery is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Debuggery is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Debuggery.  If not, see <http://www.gnu.org/licenses/>.
 */

package io.zachbr.debuggery.reflection.types.handlers.bukkit.output;

import io.zachbr.debuggery.reflection.types.TypeHandler;
import io.zachbr.debuggery.reflection.types.handlers.base.OutputHandler;
import org.bukkit.plugin.messaging.Messenger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MessengerOutputHandler implements OutputHandler<Messenger> {
    private final TypeHandler typeHandler;

    public MessengerOutputHandler(TypeHandler handler) {
        this.typeHandler = handler;
    }

    @Override
    public @Nullable String getFormattedOutput(Messenger messenger) {
        String incomingBanner = "-- Incoming Channels --\n";
        String incomingChannels = typeHandler.getOutputFor(messenger.getIncomingChannels()); // pass to collection handling
        String outgoingBanner = "\n-- Outgoing Channels --\n";
        String outgoingChannels = typeHandler.getOutputFor(messenger.getOutgoingChannels()); // pass to collection handling

        return incomingBanner + incomingChannels + outgoingBanner + outgoingChannels;
    }

    @Override
    public @NotNull Class<Messenger> getRelevantClass() {
        return Messenger.class;
    }
}
