package com.chat.demochat.util;

import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.model.enums.AesKeyStrength;
import net.lingala.zip4j.model.enums.CompressionLevel;
import net.lingala.zip4j.model.enums.CompressionMethod;
import net.lingala.zip4j.model.enums.EncryptionMethod;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class ZipUtil
{
    /**
     * 压缩包加密
     *
     * @param inputStream 文件流
     * @param targetPath  压缩后的目标路径
     * @param password    压缩密码
     * @throws ZipException
     */
    public static void compressedFileWithPassword(InputStream inputStream, String originalName, String targetPath, String password) throws IOException
    {
        ZipFile zipFile = new ZipFile(targetPath, password.toCharArray());
        ZipParameters parameters = new ZipParameters();
        parameters.setCompressionMethod(CompressionMethod.DEFLATE);
        parameters.setCompressionLevel(CompressionLevel.NORMAL);
        parameters.setEncryptionMethod(EncryptionMethod.AES);
        parameters.setAesKeyStrength(AesKeyStrength.KEY_STRENGTH_128);
        parameters.setEncryptFiles(true);
        parameters.setFileNameInZip(originalName);
        zipFile.setPassword(password.toCharArray());
        zipFile.addStream(inputStream, parameters);
        zipFile.close();
    }

    public static void main(String[] args) throws IOException
    {
        compressedFileWithPassword(new FileInputStream("D:/bbbb.mp4"), "bbbb.mp4", "D:/bbbb.zip", "123456");
    }
}
