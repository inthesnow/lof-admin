package com.linkfit.admin.domain;

public class CrmAnnouncement {
    private String id;
    private Long gymId;
    private String authorId;
    private String authorName;
    private String target;
    private String targetIds;
    private String title;
    private String content;
    private boolean sendPush;
    private String sentAt;
    private String createdAt;

    public CrmAnnouncement() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public Long getGymId() { return gymId; }
    public void setGymId(Long gymId) { this.gymId = gymId; }
    public String getAuthorId() { return authorId; }
    public void setAuthorId(String authorId) { this.authorId = authorId; }
    public String getAuthorName() { return authorName; }
    public void setAuthorName(String authorName) { this.authorName = authorName; }
    public String getTarget() { return target; }
    public void setTarget(String target) { this.target = target; }
    public String getTargetIds() { return targetIds; }
    public void setTargetIds(String targetIds) { this.targetIds = targetIds; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public boolean isSendPush() { return sendPush; }
    public void setSendPush(boolean sendPush) { this.sendPush = sendPush; }
    public String getSentAt() { return sentAt; }
    public void setSentAt(String sentAt) { this.sentAt = sentAt; }
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}
