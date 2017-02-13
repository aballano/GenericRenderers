package com.pedrogomez.renderers;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class RendererContentTest {
    @Test
    public void shouldReturnItemItem() throws Exception {
        String item = "string";
        RendererContent<String> rendererContent = new RendererContent<>(item, 1);

        assertEquals(item, rendererContent.getItem());
    }

    @Test
    public void shouldSetItem() throws Exception {
        String item = "string";
        RendererContent<String> rendererContent = new RendererContent<>(item, 1);

        String item2 = "string2";
        rendererContent.setItem(item2);
        assertEquals(item2, rendererContent.getItem());
    }

    @Test
    public void shouldReturnType() throws Exception {
        int type = 1;
        RendererContent<String> rendererContent = new RendererContent<>("string", type);

        assertEquals(type, rendererContent.getType());
    }

    @Test
    public void shouldDoEqualsWithItemAndType() throws Exception {
        RendererContent<String> rendererContent1 = new RendererContent<>("a", 1);
        RendererContent<String> rendererContent2 = new RendererContent<>("b", 1);
        RendererContent<String> rendererContent3 = new RendererContent<>("a", 2);
        RendererContent<String> rendererContent4 = new RendererContent<>("a", 1);

        assertNotEquals(rendererContent1, rendererContent2);
        assertNotEquals(rendererContent1, rendererContent3);
        assertNotEquals(rendererContent2, rendererContent3);

        assertEquals(rendererContent1, rendererContent4);
    }

    @Test
    public void shouldDoHashcodeWithItemAndType() throws Exception {
        RendererContent<String> rendererContent1 = new RendererContent<>("a", 1);
        RendererContent<String> rendererContent2 = new RendererContent<>("b", 1);
        RendererContent<String> rendererContent3 = new RendererContent<>("a", 2);
        RendererContent<String> rendererContent4 = new RendererContent<>("a", 1);

        assertNotEquals(rendererContent1.hashCode(), rendererContent2.hashCode());
        assertNotEquals(rendererContent1.hashCode(), rendererContent3.hashCode());
        assertNotEquals(rendererContent2.hashCode(), rendererContent3.hashCode());

        assertEquals(rendererContent1.hashCode(), rendererContent4.hashCode());
    }

}