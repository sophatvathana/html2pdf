package com.github.sophatvathana.html2pdf.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.github.sophatvathana.html2pdf.util.HttpRequest;
import com.itextpdf.tool.xml.exceptions.CssResolverException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.itextpdf.text.DocumentException;

import com.github.sophatvathana.html2pdf.util.HtmlConverterUtil;


@RestController
public class HtmlConverterController{
	
	@RequestMapping(value="/index")
	public ModelAndView index(HttpServletRequest httpServletRequest,HttpServletResponse response) throws IOException, DocumentException {
		ModelAndView mav=new ModelAndView("index");
		mav.addObject("url",getUrl(httpServletRequest) );
		mav.addObject("name", "sona");
		return mav;
		
	}
	
	@RequestMapping(value="/html")
	public void pdf(HttpServletRequest httpServletRequest,HttpServletResponse response,String name) throws IOException, DocumentException, CssResolverException {
    	String s = HttpRequest.sendPost(getUrl(httpServletRequest)+"/index.do", "");
		System.out.println(s);
    	String fileUrl=httpServletRequest.getSession().getServletContext().getRealPath("/")+"tmp/"+name+UUID.randomUUID();
    	System.out.println(fileUrl);
		HtmlConverterUtil.Html2Pdf(s, fileUrl);
		
		//response.setContentType("multipart/form-data");
		response.setContentType("application/pdf;charset=utf-8");
		response.setCharacterEncoding("UTF-8");
		response.setHeader("Content-Disposition", "inline;fileName="+name);
	
		File f=new File(fileUrl);
		response.setContentLength((int) f.length());
		InputStream inputStream = new FileInputStream(f);
		OutputStream os = response.getOutputStream();
//
//		byte[] b = new byte[2048];
//		int length;
//		while ((length = inputStream.read(b)) > 0) {
//			os.write(b, 0, length);
//		}
//		os.close();
//		inputStream.close();
	
		stream(inputStream, os);
		f.delete();

	}
	
	public long stream(InputStream input, OutputStream output) throws IOException {
		try (
				ReadableByteChannel inputChannel = Channels.newChannel(input);
				WritableByteChannel outputChannel = Channels.newChannel(output);
		) {
			ByteBuffer buffer = ByteBuffer.allocateDirect(10240);
			long size = 0;
			
			while (inputChannel.read(buffer) != -1) {
				buffer.flip();
				size += outputChannel.write(buffer);
				buffer.clear();
			}
			
			return size;
		}
	}
	
	private String getUrl(HttpServletRequest httpServletRequest){
		String url=httpServletRequest.getScheme()+"://"+httpServletRequest.getServerName()+":"+httpServletRequest.getServerPort()+httpServletRequest.getContextPath();
		return url;
	}
		

}
