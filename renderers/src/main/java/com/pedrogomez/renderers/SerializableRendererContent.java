package com.pedrogomez.renderers;

import java.io.Serializable;

/**
 * Wrapper to use with {@link RendererBuilder} for type or class bindings that implements the serializable interface.
 *
 * @author angelo.marchesin
 */
@SuppressWarnings("unused")
public class SerializableRendererContent<T extends Serializable> extends RendererContent<T> implements Serializable {

    /**
     * @param item Content of the wrapper.
     * @param type The type within the list.
     */
    public SerializableRendererContent(T item, int type) {
        super(item, type);
    }
}
