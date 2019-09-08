package rest.v1;

import rest.ApiHandler;
import storage.Storage;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static spark.Spark.*;
import static spark.Spark.post;

public class Handler implements ApiHandler {
    private final Gson gson = new Gson();
    private final Logger logger = LoggerFactory.getLogger(ApiHandler.class);

    public Handler(Storage storage) {
        logger.info("Create accounts API v1");
        AccountsAPI accountsApi = new rest.v1.AccountsAPI(storage);
        path("/api/v1/", () -> {
            before((req, res) -> {
                res.type("application/json");
            });
            get("/accounts", accountsApi::List, gson::toJson);
            get("/accounts/:id", accountsApi::Get, gson::toJson);
            post("/accounts", accountsApi::Create, gson::toJson);
        });

        logger.info("Create transfers API v1");
        TransferAPI transfersApi = new rest.v1.TransferAPI(storage);
        path("/api/v1/", () -> {
            before((req, res) -> {
                res.type("application/json");
            });
            post("/transfer", transfersApi::transfer, gson::toJson);
        });
    }
}
