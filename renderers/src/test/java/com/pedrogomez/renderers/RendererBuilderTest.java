package com.pedrogomez.renderers;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pedrogomez.renderers.exception.NeedsPrototypesException;
import com.pedrogomez.renderers.exception.NullContentException;
import com.pedrogomez.renderers.exception.NullLayoutInflaterException;
import com.pedrogomez.renderers.exception.NullParentException;
import com.pedrogomez.renderers.exception.PrototypeNotFoundException;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;

import static org.junit.Assert.assertEquals;

/**
 * Test class created to check the correct behaviour of RendererBuilder
 *
 * @author Pedro Vicente Gómez Sánchez.
 */
@SuppressWarnings({"unchecked", "ResultOfObjectAllocationIgnored", "ConstantConditions"})
public class RendererBuilderTest {

    @Mock
    private View mockedConvertView;
    @Mock
    private ViewGroup mockedParent;
    @Mock
    private LayoutInflater mockedLayoutInflater;
    @Mock
    private Object mockedContent;
    @Mock
    private View mockedRendererView;

    @Before
    public void setUp() {
        initializeMocks();
        initializePrototypes();
    }

    @Test(expected = NeedsPrototypesException.class)
    public void shouldThrowNeedsPrototypeExceptionIfPrototypesIsNull() {
        new ObjectRendererBuilder(null);
    }

    @Test(expected = NeedsPrototypesException.class)
    public void shouldNotAcceptNullPrototypes() {
        RendererBuilder rendererBuilder = new RendererBuilder();

        rendererBuilder.withPrototypes(null);
    }

    @Test
    public void shouldAcceptNotNullPrototypes() {
        RendererBuilder rendererBuilder = new RendererBuilder();

        ObjectRenderer renderer = new ObjectRenderer();
        rendererBuilder.withPrototypes(Collections.singletonList(renderer));
        assertEquals(1, rendererBuilder.getPrototypes().size());
        assertEquals(renderer, rendererBuilder.getPrototypes().get(0));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotAcceptNullKeysBindingAPrototype() {
        RendererBuilder rendererBuilder = new RendererBuilder();

        rendererBuilder.bind(null, new ObjectRenderer());
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotAcceptNullClass() {
        RendererBuilder rendererBuilder = new RendererBuilder();

        rendererBuilder.bind(ObjectRenderer.class, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotAcceptNullTypePototype() {
        RendererBuilder rendererBuilder = new RendererBuilder();

        rendererBuilder.bind(1, null);
    }

    @Test
    public void shouldAddPrototypeAndConfigureRendererBinding() {
        RendererBuilder rendererBuilder = new RendererBuilder();

        rendererBuilder.bind(Object.class, new ObjectRenderer());

        assertEquals(ObjectRenderer.class, rendererBuilder.getPrototypeClass(new Object()));
    }

    @Test
    public void shouldAddPrototypeAndConfigureRendererBindingWithBuilder() {
        ObjectRenderer renderer = new ObjectRenderer();
        renderer.setView(mockedRendererView);

        RendererBuilder rendererBuilder = new RendererBuilder()
              .bind(0, renderer)
              .withParent(mockedParent)
              .withLayoutInflater(mockedLayoutInflater)
              .withViewType(0);

        rendererBuilder.buildRendererViewHolder();

        assertEquals(ObjectRenderer.class, rendererBuilder.getPrototypeClass(new Object()));
    }

    @Test(expected = NullContentException.class)
    public void shouldThrowNullContentException() {
        ObjectRenderer renderer = new ObjectRenderer();
        renderer.setView(mockedRendererView);

        RendererBuilder rendererBuilder = new RendererBuilder()
              .bind(0, renderer)
              .withParent(mockedParent)
              .withLayoutInflater(mockedLayoutInflater)
              .withViewType(null);

        rendererBuilder.buildRendererViewHolder();

        assertEquals(ObjectRenderer.class, rendererBuilder.getPrototypeClass(new Object()));
    }

    @Test(expected = NullLayoutInflaterException.class)
    public void shouldThrowNullLayoutInflaterException() {
        ObjectRenderer renderer = new ObjectRenderer();
        renderer.setView(mockedRendererView);

        RendererBuilder rendererBuilder = new RendererBuilder()
              .bind(0, renderer)
              .withParent(mockedParent)
              .withLayoutInflater(null)
              .withViewType(0);

        rendererBuilder.buildRendererViewHolder();

        assertEquals(ObjectRenderer.class, rendererBuilder.getPrototypeClass(new Object()));
    }

    @Test(expected = NullParentException.class)
    public void shouldThrowNullParentException() {
        ObjectRenderer renderer = new ObjectRenderer();
        renderer.setView(mockedRendererView);

        RendererBuilder rendererBuilder = new RendererBuilder()
              .bind(0, renderer)
              .withParent(null)
              .withLayoutInflater(mockedLayoutInflater)
              .withViewType(0);

        rendererBuilder.buildRendererViewHolder();

        assertEquals(ObjectRenderer.class, rendererBuilder.getPrototypeClass(new Object()));
    }

    @Test
    public void shouldAddPrototypeAndConfigureRendererBindingForType() {
        int type = 1;
        RendererBuilder rendererBuilder = new RendererBuilder();

        rendererBuilder.bind(type, new ObjectRenderer());

        assertEquals(ObjectRenderer.class, rendererBuilder.getPrototypeClass(
              new RendererContent<>(new Object(), type)));
    }

    @Test(expected = PrototypeNotFoundException.class)
    public void shouldFailForWrongType() {
        int type = 1;
        int anotherType = 2;
        RendererBuilder rendererBuilder = new RendererBuilder();

        rendererBuilder.bind(type, new ObjectRenderer());
        rendererBuilder.bind(anotherType, new ObjectRenderer());

        rendererBuilder.getPrototypeClass(new RendererContent<>(new Object(), -1));
    }

    @Test
    public void shouldAddPrototypeAndConfigureRendererBindingForTypeWithMultiplePrototypes() {
        RendererBuilder rendererBuilder = new RendererBuilder();

        rendererBuilder.bind(Object.class, new ObjectRenderer());
        rendererBuilder.bind(String.class, new ObjectRenderer());

        assertEquals(ObjectRenderer.class, rendererBuilder.getPrototypeClass(new Object()));
    }

    @Test
    public void shouldAddPrototyeAndConfigureBindingOnConstruction() {
        ObjectRenderer renderer = new ObjectRenderer();
        RendererBuilder rendererBuilder = new RendererBuilder(renderer);

        assertEquals(1, rendererBuilder.getPrototypes().size());
        assertEquals(renderer, rendererBuilder.getPrototypes().get(0));
        assertEquals(ObjectRenderer.class, rendererBuilder.getPrototypeClass(new Object()));
    }

    private void initializeMocks() {
        MockitoAnnotations.initMocks(this);
    }

    private void initializePrototypes() {
        ObjectRenderer objectRenderer = new ObjectRenderer();
        objectRenderer.setView(mockedRendererView);
        SubObjectRenderer subObjectRenderer = new SubObjectRenderer();
        subObjectRenderer.setView(mockedRendererView);
    }
}
