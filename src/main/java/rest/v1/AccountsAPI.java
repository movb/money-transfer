package rest.v1;

import dao.Account;
import storage.Storage;
import util.ResponseWith;

import spark.Request;
import spark.Response;

import com.google.gson.Gson;

public class AccountsAPI {
    private final Storage storage;
    private final Gson gson = new Gson();

    public AccountsAPI(Storage storage) {
        this.storage = storage;
    }

    public Object List(Request request, Response response) {
        return storage.getAll();
    }

    public Object Create(Request request, Response response) {
        try {
            Account account = gson.fromJson(request.body(), Account.class);

            if (account.getId() == null || account.getId().isEmpty()) {
                return ResponseWith.Error(response, "Id empty");
            }

            if (!storage.create(account)) {
                return ResponseWith.Error(response, String.format("Account id %s already exists", account.getId()));
            }

            response.header("Location", "/api/v1/accounts/"+account.getId());
            return ResponseWith.Ok(response);
        } catch(Exception e) {
            return ResponseWith.Error(response, String.format("Error: %s", e.toString()));
        }
    }

    public Object Get(Request request, Response response) {
        String id = request.params(":id");
        Account account = storage.get(id);

        if (account == null) {
            return ResponseWith.NotFound(response);
        }

        return account;
    }

}
