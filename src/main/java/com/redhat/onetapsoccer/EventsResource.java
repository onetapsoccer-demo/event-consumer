package com.redhat.onetapsoccer;

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
import io.smallrye.common.annotation.Blocking;

@Path("/events")
public class EventsResource {

    @Inject
    MeterRegistry registry;
    
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
    public void post(@FormParam("kind") String kind, MultivaluedMap<String, String> form) {
        logger.debug("-------------");
        logger.debugf("CHEGOU UM EVENTO: %s", kind);
        for (Entry<String, List<String>> entry : form.entrySet()) {
            logger.debug(entry.getKey());
            logger.debug(entry.getValue());
        }
        logger.debug("-------------");
        registry.counter("com.redhat.forum."+kind).increment();
    }
}