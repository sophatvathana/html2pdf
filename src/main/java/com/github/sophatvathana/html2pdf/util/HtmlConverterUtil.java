package com.github.sophatvathana.html2pdf.util;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;

import com.github.sophatvathana.html2pdf.pdf.Pdf;
import com.github.sophatvathana.html2pdf.pdf.page.PageType;
import com.github.sophatvathana.html2pdf.pdf.params.Param;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.ColumnText;
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

import javax.servlet.http.HttpServletRequest;

public class HtmlConverterUtil {
	private static String test = "<body style=\"font-family:'Khmer'!important;\">\n" +
			"<div class=\"container\">\n" +
			"    <div class=\"wrapp-conten\">\n" +
			"        <div class=\"row invioce\">\n" +
			"            <div class=\"col-xs-12 col-sm-12 header-invoice\">\n" +
			"                <div class=\"header-logo\">\n" +
			"                    <img th:src=\"@{${url} + '/static/images/logo-bvc-header.png'}\"/>\n" +
			"                </div>\n" +
			"            </div>\n" +
			"            <div class=\"col-xs-12 col-sm-12\">\n" +
			"                <div class=\"col-xs-4 col-sm-4 barcode\"><img th:src=\"@{${url} + '/static/images/barcode.png'}\"/></div>\n" +
			"                <div class=\"col-xs-8 col-sm-8 invoice-title\"><img th:src=\"@{${url} + '/static/images/invoice-bvc.png'}\"/></div>\n" +
			"            </div>\n" +
			"        </div>\n" +
			"        <div class=\"row invioce\">\n" +
			"            <div class=\"col-xs-6 col-sm-6\">\n" +
			"                <div class=\"table-cus-info\">\n" +
			"                    <div class=\"cutomer-info\">\n" +
			"                        <div style=\"font-family:Khmer\" class=\"col-xs-6 col-sm-6\"><p>វិកាយ្យ :</p></div><div class=\"col-xs-6 col-sm-6\"><p>%s</p></div>\n" +
			"                        <div style=\"font-family:Khmer\" class=\"col-xs-6 col-sm-6\"><p>អត្រលេខ :</p></div><div class=\"col-xs-6 col-sm-6\"><p>100</p></div>\n" +
			"                        <div style=\"font-family:Khmer\" class=\"col-xs-6 col-sm-6\"><p>ឈ្មោះ អតិថិជន :</p></div><div class=\"col-xs-6 col-sm-6\"><p>ហេង ម៉េងហុង</p></div>\n" +
			"                        <div style=\"font-family:Khmer\" class=\"col-xs-6 col-sm-6\"><p>អាស័ដ្ធាន ទទួលវិក័យប័ត្រ:</p></div><div class=\"col-xs-6 col-sm-6 location\"><p>ភូមិត្បូង ឃុំគរ ស្រុក ត្បូង ឃ្មុំ ខេតុ ត្បូង ឃ្មុំ</p></div>\n" +
			"                    </div>\n" +
			"                    <img th:src=\"@{${url} + '/static/images/customer-info-bvc.png'}\"/>\n" +
			"                    <br/>\n" +
			"                </div>\n" +
			"            </div>\n" +
			"\n" +
			"            <div class=\"col-xs-6 col-sm-6 content-text\">\n" +
			"                <div class=\"col-xs-6 col-sm-6\"><p>ទីតាំង ប្រើប្រាស់ចរន្ត :</p></div><div class=\"col-xs-6 col-sm-6\"><p>T-0001</p></div>\n" +
			"                <div class=\"col-xs-6 col-sm-6\"><p>ថ្ងៃចេញ វិក្កយបត្រ :</p></div><div class=\"col-xs-6 col-sm-6\"><p>03/01/2017</p></div>\n" +
			"                <div class=\"col-xs-6 col-sm-6\"><p>ចាប់ពីថ្ងៃ :</p></div><div class=\"col-xs-6 col-sm-6\"><p>01/12/2017</p></div>\n" +
			"                <div class=\"col-xs-6 col-sm-6\"><p>ដល់ ថ្ងៃទី  :</p></div><div class=\"col-xs-6 col-sm-6\"><p>01/12/2017</p></div>\n" +
			"            </div>\n" +
			"        </div>\n" +
			"        <div class=\"master-content\">\n" +
			"            <img th:src=\"@{${url} + '/static/images/b-account.jpg'}\"/>\n" +
			"            <div class=\"row invioce\">\n" +
			"                <div class=\"col-xs-12 col-sm-12\">\n" +
			"                    <div class=\"col-xs-6 col-sm-6\">\n" +
			"                        <div class=\"col-xs-6 col-sm-6\"><p>ឈ្មោះគណនី ធនាគា (ACCOUNT NAME)  :</p></div><div class=\"col-xs-6 col-sm-6\"><p><b>BVC POWER DEVELOPMENT CO.,LTD</b></p></div>\n" +
			"                        <div class=\"col-xs-6 col-sm-6\"><p>លេខគណនី ធនាគា :</p></div><div class=\"col-xs-6 col-sm-6\"><p><b>00083625</b></p></div>\n" +
			"                    </div>\n" +
			"                    <div class=\"col-xs-6 col-sm-6\">\n" +
			"                        <div class=\"col-xs-6 col-sm-6\"><p>ឈ្មោះ ធនាគា (BANK NAME) :</p></div><div class=\"col-xs-6 col-sm-6\"><p><b>ធនាគា ABA</b></p></div>\n" +
			"                        <div class=\"col-xs-6 col-sm-6\"><p>រូបិយប័ណ្ណ(CURRENCY) :</p></div><div class=\"col-xs-6 col-sm-6\"><p><b>ដុល្លា</b></p></div>\n" +
			"                    </div>\n" +
			"                </div>\n" +
			"            </div>\n" +
			"            <div class=\"row invioce\">\n" +
			"                <div class=\"col-xs-12 col-sm-12\">\n" +
			"                    <table class=\"table invoice\">\n" +
			"                        <thead class=\"thead-inverse\">\n" +
			"                        <tr>\n" +
			"                            <th>\n" +
			"                                <p>នាឡិកាស្ទង់</p>\n" +
			"                                <p>METER</p>\n" +
			"                            </th>\n" +
			"                            <th>\n" +
			"                                <p>លេខអំណានចាស់</p>\n" +
			"                                <p>PREVIOUS</p>\n" +
			"                            </th>\n" +
			"                            <th>\n" +
			"                                <p>លេខអំណានថ្នី</p>\n" +
			"                                <p>CURRENT</p>\n" +
			"                            </th>\n" +
			"                            <th>\n" +
			"                                <p>មេគុណ</p>\n" +
			"                                <p>MULTIPLIER</p>\n" +
			"                            </th>\n" +
			"                            <th>\n" +
			"                                <p>ការប្រើប្រាស់ ថាមពល</p>\n" +
			"                                <p>CONSUMPTION</p>\n" +
			"                            </th>\n" +
			"                            <th>\n" +
			"                                <p>តម្លៃឯកតា</p>\n" +
			"                                <p>RATE</p>\n" +
			"                            </th>\n" +
			"                            <th>\n" +
			"                                <p>តម្លៃ សរុប</p>\n" +
			"                                <p>VALUE</p>\n" +
			"                            </th>\n" +
			"                        </tr>\n" +
			"                        </thead>\n" +
			"                        <tbody>\n" +
			"                        <tr>\n" +
			"                            <td></td>\n" +
			"                            <td></td>\n" +
			"                            <td></td>\n" +
			"                            <td></td>\n" +
			"                            <td></td>\n" +
			"                            <td>Balance brought forward</td>\n" +
			"                            <td>1234567</td>\n" +
			"                        </tr>\n" +
			"                        <tr>\n" +
			"                            <td></td>\n" +
			"                            <td></td>\n" +
			"                            <td></td>\n" +
			"                            <td></td>\n" +
			"                            <td></td>\n" +
			"                            <td><p>Enalty for reactive energt</p></td>\n" +
			"                            <td><p>123456</p></td>\n" +
			"                        </tr>\n" +
			"                        <tr>\n" +
			"                            <td><p>100001</p></td>\n" +
			"                            <td><p>00000</p></td>\n" +
			"                            <td><p>999999</p></td>\n" +
			"                            <td><p>10</p></td>\n" +
			"                            <td><p>99999</p></td>\n" +
			"                            <td><p>0.10</p></td>\n" +
			"                            <td><p>R  9999.99</p></td>\n" +
			"                        </tr>\n" +
			"                        <tr>\n" +
			"                            <td><p>100001</p></td>\n" +
			"                            <td><p>00000</p></td>\n" +
			"                            <td><p>999999</p></td>\n" +
			"                            <td><p>@10</p></td>\n" +
			"                            <td><p>99999</p></td>\n" +
			"                            <td><p>0.001</p></td>\n" +
			"                            <td><p>R  9999.99</p></td>\n" +
			"                        </tr>\n" +
			"                        </tbody>\n" +
			"                    </table>\n" +
			"                    <div class=\"col-xs-12 col-sm-12 invoice\">\n" +
			"                        <center><h1 class=\"invoice\">វិក្កយបត្រ INVOICE</h1></center>\n" +
			"                    </div>\n" +
			"                </div>\n" +
			"            </div>\n" +
			"        </div>\n" +
			"\n" +
			"        <div class=\"row invioce\">\n" +
			"            <div class=\"col-xs-12 col-sm-12 last-usage\">\n" +
			"                <div class=\"col-xs-6 col-sm-6 lase-usage\">\n" +
			"                    <div class=\"panel panel-default usage\">\n" +
			"                        <div class=\"col-xs-12 col-sm-12 history-usage\"><p>ប្រវត្តិនៃការ ប្រើប្រាស់ អគ្គិសនី រយះពេល ១២ ខែកន្លងមក</p></div>\n" +
			"                        <div class=\"panel-body\">\n" +
			"                            <ul>\n" +
			"                                <li>01/01/2017</li>\n" +
			"                                <li>01/02/2017</li>\n" +
			"                                <li>01/03/2017</li>\n" +
			"                                <li>01/04/2017</li>\n" +
			"                            </ul>\n" +
			"                            <ul>\n" +
			"                                <li>11 គ</li>\n" +
			"                                <li>12 គ</li>\n" +
			"                                <li>13 គ</li>\n" +
			"                                <li>14 គ</li>\n" +
			"                            </ul>\n" +
			"                            <ul>\n" +
			"                                <li>01/05/2017</li>\n" +
			"                                <li>01/06/2017</li>\n" +
			"                                <li>01/07/2017</li>\n" +
			"                                <li>01/08/2017</li>\n" +
			"                            </ul>\n" +
			"                            <ul>\n" +
			"                                <li>11 គ</li>\n" +
			"                                <li>12 គ</li>\n" +
			"                                <li>13 គ</li>\n" +
			"                                <li>14 គ</li>\n" +
			"                            </ul>\n" +
			"                            <ul>\n" +
			"                                <li>01/09/2017</li>\n" +
			"                                <li>01/10/2017</li>\n" +
			"                                <li>01/11/2017</li>\n" +
			"                                <li>01/12/2017</li>\n" +
			"                            </ul>\n" +
			"                            <ul>\n" +
			"                                <li>11 គ</li>\n" +
			"                                <li>12 គ</li>\n" +
			"                                <li>13 គ</li>\n" +
			"                                <li>14 គ</li>\n" +
			"                            </ul>\n" +
			"                        </div>\n" +
			"                    </div>\n" +
			"                </div>\n" +
			"                <div class=\"col-xs-6 col-sm-6 last-usage\">\n" +
			"                    <div class=\"panel panel-default usage\">\n" +
			"                        <div class=\"panel-body\">\n" +
			"                            <div class=\"col-xs-12 col-sm-12 note1\"><p>ប្រសិនជាអ្នក ប្រើប្រាស់ មានចម្ងល់ ឬ ចង់ តវា សូមទំនាក់ទំនង ដោយផ្ទាល់ ជាមួយ ភ្នាក់ទទួល ខុសត្រូវ របស់សាខា។</p></div>\n" +
			"                            <div class=\"col-xs-12 col-sm-12 note1\"><p>អគ្គិសនីក្រុមហ៊ុន អ៊ិនធើ ប៊ីភី សឺលូសិន ខបភើរេសិន រក្សាសិទ្ធិមិនដោះស្រាយជូនតាមរយះ ការបង្ហោះបញ្ហា\n" +
			"                                លើប្រព័ន្ធសារ ពត៏មានសង្គម ជាលក្ខណៈ បុគ្គលនោះទេ\u200B ។</p></div>\n" +
			"                            <div class=\"col-xs-12 col-sm-12 note1\"><p>អ្នកចុះផ្សាយមិនមានការទទួលខុសត្រូវ ដែលប៉ះពាល់ដល់ កិត្តិយសរបស់ អគ្គិសនី អ៊ិនធើ ប៊ីភី សឺលូសិន ខបភើរេសិន\n" +
			"                                អាចប្រឈមមុខជាមួយច្បាប់ដែលពាក់ពាន់ ជាធរមាន ។</p></div>\n" +
			"                        </div>\n" +
			"                    </div>\n" +
			"                </div>\n" +
			"            </div>\n" +
			"        </div>\n" +
			"\n" +
			"        <div class=\"row invioce\">\n" +
			"            <div class=\"co-xs-12 col-sm-12 money\">\n" +
			"                <div class=\"col-xs-9 col-sm-9 price\">\n" +
			"                    <div class=\"panel panel-default price\">\n" +
			"                        <p>លុយដែល ត្រូវផាកខែមុន (Punished Money)</p>\n" +
			"                    </div>\n" +
			"                </div>\n" +
			"                <div class=\"col-xs-3 col-sm-3 price\">\n" +
			"                    <div class=\"panel panel-default price1\">\n" +
			"                        <p>0</p>\n" +
			"                    </div>\n" +
			"                </div>\n" +
			"            </div>\n" +
			"        </div>\n" +
			"        <div class=\"row invioce\">\n" +
			"            <div class=\"co-xs-12 col-sm-12 money\">\n" +
			"                <div class=\"col-xs-9 col-sm-9 price\">\n" +
			"                    <div class=\"panel panel-default money\">\n" +
			"                        <p>ថ្ងៃ ផុត កំណត់ (Expiry Date)</p>\n" +
			"                    </div>\n" +
			"                </div>\n" +
			"                <div class=\"col-xs-3 col-sm-3 price\">\n" +
			"                    <div class=\"panel panel-default money1\">\n" +
			"                        <p>14/05/2016</p>\n" +
			"                    </div>\n" +
			"                </div>\n" +
			"            </div>\n" +
			"        </div>\n" +
			"        <div class=\"row invioce\">\n" +
			"            <div class=\"col-xs-12 col-sm-12 last-usage\">\n" +
			"                <div class=\"col-xs-6 col-sm-6 last-usage\">\n" +
			"                    <div class=\"panel panel-default payed\">\n" +
			"                        <div class=\"panel-body\">\n" +
			"                            <div class=\"col-xs-12 col-sm-12 note\"><p>&bull; ហួសពីថ្ងៃផុតកំណត់បង់ប្រាក់ដែលបានកំណត់ នេះ ក្នុងករណី អតិថិជនមិនបានបង់ប្រាក់ អគ្គិសនី អ៊ិនធើ ប៊ីភី សឺលូសិន ខបភើរេសិន និងផ្អាក\n" +
			"                                ការផ្គត់ថាមពលជូន</p></div>\n" +
			"                            <div class=\"col-xs-12 col-sm-12 note\"><p>&bull; ការផ្ជាប់ចរន្ត ជូនវិញ លុះត្រាតែអតិថិជន បានបង់ប្រាក់បំណុលសរុប ក្នុងវិក្កយបត្រនេះ\u200B និង\u200Bថ្លៃផាកពិន័យ ក្នុងមួយថ្ងៃ ០.០៧ ភាគរយ រួចរាល់ហើយ</p></div>\n" +
			"                            <div class=\"col-xs-12 col-sm-12 note\"><p>&bull; សូមអញ្ជើញមក បង់ប្រាក់ នៅបេឡាអគ្គិសនី ឬ គណនីធនាគារបស់ ក្រុមហ៊ុន ។</p></div>\n" +
			"                            <div class=\"col-xs-12 col-sm-12 note\"><p>&bull; រាល់ការបង់ប្រាក់ តាមរយៈ ធនាគារ អតិធិជន គឺជាអ្នកទទួលខុសត្រូវ លើថ្លៃសេវា របស់ធនាគា ។</p></div>\n" +
			"                        </div>\n" +
			"                    </div>\n" +
			"                </div>\n" +
			"                <div class=\"col-xs-6 col-sm-6 last-usage\">\n" +
			"                    <div class=\"panel panel-default payed\">\n" +
			"                        <div class=\"col-xs-12 col-sm-12 payed\">\n" +
			"                            <div class=\"col-xs-6 col-sm-6 amount\"><p>បំណល់សរុប (Amount)</p></div>\n" +
			"                            <div class=\"col-xs-6 col-sm-6 pay\"><p>R 4700.00</p></div>\n" +
			"                        </div>\n" +
			"                        <div class=\"col-xs-12 col-sm-12 payed\">\n" +
			"                            <div class=\"col-xs-6 col-sm-6 amount\"><p>ថ្ងៃបង់ប្រាក់ (Paid Date)</p></div>\n" +
			"                            <div class=\"col-xs-6 col-sm-6 pay\"><p>0</p></div>\n" +
			"                        </div>\n" +
			"                    </div>\n" +
			"                </div>\n" +
			"            </div>\n" +
			"        </div>\n" +
			"        <div class=\"row invioce\">\n" +
			"            <hr/>\n" +
			"        </div>\n" +
			"        <div class=\"row invioce\">\n" +
			"            <div class=\"col-xs-12 col-sm-12 last-usage\">\n" +
			"                <div class=\"col-xs-6 col-sm-6 last-usage\">\n" +
			"                    <div class=\"panel panel-default payed\">\n" +
			"                        <img src=\"icons/barcode.png\"/>\n" +
			"                        <div class=\"panel-body\">\n" +
			"                            <div class=\"col-xs-6 col-sm-6\"><p>ថ្ងៃចេញ វិក្កយបត្រ </p></div><div class=\"col-xs-6 col-sm-6\"><p>: 01/08/2017</p></div>\n" +
			"                            <div class=\"col-xs-6 col-sm-6\"><p>វិក្កយបត្រលេខ </p></div><div class=\"col-xs-6 col-sm-6\"><p>: 10000</p></div>\n" +
			"                            <div class=\"col-xs-6 col-sm-6\"><p>ឈ្មោះ អតិថិជន </p></div><div class=\"col-xs-6 col-sm-6\"><p>: ហេង ម៉េងហុង</p></div>\n" +
			"                            <div class=\"col-xs-6 col-sm-6\"><p>អត្តលេខអតិថិជន</p></div><div class=\"col-xs-6 col-sm-6 location\"><p>: 0001</p></div>\n" +
			"                        </div>\n" +
			"                    </div>\n" +
			"                </div>\n" +
			"                <div class=\"col-xs-6 col-sm-6 last-usage\">\n" +
			"                    <div class=\"panel panel-default payed\">\n" +
			"                        <div class=\"col-xs-12 col-sm-12 payed\">\n" +
			"                            <div class=\"col-xs-6 col-sm-6 amount\"><p>បំណល់សរុប (Amount)</p></div>\n" +
			"                            <div class=\"col-xs-6 col-sm-6 pay\"><p>R 4700.00</p></div>\n" +
			"                        </div>\n" +
			"                        <div class=\"col-xs-12 col-sm-12 payed\">\n" +
			"                            <div class=\"col-xs-6 col-sm-6 amount\"><p>ថ្ងៃបង់ប្រាក់ (Paid Date)</p></div>\n" +
			"                            <div class=\"col-xs-6 col-sm-6 pay\"><p></p></div>\n" +
			"                        </div>\n" +
			"                    </div>\n" +
			"                </div>\n" +
			"            </div>\n" +
			"        </div>\n" +
			"    </div>\n" +
			"</div>\n" +
			"</body>";
	
