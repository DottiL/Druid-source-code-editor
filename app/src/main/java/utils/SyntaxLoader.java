package utils;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import com.example.green.bachelorproject.internalFileSystem.InternalUrl;
import com.example.green.bachelorproject.managers.DropboxManager;
import com.example.green.bachelorproject.managers.LocalFileSystemManager;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by Green on 07/05/15.
 */
public class SyntaxLoader {
    private DropboxManager dropboxManager;
    private LocalFileSystemManager localFileSystemManager;
    private List<Syntax> syntaxList;
    private File appRoot;
    private File syntaxFolder;
    private Activity activity;

    public SyntaxLoader(Activity activity, DropboxManager dropboxManager, LocalFileSystemManager localFileSystemManager, File appRoot) {
        this.activity = activity;
        this.dropboxManager = dropboxManager;
        this.localFileSystemManager = localFileSystemManager;
        this.syntaxList = new ArrayList<>();
        this.appRoot = appRoot;
        this.syntaxFolder = new File(appRoot, "syntax");
        syntaxFolder.mkdirs();
        loadSyntax();

    }

    public List<Syntax> getSyntaxList() {
        return syntaxList;
    }

    public void loadSyntax() {
        List<File> files = new ArrayList<>(Arrays.asList(syntaxFolder.listFiles()));

        for(File f: files) {
            syntaxList.add(createSyntax(getSyntaxFileContent(f)));
        }
    }

    public List<String> getSyntaxFileContent(File file) {
        StringBuilder content = new StringBuilder();
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

        return new ArrayList<String>(Arrays.asList(content.toString().split("\n")));
    }

    private boolean contains(String name) {
        String fileName = name.substring(0, name.lastIndexOf('.'));

        for(Syntax s: syntaxList) {
            if(fileName.equals(s.getLanguageName())) {

            }
        }
        return false;
    }

    public boolean hasValidExtension(String fileName) {
        String extension = fileName.substring(fileName.lastIndexOf('.'), fileName.length());
        return extension.equals(".txt");
    }

    public void addSyntax(InternalUrl url) {

        if(hasValidExtension(url.getFileName())) {
            if(true) {
                boolean found = false;
                String content = null;
                switch (url.getType()) {
                    case DBX:
                        content = dropboxManager.getFileContent(url);
                        break;
                    case LOCAL:
                        content = localFileSystemManager.getFileContent(url);
                        break;
                }

                List<String> contentLines = new ArrayList<String>(Arrays.asList(content.split("\n")));

                Syntax syntax = createSyntax(contentLines);



                syntaxList.add(syntax);
                for(File ss: syntaxFolder.listFiles()) {
                    if(ss.getName().equals(syntax.getLanguageName() + ".txt")) {
                        ss.delete();
                        Iterator<Syntax> it = syntaxList.iterator();
                        while(it.hasNext()) {
                            Syntax syn = (Syntax) it.next();
                            if (syn.getLanguageName().equals(syntax.getLanguageName())) {
                                it.remove();
                                found = true;
                            }
                        }
                    }
                }

                if(found) {
                    Toast.makeText(activity, "Syntax file updated for language " + syntax.getLanguageName(), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(activity, "Added new syntax file for language " + syntax.getLanguageName(), Toast.LENGTH_SHORT).show();
                }
                syntaxList.add(syntax);
                File s = new File(syntaxFolder, syntax.getLanguageName() + ".txt");



                try {
                    BufferedWriter bf = new BufferedWriter(new FileWriter(s));
                    bf.write(content);
                    bf.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        } else {
            Toast.makeText(activity, "Wrong file extension! All syntax files must have .txt extension", Toast.LENGTH_SHORT).show();
        }


    }

    private Syntax createSyntax(List<String> contentLines) {
        String languageName = null;
        String fileExtension = null;
        boolean multiLineComment = false;
        String multiLineCommentStart = null;
        String multiLineCommentEnd = null;
        String singleLineComment = null;
        List<String> operatorsList = null;
        List<String> keywordsList = null;
        List<String> primitiveTypesList = null;
        List<String> stringDelimiters = null;
        HashMap<String,String> statements = new HashMap<>();
        List<String> statementsNames;

        String val;

        for(int i = 0; i<contentLines.size(); i++) {
            String s = contentLines.get(i);
            if(!s.startsWith("//") && s.length() != 0) {
                String element = getElement(s);
                if(element.equals("LANGUAGE_NAME")) {
                    languageName = getValue(s);
                } else if(element.equals("FILE_EXTENSION")) {
                    fileExtension = getValue(s);
                } else if(element.equals("OPERATORS")) {
                    val = getValue(s);
                    operatorsList = new ArrayList<>(Arrays.asList(val.split(",")));
                } else if(element.equals("KEYWORDS")) {
                    val = getValue(s);
                    keywordsList = new ArrayList<>(Arrays.asList(val.split(",")));
                } else if(element.equals("PRIMITIVE_TYPES")) {
                    val = getValue(s);
                    primitiveTypesList = new ArrayList<>(Arrays.asList(val.split(",")));
                } else if(element.equals("MULTILINE_COMMENT")) {
                    multiLineComment = Boolean.parseBoolean(getValue(s));
                } else if(element.equals("START_MULTILINE_COMMENT")) {
                    multiLineCommentStart = getValue(s);
                } else if(element.equals("END_MULTILINE_COMMENT")) {
                    multiLineCommentEnd = getValue(s);
                } else if(element.equals("SINGLE_LINE_COMMENT")) {
                    singleLineComment = getValue(s);
                } else if(element.equals("STRING_DELIMITERS")) {
                    val = getValue(s);
                    stringDelimiters = new ArrayList<>(Arrays.asList(val.split(",")));
                } else if(element.equals("STATEMENTS")) {
                    val = getValue(s);
                    statementsNames = new ArrayList<>(Arrays.asList(val.split(",")));
                    int count = 0;
                    String buffer = "";
                    for(int j = i+2; j<contentLines.size(); j++) {

                        String ss = contentLines.get(j);

                        if(ss.startsWith("-")) {
                            buffer = buffer.substring(1);
                            statements.put(statementsNames.get(count), buffer);
                            count++;
                            buffer = "";
                        } else {
                            buffer += "\n"+ss;
                        }

                    }
                    break;
                }
            }
        }
        return new Syntax(languageName, fileExtension, operatorsList, keywordsList, primitiveTypesList, stringDelimiters, multiLineComment, multiLineCommentStart, multiLineCommentEnd, singleLineComment, statements);
    }

    private String getElement(String line) {
        return line.substring(0, line.indexOf(":")-1);
    }

    private String getValue(String line) {
        return line.substring(line.indexOf(":")+2, line.length());
    }

    public Syntax getSyntaxByExtension(String extension) {
        for(Syntax s: syntaxList) {
            if(s.getFileExtension().equals(extension)) {
                return s;
            }
        }
        return null;
    }
}
