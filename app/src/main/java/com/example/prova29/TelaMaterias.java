package com.example.prova29;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
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

    }
}