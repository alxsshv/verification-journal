package com.alxsshv.journal.dto.mapper;

import com.alxsshv.journal.dto.ProtocolDto;
import com.alxsshv.journal.dto.ProtocolFileInfo;
import com.alxsshv.security.dto.UserNoPassDto;

public class ProtocolFileInfoToProtocolDtoMapper {

    public static ProtocolDto mapToProtocolDto(ProtocolFileInfo protocolFileInfo, UserNoPassDto userDto) {
        final ProtocolDto protocolDto = new ProtocolDto();
        protocolDto.setVerificationEmployee(userDto);
        protocolDto.setNumber(protocolFileInfo.getNumber());
        protocolDto.setDescription(protocolFileInfo.getDescription());
        protocolDto.setMiModification(protocolFileInfo.getMiModification());
        protocolDto.setMiSerialNum(protocolFileInfo.getMiSerialNum());
        final String verificationDate = protocolFileInfo.getVerificationDate().split("T")[0];
        protocolDto.setVerificationDate(verificationDate);
        return protocolDto;
    }
}
