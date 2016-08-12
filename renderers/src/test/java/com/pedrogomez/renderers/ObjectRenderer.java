package com.pedrogomez.renderers;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Renderer created only for testing purposes.
 *
 * @author Pedro Vicente Gómez Sánchez.
 */
public class ObjectRenderer extends Renderer<Object> {

    private View view;

    @Override
    protected View inflate(LayoutInflater inflater, ViewGroup parent) {
        return view;
    }

    @Override
    public void render(List<Object> payloads) { }

    public void setView(View view) {
        this.view = view;
    }
}
