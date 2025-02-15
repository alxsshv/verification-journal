package com.alxsshv.journal.utils;

import com.alxsshv.security.model.User;
import lombok.AllArgsConstructor;
import org.apache.fontbox.ttf.TrueTypeFont;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.encryption.AccessPermission;
import org.apache.pdfbox.pdmodel.encryption.StandardProtectionPolicy;
import org.apache.pdfbox.pdmodel.font.*;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.apache.pdfbox.pdmodel.font.Standard14Fonts.FontName.COURIER;
import static org.apache.pdfbox.pdmodel.font.Standard14Fonts.FontName.HELVETICA;


@Component
@AllArgsConstructor
public class PDFEditor {
    @Autowired
    private StringTransliterator transliterator;
    private static final String FONT_PATH = "src/main/resources/static/fonts/GentiumPlus.ttf";

    public void addSignatureStamp(String sourceFilePath, String signedFilePath, User user) throws IOException {
        PDDocument stampedDocument = new PDDocument();
        System.out.println("Загружаем файл");
        File file = new File(sourceFilePath);
        PDDocument pdd = Loader.loadPDF(file);
        PDFRenderer pdfRenderer = new PDFRenderer(pdd);
        int pageCount = pdd.getPages().getCount();
        for (int i = 0; i < pageCount; i++){
            addStampToPage(pdd, i, user);
            PDPage page = pdd.getPage(i);
            PDRectangle rectangle = page.getMediaBox();
            BufferedImage bim = pdfRenderer.renderImage(i,2);
            addImageToNewPage(stampedDocument,rectangle,bim,i);
        }
        AccessPermission ap = new AccessPermission();
        ap.setCanPrint(false);
        ap.setReadOnly();
        StandardProtectionPolicy spp = new StandardProtectionPolicy("0000","",ap);
        spp.setEncryptionKeyLength(128);
        spp.setPermissions(ap);
        stampedDocument.protect(spp);
        stampedDocument.save(signedFilePath);
        stampedDocument.close();
    }

    private PDDocument addStampToPage(PDDocument document, int index, User user) throws IOException {
        PDPage page = document.getPage(index);
        PDFont pdfFont = new PDType1Font(COURIER);
        PDPageContentStream contentStream = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.APPEND, true, true);
        contentStream.beginText();
        contentStream.newLineAtOffset(15,35);
        contentStream.setFont(pdfFont, 12);
        String latinStringEmployeeSignature =  transliterator.cyrilicToLatin(user.getSurname() + " " + user.getName() + " " + user.getPatronymic());
        contentStream.showText("DOCUMENT VERIFIED BY " + latinStringEmployeeSignature);
        contentStream.endText();
        contentStream.close();
        return document;
    }

    private PDDocument addImageToNewPage(PDDocument document, PDRectangle rectangle, BufferedImage image, int index) throws IOException {
        PDPage page = new PDPage(new PDRectangle(rectangle.getWidth()*2, rectangle.getHeight()*2));
        document.addPage(page);
        PDPageContentStream contentStream = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.APPEND, false, true);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ImageIO.write(image, "bmp",os);
        PDImageXObject pdImage = PDImageXObject.createFromByteArray(document, os.toByteArray(), String.valueOf(index));
        contentStream.drawImage(pdImage,PDRectangle.A4.getLowerLeftX(),PDRectangle.A4.getLowerLeftY());
        contentStream.close();
        return document;
    }

    private String encode(String string){
        byte[] stringBytes = string.getBytes();
        String asciiEncodedString = new String(stringBytes, StandardCharsets.UTF_8);
        return asciiEncodedString;
    }

}
