package com.example.prova29;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.prova29.databinding.ActivityTelaMateriasBinding;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;

public class TelaMaterias extends AppCompatActivity {

    private ActivityTelaMateriasBinding view;
    private List<Disciplinas> list = new ArrayList<>();
    private boolean very = false;

    private interface Lista {

        @GET("/disciplinas-usuario/{id}")
        Call<List<Disciplinas>> listar(@Path("id") int id);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        view = ActivityTelaMateriasBinding.inflate(getLayoutInflater());
        setContentView(view.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        view.recyclerView.setLayoutManager(new LinearLayoutManager(TelaMaterias.this));
        view.recyclerView.setHasFixedSize(true);

        Animation animation = new AlphaAnimation(1, 0);
        animation.setDuration(2000);

        view.viewHarmonico.setAnimation(animation);
        view.viewHarmonico.setVisibility(View.GONE);



        Retrofit retrofit = new Retrofit.Builder().baseUrl("http://10.0.2.2:3000").addConverterFactory(GsonConverterFactory.create()).build();

        Lista lista = retrofit.create(Lista.class);

        SharedPreferences cache = getSharedPreferences("login", MODE_PRIVATE);

        very = cache.getBoolean("mostrarPop", false);

        if (!very) {

            AlertDialog.Builder pop = new AlertDialog.Builder(TelaMaterias.this);
            pop.setTitle("Permissão de biometria");
            pop.setMessage("Aceita usar a biometria como validação de login?");
            pop.setNegativeButton("Não", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    SharedPreferences.Editor editor = getSharedPreferences("login", MODE_PRIVATE).edit();
                    editor.putBoolean("mostrarPop", true);
                    editor.apply();

                }
            });
            pop.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    SharedPreferences.Editor editor = getSharedPreferences("login", MODE_PRIVATE).edit();
                    editor.putBoolean("mostrarPop", true);
                    editor.putBoolean("permissao", true);
                    editor.apply();

                }
            });
            pop.create().show();

        }

        int id = cache.getInt("id", 0);

        lista.listar(id).enqueue(new Callback<List<Disciplinas>>() {
            @Override
            public void onResponse(Call<List<Disciplinas>> call, Response<List<Disciplinas>> response) {

                if (response.isSuccessful()) {

                    list = response.body();
                    AdapterList adapterList = new AdapterList(list, TelaMaterias.this);
                    view.recyclerView.setAdapter(adapterList);

                } else if (response.code() == 404){

                    Toast.makeText(TelaMaterias.this, "Curso não encontrado", Toast.LENGTH_SHORT).show();

                } else {

                    Log.e("ERROR", "Error: " + response.code());

                }

            }

            @Override
            public void onFailure(Call<List<Disciplinas>> call, Throwable throwable) {

                Log.e("ERROR", "Error: " + throwable.getMessage());

            }
        });

        view.imgMenu.setOnClickListener(e -> {

            view.imgMenu.setEnabled(false);
            view.viewFundo.setEnabled(true);

            Animation animation1 = new AlphaAnimation(0, 1);
            animation1.setDuration(500);

            view.viewFundo.setAnimation(animation1);
            view.viewFundo.setVisibility(View.VISIBLE);

            Animation animation2 = AnimationUtils.loadAnimation(TelaMaterias.this, R.anim.anim_in);
            animation2.setDuration(500);

            view.viewMenu.setAnimation(animation2);
            view.viewMenu.setVisibility(View.VISIBLE);

        });

        view.viewFundo.setOnClickListener(e -> {

            view.imgMenu.setEnabled(true);
            view.viewFundo.setEnabled(false);

            Animation animation1 = new AlphaAnimation(1, 0);
            animation1.setDuration(500);

            view.viewFundo.setAnimation(animation1);
            view.viewFundo.setVisibility(View.GONE);

            Animation animation2 = AnimationUtils.loadAnimation(TelaMaterias.this, R.anim.anim_out);
            animation2.setDuration(500);

            view.viewMenu.setAnimation(animation2);
            view.viewMenu.setVisibility(View.GONE);

        });

        view.viewMenu.setOnClickListener(e -> {

        });

        view.btnLogout.setOnClickListener(e -> {

            SharedPreferences.Editor editor = getSharedPreferences("login", MODE_PRIVATE).edit();

            editor.putBoolean("logado", false);
            editor.putInt("id", 0);
            editor.apply();

            startActivity(new Intent(TelaMaterias.this, MainActivity.class));
            finish();

        });

    }
}