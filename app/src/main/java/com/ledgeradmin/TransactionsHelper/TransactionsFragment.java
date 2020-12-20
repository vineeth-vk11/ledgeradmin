package com.ledgeradmin.TransactionsHelper;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.ledgeradmin.R;
import com.ledgeradmin.SalesHelper.AddSalesDialog;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;
import java.util.SortedSet;

public class  TransactionsFragment extends Fragment implements SortTransactionsDialog.OnDatesSelected, AddTransactionDialog.OnWhatsAppChecked {

    RecyclerView transactions;
    FirebaseFirestore db;
    ArrayList<TransactionsModel> transactionsModelArrayList;

    String company;
    String sales;
    String id;

    ImageButton imageButton;
    ImageButton sort;
    ImageButton download;
    ImageButton share;
    ImageButton logout;

    SearchView searchView;
    TransactionsAdapter transactionsAdapter;

    String startDateExisting;
    String endDateExisting;

    Date startDateD;
    Date endDateD;
    Date dateD;

    String creditAmount;
    String debitAmount;

    int currentDebit;
    int currentCredit;
    int openingCreditAmount;
    int openingDebitAmount;
    int openingBalance;
    int outStandingAmount;

    TextView currentC;
    TextView currentD;
    TextView outstanding;
    TextView outStandingCredit;
    TextView openingCredit;
    TextView openingDebit;

    ProgressBar progressBar;

    ImageView imageView;

    String name, address, number, osLimit;

    String initialDatePdf;
    String finalDatePdf;

    TextView toAndFrom;

    TextView toolbarText;

