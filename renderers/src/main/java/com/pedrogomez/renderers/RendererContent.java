package com.pedrogomez.renderers;

/**
 * Wrapper to use with {@link RendererBuilder} for type or class bindings.
 *
 * @author alberto.ballano
 */
@SuppressWarnings("unused")
public class RendererContent<T> {

    private T item;
    private int type = -1;

    /**
     * @param item Content of the wrapper.
     * @param type The type within the list.
     */
    public RendererContent(T item, int type) {
        this.item = item;
        this.type = type;
    }

    public T getItem() {
        return item;
    }

    public void setItem(T item) {
        this.item = item;
    }

    public int getType() {
        return type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RendererContent)) return false;

        RendererContent<?> content = (RendererContent<?>) o;

        return type == content.type && (item != null ? item.equals(content.item) : content.item == null);
    }

    @Override
    public int hashCode() {
        int result = item != null ? item.hashCode() : 0;
        result = 31 * result + type;
        return result;
    }
}
