package edu.northeastern.cs5500.starterbot.service;

import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.api.trace.Tracer;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.Getter;

@Singleton
public class OpenTelemetryService implements OpenTelemetry {

    @Getter Tracer tracer;

    @Inject
    public OpenTelemetryService() {
        tracer = GlobalOpenTelemetry.getTracer("cs5500-s23-starterbot");
    }

    public Span span(String name) {
        return span(name, SpanKind.SERVER);
    }

    public Span span(String name, SpanKind kind) {
        return getTracer().spanBuilder(name).setSpanKind(kind).startSpan();
    }
}
