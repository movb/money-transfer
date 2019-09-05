package rest.v1;

import dao.Storage;
import dao.Transaction;
import util.Message;
import util.LRUCache;

import spark.Request;
import spark.Response;
import com.google.gson.Gson;

public class TransferAPI {
    private Storage storage;
    private Gson gson = new Gson();
    //private LRUCache<String,Boolean> idempotencyKeysStorage = new LRUCache<String, Boolean>(1000);

    public TransferAPI(Storage storage) {
        this.storage = storage;
    }

    public Object transfer(Request request, Response response) {
        Transaction transaction = gson.fromJson(request.body(), Transaction.class);

        try {
            storage.transfer(transaction);
        } catch (Exception e) {
            return Message.Error(response, e.getMessage());
        }

        return Message.Ok(response);
    }
}