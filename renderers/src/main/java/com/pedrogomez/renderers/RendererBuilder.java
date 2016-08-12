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

import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.pedrogomez.renderers.exception.NeedsPrototypesException;
import com.pedrogomez.renderers.exception.NullContentException;
import com.pedrogomez.renderers.exception.NullLayoutInflaterException;
import com.pedrogomez.renderers.exception.NullParentException;
import com.pedrogomez.renderers.exception.NullPrototypeClassException;
import com.pedrogomez.renderers.exception.PrototypeNotFoundException;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Class created to work as builder for Renderer objects. This class provides methods to create a
 * Renderer instances using a fluent API.
 * <p/>
 * The library users have to extends RendererBuilder and create a new one with prototypes. The
 * RendererBuilder implementation will have to declare the mapping between objects from the
 * List and Renderer instances passed to the prototypes collection.
 * <p/>
 * This class is not going to implement the view recycling if is used with the RecyclerView widget
 * because RecyclerView class already implements the view recycling for us.
 *
 * @author Pedro Vicente Gómez Sánchez
 */
@SuppressWarnings({"DuplicateStringLiteralInspection", "unchecked", "SuspiciousMethodCalls"})
public class RendererBuilder<T> {

    protected List<Renderer> prototypes;

    private ViewGroup parent;
    private LayoutInflater layoutInflater;
    private Integer viewType;
    protected final Map<Class<T>, Class<? extends Renderer>> binding;
    protected final HashMap<Integer, Class<? extends Renderer>> typeBindings = new HashMap<>(0);

    /**
     * Initializes a RendererBuilder with an empty prototypes collection. Using this constructor some
     * binding configuration is needed.
     */
    public RendererBuilder() {
        this(new LinkedList<Renderer>());
    }

    /**
     * Initializes a RendererBuilder with just one prototype. Using this constructor the prototype
     * used will be always the same and the additional binding configuration wont be needed.
     */
    public RendererBuilder(Renderer renderer) {
        this(Collections.singletonList(renderer));
    }

    /**
     * Initializes a RendererBuilder with a list of prototypes. Using this constructor some
     * binding configuration is needed.
     */
    public RendererBuilder(List<Renderer> prototypes) {
        setPrototypes(prototypes);
        binding = new HashMap<>(1);
    }

    /**
     * Get access to the prototypes collection used to create one RendererBuilder.
     *
     * @return prototypes list.
     */
    public final List<Renderer> getPrototypes() {
        return prototypes;
    }

    /**
     * Configure prototypes used as Renderer instances.
     *
     * @param prototypes to use by the builder in order to create Renderer instances.
     */
    public final void setPrototypes(List<Renderer> prototypes) {
        if (prototypes == null) {
            throw new NeedsPrototypesException("RendererBuilder has to be created with a non null collection of"
                    + "Collection<Renderer to provide new or recycled Renderer instances");
        }
        this.prototypes = prototypes;
    }

    /**
     * Configure prototypes used as Renderer instances.
     *
     * @param prototypes to use by the builder in order to create Renderer instances.
     * @return the current RendererBuilder instance.
     */
    public RendererBuilder<T> withPrototypes(List<Renderer> prototypes) {
        if (prototypes == null) {
            throw new NeedsPrototypesException("RendererBuilder has to be created with a non null collection of"
                    + "Collection<Renderer to provide new or recycled Renderer instances");
        }
        this.prototypes.addAll(prototypes);
        return this;
    }

    /**
     * Given a class configures the binding between a class and a Renderer class.
     *
     * @param clx       to bind.
     * @param prototype used as Renderer.
     * @return the current RendererBuilder instance.
     */
    public RendererBuilder<T> bind(Class clx, Renderer prototype) {
        if (clx == null || prototype == null) {
            throw new IllegalArgumentException("The binding RecyclerView binding can't be configured using null instances");
        }
        prototypes.add(prototype);
        binding.put(clx, prototype.getClass());
        return this;
    }

    /**
     * Binds a custom type to a given {@link Renderer}.
     *
     * @param type      Integer type.
     * @param prototype used as Renderer.
     */
    public RendererBuilder<T> bind(int type, Renderer prototype) {
        if (prototype == null) {
            throw new IllegalArgumentException("The binding RecyclerView binding can't be configured using null instances");
        }
        typeBindings.put(type, prototype.getClass());
        prototypes.add(prototype);
        return this;
    }

