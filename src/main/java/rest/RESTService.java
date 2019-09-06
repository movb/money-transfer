package rest;

import dao.Storage;
import dao.InMemoryStorage;
import rest.v1.AccountsAPI;
import rest.v1.TransferAPI;

import static spark.Spark.get;
import static spark.Spark.post;
import static spark.Spark.delete;
import static spark.Spark.path;
import static spark.Spark.before;

import com.google.gson.Gson;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RESTService {
    Storage storage = new InMemoryStorage();
    private Gson gson = new Gson();

    Logger logger = LoggerFactory.getLogger(RESTService.class);

    public RESTService() {
        AccountsAPI accountsApi = new rest.v1.AccountsAPI(storage);
        path("/api/v1/", () -> {
            before((req, res) -> {
                res.type("application/json");
            });
            get("/accounts", accountsApi::List, gson::toJson);
            get("/accounts/:id", accountsApi::Get, gson::toJson);
            post("/accounts", accountsApi::Create, gson::toJson);
        });

        TransferAPI transfersApi = new rest.v1.TransferAPI(storage);
        path("/api/v1/", () -> {
            before((req, res) -> {
                res.type("application/json");
            });
            post("/transfer", transfersApi::transfer, gson::toJson);
        });
    }
}