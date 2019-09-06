package rest.v1;

import dao.Account;
import dao.Storage;
import util.Message;

import java.util.Map;
import java.util.List;
import java.util.stream.Collectors;

import spark.Request;
import spark.Response;

import com.google.gson.Gson;

public class AccountsAPI {
    private Storage storage;
    private Gson gson = new Gson();

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
                return Message.Error(response, "Id empty");
            }

            if (!storage.create(account)) {
                return Message.Error(response, String.format("Account id %s already exists", account.getId()));
            }

            response.header("Location", "/api/v1/accounts/"+account.getId());
            return Message.Ok(response);
        } catch(Exception e) {
            return Message.Error(response, String.format("Error: %s", e.toString()));
        }
    }

    public Object Get(Request request, Response response) {
        String id = request.params(":id");
        Account account = storage.get(id);

        if (account == null) {
            return Message.NotFound(response);
        }

        return account;
    }

}
