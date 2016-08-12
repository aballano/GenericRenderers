/*
 * Copyright (C) 2014 Pedro Vicente Gómez Sánchez.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.pedrogomez.renderers;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.pedrogomez.renderers.exception.NullRendererBuiltException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * RecyclerView.Adapter extension created to work RendererBuilders and Renderer instances. Other
 * adapters have to use this one to show information into RecyclerView widgets.
 *
 * This class is the heart of this library. It's used to avoid the library users declare a new
 * renderer each time they have to show information into a RecyclerView.
 *
 * RendererAdapter has to be constructed with a LayoutInflater to inflate views, one
 * RendererBuilder to provide Renderer to RendererAdapter and one Collection to
 * provide the elements to render.
 *
 * @author Pedro Vicente Gómez Sánchez.
 */
@SuppressWarnings({"unchecked", "SuspiciousMethodCalls"})
public class RendererAdapter<T> extends RecyclerView.Adapter<RendererViewHolder> {

    private final RendererBuilder<T> rendererBuilder;
    private final List<T> collection;

    public RendererAdapter(RendererBuilder rendererBuilder) {
        this(rendererBuilder, new ArrayList());
    }

    public RendererAdapter(RendererBuilder rendererBuilder, List collection) {
        this.rendererBuilder = rendererBuilder;
        this.collection = collection;
    }

    @Override
    public int getItemCount() {
        return collection.size();
    }

    public T getItem(int position) {
        return collection.get(position);
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
     * @param viewType associated to the renderer.
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
     * @param position to render.
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
        updateRendererExtraValues(content, renderer, position);
        renderer.render(payloads);
    }

    /**
     * @see List#add(Object)
     */
    public boolean add(Object element) {
        return collection.add((T) element);
    }

    /**
     * @see List#add(Object)
     * @see RecyclerView.Adapter#notifyItemInserted(int)
     */
    public boolean addAndNotify(Object element) {
        boolean result = add(element);
        notifyItemInserted(collection.size());
        return result;
    }

    /**
     * Convenient add method that also supports negative index to specify
     * that the addition should be done at the end of the list.
     *
     * @see List#add(int, Object)
     */
    public void add(int index, Object element) {
        if (index < 0) {
            add(element);
        } else {
            collection.add(index, (T) element);
        }
    }

    /**
     * Convenient add method that also supports negative index to specify
     * that the addition should be done at the end of the list.
     *
     * @see List#add(int, Object)
     * @see RecyclerView.Adapter#notifyItemInserted(int)
     */
    public void addAndNotify(int index, Object element) {
        add(index, element);
        if (index < 0) {
            index = collection.size();
        }
        notifyItemInserted(index);
    }

    /**
     * @see List#set(int, Object)
     */
    public T update(int index, Object element) {
        return collection.set(index, (T) element);
    }

    /**
     * @see List#set(int, Object)
     * @see RecyclerView.Adapter#notifyItemChanged(int)
     */
    public T updateAndNotify(int index, Object element) {
        T set = update(index, element);
        notifyItemChanged(index);
        return set;
    }

    /**
     * @see List#remove(Object)
     */
    public boolean remove(Object element) {
        return collection.remove(element);
    }

    /**
     * @see List#remove(int)
     * @see RecyclerView.Adapter#notifyItemRemoved(int)
     */
    public Object removeAndNotify(Object element) {
        int indexOf = collection.indexOf(element);
        return removeAtAndNotify(indexOf);
    }

    /**
     * @see List#remove(int)
     */
    public T removeAt(int location) {
        return collection.remove(location);
    }

    /**
     * @see List#remove(int)
     * @see RecyclerView.Adapter#notifyItemRemoved(int)
     */
    public Object removeAtAndNotify(int indexOf) {
        Object remove = removeAt(indexOf);
        notifyItemRemoved(indexOf);
        return remove;
    }

    /**
     * @see List#addAll(Collection)
     */
    public boolean addAll(Collection elements) {
        return collection.addAll(elements);
    }

    /**
     * @see List#addAll(int, Collection)
     */
    public boolean addAll(int index, Collection elements) {
        return collection.addAll(index, elements);
    }

    /**
     * @see List#addAll(Collection)
     * @see RecyclerView.Adapter#notifyItemRangeInserted(int, int)
     */
    public boolean addAllAndNotify(Collection elements) {
        int size = collection.size();
        boolean result = addAll(elements);
        notifyItemRangeInserted(size, elements.size());
        return result;
    }

    /**
     * @see List#addAll(int, Collection)
     * @see RecyclerView.Adapter#notifyItemRangeInserted(int, int)
     */
    public boolean addAllAndNotify(int index, Collection elements) {
        boolean result = addAll(index, elements);
        notifyItemRangeInserted(index, elements.size());
        return result;
    }

    /**
     * @see List#removeAll(Collection)
     */
    public boolean removeAll(Collection<?> elements) {
        return collection.removeAll(elements);
    }

    /**
     * @see List#removeAll(Collection)
     * @see Adapter#notifyDataSetChanged()
     */
    public boolean removeAllAndNotify(Collection<?> elements) {
        boolean result = removeAll(elements);
        notifyDataSetChanged();
        return result;
    }

    /**
     * @see List#clear()
     */
    public void clear() {
        collection.clear();
    }

    /**
     * @see List#clear()
     * @see Adapter#notifyDataSetChanged()
     */
    public void clearAndNotify() {
        clear();
        notifyDataSetChanged();
    }

    /**
     * @see List#indexOf(Object)
     */
    public int indexOf(Object object) {
        return collection.indexOf(object);
    }

    /**
     * @see List#contains(Object)
     */
    public boolean contains(Object object) {
        return collection.contains(object);
    }

    /**
     * @see List#containsAll(Collection)
     */
    public boolean containsAll(Collection<Object> object) {
        return collection.containsAll(object);
    }

    /**
     * Allows the client code to access the List from subtypes of RendererAdapter.
     *
     * @return list used in the adapter.
     */
    public List<T> getCollection() {
        return collection;
    }

    /**
     * Empty implementation created to allow the client code to extend this class without override
     * getView method.
     *
     * This method is called before render the Renderer and can be used in RendererAdapter extension
     * to add extra info to the renderer created like the position in the ListView/RecyclerView.
     *
     * @param content to be rendered.
     * @param renderer to be used to paint the content.
     * @param position of the content.
     */
    @SuppressWarnings("UnusedParameters")
    protected void updateRendererExtraValues(T content, Renderer renderer, int position) { }
}
