package com.pedrogomez.renderers;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.pedrovgs.renderers.BuildConfig;
import com.pedrogomez.renderers.ViewRenderer.InflateFunction;
import com.pedrogomez.renderers.ViewRenderer.RenderFunction;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

@SuppressWarnings("unchecked")
@Config(sdk = 19, constants = BuildConfig.class)
@RunWith(RobolectricGradleTestRunner.class)
public class ViewRendererTest {

    @Mock private ViewGroup mockedParent;
    @Mock private LayoutInflater mockedInflater;
    @Mock private InflateFunction inflateFunction;
    @Mock private RenderFunction renderFunction;
    private ViewRenderer renderer;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        when(mockedParent.getContext()).thenReturn(RuntimeEnvironment.application);

        renderer = spy(new ViewRenderer<>(inflateFunction, renderFunction));
    }

    @Test
    public void shouldCallInflateFunctionWithContext() throws Exception {
        renderer.inflate(mockedInflater, mockedParent);

        verify(inflateFunction).inflate(RuntimeEnvironment.application);
        verifyZeroInteractions(mockedInflater);
    }

    @Test
    public void shouldCallRenderFunctionWithContentAndRootView() throws Exception {
        Object content = new Object();
        renderer.setContent(content);
        View rootView = mock(View.class);
        when(renderer.getRootView()).thenReturn(rootView);

        List<Object> payloads = mock(List.class);
        renderer.render(payloads);

        verify(renderFunction).render(content, rootView);
        verifyZeroInteractions(payloads);
    }

}