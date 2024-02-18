package com.baidu.idl.face.utils;

import android.graphics.Bitmap;

import com.baidu.idl.main.facesdk.model.BDFaceImageInstance;
import com.baidu.idl.main.facesdk.model.BDFaceSDKCommon;

import java.nio.ByteBuffer;

/**
 * 位图转换器
 */
public class FaceBitmap {

    public static Bitmap getBDFaceImageInstance(BDFaceImageInstance newInstance) {
        Bitmap transBmp = null;
        if (newInstance.imageType == BDFaceSDKCommon.BDFaceImageType.BDFACE_IMAGE_TYPE_RGBA) {
            transBmp = Bitmap.createBitmap(newInstance.width, newInstance.height, Bitmap.Config.ARGB_8888);
            transBmp.copyPixelsFromBuffer(ByteBuffer.wrap(newInstance.data));
        } else if (newInstance.imageType == BDFaceSDKCommon.BDFaceImageType.BDFACE_IMAGE_TYPE_BGR) {
            transBmp = FaceBitmap.rgb2Bitmap(newInstance.data, newInstance.width, newInstance.height);
        } else if (newInstance.imageType == BDFaceSDKCommon.BDFaceImageType.BDFACE_IMAGE_TYPE_YUV_NV21) {
            transBmp = FaceBitmap.yuv2Bitmap(newInstance.data, newInstance.width, newInstance.height);
        } else if (newInstance.imageType == BDFaceSDKCommon.BDFaceImageType.BDFACE_IMAGE_TYPE_GRAY) {
            transBmp = depth2Bitmap(newInstance.data, newInstance.width, newInstance.height);
        }
        return transBmp;
    }

    public static Bitmap rgb2Bitmap(byte[] bytes, int width, int height) {
        // use Bitmap.Config.ARGB_8888 instead of type is OK
        Bitmap stitchBmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        byte[] rgba = new byte[width * height * 4];
        for (int i = 0; i < width * height; i++) {
            byte b1 = bytes[i * 3 + 0];
            byte b2 = bytes[i * 3 + 1];
            byte b3 = bytes[i * 3 + 2];
            // set value
            rgba[i * 4 + 0] = b3;
            rgba[i * 4 + 1] = b2;
            rgba[i * 4 + 2] = b1;
            rgba[i * 4 + 3] = (byte) 255;
        }
        stitchBmp.copyPixelsFromBuffer(ByteBuffer.wrap(rgba));
        return stitchBmp;
    }

    public static Bitmap yuv2Bitmap(byte[] data, int width, int height) {
        int frameSize = width * height;
        int[] rgba = new int[frameSize];

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int y = (0xff & ((int) data[i * width + j]));
                int u = (0xff & ((int) data[frameSize + (i >> 1) * width + (j & ~1) + 0]));
                int v = (0xff & ((int) data[frameSize + (i >> 1) * width + (j & ~1) + 1]));
                y = y < 16 ? 16 : y;

                int r = Math.round(1.164f * (y - 16) + 1.596f * (v - 128));
                int g = Math.round(1.164f * (y - 16) - 0.813f * (v - 128) - 0.391f * (u - 128));
                int b = Math.round(1.164f * (y - 16) + 2.018f * (u - 128));

                r = r < 0 ? 0 : (r > 255 ? 255 : r);
                g = g < 0 ? 0 : (g > 255 ? 255 : g);
                b = b < 0 ? 0 : (b > 255 ? 255 : b);

                rgba[i * width + j] = 0xff000000 + (b << 16) + (g << 8) + r;
            }
        }

        Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bmp.setPixels(rgba, 0, width, 0, 0, width, height);
        return bmp;
    }

    public static Bitmap depth2Bitmap(byte[] depthBytes, int width, int height) {
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        int[] argbData = new int[width * height];
        for (int i = 0; i < width * height; i++) {
            argbData[i] = (((int) depthBytes[i * 2] + depthBytes[i * 2 + 1] * 256) / 10 & 0x000000ff)
                    | ((((int) depthBytes[i * 2] + depthBytes[i * 2 + 1] * 256) / 10) & 0x000000ff) << 8
                    | ((((int) depthBytes[i * 2] + depthBytes[i * 2 + 1] * 256) / 10) & 0x000000ff) << 16
                    | 0xff000000;

        }
        bitmap.setPixels(argbData, 0, width, 0, 0, width, height);
        return bitmap;
    }
}
