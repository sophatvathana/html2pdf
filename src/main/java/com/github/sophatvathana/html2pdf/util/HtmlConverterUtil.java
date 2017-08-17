package com.github.sophatvathana.html2pdf.util;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.tool.xml.XMLWorker;
import com.itextpdf.tool.xml.XMLWorkerFontProvider;
import com.itextpdf.tool.xml.XMLWorkerHelper;

import com.itextpdf.tool.xml.css.CssFile;
import com.itextpdf.tool.xml.css.CssFileImpl;
import com.itextpdf.tool.xml.css.StyleAttrCSSResolver;
import com.itextpdf.tool.xml.exceptions.CssResolverException;
import com.itextpdf.tool.xml.html.CssAppliers;
import com.itextpdf.tool.xml.html.CssAppliersImpl;
import com.itextpdf.tool.xml.html.Tags;
import com.itextpdf.tool.xml.parser.XMLParser;
import com.itextpdf.tool.xml.pipeline.css.CSSResolver;
import com.itextpdf.tool.xml.pipeline.css.CssResolverPipeline;
import com.itextpdf.tool.xml.pipeline.end.PdfWriterPipeline;
import com.itextpdf.tool.xml.pipeline.html.HtmlPipeline;
import com.itextpdf.tool.xml.pipeline.html.HtmlPipelineContext;
import gui.ava.html.image.generator.HtmlImageGenerator;

public class HtmlConverterUtil {  

	public static void Html2Img(String html, String img) throws IOException {
		
		HtmlImageGenerator imageGenerator = new HtmlImageGenerator();
		imageGenerator.loadHtml(html);
		imageGenerator.saveAsImage(img) ;
	}
	
	public static void Html2Pdf(String html,String pdf) throws IOException, DocumentException, CssResolverException {
    	File file = new File(pdf);
        file.getParentFile().mkdirs();
        Document document = new Document();
        PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(file));
//        writer.setInitialLeading(12.5f);
        document.open();
		CSSResolver cssResolver = new StyleAttrCSSResolver();
//		File file1 = new File("/Users/sophatvathana/Desktop/sona/html2pdf/src/main/webapp/static/css/bootstrap.min.css");
//		CssFile cssFile = XMLWorkerHelper.getCSS(new FileInputStream(file1));
//		cssResolver.addCss(cssFile);
//		File file2 = new File("/Users/sophatvathana/Desktop/sona/html2pdf/src/main/webapp/static/css/style.css");
//		CssFile cssFile2 = XMLWorkerHelper.getCSS(new FileInputStream(file2));
//		cssResolver.addCss(cssFile2);
//
//		File file3 = new File("/Users/sophatvathana/Desktop/sona/html2pdf/src/main/webapp/static/css/responsive.css");
//		CssFile cssFile3 = XMLWorkerHelper.getCSS(new FileInputStream(file3));
//		cssResolver.addCss(cssFile3);
		
		XMLWorkerFontProvider fontProvider = new XMLWorkerFontProvider(XMLWorkerFontProvider.DONTLOOKFORFONTS);
		fontProvider.register("/Users/sophatvathana/Desktop/sona/html2pdf/src/main/resources/fonts/Khmer.ttf", "Khmer");
		CssAppliers cssAppliers = new CssAppliersImpl(fontProvider);
		HtmlPipelineContext htmlContext = new HtmlPipelineContext(cssAppliers);
		htmlContext.setTagFactory(Tags.getHtmlTagProcessorFactory());
		
		PdfWriterPipeline pdfs = new PdfWriterPipeline(document, writer);
		HtmlPipeline htmls = new HtmlPipeline(htmlContext, pdfs);
		CssResolverPipeline css = new CssResolverPipeline(cssResolver, htmls);
		
		KhmerLigaturizer khmerLigaturizer = new KhmerLigaturizer();
//		System.out.println(html);
		String str = khmerLigaturizer.process(html);
		System.out.println(str);
		
		
		for(int i = 0; i<2; i++) {
			XMLWorker worker = new XMLWorker(css, true);
			XMLParser p = new XMLParser(worker);
			InputStream streamTemp = new ByteArrayInputStream(str.getBytes("UTF-8"));
			p.parse(streamTemp, Charset.forName("UTF-8"));
			document.newPage();
		}
		document.close();
		
		
//        InputStream streamTemp = new ByteArrayInputStream(str.getBytes());
//        XMLWorkerHelper.getInstance().parseXHtml(writer, document, streamTemp, StandardCharsets.UTF_8);
//        document.close();
    }
	
	
	 
	public static void main(String[] args) throws IOException, DocumentException  {  
		
	}

	
	
}  