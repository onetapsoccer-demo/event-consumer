package com.redhat.onetapsoccer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletionStage;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.jboss.logging.Logger;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.smallrye.common.annotation.Blocking;

@Path("/events")
public class EventsResource {

    @Inject
    MeterRegistry registry;

    String[] players = { "Ansible", "OpenShift", "RHEL", "Cloud Services" };

    @Inject
    Logger logger;

    @Inject
    @Channel("events")
    Emitter<Event> eventEmitter;

    Map<String, Score> mapUserMaxScore = Collections.synchronizedMap(new HashMap<>());

    @GET
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Blocking
    public void get() {
        logger.debug("GreetingResource.get()");
    }

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Blocking
    public CompletionStage<Void> post(
            @FormParam("kind") String kind, @FormParam("player") int player,
            @FormParam("user") String user, @FormParam("score") Integer score,
            @FormParam("userName") String userName,
            MultivaluedMap<String, String> form) {

        createMetrics(kind, player, user, score, userName);

        //Send to Kafka
        Event event = new Event(kind, players[player], score, user, userName);
        return eventEmitter.send(event);
    }

    private void createMetrics(String kind, int player, String user, Integer score, String userName) {
        List<Tag> tags = initTags(kind, player, user, userName);

        // Generic counter
        registry.counter("com.redhat.onetapsoccer", tags).increment();

        // Counter per event kind
        registry.counter("com.redhat.onetapsoccer." + kind, tags).increment();

        // Counter per Game Over/Score
        if ("game_over".equals(kind)) {
            // Its working for this Demo, =D.
            // Do not use in Prodution
            gameOverMetric(user, userName, score);
        }
    }

    private List<Tag> initTags(String kind, int player, String user, String userName) {
        List<Tag> tags = new ArrayList<>();

        tags.add(Tag.of("kind", "" + kind));
        tags.add(Tag.of("user", "" + user));
        tags.add(Tag.of("userName", userName));
        tags.add(Tag.of("player", players[player]));
        return tags;
    }

    private void gameOverMetric(String user, String userName, Integer score) {
        List<Tag> tags = new ArrayList<>();
        // change user per name?
        tags.add(Tag.of("user", "" + user));
        tags.add(Tag.of("userName", userName));
        Score scoreObj = new Score(score);
        String userKey = user+userName;
        Score scoreMapped = mapUserMaxScore.get(userKey);
        if (scoreMapped == null) {
            mapUserMaxScore.put(userKey, scoreObj);
            Gauge.builder("com.redhat.onetapsoccer.score", scoreObj, (v) -> {
                return v.getValue();
            }).tags(tags).register(registry);
        } else {
            scoreMapped.setMaxValue(score);
        }

    }

}