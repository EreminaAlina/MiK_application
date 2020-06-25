package com.example.mynewapplication;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.text.InputType;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import java.io.*;
import java.util.*;

public class CodeActivity extends AppCompatActivity {
    public enum ErrorType {
        WRONG_PLACE_COMMAND,
        NOT_EXISTING_COMMAND,
        WRONG_COMMAND_FORMAT,
        WRONG_ADDRESS,
        WRONG_PLACE_COMMAND_ASM,
        NOT_EXISTING_COMMAND_ASM,
        WRONG_COMMAND_FORMAT_ASM,
        WRONG_ADDRESS_ASM,
    }

    public final int MAX_SIZE = 1000;
    public int start_id = 0;
    public int start_asm_id = 0;
    public LinearLayout mainLin; // главный Layout куда добавляем "строки"
    public LinearLayout asmLin; // главный Layout куда добавляем "строки"
    public Button btnStart; // кнопка которая будет заставлять все работать
    public Button btnEnterCommand; // кнопка которая будет заставлять все работать
    public Button btnEnterData; // кнопка которая будет заставлять все работать
    public Button btnAsmMik;
    public Button btnAsmBack;
    public Button btnAsmLogs;
    public Button btnAsmCompile;
    public Button btnAsmClear;
    public Button btnMainClear;
    public Button btnAsmMainLogs;
    public Button btnAsmShowMacros;
    public Button btnShowRegisters;
    public Button btnClearLogs;
    public Button btnDebug; // кнопка которая будет заставлять работать 1 линию
    public LinearLayout secL; // переменная нужна чтобы скопировать параметры
    public EditText comment;
    public EditText command; // переменная нужна чтобы скопировать параметры
    public TextView addr; // переменная нужна чтобы скопировать параметры
    public TextView tvBeautfulComment;
    public int ROWS = 100;
    public boolean isInitialized = false;
    public List<String> logs = new ArrayList<String>();
    public Map<String, Integer> labels_LA = new HashMap<String, Integer>(); // ключ - метка, значение - адрес метки
    public static Map<String, Integer> NumByCommand = new HashMap<String, Integer>();
    public static Map<Integer, String> CommandByNum = new HashMap<Integer, String>();
    public List<Pair<String, String>> MachineCodeList = new ArrayList<Pair<String, String>>();
    public List<Pair<String, String>> AssemblerCodeList = new ArrayList<Pair<String, String>>();
    public List<String> MachineCodeComments = new ArrayList<String>();
    public List<String> Comments = new ArrayList<String>();
    /////////////////////////////////
    public int sum = 0;
    public int[] map = new int[MAX_SIZE];
    public int[] usd = new int[MAX_SIZE];
    public int Z = 0;
    public int idx = 0;
    public int last_idx = 0;
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
    public Set<String> set_1step = new HashSet<String>();
    public Set<String> set_3step = new HashSet<String>();

