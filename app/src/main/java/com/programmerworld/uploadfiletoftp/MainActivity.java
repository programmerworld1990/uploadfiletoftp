package com.programmerworld.uploadfiletoftp;

import static android.Manifest.permission.READ_MEDIA_IMAGES;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

public class MainActivity extends AppCompatActivity {
    private EditText editTextUserName, editTextPassword;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);

            ActivityCompat.requestPermissions(this,
                    new String[]{READ_MEDIA_IMAGES},
                    PackageManager.PERMISSION_GRANTED);
            editTextPassword = findViewById(R.id.editTextTextPassword);
            editTextUserName = findViewById(R.id.editTextUserName);
            textView = findViewById(R.id.textViewStatus);

            StrictMode.ThreadPolicy threadPolicy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(threadPolicy);

            return insets;
        });
    }

    public void buttonUploadFile(View view){
        StorageManager storageManager = (StorageManager) getSystemService(STORAGE_SERVICE);
        StorageVolume storageVolume = storageManager.getStorageVolumes().get(0); // 0 for internal Storage
        File fileImage = new File(storageVolume.getDirectory().getPath() + "/Download/images.jpg");

        FTPClient ftpClient = new FTPClient();
        try {
            InputStream inputStream = Files.newInputStream(fileImage.toPath());
            ftpClient.connect("192.168.1.1");
            ftpClient.login(editTextUserName.getText().toString(), editTextPassword.getText().toString());
            ftpClient.changeWorkingDirectory("usb1_1/Uploads/");

            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            ftpClient.setFileTransferMode(FTP.BINARY_FILE_TYPE);
            ftpClient.enterLocalPassiveMode();
            ftpClient.sendCommand("OPTS UTF8 ON");

            boolean booleanStatus = ftpClient.storeFile("image_remoteFile.jpg", inputStream);
            textView.setText(String.valueOf("Status - " + booleanStatus));
            inputStream.close();
            ftpClient.logout();
            ftpClient.disconnect();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}