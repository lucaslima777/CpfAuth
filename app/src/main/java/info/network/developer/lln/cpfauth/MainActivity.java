package info.network.developer.lln.cpfauth;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

        /*Copyright 2018 Lucas Lima

        Licensed under the Apache License, Version 2.0 (the "License");
        you may not use this file except in compliance with the License.
        You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

        Unless required by applicable law or agreed to in writing, software
        distributed under the License is distributed on an "AS IS" BASIS,
        WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
        See the License for the specific language governing permissions and
        limitations under the License.*/

public class MainActivity extends AppCompatActivity {

    private EditText editText;
    private Button button;
    private Spinner spinner;
    private String cpf;
    private TextView tipo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText = findViewById(R.id.edittext);
        editText.addTextChangedListener(Mask.insert(Mask.CPF_MASK, editText));

        button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getJson();
                cpf = editText.getText().toString().replace(".", "").replace(" ", "").replace("-", "");
            }
        });

        spinner = findViewById(R.id.option);

        tipo = findViewById(R.id.tipo);


        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String text = spinner.getSelectedItem().toString();
                editText.setText(text);
                cpf = editText.getText().toString().replace(".", "").replace(" ", "").replace("-", "");

                switch (i) {
                    case 0:
                    case 1:
                    case 2:
                    case 3:
                        tipo.setText(R.string.regular);
                        break;
                    case 4:
                    case 5:
                        tipo.setText(R.string.suspensa);
                        break;
                    case 6:
                    case 7:
                        tipo.setText(R.string.pendente_regul);
                        break;
                    case 8:
                    case 9:
                        tipo.setText(R.string.cancelada_mult);
                        break;
                    case 10:
                    case 11:
                        tipo.setText(R.string.nula);
                        break;
                    case 12:
                    case 13:
                        tipo.setText(R.string.cancelada_ofic);
                        break;
                    case 14:
                    case 15:
                        tipo.setText(R.string.falecido);
                        break;
                    default:
                        tipo.setText(R.string.invalido);
                        break;

                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


    }


    public void getJson() {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .addHeader("Authorization", "Bearer 4e1a1858bdd584fdc077fb7d80f39283")
                .url("https://apigateway.serpro.gov.br/consulta-cpf-trial/v1/cpf/" + cpf)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                Log.d("ERRO", "Failure " + e);
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                if (response.isSuccessful()) {
                    final String responseCPF = response.body().string();

                    MainActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            try {

                                JSONObject json = new JSONObject(responseCPF);
                                JSONObject json_situacao = json.getJSONObject("situacao");
                                String str_codigo = json_situacao.getString("codigo");
                                int codigo = Integer.parseInt(str_codigo);

                                switch (codigo) {
                                    case 0:
                                        Toast.makeText(MainActivity.this, "Regular", Toast.LENGTH_LONG).show();
                                        break;
                                    case 2:
                                        Toast.makeText(MainActivity.this, "Suspensa", Toast.LENGTH_LONG).show();
                                        break;
                                    case 3:
                                        Toast.makeText(MainActivity.this, "Titular Falecido", Toast.LENGTH_LONG).show();
                                        break;
                                    case 4:
                                        Toast.makeText(MainActivity.this, "Pendente de Regularização", Toast.LENGTH_LONG).show();
                                        break;
                                    case 5:
                                        Toast.makeText(MainActivity.this, "Cancelada por Multiplicidade", Toast.LENGTH_LONG).show();
                                        break;
                                    case 8:
                                        Toast.makeText(MainActivity.this, "Nula", Toast.LENGTH_LONG).show();
                                        break;
                                    case 9:
                                        Toast.makeText(MainActivity.this, "Cancelada de Ofício", Toast.LENGTH_LONG).show();
                                        break;
                                    default:
                                        break;
                                }


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            Log.d("Sucess", "" + responseCPF);


                        }
                    });
                } else {
                    Log.d("ERRO", "Failure");
                }
            }
        });

    }


}
