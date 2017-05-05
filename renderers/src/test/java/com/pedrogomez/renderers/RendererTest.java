package com.pedrogomez.renderers;

import android.content.Context;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pedrogomez.renderers.exception.NotInflateViewException;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class RendererTest {

    @Spy
    private ObjectRenderer renderer;

    @Mock
    private Object mockedContent;
    @Mock
    private LayoutInflater mockedLayoutInflater;
    @Mock
    private ViewGroup mockedParent;
    @Mock
    private View mockedView;

    private int testPosition = 1;

    @Before
    public void setUp() {
        initializeRenderer();
        initializeMocks();
    }

    @Test
    public void shouldKeepTheContentAfterOnCreateCall() {
        givenARendererInflatingView(mockedView);

        onCreateRenderer();

        assertEquals(mockedContent, renderer.getContent());
    }

    @Test
    public void shouldReturnCorrectItemPosition() {
        givenARendererInflatingView(mockedView);

        onCreateRenderer();

        assertEquals(renderer.getPosition(), testPosition);
    }

    @Test
    public void shouldInflateViewUsingLayoutInflaterAndParentAfterOnCreateCall() {
        givenARendererInflatingView(mockedView);

        onCreateRenderer();

        verify(renderer).inflate(mockedLayoutInflater, mockedParent);
    }

    @Test(expected = NotInflateViewException.class)
    public void shouldThrowExceptionIfInflateReturnsAnEmptyViewAfterOnCreateCall() {
        givenARendererInflatingANullView();

        onCreateRenderer();
    }

    @Test
    public void shouldSetUpViewWithTheInflatedViewAfterOnCreateCall() {
        givenARendererInflatingView(mockedView);

        onCreateRenderer();

        verify(renderer).setUpView(mockedView);
    }

    @Test
    public void shouldHookListenersViewWithTheInflatedViewAfterOnCreateCall() {
        givenARendererInflatingView(mockedView);

        onCreateRenderer();

        verify(renderer).hookListeners(mockedView);
    }

    @Test
    public void shouldKeepTheContentAfterOnRecycleCall() {
        givenARendererInflatingView(mockedView);

        onRecycleRenderer();

        assertEquals(mockedContent, renderer.getContent());
    }

    @Test
    public void shouldReturnNonNullContextAfterCreation() throws Exception {
        givenARendererInflatingView(mockedView);
        when(mockedParent.getContext()).thenReturn(mock(Context.class));
        assertNull(renderer.getContext());

        renderer.onCreate(mockedContent, mockedLayoutInflater, mockedParent);

        assertNotNull(renderer.getContext());
    }

    private void initializeRenderer() {
        renderer = new ObjectRenderer();
    }

    private void initializeMocks() {
        MockitoAnnotations.initMocks(this);
    }

    private void onCreateRenderer() {
        renderer.onCreate(mockedContent, mockedLayoutInflater, mockedParent);
        renderer.setPosition(testPosition);
    }

    private void onRecycleRenderer() {
        renderer.onRecycle(mockedContent);
    }

    private void givenARendererInflatingANullView() {
        givenARendererInflatingView(null);
    }

    private void givenARendererInflatingView(@Nullable View view) {
        when(renderer.inflate(mockedLayoutInflater, mockedParent)).thenReturn(view);
    }
}
