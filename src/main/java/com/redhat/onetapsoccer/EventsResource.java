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

    String[] players = { "Ronaldo", "Suarez", "Lewandowski", "Ibrahimovic" };

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

    /*
     * player = posicao do jogador selecionado: 0, 1, 2, 3
     * user = UUID gerado para o usuário que irá jogar. Ele terá o mesmo valor para
     * partidas diferentes
     */
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Blocking
    public CompletionStage<Void> post(
            @FormParam("kind") String kind, @FormParam("player") int player,
            @FormParam("user") String user, @FormParam("score") Integer score,
            MultivaluedMap<String, String> form) {

        logger.debug("-------------");
        logger.debugf("CHEGOU UM EVENTO: %s", kind);
        List<Tag> tags = new ArrayList<>();

        // for (Entry<String, List<String>> entry : form.entrySet()) {
        // logger.debug(entry.getKey());
        // logger.debug(entry.getValue());
        // tags.add(Tag.of(entry.getKey(), entry.getValue().get(0)));
        // }

        tags.add(Tag.of("kind", "" + kind));
        tags.add(Tag.of("user", "" + user));
        tags.add(Tag.of("player", players[player]));
        
        // tags.add(Tag.of("score", "" + score));

        logger.debugf("TAGS: %s", tags);

        // Generic counter
        registry.counter("com.redhat.onetapsoccer", tags).increment();

        // Counter per kind
        registry.counter("com.redhat.onetapsoccer." + kind, tags).increment();

        // Counter per Game Over/Score
        if ("game_over".equals(kind)) {
            // Its working for this Demo, =D. 
            // Do not use in Prodution
            gameOverMetric(user, score);
        }

        Event event = new Event(kind, players[player], score, user);
        return eventEmitter.send(event);
    }

    private void gameOverMetric(String user, Integer score) {
        List<Tag> tags = new ArrayList<>();
        // change user per name?
        tags.add(Tag.of("user", "" + user));
        Score scoreObj = new Score(score);
        Score scoreMapped = mapUserMaxScore.get(user);
        if(scoreMapped == null) {
            mapUserMaxScore.put(user, scoreObj);
            Gauge.builder("com.redhat.onetapsoccer.score", scoreObj, (v) -> {
                return v.getValue();
            }).tags(tags).register(registry);
        }else {
            scoreMapped.setMaxValue(score);
        }
        

    }

}