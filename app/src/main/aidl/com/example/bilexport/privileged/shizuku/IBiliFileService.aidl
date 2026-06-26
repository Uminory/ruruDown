// IBiliFileService.aidl
package com.example.bilexport.privileged.shizuku;

interface IBiliFileService {
    String[] listDirectory(String path);
    String readFileContent(String path);
    boolean copyFile(String sourcePath, String destPath);
    boolean fileExists(String path);
    boolean isDirectory(String path);
    long getFileSize(String path);
    long getFileMtime(String path);
    boolean deleteFile(String path);
    boolean createDirectory(String path);
}