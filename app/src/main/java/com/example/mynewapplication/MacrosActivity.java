package com.example.mynewapplication;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.InputType;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.*;

import static android.widget.LinearLayout.LayoutParams.*;

public class MacrosActivity extends AppCompatActivity {

    public final String IN = "in";
    public final String OUT = "out";
    public final String HALT = "halt";
    public final String ADD = "add";
    public final String SUB = "sub";
    public final String CMP = "cmp";
    public final String LD = "ld";
    public final String ST = "st";
    public final String LA = "la";
    public final String JMP = "jmp";
    public final String JM = "jm";
    public final String JZ = "jz";

    public Set<String> SavedMacros = new HashSet<String>();
    public Set<String> DefaultMacros = new HashSet<String>();
    public int ROWS = 100;
    public int start_id = 0;
    public int start_code_id = 0;
    public List<Pair<String, String>> MacrosCode = new ArrayList<Pair<String, String>>();
    public static Map<String, Integer> NumByCommand = new HashMap<String, Integer>();
    public static Map<Integer, String> CommandByNum = new HashMap<Integer, String>();
    public Set<String> set_1step = new HashSet<String>();
    public Set<String> set_3step = new HashSet<String>();

    public Button btnBack;
    public Button btnAddMacros;
    public Button btnInformation;
    public Button btnCancel;
    public Button btnSaveMacros;
    public Button btnClearCode;
    public TextView tvListMacros;
    public LinearLayout mainLin;
    public LinearLayout codeLin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        CreateDefaultMacros();

        setContentView(R.layout.activity_macros);
        {
            String[] files = fileList();
            for (int i = 0; i < files.length; ++i) {
                if (files[i].contains(".macros")) {
                    SavedMacros.add(files[i]);
                }
            }
        }
        mainLin = findViewById(R.id.macros_main_layout);
        codeLin = findViewById(R.id.macros_code_layout);
        btnBack = findViewById(R.id.macros_button_back);
        btnAddMacros = findViewById(R.id.macros_button_add);
        btnInformation = findViewById(R.id.macros_button_information);
        btnCancel = findViewById(R.id.macros_button_cancel);
        btnSaveMacros = findViewById(R.id.macros_button_save);
        btnClearCode = findViewById(R.id.macros_button_clear_code);
        tvListMacros = findViewById(R.id.ListOfMacros);

