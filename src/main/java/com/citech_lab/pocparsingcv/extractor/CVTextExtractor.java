package com.citech_lab.pocparsingcv.extractor;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFShape;
import org.apache.poi.xslf.usermodel.XSLFSlide;
import org.apache.poi.xslf.usermodel.XSLFTextShape;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

import org.odftoolkit.odfdom.doc.OdfTextDocument;
import org.odftoolkit.odfdom.incubator.doc.text.OdfEditableTextExtractor;
import org.springframework.stereotype.Component;

import java.io.*;

@Component
public class CVTextExtractor {


    public String extractTextFromPDF(InputStream inputStream) throws IOException {
        try (PDDocument document = PDDocument.load(inputStream)) {
            PDFTextStripper pdfStripper = new PDFTextStripper();
            return pdfStripper.getText(document);
        }
    }

    public String extractTextFromDOCX(InputStream inputStream) throws IOException {
        try (XWPFDocument document = new XWPFDocument(inputStream)) {
            StringBuilder text = new StringBuilder();
            document.getParagraphs().forEach(paragraph -> text.append(paragraph.getText()).append("\n"));
            return text.toString();
        }
    }

    public String extractTextFromPPT(InputStream inputStream) throws IOException {
        StringBuilder text = new StringBuilder();
        try (XMLSlideShow ppt = new XMLSlideShow(inputStream)) {
            for (XSLFSlide slide : ppt.getSlides()) {
                for (XSLFShape shape : slide.getShapes()) {
                    if (shape instanceof XSLFTextShape) {
                        XSLFTextShape textShape = (XSLFTextShape) shape;
                        text.append(textShape.getText()).append("\n");
                    }
                }
            }
        }
        return text.toString();
    }

    public String extractTextFromODT(InputStream inputStream) throws Exception {
        OdfTextDocument odfTextDocument = OdfTextDocument.loadDocument(inputStream);
        OdfEditableTextExtractor extractor = OdfEditableTextExtractor.newOdfEditableTextExtractor(odfTextDocument);
        return extractor.getText();

    }
}
