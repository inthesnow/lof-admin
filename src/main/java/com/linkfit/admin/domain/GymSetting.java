package com.linkfit.admin.domain;

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
    public boolean isOpen() { return isOpen; }
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
