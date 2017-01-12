package com.sponte.appsponte;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;
import com.sponte.appsponte.WebService.PrimaryDataTask;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Formulario_AlunoActivity extends AppCompatActivity {
    private Spinner spMidia,spCampanha;
    private Integer nCodCliSponte;
    private Map<String, String> arrayMidias = new HashMap<String, String>();
    private Map<String, String> arrayCampanhas = new HashMap<String, String>();
    private List<String> midias = new ArrayList<String>();
    private List<String> campanhas = new ArrayList<String>();
    private Button btnSalvar;
    private EditText txtObs;
    private EditText txtNome;
    private EditText txtTelefone;
    private RadioButton radio_fem;
    private EditText txtEmail;
    private String ncodUsuario = "";
    private String funcionarioID = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aluno);
        txtObs = (EditText) findViewById(R.id.Obs);
        spMidia = (Spinner) findViewById(R.id.spMidia);
        spCampanha = (Spinner) findViewById(R.id.spCampanha);
        btnSalvar = (Button) findViewById(R.id.btnSalvar);
        txtNome = (EditText) findViewById(R.id.edtnome);
        txtTelefone = (EditText) findViewById(R.id.edtcelular);
        txtEmail = (EditText) findViewById(R.id.edtemail);
        radio_fem = (RadioButton) findViewById(R.id.radio_fem);
        nCodCliSponte = Login.sCodClisponte;
        CarregaMidias(nCodCliSponte,getWindow().getDecorView().getRootView());
        CarregaCampanhas(nCodCliSponte,getWindow().getDecorView().getRootView());
        ncodUsuario = Login.sCodusuario;
        funcionarioID = Login.sFuncionarioID;

        btnSalvar.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                String nome;
                String foneCelular;
                String email;
                String campanha;
                String midia;
                String sexo;
                String observacao;
                String campanhaID ="";
                String midiaID = "";
                String dataAtual = null;
                try {
                    dataAtual = getDataAtual();
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                if(txtEmail.length() > 0 && txtNome.length() > 0 && txtTelefone.length() > 0){
                    if(radio_fem.isSelected()){
                        sexo = "F";
                    }else{
                        sexo = "M";
                    }
                    nome = String.valueOf(txtNome.getText());
                    foneCelular = String.valueOf(txtTelefone.getText());
                    email = String.valueOf(txtEmail.getText());
                    campanha = spCampanha.getSelectedItem().toString();
                    midia = spMidia.getSelectedItem().toString();
                    observacao = String.valueOf(txtObs.getText());

                    for (Map.Entry<String,String> entry : arrayMidias.entrySet()) {
                        if (entry.getValue().equals(midia)){
                            midiaID = entry.getKey();
                            break;
                        }
                    }
                    for (Map.Entry<String,String> entry : arrayCampanhas.entrySet()) {
                        if (entry.getValue().equals(campanha)){
                            campanhaID = entry.getKey();
                            break;
                        }
                    }
                    registraAluno(nCodCliSponte,0,nome,foneCelular,email,campanhaID,midiaID,ncodUsuario,dataAtual,sexo,funcionarioID,observacao);
                    LimpaCampos();
                    startActivity(new Intent(Formulario_AlunoActivity.this, com.sponte.appsponte.Lista_AlunosActivity.class));
                    finish();
                }else{
                    Toast.makeText(Formulario_AlunoActivity.this, "Todos os campos devem ser preenchidos!", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    private void LimpaCampos() {
        txtNome.setText("");
        txtTelefone.setText("");
        txtEmail.setText("");
        radio_fem.setSelected(false);
    }

    private void registraAluno(Integer nCodCliSponte, Integer alunoID, String nome, String foneCelular, String email, String campanhaID, String midiaID, String ncodUsuario, String pegaDataAtual, String sexo, String funcionarioID, String observacao) {
        new PrimaryDataTask("", Integer.parseInt(ncodUsuario), nCodCliSponte, "SET","InsertAlunos", "", "", 0,nome,foneCelular,email,campanhaID,midiaID,pegaDataAtual,sexo,funcionarioID,observacao, new PrimaryDataTask.OnReturnServicePrimary() {
            @Override
            public void onCompletion(String RetornoWS) {
                String sRetorno = RetornoWS.substring(RetornoWS.indexOf("<retorno>") + 9, RetornoWS.indexOf("</retorno>"));
                Toast.makeText(Formulario_AlunoActivity.this,sRetorno,Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError() {
                Toast.makeText(Formulario_AlunoActivity.this, "Não foi possivel salvar o cadastro!", Toast.LENGTH_SHORT).show();
            }
        }).execute();
    }

    public String getDataAtual() throws ParseException {
        Date data = new Date();
        SimpleDateFormat formatador = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        return formatador.format(data);
    }

    private void CarregaMidias(Integer CodCliSponte, View rootView) {
        new PrimaryDataTask("", 0, CodCliSponte, "GET","RetMidias", "", "",0,"","","","","","","","","", new PrimaryDataTask.OnReturnServicePrimary() {
            @Override
            public void onCompletion(String RetornoWS) {
                SharedPreferences sharedPreferences = getSharedPreferences("LoginSenha", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                if (!RetornoWS.equals("")){
                    String sMidias;
                    sMidias = RetornoWS.substring(8,RetornoWS.length()-8);
                    String sMidiaID[] = sMidias.split("<MidiaID>");
                    String sDescricao[] = sMidias.split("<Descricao>");

                    for (int i = 1; i <= sMidiaID.length - 1; i++){
                        arrayMidias.put(sMidiaID[i].substring(0, sMidiaID[i].indexOf("</MidiaID>")),
                                        sDescricao[i].substring(0, sDescricao[i].indexOf("</Descricao>")));
                        midias.add(sDescricao[i].substring(0, sDescricao[i].indexOf("</Descricao>")));
                    }
                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(Formulario_AlunoActivity.this, android.R.layout.simple_spinner_dropdown_item, midias);
                    ArrayAdapter<String> spinnerArrayAdapter = arrayAdapter;
                    spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
                    spMidia.setAdapter(spinnerArrayAdapter);
                }
            }
            @Override
            public void onError() {
                Toast.makeText(Formulario_AlunoActivity.this, "Erro ao validar login", Toast.LENGTH_SHORT).show();
            }
        }).execute();
    }
    private void CarregaCampanhas(Integer CodCliSponte, View rootView) {
        new PrimaryDataTask("", 0, CodCliSponte, "GET", "RetCampanhas", "", "",0,"","","","","","","","","",  new PrimaryDataTask.OnReturnServicePrimary() {
            @Override
            public void onCompletion(String RetornoWS) {
                SharedPreferences sharedPreferences = getSharedPreferences("LoginSenha", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                if (!RetornoWS.equals("")){
                    String sCampanhas;
                    sCampanhas = RetornoWS.substring(8,RetornoWS.length()-8);
                    String scampanhaID[] = sCampanhas.split("<campanhaID>");
                    String sNome[] = sCampanhas.split("<Nome>");

                    for (int i = 1; i <= scampanhaID.length - 1; i++){
                        arrayCampanhas.put(scampanhaID[i].substring(0, scampanhaID[i].indexOf("</campanhaID>")),
                                sNome[i].substring(0, sNome[i].indexOf("</Nome>")));
                        campanhas.add(sNome[i].substring(0, sNome[i].indexOf("</Nome>")));
                    }
                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(Formulario_AlunoActivity.this, android.R.layout.simple_spinner_dropdown_item, campanhas);
                    ArrayAdapter<String> spinnerArrayAdapter = arrayAdapter;
                    spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
                    spCampanha.setAdapter(spinnerArrayAdapter);
                }
            }
            @Override
            public void onError() {
                Toast.makeText(Formulario_AlunoActivity.this, "Erro ao validar login", Toast.LENGTH_SHORT).show();
            }
        }).execute();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_formulario_ok:
                SharedPreferences sharedPreferences = getSharedPreferences("LoginSenha", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("sLogin", "");
                editor.putString("sSenha", "");
                editor.commit();
                Toast.makeText(this,"Deslogado com sucesso!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(Formulario_AlunoActivity.this, com.sponte.appsponte.Login.class));
        }
        return super.onOptionsItemSelected(item);
    }
}

