package com.example.pppppppp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.text.TextUtils;
import android.view.View;

import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.pdfbox.multipdf.Splitter;
import org.apache.pdfbox.pdmodel.PDDocument;


public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE = 1;
    private EditText startPageNumberEditText;
    private EditText endPageNumberEditText;
    private TextView filePathTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button selectFileButton = findViewById(R.id.select_file_button);
        Button splitPdfButton = findViewById(R.id.split_pdf_button);
        startPageNumberEditText = findViewById(R.id.start_page_number_edit_text);
        endPageNumberEditText = findViewById(R.id.end_page_number_edit_text);
        filePathTextView = findViewById(R.id.file_path_text_view);

        selectFileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("application/pdf");
                startActivityForResult(Intent.createChooser(intent, "Select a PDF file"), REQUEST_CODE);
            }
        });

        splitPdfButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int startPageNumber = Integer.parseInt(startPageNumberEditText.getText().toString());
                int endPageNumber = Integer.parseInt(endPageNumberEditText.getText().toString());
                String filePath = filePathTextView.getText().toString();

                // Validate the user input
                if (TextUtils.isEmpty(filePath)) {
                    Toast.makeText(MainActivity.this, "Please select a PDF file", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (startPageNumber <= 0 || endPageNumber <= 0 || startPageNumber > endPageNumber) {
                    Toast.makeText(MainActivity.this, "Please enter valid page numbers", Toast.LENGTH_SHORT).show();
                    return;
                }

                try {
                    // Load the PDF document
                    PDDocument document = PDDocument.load(new File(filePath));

                    // Split the PDF document
                    Splitter splitter = new Splitter();
                    splitter.setStartPage(startPageNumber);
                    splitter.setEndPage(endPageNumber);
                    List<PDDocument> pages = splitter.split(document);

                    // Save the split PDF pages to separate files
                    String fileName = filePath.substring(filePath.lastIndexOf("/") + 1);
                    fileName = fileName.substring(0, fileName.lastIndexOf("."));
                    for (int i = 0; i < pages.size(); i++) {
                        PDDocument page = pages.get(i);
                        String pageFileName = fileName + "_page" + (i + 1) + ".pdf";
                        page.save(pageFileName);
                        page.close();
                    }

                    // Close the PDF document
                    document.close();

                    Toast.makeText(MainActivity.this, "PDF file has been split successfully", Toast.LENGTH_SHORT).show();

                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this, "Error occurred while splitting the PDF file", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri fileUri = data.getData();
            filePathTextView.setText(fileUri.getPath());
        }
    }
}
