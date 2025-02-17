package com.alxsshv.journal.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProtocolFileInfo {
    private String description;
    private String number;
    private String verificationDate;
    private long journalId;
    private long verificationEmployeeId;
    private String miModification;
    private String miSerialNum;


    @Override
    public String toString() {
        return "ProtocolFileInfo{" +
                "description='" + description + '\'' +
                ", number='" + number + '\'' +
                ", verificationDate='" + verificationDate + '\'' +
                ", journalId=" + journalId +
                ", verificationEmployeeId=" + verificationEmployeeId +
                ", miModel='" + miModification + '\'' +
                ", miSerialNum='" + miSerialNum + '\'' +
                '}';
    }
}
