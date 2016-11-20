package com.pedrogomez.renderers;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Renderer created only for testing purposes.
 *
 * @author alberto.ballano
 */
public class ObjectRendererContentRenderer extends Renderer<RendererContent<Object>> {

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
