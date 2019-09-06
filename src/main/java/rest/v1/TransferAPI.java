package rest.v1;

import storage.Storage;
import dao.Transaction;
import util.ResponseWith;

import spark.Request;
import spark.Response;
import com.google.gson.Gson;
import util.Validators;

public class TransferAPI {
    private Storage storage;
    private Gson gson = new Gson();

    public TransferAPI(Storage storage) {
        this.storage = storage;
    }

    public Object transfer(Request request, Response response) {
        Transaction transaction = gson.fromJson(request.body(), Transaction.class);
        Validators.validateTransaction(transaction);

        try {
            storage.transfer(transaction);
        } catch (Exception e) {
            return ResponseWith.Error(response, e.getMessage());
        }

        return ResponseWith.Ok(response);
    }
}