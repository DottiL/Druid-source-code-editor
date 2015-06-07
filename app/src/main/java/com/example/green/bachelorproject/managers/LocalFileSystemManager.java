package com.example.green.bachelorproject.managers;

import android.app.Activity;
import android.os.Environment;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import com.example.green.bachelorproject.internalFileSystem.FileType;
import com.example.green.bachelorproject.internalFileSystem.InternalFile;
import com.example.green.bachelorproject.internalFileSystem.InternalUrl;

/**
 * Created by Green on 10/04/15.
 */
public class LocalFileSystemManager {
    private File root;
    private Activity activity;

    public LocalFileSystemManager(Activity activity) {
        this.activity = activity;
        if(isExternalStorageWritable()) {
            this.root = Environment.getExternalStorageDirectory();
        }
    }

    private boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    private boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

    public File getFile(InternalUrl url) {
        String path = url.getOriginalPath();
        File file = new File(url.getOriginalPath());
        return file;
    }

    public File getRoot() {
        return root;
    }

    public String getFileContent(InternalUrl url) {
        File file = new File(root, url.getOriginalPath());
        StringBuilder content = null;

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));

            String line;
            while((line = br.readLine()) != null) {
                content.append(line);
                content.append("\n");

            }
            br.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return content.toString();
    }

    public InternalUrl createFile(InternalFile file, InternalUrl destination) {
        File newFile;
        newFile = new File(destination.getOriginalPath());

        return new InternalUrl(FileType.LOCAL, "Local", newFile.getAbsolutePath(), newFile.isDirectory());
    }

    public void writeToFile(String content, InternalUrl destination) {
        File file = new File(destination.getOriginalPath());
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(file));
            bw.write(content);
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            Toast toast = Toast.makeText(activity, destination.toString(), Toast.LENGTH_SHORT);
            toast.show();
        }
    }
}
