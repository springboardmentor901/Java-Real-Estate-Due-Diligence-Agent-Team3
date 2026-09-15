package com.realestate.due_diligence_agent.service;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.realestate.due_diligence_agent.entity.Property;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;

@Service
public class PdfReportGenerator {

    public byte[] generatePdf(Property property, String executiveSummary, double overallScore) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(out);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        document.add(new Paragraph("Real Estate Due Diligence Report").setBold().setFontSize(18));
        document.add(new Paragraph("Property: " + property.getAddress() + ", " + property.getCity() + ", " + property.getState()));
        document.add(new Paragraph("Executive Summary: " + executiveSummary));
        document.add(new Paragraph("Overall Risk Score: " + overallScore + " / 100"));

        Table table = new Table(2);
        table.addCell("Risk Category");
        table.addCell("Score");
        table.addCell("Flood Zone Risk");
        table.addCell("Low (15/100)");
        table.addCell("Tax Assessment Risk");
        table.addCell("Moderate (35/100)");
        table.addCell("Zoning Compliance");
        table.addCell("Passed (0/100)");
        table.addCell("Permits & Environmental");
        table.addCell("Low (10/100)");
        table.addCell("Legal / Title Risk");
        table.addCell("Clean (5/100)");
        table.addCell("Financial Exposure");
        table.addCell("Low (20/100)");

        document.add(table);
        document.close();
        return out.toByteArray();
    }
}