    RendererBuilder withParent(ViewGroup parent) {
        this.parent = parent;
        return this;
    }

    RendererBuilder withLayoutInflater(LayoutInflater layoutInflater) {
        this.layoutInflater = layoutInflater;
        return this;
    }

    RendererBuilder withViewType(Integer viewType) {
        this.viewType = viewType;
        return this;
    }

    /**
     * Main method of this class related to RecyclerView widget. This method is the responsible of
     * create a new Renderer instance with all the needed information to implement the rendering.
     * This method will validate all the attributes passed in the builder constructor and will create
     * a RendererViewHolder instance.
     * <p/>
     * This method is used with RecyclerView because the view recycling mechanism is implemented out
     * of this class and we only have to return new RendererViewHolder instances.
     *
     * @return ready to use RendererViewHolder instance.
     */
    protected RendererViewHolder buildRendererViewHolder() {
        validateAttributesToCreateANewRendererViewHolder();

        Renderer renderer = getPrototypeByIndex(viewType).copy();
        renderer.onCreate(null, layoutInflater, parent);
        return new RendererViewHolder(renderer);
    }

    /**
     * Gets one prototype using the prototype index which is equals to the view type. This method
     * has to be implemented because prototypes member is declared with Collection and that interface
     * doesn't allow the client code to get one element by index.
     *
     * @param prototypeIndex used to search.
     * @return prototype renderer.
     */
    private Renderer getPrototypeByIndex(int prototypeIndex) {
        return prototypes.get(prototypeIndex);
    }

    private static void validatePrototypeClass(Class prototypeClass) {
        if (prototypeClass == null) {
            throw new NullPrototypeClassException("Your getPrototypeClass method implementation can't return a null class");
        }
    }

    /**
     * Return the item view type used by the adapter to implement recycle mechanism.
     *
     * @param content to be rendered.
     * @return an integer that represents the renderer inside the adapter.
     */
    int getItemViewType(T content) {
        Class prototypeClass = getPrototypeClass(content);
        validatePrototypeClass(prototypeClass);
        return getItemViewType(prototypeClass);
    }

    /**
     * Return the Renderer class associated to the prototype.
     *
     * @param prototypeClass used to search the renderer in the prototypes collection.
     * @return the prototype index associated to the prototypeClass.
     */
    private int getItemViewType(Class prototypeClass) {
        int itemViewType = -1;
        for (int i = 0, prototypesSize = prototypes.size(); i < prototypesSize; i++) {
            Renderer renderer = prototypes.get(i);
            if (renderer.getClass().equals(prototypeClass)) {
                itemViewType = i;
                break;
            }
        }
        if (itemViewType == -1) {
            throw new PrototypeNotFoundException("Review your RendererBuilder implementation, you are returning one"
                    + " prototype class not found in prototypes collection");
        }
        return itemViewType;
    }

    /**
     * Throws one RendererException if the viewType, layoutInflater or parent are null.
     */
    private void validateAttributesToCreateANewRendererViewHolder() {
        if (viewType == null) {
            throw new NullContentException("RendererBuilder needs a view type to create a RendererViewHolder");
        }
        if (layoutInflater == null) {
            throw new NullLayoutInflaterException("RendererBuilder needs a LayoutInflater to create a RendererViewHolder");
        }
        if (parent == null) {
            throw new NullParentException("RendererBuilder needs a parent to create a RendererViewHolder");
        }
    }

    /**
     * Method to be implemented by the RendererBuilder subtypes. In this method the library user will
     * define the mapping between content and renderer class.
     *
     * @param content used to map object to Renderers.
     * @return the class associated to the renderer.
     */
    protected Class getPrototypeClass(T content) {
        if (!typeBindings.isEmpty() && content instanceof RendererContent) {
            RendererContent rendererContent = (RendererContent) content;
            Class<? extends Renderer> renderer = typeBindings.get(rendererContent.getType());
            if (renderer != null) {
                return renderer;
            }
        }

        if (prototypes.size() == 1) {
            return prototypes.get(0).getClass();
        }

        Class<?> aClass = content.getClass();
        if (binding.containsKey(aClass)) {
            return binding.get(aClass);
        }

        throw new PrototypeNotFoundException("No prototype was found for the class " + aClass.getSimpleName());
    }
}
