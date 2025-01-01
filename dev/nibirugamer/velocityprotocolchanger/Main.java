package dev.nibirugamer.velocityprotocolchanger;

import com.google.inject.Inject;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyPingEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.server.ServerPing;
import org.slf4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

@Plugin(
        id = "velocityprotocolchanger",
        name = "VelocityProtocolChanger",
        version = "1.0-SNAPSHOT",
        description = "Change protocol version, simple as that.",
        url = "https://github.com/NibiruGamer/VelocityProtocolChanger",
        authors = {"NibiruGamer"}
)
public class Main {
    private final Logger logger;
    private final Path dataDir;
    private String protocol;
    @Inject
    public Main(@DataDirectory Path dataDir, Logger logger){
        this.dataDir = dataDir;
        this.logger = logger;
    }
    @Subscribe
    public void onProxyInitialize(ProxyInitializeEvent event) {
        try {
            loadConfiguration();
        } catch (IOException e) {
            logger.error("Failed to load configuration", e);
        }
    }



    private void loadConfiguration() throws IOException {
        if (!Files.exists(dataDir)) {
            Files.createDirectories(dataDir);
        }

        Path configFile = dataDir.resolve("config.properties");

        Properties properties = new Properties();

        if (Files.notExists(configFile)) {
            properties.setProperty("protocol", "Velocity Server");
            Files.write(configFile, propertiesToString(properties).getBytes());
        } else {
            properties.load(Files.newInputStream(configFile));
        }

        protocol = properties.getProperty("protocol", "Velocity Server");
    }

    private String propertiesToString(Properties properties) {
        StringBuilder builder = new StringBuilder();
        properties.forEach((key, value) -> builder.append(key).append("=").append(value).append("\n"));
        return builder.toString();
    }

    @Subscribe
    public void onProxyPing(ProxyPingEvent event) {
        ServerPing ping = event.getPing();

        if (ping == null) {
            return;
        }

        ServerPing.Version version = new ServerPing.Version(ping.getVersion().getProtocol(), protocol);
        ServerPing updatedPing = ping.asBuilder().version(version).build();
        event.setPing(updatedPing);
    }

}
