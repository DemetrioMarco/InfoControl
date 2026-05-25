package com.infocontrol.apirest.service;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.infocontrol.apirest.dto.response.StockUbicacionResponse;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class ReportExportService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public byte[] exportarExcelStockPorUbicacion(List<StockUbicacionResponse.PorSubUbicacion> reportes) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Stock por Ubicación");

            CellStyle headerStyle = crearEstiloHeader(workbook);
            CellStyle dataStyle = crearEstiloData(workbook);
            CellStyle totalStyle = crearEstiloTotal(workbook);

            int rowNum = 0;

            Row titleRow = sheet.createRow(rowNum++);
            org.apache.poi.ss.usermodel.Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue("REPORTE DE STOCK POR UBICACIÓN");
            titleCell.setCellStyle(crearEstiloTitulo(workbook));
            sheet.addMergedRegion(new org.apache.poi.ss.util.CellRangeAddress(0, 0, 0, 5));

            Row dateRow = sheet.createRow(rowNum++);
            dateRow.createCell(0).setCellValue("Fecha: " + LocalDateTime.now().format(DATE_FORMATTER));
            rowNum++;

            Row headerRow = sheet.createRow(rowNum++);
            String[] headers = {"Tipo Ubicación", "Ubicación", "Sub-Ubicación", "Código Producto", "Producto", "Cantidad"};
            for (int i = 0; i < headers.length; i++) {
                org.apache.poi.ss.usermodel.Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            for (StockUbicacionResponse.PorSubUbicacion ubicacion : reportes) {
                for (StockUbicacionResponse.DetallePorProducto producto : ubicacion.productos) {
                    Row row = sheet.createRow(rowNum++);
                    row.createCell(0).setCellValue(ubicacion.getTipoUbicacionNombre());
                    row.createCell(1).setCellValue(ubicacion.getUbicacionNombre());
                    row.createCell(2).setCellValue(ubicacion.getSubUbicacionNombre());
                    row.createCell(3).setCellValue(producto.getCodigoInterno());
                    row.createCell(4).setCellValue(producto.getNombreProducto());

                    org.apache.poi.ss.usermodel.Cell cantidadCell = row.createCell(5);
                    cantidadCell.setCellValue(producto.getCantidad());
                    cantidadCell.setCellStyle(dataStyle);

                    for (int i = 0; i < 6; i++) {
                        row.getCell(i).setCellStyle(dataStyle);
                    }
                }

                Row totalRow = sheet.createRow(rowNum++);
                totalRow.createCell(2).setCellValue("TOTAL SUB-UBICACIÓN:");
                org.apache.poi.ss.usermodel.Cell totalCell = totalRow.createCell(5);
                totalCell.setCellValue(ubicacion.getStockTotal());
                totalCell.setCellStyle(totalStyle);
                totalRow.getCell(2).setCellStyle(totalStyle);
                rowNum++;
            }

            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            ByteArrayOutputStream output = new ByteArrayOutputStream();
            workbook.write(output);
            return output.toByteArray();
        }
    }

    public byte[] exportarPdfStockPorUbicacion(List<StockUbicacionResponse.PorSubUbicacion> reportes) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(output);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document document = new Document(pdfDoc);

        // Usar fuentes estándar de iText sin argumentos
        PdfFont fontBold = PdfFontFactory.createFont(com.itextpdf.io.font.constants.StandardFonts.HELVETICA_BOLD);
        PdfFont fontRegular = PdfFontFactory.createFont(com.itextpdf.io.font.constants.StandardFonts.HELVETICA);

        Paragraph title = new Paragraph("REPORTE DE STOCK POR UBICACIÓN")
                .setFont(fontBold)
                .setFontSize(16)
                .setTextAlignment(TextAlignment.CENTER);
        document.add(title);

        Paragraph date = new Paragraph("Fecha: " + LocalDateTime.now().format(DATE_FORMATTER))
                .setFont(fontRegular)
                .setFontSize(10)
                .setTextAlignment(TextAlignment.LEFT);
        document.add(date);
        document.add(new Paragraph("\n"));

        Table table = new Table(new float[]{2, 2, 2, 1.5f, 2, 1});
        table.setWidth(UnitValue.createPercentValue(100));

        String[] headers = {"Tipo Ubicación", "Ubicación", "Sub-Ubicación", "Código", "Producto", "Cantidad"};
        for (String header : headers) {
            Cell headerCell = new Cell()
                    .add(new Paragraph(header).setFont(fontBold))
                    .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                    .setTextAlignment(TextAlignment.CENTER);
            table.addHeaderCell(headerCell);
        }

        for (StockUbicacionResponse.PorSubUbicacion ubicacion : reportes) {
            for (StockUbicacionResponse.DetallePorProducto producto : ubicacion.productos) {
                table.addCell(new Cell().add(new Paragraph(ubicacion.getTipoUbicacionNombre()).setFont(fontRegular)));
                table.addCell(new Cell().add(new Paragraph(ubicacion.getUbicacionNombre()).setFont(fontRegular)));
                table.addCell(new Cell().add(new Paragraph(ubicacion.getSubUbicacionNombre()).setFont(fontRegular)));
                table.addCell(new Cell().add(new Paragraph(producto.getCodigoInterno()).setFont(fontRegular)));
                table.addCell(new Cell().add(new Paragraph(producto.getNombreProducto()).setFont(fontRegular)));
                table.addCell(new Cell()
                        .add(new Paragraph(String.valueOf(producto.getCantidad())).setFont(fontRegular))
                        .setTextAlignment(TextAlignment.RIGHT));
            }

            table.addCell(new Cell(1, 5)
                    .add(new Paragraph("TOTAL SUB-UBICACIÓN:").setFont(fontBold))
                    .setTextAlignment(TextAlignment.RIGHT));

            table.addCell(new Cell()
                    .add(new Paragraph(String.valueOf(ubicacion.getStockTotal())).setFont(fontBold))
                    .setTextAlignment(TextAlignment.RIGHT));
        }

        document.add(table);
        document.close();
        return output.toByteArray();
    }

    private CellStyle crearEstiloHeader(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setColor(IndexedColors.WHITE.getIndex());
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }

    private CellStyle crearEstiloTitulo(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 14);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        return style;
    }

    private CellStyle crearEstiloData(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }

    private CellStyle crearEstiloTotal(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }
}
