package com.ulp.tp3laboratorio3;

import static android.app.Activity.RESULT_OK;

import android.app.Application;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class RegistroViewModel extends AndroidViewModel {
    private MutableLiveData<Boolean> usuarioGuardado;
    private MutableLiveData<Uri> uriMutableLiveData;

    public RegistroViewModel(Application application) {
        super(application);
        usuarioGuardado = new MutableLiveData<>();
    }

    public MutableLiveData<Boolean> getUsuarioGuardado() {
        return usuarioGuardado;
    }
    public LiveData<Uri> getUriMutable(){

        if(uriMutableLiveData==null){
            uriMutableLiveData=new MutableLiveData<>();
        }
        return uriMutableLiveData;
    }

    public boolean guardarActualizarUsuario(String mail, String password, String nombre, String apellido, long dni, Uri fotoUri) {
        File archivo = new File(getApplication().getFilesDir(), "users.dat");
        ArrayList<Usuario> usuarios = new ArrayList<>();
        boolean usuarioExistente = false;

        if (archivo.exists()) {
            try {
                FileInputStream fis = new FileInputStream(archivo);
                BufferedInputStream bis = new BufferedInputStream(fis);
                ObjectInputStream ois = new ObjectInputStream(bis);

                while (true) {
                    try {
                        Usuario usuario = (Usuario) ois.readObject();
                        if (usuario.getMail().equals(mail)) {
                            usuario.setNombre(nombre);
                            usuario.setApellido(apellido);
                            usuario.setPassword(password);
                            usuario.setDni(dni);
                            usuario.setFotoUri(fotoUri);
                            usuarioExistente = true;
                        }
                        usuarios.add(usuario);
                    } catch (EOFException eof) {
                        break;
                    }
                }
                ois.close();
            } catch (IOException | ClassNotFoundException e) {
                Toast.makeText(getApplication(), "Error al leer datos", Toast.LENGTH_LONG).show();
            }
        }

        if (!usuarioExistente) {
            usuarios.add(new Usuario(mail, password, nombre, apellido, dni, fotoUri));
        }

        try {
            FileOutputStream fos = new FileOutputStream(archivo, false);
            BufferedOutputStream bos = new BufferedOutputStream(fos);
            ObjectOutputStream oos = new ObjectOutputStream(bos);

            for (Usuario usuario : usuarios) {
                oos.writeObject(usuario);
            }

            bos.flush();
            oos.close();
            usuarioGuardado.postValue(true);
            return usuarioExistente;
        } catch (IOException e) {
            usuarioGuardado.postValue(false);
            Toast.makeText(getApplication(), "Error al guardar datos", Toast.LENGTH_LONG).show();
            return false;
        }
    }

    public void recibirFoto(ActivityResult result) {
        if(result.getResultCode() == RESULT_OK){
            Intent data=result.getData();
            Uri uri=data.getData();
            uriMutableLiveData.setValue(uri);


        }
    }

}
