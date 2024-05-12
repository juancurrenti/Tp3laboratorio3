package com.ulp.tp3laboratorio3;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputType;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.ulp.tp3laboratorio3.databinding.ActivityRegistroBinding;


public class RegistroActivity extends AppCompatActivity {
    private RegistroViewModel rm;
    private ActivityRegistroBinding binding;
    private Intent intent;
    private ActivityResultLauncher<Intent> arl;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegistroBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        rm = new ViewModelProvider(this).get(RegistroViewModel.class);
        abrirGaleria();
        rm.getUriMutable().observe(this, new Observer<Uri>() {
            @Override
            public void onChanged(Uri uri) {
                binding.ivFoto.setImageURI(uri);
            }
        });

        if (getIntent().hasExtra("usuarioDatos")) {
            String usuarioDatos = getIntent().getStringExtra("usuarioDatos");
            cargarDatosUsuario(usuarioDatos);
        }

        binding.buttonRegistrar.setOnClickListener(v -> {
            guardarUsuario();
        });

        binding.cbPassword.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                binding.etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            } else {
                binding.etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            }
            binding.etPassword.setSelection(binding.etPassword.getText().length());
        });

        binding.btnCargar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                arl.launch(intent);
            }
        });
    }

    private void cargarDatosUsuario(String datos) {
        String[] partes = datos.split(" ");
        if (partes.length >= 5) {
            binding.etEmail.setText(partes[0]);
            binding.etPassword.setText(partes[1]);
            binding.etNombre.setText(partes[2]);
            binding.etApellido.setText(partes[3]);
            binding.etDni.setText(partes[4]);
            binding.buttonRegistrar.setText("Actualizar Datos");
        }
    }

    private void guardarUsuario() {
        String email = binding.etEmail.getText().toString();
        String password = binding.etPassword.getText().toString();
        String nombre = binding.etNombre.getText().toString();
        String apellido = binding.etApellido.getText().toString();
        long dni = Long.parseLong(binding.etDni.getText().toString());
        Uri fotoUri = rm.getUriMutable().getValue();

        boolean resultado = rm.guardarActualizarUsuario(email, password, nombre, apellido, dni, fotoUri);

        if (resultado) {
            Toast.makeText(getApplicationContext(), "Datos del usuario actualizados correctamente!", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getApplicationContext(), "Nuevo usuario registrado correctamente!", Toast.LENGTH_LONG).show();
        }
        finish();
    }


    private void abrirGaleria(){
        intent=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        arl=registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                rm.recibirFoto(result);
            }

        });

    }

}
