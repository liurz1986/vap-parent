package com.vrv.vap.admin.common.util;

/**
 * 处理中文不显示问题
 * Created by lizj on 2020/12/11
 */
import com.itextpdf.text.Font;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.tool.xml.XMLWorkerFontProvider;
import org.springframework.core.io.DefaultResourceLoader;

import java.io.File;


public class AsianFontProvider extends XMLWorkerFontProvider {
    @Override
    public Font getFont(String fontname, String encoding, float size, final int style) {
        try {
            File file = new DefaultResourceLoader().getResource("").getFile();
            //字体文件绝对路径
            String path =file.toString()+File.separator+"fonts"+File.separator+"simhei.ttf";
            BaseFont bfChinese =BaseFont.createFont( path , BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            return new Font(bfChinese, size, style);
        } catch (Exception e) {

        }
        return super.getFont(fontname, encoding, size, style);
    }
}
