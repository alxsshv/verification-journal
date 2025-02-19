package com.alxsshv.journal.utils;

import com.alxsshv.security.model.User;
import lombok.AllArgsConstructor;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import javax.imageio.ImageIO;

import static org.apache.pdfbox.pdmodel.font.Standard14Fonts.FontName.COURIER;

@Component
@AllArgsConstructor
public class PdfStamp {
    @Autowired
    private StringTransliterator transliterator;

    public PDDocument setStamp(PDDocument pdd, User user) throws IOException {
        final PDDocument stampedDocument = new PDDocument();
        final PDFRenderer pdfRenderer = new PDFRenderer(pdd);
        final int pageCount = pdd.getPages().getCount();
        for (int i = 0; i < pageCount; i++) {
            setStampToPage(pdd, i, user);
            final PDPage page = pdd.getPage(i);
            final PDRectangle rectangle = page.getMediaBox();
            final BufferedImage bim = pdfRenderer.renderImage(i, 2);
            addImageToNewPage(stampedDocument, rectangle, bim, i);
        }
        return stampedDocument;
    }

    private void setStampToPage(PDDocument document, int pageIndex, User user) throws IOException {
        final PDPage page = document.getPage(pageIndex);
        final PDFont pdfFont = new PDType1Font(COURIER);
        final PDPageContentStream contentStream = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.APPEND, true, true);
        contentStream.beginText();
        contentStream.newLineAtOffset(15, 35);
        contentStream.setFont(pdfFont, 12);
        final String latinStringEmployeeSignature = transliterator.cyrilicToLatin(user.getSurname() + " " + user.getName() + " " + user.getPatronymic());
        contentStream.showText("DOCUMENT VERIFIED BY " + latinStringEmployeeSignature);
        contentStream.endText();
        contentStream.close();
    }

    private void addImageToNewPage(PDDocument document, PDRectangle rectangle, BufferedImage image, int index) throws IOException {
        final PDPage page = new PDPage(new PDRectangle(rectangle.getWidth() * 2, rectangle.getHeight() * 2));
        document.addPage(page);
        final PDPageContentStream contentStream = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.APPEND, false, true);
        final ByteArrayOutputStream os = new ByteArrayOutputStream();
        ImageIO.write(image, "bmp", os);
        final PDImageXObject pdImage = PDImageXObject.createFromByteArray(document, os.toByteArray(), String.valueOf(index));
        contentStream.drawImage(pdImage, PDRectangle.A4.getLowerLeftX(), PDRectangle.A4.getLowerLeftY());
        contentStream.close();
    }
}
