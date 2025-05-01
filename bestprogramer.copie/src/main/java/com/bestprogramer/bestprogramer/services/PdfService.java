package com.bestprogramer.bestprogramer.services;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Service
public class PdfService {

    public ByteArrayOutputStream generateCoursePdf(String courseContent) throws IOException {
        Document document = new Document();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        
        try {
            PdfWriter.getInstance(document, byteArrayOutputStream);
            document.open();

            // Configuration des polices
            Font titleFont = new Font(Font.FontFamily.TIMES_ROMAN, 22, Font.BOLD, BaseColor.DARK_GRAY);
            Font chapterFont = new Font(Font.FontFamily.TIMES_ROMAN, 16, Font.BOLD, BaseColor.BLUE);
            Font sectionFont = new Font(Font.FontFamily.TIMES_ROMAN, 14, Font.BOLD);
            Font normalFont = new Font(Font.FontFamily.TIMES_ROMAN, 12);
            
            // Titre du document
            Paragraph title = new Paragraph("COURS D'ART ORATOIRE", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20f);
            document.add(title);

            // Traitement du contenu
            String[] lines = courseContent.split("\n");
            
            for (String line : lines) {
                if (line.trim().isEmpty()) {
                    document.add(new Paragraph(" ")); // Espace vide
                    continue;
                }
                
                // Détection des titres de chapitre
                if (line.startsWith("Chapitre") || line.startsWith("# ")) {
                    Paragraph chapter = new Paragraph(line.replace("#", "").trim(), chapterFont);
                    chapter.setSpacingBefore(15f);
                    chapter.setSpacingAfter(10f);
                    document.add(chapter);
                } 
                // Détection des sections
                else if (line.startsWith("## ") || line.startsWith("Section")) {
                    Paragraph section = new Paragraph(line.replace("##", "").trim(), sectionFont);
                    section.setSpacingBefore(10f);
                    section.setSpacingAfter(5f);
                    document.add(section);
                } 
                // Contenu normal
                else {
                    Paragraph paragraph = new Paragraph(line, normalFont);
                    paragraph.setSpacingAfter(3f);
                    document.add(paragraph);
                }
            }

            // Pied de page
            Paragraph footer = new Paragraph("\n\nDocument généré le: " + new java.util.Date(), normalFont);
            footer.setAlignment(Element.ALIGN_RIGHT);
            document.add(footer);

        } catch (DocumentException e) {
            throw new IOException("Erreur lors de la génération du PDF", e);
        } finally {
            document.close();
        }
        
        return byteArrayOutputStream;
    }
}