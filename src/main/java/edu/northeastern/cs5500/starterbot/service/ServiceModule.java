package edu.northeastern.cs5500.starterbot.service;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.context.Scope;
import java.util.Collection;
import java.util.EnumSet;
import javax.annotation.Nonnull;
import javax.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;

@Module
@Slf4j
public abstract class ServiceModule { // NOSONAR
    @Binds
    abstract OpenTelemetry bindOpenTelemetry(OpenTelemetryService service);

    static String getBotToken() {
        return new ProcessBuilder().environment().get("BOT_TOKEN");
    }

    @Provides
    @Singleton
    static JDA provideJDA(OpenTelemetryService openTelemetryService) {
        var span = openTelemetryService.span("start", SpanKind.PRODUCER);
        try (Scope scope = span.makeCurrent()) {
            String token = getBotToken();
            if (token == null) {
                throw new IllegalArgumentException(
                        "The BOT_TOKEN environment variable is not defined.");
            }
            @SuppressWarnings("null")
            @Nonnull
            Collection<GatewayIntent> intents = EnumSet.noneOf(GatewayIntent.class);
            return JDABuilder.createLight(token, intents).build();

        } catch (Exception e) {
            log.error("Unable to start the bot", e);
            span.recordException(e);
        } finally {
            span.end();
        }
        return null;
    }
}
