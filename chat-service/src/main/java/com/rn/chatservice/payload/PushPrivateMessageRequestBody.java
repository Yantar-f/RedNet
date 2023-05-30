package com.rn.chatservice.payload;

public class PushPrivateMessageRequestBody {
    private final String userFromId;
    private final String userToId;
    private final String message;

    public PushPrivateMessageRequestBody(
            String userFromId,
            String userToId,
            String message
    ) {
        this.userFromId = userFromId;
        this.userToId = userToId;
        this.message = message;
    }

    public String getUserFromId() {
        return userFromId;
    }

    public String getUserToId() {
        return userToId;
    }

    public String getMessage() {
        return message;
    }
}
