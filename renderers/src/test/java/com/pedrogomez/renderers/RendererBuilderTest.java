package com.pedrogomez.renderers;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.pedrovgs.renderers.BuildConfig;
import com.pedrogomez.renderers.exception.NeedsPrototypesException;
import com.pedrogomez.renderers.exception.NullContentException;
import com.pedrogomez.renderers.exception.NullLayoutInflaterException;
import com.pedrogomez.renderers.exception.NullParentException;
import com.pedrogomez.renderers.exception.PrototypeNotFoundException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@SuppressWarnings({"unchecked", "ResultOfObjectAllocationIgnored", "ConstantConditions"})
@Config(sdk = 19, constants = BuildConfig.class)
@RunWith(RobolectricGradleTestRunner.class)
public class RendererBuilderTest {

    @Mock private View mockedConvertView;
    @Mock private ViewGroup mockedParent;
    @Mock private LayoutInflater mockedLayoutInflater;
    @Mock private Object mockedContent;
    @Mock private View mockedRendererView;

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
        RendererBuilder rendererBuilder = RendererBuilder.create()
              .getRendererBuilder();

        rendererBuilder.withPrototypes(null);
    }

    @Test
    public void shouldAcceptNotNullPrototypes() {
        RendererBuilder rendererBuilder = RendererBuilder.create()
              .getRendererBuilder();

        ObjectRenderer renderer = new ObjectRenderer();
        rendererBuilder.withPrototypes(Collections.singletonList(renderer));
        assertEquals(1, rendererBuilder.getPrototypes().size());
        assertEquals(renderer, rendererBuilder.getPrototypes().get(0));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotAcceptNullKeysBindingAPrototype() {
        RendererBuilder.create().bind(null, new ObjectRenderer());
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotAcceptNullClass() {
        RendererBuilder.create().bind(ObjectRenderer.class, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotAcceptNullTypePrototype() {
        RendererBuilder.create().bind(1, null);
    }

    @Test
    public void shouldAddPrototypeAndConfigureRendererBinding() {
        RendererBuilder rendererBuilder = RendererBuilder.create()
              .bind(String.class, new ObjectRenderer())
              .getRendererBuilder();

        assertEquals(ObjectRenderer.class, rendererBuilder.getPrototypeClass(""));
    }

    @Test
    public void shouldAddPrototypeAndConfigureRendererBindingWithBuilder() {
        ObjectRenderer renderer = new ObjectRenderer();
        renderer.setView(mockedRendererView);

        RendererBuilder rendererBuilder = RendererBuilder.create()
              .bind(String.class, renderer)
              .getRendererBuilder()
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

        RendererBuilder rendererBuilder = RendererBuilder.create()
              .bind(String.class, renderer)
              .getRendererBuilder()
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

        RendererBuilder rendererBuilder = RendererBuilder.create()
              .bind(String.class, renderer)
              .getRendererBuilder()
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

        RendererBuilder rendererBuilder = RendererBuilder.create()
              .bind(String.class, renderer)
              .getRendererBuilder()
              .withParent(null)
              .withLayoutInflater(mockedLayoutInflater)
              .withViewType(0);

        rendererBuilder.buildRendererViewHolder();

        assertEquals(ObjectRenderer.class, rendererBuilder.getPrototypeClass(new Object()));
    }

    @Test
    public void shouldAddPrototypeAndConfigureRendererBindingForType() {
        int type = 1;
        RendererBuilder rendererBuilder = RendererBuilder.create()
              .bind(type, new ObjectRendererContentRenderer())
              .getRendererBuilder();

        assertEquals(ObjectRendererContentRenderer.class, rendererBuilder.getPrototypeClass(
              new RendererContent<>(new Object(), type)));
    }

    @Test(expected = PrototypeNotFoundException.class)
    public void shouldFailForWrongType() {
        int type = 1;
        int anotherType = 2;
        RendererBuilder rendererBuilder = RendererBuilder.create()
              .bind(type, new ObjectRendererContentRenderer())
              .bind(anotherType, new ObjectRendererContentRenderer())
              .getRendererBuilder();

        rendererBuilder.getPrototypeClass(new RendererContent<>(new Object(), -1));
    }

    @Test
    public void shouldAddPrototypeAndConfigureRendererBindingForTypeWithMultiplePrototypes() {
        RendererBuilder rendererBuilder = RendererBuilder.create()
              .bind(Integer.class, new ObjectRenderer())
              .bind(String.class, new ObjectRenderer())
              .getRendererBuilder();

        assertEquals(ObjectRenderer.class, rendererBuilder.getPrototypeClass(1));
    }

    @Test
    public void shouldAddPrototypeAndConfigureBindingOnConstruction() {
        ObjectRenderer renderer = new ObjectRenderer();
        RendererBuilder rendererBuilder = RendererBuilder.create(renderer)
              .getRendererBuilder();

        assertEquals(1, rendererBuilder.getPrototypes().size());
        assertEquals(renderer, rendererBuilder.getPrototypes().get(0));
        assertEquals(ObjectRenderer.class, rendererBuilder.getPrototypeClass(new Object()));
    }

    @Test
    public void shouldCheckBindingsThatInheritParentClass() {
        RendererBuilder rendererBuilder = RendererBuilder.create()
              .bind(ParentClass.class, new ObjectRenderer())
              .getRendererBuilder();

        assertEquals(ObjectRenderer.class, rendererBuilder.getPrototypeClass(new ChildClass()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowIllegalArgumentExceptionForObjectBinding() {
        RendererBuilder rendererBuilder = RendererBuilder.create()
              .bind(Object.class, new ObjectRenderer())
              .getRendererBuilder();

        assertEquals(ObjectRenderer.class, rendererBuilder.getPrototypeClass(new Object()));
    }

    @Test
    public void shouldCreateEmptyAdapter() throws Exception {
        RendererAdapter adapter = RendererBuilder.create()
              .bind(String.class, new ObjectRenderer())
              .build();

        assertTrue(adapter.getCollection().isEmpty());
    }

    @Test
    public void shouldCreateAdapterWithItems() throws Exception {
        List<String> list = Arrays.asList("1", "2", "3");
        RendererAdapter adapter = RendererBuilder.create()
              .bind(String.class, new ObjectRenderer())
              .buildWith(list);

        assertEquals(list, adapter.getCollection());
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

    private static class ParentClass {
        ParentClass() {
        }
    }

    private static class ChildClass extends ParentClass {
        ChildClass() {
        }
    }
}
