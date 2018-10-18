package com.pedrogomez.renderers;

import android.support.v7.recyclerview.extensions.AsyncListDiffer;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.pedrogomez.renderers.exception.NullRendererBuiltException;

import java.util.Collections;
import java.util.List;

public class AsyncRendererAdapter<T> extends Adapter<RendererViewHolder> {

    private final RendererBuilder<T> rendererBuilder;
    private final AsyncListDiffer<T> asyncListDiffer;

    public AsyncRendererAdapter(RendererBuilder rendererBuilder, DiffUtil.ItemCallback<T> listDiffer) {
        this.rendererBuilder = rendererBuilder;
        asyncListDiffer = new AsyncListDiffer<>(this, listDiffer);
    }

    public AsyncRendererAdapter<T> into(RecyclerView recyclerView) {
        recyclerView.setAdapter(this);
        return this;
    }

    @Override
    public int getItemCount() {
        return asyncListDiffer.getCurrentList().size();
    }

    public T getItem(int position) {
        return asyncListDiffer.getCurrentList().get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * Indicate to the RecyclerView the type of Renderer used to one position using a numeric value.
     *
     * @param position to analyze.
     * @return the id associated to the Renderer used to render the content given a position.
     */
    @Override
    public int getItemViewType(int position) {
        T content = getItem(position);
        return rendererBuilder.getItemViewType(content);
    }

    /**
     * One of the two main methods in this class. Creates a RendererViewHolder instance with a
     * Renderer inside ready to be used. The RendererBuilder to create a RendererViewHolder using the
     * information given as parameter.
     *
     * @param viewGroup used to create the ViewHolder.
     * @param viewType  associated to the renderer.
     * @return ViewHolder extension with the Renderer it has to use inside.
     */
    @Override
    public RendererViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        rendererBuilder.withParent(viewGroup);
        rendererBuilder.withLayoutInflater(LayoutInflater.from(viewGroup.getContext()));
        rendererBuilder.withViewType(viewType);
        RendererViewHolder viewHolder = rendererBuilder.buildRendererViewHolder();
        if (viewHolder == null) {
            throw new NullRendererBuiltException("RendererBuilder have to return a not null viewHolder");
        }
        return viewHolder;
    }

    /**
     * Given a RendererViewHolder passed as argument and a position renders the view using the
     * Renderer previously stored into the RendererViewHolder.
     *
     * @param viewHolder with a Renderer class inside.
     * @param position   to render.
     */
    @Override
    public void onBindViewHolder(RendererViewHolder viewHolder, int position) {
        onBindViewHolder(viewHolder, position, Collections.emptyList());
    }

    @Override
    public void onBindViewHolder(RendererViewHolder viewHolder, int position, List<Object> payloads) {
        T content = getItem(position);
        Renderer renderer = viewHolder.getRenderer();
        if (renderer == null) {
            throw new NullRendererBuiltException("RendererBuilder have to return a not null renderer");
        }
        renderer.setContent(content);
        renderer.setPosition(position);
        updateRendererExtraValues(content, renderer, position);
        renderer.render(payloads);
    }

    @Override public void onViewAttachedToWindow(RendererViewHolder viewHolder) {
        super.onViewAttachedToWindow(viewHolder);
        Renderer renderer = viewHolder.getRenderer();
        renderer.onAttached();
    }

    @Override public void onViewDetachedFromWindow(RendererViewHolder viewHolder) {
        Renderer renderer = viewHolder.getRenderer();
        renderer.onDetached();
        super.onViewDetachedFromWindow(viewHolder);
    }

    @Override public void onViewRecycled(RendererViewHolder viewHolder) {
        Renderer renderer = viewHolder.getRenderer();
        renderer.onRecycled();
        super.onViewRecycled(viewHolder);
    }

    public void submitList(List<T> newList) {
        asyncListDiffer.submitList(newList);
    }

    /**
     * Empty implementation created to allow the client code to extend this class without override
     * getView method.
     * <p>
     * This method is called before render the Renderer and can be used in RendererAdapter extension
     * to add extra info to the renderer created like the position in the ListView/RecyclerView.
     *
     * @param content  to be rendered.
     * @param renderer to be used to paint the content.
     * @param position of the content.
     */
    @SuppressWarnings("UnusedParameters")
    protected void updateRendererExtraValues(T content, Renderer renderer, int position) {
    }
}