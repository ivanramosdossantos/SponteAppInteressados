package com.sponte.appsponte;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.sponte.appsponte.WebService.PrimaryDataTask;

import java.util.HashMap;
import java.util.Map;

public class Lista_AlunosActivity extends AppCompatActivity {
    private Integer nCodCliSponte = Login.sCodClisponte;
    private String usuarioID = Login.sCodusuario;
    private String sRetorno;
    private ListView listaDeAlunos;
    private TextView txtAviso;
    private Button btnSair;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista__alunos);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        txtAviso = (TextView) findViewById(R.id.txtAviso);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.btnIncluir);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent Simple = new Intent(getApplicationContext(), Formulario_AlunoActivity.class);
                //Intent Simple = new Intent(getApplicationContext(), teste.class);
                startActivity(Simple);
            }
        });
        listaDeAlunos = (ListView) findViewById(R.id.lista);
        carregaLista(usuarioID);
    }

    @Override
    protected void onResume() {
        super.onResume();
        carregaLista(usuarioID);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    private void carregaLista(String usuarioID) {
        new PrimaryDataTask("", Integer.parseInt(usuarioID), nCodCliSponte, "GET","RetAlunos", "", "", 0,"","","","","","","","","", new PrimaryDataTask.OnReturnServicePrimary() {
            @Override
            public void onCompletion(String RetornoWS) {
                String sRetorno = RetornoWS.substring(RetornoWS.indexOf("<Alunos>") + 8, RetornoWS.indexOf("</Alunos>"));
                String sAlunos = sRetorno = RetornoWS.substring(8,RetornoWS.length()-8);
                String sAlunosNome[] = sAlunos.split("<Nome>");
                String NomeAlunos[] = sAlunos.split("<Nome>");
                String sAlunosTelefone[] = sAlunos.split("<FoneCelular>");
                String sAlunosEmail[] = sAlunos.split("<Email>");

                for (int i = 1; i <= sAlunosNome.length - 1; i++){
                    NomeAlunos[i] = "Nome: "+sAlunosNome[i].substring(0, sAlunosNome[i].indexOf("</Nome>"))+
                    "                                                                 Tel: " +sAlunosTelefone[i].substring(0, sAlunosTelefone[i].indexOf("</FoneCelular>"))+
                    "                                                                 E-mail: " +sAlunosEmail[i].substring(0, sAlunosEmail[i].indexOf("</Email>"));
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(Lista_AlunosActivity.this, android.R.layout.simple_list_item_1,NomeAlunos);
                listaDeAlunos.setAdapter(adapter);
                if (NomeAlunos.length <= 1){
                    txtAviso.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void onError() {
                Toast.makeText(Lista_AlunosActivity.this, "Não foi possivel salvar o cadastro!", Toast.LENGTH_SHORT).show();
            }
        }).execute();
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_formulario_ok:
                SharedPreferences sharedPreferences = getSharedPreferences("LoginSenha", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("sLogin", "");
                editor.putString("sSenha", "");
                editor.commit();
                Toast.makeText(this,"Deslogado com sucesso!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, com.sponte.appsponte.Login.class));
                break;
            case R.id.sobre:
                startActivity(new Intent(this, com.sponte.appsponte.SobreActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