	private static String localFontPath = HtmlConverterUtil.class.getClassLoader().getResource("/fonts/Khmer.ttf").getPath();
	public static void Html2Img(String html, String img) throws IOException {
		
		HtmlImageGenerator imageGenerator = new HtmlImageGenerator();
		imageGenerator.loadHtml(html);
		imageGenerator.saveAsImage(img) ;
	}
	
//	public static void Html2Pdf(String html,String pdf) throws IOException, DocumentException, CssResolverException {
//
//		Document document = new Document();
//		//String localFontPath = HtmlConverterUtil.class.getClassLoader().getResource("kh_battambang.ttf").getPath();
//		try {
//			File file = new File(pdf);
//			file.getParentFile().mkdirs();
////        	if (!file.exists()) {
////        		file.createNewFile();
////        	}
//			PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(file));
//			document.open();
//
//			KhmerLigaturizer d = new KhmerLigaturizer();
//			//String strProcess = d.process("\u0936\u093e\u0902\u0924\u094d\u093f");
//			String strProcess = d.process("ហេតុនេះ​យើង​ត្រូវ​តែរួម​គ្នា​ដោះ​ស្រាយ ខ្លួន ស្វាគមន៍មកកាន់អូស៊ីហ្វាយ");
//
//			BaseFont bfComic = BaseFont.createFont("/Users/sophatvathana/Desktop/sona/html2pdf/src/main/resources/fonts/Kh-Battambang.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
//			//document.add(new Paragraph("Unicode: ការ​សិក្សា​ខាងបច្ចេក​វិទ្យា​នៅ​មាន​តម្រូវការខ្លាំង​លើ​ការ​ប្រើប្រាស់​ភាសា​ខ្មែរ", new Font(bfComic, 12)));
//			document.add(new Paragraph("Hello"));
//			document.add(new Paragraph(strProcess, new Font(bfComic, 22)));
//
//			ColumnText columnText = new ColumnText(writer.getDirectContent());
//			columnText.setRunDirection(PdfWriter.RUN_DIRECTION_LTR);
//			//columnText.addElement(new Paragraph("Unicode: ហេតុនេះ​យើង​ត្រូវ​តែរូម​គ្នា​ដោះ​ស្រាយ......", new Font(bfComic, 12)));
//			document.add(new Paragraph(d.process("\u1780\u17B6\u179A\u200B\u179F\u17B7\u1780\u17D2\u179F\u17B6\u200B\u1781\u17B6\u1784\u1794\u1785\u17D2\u1785\u17C1\u1780\u200B\u179C\u17B7\u1791\u17D2\u1799\u17B6\u200B\u1793\u17C5\u200B\u1798\u17B6\u1793\u200B\u178F\u1798\u17D2\u179A\u17BC\u179C\u1780\u17B6\u179A\u1781\u17D2\u179B\u17B6\u17C6\u1784\u200B\u179B\u17BE\u200B\u1780\u17B6\u179A\u200B\u1794\u17D2"), new Font(bfComic, 12)));
//
//		} catch (Exception e) {
//			System.err.println(e.getMessage());
//		}
//		document.close();
//
//	}
//
//	public static void Html2Pdf(HttpServletRequest httpServletRequest, String pdf) throws IOException, DocumentException, CssResolverException, InterruptedException {
//    	System.out.println(localFontPath);
//		File file = new File(pdf);
//        file.getParentFile().mkdirs();
//        Document document = new Document();
//        PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(file));
////		writer.setPdfVersion(PdfWriter.VERSION_1_7);
//        writer.setInitialLeading(12.5f);
//        document.open();
//		CSSResolver cssResolver = new StyleAttrCSSResolver();
////		File file1 = new File("/Users/sophatvathana/Desktop/sona/html2pdf/src/main/webapp/static/css/bootstrap.min.css");
////		CssFile cssFile = XMLWorkerHelper.getCSS(new FileInputStream(file1));
////		cssResolver.addCss(cssFile);
////		File file2 = new File("/Users/sophatvathana/Desktop/sona/html2pdf/src/main/webapp/static/css/style.css");
////		CssFile cssFile2 = XMLWorkerHelper.getCSS(new FileInputStream(file2));
//		InputStream csspathtest = Thread.currentThread()
//				.getContextClassLoader()
////				.getResourceAsStream("css/bootstrap.css")
//				.getResourceAsStream("css/style.css");
//		CssFile cssFile2 = XMLWorkerHelper.getCSS(csspathtest);
//		cssResolver.addCss(cssFile2);
//
////		File file3 = new File("/Users/sophatvathana/Desktop/sona/html2pdf/src/main/webapp/static/css/responsive.css");
////		CssFile cssFile3 = XMLWorkerHelper.getCSS(new FileInputStream(file3));
////		cssResolver.addCss(cssFile3);
//
//		XMLWorkerFontProvider fontProvider = new XMLWorkerFontProvider(XMLWorkerFontProvider.DONTLOOKFORFONTS);
////		BaseFont baseFont = BaseFont.createFont("/Users/sophatvathana/Desktop/sona/html2pdf/src/main/resources/fonts/Khmer.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
////		Font font = new Font(baseFont, 30f);
//
//		fontProvider.register("/Users/sophatvathana/Desktop/sona/html2pdf/src/main/resources/fonts/kh_battambang.ttf", "Khmer");
//		fontProvider.setUseUnicode(true);
//		CssAppliers cssAppliers = new CssAppliersImpl(fontProvider);
//		HtmlPipelineContext htmlContext = new HtmlPipelineContext(cssAppliers);
//		htmlContext.setTagFactory(Tags.getHtmlTagProcessorFactory());
//
//		PdfWriterPipeline pdfs = new PdfWriterPipeline(document, writer);
//		HtmlPipeline htmls = new HtmlPipeline(htmlContext, pdfs);
//		CssResolverPipeline css = new CssResolverPipeline(cssResolver, htmls);
//		KhmerLigaturizer khmerLigaturizer = new KhmerLigaturizer();
//		for(int i = 0; i<3000; i++) {
//			String s = HttpRequest.sendPost(getUrl(httpServletRequest)+"/index.do", "");
//			//		System.out.println(html);
//			String str = khmerLigaturizer.process(s);
//   //		System.out.println(str);
//			XMLWorker worker = new XMLWorker(css, true);
//			XMLParser p = new XMLParser(worker);
//			InputStream streamTemp = new ByteArrayInputStream(str.getBytes("UTF-8"));
//			p.parse(streamTemp, Charset.forName("UTF-8"));
//			document.newPage();
//		}
//		document.close();
//
////
////        InputStream streamTemp = new ByteArrayInputStream(str.getBytes());
////        XMLWorkerHelper.getInstance().parseXHtml(writer, document, streamTemp, StandardCharsets.UTF_8);
////        document.close();
////
//
//    }

    public static void Html2Pdf(HttpServletRequest httpServletRequest, String pdf) throws IOException, InterruptedException {
		long t = System.currentTimeMillis();
		Pdf pdfs = new Pdf();
		for(int i =0; i<10000;i++){
			try {
				String s = HttpRequest.sendPost(getUrl(httpServletRequest) + "/index.do", "");
				pdfs.addPageFromString(s);
			}catch (Exception e){
			
			}
		}
		pdfs.saveAs(pdf);
//		pdfs.addParam(new Param("--enable-javascript"));
		
		long e = System.currentTimeMillis();
		System.out.println(t - e);
	}

private static String getUrl(HttpServletRequest httpServletRequest){
	String url=httpServletRequest.getScheme()+"://"+httpServletRequest.getServerName()+":"+httpServletRequest.getServerPort()+httpServletRequest.getContextPath();
	return url;
}

	 
	public static void main(String[] args) throws IOException, DocumentException  {  
		
	}

	
	
}  