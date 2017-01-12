package com.sponte.appsponte.WebService;

import android.os.AsyncTask;
import android.util.Log;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

public class PrimaryDataTask extends AsyncTask<Void, Void, Boolean> {

    private String sSenha = "spweb123";
    private Integer alunoID;
    private String mResponse;
    private OnReturnServicePrimary mListener;
    private String valorteste = "";
    private String TipoMetodo = "";
    private String NomeMetodo = "";
    private String sLoginUsuario = "";
    private String sSenhaUsuario = "";
    private Integer nCodCliSponte = 0;
    private String URL;
    private String NAMESPACE;
    private String campanhaID;
    private String midias;
    private String funcionarioID;
    private String nome;
    private String FoneCelular;
    private String email;
    private String sexo;
    private String observacao;
    private String datainclusao;
    private Integer nCodUsuario;

    public PrimaryDataTask(String s, Integer nUsuarioID, Integer nCodCliSponte, String TipoMetodo, String NomeMetodo, String sLoginUsuario, String sSenhaUsuario, Integer alunoID, String nome, String foneCelular, String email, String campanhaID, String midiaID, String pegaDataAtual,String sexo,String funcionarioID,String observacao, OnReturnServicePrimary mListener) {
        this.mListener = mListener;
        this.TipoMetodo = TipoMetodo;
        this.NomeMetodo = NomeMetodo;
        this.sLoginUsuario = sLoginUsuario;
        this.sSenhaUsuario = sSenhaUsuario;
        this.nCodCliSponte = nCodCliSponte;
        this.alunoID = alunoID;
        this.nome = nome;
        this.FoneCelular = foneCelular;
        this.email = email;
        this.sexo = sexo;
        this.datainclusao = pegaDataAtual;
        this.funcionarioID = funcionarioID;
        this.midias = midiaID;
        this.nCodUsuario = nUsuarioID;
        this.campanhaID = campanhaID;
        this.observacao = observacao;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Boolean doInBackground(Void... params) {

            NAMESPACE = "http://www.sponteweb.net.br/" ;
            URL = "http://192.168.1.167/WSInteressados/WSInteressados.asmx";

        String[] arrMetodos = new String[0];
        Integer i = 0;

        if (TipoMetodo.equals("GET")) {
            if (NomeMetodo.equals("EfetuaLogin")){
                arrMetodos = new String[1];
                arrMetodos[0] = "EfetuaLogin";
            } else if (NomeMetodo.equals("RetCampanhas")){
                arrMetodos = new String[1];
                arrMetodos[0] = "RetCampanhas";
            } else if (NomeMetodo.equals("RetMidias")){
                arrMetodos = new String[1];
                arrMetodos[0] = "RetMidias";
            } else if (NomeMetodo.equals("RetAlunos")){
                arrMetodos = new String[1];
                arrMetodos[0] = "RetAlunos";
            }

            while (i < arrMetodos.length) {
                final String METHOD_NAME = arrMetodos[i];
                final String SOAP_ACTION = NAMESPACE + METHOD_NAME;
                try {
                    SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
                    if (arrMetodos[i].equals("EfetuaLogin")) {
                        request.addProperty("sSenha", sSenha);
                        request.addProperty("sLoginUsuario", sLoginUsuario);
                        request.addProperty("sSenhaUsuario", sSenhaUsuario);
                    } else if (arrMetodos[i].equals("RetCampanhas")) {
                        request.addProperty("sSenha", sSenha);
                        request.addProperty("nCodCliSponte", nCodCliSponte);
                    } else if (arrMetodos[i].equals("RetMidias")) {
                        request.addProperty("sSenha", sSenha);
                        request.addProperty("nCodCliSponte", nCodCliSponte);
                    } else if (arrMetodos[i].equals("RetAlunos")) {
                        request.addProperty("sSenha", sSenha);
                        request.addProperty("nCodCliSponte", nCodCliSponte);
                        request.addProperty("nUsuarioID", nCodUsuario);
                    }

                    SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                    envelope.dotNet = true;
                    envelope.setOutputSoapObject(request);

                    HttpTransportSE httpTransport = new HttpTransportSE(URL);
                    httpTransport.call(SOAP_ACTION, envelope);
                    SoapPrimitive response = (SoapPrimitive) envelope.getResponse();

                    mResponse = response.toString();
                    valorteste = valorteste + mResponse;

                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("ERRO", e.toString());
                }
                i++;
            }
        } else if (TipoMetodo.equals("SET")) {

            String METHOD_NAME = "InsertAlunos";
            String SOAP_ACTION = NAMESPACE + METHOD_NAME;

            try {
                SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

                request.addProperty("sSenha", sSenha);
                request.addProperty("nCodCliSponte", nCodCliSponte);
                request.addProperty("id", alunoID);
                request.addProperty("nome", nome);
                request.addProperty("FoneCelular", FoneCelular);
                request.addProperty("email", email);
                request.addProperty("CampanhaID", campanhaID);
                request.addProperty("midias", midias);
                request.addProperty("nCodUsuario", nCodUsuario);
                request.addProperty("datainclusao", datainclusao);
                request.addProperty("sexo", sexo);
                request.addProperty("funcionarioID", funcionarioID);
                request.addProperty("Observacao", observacao);

                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                envelope.dotNet = true;
                envelope.setOutputSoapObject(request);

                HttpTransportSE httpTransport = new HttpTransportSE(URL);
                httpTransport.call(SOAP_ACTION, envelope);
                SoapPrimitive response = (SoapPrimitive) envelope.getResponse();

                mResponse = response.toString();
                valorteste = valorteste + mResponse;


            } catch (Exception e) {
                e.printStackTrace();
                Log.e("ERRO", e.toString());
                return false;
            }
        }
        return true;
    }

    protected void onPostExecute(Boolean sucess) {
        if (mListener != null) {
            if (sucess) mListener.onCompletion(valorteste);
            else mListener.onError();
        }
    }

    public interface OnReturnServicePrimary {
        public void onCompletion(String valorteste);
        public void onError();
    }

}

