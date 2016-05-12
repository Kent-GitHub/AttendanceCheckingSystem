package com.laughing8.attendancecheckin.utils.qr_code;

import android.graphics.Bitmap;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.util.EnumMap;
import java.util.Map;

/**
 * Created by Laughing8 on 2016/3/19.
 */
public class QRCoderUtils {

    private static int QR_Width=368;
    private static int QR_Height=368;

    public static Bitmap enCode(String source){
        Map<EncodeHintType,Object> hints = new EnumMap<>(EncodeHintType.class);
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
        hints.put(EncodeHintType.MARGIN, 3);
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
        //图像数据转换，使用了矩阵转换
        BitMatrix bitMatrix = null;
        try {
            bitMatrix = new QRCodeWriter().encode(source, BarcodeFormat.QR_CODE, QR_Width, QR_Height, hints);
        } catch (WriterException e) {
            e.printStackTrace();
        }
        int[] pixels = new int[QR_Width * QR_Height];
        //下面这里按照二维码的算法，逐个生成二维码的图片，
        //两个for循环是图片横列扫描的结果
        for (int y = 0; y < QR_Height; y++)
        {
            for (int x = 0; x < QR_Width; x++)
            {
                if (bitMatrix.get(x, y))
                {
                    pixels[y * QR_Width + x] = 0xff000000;
                }
                else
                {
                    pixels[y * QR_Width + x] = 0xffe8e8e8;
                }
            }
        }
        //生成二维码图片的格式，使用ARGB_8888
        Bitmap bitmap = Bitmap.createBitmap(QR_Width, QR_Height, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, QR_Width, 0, 0, QR_Width, QR_Height);
        return bitmap;
    }
}
