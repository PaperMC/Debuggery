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

package io.zachbr.debuggery;

import com.google.inject.Inject;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import io.zachbr.debuggery.commands.ProxyPlayerCommand;
import io.zachbr.debuggery.commands.ProxyServerCommand;
import io.zachbr.debuggery.commands.ServerConnectionCommand;
import io.zachbr.debuggery.commands.base.CommandBase;

@Plugin(id = "debuggery",
        name = "Debuggery",
        version = DebuggeryBase.VERSION,
        description = "A small plugin designed to expose API values at runtime.",
        authors = {"Z750", "kennytv"},
        url = "https://github.com/PaperMC/Debuggery")
public class DebuggeryVelocity extends DebuggeryBase {
    private final ProxyServer server;

    @Inject
    DebuggeryVelocity(ProxyServer server, org.slf4j.Logger logger) {
        super(new VelocityLogger(logger));
        this.server = server;

        registerCommand(new ProxyPlayerCommand(this));
        registerCommand(new ProxyServerCommand(this));
        registerCommand(new ServerConnectionCommand(this));
    }

    @Override
    String getPluginVersion() {
        return DebuggeryBase.VERSION;
    }

    @Override
    String getPlatformName() {
        return server.getVersion().getName();
    }

    @Override
    String getPlatformVersion() {
        return server.getVersion().getVersion();
    }

    public ProxyServer getProxyServer() {
        return this.server;
    }

    private void registerCommand(CommandBase base) {
        this.server.getCommandManager().register(base.getName(), base);
    }
}
