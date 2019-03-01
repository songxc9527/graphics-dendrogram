package com.hlhlo.law.utils;

import com.hlhlo.law.dto.ShareHolderDto;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class Dendrogram {

    /**
     * 生成树图写到磁盘
     * @param picType 图片类型JPG GIF JPEG PNG
     * @param file 图片文件
     * @param list 数据模型 [{name: '', children: [{name: '', children: [{}]}]}]
     * @param rootName 根名称
     * @return
     */
    public static boolean writeImage(String picType, File file, List<ShareHolderDto> list, String rootName) {
        BufferedImage bimg = new BufferedImage(2002, 2002, BufferedImage.TYPE_INT_BGR);
        // 拿到画笔
        Graphics2D g = bimg.createGraphics();
        // 画一个图形系统默认是白色
        int imgWidth = 2000;
        int imgHeight = 2000;
        g.fillRect(1, 1, imgWidth, imgHeight);
        // 设置画笔颜色
        g.setColor(new Color(12, 123, 88));
        int fontSize = 14;
        // 设置画笔画出的字体风格
        g.setFont(new Font("隶书", Font.ITALIC, fontSize));
        g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_DEFAULT);
        //消除文字锯齿
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        //消除画图锯齿
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        // 写一个字符串
        int margin = 60;
        int parentY = imgHeight / 2;
        g.drawString(rootName, fontSize, parentY);
        int parentX = computeParentX(rootName, fontSize, fontSize);
        int heightL2 = (imgHeight - fontSize) / list.size();
        for (int i = 0; i < list.size(); i++) {
            ShareHolderDto shareHolderDto = list.get(i);
            int height = heightL2 / 2;
            String name = shareHolderDto.getName();
            g.drawString(name, parentX + margin, height + heightL2 * i);
            // TODO 这里可以写其他属性， y随属性个数增加而增加 fontSize * i
            g.drawString("注册资本：" + (shareHolderDto.getRegCapital() == null?"-":shareHolderDto.getRegCapital()), parentX + margin, height + heightL2 * i + fontSize);
            g.drawLine(parentX, parentY, parentX + margin, height + heightL2 * i);
            if (shareHolderDto.getChildren() != null && !shareHolderDto.getChildren().isEmpty()) {
                int myX = computeParentX(shareHolderDto.getName(), parentX + margin, fontSize);
                drawChildrenTransverse(g, shareHolderDto.getChildren(), height + heightL2 * i, heightL2, heightL2 * i, fontSize, margin, myX);
            }
        }
        // 释放画笔
        g.dispose();
        // 将画好的图片通过流形式写到硬盘上
        boolean val = false;
        try {
            val = ImageIO.write(bimg, picType, file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return val;
    }

    /**
     * 循环子树
     * @param g Graphics2D
     * @param children 子节点
     * @param parentY 父节点的Y坐标
     * @param parentHeight 父区域的高度
     * @param startY 父区域起始Y坐标
     * @param fontSize 字体大小
     * @param margin 兄弟节点的间距
     * @param parentX 父节点的X坐标
     */
    private static void drawChildrenTransverse(Graphics2D g, List<ShareHolderDto> children, int parentY, int parentHeight, int startY, int fontSize, int margin, int parentX) {
        int heightLn = parentHeight / children.size();
        for (int i = 0; i < children.size(); i++) {
            ShareHolderDto shareHolderDto = children.get(i);
            int y = heightLn / 2 + heightLn * i + startY;
            int x = parentX + margin;
            String name = shareHolderDto.getName();
            g.drawString(name, x , y);
            g.drawLine(parentX, parentY, x, y);
            if (shareHolderDto.getChildren() != null && !shareHolderDto.getChildren().isEmpty()) {
                int myX = computeParentX(shareHolderDto.getName(), x, fontSize);
                int myStartY = heightLn * i + startY;
                drawChildrenTransverse(g, shareHolderDto.getChildren(),y, heightLn, myStartY, fontSize, margin, myX);
            }
        }
    }

    /**
     * 计算父节点名字末尾X坐标
     * @param str 文本
     * @param patentX 父节点起点X
     * @param fontSize 字体大小
     * @return
     */
    private static int computeParentX(String str, int patentX, int fontSize) {
        return patentX + str.length() * fontSize;
    }

}