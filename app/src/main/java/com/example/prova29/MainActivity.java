package com.example.prova29;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.prova29.databinding.ActivityMainBinding;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding view;

    private interface Login {

        @GET("/login")
        Call<Usuario> logar(@Query("email") String email, @Query("senha") String senha);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        view = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(view.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Retrofit retrofit = new Retrofit.Builder().baseUrl("http://10.0.2.2:3000").addConverterFactory(GsonConverterFactory.create()).build();

        Login login = retrofit.create(Login.class);

        view.btnEntrar.setOnClickListener(e -> {

            String email = view.edtEmail.getText().toString().trim();
            String senha = view.edtSenha.getText().toString().trim();

            login.logar(email, senha).enqueue(new Callback<Usuario>() {
                @Override
                public void onResponse(Call<Usuario> call, Response<Usuario> response) {

                    if (response.isSuccessful()) {

                        Usuario usuario = response.body();

                        SharedPreferences.Editor editor = getSharedPreferences("login", MODE_PRIVATE).edit();
                        editor.putBoolean("logado", true);
                        editor.putBoolean("logadoCampos", true);
                        editor.putInt("id", usuario.getId());
                        editor.apply();

                        startActivity(new Intent(MainActivity.this, TelaMaterias.class));
                        finish();

                    } else {

                        Toast.makeText(MainActivity.this, "E-mail/Senha incorretos!", Toast.LENGTH_SHORT).show();

                    }

                }

                @Override
                public void onFailure(Call<Usuario> call, Throwable throwable) {

                    Log.e("ERROR", "Error: " + throwable.getMessage());

                }
            });

        });

    }
}