        btnBack.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Back();
            }
        });
        btnAddMacros.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddMacros();
            }
        });
        btnInformation.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowInformation();
            }
        });
        btnCancel.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Cancel();
            }
        });
        btnSaveMacros.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Save();
            }
        });
        btnClearCode.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClearMacros();
            }
        });

        start_id = mainLin.getChildCount();
        start_code_id = codeLin.getChildCount();
        Initialize();
        LoadMacros();
    }

    private void CreateDefaultMacros() {
        CreateMulMacros();
        CreateDivMacros();
        CreateModMacros();
        CreateFactMacros();
    }

    private void CreateModMacros() {
        ArrayList<String> code = new ArrayList<String>();
        ArrayList<String> labels = new ArrayList<String>();
        code.add("m n");
        code.add("jmp begin");
        code.add("");
        code.add("");
        code.add("");
        code.add("ld m");
        code.add("st x");
        code.add("ld n");
        code.add("st y");
        code.add("la 0000");
        code.add("st res");
        code.add("ld x");
        code.add("jz fin");
        code.add("jm finm");
        code.add("sub y");
        code.add("st x");
        code.add("jmp iter");
        code.add("add y");
        code.add("st res");
        code.add("ld res");

        labels.add("");
        labels.add("x");
        labels.add("y");
        labels.add("res");
        labels.add("begin");
        labels.add("");
        labels.add("");
        labels.add("");
        labels.add("");
        labels.add("");
        labels.add("iter");
        labels.add("");
        labels.add("");
        labels.add("");
        labels.add("");
        labels.add("");
        labels.add("finm");
        labels.add("");
        labels.add("fin");

        String fileName = "mod";
        try {
            FileOutputStream outputFile = openFileOutput(fileName.concat(".macros"), MODE_PRIVATE);
            outputFile.write((code.get(0) + "\n").getBytes());
            for (int i = 1; i < code.size(); ++i) {
                outputFile.write((code.get(i) + "\n").getBytes());
            }
            outputFile.close();

            outputFile = openFileOutput(fileName.concat(".labels"), MODE_PRIVATE);
            for (int i = 0; i < labels.size(); ++i) {
                outputFile.write((labels.get(i) + "\n").getBytes());
            }
            outputFile.close();
        } catch (Exception e) {

        }
        DefaultMacros.add(fileName.concat(".macros"));
        SavedMacros.add(fileName.concat(".macros"));
    }

    private void CreateDivMacros() {
        ArrayList<String> code = new ArrayList<String>();
        ArrayList<String> labels = new ArrayList<String>();
        code.add("m n");
        code.add("jmp begin");
        code.add("");
        code.add("");
        code.add("");
        code.add("");
        code.add("ld m");
        code.add("st x");
        code.add("ld n");
        code.add("st y");
        code.add("la 0001");
        code.add("st one");
        code.add("la 0000");
        code.add("st res");
        code.add("ld x");
        code.add("jz fin");
        code.add("jm finm");
        code.add("sub y");
        code.add("st x");
        code.add("ld res");
        code.add("add one");
        code.add("st res");
        code.add("jmp iter");
        code.add("ld res");
        code.add("sub one");
        code.add("st res");
        code.add("ld res");

        labels.add("");
        labels.add("x");
        labels.add("y");
        labels.add("one");
        labels.add("res");
        labels.add("begin");
        labels.add("");
        labels.add("");
        labels.add("");
        labels.add("");
        labels.add("");
        labels.add("");
        labels.add("");
        labels.add("iter");
        labels.add("");
        labels.add("");
        labels.add("");
        labels.add("");
        labels.add("");
        labels.add("");
        labels.add("");
        labels.add("");
        labels.add("finm");
        labels.add("");
        labels.add("");
        labels.add("fin");

        String fileName = "div";
        try {
            FileOutputStream outputFile = openFileOutput(fileName.concat(".macros"), MODE_PRIVATE);
            outputFile.write((code.get(0) + "\n").getBytes());
            for (int i = 1; i < code.size(); ++i) {
                outputFile.write((code.get(i) + "\n").getBytes());
            }
            outputFile.close();

            outputFile = openFileOutput(fileName.concat(".labels"), MODE_PRIVATE);
            for (int i = 0; i < labels.size(); ++i) {
                outputFile.write((labels.get(i) + "\n").getBytes());
            }
            outputFile.close();
        } catch (Exception e) {

        }
        DefaultMacros.add(fileName.concat(".macros"));
        SavedMacros.add(fileName.concat(".macros"));
    }

    private void CreateMulMacros() {
        ArrayList<String> code = new ArrayList<String>();
        ArrayList<String> labels = new ArrayList<String>();
        code.add("m n");
        code.add("jmp begin");
        code.add("");
        code.add("");
        code.add("");
        code.add("la 0001");
        code.add("st one");
        code.add("la 0000");
        code.add("st res");
        code.add("ld m");
        code.add("st x");
        code.add("ld x");
        code.add("jz fin");
        code.add("sub one");
        code.add("st x");
        code.add("ld res");
        code.add("add n");
        code.add("st res");
        code.add("jmp iter");
        code.add("ld res");

        labels.add("");
        labels.add("one");
        labels.add("res");
        labels.add("x");
        labels.add("begin");
        labels.add("");
        labels.add("");
        labels.add("");
        labels.add("");
        labels.add("");
        labels.add("iter");
        labels.add("");
        labels.add("");
        labels.add("");
        labels.add("");
        labels.add("");
        labels.add("");
        labels.add("");
        labels.add("fin");
        String fileName = "mul";
        try {
            FileOutputStream outputFile = openFileOutput(fileName.concat(".macros"), MODE_PRIVATE);
            outputFile.write((code.get(0) + "\n").getBytes());
            for (int i = 1; i < code.size(); ++i) {
                outputFile.write((code.get(i) + "\n").getBytes());
            }
            outputFile.close();

            outputFile = openFileOutput(fileName.concat(".labels"), MODE_PRIVATE);
            for (int i = 0; i < labels.size(); ++i) {
                outputFile.write((labels.get(i) + "\n").getBytes());
            }
            outputFile.close();
        } catch (Exception e) {

        }
        DefaultMacros.add(fileName.concat(".macros"));
        SavedMacros.add(fileName.concat(".macros"));
    }

    private void CreateFactMacros() {
        ArrayList<String> code = new ArrayList<String>();
        ArrayList<String> labels = new ArrayList<String>();

        code.add("n");
        code.add("jmp begin");
        code.add("");
        code.add("");
        code.add("la 0001");
        code.add("st one");
        code.add("st res");
        code.add("ld n");
        code.add("jz fin");
        code.add("mul n res");
        code.add("st res");
        code.add("ld n");
        code.add("sub one");
        code.add("st n");
        code.add("jmp iter");
        code.add("ld res");

        labels.add("");
        labels.add("res");
        labels.add("one");
        labels.add("begin");
        labels.add("");
        labels.add("");
        labels.add("");
        labels.add("iter");
        labels.add("");
        labels.add("");
        labels.add("");
        labels.add("");
        labels.add("");
        labels.add("");
        labels.add("fin");

        String fileName = "fact";
        try {
            FileOutputStream outputFile = openFileOutput(fileName.concat(".macros"), MODE_PRIVATE);
            outputFile.write((code.get(0) + "\n").getBytes());
            for (int i = 1; i < code.size(); ++i) {
                outputFile.write((code.get(i) + "\n").getBytes());
            }
            outputFile.close();

            outputFile = openFileOutput(fileName.concat(".labels"), MODE_PRIVATE);
            for (int i = 0; i < labels.size(); ++i) {
                outputFile.write((labels.get(i) + "\n").getBytes());
            }
            outputFile.close();
        } catch (Exception e) {

        }
        DefaultMacros.add(fileName.concat(".macros"));
        SavedMacros.add(fileName.concat(".macros"));
    }

    private void Back(){
        Intent intent = new Intent(this, CodeActivity.class);
        startActivity(intent);
    }

    private void AddMacros(){
        ClearMacros();
        EditText et_name = findViewById(R.id.macros_name);
        EditText et_parameters = findViewById(R.id.macros_parameters);
        et_name.setText("");
        et_parameters.setText("");

        mainLin.setVisibility(View.GONE);
        codeLin.setVisibility(View.VISIBLE);
    }

    private void Cancel(){
        LoadMacros();
        mainLin.setVisibility(View.VISIBLE);
        codeLin.setVisibility(View.GONE);
    }

    private void Save(){
        EditText et_name = findViewById(R.id.macros_name);
        EditText et_parameters = findViewById(R.id.macros_parameters);
        String[] parameters = et_parameters.getText().toString().split(" ");
        String fileName = et_name.getText().toString().trim();
        if (DefaultMacros.contains(fileName.concat(".macros")))
        {
            ErrorFound("Нельзя называть макросы в точности как дефолтные.");
            return;
        }
        FillAssemblerCode();
        if (AnalysMacros()) {
            try {
                FileOutputStream outputFile = openFileOutput(fileName.concat(".macros"), MODE_PRIVATE);
                outputFile.write((et_parameters.getText().toString() + "\n").getBytes());
                int last_idx = FindLastIdx();
                for (int i = 0; i < last_idx; ++i) {
                    outputFile.write((getCommandAt(i) + "\n").getBytes());
                }
                outputFile.close();

                outputFile = openFileOutput(fileName.concat(".labels"), MODE_PRIVATE);
                for (int i = 0; i < last_idx; ++i) {
                    outputFile.write((getLabelAt(i) + "\n").getBytes());
                }
                outputFile.close();
            } catch (Exception e) {

            }
            SavedMacros.add(fileName.concat(".macros"));
            Cancel();
        }else{
            ErrorFound("Не смогли сохранить макрос, ошибка в коде.");
        }
    }

    //находит адрес последней команды (нужно чтобы не сохранять все 100 строк, а только те где есть данные)
    private int FindLastIdx(){
        for(int i = 0; i < ROWS; ++i){
            if (getCommandAt(i).trim().isEmpty() && getLabelAt(i).trim().isEmpty()){
                return i;
            }
        }
        return ROWS;
    }

    private boolean AnalysMacros(){
        EditText et_parameters = findViewById(R.id.macros_parameters);
        String[] parameters = et_parameters.getText().toString().split(" ");
        int err_cnt = 0;
        int par_len = parameters.length;

        Map<String, Integer> labels_LA = new HashMap<String, Integer>(); // ключ - метка, значение - адрес метки
        Set<String> labels_par = new HashSet<String>(); // ключ - метка, значение - адрес метки
        for (int i = 0; i < MacrosCode.size(); ++i) {
            String label = MacrosCode.get(i).first;
            if (!label.isEmpty())
                labels_LA.put(label, i);
        }

        //проверка что метки из параметров использются только в строках команды, и не используются в качестве меток
        for(int i = 0; i < par_len; ++i){
            if (labels_LA.containsKey(parameters[i])) {
                err_cnt++;
                ErrorFound("Неправильное использование введенных параметров (они встречаются в столбце меток).");
            }else{
                labels_par.add(parameters[i]);
            }
        }

        for(int id = 0; id < MacrosCode.size(); ++id){
            String str = MacrosCode.get(id).second;
            str = str.trim();
            int indexp = str.indexOf(" ");
            String s_addr = "";
            if (indexp == -1) {
                indexp = str.length();
            } else {
                s_addr = str.substring(indexp + 1);
            }
            String s_command = str.substring(0, indexp);
            if (s_command.isEmpty()) {
                continue;
            }
            ////
            if (SavedMacros.contains(s_command.concat(".macros"))){
                String parameters1 = str.substring(indexp + 1);
                String parameters2 = GetMacrosParameters(s_command);
                String[] labels = parameters1.split(" ");
                if (parameters1.split(" ").length != parameters2.split(" ").length){
                    err_cnt++;
                }
                for(int i = 0; i < labels.length; ++i){
                    if (!labels_LA.containsKey(labels[i]) && !labels_par.contains(labels[i])){
                        err_cnt++;
                    }
                }
                continue;
            }
            ////
            if (!(set_1step.contains(s_command) || set_3step.contains(s_command))) {
                err_cnt++;
                continue;
            }
            if (set_1step.contains(s_command)) {
                if (!s_addr.isEmpty()) {
                    err_cnt++;
                }
            } else if (set_3step.contains(s_command)) {
                if (s_command.equals(LA)) {
                    try {
                        Integer.parseInt(s_addr);
                        continue;
                    } catch (Exception e) {
                        err_cnt++;

                    }
                }
                if (!labels_LA.containsKey(s_addr) && !labels_par.contains(s_addr)) {
                    err_cnt++;
                }
            }
        }
        return (err_cnt == 0);
    }

    private String GetMacrosParameters(String macros){
        String lines = "";
        try {
            FileInputStream inputFile = openFileInput(macros.concat(".macros"));
            InputStreamReader reader = new InputStreamReader(inputFile);
            BufferedReader buffer = new BufferedReader(reader);
            lines = buffer.readLine();
            inputFile.close();
        }catch (Exception e){
            ErrorFound("Не удалось открыть макрос " + macros);
        }
        return lines;
    }

    private void ShowInformation(){
        return;
    }

    private void ClearMacros(){
        for (int i = start_code_id; i < codeLin.getChildCount(); ++i){
            LinearLayout linL = (LinearLayout)codeLin.getChildAt(i);
            EditText et_label = (EditText)linL.getChildAt(0);
            EditText et_comm = (EditText)linL.getChildAt(1);
            et_label.setText("");
            et_comm.setText("");
        }
    }

    private void LoadMacros(){
        final LinearLayout load_layout = findViewById(R.id.macros_main_layout);
        load_layout.removeViews(start_id, load_layout.getChildCount() - start_id);
        for (final String s : SavedMacros) {
            final LinearLayout new_layout = new LinearLayout(this);
            new_layout.setOrientation(LinearLayout.HORIZONTAL);
            final Button btn = new Button(this);
            btn.setLayoutParams(new LayoutParams(500, WRAP_CONTENT));
            btn.setText(s);
            btn.setTypeface(btnAddMacros.getTypeface());
            btn.setBackgroundResource(R.drawable.insent);
            btn.setOnClickListener(new Button.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ClearMacros();
                    AddMacros();
                    try{
                        FileInputStream inputFile = openFileInput(s);
                        InputStreamReader reader = new InputStreamReader(inputFile);
                        BufferedReader buffer = new BufferedReader(reader);
                        String lines = "";
                        int i = 0;
                        lines = buffer.readLine();
                        EditText et_name = findViewById(R.id.macros_name);
                        EditText et_parameters = findViewById(R.id.macros_parameters);
                        et_name.setText(btn.getText().toString().substring(0, s.indexOf(".macros")));
                        et_parameters.setText(lines);
                        while ((lines = buffer.readLine()) != null){
                            setCommandAt(i++, lines);
                        }
                        inputFile.close();

                        String file_label = s.substring(0, s.indexOf(".macros")).concat(".labels");
                        inputFile = openFileInput(file_label);
                        reader = new InputStreamReader(inputFile);
                        buffer = new BufferedReader(reader);
                        i = 0;
                        while ((lines = buffer.readLine()) != null){
                            setLabelAt(i++, lines);
                        }
                        inputFile.close();
                    }catch (Exception e){
                        ErrorFound("Не смогли открыть код макроса.");
                    }

                }
            });
            new_layout.addView(btn, 0);
            if (!DefaultMacros.contains(s)) {
                Button btnDelete = new Button(this);
                btnDelete.setText("DELETE");
                btnDelete.setTypeface(btnAddMacros.getTypeface());
                btnDelete.setBackgroundResource(R.drawable.insent);
                btnDelete.setOnClickListener(new Button.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String file_name = btn.getText().toString();
                        file_name = file_name.substring(0, file_name.indexOf(".macros"));
                        deleteFile(file_name.concat(".macros"));
                        deleteFile(file_name.concat(".labels"));
                        SavedMacros.remove(btn.getText().toString());
                        load_layout.removeView(new_layout);
                    }
                });
                new_layout.addView(btnDelete, 1);
            }
            load_layout.addView(new_layout);
        }
    }

    public void setCommandAt(int id, String command) {
        TextView v = (TextView) ((LinearLayout) codeLin.getChildAt(id + start_code_id)).getChildAt(1);
        v.setText(command);
    }

    public void setLabelAt(int id, String label) {
        TextView v = (TextView) ((LinearLayout) codeLin.getChildAt(id + start_code_id)).getChildAt(0);
        v.setText(label);
    }

    public String getCommandAt(int id) {
        TextView v = (TextView) ((LinearLayout) codeLin.getChildAt(id + start_code_id)).getChildAt(1);
        return v.getText().toString();
    }

    public String getLabelAt(int id) {
        TextView v = (TextView) ((LinearLayout) codeLin.getChildAt(id + start_code_id)).getChildAt(0);
        return v.getText().toString();
    }

    public void FillAssemblerCode(){
        MacrosCode.clear();
        for (int i = start_code_id; i < codeLin.getChildCount(); ++i){
            LinearLayout linL = (LinearLayout)codeLin.getChildAt(i);
            EditText et_label = (EditText)linL.getChildAt(0);
            EditText et_comm = (EditText)linL.getChildAt(1);
            MacrosCode.add(new Pair(et_label.getText().toString() ,et_comm.getText().toString()));
        }
    }


    public void ErrorFound(String text) {
        //TODO: GUI output
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Ошибка");
        alert.setMessage(text);
        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        alert.create().show();
    }
    private void Initialize(){
        codeLin.removeViews(start_code_id, codeLin.getChildCount() - start_code_id);
        TextView tv_label = findViewById(R.id.MacrosCodeLabel);
        for (int i = 0; i < ROWS; i++) {
            LinearLayout linLayout = new LinearLayout(this);
            linLayout.setOrientation(LinearLayout.HORIZONTAL);

            EditText addr_v = new EditText(this);
            addr_v.setLayoutParams(tv_label.getLayoutParams());
            addr_v.setTypeface(tvListMacros.getTypeface());
            linLayout.addView(addr_v, 0);

            EditText comm = new EditText(this);
            comm.setInputType(InputType.TYPE_CLASS_TEXT);
            comm.setLayoutParams(new LayoutParams(MATCH_PARENT, MATCH_PARENT));
            comm.setTextSize(20);
            comm.setTypeface(tvListMacros.getTypeface());
            linLayout.addView(comm, 1);

            linLayout.setBackgroundColor(getResources().getColor(R.color.colorWhite));
            codeLin.addView(linLayout, i + start_code_id);
        }
        ////////////////////
        set_1step.add(IN);
        set_1step.add(OUT);
        set_1step.add(HALT);
        set_3step.add(LD);
        set_3step.add(ST);
        set_3step.add(LA);
        set_3step.add(JM);
        set_3step.add(JZ);
        set_3step.add(ADD);
        set_3step.add(SUB);
        set_3step.add(CMP);
        set_3step.add(JMP);
        /////////////////////
        NumByCommand.put(IN, 1);
        NumByCommand.put(LD, 21);
        NumByCommand.put(ST, 22);
        NumByCommand.put(LA, 23);
        NumByCommand.put(JM, 34);
        NumByCommand.put(JZ, 33);
        NumByCommand.put(ADD, 10);
        NumByCommand.put(SUB, 11);
        NumByCommand.put(CMP, 12);
        NumByCommand.put(JMP, 30);
        NumByCommand.put(OUT, 2);
        NumByCommand.put(HALT, 99);
        //////////////////////////
        CommandByNum.put(1, IN);
        CommandByNum.put(21, LD);
        CommandByNum.put(22, ST);
        CommandByNum.put(23, LA);
        CommandByNum.put(34, JM);
        CommandByNum.put(33, JZ);
        CommandByNum.put(10, ADD);
        CommandByNum.put(11, SUB);
        CommandByNum.put(12, CMP);
        CommandByNum.put(30, JMP);
        CommandByNum.put(2, OUT);
        CommandByNum.put(99, HALT);
    }


}
