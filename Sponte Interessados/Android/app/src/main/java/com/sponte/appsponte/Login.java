package com.sponte.appsponte;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.sponte.appsponte.WebService.PrimaryDataTask;

public class Login extends AppCompatActivity {
    private static final String TAG = " >> Login";
    public static String sCodusuario;
    public static Integer sCodClisponte;
    public static String sFuncionarioID;
    private String sLogin, sSenha;
    EditText txtLogin, txtSenha;
    Button btnEntrar;
    private View mProgressView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        mProgressView = findViewById(R.id.login_progress);

        txtLogin = (EditText) findViewById(R.id.txtLogin);
        txtSenha = (EditText) findViewById(R.id.txtSenha);
        btnEntrar = (Button) findViewById(R.id.btnEntrar);

        SharedPreferences sP = getSharedPreferences("LoginSenha", MODE_PRIVATE);
        sLogin = sP.getString("sLogin", sLogin);
        sSenha = sP.getString("sSenha", sSenha);

        if ((sLogin.length() > 0) && (sSenha.length() > 0) && sLogin.contains("@")) {
            ValidaLogin(sLogin, sSenha);
        }
        
        btnEntrar.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 sLogin = txtLogin.getText().toString();
                 sSenha = txtSenha.getText().toString();

                 if ((sLogin.length() > 0) && (sSenha.length() > 0) && sLogin.contains("@")) {
                     ValidaLogin(sLogin, sSenha);
                 } else {
                     Toast.makeText(Login.this, "Verifique o usuário e senha.", Toast.LENGTH_SHORT).show();
                     showProgress(false);
                 }
             }
         }

        );
    }

    private void showProgress(final boolean show) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }

    private void ValidaLogin(String usuario, String senha) {
        new PrimaryDataTask("", 0, 0, "GET","EfetuaLogin", usuario, senha, 0,"","","","","","","","","", new PrimaryDataTask.OnReturnServicePrimary() {
            @Override
            public void onCompletion(String RetornoWS) {
                if (RetornoWS.length() > 0) {
                    SharedPreferences sharedPreferences = getSharedPreferences("LoginSenha", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    String sRetorno;
                    String sCID;
                    String sNomeUsuario;
                    sRetorno = RetornoWS.substring(RetornoWS.indexOf("<cid>") + 5, RetornoWS.indexOf("</cid>"));
                    sCodusuario = RetornoWS.substring(RetornoWS.indexOf("<codusuario>") + 12, RetornoWS.indexOf("</codusuario>"));
                    sNomeUsuario = RetornoWS.substring(RetornoWS.indexOf("<nomeusuario>") + 13, RetornoWS.indexOf("</nomeusuario>"));
                    sFuncionarioID = RetornoWS.substring(RetornoWS.indexOf("<funcionarioID>") + 15, RetornoWS.indexOf("</funcionarioID>"));

                    String sEmpresas;
                    sEmpresas = RetornoWS.substring(RetornoWS.indexOf("<empresas>") + 10, RetornoWS.indexOf("</empresas>"));
                    String[] parts = sEmpresas.split("<empresa>");
                    editor.putInt("QuantidadeEmpresas", (parts.length - 1));
                    editor.putString("sNomeUsuario", sNomeUsuario);
                    editor.commit();

                    if (parts.length > 0) {
                        Integer i = 0;
                        String sNomeEmpresa;
                        String sLogado;
                        while (i < parts.length) {
                            try {
                                sNomeEmpresa = parts[i].substring(parts[i].indexOf("<nomemepresa>") + 13, parts[i].indexOf("</nomemepresa>"));
                                editor.putString("NomeEmpresa" + i, sNomeEmpresa);
                                sCodClisponte = Integer.valueOf(parts[i].substring(parts[i].indexOf("<codclisponte>") + 14, parts[i].indexOf("</codclisponte>")));
                                editor.putInt("CID" + i, sCodClisponte);
                                sLogado = parts[i].substring(parts[i].indexOf("<logado>") + 8, parts[i].indexOf("</logado>"));
                                if (sLogado.equals("1")) {
                                    editor.putInt("nPosition", i);
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                                Log.e("ERRO", e.toString());
                            }
                            i++;
                        }
                        if (sRetorno.equals("1")) {
                            sRetorno = "Usuário ou senha inválida.";
                            Toast.makeText(Login.this, sRetorno, Toast.LENGTH_SHORT).show();
                        } else if (sRetorno.equals("2")) {
                            sRetorno = "Usuário não encontrado.";
                            Toast.makeText(Login.this, sRetorno, Toast.LENGTH_SHORT).show();
                        } else if (sRetorno.equals("5")) {
                            sRetorno = "Usuário sem permissão de acesso.";
                            Toast.makeText(Login.this, sRetorno, Toast.LENGTH_SHORT).show();
                        } else if (sRetorno.equals("6")) {
                            sRetorno = "Usuário não encontrado.";
                            Toast.makeText(Login.this, sRetorno, Toast.LENGTH_SHORT).show();
                        } else if (sRetorno.equals("0")) {
                            sCID = RetornoWS.substring(RetornoWS.indexOf("<codclisponte>") + 14, RetornoWS.indexOf("</codclisponte>"));
                            SharedPreferences sharedPreferences2 = getSharedPreferences("LoginSenha", MODE_PRIVATE);
                            SharedPreferences.Editor editor2 = sharedPreferences2.edit();
                            editor2.putInt("sCID", Integer.valueOf(sCID));
                            editor2.putString("sLogin", sLogin);
                            editor2.putString("sSenha", sSenha);
                            editor2.putString("sCodusuario", sCodusuario);
                            editor2.commit();
                            Intent Simple = new Intent(getApplicationContext(), Lista_AlunosActivity.class);
                            startActivity(Simple);
                            finish();
                        }
                        showProgress(false);
                    }
                    editor.commit();
                }
            }

            @Override
            public void onError() {
                Toast.makeText(Login.this, "Erro ao validar login", Toast.LENGTH_SHORT).show();
                showProgress(false);
            }
        }).execute();
    }
}