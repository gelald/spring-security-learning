package com.github.gelald.oauth2.utils;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

/**
 * @author WuYingBin
 * date: 2023/5/31
 */

public class VerifyCodeUtils {
    // 定义图形验证码中绘制的字符的字体
    private static final Font mFont = new Font("Arial Black", Font.PLAIN, 23);
    // 图形验证码的大小
    private static final int IMG_WIDTH = 72;
    private static final int IMG_HEIGHT = 27;

    // 获取随机颜色的方法
    private static Color getRandColor(int fc, int bc) {
        Random random = new Random();
        if (fc > 255)
            fc = 255;
        if (bc > 255)
            bc = 255;
        int r = fc + random.nextInt(bc - fc);
        int g = fc + random.nextInt(bc - fc);
        int b = fc + random.nextInt(bc - fc);
        return new Color(r, g, b);
    }

    // 获取随机字符串
    private static String getRandomChar() {
        int rand = (int) Math.round(Math.random() * 2);
        long itmp;
        char ctmp;
        switch (rand) {
            case 1:
                itmp = Math.round(Math.random() * 25 + 65);
                ctmp = (char) itmp;
                return String.valueOf(ctmp);
            case 2:
                itmp = Math.round(Math.random() * 25 + 97);
                ctmp = (char) itmp;
                return String.valueOf(ctmp);
            default:
                itmp = Math.round(Math.random() * 9);
                return itmp + "";
        }
    }

    public static void draw(HttpServletRequest request, HttpServletResponse response) throws Exception {
        // 设置禁止缓存
        response.setHeader("Pragma", "No-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);
        response.setContentType("image/jpeg");
        BufferedImage image = new BufferedImage(IMG_WIDTH, IMG_HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics graphics = image.getGraphics();
        Random random = new Random();
        graphics.setColor(getRandColor(200, 250));
        // 填充背景色
        graphics.fillRect(1, 1, IMG_WIDTH - 1, IMG_HEIGHT - 1);
        // 为图形验证码绘制边框
        graphics.setColor(new Color(102, 102, 102));
        graphics.drawRect(0, 0, IMG_WIDTH, IMG_HEIGHT);
        graphics.setColor(getRandColor(160, 200));
        // 生成随机干扰线
        for (int i = 0; i < 80; i++) {
            int x = random.nextInt(IMG_WIDTH - 1);
            int y = random.nextInt(IMG_HEIGHT - 1);
            int x1 = random.nextInt(6) + 1;
            int y1 = random.nextInt(12) + 1;
            graphics.drawLine(x, y, x + x1, y + y1);
        }
        graphics.setColor(getRandColor(160, 200));
        // 生成随机干扰线
        for (int i = 0; i < 80; i++) {
            int x = random.nextInt(IMG_WIDTH - 1);
            int y = random.nextInt(IMG_HEIGHT - 1);
            int x1 = random.nextInt(12) + 1;
            int y1 = random.nextInt(6) + 1;
            graphics.drawLine(x, y, x - x1, y - y1);
        }
        // 设置绘制字符的字体
        graphics.setFont(mFont);
        // 用于保存系统生成的随机字符串
        StringBuilder sRand = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            String tmp = getRandomChar();
            sRand.append(tmp);
            graphics.setColor(new Color(20 + random.nextInt(110), 20 + random.nextInt(110), 20 + random.nextInt(110)));
            graphics.drawString(tmp, 15 * i + 10, 20);
        }
        // 获取HttpSession对象
        HttpSession session = request.getSession(true);
        session.removeAttribute("code");
        session.setAttribute("code", sRand.toString());
        graphics.dispose();
        // 向输出流中输出图片
        ImageIO.write(image, "JPEG", response.getOutputStream());
    }
}