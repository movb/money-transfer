package dao;

import lombok.*;
import java.util.Optional;

@Builder
public class Message {
    private String status;
    @Builder.Default
    private Optional<String> message = Optional.empty();
}
