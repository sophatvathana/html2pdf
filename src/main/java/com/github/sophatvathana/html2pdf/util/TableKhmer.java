package com.github.sophatvathana.html2pdf.util;

import com.itextpdf.text.Font;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPTable;

public class TableKhmer extends PdfPTable {
	
	public TableKhmer() {}
	public TableKhmer(float[] relativeWidths) { super(relativeWidths);}
	public TableKhmer(int numColumns) { super(numColumns);}
	public TableKhmer(PdfPTable table) { super(table);}
	
	public void addCell(String text, Font font) {
		addCell(new Phrase(text, font));
	}
}
