package jxl.example;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;

import jxl.Cell;
import jxl.CellType;
import jxl.FormulaCell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.biff.CellReferenceHelper;
import jxl.biff.formula.FormulaException;

public class SimpleExample {
	public static void main(String[] args) throws Exception {
		File file = new File("D:\\Temp\\csv\\test.xsl");
		file.createNewFile();
		//		FileOutputStream out = new FileOutputStream(file);

		try {

			OutputStreamWriter osw = new OutputStreamWriter(System.out, "utf-8");
			BufferedWriter bw = new BufferedWriter(osw);

			Workbook w = Workbook.getWorkbook(file);

			for (int sheet = 0; sheet < w.getNumberOfSheets(); sheet++) {
				Sheet s = w.getSheet(sheet);

				bw.write(s.getName());
				bw.newLine();

				Cell[] row = null;
				Cell c = null;

				for (int i = 0 ; i < s.getRows() ; i++) {
					row = s.getRow(i);

					for (int j = 0; j < row.length; j++) {
						c = row[j];
						if (c.getType() == CellType.NUMBER_FORMULA || 
								c.getType() == CellType.STRING_FORMULA || 
								c.getType() == CellType.BOOLEAN_FORMULA ||
								c.getType() == CellType.DATE_FORMULA ||
								c.getType() == CellType.FORMULA_ERROR) {
							FormulaCell nfc = (FormulaCell) c;
							StringBuffer sb = new StringBuffer();
							CellReferenceHelper.getCellReference
							(c.getColumn(), c.getRow(), sb);

							try {
								bw.write("Formula in "  + sb.toString() + 
										" value:  " + c.getContents());
								bw.flush();
								bw.write(" formula: " + nfc.getFormula());
								bw.flush();
								bw.newLine();
							} catch (FormulaException e) {
								bw.newLine();
								e.printStackTrace();
							}
						}
					}
				}
			}
			bw.flush();
			bw.close();
		} catch (UnsupportedEncodingException e) {
			System.err.println(e.toString());
		}
	}
}
