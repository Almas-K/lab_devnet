package kz.iitu.filemanager;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.security.crypto.EncryptedFile;
import androidx.security.crypto.MasterKeys;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.security.keystore.KeyGenParameterSpec;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MainActivity extends AppCompatActivity {
    TextView textView;
    String testFile = "my_encrypted_data.txt";
    TextView textView2;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = findViewById(R.id.editText);
        textView2 = findViewById(R.id.readInfo);
        context = getApplicationContext();
    }

    public void ReadFile(View view) {
        KeyGenParameterSpec keyGenParameterSpec = MasterKeys.AES256_GCM_SPEC;
        try {
            String mainKeyAlias = MasterKeys.getOrCreate(keyGenParameterSpec);
            EncryptedFile encryptedFile = new EncryptedFile.Builder(
                    new File(context.getFilesDir(), testFile),
                    context,
                    mainKeyAlias, EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
            ).build();
            InputStream inputStream = encryptedFile.openFileInput();
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            int nextByte = inputStream.read();
            while (nextByte != -1) {
                byteArrayOutputStream.write(nextByte);
                nextByte = inputStream.read();
            }
            byte[] plaintext = byteArrayOutputStream.toByteArray();
            textView2.setText("Decrypted info " + new String(plaintext, StandardCharsets.UTF_8));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void WriteToFile(View view) {
        // Although you can define your own key generation parameter specification, it's // recommended that you use the value specified here.
        KeyGenParameterSpec keyGenParameterSpec = MasterKeys.AES256_GCM_SPEC;
        try {
            String mainKeyAlias = MasterKeys.getOrCreate(keyGenParameterSpec);
            // Create a file with this name, or replace an entire existing file
            // that has the same name. Note that you cannot append to an existing file,
            // and the file name cannot contain path separators.

            File fileEx = context.getFileStreamPath(testFile);
            if (fileEx.exists()) {
                System.out.println("File deleted" +fileEx.delete());
            }
            File file = new File(context.getFilesDir(), testFile);
            EncryptedFile encryptedFile = new EncryptedFile.Builder(
                    file,
                    context,
                    mainKeyAlias, EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB).build();

            byte[] fileContent = textView.getText().toString().getBytes(StandardCharsets.UTF_8);
            OutputStream outputStream = encryptedFile.openFileOutput();
            outputStream.write(fileContent);
            outputStream.flush();
            outputStream.close();

            String string = "";
            StringBuilder stringBuilder = new StringBuilder();
            InputStream is = new FileInputStream(context.getFilesDir() + "/" + testFile);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            while (true) {
                try {
                    if ((string = reader.readLine()) == null) break;
                } catch (IOException e) {
                    e.printStackTrace();
                }
                stringBuilder.append(string).append("\n");
                textView2.setText("Encrypted info " + stringBuilder);
                textView.setText("");
            }
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Toast.makeText(getBaseContext(), stringBuilder.toString(),
                    Toast.LENGTH_LONG).show();


        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void GenerateMessage(View view){
        try{
            byte[] message = "My text: isns1906".getBytes(StandardCharsets.UTF_8);
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(message);
            AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(this);
            dlgAlert.setMessage(digest.toString());
            dlgAlert.setTitle("App Title");
            dlgAlert.setPositiveButton("OK", null);
            dlgAlert.setCancelable(true);
            dlgAlert.create().show();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

}