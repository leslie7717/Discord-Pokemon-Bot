package edu.northeastern.cs5500.starterbot;

import dagger.Component;
import edu.northeastern.cs5500.starterbot.command.CommandModule;
import edu.northeastern.cs5500.starterbot.listener.MessageListener;
import edu.northeastern.cs5500.starterbot.repository.RepositoryModule;
import edu.northeastern.cs5500.starterbot.service.OpenTelemetryService;
import edu.northeastern.cs5500.starterbot.service.ServiceModule;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.context.Scope;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;

@Component(modules = {CommandModule.class, RepositoryModule.class, ServiceModule.class})
@Singleton
interface BotComponent {
    public Bot bot();
}

@Slf4j
public class Bot {

    @Inject
    Bot() {}

    @Inject MessageListener messageListener;
    @Inject OpenTelemetryService openTelemetryService;
    @Inject JDA jda;

    static String getBotToken() {
        return new ProcessBuilder().environment().get("BOT_TOKEN");
    }

    void start() {
        var span = openTelemetryService.span("updateCommands", SpanKind.PRODUCER);
        try (Scope scope = span.makeCurrent()) {
            jda.addEventListener(messageListener);
            CommandListUpdateAction commands = jda.updateCommands();
            commands.addCommands(messageListener.allCommandData());
            commands.queue();
        } catch (Exception e) {
            log.error("Unable to add message listeners", e);
            span.recordException(e);
        } finally {
            span.end();
        }
    }
}
