package com.paohaijiao.javelin.echarts;
import org.apache.batik.dom.GenericDOMImplementation;
import org.apache.batik.svggen.SVGGraphics2D;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;

import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
public class Main {
        public static void main(String[] args) throws Exception {
            DOMImplementation domImpl = GenericDOMImplementation.getDOMImplementation();
            Document document = domImpl.createDocument("http://www.w3.org/2000/svg", "svg", null);
            SVGGraphics2D svgGenerator = new SVGGraphics2D(document);
            svgGenerator.drawString("Java生成的模拟图表", 50, 20);
            int[] data = {120, 200, 150, 80, 70, 110, 130};
            String[] labels = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
            for (int i = 0; i < data.length; i++) {
                int x = 50 + i * 40;
                int height = data[i] / 2;
                svgGenerator.fillRect(x, 150 - height, 30, height);
                svgGenerator.drawString(labels[i], x, 170);
            }

            // 输出SVG文件
            try (Writer out = new OutputStreamWriter(new FileOutputStream("d://test/java_chart.svg"), "UTF-8")) {
                svgGenerator.stream(out, true);
            }

            System.out.println("SVG图表生成成功！");
        }
}
