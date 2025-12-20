 package com.aparicioamaral.quinzenumerosaleatorios;// MANTENHA SEU PACOTE

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Ocultar a barra superior (ActionBar) para ficar tela cheia
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Configura o temporizador para 3000 milissegundos (3 segundos)
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                // 1. Cria a intenção de ir para a tela principal
                Intent irParaPrincipal = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(irParaPrincipal);

                // 2. Fecha a tela de boas-vindas para o usuário não poder voltar nela
                finish();
            }
        }, 3000); // 3000 = 3 segundos
    }
}