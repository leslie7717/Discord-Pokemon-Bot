package edu.northeastern.cs5500.starterbot.listener;

import edu.northeastern.cs5500.starterbot.command.ButtonHandler;
import edu.northeastern.cs5500.starterbot.command.SlashCommandHandler;
import edu.northeastern.cs5500.starterbot.command.StringSelectHandler;
import edu.northeastern.cs5500.starterbot.exception.ButtonNotFoundException;
import edu.northeastern.cs5500.starterbot.exception.CommandNotFoundException;
import edu.northeastern.cs5500.starterbot.exception.StringSelectNotFoundException;
import edu.northeastern.cs5500.starterbot.service.OpenTelemetryService;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.StatusCode;
import io.opentelemetry.context.Scope;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Provider;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

@Slf4j
public class MessageListener extends ListenerAdapter {

    @Inject Map<String, Provider<SlashCommandHandler>> commands;
    @Inject Map<String, Provider<ButtonHandler>> buttons;
    @Inject Map<String, Provider<StringSelectHandler>> stringSelects;

    @Inject OpenTelemetryService openTelemetryService;

    @Inject
    public MessageListener() {
        super();
    }

    @Override
    public void onSlashCommandInteraction(@Nonnull SlashCommandInteractionEvent event) {
        var name = event.getName();
        Span span = openTelemetryService.span(name);

        try (Scope scope = span.makeCurrent()) {
            for (Entry<String, Provider<SlashCommandHandler>> entry : commands.entrySet()) {
                if (entry.getKey().equals(name)) {
                    entry.getValue().get().onSlashCommandInteraction(event);
                    span.end();
                    return;
                }
            }

            throw new CommandNotFoundException(name);
        } catch (Exception e) {
            span.setStatus(StatusCode.ERROR);
            span.recordException(e);
            log.error("onSlashCommandInteraction failed", e);
        } finally {
            span.end();
        }
    }

    public @Nonnull Collection<CommandData> allCommandData() {
        Collection<CommandData> commandData =
                commands.values().stream()
                        .map(Provider<SlashCommandHandler>::get)
                        .map(SlashCommandHandler::getCommandData)
                        .collect(Collectors.toList());
        if (commandData == null) {
            return new ArrayList<>();
        }
        return commandData;
    }

    @Override
    public void onButtonInteraction(@Nonnull ButtonInteractionEvent event) {
        log.info("onButtonInteraction: {}", event.getButton().getId());
        String id = event.getButton().getId();
        Objects.requireNonNull(id);
        String handlerName = id.split(":", 2)[0];

        Span span = openTelemetryService.span(handlerName);

        try (Scope scope = span.makeCurrent()) {
            for (Entry<String, Provider<ButtonHandler>> entry : buttons.entrySet()) {
                if (entry.getKey().equals(handlerName)) {
                    entry.getValue().get().onButtonInteraction(event);
                    span.end();
                    return;
                }
            }

            throw new ButtonNotFoundException(handlerName);
        } catch (Exception e) {
            span.setStatus(StatusCode.ERROR);
            span.recordException(e);
            log.error("onButtonInteraction failed", e);
        } finally {
            span.end();
        }
    }

    @Override
    public void onStringSelectInteraction(@Nonnull StringSelectInteractionEvent event) {
        log.info("onStringSelectInteraction: {}", event.getComponent().getId());
        String handlerName = event.getComponent().getId();

        Span span = openTelemetryService.span(handlerName);

        try (Scope scope = span.makeCurrent()) {
            for (Entry<String, Provider<StringSelectHandler>> entry : stringSelects.entrySet()) {
                if (entry.getKey().equals(handlerName)) {
                    entry.getValue().get().onStringSelectInteraction(event);
                    return;
                }
            }

            throw new StringSelectNotFoundException(handlerName);
        } catch (Exception e) {
            span.setStatus(StatusCode.ERROR);
            span.recordException(e);
            log.error("onStringSelectInteraction failed", e);
        } finally {
            span.end();
        }
    }
}