    /////////////////////////////////
    public Set<String> SavedFiles = new HashSet<String>();
    public Set<String> SavedMacros = new HashSet<String>();
    /*
    TODO: добавить к каждой функции дополнительный параметр CODE_TYPE, чтобы в зависимостри от того, в каком режиме мы работаем, мы делали шаги:
    TODO: например если в режиме ассемблера то выполняем каждую строку (то есть шаг в 1)
    TODO: если в режиме машинного кода, то в зависимости от команды делать шаг в 1 или в 3
    */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assembler_code);
        //Загружаем существующие файлы
        {
            String[] files = fileList();
            for(int i = 0; i < files.length; ++i){
                if (files[i].contains(".mik")){
                    SavedFiles.add(files[i]);
                }
                if (files[i].contains(".macros")){
                    SavedMacros.add(files[i].substring(0, files[i].indexOf(".macros")));
                }
            }
        }
        mainLin = findViewById(R.id.AssemblerCodeMainLinLayout);
        asmLin = findViewById(R.id.AssemblerCodeAsmLayout);
        btnStart = findViewById(R.id.AssemblerCodeBtnStart);
        btnEnterCommand = findViewById(R.id.AssemblerCodeBtnEnterCode);
        btnEnterData = findViewById(R.id.AssemblerCodeBtnEnterData);
        btnShowRegisters = findViewById(R.id.AssemblerCodeBtnShowRegisters);
        btnMainClear = findViewById(R.id.AssemblerCodeBtnClearCode);
        btnClearLogs = findViewById(R.id.AssemblerCodeBtnClearLogs);
        btnDebug = findViewById(R.id.AssemblerCodeBtnDebug);
        btnAsmCompile = findViewById(R.id.AssemblerCodeAsmCompile);
        btnAsmShowMacros = findViewById(R.id.AssemblerCodeAsmShowMacros);
        btnAsmMik = findViewById(R.id.AssemblerCodeBtnAsmMik);
        btnAsmLogs = findViewById(R.id.AssemblerCodeAsmShowLogs);
        btnAsmMainLogs = findViewById(R.id.AssemblerCodeBtnShowLogMain);
        btnAsmBack = findViewById(R.id.AssemblerCodeAsmBack);
        btnAsmClear = findViewById(R.id.AssemblerCodeAsmClear);
        tvBeautfulComment = findViewById(R.id.BeautifulComment);
        secL = findViewById(R.id.AssemblerCodeSecondLinear);
        addr = findViewById(R.id.AssemblerCodeCopyAddr);
        command = findViewById(R.id.AssemblerCodeCopyCommand);
        comment = findViewById(R.id.AssemblerCodeCopyComment);

        btnAsmMik.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowAsmMik();
            }
        });
        btnClearLogs.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClearLogs();
            }
        });
        btnMainClear.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClearCode();
            }
        });
        btnEnterCommand.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                EnterCommand();
            }
        });
        btnEnterData.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                EnterData();
            }
        });
        btnShowRegisters.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowRegisters();
            }
        });
        btnAsmClear.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClearAsmMik();
            }
        });
        btnAsmCompile.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                CompileCode();
            }
        });
        btnAsmLogs.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowLogs();
            }
        });
        btnAsmMainLogs.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowLogs();
            }
        });
        btnAsmBack.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Back();
            }
        });
        btnAsmShowMacros.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowMacros();
            }
        });
        btnStart.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                InitializeStartIndexNorm();
            }
        });
        btnDebug.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isInitialized)
                    InitializeStartIndexDebug();
                else
                    Handle1Line();
            }
        });

        start_id = mainLin.getChildCount() - 1;
        start_asm_id = asmLin.getChildCount() - 1;

        InitializeComponents();
        Initialize();
        InitializeAsmMik();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.additional_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.load_settings:
                LoadCode();
                return true;
            case R.id.save_settings:
                SaveCode();
                return true;
            case R.id.instruction_settings:
                ShowInstructions();
                return true;
            case R.id.exit_settings:
                ExitToMenu();
                return true;
            case R.id.macros_settings:
                OpenMacros();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void InitializeFromAssemblerCode() {
        int toAdd = ROWS - AssemblerCodeList.size();
        for (int i = 0; i < toAdd; ++i) {
            AssemblerCodeList.add(new Pair<String, String>("", ""));
        }
        for (int i = 0; i < ROWS; ++i) {
            setLabelAt(i, AssemblerCodeList.get(i).first);
            setCommandAt(i, AssemblerCodeList.get(i).second);
        }
    }

    private int FindLastIdx(){
        int res = 0;
        for(int i = 0; i < MachineCodeList.size(); ++i){
            if (!MachineCodeList.get(i).second.trim().isEmpty()){
                res = i;
            }
        }
        return res;
    }

    private void InitializeFromMachineCode() {
        SetLines(100 + FindLastIdx());
        for (int i = 0; i < MachineCodeList.size(); ++i) {
            setLabelAt(i, MachineCodeList.get(i).first);
            setCommandAt(i, MachineCodeList.get(i).second);
            setCommentAt(i, MachineCodeComments.get(i));
        }
        isInitialized = false;
    }

    private void AskForClearStart() {
        AlertDialog.Builder mDialogBuilder = new AlertDialog.Builder(this);

        //Настраиваем сообщение в диалоговом окне:
        mDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                InitializeComponents();
                            }
                        })
                .setNegativeButton("Отмена",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                            }
                        });
        AlertDialog alertDialog = mDialogBuilder.create();
        //и отображаем его:
        alertDialog.show();
    }

    private void SaveCode() {
        LayoutInflater li = LayoutInflater.from(this);
        View promptsView = li.inflate(R.layout.save_layout, null);

        //Создаем AlertDialog
        AlertDialog.Builder mDialogBuilder = new AlertDialog.Builder(this);

        //Настраиваем prompt.xml для нашего AlertDialog:
        mDialogBuilder.setView(promptsView);
        //Настраиваем отображение поля для ввода текста в открытом диалоге:
        final EditText et = promptsView.findViewById(R.id.save_file);

        //Настраиваем сообщение в диалоговом окне:
        mDialogBuilder
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                try {
                                    String fileName = et.getText().toString().concat(".mik");
                                    FileOutputStream outputFile = openFileOutput(fileName, MODE_PRIVATE);
                                    for(int i = 0; i < ROWS; ++i){
                                        outputFile.write((getCommandAt(i) + "\n").getBytes());
                                    }
                                    outputFile.close();
                                    SavedFiles.add(fileName);

                                    fileName = et.getText().toString().concat(".comment");
                                    outputFile = openFileOutput(fileName, MODE_PRIVATE);
                                    for(int i = 0; i < ROWS; ++i){
                                        outputFile.write((getCommentAt(i) + "\n").getBytes());
                                    }
                                } catch (Exception e) {
                                    logs.add("Ошибка при сохранении.");
                                    ErrorFound();
                                }
                            }
                        })
                .setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        AlertDialog alertDialog = mDialogBuilder.create();
        //и отображаем его:
        alertDialog.show();

        return;
    }

    private void LoadCode() {
        LayoutInflater li = LayoutInflater.from(this);
        View promptsView = li.inflate(R.layout.load_layout, null);

        //Создаем AlertDialog
        final AlertDialog.Builder mDialogBuilder = new AlertDialog.Builder(this);

        //Настраиваем prompt.xml для нашего AlertDialog:
        mDialogBuilder.setView(promptsView);

        //Настраиваем отображение поля для ввода текста в открытом диалоге:
        final LinearLayout load_layout = promptsView.findViewById(R.id.load_linear_layout);
        load_layout.removeAllViews();

        TextView tv = new TextView(this);
        tv.setTypeface(addr.getTypeface());
        tv.setText("Загрузка файла:");
        tv.setTextSize(25);
        tv.setTextColor(Color.BLACK);
        load_layout.addView(tv);

        //Настраиваем сообщение в диалоговом окне:
        mDialogBuilder
                .setPositiveButton("Отмена",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
        final AlertDialog alertDialog = mDialogBuilder.create();

        for (final String s : SavedFiles) {
            final LinearLayout new_layout = new LinearLayout(this);
            new_layout.setOrientation(LinearLayout.HORIZONTAL);
            final Button btn = new Button(this);
            btn.setText(s);
            btn.setBackgroundResource(R.drawable.insent);
            btn.setTypeface(btnStart.getTypeface());

            btn.setOnClickListener(new Button.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ClearCode();
                    try{
                        FileInputStream inputFile = openFileInput(s);
                        InputStreamReader reader = new InputStreamReader(inputFile);
                        BufferedReader buffer = new BufferedReader(reader);
                        String lines = "";
                        int i = 0;
                        while ((lines = buffer.readLine()) != null){
                            setCommandAt(i++, lines);
                        }
                        inputFile.close();

                        FileInputStream inputFile2 = openFileInput(s.substring(0, s.indexOf(".mik")).concat(".comment"));
                        InputStreamReader reader2 = new InputStreamReader(inputFile2);
                        BufferedReader buffer2 = new BufferedReader(reader2);
                        lines = "";
                        i = 0;
                        while ((lines = buffer2.readLine()) != null){
                            setCommentAt(i++, lines);
                        }
                        inputFile2.close();
                        Initialize();
                    }catch (Exception e){
                        logs.add("Ошибка при попытке загрузить файл.");
                        ErrorFound();
                    }
                    alertDialog.dismiss();
                }
            });
            Button btnDelete = new Button(this);
            btnDelete.setText("DELETE");
            btnDelete.setBackgroundResource(R.drawable.insent);
            btnDelete.setTypeface(btnStart.getTypeface());
            btnDelete.setOnClickListener(new Button.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteFile(s);
                    deleteFile(s.substring(0, s.indexOf(".mik")).concat(".comment"));
                    SavedFiles.remove(btn.getText().toString());
                    load_layout.removeView(new_layout);
                    alertDialog.dismiss();
                }
            });
            new_layout.addView(btn, 0);
            new_layout.addView(btnDelete, 1);
            load_layout.addView(new_layout);
        }
        //и отображаем его:
        alertDialog.show();

        return;
    }

    private void ShowInstructions() {
        Intent intent = new Intent(this, InfoActivity.class);
        startActivity(intent);
    }

    private void ExitToMenu() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private void OpenMacros(){
        Intent intent = new Intent(this, MacrosActivity.class);
        startActivity(intent);
    }

    private void SetLines(int n) {
        EditText et_addr = findViewById(R.id.AssemblerCodeCommandAddress);
        mainLin.removeViews(start_id, mainLin.getChildCount() - start_id);
        for (int i = 0; i < n; i++) {
            LinearLayout linLayout = new LinearLayout(this);
            linLayout.setLayoutParams(secL.getLayoutParams());

            TextView addr_v = new TextView(this);
            addr_v.setLayoutParams(addr.getLayoutParams());
            addr_v.setGravity(View.TEXT_ALIGNMENT_GRAVITY);
            addr_v.setText(FourDigitAddress(i));
            addr_v.setTextSize(20);
            addr_v.setTypeface(et_addr.getTypeface());
            linLayout.addView(addr_v, 0);

            TextView comm = new TextView(this);
            comm.setLayoutParams(command.getLayoutParams());
            comm.setTextSize(20);
            comm.setTypeface(et_addr.getTypeface());
            linLayout.addView(comm, 1);

            EditText comt = new EditText(this);
            comt.setLayoutParams(comment.getLayoutParams());
            comt.setTextSize(20);
            comt.setInputType(InputType.TYPE_CLASS_TEXT);
            comt.setTypeface(tvBeautfulComment.getTypeface());
            linLayout.addView(comt, 2);

            linLayout.setBackgroundColor(getResources().getColor(R.color.colorWhite));
            mainLin.addView(linLayout, i + start_id);
        }
        ROWS = n;
    }

    private void InitializeAsmMik(){
        asmLin.removeViews(start_asm_id, asmLin.getChildCount() - start_asm_id);

        for (int i = 0; i < ROWS; i++) {
            LinearLayout linLayout = new LinearLayout(this);
            linLayout.setLayoutParams(secL.getLayoutParams());

            EditText addr_v = new EditText(this);
            addr_v.setLayoutParams(addr.getLayoutParams());
            addr_v.setTextSize(20);
            addr_v.setTypeface(tvBeautfulComment.getTypeface());
            linLayout.addView(addr_v, 0);

            EditText comm = new EditText(this);
            comm.setLayoutParams(command.getLayoutParams());
            comm.setTextSize(20);
            comm.setInputType(InputType.TYPE_CLASS_TEXT);
            comm.setTypeface(tvBeautfulComment.getTypeface());
            linLayout.addView(comm, 1);

            EditText comt = new EditText(this);
            comt.setLayoutParams(comment.getLayoutParams());
            comt.setTextSize(20);
            comt.setInputType(InputType.TYPE_CLASS_TEXT);
            comt.setTypeface(tvBeautfulComment.getTypeface());
            linLayout.addView(comt, 2);

            linLayout.setBackgroundColor(getResources().getColor(R.color.colorWhite));
            asmLin.addView(linLayout, i + start_asm_id);
        }
    }

    private void ClearAsmMik(){
        for (int i = start_asm_id; i < asmLin.getChildCount(); ++i){
            LinearLayout linL = (LinearLayout)asmLin.getChildAt(i);
            EditText et_label = (EditText)linL.getChildAt(0);
            EditText et_comm = (EditText)linL.getChildAt(1);
            et_label.setText("");
            et_comm.setText("");
        }
    }

    private void InitializeComponents() {
        TextView tv = (TextView) ((LinearLayout) mainLin.getChildAt(start_id - 1)).getChildAt(0);
        tv.setText("Адрес");
        SetLines(ROWS);
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

    private String FourDigitAddress(int i){
        String num = ToString(i);
        if (i / 10 == 0)
            return "000" + num;
        if (i / 100 == 0)
            return "00" + num;
        if (i / 1000 == 0)
            return "0" + num;
        return num;
    }

    public void setColorRunning(int id) {
        LinearLayout ll = (LinearLayout) mainLin.getChildAt(id + start_id);
        ll.setBackgroundColor(getResources().getColor(R.color.colorLime));
    }

    public void setColorException(int id) {
        LinearLayout ll = (LinearLayout) mainLin.getChildAt(id + start_id);
        ll.setBackgroundColor(getResources().getColor(R.color.colorRed));
    }

    public void setColorDefault(int id) {
        LinearLayout ll = (LinearLayout) mainLin.getChildAt(id + start_id);
        ll.setBackgroundColor(getResources().getColor(R.color.colorWhite));
    }

    public void InitializeStartIndexNorm(){
        LayoutInflater li = LayoutInflater.from(this);
        View promptsView = li.inflate(R.layout.start_position, null);
        final EditText et = promptsView.findViewById(R.id.startPosition);
        //Создаем AlertDialog
        AlertDialog.Builder mDialogBuilder = new AlertDialog.Builder(this);

        //Настраиваем prompt.xml для нашего AlertDialog:
        mDialogBuilder.setView(promptsView);

        //Настраиваем сообщение в диалоговом окне:
        mDialogBuilder.setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                try{
                                    idx = Integer.parseInt(et.getText().toString());
                                }catch(Exception e){
                                    idx = 0;
                                }
                                HandleEverything();
                            }
                        });
        AlertDialog alertDialog = mDialogBuilder.create();
        //и отображаем его:
        alertDialog.show();
    }

    public void InitializeStartIndexDebug(){
        LayoutInflater li = LayoutInflater.from(this);
        View promptsView = li.inflate(R.layout.start_position, null);
        final EditText et = promptsView.findViewById(R.id.startPosition);
        //Создаем AlertDialog
        AlertDialog.Builder mDialogBuilder = new AlertDialog.Builder(this);

        //Настраиваем prompt.xml для нашего AlertDialog:
        mDialogBuilder.setView(promptsView);
        //Настраиваем сообщение в диалоговом окне:
        mDialogBuilder.setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                try{
                                    idx = Integer.parseInt(et.getText().toString());
                                }catch(Exception e){
                                    idx = 0;
                                }
                                Handle1Line();
                            }
                        });
        AlertDialog alertDialog = mDialogBuilder.create();
        //и отображаем его:
        alertDialog.show();
    }

    private String ToString(int a){
        return Integer.toString(a);
    }

    public void HandleEverything() {
        while (idx < ROWS) {
            if (idx == 0 || !isInitialized) {
                isInitialized = true;
                Initialize();
                if (!AnalysCode()) {
                    ErrorFound();
                    haltApp();
                    return;
                }
            }
            String str;
            str = MachineCodeList.get(idx).second;
            str = str.trim();
            int indexp = str.indexOf(" ");
            String s_addr = "";
            if (indexp == -1) {
                indexp = str.length();
            } else {
                s_addr = str.substring(indexp + 1);
            }
            String s_command = str.substring(0, indexp);
            if (s_command.isEmpty() || s_command.equals("-") || usd[idx] != 1) {
                haltApp();
                return;
            }
            else {
                s_command = CommandByNum.get(Integer.parseInt(s_command));

                switch (s_command) {
                    case IN:
                        InputNumber(true);
                        return;
                    case OUT:
                        OutputNumber(true);
                        return;
                    case HALT:
                        haltApp();
                        return;
                    case ADD:
                        AddNumber(s_addr);
                        break;
                    case SUB:
                        SubNumber(s_addr);
                        break;
                    case CMP:
                        CmpNumbers(s_addr);
                        break;
                    case LD:
                        LoadNumber(s_addr);
                        break;
                    case ST:
                        UploadNumber(s_addr);
                        break;
                    case LA:
                        LoadAddress(s_addr);
                        break;
                    case JMP:
                        UnconditionalJump(s_addr);
                        break;
                    case JM:
                        MinusJump(s_addr);
                        break;
                    case JZ:
                        ZeroJump(s_addr);
                        break;
                    default:
                        haltApp();
                        return;
                }
            }
        }
    }

    public void Handle1Line() {
        if (idx < ROWS) {
            if (idx == 0 || !isInitialized) {
                isInitialized = true;
                Initialize();
                if (!AnalysCode()) {
                    ErrorFound();
                    haltApp();
                    return;
                }
            }
            String str;
            str = MachineCodeList.get(idx).second;
            str = str.trim();
            int indexp = str.indexOf(" ");
            String s_addr = "";
            if (indexp == -1) {
                indexp = str.length();
            } else {
                s_addr = str.substring(indexp + 1);
            }
            String s_command = str.substring(0, indexp);
            if (s_command.isEmpty() || s_command.equals("-") || usd[idx] != 1) {
                haltApp();
                return;
            }
            else {
                s_command = CommandByNum.get(Integer.parseInt(s_command));
                if (!s_addr.isEmpty()){
                    int id = Integer.parseInt(s_addr);
                    if (id < 0){
                        idx += 3;
                        logs.add("Ошибка. Отрицательный адрес " + FourDigitAddress(id)+ ". Строка " + ToString(idx) +".");
                        return;
                    }
                }
                switch (s_command) {
                    case IN:
                        InputNumber(false);
                        return;
                    case OUT:
                        OutputNumber(false);
                        return;
                    case HALT:
                        haltApp();
                        return;
                    case ADD:
                        AddNumber(s_addr);
                        break;
                    case SUB:
                        SubNumber(s_addr);
                        break;
                    case CMP:
                        CmpNumbers(s_addr);
                        break;
                    case LD:
                        LoadNumber(s_addr);
                        break;
                    case ST:
                        UploadNumber(s_addr);
                        break;
                    case LA:
                        LoadAddress(s_addr);
                        break;
                    case JMP:
                        UnconditionalJump(s_addr);
                        break;
                    case JM:
                        MinusJump(s_addr);
                        break;
                    case JZ:
                        ZeroJump(s_addr);
                        break;
                    default:
                        haltApp();
                        return;
                }
            }
        }
    }

    public void haltApp() {
        idx = 0;
        isInitialized = false;
        sum = 0;
        Z = 0;
    }

    public void setCommandAt(int id, String command) {
        TextView v = (TextView) ((LinearLayout) mainLin.getChildAt(id + start_id)).getChildAt(1);
        v.setText(command);
    }

    public void setCommentAt(int id, String command){
        EditText v = (EditText) ((LinearLayout) mainLin.getChildAt(id + start_id)).getChildAt(2);
        v.setText(command);
    }

    public void setLabelAt(int id, String label) {
        TextView v = (TextView) ((LinearLayout) mainLin.getChildAt(id + start_id)).getChildAt(0);
        v.setText(label);
    }

    public String getCommandAt(int id) {
        TextView v = (TextView) ((LinearLayout) mainLin.getChildAt(id + start_id)).getChildAt(1);
        return v.getText().toString();
    }

    public String getCommentAt(int id) {
        EditText v = (EditText) ((LinearLayout) mainLin.getChildAt(id + start_id)).getChildAt(2);
        return v.getText().toString();
    }

    public String getLabelAt(int id) {
        TextView v = (TextView) ((LinearLayout) mainLin.getChildAt(id + start_id)).getChildAt(0);
        return v.getText().toString();
    }

    public void Initialize() {
        MachineCodeList.clear();
        MachineCodeComments.clear();
        for (int i = 0; i < ROWS; ++i) {
            MachineCodeList.add(new Pair(getLabelAt(i), getCommandAt(i)));
            MachineCodeComments.add("");
        }

        for (int i = 0; i < MAX_SIZE; ++i) {
            map[i] = 0;
        }

        for (int i = 0; i < ROWS; ++i) {
            LinearLayout linLayout = ((LinearLayout) mainLin.getChildAt(i + start_id));
            linLayout.setBackgroundColor(getResources().getColor(R.color.colorWhite));
        }
    }

    public void UpdateZ() {
        int oldZ = Z;
        Z = sum > 0 ? 1 : (sum < 0 ? -1 : 0);
        if (Z != oldZ)
            logs.add("Регистр 'Z' изменился с " + ToString(oldZ) + " на " + ToString(Z) + ".");
    }

    public void InputNumber(final boolean everything) {
        LayoutInflater li = LayoutInflater.from(this);
        View promptsView = li.inflate(R.layout.prompt, null);

        //Создаем AlertDialog
        AlertDialog.Builder mDialogBuilder = new AlertDialog.Builder(this);

        //Настраиваем prompt.xml для нашего AlertDialog:
        mDialogBuilder.setView(promptsView);

        //Настраиваем отображение поля для ввода текста в открытом диалоге:
        final EditText userInput = promptsView.findViewById(R.id.input_text);

        //Настраиваем сообщение в диалоговом окне:
        mDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                String number = "";
                                try{
                                    number = userInput.getText().toString().trim();
                                    sum = Integer.parseInt(number);
                                }catch (Exception e){
                                    sum = 0;
                                }
                                logs.add("Пользователь ввел число - " + ToString(sum) + ".");
                                UpdateZ();
                                idx++;
                                if (everything)
                                    HandleEverything();
                                else {
                                    setColorDefault(last_idx);
                                }
                            }
                        });
        AlertDialog alertDialog = mDialogBuilder.create();
        //и отображаем его:
        alertDialog.show();
    }

    public void OutputNumber(final boolean everything) {
        //TODO: GUI output
        LayoutInflater li = LayoutInflater.from(this);
        View promptsView = li.inflate(R.layout.load_layout, null);

        final LinearLayout load_layout = promptsView.findViewById(R.id.load_linear_layout);
        load_layout.removeAllViews();

        TextView tv = new TextView(this);
        tv.setTypeface(addr.getTypeface());
        tv.setText("Окно вывода:");
        tv.setTextSize(30);
        tv.setTextColor(Color.BLACK);
        load_layout.addView(tv);

        TextView tv2 = new TextView(this);
        tv2.setTypeface(command.getTypeface());
        tv2.setText(ToString(sum));
        tv2.setTextSize(30);
        tv2.setTextColor(Color.BLACK);
        load_layout.addView(tv2);

        last_idx = idx;
        logs.add("Выводится число на экран - " + ToString(sum) + ".");
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setView(promptsView);
        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                idx++;
                if (everything)
                    HandleEverything();
                else {
                    setColorDefault(last_idx);
                }
            }
        });
        alert.create().show();
    }

    public void ErrorFound() {
        //TODO: GUI output
        LayoutInflater li = LayoutInflater.from(this);
        View promptsView = li.inflate(R.layout.load_layout, null);

        //Создаем AlertDialog
        final AlertDialog.Builder mDialogBuilder = new AlertDialog.Builder(this);

        //Настраиваем prompt.xml для нашего AlertDialog:
        mDialogBuilder.setView(promptsView);

        //Настраиваем отображение поля для ввода текста в открытом диалоге:
        final LinearLayout load_layout = promptsView.findViewById(R.id.load_linear_layout);
        load_layout.removeAllViews();

        TextView tv = new TextView(this);
        tv.setTypeface(addr.getTypeface());
        tv.setText("Найдены ошибки. Проверьте логи!");
        tv.setTextSize(30);
        tv.setTextColor(Color.BLACK);
        load_layout.addView(tv);

        //Настраиваем сообщение в диалоговом окне:
        mDialogBuilder
                .setPositiveButton("Отмена",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
        final AlertDialog alertDialog = mDialogBuilder.create();
        alertDialog.show();
    }

    public void AddNumber(String s_addr) {
        int id = Integer.parseInt(s_addr);
        logs.add("Попытка добавить число по адресу " + FourDigitAddress(id) + " к сумматору.");
        if (usd[id] == 3) {
            int value = map[id] * 100 + map[id + 1];
            sum += value;
            LogAction(idx, "ADD", "Сумматор теперь равен " + ToString(sum) +".");
            UpdateZ();
        }
        idx += 3;
        // TODO: throw an error
    }

    public void SubNumber(String s_addr) {
        int id = Integer.parseInt(s_addr);
        logs.add("Пытаемся вычесть число по адресу " + FourDigitAddress(id) + " из сумматора.");
        if (usd[id] == 3) {
            int value = map[id] * 100 + map[id + 1];
            sum -= value;
            LogAction(idx, "SUB", "Сумматор теперь равен " + ToString(sum) +".");
            UpdateZ();
        }
        idx += 3;
        // TODO: throw an error
    }

    public void CmpNumbers(String s_addr) {
        int id = Integer.parseInt(s_addr);
        logs.add("Сравниваем число по адресу " + FourDigitAddress(id) + " с сумматором.");
        if (usd[id] == 3) {
            int value = sum - (map[id] * 100 + map[id + 1]);
            int oldZ = Z;
            Z = value > 0 ? 1 : (value < 0 ? -1 : 0);
            logs.add("Регистр 'Z' теперь равен " + ToString(Z) + ".");
        }
        idx += 3;
    }

    public void LoadNumber(String s_addr) {
        int id = Integer.parseInt(s_addr);
        logs.add("Пытаемся загрузить число из адреса " + FourDigitAddress(id) + ".");
        if (usd[id] == 3) {
            LogAction(idx, "LD", " Число " + ToString(map[id] * 100 + map[id + 1]) + " загружено в сумматор.");
            sum = map[id] * 100 + map[id + 1];
            UpdateZ();
        }
        idx += 3;
    }

    public void UploadNumber(String s_addr) {
        last_idx = idx;
        int id = Integer.parseInt(s_addr);
        logs.add("Пытаемся сохранить содержимое сумматора по адресу " + FourDigitAddress(id) + ".");
        if ((usd[id] == 0 && usd[id + 1] == 0) || (usd[id] == 3 && usd[id + 1] == 2)) {
            //setCommandAt(id, ToString(sum));
            LogAction(idx, "ST", " Загрузили " + ToString(sum) + " в адрес " + FourDigitAddress(id) + ".");
            map[id] = sum / 100;
            map[id + 1] = sum % 100;
            setCommandAt(id, ToString(map[id]));
            setCommandAt(id + 1, ToString(map[id + 1]));
            usd[id] = 3;
            usd[id + 1] = 2;
            UpdateZ();
        }else{
            logs.add("Ошибка. Не смогли загрузить число. Адрес не пустой.");
            ErrorFound();
        }
        idx += 3;
    }

    public void LoadAddress(String s_addr) {
        last_idx = idx;
        int id = Integer.parseInt(s_addr);
        LogAction(idx, "LA", " Число по адресу " + FourDigitAddress(id) + " загружено в сумматор.");
        sum = id;
        idx += 3;
    }

    public void UnconditionalJump(String s_addr) {
        last_idx = idx;
        int id = Integer.parseInt(s_addr);
        idx = id;
        LogAction(idx, "JMP", " Переход к адресу " + FourDigitAddress(idx) + ".");
    }

    public void ZeroJump(String s_addr) {
        logs.add("Попытка сделать операцию JZ в строке " + ToString(idx) + ".");
        if (Z == 0) {
            last_idx = idx;
            int id = Integer.parseInt(s_addr);
            idx = id;
            LogAction(idx, "JZ", " Переход к адресу " + FourDigitAddress(idx) + ".");
        }else{
            idx+=3;
        }
    }

    public void MinusJump(String s_addr) {
        logs.add("Попытка сделать операцию JM в строке " + ToString(idx) + ".");
        if (Z < 0) {
            last_idx = idx;
            int id = Integer.parseInt(s_addr);
            idx = id;
            LogAction(idx, "JM", " Переход к адресу " + FourDigitAddress(idx) + ".");
        }else{
            idx+=3;
        }

    }

    public void LogError(int father_id, int child_id, ErrorType TYPE_OF_ERROR) {
        if (TYPE_OF_ERROR == ErrorType.WRONG_PLACE_COMMAND)
            logs.add("Ошибка. Адрес: " + FourDigitAddress(child_id) + ". Неправильное место (посмотрите на адрес " + FourDigitAddress(father_id) + ")");
        if (TYPE_OF_ERROR == ErrorType.NOT_EXISTING_COMMAND)
            logs.add("Ошибка. Адрес: " + FourDigitAddress(father_id) + ". Нет такой команды");
        if (TYPE_OF_ERROR == ErrorType.WRONG_COMMAND_FORMAT)
            logs.add("Ошибка. Адрес: " + FourDigitAddress(father_id) + ". Команда неправильная (содержит ошибку)");
        if (TYPE_OF_ERROR == ErrorType.WRONG_ADDRESS)
            logs.add("Ошибка. Адрес: " + FourDigitAddress(father_id) + ". Команда содержит неправильный адрес (или метку)");
        if (TYPE_OF_ERROR == ErrorType.WRONG_PLACE_COMMAND_ASM)
            logs.add("Ошибка. Код в ASM MIK, строка: " + ToString(child_id) + ". Неправильное место (посмотрите на адрес " + FourDigitAddress(father_id) + ")");
        if (TYPE_OF_ERROR == ErrorType.NOT_EXISTING_COMMAND_ASM)
            logs.add("Ошибка. Код в ASM MIK, строка: " + ToString(father_id) + ". Нет такой команды");
        if (TYPE_OF_ERROR == ErrorType.WRONG_COMMAND_FORMAT_ASM)
            logs.add("Ошибка. Код в ASM MIK, строка: " + ToString(father_id) + ". Команда неправильная (содержит ошибку)");
        if (TYPE_OF_ERROR == ErrorType.WRONG_ADDRESS_ASM)
            logs.add("Ошибка. Код в ASM MIK, строка: " + ToString(father_id) + ". Команда содержит неправильный адрес (или метку)");
    }

    public void LogAction(int id, String action, String commentary){
        logs.add("Произведена операция '" + action + "' по адресу " + FourDigitAddress(id) + ". " + commentary);
    }

    public boolean FindLabel(String label) {
        return labels_LA.containsKey(label);
    }

    public boolean AnalysCode() {
        int err_cnt = 0;
        int id = 0;
        while (id < ROWS) {
            String str;
            str = getCommandAt(id);
            str = str.trim();
            int indexp = str.indexOf(" ");
            String s_addr = "";
            if (indexp == -1) {
                indexp = str.length();
            } else {
                s_addr = str.substring(indexp + 1);
            }
            String s_command = str.substring(0, indexp);
            if (s_command.isEmpty()  || s_command.equals("-")) {
                id++;
                continue;
            }

            int command_num = 0;
            boolean isNum = true;
            try {
                if (!s_command.isEmpty())
                    command_num = Integer.parseInt(s_command);
            } catch (Exception e) {
                isNum = false;
                err_cnt++;
                LogError(id, id, ErrorType.NOT_EXISTING_COMMAND);
            }
            if (!isNum) {
                id++;
                continue;
            }
            if (usd[id] == 3 && usd[id + 1] == 2){
                try{
                    Integer.parseInt(getCommandAt(id));
                    Integer.parseInt(getCommandAt(id + 1));
                }catch (Exception e){
                    err_cnt++;
                    logs.add("Ошибка. Адрес " + FourDigitAddress(id) + " или " + FourDigitAddress(id + 1) + ". Невозможно распознать число.");
                }
                id += 2;
                continue;
            }else {
                s_command = CommandByNum.get(command_num);
                if (set_1step.contains(s_command)) {
                    if (!s_addr.isEmpty()) {
                        err_cnt++;
                        LogError(id, id, ErrorType.WRONG_COMMAND_FORMAT);
                    }
                } else {
                    if (!getCommandAt(id + 1).trim().isEmpty() && !getCommandAt(id + 1).equals("-")){
                        err_cnt++;
                        LogError(id, id + 1, ErrorType.WRONG_PLACE_COMMAND);
                    }
                    if (!getCommandAt(id + 2).trim().isEmpty() && !getCommandAt(id + 2).equals("-")) {
                        err_cnt++;
                        LogError(id, id + 2, ErrorType.WRONG_PLACE_COMMAND);
                    }
                    try {
                        int ad = Integer.parseInt(s_addr);
                        if (!(ad >= 0 && ad < ROWS) && (!s_command.equals(LA))) {
                            err_cnt++;
                            LogError(id, id, ErrorType.WRONG_ADDRESS);
                        }else if ((s_command.equals(LA)) && (ad < 0 && ad >= 10000)){
                            err_cnt++;
                            LogError(id, id, ErrorType.WRONG_ADDRESS);
                        }
                    } catch (Exception e) {
                        err_cnt++;
                        LogError(id, id, ErrorType.WRONG_ADDRESS);
                    }
                }
            }
            id++;
        }
        return (err_cnt == 0);
    }

    // Label + command (as word)
    public void FromAssemblerToMachineCode() {
        MachineCodeList.clear();
        MachineCodeComments.clear();
        labels_LA.clear();
        for(int i = 0; i < MAX_SIZE; ++i) {
            usd[i] = 0;
        }
        int cur_idx = 0; // номер линии в машинном коде
        for (int i = 0; i < AssemblerCodeList.size(); ++i) {
            String cur_label = AssemblerCodeList.get(i).first;
            if (!cur_label.isEmpty())
                labels_LA.put(cur_label, cur_idx);
            String cur_str = AssemblerCodeList.get(i).second;
            cur_str = cur_str.trim();
            int indexp = cur_str.indexOf(" ");
            String s_addr = "";
            if (indexp == -1) {
                indexp = cur_str.length();
            } else {
                s_addr = cur_str.substring(indexp + 1);
            }
            String cur_command = cur_str.substring(0, indexp);
            if (set_3step.contains(cur_command))
                cur_idx += 3;
            else if (!cur_label.isEmpty() && cur_command.isEmpty())
                cur_idx += 2;
            else
                cur_idx++;
        }
        cur_idx = 0;
        for (int i = 0; i < AssemblerCodeList.size(); ++i) {
            MachineCodeComments.add(Comments.get(i));
            String str = AssemblerCodeList.get(i).second;
            String lbl = AssemblerCodeList.get(i).first;
            str = str.trim();
            int indexp = str.indexOf(" ");
            String s_addr = "";
            if (indexp == -1) {
                indexp = str.length();
            } else {
                s_addr = str.substring(indexp + 1);
            }
            String s_command = str.substring(0, indexp);
            if (s_command.isEmpty() && !lbl.isEmpty()){
                MachineCodeList.add(new Pair<String, String>(FourDigitAddress(cur_idx), "-"));
                MachineCodeList.add(new Pair<String, String>(FourDigitAddress(cur_idx + 1), "-"));
                MachineCodeComments.add("");
                usd[cur_idx] = 3;
                usd[cur_idx + 1] = 2;
                cur_idx += 2;
            }else if (s_command.isEmpty()) {
                MachineCodeList.add(new Pair<String, String>(FourDigitAddress(cur_idx), ""));
                cur_idx++;
            } else {
                if (!s_addr.isEmpty()) {
                    if (s_command.equals(LA))
                        MachineCodeList.add(new Pair<String, String>(FourDigitAddress(cur_idx), NumByCommand.get(s_command).toString() + " " + FourDigitAddress(Integer.parseInt(s_addr))));
                    else
                        MachineCodeList.add(new Pair<String, String>(FourDigitAddress(cur_idx), NumByCommand.get(s_command).toString() + " " + FourDigitAddress(labels_LA.get(s_addr))));
                    MachineCodeList.add(new Pair<String, String>(FourDigitAddress(cur_idx + 1), "-"));
                    MachineCodeList.add(new Pair<String, String>(FourDigitAddress(cur_idx + 2), "-"));
                    MachineCodeComments.add("");
                    MachineCodeComments.add("");
                    usd[cur_idx] = usd[cur_idx + 1] = usd[cur_idx + 2] = 1;
                    cur_idx += 3;
                } else {
                    String com = NumByCommand.get(s_command).toString();
                    if (com.length() == 1){
                        com = "0" + com;
                    }
                    MachineCodeList.add(new Pair<String, String>(FourDigitAddress(cur_idx), com));
                    usd[cur_idx] = 1;
                    cur_idx++;
                }
            }
        }
        return;
    }

    public void ShowAsmMik(){
        mainLin.setVisibility(View.GONE);
        asmLin.setVisibility(View.VISIBLE);
    }

    public void Back(){
        mainLin.setVisibility(View.VISIBLE);
        asmLin.setVisibility(View.GONE);
    }

    public void CompileCode(){
        FillAssemblerCode();
        ClearCode();
        if (AnalysAsmCode()) {
            ParseMacrosInAssemblerCode();
            FromAssemblerToMachineCode();
            InitializeComponents();
            InitializeFromMachineCode();
            Back();
        }else{
            ErrorFound();
        }
    }

    private void ParseMacrosInAssemblerCode(){
        boolean anyMacros = true;
        while (anyMacros) {
            anyMacros = false;
            for (int id = 0; id < AssemblerCodeList.size(); ++id) {
                String str = AssemblerCodeList.get(id).second;
                str = str.trim();
                int indexp = str.indexOf(" ");
                String s_addr = "";
                if (indexp == -1) {
                    indexp = str.length();
                } else {
                    s_addr = str.substring(indexp + 1);
                }
                String s_command = str.substring(0, indexp);

                if (SavedMacros.contains(s_command)) {
                    anyMacros = true;
                    ArrayList<Pair<String, String>> MacrosCode = GetMacrosCode(s_command, s_addr);
                    AssemblerCodeList.remove(id);
                    for (int i = 0; i < MacrosCode.size(); ++i) {
                        AssemblerCodeList.add(i + id, MacrosCode.get(i));
                        if (i != 0)
                            Comments.add(i + id, "");
                    }

                    id += MacrosCode.size() - 1;
                }
            }
        }
    }

    private ArrayList<Pair<String, String>> GetMacrosCode(String macros, String parameters){
        ArrayList<Pair<String, String>> MacrosCode = new ArrayList<Pair<String, String>>();
        List<String> parameters1 = new ArrayList<String>();
        List<String> parameters2 = Arrays.asList(parameters.split(" "));
        try{
            FileInputStream inputFile = openFileInput(macros.concat(".macros"));
            InputStreamReader reader = new InputStreamReader(inputFile);
            BufferedReader buffer = new BufferedReader(reader);
            String lines = "";
            int i = 0;
            lines = buffer.readLine();
            parameters1 = Arrays.asList(lines.split(" "));
            while ((lines = buffer.readLine()) != null){
                MacrosCode.add(new Pair(" " , lines));
            }
            inputFile.close();

            String file_label = macros.concat(".labels");
            inputFile = openFileInput(file_label);
            reader = new InputStreamReader(inputFile);
            buffer = new BufferedReader(reader);
            i = 0;
            while (i < MacrosCode.size() && (lines = buffer.readLine()) != null){
                MacrosCode.set(i, new Pair(lines, MacrosCode.get(i).second));
                i++;
            }
            inputFile.close();
        }catch (Exception e){
            logs.add("Не смогли открыть код макроса " + macros + ".");
            ErrorFound();
        }

        Set<String> macros_labels = new HashSet<String>();
        for(int id = 0; id < MacrosCode.size(); ++id){
            if (!MacrosCode.get(id).first.trim().isEmpty())
                macros_labels.add(MacrosCode.get(id).first);
        }
        for(int id = 0; id < MacrosCode.size(); ++id){
            String str = MacrosCode.get(id).second;
            String lbl = MacrosCode.get(id).first;
            if (!lbl.trim().isEmpty()){
                lbl = macros.concat("_" + lbl);
            }
            str = str.trim();
            int indexp = str.indexOf(" ");
            String s_addr = "";
            if (indexp == -1) {
                indexp = str.length();
            } else {
                s_addr = str.substring(indexp + 1);
            }
            String s_command = str.substring(0, indexp);
            if (SavedMacros.contains(s_command)){
                String[] localPar = s_addr.split(" ");
                for(int i = 0; i < localPar.length; ++i)
                {
                    if (macros_labels.contains(localPar[i]))
                        localPar[i] = macros.concat("_" + localPar[i]);
                }
                for(int i = 0; i < parameters1.size(); ++i){
                    for(int j = 0; j < localPar.length; ++j) {
                        if (parameters1.get(i).equals(localPar[j]))
                            localPar[j] = parameters2.get(i);
                    }
                }

                s_addr = "";
                for(int i = 0; i < localPar.length; ++i)
                {
                    s_addr = s_addr.concat(localPar[i] + " ");
                }
                s_addr = s_addr.trim();
            }else if (macros_labels.contains(s_addr)){
                s_addr = macros.concat("_" + s_addr);
            }

            for(int i = 0; i < parameters1.size(); ++i){
                if (parameters1.get(i).equals(s_addr))
                    s_addr = parameters2.get(i);
            }

            MacrosCode.set(id, new Pair(lbl, s_command + " " + s_addr));
        }
        return MacrosCode;
    }

    public void FillAssemblerCode(){
        AssemblerCodeList.clear();
        Comments.clear();
        for (int i = start_asm_id; i < asmLin.getChildCount(); ++i){
            LinearLayout linL = (LinearLayout)asmLin.getChildAt(i);
            EditText et_label = (EditText)linL.getChildAt(0);
            EditText et_comm = (EditText)linL.getChildAt(1);
            EditText et_comment = (EditText)linL.getChildAt(2);
            AssemblerCodeList.add(new Pair(et_label.getText().toString() ,et_comm.getText().toString()));
            Comments.add(et_comment.getText().toString());
        }
        for(int i = 0; i < ROWS; ++i){
            MachineCodeComments.add("");
        }
    }

    public boolean AnalysAsmCode() {
        labels_LA.clear();
        for (int i = 0; i < AssemblerCodeList.size(); ++i) {
            String label = AssemblerCodeList.get(i).first;
            if (!label.isEmpty())
                labels_LA.put(label, i);
        }
        int err_cnt = 0;
        for(int id = 0; id < AssemblerCodeList.size(); ++id){
            String str = AssemblerCodeList.get(id).second;
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
            if (SavedMacros.contains(s_command)){
                String parameters1 = str.substring(indexp + 1);
                String parameters2 = GetMacrosParameters(s_command);
                String[] labels = parameters1.split(" ");
                if (parameters1.split(" ").length != parameters2.split(" ").length){
                    err_cnt++;
                    logs.add("Неправильные параметры макроса в строке " + ToString(id) + ".");
                }
                for(int i = 0; i < labels.length; ++i){
                    if (!FindLabel(labels[i])){
                        err_cnt++;
                        logs.add("Не найдена метка '" + labels[i] + "', используемая в макросе '" + s_command + "'. Строка " + ToString(id) + ".");
                    }
                }
                continue;
            }
            if (!(set_1step.contains(s_command) || set_3step.contains(s_command))) {
                err_cnt++;
                LogError(id, id, ErrorType.NOT_EXISTING_COMMAND_ASM);
                continue;
            }
            if (set_1step.contains(s_command)) {
                if (!s_addr.isEmpty()) {
                    err_cnt++;
                    LogError(id, id, ErrorType.WRONG_COMMAND_FORMAT_ASM);
                }
            } else if (set_3step.contains(s_command)) {
                if (s_command.equals(LA)) {
                    try {
                        Integer.parseInt(s_addr);
                        continue;
                    } catch (Exception e) {
                        err_cnt++;
                        LogError(id, id, ErrorType.WRONG_COMMAND_FORMAT_ASM);
                    }
                }
                if (!FindLabel(s_addr)) {
                    err_cnt++;
                    LogError(id, id, ErrorType.WRONG_ADDRESS_ASM);
                }
            }
        }
        return (err_cnt == 0);
    }

    public void ShowLogs(){
        LayoutInflater li = LayoutInflater.from(this);
        View promptsView = li.inflate(R.layout.logs, null);

        //Создаем AlertDialog
        AlertDialog.Builder mDialogBuilder = new AlertDialog.Builder(this);

        //Настраиваем prompt.xml для нашего AlertDialog:
        mDialogBuilder.setView(promptsView);
        //Настраиваем отображение поля для ввода текста в открытом диалоге:
        final TextView programLogs = promptsView.findViewById(R.id.logsTextView);
        programLogs.setText("Логи:\n");
        for(int i = 0; i < logs.size(); ++i){
            programLogs.setText(programLogs.getText().toString() + '\n' + logs.get(i));
        }

        //Настраиваем сообщение в диалоговом окне:
        mDialogBuilder
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
        AlertDialog alertDialog = mDialogBuilder.create();
        //и отображаем его:
        alertDialog.show();
    }

    public void ShowRegisters(){
        LayoutInflater li = LayoutInflater.from(this);
        View promptsView = li.inflate(R.layout.registers, null);

        //Создаем AlertDialog
        AlertDialog.Builder mDialogBuilder = new AlertDialog.Builder(this);

        //Настраиваем prompt.xml для нашего AlertDialog:
        mDialogBuilder.setView(promptsView);
        mDialogBuilder.setMessage("Регистры:");
        //Настраиваем отображение поля для ввода текста в открытом диалоге:
        final TextView tv_sum = promptsView.findViewById(R.id.registerSummator);
        final TextView tv_z = promptsView.findViewById(R.id.registerZ);
        final TextView tv_ra = promptsView.findViewById(R.id.registerRA);
        final TextView tv_rk = promptsView.findViewById(R.id.registerRK);
        final TextView tv_r1 = promptsView.findViewById(R.id.registerR1);
        tv_sum.setText(ToString(sum));
        tv_z.setText(ToString(Z));
        tv_ra.setText(ToString(idx));
        String str = getCommandAt(last_idx);
        str = str.trim();
        int indexp = str.indexOf(" ");
        if (indexp == -1) {
            indexp = str.length();
        }
        final String oldRA = tv_ra.getText().toString();
        String s_command = str.substring(0, indexp);
        tv_rk.setText(s_command);
        tv_r1.setText("0");
        //Настраиваем сообщение в диалоговом окне:
        mDialogBuilder
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
        AlertDialog alertDialog = mDialogBuilder.create();
        //и отображаем его:
        alertDialog.show();
    }

    public void ClearCode(){
        haltApp();
        EditText et_data_first = findViewById(R.id.AssemblerCodeDataAddressFirst);
        EditText et_data_second = findViewById(R.id.AssemblerCodeDataAddressSecond);
        EditText et_comm = findViewById(R.id.AssemblerCodeCommandCode);
        EditText et_addr = findViewById(R.id.AssemblerCodeCommandAddress);
        et_comm.setText("0");
        et_addr.setText("0");
        et_data_first.setText("0");
        et_data_second.setText("0");
        for(int i = 0; i < ROWS; ++i){
            setCommandAt(i, "");
            setCommentAt(i, "");
        }
        Initialize();
    }

    public void ClearLogs(){
        logs.clear();
    }

    public void EnterCommand(){
        EditText et_comm = findViewById(R.id.AssemblerCodeCommandCode);
        EditText et_addr = findViewById(R.id.AssemblerCodeCommandAddress);
        EditText et_data_first = findViewById(R.id.AssemblerCodeDataAddressFirst);
        EditText et_data_second = findViewById(R.id.AssemblerCodeDataAddressSecond);
        String first_num = et_data_first.getText().toString();
        String second_num = et_data_second.getText().toString();
        first_num = first_num.isEmpty() ? "0" : first_num;
        second_num = second_num.isEmpty() ? "0" : second_num;
        int fst = Integer.parseInt(first_num);
        int scnd = Integer.parseInt(second_num);
        String num = ToString(fst * 100 + scnd);
        logs.add("Попытка добавить команду '" + et_comm.getText().toString() + "' в адрес '" + FourDigitAddress(Integer.parseInt(et_addr.getText().toString())) + "'.");
        try{
            int ad;
            try{
                ad = Integer.parseInt(et_addr.getText().toString());
            } catch (Exception e){
                ad = 0;
            }
            if (set_3step.contains(CommandByNum.get(Integer.parseInt(et_comm.getText().toString())))) {
                setCommandAt(ad, et_comm.getText().toString() + " " + FourDigitAddress(Integer.parseInt(num)));
                setCommandAt(ad + 1, "-");
                setCommandAt(ad + 2, "-");
                usd[ad] = usd[ad + 1] = usd[ad + 2] = 1;
                int id = fst * 100 + scnd;
                if (!et_comm.getText().toString().equals("23")){
                    usd[id] = 3;
                    usd[id + 1] = 2;
                }
                ad += 3;
            }else{
                setCommandAt(ad, et_comm.getText().toString());
                usd[ad] = 1;
                ad++;
            }
            et_addr.setText(FourDigitAddress(ad));
        }catch (Exception e){
            ErrorFound();
            logs.add("Ошибка при попытке добавить команду.");
        }
    }

    public void EnterData(){
        logs.add("Пытаемся добавить число вручную.");
        EditText et_addr = findViewById(R.id.AssemblerCodeCommandAddress);
        EditText et_data_first = findViewById(R.id.AssemblerCodeDataAddressFirst);
        EditText et_data_second = findViewById(R.id.AssemblerCodeDataAddressSecond);
        String num = et_data_first.getText().toString() + et_data_second.getText().toString();
        if (num.length() > 4){
            logs.add("Ошибка. Введенные данные неправильные. Допустимы только 4-х символьные числа.");
            ErrorFound();
        }else{
            try{
                int id = Integer.parseInt(et_addr.getText().toString());
                if (id >= 0 && id < ROWS) {
                    if ((usd[id] == 0 && usd[id + 1] == 0) || (usd[id] == 3 && usd[id + 1] == 2)) {
                        usd[id] = 3;
                        usd[id + 1] = 2;
                        map[id] = Integer.parseInt(et_data_first.getText().toString());
                        map[id + 1] = Integer.parseInt(et_data_second.getText().toString());
                        setCommandAt(id, et_data_first.getText().toString());
                        setCommandAt(id + 1, et_data_second.getText().toString());
                    }
                }else{
                    logs.add("Не можем запиcать в адрес " + FourDigitAddress(Integer.parseInt(ToString(id))) + ". Содержимое по адресу не пусто или лежит не в границах оперативной памяти.");
                    ErrorFound();
                }
            }catch(Exception e){
                logs.add("Ошибка при обработке введенных данных.");
                ErrorFound();
            }
        }
    }

    private void ShowMacros(){
        LayoutInflater li = LayoutInflater.from(this);
        View promptsView = li.inflate(R.layout.load_layout, null);

        //Создаем AlertDialog
        final AlertDialog.Builder mDialogBuilder = new AlertDialog.Builder(this);

        //Настраиваем prompt.xml для нашего AlertDialog:
        mDialogBuilder.setView(promptsView);

        //Настраиваем отображение поля для ввода текста в открытом диалоге:
        final LinearLayout load_layout = promptsView.findViewById(R.id.load_linear_layout);
        load_layout.removeAllViews();

        TextView tv = new TextView(this);
        tv.setTypeface(addr.getTypeface());
        tv.setText("Макросы:");
        tv.setTextSize(25);
        tv.setTextColor(Color.BLACK);
        load_layout.addView(tv);

        //Настраиваем сообщение в диалоговом окне:
        mDialogBuilder
                .setPositiveButton("Отмена",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
        final AlertDialog alertDialog = mDialogBuilder.create();

        for (final String s : SavedMacros) {
            final LinearLayout new_layout = new LinearLayout(this);
            new_layout.setOrientation(LinearLayout.HORIZONTAL);
            final TextView tv_macros = new TextView(this);
            tv_macros.setText(s);
            tv_macros.setTextSize(20);
            tv_macros.setTextColor(Color.BLACK);
            tv_macros.setTypeface(addr.getTypeface());
            new_layout.addView(tv_macros, 0);
            load_layout.addView(new_layout);
        }
        //и отображаем его:
        alertDialog.show();

        return;
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
            logs.add("Не получилось открыть файл " + macros.concat(".macros") + ".");
            ErrorFound();
        }
        return lines;
    }
}