    TextView alert;
    ImageView alertImage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_transactions, container, false);

        Bundle bundle = getArguments();
        company = bundle.getString("company");
        sales = bundle.getString("sales");
        id = bundle.getString("userId");
        number = bundle.getString("number");
        address = bundle.getString("address");
        name = bundle.getString("name");
        osLimit = bundle.getString("osLimit");

        imageButton = getActivity().findViewById(R.id.add_transaction);
        imageButton.setVisibility(View.VISIBLE);

        toolbarText = getActivity().findViewById(R.id.toolbar);
        toolbarText.setText(name);
        toolbarText.setTextSize(14);

        sort = getActivity().findViewById(R.id.sort);
        download = getActivity().findViewById(R.id.download);
        share = getActivity().findViewById(R.id.share);
        logout = getActivity().findViewById(R.id.logout);
        toAndFrom = view.findViewById(R.id.toAndFrom);

        alert = view.findViewById(R.id.alert);
        currentC = view.findViewById(R.id.currentCredit);
        currentD = view.findViewById(R.id.currentDebit);
        outstanding = view.findViewById(R.id.outStandingTotal);
        openingDebit = view.findViewById(R.id.openingBalanceDebit);
        openingCredit = view.findViewById(R.id.openingBalanceCredit);
        outStandingCredit = view.findViewById(R.id.outStandingCredit);
        progressBar = view.findViewById(R.id.progressBar5);
        imageView = view.findViewById(R.id.empty);
        alertImage = view.findViewById(R.id.imageView4);

        sort.setVisibility(View.VISIBLE);
        download.setVisibility(View.VISIBLE);
        share.setVisibility(View.VISIBLE);
        logout.setVisibility(View.INVISIBLE);

        Log.i("company",company);
        Log.i("sales",sales);
        Log.i("userId",id);

        transactions = view.findViewById(R.id.transactions_recycler);
        db = FirebaseFirestore.getInstance();
        transactionsModelArrayList = new ArrayList<>();

        transactions.setLayoutManager(new LinearLayoutManager(getContext()));
        transactions.setHasFixedSize(true);

        Dexter.withActivity(getActivity())
                .withPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        download.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                            }
                        });
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {

                    }
                })
                .check();

        sort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SortTransactionsDialog sortTransactionsDialog = new SortTransactionsDialog(startDateExisting,endDateExisting);
                sortTransactionsDialog.setTargetFragment(TransactionsFragment.this, 1);
                sortTransactionsDialog.show(getActivity().getSupportFragmentManager(), "Sort transactions");
            }
        });

        db.collection("Companies").document(company).collection("sales").document(sales).collection("dealers")
                .document(id).collection("transactions").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                getTransactions();
            }
        });

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddTransactionDialog addTransactionDialog = new AddTransactionDialog(company,sales,id,null,null,null,null,null, null);
                addTransactionDialog.setTargetFragment(TransactionsFragment.this, 1);
                addTransactionDialog.show(getActivity().getSupportFragmentManager(), "Add Transaction Dialog");
            }
        });

        searchView = view.findViewById(R.id.searchView);
        searchView.setVisibility(View.GONE);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                transactionsAdapter.getFilter().filter(newText);
                return false;
            }
        });

        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dexter.withActivity(getActivity())
                        .withPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        .withListener(new PermissionListener() {
                            @Override
                            public void onPermissionGranted(PermissionGrantedResponse response) {
                                try {
                                    createPDFFile( name, address, initialDatePdf, finalDatePdf);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onPermissionDenied(PermissionDeniedResponse response) {
                                Toast.makeText(getContext(),"you have not given the permissions",Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {

                            }
                        })
                        .check();
            }
        });

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Dexter.withActivity(getActivity())
                        .withPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        .withListener(new PermissionListener() {
                            @Override
                            public void onPermissionGranted(PermissionGrantedResponse response) {
                                File file = null;

                                try {
                                    file = createPDFFileAndShare(name, address, initialDatePdf, finalDatePdf);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                                Uri uri = FileProvider.getUriForFile(getActivity(), getActivity().getApplicationContext().getPackageName() + ".fileprovider", file);
                                Intent share = new Intent();
                                share.setAction(Intent.ACTION_SEND);
                                share.setType("application/pdf");
                                share.putExtra(Intent.EXTRA_STREAM, uri);
                                share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                                startActivity(Intent.createChooser(share, "Share"));
                            }

                            @Override
                            public void onPermissionDenied(PermissionDeniedResponse response) {
                                Toast.makeText(getContext(),"you have not given the permissions",Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {

                            }
                        })
                        .check();

            }
        });

        return view;
    }

    private void createPDFFile(String name, String address, String initialDate, String finalDate) throws IOException {

        String fileName = name + initialDate + "-" + finalDate+".pdf";

        String path = Environment.getExternalStorageDirectory() + File.separator + "LedgerAdmin.pdf";
        File file = new File(path);

        if(!file.exists()){
            file.createNewFile();
        }

        try{
            Document document = new Document();

            PdfWriter.getInstance(document, new FileOutputStream(path));

            document.open();

            BaseFont fontName = BaseFont.createFont("assets/fonts/Roboto-Black.ttf","UTF-8" , BaseFont.EMBEDDED);
            BaseFont font1 = BaseFont.createFont("assets/fonts/Roboto-Regular.ttf","UTF-8" , BaseFont.EMBEDDED);

            Font titleFont = new Font(fontName, 18.0f, Font.NORMAL, BaseColor.BLACK);
            Font contactFont = new Font(font1, 12.0f, Font.NORMAL, BaseColor.BLACK);
            Font dealerFont = new Font(fontName, 24.0f, Font.NORMAL, BaseColor.BLACK);
            Font dealerAddressFont = new Font(font1, 18.0f, Font.NORMAL, BaseColor.BLACK);
            Font transactionFont = new Font(font1, 16.0f, Font.NORMAL, BaseColor.BLACK);
            Font transactionFont1 = new Font(fontName, 16.0f, Font.NORMAL, BaseColor.BLACK);
            Font transactionFont2 = new Font(fontName, 16.0f, Font.NORMAL, BaseColor.WHITE);

            addNewItem(document, "21 ST CENTURY BUSINESS SYNDICATE", Element.ALIGN_CENTER, titleFont);
            addNewItem(document, "Contact : 0612-2325412,9334120345", Element.ALIGN_CENTER, contactFont);

            document.add(new Paragraph(" "));

            addNewItem(document, name,Element.ALIGN_CENTER,dealerFont);

            document.add(new Paragraph(" "));

            addNewItem(document, address,Element.ALIGN_CENTER,dealerAddressFont);

            document.add(new Paragraph(" "));

            addNewItem(document, "Ledger Account",Element.ALIGN_CENTER,dealerAddressFont);

            document.add(new Paragraph(" "));

            addNewItem(document, initialDatePdf +" - " + finalDatePdf,Element.ALIGN_CENTER,dealerAddressFont);

            document.add(new Paragraph(" "));
            document.add(new Paragraph(" "));


            float[] columnWidths = {80f,100f,80f,100f,100f};

            PdfPTable table = new PdfPTable(columnWidths);
            table.setWidthPercentage(100);

            PdfPCell cell1 = new PdfPCell(new Paragraph(new Chunk("Date", titleFont)));
            cell1.setBorderColor(BaseColor.WHITE);
            cell1.setFixedHeight(50f);

            PdfPCell cell2 = new PdfPCell(new Paragraph(new Paragraph(new Chunk("particular", titleFont))));
            cell2.setBorderColor(BaseColor.WHITE);
            cell2.setFixedHeight(50f);

            PdfPCell cell3 = new PdfPCell(new Paragraph( new Paragraph(new Chunk("Vch No.", titleFont))));
            cell3.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell3.setBorderColor(BaseColor.WHITE);
            cell3.setFixedHeight(50f);

            PdfPCell cell4 = new PdfPCell(new Paragraph(new Paragraph(new Chunk("Debit", titleFont))));
            cell4.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell4.setBorderColor(BaseColor.WHITE);
            cell4.setFixedHeight(50f);

            PdfPCell cell5 = new PdfPCell(new Paragraph(new Paragraph(new Chunk("Credit", titleFont))));
            cell5.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell5.setBorderColor(BaseColor.WHITE);
            cell5.setFixedHeight(50f);

            table.addCell(cell1);
            table.addCell(cell2);
            table.addCell(cell3);
            table.addCell(cell4);
            table.addCell(cell5);

            PdfPCell cell11 = new PdfPCell(new Paragraph(new Paragraph(new Chunk("", transactionFont))));
            PdfPCell cell12 = new PdfPCell(new Paragraph(new Paragraph(new Chunk("Opening Balance", transactionFont))));
            PdfPCell cell13 = new PdfPCell(new Paragraph(new Paragraph(new Chunk("", transactionFont))));
            PdfPCell cell14;
            PdfPCell cell15;
            if(openingBalance>=0){
                cell14 = new PdfPCell(new Paragraph(new Paragraph(new Chunk("", transactionFont))));
                cell15 = new PdfPCell(new Paragraph(new Paragraph(new Chunk(String.valueOf(Math.abs(openingBalance)), transactionFont))));
            }
            else {
                cell14 = new PdfPCell(new Paragraph(new Paragraph(new Chunk(String.valueOf(Math.abs(openingBalance)), transactionFont))));
                cell15 = new PdfPCell(new Paragraph(new Paragraph(new Chunk("", transactionFont))));
            }

            cell13.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell14.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell15.setHorizontalAlignment(Element.ALIGN_RIGHT);

            cell11.setBorderColor(BaseColor.WHITE);
            cell12.setBorderColor(BaseColor.WHITE);
            cell13.setBorderColor(BaseColor.WHITE);
            cell14.setBorderColor(BaseColor.WHITE);
            cell15.setBorderColor(BaseColor.WHITE);

            table.addCell(cell11);
            table.addCell(cell12);
            table.addCell(cell13);
            table.addCell(cell14);
            table.addCell(cell15);


            for(int i = 0; i<transactionsModelArrayList.size();i++){

                PdfPCell cell6 = new PdfPCell(new Paragraph(new Paragraph(new Chunk(transactionsModelArrayList.get(i).getDate(), transactionFont))));
                PdfPCell cell7 = new PdfPCell(new Paragraph(new Paragraph(new Chunk(transactionsModelArrayList.get(i).getParticular(), transactionFont))));
                PdfPCell cell8 = new PdfPCell(new Paragraph(new Paragraph(new Chunk(transactionsModelArrayList.get(i).getVoucher(), transactionFont))));
                PdfPCell cell9 = new PdfPCell(new Paragraph(new Paragraph(new Chunk(transactionsModelArrayList.get(i).getDebit(), transactionFont))));
                PdfPCell cell10 = new PdfPCell(new Paragraph(new Paragraph(new Chunk(transactionsModelArrayList.get(i).getCredit(), transactionFont))));

                cell10.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell9.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell8.setHorizontalAlignment(Element.ALIGN_RIGHT);

                cell6.setFixedHeight(30f);
                cell7.setFixedHeight(30f);
                cell8.setFixedHeight(30f);
                cell9.setFixedHeight(30f);
                cell10.setFixedHeight(30f);

                cell6.setBorderColor(BaseColor.WHITE);
                cell7.setBorderColor(BaseColor.WHITE);
                cell8.setBorderColor(BaseColor.WHITE);
                cell9.setBorderColor(BaseColor.WHITE);
                cell10.setBorderColor(BaseColor.WHITE);

                table.addCell(new PdfPCell(cell6));
                table.addCell(new PdfPCell(cell7));
                table.addCell(new PdfPCell(cell8));
                table.addCell(new PdfPCell(cell9));
                table.addCell(new PdfPCell(cell10));
                table.setSpacingBefore(10f);

            }

            PdfPCell cell21 = new PdfPCell(new Paragraph(new Paragraph(new Chunk("1", transactionFont2))));
            PdfPCell cell22 = new PdfPCell(new Paragraph(new Paragraph(new Chunk("", transactionFont2))));
            PdfPCell cell23 = new PdfPCell(new Paragraph(new Paragraph(new Chunk("", transactionFont2))));
            PdfPCell cell24 = new PdfPCell(new Paragraph(new Paragraph(new Chunk("", transactionFont2))));
            PdfPCell cell25 = new PdfPCell(new Paragraph(new Paragraph(new Chunk("", transactionFont2))));

            cell21.setBorderColor(BaseColor.WHITE);
            cell22.setBorderColor(BaseColor.WHITE);
            cell23.setBorderColor(BaseColor.WHITE);
            cell24.setBorderColor(BaseColor.WHITE);
            cell25.setBorderColor(BaseColor.WHITE);

            table.addCell(cell21);
            table.addCell(cell22);
            table.addCell(cell23);
            table.addCell(cell24);
            table.addCell(cell25);

            PdfPCell cell26 = new PdfPCell(new Paragraph(new Paragraph(new Chunk("1", transactionFont2))));
            PdfPCell cell27 = new PdfPCell(new Paragraph(new Paragraph(new Chunk("", transactionFont2))));
            PdfPCell cell28 = new PdfPCell(new Paragraph(new Paragraph(new Chunk("", transactionFont2))));
            PdfPCell cell29 = new PdfPCell(new Paragraph(new Paragraph(new Chunk("", transactionFont2))));
            PdfPCell cell30 = new PdfPCell(new Paragraph(new Paragraph(new Chunk("", transactionFont2))));

            cell26.setBorderColor(BaseColor.WHITE);
            cell27.setBorderColor(BaseColor.WHITE);
            cell28.setBorderColor(BaseColor.WHITE);
            cell29.setBorderColor(BaseColor.WHITE);
            cell30.setBorderColor(BaseColor.WHITE);

            table.addCell(cell26);
            table.addCell(cell27);
            table.addCell(cell28);
            table.addCell(cell29);
            table.addCell(cell30);

            PdfPCell cell16 = new PdfPCell(new Paragraph(new Paragraph(new Chunk("", transactionFont))));
            PdfPCell cell17 = new PdfPCell(new Paragraph(new Paragraph(new Chunk("Current Balance", transactionFont1))));
            PdfPCell cell18 = new PdfPCell(new Paragraph(new Paragraph(new Chunk("", transactionFont))));
            PdfPCell cell19 = new PdfPCell(new Paragraph(new Paragraph(new Chunk(String.valueOf(Math.abs(currentDebit)), transactionFont1))));
            PdfPCell cell20 = new PdfPCell(new Paragraph(new Paragraph(new Chunk(String.valueOf(Math.abs(currentCredit)), transactionFont1))));

            cell19.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell20.setHorizontalAlignment(Element.ALIGN_RIGHT);

            cell16.setBorderColor(BaseColor.WHITE);
            cell17.setBorderColor(BaseColor.WHITE);
            cell18.setBorderColor(BaseColor.WHITE);
            cell19.setBorderColor(BaseColor.WHITE);
            cell20.setBorderColor(BaseColor.WHITE);

            table.addCell(cell16);
            table.addCell(cell17);
            table.addCell(cell18);
            table.addCell(cell19);
            table.addCell(cell20);

            PdfPCell cell34 ;
            PdfPCell cell35 ;

            PdfPCell cell31 = new PdfPCell(new Paragraph(new Paragraph(new Chunk("", transactionFont))));
            PdfPCell cell32 = new PdfPCell(new Paragraph(new Paragraph(new Chunk("Outstanding Balance", transactionFont1))));
            PdfPCell cell33 = new PdfPCell(new Paragraph(new Paragraph(new Chunk("", transactionFont))));

            if(outStandingAmount>0){
                 cell34 = new PdfPCell(new Paragraph(new Paragraph(new Chunk("", transactionFont1))));
                 cell35 = new PdfPCell(new Paragraph(new Paragraph(new Chunk(String.valueOf(Math.abs(outStandingAmount)), transactionFont1))));
            }
            else {
                 cell34 = new PdfPCell(new Paragraph(new Paragraph(new Chunk(String.valueOf(Math.abs(outStandingAmount)), transactionFont1))));
                 cell35 = new PdfPCell(new Paragraph(new Paragraph(new Chunk("", transactionFont1))));
            }

            cell34.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell35.setHorizontalAlignment(Element.ALIGN_RIGHT);

            cell31.setBorderColor(BaseColor.WHITE);
            cell32.setBorderColor(BaseColor.WHITE);
            cell33.setBorderColor(BaseColor.WHITE);
            cell34.setBorderColor(BaseColor.WHITE);
            cell35.setBorderColor(BaseColor.WHITE);

            table.addCell(cell31);
            table.addCell(cell32);
            table.addCell(cell33);
            table.addCell(cell34);
            table.addCell(cell35);

            document.add(table);

            document.close();

            Toast.makeText(getContext(),"Pdf generated",Toast.LENGTH_SHORT).show();
        }catch (DocumentException e) {
            e.printStackTrace();
        }

    }

    private File createPDFFileAndShare(String name, String address, String initialDate, String finalDate) throws IOException {
        String fileName = name + initialDate + "-" + finalDate+".pdf";
        Log.i("fileName",fileName);
        String path = Environment.getExternalStorageDirectory() + File.separator + "LedgerAdmin.pdf";
        File file = new File(path);

        if(!file.exists()){
            file.createNewFile();
        }

        try{
            Document document = new Document();

            PdfWriter.getInstance(document, new FileOutputStream(path));

            document.open();

            BaseFont fontName = BaseFont.createFont("assets/fonts/Roboto-Black.ttf","UTF-8" , BaseFont.EMBEDDED);
            BaseFont font1 = BaseFont.createFont("assets/fonts/Roboto-Regular.ttf","UTF-8" , BaseFont.EMBEDDED);

            Font titleFont = new Font(fontName, 18.0f, Font.NORMAL, BaseColor.BLACK);
            Font contactFont = new Font(font1, 12.0f, Font.NORMAL, BaseColor.BLACK);
            Font dealerFont = new Font(fontName, 24.0f, Font.NORMAL, BaseColor.BLACK);
            Font dealerAddressFont = new Font(font1, 18.0f, Font.NORMAL, BaseColor.BLACK);
            Font transactionFont = new Font(font1, 16.0f, Font.NORMAL, BaseColor.BLACK);
            Font transactionFont1 = new Font(fontName, 16.0f, Font.NORMAL, BaseColor.BLACK);
            Font transactionFont2 = new Font(fontName, 16.0f, Font.NORMAL, BaseColor.WHITE);

            addNewItem(document, "21 ST CENTURY BUSINESS SYNDICATE", Element.ALIGN_CENTER, titleFont);
            addNewItem(document, "Contact : 0612-2325412,9334120345", Element.ALIGN_CENTER, contactFont);

            document.add(new Paragraph(" "));

            addNewItem(document, name,Element.ALIGN_CENTER,dealerFont);

            document.add(new Paragraph(" "));

            addNewItem(document, address,Element.ALIGN_CENTER,dealerAddressFont);

            document.add(new Paragraph(" "));

            addNewItem(document, "Ledger Account",Element.ALIGN_CENTER,dealerAddressFont);

            document.add(new Paragraph(" "));

            addNewItem(document, initialDatePdf +" - " + finalDatePdf,Element.ALIGN_CENTER,dealerAddressFont);

            document.add(new Paragraph(" "));
            document.add(new Paragraph(" "));


            float[] columnWidths = {80f,100f,80f,100f,100f};

            PdfPTable table = new PdfPTable(columnWidths);
            table.setWidthPercentage(100);

            PdfPCell cell1 = new PdfPCell(new Paragraph(new Chunk("Date", titleFont)));
            cell1.setBorderColor(BaseColor.WHITE);
            cell1.setFixedHeight(50f);

            PdfPCell cell2 = new PdfPCell(new Paragraph(new Paragraph(new Chunk("particular", titleFont))));
            cell2.setBorderColor(BaseColor.WHITE);
            cell2.setFixedHeight(50f);

            PdfPCell cell3 = new PdfPCell(new Paragraph( new Paragraph(new Chunk("Vch No.", titleFont))));
            cell3.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell3.setBorderColor(BaseColor.WHITE);
            cell3.setFixedHeight(50f);

            PdfPCell cell4 = new PdfPCell(new Paragraph(new Paragraph(new Chunk("Debit", titleFont))));
            cell4.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell4.setBorderColor(BaseColor.WHITE);
            cell4.setFixedHeight(50f);

            PdfPCell cell5 = new PdfPCell(new Paragraph(new Paragraph(new Chunk("Credit", titleFont))));
            cell5.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell5.setBorderColor(BaseColor.WHITE);
            cell5.setFixedHeight(50f);

            table.addCell(cell1);
            table.addCell(cell2);
            table.addCell(cell3);
            table.addCell(cell4);
            table.addCell(cell5);

            PdfPCell cell11 = new PdfPCell(new Paragraph(new Paragraph(new Chunk("", transactionFont))));
            PdfPCell cell12 = new PdfPCell(new Paragraph(new Paragraph(new Chunk("Opening Balance", transactionFont))));
            PdfPCell cell13 = new PdfPCell(new Paragraph(new Paragraph(new Chunk("", transactionFont))));
            PdfPCell cell14;
            PdfPCell cell15;
            if(openingBalance>=0){
                cell14 = new PdfPCell(new Paragraph(new Paragraph(new Chunk("", transactionFont))));
                cell15 = new PdfPCell(new Paragraph(new Paragraph(new Chunk(String.valueOf(Math.abs(openingBalance)), transactionFont))));
            }
            else {
                cell14 = new PdfPCell(new Paragraph(new Paragraph(new Chunk(String.valueOf(Math.abs(openingBalance)), transactionFont))));
                cell15 = new PdfPCell(new Paragraph(new Paragraph(new Chunk("", transactionFont))));
            }

            cell13.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell14.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell15.setHorizontalAlignment(Element.ALIGN_RIGHT);

            cell11.setBorderColor(BaseColor.WHITE);
            cell12.setBorderColor(BaseColor.WHITE);
            cell13.setBorderColor(BaseColor.WHITE);
            cell14.setBorderColor(BaseColor.WHITE);
            cell15.setBorderColor(BaseColor.WHITE);

            table.addCell(cell11);
            table.addCell(cell12);
            table.addCell(cell13);
            table.addCell(cell14);
            table.addCell(cell15);


            for(int i = 0; i<transactionsModelArrayList.size();i++){

                PdfPCell cell6 = new PdfPCell(new Paragraph(new Paragraph(new Chunk(transactionsModelArrayList.get(i).getDate(), transactionFont))));
                PdfPCell cell7 = new PdfPCell(new Paragraph(new Paragraph(new Chunk(transactionsModelArrayList.get(i).getParticular(), transactionFont))));
                PdfPCell cell8 = new PdfPCell(new Paragraph(new Paragraph(new Chunk(transactionsModelArrayList.get(i).getVoucher(), transactionFont))));
                PdfPCell cell9 = new PdfPCell(new Paragraph(new Paragraph(new Chunk(transactionsModelArrayList.get(i).getDebit(), transactionFont))));
                PdfPCell cell10 = new PdfPCell(new Paragraph(new Paragraph(new Chunk(transactionsModelArrayList.get(i).getCredit(), transactionFont))));

                cell10.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell9.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell8.setHorizontalAlignment(Element.ALIGN_RIGHT);

                cell6.setFixedHeight(30f);
                cell7.setFixedHeight(30f);
                cell8.setFixedHeight(30f);
                cell9.setFixedHeight(30f);
                cell10.setFixedHeight(30f);

                cell6.setBorderColor(BaseColor.WHITE);
                cell7.setBorderColor(BaseColor.WHITE);
                cell8.setBorderColor(BaseColor.WHITE);
                cell9.setBorderColor(BaseColor.WHITE);
                cell10.setBorderColor(BaseColor.WHITE);

                table.addCell(new PdfPCell(cell6));
                table.addCell(new PdfPCell(cell7));
                table.addCell(new PdfPCell(cell8));
                table.addCell(new PdfPCell(cell9));
                table.addCell(new PdfPCell(cell10));
                table.setSpacingBefore(10f);

            }

            PdfPCell cell21 = new PdfPCell(new Paragraph(new Paragraph(new Chunk("1", transactionFont2))));
            PdfPCell cell22 = new PdfPCell(new Paragraph(new Paragraph(new Chunk("", transactionFont2))));
            PdfPCell cell23 = new PdfPCell(new Paragraph(new Paragraph(new Chunk("", transactionFont2))));
            PdfPCell cell24 = new PdfPCell(new Paragraph(new Paragraph(new Chunk("", transactionFont2))));
            PdfPCell cell25 = new PdfPCell(new Paragraph(new Paragraph(new Chunk("", transactionFont2))));

            cell21.setBorderColor(BaseColor.WHITE);
            cell22.setBorderColor(BaseColor.WHITE);
            cell23.setBorderColor(BaseColor.WHITE);
            cell24.setBorderColor(BaseColor.WHITE);
            cell25.setBorderColor(BaseColor.WHITE);

            table.addCell(cell21);
            table.addCell(cell22);
            table.addCell(cell23);
            table.addCell(cell24);
            table.addCell(cell25);

            PdfPCell cell26 = new PdfPCell(new Paragraph(new Paragraph(new Chunk("1", transactionFont2))));
            PdfPCell cell27 = new PdfPCell(new Paragraph(new Paragraph(new Chunk("", transactionFont2))));
            PdfPCell cell28 = new PdfPCell(new Paragraph(new Paragraph(new Chunk("", transactionFont2))));
            PdfPCell cell29 = new PdfPCell(new Paragraph(new Paragraph(new Chunk("", transactionFont2))));
            PdfPCell cell30 = new PdfPCell(new Paragraph(new Paragraph(new Chunk("", transactionFont2))));

            cell26.setBorderColor(BaseColor.WHITE);
            cell27.setBorderColor(BaseColor.WHITE);
            cell28.setBorderColor(BaseColor.WHITE);
            cell29.setBorderColor(BaseColor.WHITE);
            cell30.setBorderColor(BaseColor.WHITE);

            table.addCell(cell26);
            table.addCell(cell27);
            table.addCell(cell28);
            table.addCell(cell29);
            table.addCell(cell30);

            PdfPCell cell16 = new PdfPCell(new Paragraph(new Paragraph(new Chunk("", transactionFont))));
            PdfPCell cell17 = new PdfPCell(new Paragraph(new Paragraph(new Chunk("Current Balance", transactionFont1))));
            PdfPCell cell18 = new PdfPCell(new Paragraph(new Paragraph(new Chunk("", transactionFont))));
            PdfPCell cell19 = new PdfPCell(new Paragraph(new Paragraph(new Chunk(String.valueOf(Math.abs(currentDebit)), transactionFont1))));
            PdfPCell cell20 = new PdfPCell(new Paragraph(new Paragraph(new Chunk(String.valueOf(Math.abs(currentCredit)), transactionFont1))));

            cell19.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell20.setHorizontalAlignment(Element.ALIGN_RIGHT);

            cell16.setBorderColor(BaseColor.WHITE);
            cell17.setBorderColor(BaseColor.WHITE);
            cell18.setBorderColor(BaseColor.WHITE);
            cell19.setBorderColor(BaseColor.WHITE);
            cell20.setBorderColor(BaseColor.WHITE);

            table.addCell(cell16);
            table.addCell(cell17);
            table.addCell(cell18);
            table.addCell(cell19);
            table.addCell(cell20);

            PdfPCell cell34 ;
            PdfPCell cell35 ;

            PdfPCell cell31 = new PdfPCell(new Paragraph(new Paragraph(new Chunk("", transactionFont))));
            PdfPCell cell32 = new PdfPCell(new Paragraph(new Paragraph(new Chunk("Outstanding Balance", transactionFont1))));
            PdfPCell cell33 = new PdfPCell(new Paragraph(new Paragraph(new Chunk("", transactionFont))));

            if(outStandingAmount>0){
                cell34 = new PdfPCell(new Paragraph(new Paragraph(new Chunk("", transactionFont1))));
                cell35 = new PdfPCell(new Paragraph(new Paragraph(new Chunk(String.valueOf(Math.abs(outStandingAmount)), transactionFont1))));
            }
            else {
                cell34 = new PdfPCell(new Paragraph(new Paragraph(new Chunk(String.valueOf(Math.abs(outStandingAmount)), transactionFont1))));
                cell35 = new PdfPCell(new Paragraph(new Paragraph(new Chunk("", transactionFont1))));
            }

            cell34.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cell35.setHorizontalAlignment(Element.ALIGN_RIGHT);

            cell31.setBorderColor(BaseColor.WHITE);
            cell32.setBorderColor(BaseColor.WHITE);
            cell33.setBorderColor(BaseColor.WHITE);
            cell34.setBorderColor(BaseColor.WHITE);
            cell35.setBorderColor(BaseColor.WHITE);

            table.addCell(cell31);
            table.addCell(cell32);
            table.addCell(cell33);
            table.addCell(cell34);
            table.addCell(cell35);

            document.add(table);

            document.close();

            Toast.makeText(getContext(),"Pdf generated",Toast.LENGTH_SHORT).show();
        }catch (DocumentException e) {
            e.printStackTrace();
        }
        return file;
    }

    private void addNewItem(Document document, String text, int alignCenter, Font font) throws DocumentException {

        Chunk chunk = new Chunk(text, font);
        Paragraph paragraph = new Paragraph(chunk);
        paragraph.setAlignment(alignCenter);
        document.add(paragraph);
    }

    private void getTransactions(){

        progressBar.setVisibility(View.VISIBLE);
        db.collection("Companies").document(company).collection("sales").document(sales).collection("dealers")
                .document(id).collection("transactions").orderBy("date").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                transactionsModelArrayList.clear();
                currentDebit = 0;
                currentCredit = 0;
                openingCreditAmount = 0;
                openingDebitAmount = 0;
                openingBalance = 0;
                outStandingAmount = 0;

                currentC.setText("");
                currentD.setText("");
                outstanding.setText("");
                outStandingCredit.setText("");
                openingCredit.setText("");
                openingDebit.setText("");

                for(DocumentSnapshot documentSnapshot: task.getResult()){
                    TransactionsModel transactionsModel = new TransactionsModel();

                    if(documentSnapshot.getString("type").equals("Credit")){
                        creditAmount = documentSnapshot.getString("amount");
                        debitAmount = "0";

                        transactionsModel.setCredit(documentSnapshot.getString("amount"));
                        transactionsModel.setDebit("");
                    }
                    else {
                        transactionsModel.setDebit(documentSnapshot.getString("amount"));
                        transactionsModel.setCredit("");

                        creditAmount = "0";
                        debitAmount = documentSnapshot.getString("amount");

                    }

                    DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                    Date date = new Date();
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(date);
                    calendar.add(Calendar.DAY_OF_MONTH, -60);
                    String finalDateS = dateFormat.format(calendar.getTime());
                    initialDatePdf = finalDateS;

                    Log.i("final date",finalDateS);

                    Date finalDate = null;
                    Date currentDate = null;

                    try {
                        finalDate = new SimpleDateFormat("dd/MM/yyyy").parse(finalDateS);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    try {
                        currentDate = new SimpleDateFormat("dd/MM/yyyy").parse(documentSnapshot.getString("date"));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    if(currentDate.after(finalDate)){
                        currentDebit += Integer.parseInt(debitAmount);
                        currentCredit += Integer.parseInt(creditAmount);
                    }

                    if(!currentDate.after(finalDate)){
                        openingCreditAmount += Integer.parseInt(creditAmount);
                        openingDebitAmount += Integer.parseInt(debitAmount);
                    }

                    transactionsModel.setDate(documentSnapshot.getString("date"));
                    transactionsModel.setParticular(documentSnapshot.getString("particular"));
                    transactionsModel.setCompany(company);
                    transactionsModel.setSales(sales);
                    transactionsModel.setId(documentSnapshot.getId());
                    transactionsModel.setAmount(documentSnapshot.getString("amount"));
                    transactionsModel.setDealer(id);
                    transactionsModel.setType(documentSnapshot.getString("type"));
                    transactionsModel.setVoucher(documentSnapshot.getString("voucher"));

                    try {
                        transactionsModel.setDateD(new SimpleDateFormat("dd/MM/yyyy").parse(documentSnapshot.getString("date")));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    if(currentDate.after(finalDate)){
                        transactionsModelArrayList.add(transactionsModel);
                    }
                }

                openingBalance = openingCreditAmount - openingDebitAmount;
                outStandingAmount = currentCredit - currentDebit+openingBalance;

                if(openingBalance<0){
                    openingDebit.setText(String.valueOf(Math.abs(openingBalance)));
                }
                else {
                    openingCredit.setText(String.valueOf(Math.abs(openingBalance)));
                }

                if(outStandingAmount>0){
                    outStandingCredit.setText(String.valueOf(Math.abs(outStandingAmount) + " Cr"));
                }
                else {
                    outstanding.setText(String.valueOf(Math.abs(outStandingAmount) + " Dr"));
                }

                if(outStandingAmount<0){
                    if(Integer.parseInt(osLimit)>Math.abs(outStandingAmount)){
                        alert.setVisibility(View.GONE);
                        alertImage.setVisibility(View.GONE);
                    }else {

                        alert.setVisibility(View.VISIBLE);
                        alertImage.setVisibility(View.VISIBLE);
                        alert.setText("Dealers outstanding limit is " + osLimit + " and current outstanding is "+String.valueOf(Math.abs(outStandingAmount)));
                    }
                }

                currentC.setText(String.valueOf(Math.abs(currentCredit)));
                currentD.setText(String.valueOf(Math.abs(currentDebit)));

                Comparator c = Collections.reverseOrder();
                Collections.sort(transactionsModelArrayList,c);

                if(transactionsModelArrayList.size()!=0){
                    finalDatePdf = transactionsModelArrayList.get(0).getDate();
                }
                else {
                    String date = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
                    finalDatePdf = date;
                }

                transactionsAdapter = new TransactionsAdapter(getContext(), transactionsModelArrayList);
                transactions.setAdapter(transactionsAdapter);
                progressBar.setVisibility(View.INVISIBLE);

                String date = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
                toAndFrom.setText(initialDatePdf + " - " + date);

                if(transactionsModelArrayList.size()==0){
                    imageView.setVisibility(View.VISIBLE);
                }
                else {
                    imageView.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    private void getSortedTransactions(final String start, final String end){
        progressBar.setVisibility(View.VISIBLE);
        db.collection("Companies").document(company).collection("sales").document(sales).collection("dealers")
                .document(id).collection("transactions").orderBy("date").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                transactionsModelArrayList.clear();
                currentDebit = 0;
                currentCredit = 0;
                openingCreditAmount = 0;
                openingDebitAmount = 0;
                openingBalance = 0;
                outStandingAmount = 0;

                currentC.setText("");
                currentD.setText("");
                outstanding.setText("");
                outStandingCredit.setText("");
                openingCredit.setText("");
                openingDebit.setText("");

                for(DocumentSnapshot documentSnapshot: task.getResult()){
                    TransactionsModel transactionsModel = new TransactionsModel();

                    try {
                        startDateD = new SimpleDateFormat("dd/MM/yyyy").parse(start);
                        initialDatePdf = start;
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    try {
                        endDateD = new SimpleDateFormat("dd/MM/yyyy").parse(end);
                        finalDatePdf = end;
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    try {
                        dateD = new SimpleDateFormat("dd/MM/yyyy").parse(documentSnapshot.getString("date"));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }


                    if(documentSnapshot.getString("type").equals("Credit")){
                        transactionsModel.setCredit(documentSnapshot.getString("amount"));
                        transactionsModel.setDebit("");

                        creditAmount = documentSnapshot.getString("amount");
                        debitAmount = "0";

                    }
                    else {
                        transactionsModel.setDebit(documentSnapshot.getString("amount"));
                        transactionsModel.setCredit("");

                        creditAmount = "0";
                        debitAmount = documentSnapshot.getString("amount");
                    }

                    try {
                        transactionsModel.setDateD(new SimpleDateFormat("dd/MM/yyyy").parse(documentSnapshot.getString("date")));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    transactionsModel.setDate(documentSnapshot.getString("date"));
                    transactionsModel.setParticular(documentSnapshot.getString("particular"));
                    transactionsModel.setCompany(company);
                    transactionsModel.setSales(sales);
                    transactionsModel.setId(documentSnapshot.getId());
                    transactionsModel.setAmount(documentSnapshot.getString("amount"));
                    transactionsModel.setDealer(id);
                    transactionsModel.setType(documentSnapshot.getString("type"));
                    transactionsModel.setVoucher(documentSnapshot.getString("voucher"));

                    if(!dateD.before(startDateD)&& !dateD.after(endDateD)){
                        Log.i("date for current", String.valueOf(dateD));
                        currentDebit += Integer.parseInt(debitAmount);
                        currentCredit += Integer.parseInt(creditAmount);
                    }

                    if(dateD.before(startDateD)){
                        openingCreditAmount += Integer.parseInt(creditAmount);
                        openingDebitAmount += Integer.parseInt(debitAmount);
                    }

                    if(!dateD.before(startDateD)&& !dateD.after(endDateD)){
                        transactionsModelArrayList.add(transactionsModel);
                    }
                }

                openingBalance = openingCreditAmount - openingDebitAmount;
                outStandingAmount = currentCredit - currentDebit+openingBalance;

                if(openingBalance<0){
                    openingDebit.setText(String.valueOf(Math.abs(openingBalance)));
                }
                else {
                    openingCredit.setText(String.valueOf(Math.abs(openingBalance)));
                }

                if(outStandingAmount>0){
                    outStandingCredit.setText(String.valueOf(Math.abs(outStandingAmount) + "Cr"));
                }
                else {
                    outstanding.setText(String.valueOf(Math.abs(outStandingAmount) + "Dr"));
                }

                if(outStandingAmount<0){
                    if(Integer.parseInt(osLimit)>Math.abs(outStandingAmount)){

                        alert.setVisibility(View.GONE);
                        alert.setVisibility(View.GONE);
                    }else {

                        alert.setVisibility(View.VISIBLE);
                        alertImage.setVisibility(View.VISIBLE);

                        alert.setText("Dealers outstanding limit is " + osLimit + " and current outstanding is "+String.valueOf(Math.abs(outStandingAmount)));
                    }
                }

                currentC.setText(String.valueOf(Math.abs(currentCredit)));
                currentD.setText(String.valueOf(Math.abs(currentDebit)));


                Comparator c = Collections.reverseOrder();
                Collections.sort(transactionsModelArrayList,c);

                transactionsAdapter = new TransactionsAdapter(getContext(), transactionsModelArrayList);
                transactions.setAdapter(transactionsAdapter);
                progressBar.setVisibility(View.INVISIBLE);
                toAndFrom.setText(initialDatePdf + " -- " + finalDatePdf);

                if(transactionsModelArrayList.size()==0){
                    imageView.setVisibility(View.VISIBLE);
                }
                else {
                    imageView.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    @Override
    public void sendInput(String startDate, String endDate) {
        if(startDate!=null && endDate != null){
            startDateExisting = startDate;
            endDateExisting = endDate;
            getSortedTransactions(startDate,endDate);
        }
        else {
            startDateExisting = null;
            endDateExisting = null;
            getTransactions();
        }

    }

    @Override
    public void sendWhatsAppInput(String amount, String type, String send) {
        if(amount!=null && type!=null && send != null){
            if(send.equals("yes")){
                PackageManager packageManager = getActivity().getPackageManager();
                Intent i = new Intent(Intent.ACTION_VIEW);

                String m;
                String numberToSend = "+91"+number;
                try {
                    if(type.equals("Debit")){
                        m = "Debited";
                    }
                    else{
                        m = "Credited";
                    }
                    String message = "Your 21st Century Business Syndicate account has been "+m+" with Rs" + amount +".";
                    String url = "https://api.whatsapp.com/send?phone="+ numberToSend +"&text=" + URLEncoder.encode(message, "UTF-8");
                    i.setPackage("com.whatsapp");
                    i.setData(Uri.parse(url));
                    if (i.resolveActivity(packageManager) != null) {
                        getActivity().startActivity(i);
                    }
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
        else {

        }
    }
}