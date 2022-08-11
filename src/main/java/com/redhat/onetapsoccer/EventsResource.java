package com.redhat.onetapsoccer;

import java.util.List;
import java.util.Map.Entry;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
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
    public void post(@FormParam("kind") String kind, MultivaluedMap<String, String> form,
            @Context HttpHeaders headers) {
        logger.debug("-------------");
        logger.debugf("CHEGOU UM EVENTO: %s", kind);
        for (Entry<String, List<String>> entry : form.entrySet()) {
            logger.debug(entry.getKey());
            logger.debug(entry.getValue());
        }
        
        // sec-ch-ua-platform
        // One of the following strings:
        // "Android", "Chrome OS", "Chromium OS", "iOS", "Linux", "macOS", "Windows", or
        // "Unknown". or NULL!!!
        // "Unknown". or NULL!!!logger.debug("---Headers");
        // for (Entry<String, List<String>> entry : headers.getRequestHeaders().entrySet()) {
        //     logger.debug(entry.getKey());
        //     logger.debug(entry.getValue());
        // }
        String userAgent = getUserAgent(headers);
        String os = getOS(userAgent);
        String browser = getBrowser(userAgent);
        logger.debugf("OS: %s", os);
        logger.debugf("BROWSER: %s", browser);

        registry.counter("com.redhat.forum." + kind).increment();
    }

    private String getUserAgent(HttpHeaders headers) {
        List<String> values = headers.getRequestHeader("User-Agent");
        if (values == null || values.isEmpty()) {
            return "DUMMY USER AGENT";
        }
        return values.get(0).toLowerCase();
    }

    private String getOS(String browserDetails) {
        String userAgent = browserDetails;

        try {
            if (userAgent.indexOf("windows") >= 0) {
                return "Windows";
            } else if (userAgent.indexOf("mac") >= 0) {
                return "Mac";
            } else if (userAgent.indexOf("x11") >= 0) {
                return "Unix";
            } else if (userAgent.indexOf("android") >= 0) {
                return "Android";
            } else if (userAgent.indexOf("iphone") >= 0) {
                return "IPhone";
            } else {
                return "UnKnown";
            }
        }catch(Exception e) {
            return "UnKnown";
        }
        
    }

    private String getBrowser(String browserDetails) {
        String userAgent = browserDetails;
        String user = userAgent;

        try {
            if (user.contains("msie")) {
                return "IE";
            } else if (user.contains("safari") && user.contains("version")) {
                return "Safari";
            } else if (user.contains("opr") || user.contains("opera")) {
                return "Opera";
            } else if (user.contains("chrome")) {
                return "Chrome";
            } else if ((user.indexOf("mozilla/7.0") > -1) || (user.indexOf("netscape6") != -1)
                    || (user.indexOf("mozilla/4.7") != -1) || (user.indexOf("mozilla/4.78") != -1)
                    || (user.indexOf("mozilla/4.08") != -1) || (user.indexOf("mozilla/3") != -1)) {
                return "Netscape";
            } else if (user.contains("firefox")) {
                return "Firefox";
            } else if (user.contains("rv")) {
                return "IE";
            } else {
                return "UnKnown";
            }
        }catch(Exception e) {
            return "UnKnown";
        }
        
    }
}