package com.redhat.onetapsoccer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

import org.jboss.logging.Logger;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.smallrye.common.annotation.Blocking;

@Path("/events")
public class EventsResource {

    @Inject
    MeterRegistry registry;

    String[] players = { "Ronaldo", "Suarez", "João", "Ibrahimovic" };

    @Inject
    Logger logger;

    @GET
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Blocking
    public void get() {
        logger.debug("GreetingResource.get()");
    }

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Blocking
    public void post(@FormParam("kind") String kind, @FormParam("player") int player,
            MultivaluedMap<String, String> form) {
        logger.debug("-------------");
        logger.debugf("CHEGOU UM EVENTO: %s", kind);
        List<Tag> tags = new ArrayList<>();
        for (Entry<String, List<String>> entry : form.entrySet()) {
            logger.debug(entry.getKey());
            logger.debug(entry.getValue());
            tags.add(Tag.of(entry.getKey(), entry.getValue().get(0)));
        }

        tags.add(Tag.of("player", players[player]));

        registry.counter("com.redhat.onetapsoccer", tags).increment();

        registry.counter("com.redhat.onetapsoccer." + kind).increment();
    }

}