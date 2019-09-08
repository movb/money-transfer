package rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import storage.Storage;
import storage.InMemoryStorage;
import rest.v1.Handler;
import static spark.Spark.port;

public class Service {
    private final Storage storage = new InMemoryStorage();
    private final Logger logger = LoggerFactory.getLogger(Service.class);

    public Service(int servicePort) {
        logger.info("Starting REST service");
        port(servicePort);
        var apiV1 = new rest.v1.Handler(storage);
    }
}