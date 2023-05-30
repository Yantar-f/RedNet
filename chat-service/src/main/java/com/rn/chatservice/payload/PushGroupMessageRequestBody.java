package com.rn.chatservice.payload;

public class PushGroupMessageRequestBody {
    private final String groupId;
    private final String userId;
    private final String message;

    public PushGroupMessageRequestBody(
            String groupId,
            String userId,
            String message
    ) {
        this.groupId = groupId;
        this.userId = userId;
        this.message = message;
    }

    public String getGroupId() {
        return groupId;
    }

    public String getUserId() {
        return userId;
    }

    public String getMessage() {
        return message;
    }
}
