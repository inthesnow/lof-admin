package com.linkfit.admin.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

public class GymSetting {
    private int id = 1;
    private String gymName;
    private String gymPhone;
    private String gymAddress;
    private boolean isOpen;
    private String monOpen;  private String monClose;  private boolean monClosed;
    private String tueOpen;  private String tueClose;  private boolean tueClosed;
    private String wedOpen;  private String wedClose;  private boolean wedClosed;
    private String thuOpen;  private String thuClose;  private boolean thuClosed;
    private String friOpen;  private String friClose;  private boolean friClosed;
    private String satOpen;  private String satClose;  private boolean satClosed;
    private String sunOpen;  private String sunClose;  private boolean sunClosed;
    private String notice;
    private LocalDateTime updatedAt;

    public GymSetting() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getGymName() { return gymName; }
    public void setGymName(String gymName) { this.gymName = gymName; }
    public String getGymPhone() { return gymPhone; }
    public void setGymPhone(String gymPhone) { this.gymPhone = gymPhone; }
    public String getGymAddress() { return gymAddress; }
    public void setGymAddress(String gymAddress) { this.gymAddress = gymAddress; }
    // Jackson 기본 빈 네이밍 규칙상 isOpen()/setOpen()은 프로퍼티명이 "open"으로 인식되어
    // 프론트/다른 엔드포인트가 쓰는 JSON 키 "isOpen"과 어긋났다(그 결과 PUT /api/settings/gym
    // 저장 시 isOpen 값이 바인딩되지 않고 기본값 false로 덮어써져, "영업중"으로 바꿔도
    // 다른 설정을 저장하면 다시 "휴무"로 강제 복귀하는 버그로 이어졌음). JSON 키를 "isOpen"으로 고정.
    @JsonProperty("isOpen")
    public boolean isOpen() { return isOpen; }
    @JsonProperty("isOpen")
    public void setOpen(boolean open) { isOpen = open; }
    public String getMonOpen() { return monOpen; }
    public void setMonOpen(String v) { monOpen = v; }
    public String getMonClose() { return monClose; }
    public void setMonClose(String v) { monClose = v; }
    public boolean isMonClosed() { return monClosed; }
    public void setMonClosed(boolean v) { monClosed = v; }
    public String getTueOpen() { return tueOpen; }
    public void setTueOpen(String v) { tueOpen = v; }
    public String getTueClose() { return tueClose; }
    public void setTueClose(String v) { tueClose = v; }
    public boolean isTueClosed() { return tueClosed; }
    public void setTueClosed(boolean v) { tueClosed = v; }
    public String getWedOpen() { return wedOpen; }
    public void setWedOpen(String v) { wedOpen = v; }
    public String getWedClose() { return wedClose; }
    public void setWedClose(String v) { wedClose = v; }
    public boolean isWedClosed() { return wedClosed; }
    public void setWedClosed(boolean v) { wedClosed = v; }
    public String getThuOpen() { return thuOpen; }
    public void setThuOpen(String v) { thuOpen = v; }
    public String getThuClose() { return thuClose; }
    public void setThuClose(String v) { thuClose = v; }
    public boolean isThuClosed() { return thuClosed; }
    public void setThuClosed(boolean v) { thuClosed = v; }
    public String getFriOpen() { return friOpen; }
    public void setFriOpen(String v) { friOpen = v; }
    public String getFriClose() { return friClose; }
    public void setFriClose(String v) { friClose = v; }
    public boolean isFriClosed() { return friClosed; }
    public void setFriClosed(boolean v) { friClosed = v; }
    public String getSatOpen() { return satOpen; }
    public void setSatOpen(String v) { satOpen = v; }
    public String getSatClose() { return satClose; }
    public void setSatClose(String v) { satClose = v; }
    public boolean isSatClosed() { return satClosed; }
    public void setSatClosed(boolean v) { satClosed = v; }
    public String getSunOpen() { return sunOpen; }
    public void setSunOpen(String v) { sunOpen = v; }
    public String getSunClose() { return sunClose; }
    public void setSunClose(String v) { sunClose = v; }
    public boolean isSunClosed() { return sunClosed; }
    public void setSunClosed(boolean v) { sunClosed = v; }
    public String getNotice() { return notice; }
    public void setNotice(String notice) { this.notice = notice; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
