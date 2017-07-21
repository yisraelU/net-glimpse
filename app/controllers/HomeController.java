package controllers;

import akka.actor.ActorRef;
import akka.actor.Props;
import com.fasterxml.jackson.databind.JsonNode;
import play.Logger;
import play.Logger.ALogger;
import play.mvc.Controller;
import play.mvc.LegacyWebSocket;
import play.mvc.Result;
import play.mvc.WebSocket;
import services.PcapInitializer;
import services.WebSocketActor;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

/**
 * Created by Kristian Lange on 2017.
 */
@Singleton
public class HomeController extends Controller {

    private final PcapInitializer pcapInitializer;

    @Inject
    public HomeController(PcapInitializer pcapInitializer) {
        this.pcapInitializer = pcapInitializer;
    }

    /**
     * Endpoint serves WebSockets that streams network header data
     *
     * @param nif Network interface name of the interface to be intercepted
     * @return WebSocket that streams network header data
     */
    public LegacyWebSocket<JsonNode> netdata(String nif) {
        ActorRef pcapDispatcherActorRef =
                pcapInitializer.getPcapDispatcherActorRef(nif);
        return WebSocket.withActor(out -> WebSocketActor.props(out,
                pcapDispatcherActorRef));
    }

}
