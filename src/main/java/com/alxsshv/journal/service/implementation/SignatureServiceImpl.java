package com.alxsshv.journal.service.implementation;

import com.alxsshv.config.PathsConfig;
import com.alxsshv.journal.dto.ProtocolDto;
import com.alxsshv.journal.model.Protocol;
import com.alxsshv.journal.service.interfaces.DigitalSignatureService;
import com.alxsshv.journal.service.interfaces.ProtocolService;
import com.alxsshv.journal.utils.PathResolver;
import com.alxsshv.journal.utils.PdfEditor;
import com.alxsshv.security.model.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import javax.naming.OperationNotSupportedException;

@Service
@AllArgsConstructor
@Getter
@Setter
public class SignatureServiceImpl implements DigitalSignatureService {
    @Autowired
    private PdfEditor pdfEditor;
    @Autowired
    private PathsConfig pathsConfig;
    @Autowired
    private ProtocolService protocolService;
    @Autowired
    private PathResolver pathResolver;
    @Autowired
    private ModelMapper mapper;

    public void setUserStamp(long id, User currentUser) throws OperationNotSupportedException, IOException {
        final Protocol protocol = protocolService.getProtocolById(id);
        if (protocol.isSigned()) {
            throw new OperationNotSupportedException("Файл уже имеет подпись поверителя. Повторное подписание не возможно");
        }
        if (!protocol.getVerificationEmployee().equals(currentUser)) {
            throw new OperationNotSupportedException("Протокол может подписать только поверитель, выполнивший поверку");
        }
        final String sourceFilePath = pathsConfig.getOriginProtocolsPath() + "/" + protocol.getStorageFileName();
        pathResolver.createFilePathIfNotExist(pathsConfig.getSignedProtocolsPath());
        final String signedFilePath = pathsConfig.getSignedProtocolsPath() + "/" + protocol.getStorageFileName();
        pdfEditor.addSignatureStamp(sourceFilePath, signedFilePath, protocol.getVerificationEmployee());
        protocol.setSigned(true);
        protocol.setAwaitingSigning(false);
        protocol.setSignedFileName(protocol.getStorageFileName());
        final ProtocolDto protocolDto = mapper.map(protocol, ProtocolDto.class);
        protocolService.update(protocolDto);
    }
}
