package com.baidu.idl.face.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class Hex {

    public static void main(String[] args) {
        String hexString = "7A C1 EE 90 36 37 DF 15 13 4D 9F 99 4F A6 C7 1C 98 36 C7 02 4C 02 59 05 47 26 03 89 7F 14 4D 08 DA DD CD B0 8A E5 BF B5 6E 1B 4A 39 22 83 33 3D 8C 1B 1E A1 21 06 89 A4 5B DF FF 28 AB 35 1F AE 48 5C D6 D0 F6 6A CE 57 39 6E 96 D9 D8 B5 67 5C 3C 72 9A 40 2F E6 AC C5 F3 07 FC 48 67 D3 0C CD 7B 70 6C F2 D4 61 BE F5 2F 02 5A F9 B7 DA 06 FD 46 16 3A E1 62 FF 08 E5 8C 83 5C 69 42 88 06 6D 17 68 60 10 EA 91 F6 16 B8 80 1F 18 E2 AD 99 1C 9F 64 6B 81 AB 3D 59 04 CD D6 DD 88 BC 2F A1 8D 64 2F 0E B0 44 85 85 34 31 50 22 B8 35 7F 0C BC FE BA 87 20 9E 30 92 24 4A 14 5B AA 3F 8B 58 AE 2B 82 C1 D1 0C 0F E2 D6 72 FB 52 58 6B B2 B2 5E F3 67 BF C1 BA 4A A1 45 0A 79 40 48 15 88 FB CC DD C2 C7 71 3F F4 EE 74 89 E3 47 F9 4E CD 2D FC EB 76 48 E0 4A CC 04 64 EF 2B 5B 68 E2 59 E6 6D AE A1 4C 10 79 F0 54 15 A0 26 0A 9A 8A 2E 9E 9C 05 C1 BD 81 06 D1 5E 06 33 2D 16 88 32 21 7F 8D DB A1 9C B1 FD 81 ED B4 89 6A 64 3A 61 FE 81 BC 13 61 72 A3 75 A2 8A A5 96 C6 72 A9 12 48 42 2D 5E 3B 2B 51 BA D6 6C D4 08 87 74 D8 24 23 DC DC BC D3 44 40 ED 4F 58 C4 C6 E0 C4 C8 C9 F3 65 4E 35 60 84 72 EA 5C 23 74 47 1C 7B FA 51 CB 8C 7C C1 C1 26 60 CA 78 68 66 85 A9 A7 E8 A4 F2 0C 6E 91 1E B8 10 65 2B EA 15 BB 5C 80 99 2A 31 3F 1E AA 5E 81 80 A3 83 3B 84 23 50 05 88 16 77 78 0C BF 89 B2 31 82 42 1B B4 4D 5B 2F B8 90 46 39 BC 62 58 8D A0 C8 BA 96 A5 62 F8 6A A8 ED 51 B2 AF 5A 57 5A D0 C1 F8 13 54 B2 F7 5D 58 25 E1 E3 5C 47 35 D8 C2 EE A7 73 C4 8A 39 F7 C9 15 7B F4 CC 56 28 C4 F0 72 43 8B 74 77 08 5F 79 42 54 DA 7D 7B 51 36 61 3B 4D A1 E5 FA C5 D1 E8 E4 31 BE 6D";
        byte[] data = Hex.toBytes(hexString);
//        byte[] data = fileToBytes("D://Android/Document/HandHygiene/2023077/face.dat");
        System.out.println("length:"+data.length);
        System.out.println(Hex.toHex(data));
    }

    public static byte[] fileToBytes(String path){
        File file = new File(path);
        try (FileInputStream fis = new FileInputStream(file);
             ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                bos.write(buffer, 0, bytesRead);
            }
            return bos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 字节转16进制字符串
     *
     * @param data 字节数组
     * @return
     */
    public static String toHex(byte[] data) {
        StringBuilder builder = new StringBuilder();
        for (byte bt : data) {
            builder.append(String.format("%02X ", bt));
        }
        return builder.toString();
    }

    /**
     * 字节转16进制字符串
     *
     * @param data    字节数组
     * @param divided 是否划分
     * @return
     */
    public static String toHex(byte[] data, boolean divided) {
        StringBuilder builder = new StringBuilder();
        for (byte bt : data) {
            builder.append(String.format(divided?"%02X ":"%02X", bt));
        }
        return builder.toString();
    }


    /**
     * 16进制字符串转字符串
     *
     * @param hexString 16进制字符
     * @return
     */
    public static byte[] toBytes(String hexString) {
        hexString = hexString.replace(" ", "");
        byte[] bytes = new byte[hexString.length() / 2];
        for (int i = 0; i < hexString.length(); i += 2) {
            bytes[i / 2] = (byte) Integer.parseInt(hexString.substring(i, i + 2), 16);
        }
        return bytes;
    }

}
