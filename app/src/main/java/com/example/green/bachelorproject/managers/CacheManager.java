package com.example.green.bachelorproject.managers;

import android.app.Activity;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.example.green.bachelorproject.internalFileSystem.FileType;
import com.example.green.bachelorproject.internalFileSystem.InternalFile;
import com.example.green.bachelorproject.internalFileSystem.InternalUrl;

/**
 * Created by Green on 16/04/15.
 */
public class CacheManager {
    private Activity activity;
    private File cacheDir;
    private File localFileCache;
    private File dbxFileCache;
    private File temp;
    private List<Boolean> availableNames;

    public CacheManager(Activity activity) {
        this.activity = activity;
        cacheDir = activity.getFilesDir();

        availableNames = new ArrayList<>();
        cacheDir.mkdirs();
        dbxFileCache = new File(cacheDir, FileType.DBX.toString());
        dbxFileCache.mkdirs();
        localFileCache = new File(cacheDir, FileType.LOCAL.toString());
        localFileCache.mkdirs();
        temp = new File(cacheDir, FileType.CACHE.toString());
        temp.mkdirs();
    }

    public void updateCacheFile(String content, InternalFile file) {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(file.getAbsoluteCachePath()));
            bw.write(content);
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String readFromCache(InternalFile file) {
        StringBuilder builder = new StringBuilder();
        String content = null;
        try {
            BufferedReader br = new BufferedReader(new FileReader(file.getAbsoluteCachePath()));
            String line;

            while ((line = br.readLine()) != null) {
                builder.append(line);
                builder.append('\n');
            }

            if(builder.length() != 0) {
                content = builder.substring(0, builder.length()-1);
            }

            br.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return content;
    }

    public InternalFile createCacheFile(InternalUrl url) {
        File cacheFileParent = new File(cacheDir, url.getPathForCache().substring(0, url.getPathForCache().lastIndexOf("/")));
        cacheFileParent.mkdirs();
        File file = new File(cacheFileParent.toString(), url.getFileName());
        return new InternalFile(url, file.getAbsolutePath());
    }

    public InternalFile createNewFile() {
        int count;
        if(availableNames.size() == 0) {
            availableNames.add(true);
            InternalUrl url = new InternalUrl(FileType.CACHE, "Cache", temp.getAbsolutePath() + "/" + "untitled0", false);
            return createCacheFile(url);
        } else {
            count = 0;
            for(boolean b: availableNames) {
                if(!b) {
                    InternalUrl url = new InternalUrl(FileType.CACHE, "Cache", temp.getAbsolutePath() + "/" + "untitled" + Integer.toString(count), false);
                    availableNames.set(count, true);
                    return createCacheFile(url);
                }
                count++;
            }

            InternalUrl url = new InternalUrl(FileType.CACHE, "Cache", temp.getAbsolutePath() + "/" + "untitled" + Integer.toString(count), false);
            availableNames.add(true);
            return createCacheFile(url);
        }
    }

    public void writeToCacheFile(String content, InternalFile file) {
        File cacheFile = new File(file.getAbsoluteCachePath());

        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(cacheFile));
            bw.write(content);
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void deleteFile(InternalFile file) {
        if(file.getType() == FileType.CACHE) {
            int num = Integer.parseInt(file.getFileName().substring(8, file.getFileName().length()));
            availableNames.set(num, false);
        }
        File cacheFile = new File(file.getAbsoluteCachePath());
        cacheFile.delete();
    }
}
