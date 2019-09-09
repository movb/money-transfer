package util;

import model.Message;

import java.util.Optional;

import spark.Response;

public class ResponseWith {

    public static Message Error(Response response, String message) {
        response.status(400);
        return Message.builder().status("error").message(Optional.of(message)).build();
    }

    public static Message NotFound(Response response) {
        response.status(404);
        return Message.builder().status("error").message(Optional.of("Not found")).build();
    }

    public static Message Ok(Response response) {
        response.status(200);
        return Message.builder().status("ok").build();
    }
}
