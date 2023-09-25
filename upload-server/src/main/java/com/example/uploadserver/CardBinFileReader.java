package com.example.uploadserver;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ooxml.util.SAXHelper;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.eventusermodel.ReadOnlySharedStringsTable;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.eventusermodel.XSSFSheetXMLHandler;
import org.apache.poi.xssf.model.StylesTable;
import org.apache.poi.xssf.usermodel.XSSFComment;
import org.springframework.stereotype.Component;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
@RequiredArgsConstructor
public class CardBinFileReader {

    private static final String XLSX = "xlsx";

    public List<CardBinNumber> readXlsx(InputStream inputStream) {
        ExcelSheetHandler excelSheetHandler = new ExcelSheetHandler().readExcel(inputStream);

        List<List<String>> rows = excelSheetHandler.getRows();
        List<String> header = excelSheetHandler.getHeader();

        if (!header.get(1).equals("발급사") ||
                !header.get(2).equals("BIN") ||
                !header.get(3).equals("전표인자명") ||
                !header.get(4).equals("개인/법인"))
            throw new RuntimeException("please check file's contents");

        Map<String, CardBinNumber> map = new HashMap<>();

        for (List<String> row : rows) {
            CardBinNumber cardBinNumber = CardBinNumber.builder()
                    .cardName(row.get(3))
                    .issuingCompanyName(row.get(1))
                    .code(row.get(2))
                    .build();

            map.put(row.get(2), cardBinNumber);
        }

        return new ArrayList<>(map.values());
    }

    @Getter
    static class ExcelSheetHandler implements XSSFSheetXMLHandler.SheetContentsHandler {

        private int currentCol = -1;
        private int currRowNum = 0;

        private List<List<String>> rows = new ArrayList<>();
        private List<String> row = new ArrayList<>();
        private List<String> header = new ArrayList<>();

        public ExcelSheetHandler readExcel(InputStream stream) {

            ExcelSheetHandler sheetHandler = new ExcelSheetHandler();
            try {
                OPCPackage opc = OPCPackage.open(stream);
                XSSFReader xssfReader = new XSSFReader(opc);
                StylesTable styles = xssfReader.getStylesTable();
                ReadOnlySharedStringsTable strings = new ReadOnlySharedStringsTable(opc);

                InputStream inputStream = xssfReader.getSheetsData().next();
                InputSource inputSource = new InputSource(inputStream);
                ContentHandler handle = new XSSFSheetXMLHandler(styles, strings, sheetHandler, false);

                XMLReader xmlReader = SAXHelper.newXMLReader();
                xmlReader.setContentHandler(handle);

                xmlReader.parse(inputSource);
                inputStream.close();
                opc.close();
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage());
            }

            return sheetHandler;
        }

        @Override
        public void startRow(int arg0) {
            this.currentCol = -1;
            this.currRowNum = arg0;
        }

        @Override
        public void cell(String columnName, String value, XSSFComment var3) {
            int iCol = (new CellReference(columnName)).getCol();
            int emptyCol = iCol - currentCol - 1;

            for (int i = 0; i < emptyCol; i++) {
                row.add("");
            }
            currentCol = iCol;
            row.add(value);
        }



        @Override
        public void endRow(int rowNum) {
            if (rowNum == 0) {
                header = new ArrayList<>(row);
            } else {
                if (row.size() < header.size()) {
                    for (int i = row.size(); i < header.size(); i++) {
                        row.add("");
                    }
                }
                rows.add(new ArrayList<>(row));
            }
            row.clear();
        }
    }
}
