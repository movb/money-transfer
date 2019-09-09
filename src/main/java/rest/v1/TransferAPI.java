package rest.v1;

import model.Transaction;
import storage.Storage;
import util.ResponseWith;
import util.Validators;

import spark.Request;
import spark.Response;
import com.google.gson.Gson;

public class TransferAPI {
    private final Storage storage;
    private final Gson gson = new Gson();

    public TransferAPI(Storage storage) {
        this.storage = storage;
    }

    public Object transfer(Request request, Response response) {
        try {
            Transaction transaction = gson.fromJson(request.body(), Transaction.class);
            Validators.validateTransaction(transaction);

            storage.transfer(transaction);
        } catch (Exception e) {
            return ResponseWith.Error(response, e.getMessage());
        }

        return ResponseWith.Ok(response);
    }
}