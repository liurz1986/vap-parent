package com.vrv.vap.admin.common.util;

/**
 * html转pdf word
 * Created by lizj on 2020/12/11
 */

import com.itextpdf.text.Document;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.tool.xml.Pipeline;
import com.itextpdf.tool.xml.XMLWorker;
import com.itextpdf.tool.xml.XMLWorkerHelper;
import com.itextpdf.tool.xml.css.CssFilesImpl;
import com.itextpdf.tool.xml.css.StyleAttrCSSResolver;
import com.itextpdf.tool.xml.html.CssAppliersImpl;
import com.itextpdf.tool.xml.html.HTML;
import com.itextpdf.tool.xml.html.TagProcessorFactory;
import com.itextpdf.tool.xml.html.Tags;
import com.itextpdf.tool.xml.parser.XMLParser;
import com.itextpdf.tool.xml.pipeline.css.CssResolverPipeline;
import com.itextpdf.tool.xml.pipeline.end.PdfWriterPipeline;
import com.itextpdf.tool.xml.pipeline.html.HtmlPipeline;
import com.itextpdf.tool.xml.pipeline.html.HtmlPipelineContext;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import org.apache.poi.poifs.filesystem.DirectoryEntry;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

import java.io.*;
import java.nio.charset.Charset;


public class ReportUtils {

    /**
     *  HTML TO PDF 到指定目录
     * @param html  html内容
     * @param os  PDF文件生成目录
     */
    public static void writeStringToOutputStreamAsPDF(String html, OutputStream os) {
        writeToOutputStreamAsPDF(new ByteArrayInputStream(html.getBytes()), os);
    }

    /**
     * HTML TO PDF 到指定目录
     * @param html  html内容
     * @param os PDF文件生成目录
     */
    public static void writeToOutputStreamAsPDF(InputStream html, OutputStream os) {
        Document document = null;
        try {
            document = new Document(PageSize.A4,36, 36, 36, 36);
            PdfWriter pdfWriter = PdfWriter.getInstance(document, os);
            document.open();
            //BASE64图片处理
            final TagProcessorFactory tagProcessorFactory = Tags.getHtmlTagProcessorFactory();
            tagProcessorFactory.removeProcessor(HTML.Tag.IMG);
            tagProcessorFactory.addProcessor(new ImageTagProcessor(), HTML.Tag.IMG);

            final CssFilesImpl cssFiles = new CssFilesImpl();
            cssFiles.add(XMLWorkerHelper.getInstance().getDefaultCSS());
            final StyleAttrCSSResolver cssResolver = new StyleAttrCSSResolver(cssFiles);
            final HtmlPipelineContext hpc = new HtmlPipelineContext(new CssAppliersImpl(new AsianFontProvider()));
            hpc.setAcceptUnknown(true).autoBookmark(true).setTagFactory(tagProcessorFactory);
            final HtmlPipeline htmlPipeline = new HtmlPipeline(hpc, new PdfWriterPipeline(document, pdfWriter));
            final Pipeline<?> pipeline = new CssResolverPipeline(cssResolver, htmlPipeline);

            final XMLWorker worker = new XMLWorker(pipeline, true);
            final Charset charset = Charset.forName("UTF-8");
            final XMLParser xmlParser = new XMLParser(true, worker, charset);
            xmlParser.parse(html,charset);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                html.close();
                document.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * HTML TO PDF 到指定目录
     * @param html  html内容
     * @param os PDF文件生成目录
     */
    public static void writeStringToOutputStreamAsPDF2(String html, OutputStream os,String fontPath) {
        try {
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.useFastMode();
            //下面这个方法是要自己指定 字体文件   不然转出的pdf文件中 中文会变成####
//            File file = new DefaultResourceLoader().getResource("").getFile();
//            String filePath = file.toString()+File.separator+"fonts"+File.separator+"simhei.ttf";
//
//            ClassPathResource  resource = new ClassPathResource("fonts"+File.separator+"simhei.ttf");
            builder.useFont( new File(fontPath), "simhei");


            //第一个参数是html页面  第二个参数 类似于一个画板 暂时不知道作用 我看示例项目用到
            builder.withHtmlContent(html, "");
            builder.toStream(os);
            builder.run();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     *  HTML TO word 到指定目录
     * @param html  html内容
     * @param os  word文件生成目录
     */
    public static void writeStringToOutputStreamAsWord(String html, OutputStream os) {
        writeToOutputStreamAsWord(new ByteArrayInputStream(html.getBytes()), os);
    }

    /**
     * HTML TO word 到指定目录
     * @param html  html内容
     * @param os word文件生成目录
     */
    public static void writeToOutputStreamAsWord(InputStream html, OutputStream os) {
        POIFSFileSystem poifs = null;
        try {
            poifs = new POIFSFileSystem();
            DirectoryEntry directory = poifs.getRoot();
            directory.createDocument("WordDocument", html);
            poifs.writeFilesystem(os);//当前目录下就生成了一个测试.doc的文档
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                poifs.close();
                html.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     *  报存成html文件 到指定目录
     * @param html  html内容
     * @param os  html文件生成目录
     */
    public static void writeStringToOutputStreamAsHtml(String html, OutputStream os) {
        try {
            os.write(html.getBytes("utf-8"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

