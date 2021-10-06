package com.example.audiolibrosv1.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.camera2.params.Capability;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.audiolibrosv1.AdaptadorLibros;
import com.example.audiolibrosv1.Aplicacion;
import com.example.audiolibrosv1.Libro;
import com.example.audiolibrosv1.MainActivity;
import com.example.audiolibrosv1.R;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import java.util.Vector;

public class SelectorFragment extends Fragment {
    private Activity actividad;
    private RecyclerView recyclerView;
    private AdaptadorLibros adaptador;
    private Vector<Libro> vectorLibros;



    @Override public void onAttach(Context contexto) {
        super.onAttach(contexto);
        if (contexto instanceof Activity) {
            this.actividad = (Activity) contexto;
            Aplicacion app = (Aplicacion) actividad.getApplication();
            adaptador = app.getAdaptador();
            vectorLibros = app.getVectorLibros();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_selector, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_ultimo) {
            ((MainActivity) actividad).irUltimoVisitado();
            return true;
        } else if (id == R.id.menu_buscar) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override public View onCreateView(LayoutInflater inflador, ViewGroup
            contenedor, Bundle savedInstanceState) {

        View vista = inflador.inflate(R.layout.fragment_selector,
                contenedor, false);
        recyclerView = (RecyclerView) vista.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(actividad,2));
        recyclerView.setAdapter(adaptador);

        setHasOptionsMenu(true);

        adaptador.setOnItemLongClickListener(new View.OnLongClickListener() {
            public boolean onLongClick(final View v) {
                final int id = recyclerView.getChildAdapterPosition(v);
                AlertDialog.Builder menu = new AlertDialog.Builder(actividad);
                CharSequence[] opciones = { "Compartir", "Borrar ", "Insertar" };
                menu.setItems(opciones, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int opcion) {
                        switch (opcion) {
                            case 0: //Compartir
                                Libro libro = vectorLibros.elementAt(id);
                                Intent i = new Intent(Intent.ACTION_SEND);
                                i.setType("text/plain");
                                i.putExtra(Intent.EXTRA_SUBJECT, libro.titulo);
                                i.putExtra(Intent.EXTRA_TEXT, libro.urlAudio);
                                startActivity(Intent.createChooser(i, "Compartir"));
                                break;
                            case 1: //Borrar

                                Snackbar.make(v,"Estas Seguro",Snackbar.LENGTH_LONG)
                                        .setAction("SI", new View.OnClickListener(){
                                            @Override
                                            public void onClick(View view){
                                                vectorLibros.remove(id);
                                                adaptador.notifyDataSetChanged();
                                            }
                                        })
                                        .show();
                                break;
                            case 2: //Insertar
                                vectorLibros.add(vectorLibros.elementAt(id));
                                adaptador.notifyDataSetChanged();
                                Snackbar.make(v,"Libro insertado", Snackbar.LENGTH_INDEFINITE)
                                        .setAction("OK", new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {

                                            }
                                        })
                                        .show();
                                break;
                        }
                    }
                });
                menu.create().show();
                return true;
            }
        });

        adaptador.setOnItemClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Toast.makeText(actividad, "Seleccionado el elemento: "
                                + recyclerView.getChildAdapterPosition(v),
                        Toast.LENGTH_SHORT).show();*/
                ((MainActivity) actividad).mostrarDetalle(
                        recyclerView.getChildAdapterPosition(v));
            }
        });

        TabLayout tabs = actividad.findViewById(R.id.tabs);
        if(tabs!=null) {
            tabs.addTab(tabs.newTab().setText("Todos"));
            tabs.addTab(tabs.newTab().setText("Nuevos"));
            tabs.addTab(tabs.newTab().setText("Leidos"));
            tabs.setTabMode(TabLayout.MODE_SCROLLABLE);
        }

        return vista;
    }
}
