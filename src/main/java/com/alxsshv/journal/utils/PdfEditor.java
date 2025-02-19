package com.alxsshv.journal.utils;

import com.alxsshv.security.model.User;
import lombok.AllArgsConstructor;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.encryption.AccessPermission;
import org.apache.pdfbox.pdmodel.encryption.StandardProtectionPolicy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;

@Component
@AllArgsConstructor
public class PdfEditor {
    @Autowired
    private PdfStamp pdfStamp;

    public void addSignatureStamp(String sourceFilePath, String signedFilePath, User user) throws IOException {
        final File file = new File(sourceFilePath);
        final PDDocument pdd = Loader.loadPDF(file);
        final PDDocument stampedDocument = pdfStamp.setStamp(pdd, user);
        final AccessPermission ap = new AccessPermission();
        ap.setCanPrint(false);
        ap.setReadOnly();
        final StandardProtectionPolicy spp = new StandardProtectionPolicy("0000", "", ap);
        spp.setEncryptionKeyLength(128);
        spp.setPermissions(ap);
        stampedDocument.protect(spp);
        stampedDocument.save(signedFilePath);
        stampedDocument.close();
    }
}
